package org.egge.pricer;

import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * A buffered writer.  Formats and outputs records to a stream.
 * @author brianegge
 *
 */
public class TotalWriter {

	private final PrintStream out;

	public TotalWriter(OutputStream out) {
		this.out = new PrintStream(new BufferedOutputStream(out));
	}

	public void write(long timestamp, Side side, Double price) {
		out.print(timestamp);
		out.print(" ");
		out.print(side == Side.BID ? Side.ASK.abbr : Side.BID.abbr);
		out.print(" ");

		if (price == null) {
			out.print("NA\n");
		} else {
			// the BigDecimal class excels at formatting to a String.  
			// A double has a hard time converting to base 10 and then a string.
			out.print(FORMAT.format(price));
			out.print("\n");
		}					
	}

	public void close()  {
		out.flush();
	}

	private static final NumberFormat FORMAT = new DecimalFormat("#0.00");
	
}
