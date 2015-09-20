package org.egge.common;

/**
 * Generic data holder class for three items.  Normally part of a package of tuple classes.
 * @author brianegge
 *
 * @param <T>
 * @param <U>
 * @param <V>
 */
public class Triple<T,U,V> {

	private final T t;
	private final U u;
	private final V v;
	
	public Triple(T t, U u, V v) {
		this.t = t;
		this.u = u;
		this.v = v;
	}
	
	public static <T,U,V> Triple<T,U,V> of(T t, U u, V v) {
		return new Triple<T,U,V>(t,u,v);
	}
	
	public T first() {
		return t;
	}
	public U second() {
		return u;
	}
	public V third() {
		return v;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((t == null) ? 0 : t.hashCode());
		result = prime * result + ((u == null) ? 0 : u.hashCode());
		result = prime * result + ((v == null) ? 0 : v.hashCode());
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Triple<T,U,V>  other = (Triple<T,U,V>) obj;
		if (t == null) {
			if (other.t != null)
				return false;
		} else if (!t.equals(other.t))
			return false;
		if (u == null) {
			if (other.u != null)
				return false;
		} else if (!u.equals(other.u))
			return false;
		if (v == null) {
			if (other.v != null)
				return false;
		} else if (!v.equals(other.v))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Triple [t=" + t + ", u=" + u + ", v=" + v + "]";
	}
}
