package org.egge.pricer;

public enum Side {
	BID("B"), ASK("S");

	public final String abbr;

	private Side(String abbr) {
		this.abbr = abbr;

	}
	public static Side parse(String abbr) { 
		for(Side s : Side.values()) { 
			if (s.abbr.equals(abbr)) {
				return s;
			}
		}
		throw new IllegalArgumentException("Unknown side " + abbr);
	}
}
