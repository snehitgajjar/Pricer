package org.egge.pricer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Test;

public class OrderTest {

	@Test
	public void testBids() {
		Order a = new Order(0, OrderType.ADD, "a") {{
			setPrice(Double.valueOf("10.00"));
			setSide(Side.BID);
		}};
		Order b = new Order(0, OrderType.ADD, "b") {{
			setPrice(Double.valueOf("11.00"));
			setSide(Side.BID);
		}};
		Order c = new Order(0, OrderType.ADD, "c") {{
			setPrice(Double.valueOf("9.00"));
			setSide(Side.BID);
		}};
		List<Order> l = Arrays.asList(a, b, c);
		Collections.shuffle(l);
		Collections.sort(l);
		Assert.assertEquals(Arrays.asList(b,a,c), l);
		Assert.assertEquals(Arrays.asList(b,a,c), Arrays.asList(new TreeSet<Order>(l).toArray()));
	}
	@Test
	public void testAsks() {
		Order a = new Order(0, OrderType.ADD, "a");
		a.setPrice(Double.valueOf("10.00"));
		a.setSide(Side.ASK);
		Order b = new Order(0, OrderType.ADD, "a");
		b.setPrice(Double.valueOf("11.00"));
		b.setSide(Side.ASK);
		Order c = new Order(0, OrderType.ADD, "a");
		c.setPrice(Double.valueOf("9.00"));
		c.setSide(Side.ASK);
		List<Order> l = Arrays.asList(a, b, c);
		Collections.shuffle(l);
		Collections.sort(l);
		Assert.assertEquals(Arrays.asList(c,a,b), l);
	}
}
