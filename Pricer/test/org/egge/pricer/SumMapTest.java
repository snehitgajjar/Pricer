package org.egge.pricer;

import java.math.BigDecimal;

import junit.framework.Assert;

import org.junit.Test;


public class SumMapTest {

	@Test
	public void testSell() throws Exception {
		SumMap map = new SumMap();
		add(map, Parser.parse("1 A a S 1.0 1"));
		Assert.assertEquals(new BigDecimal("1.0"), map.totalExpense(1));
		add(map, Parser.parse("1 A b S 2.0 1"));
		Assert.assertEquals(new BigDecimal("1.0"), map.totalExpense(1));
		Assert.assertEquals(new BigDecimal("3.0"), map.totalExpense(2));
		add(map, Parser.parse("1 A c S 4.0 1"));
		Assert.assertEquals(new BigDecimal("1.0"), map.totalExpense(1));
		Assert.assertEquals(new BigDecimal("3.0"), map.totalExpense(2));
		Assert.assertEquals(new BigDecimal("7.0"), map.totalExpense(3));
		map.remove(Parser.parse("1 A b S 2.0 1"));
		Assert.assertEquals(new BigDecimal("1.0"), map.totalExpense(1));
		Assert.assertEquals(new BigDecimal("5.0"), map.totalExpense(2));
		map.remove(Parser.parse("1 A a S 1.0 1"));
		Assert.assertEquals(new BigDecimal("4.0"), map.totalExpense(1));
	}
	private void add(SumMap map, Order order) {
		map.put(order, order.getSize());
	}
	@Test
	public void testSell2() throws Exception {
		SumMap map = new SumMap();
		add(map, Parser.parse("1 A a S 4.0 1"));
		Assert.assertEquals(new BigDecimal("4.0"), map.totalExpense(1));
		add(map, Parser.parse("1 A b S 2.0 1"));
		Assert.assertEquals(new BigDecimal("2.0"), map.totalExpense(1));
		Assert.assertEquals(new BigDecimal("6.0"), map.totalExpense(2));
		add(map, Parser.parse("1 A c S 1.0 1"));
		Assert.assertEquals(new BigDecimal("1.0"), map.totalExpense(1));
		Assert.assertEquals(new BigDecimal("3.0"), map.totalExpense(2));
		Assert.assertEquals(new BigDecimal("7.0"), map.totalExpense(3));
		add(map, Parser.parse("1 A d S 8.0 1"));
		Assert.assertEquals(new BigDecimal("1.0"), map.totalExpense(1));
		Assert.assertEquals(new BigDecimal("3.0"), map.totalExpense(2));
		Assert.assertEquals(new BigDecimal("7.0"), map.totalExpense(3));
		Assert.assertEquals(new BigDecimal("15.0"), map.totalExpense(4));
	}
	@Test
	public void testRight() throws Exception {
		SumMap map = new SumMap();
		for(String s : "28845097 A y S 44.10 500, 28845606 A z S 44.13 100, 28817570 A o S 44.14 150, 28860037 A bb S 44.16 2000, 28861264 A cb S 44.17 100, 28841307 A x S 44.40 100".split(", ")) {
			add(map, Parser.parse(s));
		}
		map.totalExpense(1);
	}
}
