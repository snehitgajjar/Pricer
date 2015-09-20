package org.egge.pricer;

import java.util.HashMap;
import java.util.Map;

/**
 * contains one side of the order book.  This contains two data structures - a SumMap and an orderIdMap.  
 * @author brianegge
 *
 */
public class OrderBookDetail {

	private final Side side;
	private final int target;
	private final TotalWriter writer;

	public OrderBookDetail(Side side, int target, TotalWriter writer) {
		this.side = side;
		this.target = target;
		this.writer = writer;
	}

	/**
	 * The sumMap is a modified red-black binary tree which maintains a running sum of order sizes and total price.
	 */
	private final SumMap sumMap = new SumMap();
	/**
	 * reduce orders only contain the order id, so this map is used to find the original order to modify.
	 */
	private final Map<String, Order> orderIdMap = new HashMap<String, Order>();
	private int size = 0;
	private Double lastTotal;

	public void add(Order o) throws InterruptedException {
		size += o.getSize();
		sumMap.put(o, o.getSize());
		orderIdMap.put(o.getOrderId(), o);
		if (size < target) {
			return;
		}
		processTotal(o.getTimestamp());
	}

	/**
	 * computes the total dollar value at a given size.  Sends a message to the write queue if this amount has changed.
	 * @param timestamp
	 * @throws InterruptedException
	 */
	private void processTotal(long timestamp) throws InterruptedException {
		Double totalExpense = sumMap.totalExpense(target);
		if (totalExpense == null && lastTotal != null) {
			writer.write(timestamp, side, null);
		}
		if (totalExpense != null && (lastTotal == null || (Math.abs(totalExpense - lastTotal) >= 0.001))) {
			writer.write(timestamp, side, totalExpense);
		}
		lastTotal = totalExpense;
	}

	public boolean remove(Order o) throws InterruptedException {
		Order orig = orderIdMap.get(o.getOrderId());
		if (orig == null)
			return false;
		Integer removed = sumMap.remove(orig);
		assert removed != null : "Order " + o + " found in orderIdMap as "
				+ orig + ", but not in set\n" + sumMap;
		if (o.getSize() < orig.getSize()) {
			orig.setSize(orig.getSize() - o.getSize());
			sumMap.put(orig, orig.getSize());
			size -= o.getSize();
			if (size + o.getSize() >= target)
				processTotal(o.getTimestamp());
		} else {
			size -= o.getSize();
			orderIdMap.remove(o.getOrderId());
			if (size + orig.getSize() >= target)
				processTotal(o.getTimestamp());
		}
		return true;
	}
}
