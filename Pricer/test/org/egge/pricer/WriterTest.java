package org.egge.pricer;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.Random;

import org.junit.Test;


public class WriterTest {

	@Test
	public void testWriter() throws Exception {
		Random r = new Random();
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(new BufferedOutputStream(byteArrayOutputStream));
		for(int i = 0; i < 211539; i++) {
			out.print("abc");
			out.print(" ");
			out.print(r.nextBoolean() ? Side.ASK.abbr : Side.BID.abbr);
			out.print(" ");
			out.print(new BigDecimal(r.nextInt(10000)).movePointLeft(2).toPlainString());
			if (byteArrayOutputStream.size() > 8000)
				byteArrayOutputStream.reset();
		}
		out.flush();
	}
	public static void main(String[] args) {
		Random r = new Random();
		PrintStream out = new PrintStream(new BufferedOutputStream(System.out));
		for(int i = 0; i < 211539; i++) {
			out.print("abc");
			out.print(" ");
			out.print(r.nextBoolean() ? Side.ASK.abbr : Side.BID.abbr);
			out.print(" ");
			out.print(new BigDecimal(r.nextInt(10000)).movePointLeft(2).toPlainString());
			out.print("\n");
		}
		out.flush();
	}
	
}
