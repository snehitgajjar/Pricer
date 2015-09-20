package org.egge.pricer;

public enum OrderType {
  ADD, REDUCE;
  public String abbr() {
	  return toString().substring(0,1);
  }
}
