package org.egge.pricer;


/**
 * Stores the current state of the order book.  Contains all orders in two detail objects.  The class when executed pulls
 * data from the input queue and writes it to the output queue.  
 *  
 * @author brianegge
 *
 */
public class OrderBook implements Runnable {

	private final Parser in;
	private final OrderBookDetail bids;
	private final OrderBookDetail asks;
	private final TotalWriter out;

	public OrderBook(Parser parser, int target, TotalWriter out) {
		this.in = parser;
		this.out = out;
		bids = new OrderBookDetail(Side.BID, target, out);
		asks = new OrderBookDetail(Side.ASK, target, out);
	}

	public void run() {
		while (true) {
			Order order;
			try {
				order = in.take();
				if (order == null) {
					out.close();
					break;
				}
				if (order.getOrderType() == OrderType.ADD) {
					((order.getSide() == Side.BID) ? bids : asks).add(order);
				} else {
					// we don't know which side the order was on.  First try removing from bids then asks
					if (!bids.remove(order))
							asks.remove(order);
				}
					
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				break;
			}
		}

	}
	

}
