package org.egge.pricer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

import org.junit.Test;

public class MainTest {

	@Test
	public void testMain() throws IOException {
		InputStreamReader reader = new InputStreamReader(new FileInputStream("share/small.in"));
		Parser parser = new Parser(reader);
		TotalWriter out = new TotalWriter(System.out);
		new OrderBook(parser, 200, out).run();
		reader.close();
	}
	
	@Test
	public void testPricer1() throws IOException {
		InputStreamReader reader = new InputStreamReader(new GZIPInputStream(new FileInputStream("share/pricer.in")));
		Parser parser = new Parser(reader);
		TotalWriter out = new TotalWriter(System.out);
		new OrderBook(parser, 1, out).run();
		reader.close();
	}

}
