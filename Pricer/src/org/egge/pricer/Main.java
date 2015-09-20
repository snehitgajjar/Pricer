package org.egge.pricer;

import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Entry point in the program from the command line.  Expects a single argument of the target depth and for the input
 * data to arrive in stdin.
 * 
 * Note input and output parser/writers are each run in their own thread forming a concurrent pipeline.
 * 
 * @author brianegge
 *
 */
public class Main {
	
	public static void main(String[] args) throws IOException {
		if (args.length == 0 || "-h".equals(args[0]) || ("--help").equals(args[0])) {
			System.out.println("usage: Main target-size");
			return;
		}
		int target = Integer.valueOf(args[0]);
		InputStreamReader reader = new InputStreamReader(System.in);
		Parser parser = new Parser(reader);
		TotalWriter out = new TotalWriter(System.out);
		System.out.println("before.....");
		new OrderBook(parser, target, out ).run();
		System.out.println("after.....");
		reader.close();
	}

}
