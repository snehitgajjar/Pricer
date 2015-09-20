package org.egge.pricer;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.zip.GZIPInputStream;

import org.junit.Test;


public class ParserTest {

	@Test
	public void testParser() throws Exception {
		InputStreamReader reader = new InputStreamReader(new GZIPInputStream(new FileInputStream("share/pricer.in.gz")));
		LineNumberReader lr = new LineNumberReader(reader);
		String line;
		while((line = lr.readLine()) != null) {
			Order order = Parser.parse(line);
			assert order != null;
		}
	}
}
