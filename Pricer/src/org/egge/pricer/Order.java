package org.egge.pricer;

import java.util.Comparator;


/**
 * Represents both an order request and an order state.  When used to store order state, the action portion is ignored.
 * 
 * Each order is comparable, with bid orders sorting descending and ask orders sorting ascending.
 * 
 * @author brianegge
 *
 */
public class Order implements Comparable<Order> {

	public static final Order POISON = new Order(0, null, null);

	private final int timestamp;
	private final OrderType orderType;
	private final String orderId;
	private Side side;
	private double price;
	private int size;

	public Order(int timestamp, OrderType orderType, String orderId) {
		this.timestamp = timestamp;
		this.orderType = orderType;
		this.orderId = orderId;
	}

	public void setSide(Side side) {
		this.side = side;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int compareTo(Order o) {
		assert o.side == side;
		assert side != null;
		int i = Double.compare(price, o.price) * (side == Side.BID ? -1 : 1);
		if (i != 0)
			return i;
		// i = Integer.signum(timestamp - o.timestamp); a normal order book would be sorted by time after price
		return orderId.compareTo(o.orderId);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((orderId == null) ? 0 : orderId.hashCode());
		result = prime * result
				+ ((orderType == null) ? 0 : orderType.hashCode());
		result = prime * result + timestamp;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Order other = (Order) obj;
		if (orderId == null) {
			if (other.orderId != null)
				return false;
		} else if (!orderId.equals(other.orderId))
			return false;
		if (orderType == null) {
			if (other.orderType != null)
				return false;
		} else if (!orderType.equals(other.orderType))
			return false;
		if (timestamp != other.timestamp)
			return false;
		return true;
	}

	public int getSize() {
		return size;
	}

	public double getPrice() {
		return price;
	}

	public String getOrderId() {
		return orderId;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public OrderType getOrderType() {
		return orderType;
	}
	
	public Side getSide() {
		return side;
	}

	@Override
	public String toString() {
		String abbr = orderType == null ? "null" : orderType.abbr();
		String sideabbr = side == null ? "null" : side.abbr;
		return String.format("%d %s %s %s %.2f %d", timestamp, abbr, orderId, sideabbr, price, size); 
	}

	

}
