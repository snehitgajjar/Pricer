package org.egge.pricer;

import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses each line from the input and writes the output to the queue.  Errors are written to stderr and ignored.
 * @author brianegge
 *
 */
public class Parser  {

	private final LineNumberReader lineReader;

	private static final Pattern ADD = Pattern
			.compile("(\\d+) A (\\w+) ([BS]) (\\d+.\\d+) (\\d+)");
	private static final Pattern REDUCE = Pattern
			.compile("(\\d+) R (\\w+) (\\d+)");
	
	public Parser(InputStreamReader reader) {
		 this.lineReader = new LineNumberReader(reader);
	}

	public Order take() {
		while(true) {
			String line = "";
			try {
				line = lineReader.readLine();
				//System.out.println("reading line....."+line);
				if (line==null)
					return null;
				return parse(line);
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("Failed to parse line: "
						+ lineReader.getLineNumber() + "\n" + line);
			}
		}
		
	}
	
	public static Order parse(String line) {
		Matcher matcher = ADD.matcher(line);
		if (matcher.matches()) {
			Order order = new Order(Integer.parseInt(matcher.group(1)),
					OrderType.ADD, matcher.group(2));
			order.setSide(Side.parse(matcher.group(3)));
			order.setPrice(Double.valueOf(matcher.group(4)));
			order.setSize(Integer.parseInt(matcher.group(5)));
			return order;
		}
		matcher = REDUCE.matcher(line);
		if (matcher.matches()) {
			Order order = new Order(Integer.parseInt(matcher.group(1)),
					OrderType.REDUCE, matcher.group(2));
			order.setSize(Integer.parseInt(matcher.group(3)));
			return order;
		}
		throw new IllegalArgumentException("Line didn't match any expected format");
	}

}
