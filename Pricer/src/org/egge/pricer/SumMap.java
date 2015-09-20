package org.egge.pricer;

import java.io.IOException;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;

/**
 * This is a non-generic copy lang.util.TreeMap, modified to keep a sum at each node.  Each node's
 * sum contains the sum for it and it's children.  The root contains the sum of the entire tree.  This allows for
 * partial sum's to be calculated in O(log n) time.  Modified methods include: <tt>rotateLeft</tt>, <tt>rotateRight</tt>,
 * <tt>put</tt>, and <tt>remove</tt>.
 */
/*
 * @(#)SumMap.java	1.77 08/05/15
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/**
 * A Red-Black tree based {@link NavigableMap} implementation. The map is sorted
 * according to the {@linkplain Comparable natural ordering} of its keys, or by
 * a {@link Comparator} provided at map creation time, depending on which
 * constructor is used.
 * 
 * <p>
 * This implementation provides guaranteed log(n) time cost for the
 * <tt>containsKey</tt>, <tt>get</tt>, <tt>put</tt> and <tt>remove</tt>
 * operations. Algorithms are adaptations of those in Cormen, Leiserson, and
 * Rivest's <I>Introduction to Algorithms</I>.
 * 
 * <p>
 * Note that the ordering maintained by a sorted map (whether or not an explicit
 * comparator is provided) must be <i>consistent with equals</i> if this sorted
 * map is to correctly implement the <tt>Map</tt> interface. (See
 * <tt>Comparable</tt> or <tt>Comparator</tt> for a precise definition of
 * <i>consistent with equals</i>.) This is so because the <tt>Map</tt> interface
 * is defined in terms of the equals operation, but a map performs all key
 * comparisons using its <tt>compareTo</tt> (or <tt>compare</tt>) method, so two
 * keys that are deemed equal by this method are, from the standpoint of the
 * sorted map, equal. The behavior of a sorted map <i>is</i> well-defined even
 * if its ordering is inconsistent with equals; it just fails to obey the
 * general contract of the <tt>Map</tt> interface.
 * 
 * <p>
 * <strong>Note that this implementation is not synchronized.</strong> If
 * multiple threads access a map concurrently, and at least one of the threads
 * modifies the map structurally, it <i>must</i> be synchronized externally. (A
 * structural modification is any operation that adds or deletes one or more
 * mappings; merely changing the value associated with an existing key is not a
 * structural modification.) This is typically accomplished by synchronizing on
 * some object that naturally encapsulates the map. If no such object exists,
 * the map should be "wrapped" using the
 * {@link Collections#synchronizedSortedMap Collections.synchronizedSortedMap}
 * method. This is best done at creation time, to prevent accidental
 * unsynchronized access to the map:
 * 
 * <pre>
 *   SortedMap m = Collections.synchronizedSortedMap(new SumMap(...));
 * </pre>
 * 
 * <p>
 * The iterators returned by the <tt>iterator</tt> method of the collections
 * returned by all of this class's "collection view methods" are
 * <i>fail-fast</i>: if the map is structurally modified at any time after the
 * iterator is created, in any way except through the iterator's own
 * <tt>remove</tt> method, the iterator will throw a
 * {@link ConcurrentModificationException}. Thus, in the face of concurrent
 * modification, the iterator fails quickly and cleanly, rather than risking
 * arbitrary, non-deterministic behavior at an undetermined time in the future.
 * 
 * <p>
 * Note that the fail-fast behavior of an iterator cannot be guaranteed as it
 * is, generally speaking, impossible to make any hard guarantees in the
 * presence of unsynchronized concurrent modification. Fail-fast iterators throw
 * <tt>ConcurrentModificationException</tt> on a best-effort basis. Therefore,
 * it would be wrong to write a program that depended on this exception for its
 * correctness: <i>the fail-fast behavior of iterators should be used only to
 * detect bugs.</i>
 * 
 * <p>
 * All <tt>Map.Entry</tt> pairs returned by methods in this class and its views
 * represent snapshots of mappings at the time they were produced. They do
 * <em>not</em> support the <tt>Entry.setValue</tt> method. (Note however that
 * it is possible to change mappings in the associated map using <tt>put</tt>.)
 * 
 * <p>
 * This class is a member of the <a href="{@docRoot}
 * /../technotes/guides/collections/index.html"> Java Collections Framework</a>.
 * 
 * @param <Order>
 *            the type of keys maintained by this map
 * @param <Long>
 *            the type of mapped values
 * 
 * @author Josh Bloch and Doug Lea
 * @version 1.73, 05/10/06
 * @see Map
 * @see HashMap
 * @see Hashtable
 * @see Comparable
 * @see Comparator
 * @see Collection
 * @since 1.2
 */
@SuppressWarnings("unchecked")
public class SumMap extends AbstractMap<Order, Integer> implements Cloneable, java.io.Serializable {
	/**
	 * The comparator used to maintain order in this tree map, or null if it
	 * uses the natural ordering of its keys.
	 * 
	 * @serial
	 */
	private final Comparator<? super Order> comparator;

	private transient Entry root = null;

	/**
	 * The number of entries in the tree
	 */
	private transient int size = 0;

	/**
	 * The number of structural modifications to the tree.
	 */
	private transient int modCount = 0;

	/**
	 * Constructs a new, empty tree map, using the natural ordering of its keys.
	 * All keys inserted into the map must implement the {@link Comparable}
	 * interface. Furthermore, all such keys must be <i>mutually comparable</i>:
	 * <tt>k1.compareTo(k2)</tt> must not throw a <tt>ClassCastException</tt>
	 * for any keys <tt>k1</tt> and <tt>k2</tt> in the map. If the user attempts
	 * to put a key into the map that violates this constraint (for example, the
	 * user attempts to put a string key into a map whose keys are integers),
	 * the <tt>put(Object key, Object value)</tt> call will throw a
	 * <tt>ClassCastException</tt>.
	 */
	public SumMap() {
		comparator = null;
	}

	/**
	 * Constructs a new, empty tree map, ordered according to the given
	 * comparator. All keys inserted into the map must be <i>mutually
	 * comparable</i> by the given comparator: <tt>comparator.compare(k1,
	 * k2)</tt> must not throw a <tt>ClassCastException</tt> for any keys
	 * <tt>k1</tt> and <tt>k2</tt> in the map. If the user attempts to put a key
	 * into the map that violates this constraint, the <tt>put(Object
	 * key, Object value)</tt> call will throw a <tt>ClassCastException</tt>.
	 * 
	 * @param comparator
	 *            the comparator that will be used to order this map. If
	 *            <tt>null</tt>, the {@linkplain Comparable natural ordering} of
	 *            the keys will be used.
	 */
	public SumMap(Comparator<? super Order> comparator) {
		this.comparator = comparator;
	}

	/**
	 * Constructs a new tree map containing the same mappings as the given map,
	 * ordered according to the <i>natural ordering</i> of its keys. All keys
	 * inserted into the new map must implement the {@link Comparable}
	 * interface. Furthermore, all such keys must be <i>mutually comparable</i>:
	 * <tt>k1.compareTo(k2)</tt> must not throw a <tt>ClassCastException</tt>
	 * for any keys <tt>k1</tt> and <tt>k2</tt> in the map. This method runs in
	 * n*log(n) time.
	 * 
	 * @param m
	 *            the map whose mappings are to be placed in this map
	 * @throws ClassCastException
	 *             if the keys in m are not {@link Comparable}, or are not
	 *             mutually comparable
	 * @throws NullPointerException
	 *             if the specified map is null
	 */
	public SumMap(Map<? extends Order, ? extends Integer> m) {
		comparator = null;
		putAll(m);
	}

	/**
	 * Constructs a new tree map containing the same mappings and using the same
	 * ordering as the specified sorted map. This method runs in linear time.
	 * 
	 * @param m
	 *            the sorted map whose mappings are to be placed in this map,
	 *            and whose comparator is to be used to sort this map
	 * @throws NullPointerException
	 *             if the specified map is null
	 */
	public SumMap(SortedMap<Order, ? extends Integer> m) {
		comparator = m.comparator();
		try {
			buildFromSorted(m.size(), m.entrySet().iterator(), null, null);
		} catch (java.io.IOException cannotHappen) {
		} catch (ClassNotFoundException cannotHappen) {
		}
	}

	// Query Operations

	/**
	 * Returns the number of key-value mappings in this map.
	 * 
	 * @return the number of key-value mappings in this map
	 */
	public int size() {
		return size;
	}

	/**
	 * Returns <tt>true</tt> if this map contains a mapping for the specified
	 * key.
	 * 
	 * @param key
	 *            key whose presence in this map is to be tested
	 * @return <tt>true</tt> if this map contains a mapping for the specified
	 *         key
	 * @throws ClassCastException
	 *             if the specified key cannot be compared with the keys
	 *             currently in the map
	 * @throws NullPointerException
	 *             if the specified key is null and this map uses natural
	 *             ordering, or its comparator does not permit null keys
	 */
	public boolean containsKey(Object key) {
		return getEntry(key) != null;
	}

	/**
	 * Returns <tt>true</tt> if this map maps one or more keys to the specified
	 * value. More formally, returns <tt>true</tt> if and only if this map
	 * contains at least one mapping to a value <tt>v</tt> such that
	 * <tt>(value==null ? v==null : value.equals(v))</tt>. This operation will
	 * probably require time linear in the map size for most implementations.
	 * 
	 * @param value
	 *            value whose presence in this map is to be tested
	 * @return <tt>true</tt> if a mapping to <tt>value</tt> exists;
	 *         <tt>false</tt> otherwise
	 * @since 1.2
	 */
	public boolean containsValue(Object value) {
		for (Entry e = getFirstEntry(); e != null; e = successor(e))
			if (valEquals(value, e.value))
				return true;
		return false;
	}

	/**
	 * Returns the value to which the specified key is mapped, or {@code null}
	 * if this map contains no mapping for the key.
	 * 
	 * <p>
	 * More formally, if this map contains a mapping from a key {@code k} to a
	 * value {@code v} such that {@code key} compares equal to {@code k}
	 * according to the map's ordering, then this method returns {@code v};
	 * otherwise it returns {@code null}. (There can be at most one such
	 * mapping.)
	 * 
	 * <p>
	 * A return value of {@code null} does not <i>necessarily</i> indicate that
	 * the map contains no mapping for the key; it's also possible that the map
	 * explicitly maps the key to {@code null}. The {@link #containsKey
	 * containsKey} operation may be used to distinguish these two cases.
	 * 
	 * @throws ClassCastException
	 *             if the specified key cannot be compared with the keys
	 *             currently in the map
	 * @throws NullPointerException
	 *             if the specified key is null and this map uses natural
	 *             ordering, or its comparator does not permit null keys
	 */
	public Integer get(Object key) {
		Entry p = getEntry(key);
		return (p == null ? null : p.value);
	}

	public Comparator<? super Order> comparator() {
		return comparator;
	}

	/**
	 * @throws NoSuchElementException
	 *             {@inheritDoc}
	 */
	public Order firstKey() {
		return key(getFirstEntry());
	}

	/**
	 * @throws NoSuchElementException
	 *             {@inheritDoc}
	 */
	public Order lastKey() {
		return key(getLastEntry());
	}

	/**
	 * Copies all of the mappings from the specified map to this map. These
	 * mappings replace any mappings that this map had for any of the keys
	 * currently in the specified map.
	 * 
	 * @param map
	 *            mappings to be stored in this map
	 * @throws ClassCastException
	 *             if the class of a key or value in the specified map prevents
	 *             it from being stored in this map
	 * @throws NullPointerException
	 *             if the specified map is null or the specified map contains a
	 *             null key and this map does not permit null keys
	 */
	public void putAll(Map<? extends Order, ? extends Integer> map) {
		int mapSize = map.size();
		if (size == 0 && mapSize != 0 && map instanceof SortedMap) {
			Comparator c = ((SortedMap) map).comparator();
			if (c == comparator || (c != null && c.equals(comparator))) {
				++modCount;
				try {
					buildFromSorted(mapSize, map.entrySet().iterator(), null, null);
				} catch (java.io.IOException cannotHappen) {
				} catch (ClassNotFoundException cannotHappen) {
				}
				return;
			}
		}
		super.putAll(map);
	}

	/**
	 * Returns this map's entry for the given key, or <tt>null</tt> if the map
	 * does not contain an entry for the key.
	 * 
	 * @return this map's entry for the given key, or <tt>null</tt> if the map
	 *         does not contain an entry for the key
	 * @throws ClassCastException
	 *             if the specified key cannot be compared with the keys
	 *             currently in the map
	 * @throws NullPointerException
	 *             if the specified key is null and this map uses natural
	 *             ordering, or its comparator does not permit null keys
	 */
	final Entry getEntry(Object key) {
		// Offload comparator-based version for sake of performance
		if (comparator != null)
			return getEntryUsingComparator(key);
		if (key == null)
			throw new NullPointerException();
		Comparable<? super Order> k = (Comparable<? super Order>) key;
		Entry p = root;
		while (p != null) {
			int cmp = k.compareTo(p.key);
			if (cmp < 0)
				p = p.left;
			else if (cmp > 0)
				p = p.right;
			else
				return p;
		}
		return null;
	}

	/**
	 * Version of getEntry using comparator. Split off from getEntry for
	 * performance. (This is not worth doing for most methods, that are less
	 * dependent on comparator performance, but is worthwhile here.)
	 */
	final Entry getEntryUsingComparator(Object key) {
		Order k = (Order) key;
		Comparator<? super Order> cpr = comparator;
		if (cpr != null) {
			Entry p = root;
			while (p != null) {
				int cmp = cpr.compare(k, p.key);
				if (cmp < 0)
					p = p.left;
				else if (cmp > 0)
					p = p.right;
				else
					return p;
			}
		}
		return null;
	}

	/**
	 * Gets the entry corresponding to the specified key; if no such entry
	 * exists, returns the entry for the least key greater than the specified
	 * key; if no such entry exists (i.e., the greatest key in the Tree is less
	 * than the specified key), returns <tt>null</tt>.
	 */
	final Entry getCeilingEntry(Order key) {
		Entry p = root;
		while (p != null) {
			int cmp = compare(key, p.key);
			if (cmp < 0) {
				if (p.left != null)
					p = p.left;
				else
					return p;
			} else if (cmp > 0) {
				if (p.right != null) {
					p = p.right;
				} else {
					Entry parent = p.parent;
					Entry ch = p;
					while (parent != null && ch == parent.right) {
						ch = parent;
						parent = parent.parent;
					}
					return parent;
				}
			} else
				return p;
		}
		return null;
	}

	/**
	 * Gets the entry corresponding to the specified key; if no such entry
	 * exists, returns the entry for the greatest key less than the specified
	 * key; if no such entry exists, returns <tt>null</tt>.
	 */
	final Entry getFloorEntry(Order key) {
		Entry p = root;
		while (p != null) {
			int cmp = compare(key, p.key);
			if (cmp > 0) {
				if (p.right != null)
					p = p.right;
				else
					return p;
			} else if (cmp < 0) {
				if (p.left != null) {
					p = p.left;
				} else {
					Entry parent = p.parent;
					Entry ch = p;
					while (parent != null && ch == parent.left) {
						ch = parent;
						parent = parent.parent;
					}
					return parent;
				}
			} else
				return p;

		}
		return null;
	}

	/**
	 * Gets the entry for the least key greater than the specified key; if no
	 * such entry exists, returns the entry for the least key greater than the
	 * specified key; if no such entry exists returns <tt>null</tt>.
	 */
	final Entry getHigherEntry(Order key) {
		Entry p = root;
		while (p != null) {
			int cmp = compare(key, p.key);
			if (cmp < 0) {
				if (p.left != null)
					p = p.left;
				else
					return p;
			} else {
				if (p.right != null) {
					p = p.right;
				} else {
					Entry parent = p.parent;
					Entry ch = p;
					while (parent != null && ch == parent.right) {
						ch = parent;
						parent = parent.parent;
					}
					return parent;
				}
			}
		}
		return null;
	}

	/**
	 * Returns the entry for the greatest key less than the specified key; if no
	 * such entry exists (i.e., the least key in the Tree is greater than the
	 * specified key), returns <tt>null</tt>.
	 */
	final Entry getLowerEntry(Order key) {
		Entry p = root;
		while (p != null) {
			int cmp = compare(key, p.key);
			if (cmp > 0) {
				if (p.right != null)
					p = p.right;
				else
					return p;
			} else {
				if (p.left != null) {
					p = p.left;
				} else {
					Entry parent = p.parent;
					Entry ch = p;
					while (parent != null && ch == parent.left) {
						ch = parent;
						parent = parent.parent;
					}
					return parent;
				}
			}
		}
		return null;
	}

	/**
	 * Associates the specified value with the specified key in this map. If the
	 * map previously contained a mapping for the key, the old value is
	 * replaced.
	 * 
	 * @param key
	 *            key with which the specified value is to be associated
	 * @param value
	 *            value to be associated with the specified key
	 * 
	 * @return the previous value associated with <tt>key</tt>, or <tt>null</tt>
	 *         if there was no mapping for <tt>key</tt>. (A <tt>null</tt> return
	 *         can also indicate that the map previously associated
	 *         <tt>null</tt> with <tt>key</tt>.)
	 * @throws ClassCastException
	 *             if the specified key cannot be compared with the keys
	 *             currently in the map
	 * @throws NullPointerException
	 *             if the specified key is null and this map uses natural
	 *             ordering, or its comparator does not permit null keys
	 */
	public Integer put(Order key, Integer value) {
		Entry t = root;
		if (t == null) {
			// TBD:
			// 5045147: (coll) Adding null to an empty TreeSet should
			// throw NullPointerException
			//
			// compare(key, key); // type check
			root = new Entry(key, value, null);
			size = 1;
			modCount++;
			return null;
		}
//		assert selfCheck();
		int cmp;
		Entry parent;
		// split comparator and comparable paths
		Comparator<? super Order> cpr = comparator;
		if (cpr != null) {
			do {
				parent = t;
				cmp = cpr.compare(key, t.key);
				if (cmp < 0)
					t = t.left;
				else if (cmp > 0)
					t = t.right;
				else
					return t.setValue(value);
			} while (t != null);
		} else {
			if (key == null)
				throw new NullPointerException();
			Comparable<? super Order> k = key;
			do {
				parent = t;
				cmp = k.compareTo(t.key);
				if (cmp < 0) {
					t = t.left;
				} else if (cmp > 0) {
					t = t.right;
				} else
					return t.setValue(value);
			} while (t != null);
		}
		Entry e = new Entry(key, value, parent);
		if (cmp < 0) {
			parent.left = e;
		} else {
			parent.right = e;
		}
		adjustSum(parent, e.sum, e.totalExpense);
		fixAfterInsertion(e);
//		assert selfCheck();
		size++;
		modCount++;
		return null;
	}

	private static void adjustSum(Entry t, int sum, double totalExpense) {
		while (t != null) {
			t.sum += sum;
			t.totalExpense += totalExpense;
			assert t.sum >= 0 : "Failed to adjust " + t;
			t = t.parent;
		}
	}

	/**
	 * Removes the mapping for this key from this SumMap if present.
	 * 
	 * @param key
	 *            key for which mapping should be removed
	 * @return the previous value associated with <tt>key</tt>, or <tt>null</tt>
	 *         if there was no mapping for <tt>key</tt>. (A <tt>null</tt> return
	 *         can also indicate that the map previously associated
	 *         <tt>null</tt> with <tt>key</tt>.)
	 * @throws ClassCastException
	 *             if the specified key cannot be compared with the keys
	 *             currently in the map
	 * @throws NullPointerException
	 *             if the specified key is null and this map uses natural
	 *             ordering, or its comparator does not permit null keys
	 */
	public Integer remove(Object key) {
		Entry p = getEntry(key);
		if (p == null)
			return null;
		assert p.getKey() == key : "Found " + p + " instead of " + key;

		Integer oldValue = p.value;
		deleteEntry(p);
		return oldValue;
	}

	/**
	 * Removes all of the mappings from this map. The map will be empty after
	 * this call returns.
	 */
	public void clear() {
		modCount++;
		size = 0;
		root = null;
	}

	/**
	 * Returns a shallow copy of this <tt>SumMap</tt> instance. (The keys and
	 * values themselves are not cloned.)
	 * 
	 * @return a shallow copy of this map
	 */
	public Object clone() {
		SumMap clone = null;
		try {
			clone = (SumMap) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new InternalError();
		}

		// Put clone into "virgin" state (except for comparator)
		clone.root = null;
		clone.size = 0;
		clone.modCount = 0;
		clone.entrySet = null;

		// Initialize clone with our mappings
		try {
			clone.buildFromSorted(size, entrySet().iterator(), null, null);
		} catch (java.io.IOException cannotHappen) {
		} catch (ClassNotFoundException cannotHappen) {
		}

		return clone;
	}

	// NavigableMap API methods

	/**
	 * @since 1.6
	 */
	public Map.Entry firstEntry() {
		return exportEntry(getFirstEntry());
	}

	/**
	 * @since 1.6
	 */
	public Map.Entry lastEntry() {
		return exportEntry(getLastEntry());
	}

	/**
	 * @since 1.6
	 */
	public Map.Entry pollFirstEntry() {
		Entry p = getFirstEntry();
		Map.Entry result = exportEntry(p);
		if (p != null)
			deleteEntry(p);
		return result;
	}

	/**
	 * @since 1.6
	 */
	public Map.Entry pollLastEntry() {
		Entry p = getLastEntry();
		Map.Entry result = exportEntry(p);
		if (p != null)
			deleteEntry(p);
		return result;
	}

	/**
	 * @throws ClassCastException
	 *             {@inheritDoc}
	 * @throws NullPointerException
	 *             if the specified key is null and this map uses natural
	 *             ordering, or its comparator does not permit null keys
	 * @since 1.6
	 */
	public Map.Entry lowerEntry(Order key) {
		return exportEntry(getLowerEntry(key));
	}

	/**
	 * @throws ClassCastException
	 *             {@inheritDoc}
	 * @throws NullPointerException
	 *             if the specified key is null and this map uses natural
	 *             ordering, or its comparator does not permit null keys
	 * @since 1.6
	 */
	public Order lowerKey(Order key) {
		return keyOrNull(getLowerEntry(key));
	}

	/**
	 * @throws ClassCastException
	 *             {@inheritDoc}
	 * @throws NullPointerException
	 *             if the specified key is null and this map uses natural
	 *             ordering, or its comparator does not permit null keys
	 * @since 1.6
	 */
	public Map.Entry floorEntry(Order key) {
		return exportEntry(getFloorEntry(key));
	}

	/**
	 * @throws ClassCastException
	 *             {@inheritDoc}
	 * @throws NullPointerException
	 *             if the specified key is null and this map uses natural
	 *             ordering, or its comparator does not permit null keys
	 * @since 1.6
	 */
	public Order floorKey(Order key) {
		return keyOrNull(getFloorEntry(key));
	}

	/**
	 * @throws ClassCastException
	 *             {@inheritDoc}
	 * @throws NullPointerException
	 *             if the specified key is null and this map uses natural
	 *             ordering, or its comparator does not permit null keys
	 * @since 1.6
	 */
	public Map.Entry ceilingEntry(Order key) {
		return exportEntry(getCeilingEntry(key));
	}

	/**
	 * @throws ClassCastException
	 *             {@inheritDoc}
	 * @throws NullPointerException
	 *             if the specified key is null and this map uses natural
	 *             ordering, or its comparator does not permit null keys
	 * @since 1.6
	 */
	public Order ceilingKey(Order key) {
		return keyOrNull(getCeilingEntry(key));
	}

	/**
	 * @throws ClassCastException
	 *             {@inheritDoc}
	 * @throws NullPointerException
	 *             if the specified key is null and this map uses natural
	 *             ordering, or its comparator does not permit null keys
	 * @since 1.6
	 */
	public Map.Entry higherEntry(Order key) {
		return exportEntry(getHigherEntry(key));
	}

	/**
	 * @throws ClassCastException
	 *             {@inheritDoc}
	 * @throws NullPointerException
	 *             if the specified key is null and this map uses natural
	 *             ordering, or its comparator does not permit null keys
	 * @since 1.6
	 */
	public Order higherKey(Order key) {
		return keyOrNull(getHigherEntry(key));
	}

	// Views

	/**
	 * Fields initialized to contain an instance of the entry set view the first
	 * time this view is requested. Views are stateless, so there's no reason to
	 * create more than one.
	 */
	private transient EntrySet entrySet = null;

	/**
	 * Returns a {@link Set} view of the keys contained in this map. The set's
	 * iterator returns the keys in ascending order. The set is backed by the
	 * map, so changes to the map are reflected in the set, and vice-versa. If
	 * the map is modified while an iteration over the set is in progress
	 * (except through the iterator's own <tt>remove</tt> operation), the
	 * results of the iteration are undefined. The set supports element removal,
	 * which removes the corresponding mapping from the map, via the
	 * <tt>Iterator.remove</tt>, <tt>Set.remove</tt>, <tt>removeAll</tt>,
	 * <tt>retainAll</tt>, and <tt>clear</tt> operations. It does not support
	 * the <tt>add</tt> or <tt>addAll</tt> operations.
	 */
	// public Set<K> keySet() {
	// return navigableKeySet();
	// }

	/**
	 * @since 1.6
	 */
	// public NavigableSet<K> navigableKeySet() {
	// KeySet<K> nks = navigableKeySet;
	// return (nks != null) ? nks : (navigableKeySet = new KeySet(this));
	// }

	/**
	 * @since 1.6
	 */
	// public NavigableSet<K> descendingKeySet() {
	// return descendingMap().navigableKeySet();
	// }

	/**
	 * Returns a {@link Collection} view of the values contained in this map.
	 * The collection's iterator returns the values in ascending order of the
	 * corresponding keys. The collection is backed by the map, so changes to
	 * the map are reflected in the collection, and vice-versa. If the map is
	 * modified while an iteration over the collection is in progress (except
	 * through the iterator's own <tt>remove</tt> operation), the results of the
	 * iteration are undefined. The collection supports element removal, which
	 * removes the corresponding mapping from the map, via the
	 * <tt>Iterator.remove</tt>, <tt>Collection.remove</tt>, <tt>removeAll</tt>,
	 * <tt>retainAll</tt> and <tt>clear</tt> operations. It does not support the
	 * <tt>add</tt> or <tt>addAll</tt> operations.
	 */
	// public Collection<V> values() {
	// Collection<V> vs = values;
	// return (vs != null) ? vs : (values = new Values());
	// }

	/**
	 * Returns a {@link Set} view of the mappings contained in this map. The
	 * set's iterator returns the entries in ascending key order. The set is
	 * backed by the map, so changes to the map are reflected in the set, and
	 * vice-versa. If the map is modified while an iteration over the set is in
	 * progress (except through the iterator's own <tt>remove</tt> operation, or
	 * through the <tt>setValue</tt> operation on a map entry returned by the
	 * iterator) the results of the iteration are undefined. The set supports
	 * element removal, which removes the corresponding mapping from the map,
	 * via the <tt>Iterator.remove</tt>, <tt>Set.remove</tt>, <tt>removeAll</tt>
	 * , <tt>retainAll</tt> and <tt>clear</tt> operations. It does not support
	 * the <tt>add</tt> or <tt>addAll</tt> operations.
	 */
	public Set<java.util.Map.Entry<Order, Integer>> entrySet() {
		EntrySet es = entrySet;
		return (es != null) ? es : (entrySet = new EntrySet());
	}

	// View class support

	class Values extends AbstractCollection<Integer> {
		public Iterator<Integer> iterator() {
			return new ValueIterator(getFirstEntry());
		}

		public int size() {
			return SumMap.this.size();
		}

		public boolean contains(Object o) {
			return SumMap.this.containsValue(o);
		}

		@SuppressWarnings("synthetic-access")
		public boolean remove(Object o) {
			for (Entry e = getFirstEntry(); e != null; e = successor(e)) {
				if (valEquals(e.getValue(), o)) {
					deleteEntry(e);
					return true;
				}
			}
			return false;
		}

		public void clear() {
			SumMap.this.clear();
		}
	}

	class EntrySet extends AbstractSet<Map.Entry<Order, Integer>> {
		public Iterator<java.util.Map.Entry<Order, Integer>> iterator() {
			return new EntryIterator(getFirstEntry());
		}

		public boolean contains(Object o) {
			if (!(o instanceof Map.Entry))
				return false;
			Map.Entry<Order, Integer> entry = (Map.Entry) o;
			Integer value = entry.getValue();
			Entry p = getEntry(entry.getKey());
			return p != null && valEquals(p.getValue(), value);
		}

		@SuppressWarnings("synthetic-access")
		public boolean remove(Object o) {
			if (!(o instanceof Map.Entry))
				return false;
			Map.Entry<Order, Integer> entry = (Map.Entry) o;
			Integer value = entry.getValue();
			Entry p = getEntry(entry.getKey());
			if (p != null && valEquals(p.getValue(), value)) {
				deleteEntry(p);
				return true;
			}
			return false;
		}

		public int size() {
			return SumMap.this.size();
		}

		public void clear() {
			SumMap.this.clear();
		}
	}

	/*
	 * Unlike Values and EntrySet, the KeySet class is static, delegating to a
	 * NavigableMap to allow use by SubMaps, which outweighs the ugliness of
	 * needing type-tests for the following Iterator methods that are defined
	 * appropriately in main versus submap classes.
	 */

	Iterator<Order> keyIterator() {
		return new KeyIterator(getFirstEntry());
	}

	Iterator<Order> descendingKeyIterator() {
		return new DescendingKeyIterator(getLastEntry());
	}

	/**
	 * Base class for SumMap Iterators
	 */
	abstract class PrivateEntryIterator<T> implements Iterator<T> {
		Entry next;
		Entry lastReturned;
		int expectedModCount;

		PrivateEntryIterator(Entry first) {
			expectedModCount = modCount;
			lastReturned = null;
			next = first;
		}

		public final boolean hasNext() {
			return next != null;
		}

		final Entry nextEntry() {
			Entry e = next;
			if (e == null)
				throw new NoSuchElementException();
			if (modCount != expectedModCount)
				throw new ConcurrentModificationException();
			next = successor(e);
			lastReturned = e;
			return e;
		}

		final Entry prevEntry() {
			Entry e = next;
			if (e == null)
				throw new NoSuchElementException();
			if (modCount != expectedModCount)
				throw new ConcurrentModificationException();
			next = predecessor(e);
			lastReturned = e;
			return e;
		}

		public void remove() {
			if (lastReturned == null)
				throw new IllegalStateException();
			if (modCount != expectedModCount)
				throw new ConcurrentModificationException();
			// deleted entries are replaced by their successors
			if (lastReturned.left != null && lastReturned.right != null)
				next = lastReturned;
			deleteEntry(lastReturned);
			expectedModCount = modCount;
			lastReturned = null;
		}
	}

	final class EntryIterator extends PrivateEntryIterator<Map.Entry<Order, Integer>> {
		EntryIterator(Entry first) {
			super(first);
		}

		public Map.Entry next() {
			return nextEntry();
		}
	}

	final class ValueIterator extends PrivateEntryIterator<Integer> {
		ValueIterator(Entry first) {
			super(first);
		}

		public Integer next() {
			return nextEntry().value;
		}
	}

	final class KeyIterator extends PrivateEntryIterator<Order> {
		KeyIterator(Entry first) {
			super(first);
		}

		public Order next() {
			return nextEntry().key;
		}
	}

	final class DescendingKeyIterator extends PrivateEntryIterator<Order> {
		DescendingKeyIterator(Entry first) {
			super(first);
		}

		public Order next() {
			return prevEntry().key;
		}
	}

	// Little utilities

	/**
	 * Compares two keys using the correct comparison method for this SumMap.
	 */
	final int compare(Object k1, Object k2) {
		return comparator == null ? ((Comparable<? super Order>) k1).compareTo((Order) k2) : comparator.compare((Order) k1, (Order) k2);
	}

	/**
	 * Test two values for equality. Differs from o1.equals(o2) only in that it
	 * copes with <tt>null</tt> o1 properly.
	 */
	final static boolean valEquals(Object o1, Object o2) {
		return (o1 == null ? o2 == null : o1.equals(o2));
	}

	/**
	 * Return SimpleImmutableEntry for entry, or null if null
	 */
	static Map.Entry<Order, Integer> exportEntry(SumMap.Entry e) {
		return e == null ? null : new SumMap.Entry(e.key, e.value, e.parent);
	}

	/**
	 * Return key for entry, or null if null
	 */
	static Order keyOrNull(SumMap.Entry e) {
		return e == null ? null : e.key;
	}

	/**
	 * Returns the key corresponding to the specified Entry.
	 * 
	 * @throws NoSuchElementException
	 *             if the Entry is null
	 */
	static Order key(Entry e) {
		if (e == null)
			throw new NoSuchElementException();
		return e.key;
	}

	// Red-black mechanics

	private static final boolean RED = false;
	private static final boolean BLACK = true;

	/**
	 * Node in the Tree. Doubles as a means to pass key-value pairs back to user
	 * (see Map.Entry).
	 */

	static final class Entry implements Map.Entry<Order, Integer> {
		Order key;
		int value;
		Entry left = null;
		Entry right = null;
		Entry parent;
		boolean color = BLACK;
		int sum;
		double totalExpense; // sum * price

		/**
		 * Make a new cell with given key, value, and parent, and with
		 * <tt>null</tt> child links, and BLACK color.
		 */
		Entry(Order key, Integer value, Entry parent) {
			this.key = key;
			this.value = value;
			this.parent = parent;
			sum = value;
			totalExpense = key.getPrice() * value;
		}

		/**
		 * Returns the key.
		 * 
		 * @return the key
		 */
		public Order getKey() {
			return key;
		}

		/**
		 * Returns the value associated with the key.
		 * 
		 * @return the value associated with the key
		 */
		public Integer getValue() {
			return value;
		}

		/**
		 * Replaces the value currently associated with the key with the given
		 * value.
		 * 
		 * @return the value associated with the key before this method was
		 *         called
		 */
		public Integer setValue(Integer value) {
			int diff = ((Number) value).intValue() - ((Number) this.value).intValue();
			Integer oldValue = this.value;
			this.value = value;
			sum += diff;
			double expenseDiff = key.getPrice() * diff;
			totalExpense += expenseDiff;
			adjustSum(parent, diff, expenseDiff);
			return oldValue;
		}

		public boolean equals(Object o) {
			if (!(o instanceof Map.Entry))
				return false;
			Map.Entry<?, ?> e = (Map.Entry<?, ?>) o;

			return valEquals(key, e.getKey()) && valEquals(value, e.getValue());
		}

		public int hashCode() {
			int keyHash = (key == null ? 0 : key.hashCode());
			int valueHash = value;
			return keyHash ^ valueHash;
		}

		public String toString() {
			return key + " �=" + sum;
		}

		public double getExpense() {
			return key.getPrice() * value;
		}

	}

	/**
	 * Returns the first Entry in the SumMap (according to the SumMap's key-sort
	 * function). Returns null if the SumMap is empty.
	 */
	final Entry getFirstEntry() {
		Entry p = root;
		if (p != null)
			while (p.left != null)
				p = p.left;
		return p;
	}

	/**
	 * Returns the last Entry in the SumMap (according to the SumMap's key-sort
	 * function). Returns null if the SumMap is empty.
	 */
	final Entry getLastEntry() {
		Entry p = root;
		if (p != null)
			while (p.right != null)
				p = p.right;
		return p;
	}

	/**
	 * Returns the successor of the specified Entry, or null if no such.
	 */
	static <K, V> SumMap.Entry successor(Entry t) {
		if (t == null)
			return null;
		else if (t.right != null) {
			Entry p = t.right;
			while (p.left != null)
				p = p.left;
			return p;
		} else {
			Entry p = t.parent;
			Entry ch = t;
			while (p != null && ch == p.right) {
				ch = p;
				p = p.parent;
			}
			return p;
		}
	}

	/**
	 * Returns the predecessor of the specified Entry, or null if no such.
	 */
	static <K, V> Entry predecessor(Entry t) {
		if (t == null)
			return null;
		else if (t.left != null) {
			Entry p = t.left;
			while (p.right != null)
				p = p.right;
			return p;
		} else {
			Entry p = t.parent;
			Entry ch = t;
			while (p != null && ch == p.left) {
				ch = p;
				p = p.parent;
			}
			return p;
		}
	}

	/**
	 * Balancing operations.
	 * 
	 * Implementations of rebalancings during insertion and deletion are
	 * slightly different than the CLR version. Rather than using dummy
	 * nilnodes, we use a set of accessors that deal properly with null. They
	 * are used to avoid messiness surrounding nullness checks in the main
	 * algorithms.
	 */

	private static boolean colorOf(Entry p) {
		return (p == null ? BLACK : p.color);
	}

	private static Entry parentOf(Entry p) {
		return (p == null ? null : p.parent);
	}

	private static void setColor(Entry p, boolean c) {
		if (p != null)
			p.color = c;
	}

	private static Entry leftOf(Entry p) {
		return (p == null) ? null : p.left;
	}

	private static Entry rightOf(Entry p) {
		return (p == null) ? null : p.right;
	}

	/** 
	 * Care must be taken to keep the running sum's in sync while rotating elements in the tree.  
	 * @param p
	 */
	private void rotateLeft(Entry p) {
		if (p != null) {
			Entry r = p.right;
			p.sum -= r.sum;
			p.totalExpense -= r.totalExpense;
			p.right = r.left;
			if (p.right != null) {
				p.sum += p.right.sum;
				p.totalExpense += p.right.totalExpense;
			}
			if (r.left != null) {
				r.left.parent = p;
			}
			r.parent = p.parent;
			if (p.parent == null)
				root = r;
			else if (p.parent.left == p)
				p.parent.left = r;
			else
				p.parent.right = r;
			if (r.left != null) {
				r.sum -= r.left.sum;
				r.totalExpense -= r.left.totalExpense;
			}
			r.left = p;
			r.sum += p.sum;
			r.totalExpense += p.totalExpense;
			p.parent = r;
		}
	}

	/** From CLR */
	private void rotateRight(Entry p) {
		if (p != null) {
			Entry l = p.left;
			p.sum -= l.sum;
			p.totalExpense -= l.totalExpense;
			p.left = l.right;
			if (p.left != null) {
				p.sum += p.left.sum;
				p.totalExpense += p.left.totalExpense;
			}
			if (l.right != null)
				l.right.parent = p;
			l.parent = p.parent;
			if (p.parent == null)
				root = l;
			else if (p.parent.right == p)
				p.parent.right = l;
			else
				p.parent.left = l;
			if (l.right != null) {
				l.sum -= l.right.sum;
				l.totalExpense -= l.right.totalExpense;
			}
			l.right = p;
			l.sum += p.sum;
			l.totalExpense += p.totalExpense;
			p.parent = l;
		}
	}

	/** From CLR */
	private void fixAfterInsertion(Entry x) {
		x.color = RED;

		while (x != null && x != root && x.parent.color == RED) {
			if (parentOf(x) == leftOf(parentOf(parentOf(x)))) {
				Entry y = rightOf(parentOf(parentOf(x)));
				if (colorOf(y) == RED) {
					setColor(parentOf(x), BLACK);
					setColor(y, BLACK);
					setColor(parentOf(parentOf(x)), RED);
					x = parentOf(parentOf(x));
				} else {
					if (x == rightOf(parentOf(x))) {
						x = parentOf(x);
						rotateLeft(x);
					}
					setColor(parentOf(x), BLACK);
					setColor(parentOf(parentOf(x)), RED);
					rotateRight(parentOf(parentOf(x)));
				}
			} else {
				Entry y = leftOf(parentOf(parentOf(x)));
				if (colorOf(y) == RED) {
					setColor(parentOf(x), BLACK);
					setColor(y, BLACK);
					setColor(parentOf(parentOf(x)), RED);
					x = parentOf(parentOf(x));
				} else {
					if (x == leftOf(parentOf(x))) {
						x = parentOf(x);
						rotateRight(x);
					}
					setColor(parentOf(x), BLACK);
					setColor(parentOf(parentOf(x)), RED);
					rotateLeft(parentOf(parentOf(x)));
				}
			}
		}
		root.color = BLACK;
	}

	/**
	 * Delete node p, and then rebalance the tree.
	 */
	private void deleteEntry(Entry p) {
		modCount++;
		size--;

		int oldValue = p.value;
		double oldTotal = p.getExpense();
		if (p.parent != null) {
			adjustSum(p.parent, -oldValue, -oldTotal);
		}
		// If strictly internal, copy successor's element to p and then make p
		// point to successor.
		if (p.left != null && p.right != null) {
			Entry s = successor(p);
			p.key = s.key;
			p.value = s.value;
			// copy aggregate values as well
			p.sum = s.sum;
			p.totalExpense = s.totalExpense;
			p = s;
		} // p has 2 children
		else {
			// zero out values so sum's won't get affected during rotation
			p.sum = 0;
			p.totalExpense = 0d;
			p.value = 0;
		}

		// Start fixup at replacement node, if it exists.
		Entry replacement = (p.left != null ? p.left : p.right);

		if (replacement != null) {
			// Link replacement to parent
			replacement.parent = p.parent;
			if (p.parent == null)
				root = replacement;
			else if (p == p.parent.left)
				p.parent.left = replacement;
			else
				p.parent.right = replacement;
			fixSum(p.parent);

			// Null out links so they are OK to use by fixAfterDeletion.
			p.left = p.right = p.parent = null;

			// Fix replacement
			if (p.color == BLACK)
				fixAfterDeletion(replacement);
		} else if (p.parent == null) { // return if we are the only node.
			root = null;
		} else { // No children. Use self as phantom replacement and unlink.
			if (p.color == BLACK) {
				fixAfterDeletion(p);
			}

			if (p.parent != null) {
				if (p == p.parent.left)
					p.parent.left = null;
				else if (p == p.parent.right)
					p.parent.right = null;
				fixSum(p.parent);
				p.parent = null;
			}
		}
	}



	/**
	 * recalcuate the sum from the current node back to the root
	 * @param p
	 */
	private void fixSum(Entry p) {
		while(p != null) {
			p.sum = (p.left != null ? p.left.sum : 0) + (p.right != null ? p.right.sum: 0) + p.value;
			p.totalExpense = (p.left != null ? p.left.totalExpense : 0d) + (p.right != null ? p.right.totalExpense : 0d) + (p.getExpense());
			p = p.parent;
		}

	}

	/** From CLR */
	private void fixAfterDeletion(Entry x) {
		while (x != root && colorOf(x) == BLACK) {
			if (x == leftOf(parentOf(x))) {
				Entry sib = rightOf(parentOf(x));

				if (colorOf(sib) == RED) {
					setColor(sib, BLACK);
					setColor(parentOf(x), RED);
					rotateLeft(parentOf(x));
					sib = rightOf(parentOf(x));
				}

				if (colorOf(leftOf(sib)) == BLACK && colorOf(rightOf(sib)) == BLACK) {
					setColor(sib, RED);
					x = parentOf(x);
				} else {
					if (colorOf(rightOf(sib)) == BLACK) {
						setColor(leftOf(sib), BLACK);
						setColor(sib, RED);
						rotateRight(sib);
						sib = rightOf(parentOf(x));
					}
					setColor(sib, colorOf(parentOf(x)));
					setColor(parentOf(x), BLACK);
					setColor(rightOf(sib), BLACK);
					rotateLeft(parentOf(x));
					x = root;
				}
			} else { // symmetric
				Entry sib = leftOf(parentOf(x));

				if (colorOf(sib) == RED) {
					setColor(sib, BLACK);
					setColor(parentOf(x), RED);
					rotateRight(parentOf(x));
					sib = leftOf(parentOf(x));
				}

				if (colorOf(rightOf(sib)) == BLACK && colorOf(leftOf(sib)) == BLACK) {
					setColor(sib, RED);
					x = parentOf(x);
				} else {
					if (colorOf(leftOf(sib)) == BLACK) {
						setColor(rightOf(sib), BLACK);
						setColor(sib, RED);
						rotateLeft(sib);
						sib = leftOf(parentOf(x));
					}
					setColor(sib, colorOf(parentOf(x)));
					setColor(parentOf(x), BLACK);
					setColor(leftOf(sib), BLACK);
					rotateRight(parentOf(x));
					x = root;
				}
			}
		}

		setColor(x, BLACK);
	}

	private static final long serialVersionUID = 919286545866124006L;

	/**
	 * Save the state of the <tt>SumMap</tt> instance to a stream (i.e.,
	 * serialize it).
	 * 
	 * @serialData The <i>size</i> of the SumMap (the number of key-value
	 *             mappings) is emitted (int), followed by the key (Object) and
	 *             value (Object) for each key-value mapping represented by the
	 *             SumMap. The key-value mappings are emitted in key-order (as
	 *             determined by the SumMap's Comparator, or by the keys'
	 *             natural ordering if the SumMap has no Comparator).
	 */
	private void writeObject(java.io.ObjectOutputStream s) throws java.io.IOException {
		// Write out the Comparator and any hidden stuff
		s.defaultWriteObject();

		// Write out size (number of Mappings)
		s.writeInt(size);

		// Write out keys and values (alternating)
		for (Iterator<java.util.Map.Entry<Order, Integer>> i = entrySet().iterator(); i.hasNext();) {
			Map.Entry e = i.next();
			s.writeObject(e.getKey());
			s.writeObject(e.getValue());
		}
	}

	/**
	 * Reconstitute the <tt>SumMap</tt> instance from a stream (i.e.,
	 * deserialize it).
	 */
	private void readObject(final java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException {
		// Read in the Comparator and any hidden stuff
		s.defaultReadObject();

		// Read in size
		int _size = s.readInt();

		buildFromSorted(_size, null, s, null);
	}

	/** Intended to be called only from TreeSet.readObject */
	void readTreeSet(int _size, java.io.ObjectInputStream s, Integer defaultVal) throws java.io.IOException, ClassNotFoundException {
		buildFromSorted(_size, null, s, defaultVal);
	}

	/** Intended to be called only from TreeSet.addAll */
	void addAllForTreeSet(SortedSet<? extends Order> set, Integer defaultVal) {
		try {
			buildFromSorted(set.size(), set.iterator(), null, defaultVal);
		} catch (java.io.IOException cannotHappen) {
		} catch (ClassNotFoundException cannotHappen) {
		}
	}

	/**
	 * Linear time tree building algorithm from sorted data. Can accept keys
	 * and/or values from iterator or stream. This leads to too many parameters,
	 * but seems better than alternatives. The four formats that this method
	 * accepts are:
	 * 
	 * 1) An iterator of Map.Entries. (it != null, defaultVal == null). 2) An
	 * iterator of keys. (it != null, defaultVal != null). 3) A stream of
	 * alternating serialized keys and values. (it == null, defaultVal == null).
	 * 4) A stream of serialized keys. (it == null, defaultVal != null).
	 * 
	 * It is assumed that the comparator of the SumMap is already set prior to
	 * calling this method.
	 * 
	 * @param _size
	 *            the number of keys (or key-value pairs) to be read from the
	 *            iterator or stream
	 * @param it
	 *            If non-null, new entries are created from entries or keys read
	 *            from this iterator.
	 * @param str
	 *            If non-null, new entries are created from keys and possibly
	 *            values read from this stream in serialized form. Exactly one
	 *            of it and str should be non-null.
	 * @param defaultVal
	 *            if non-null, this default value is used for each value in the
	 *            map. If null, each value is read from iterator or stream, as
	 *            described above.
	 * @throws IOException
	 *             propagated from stream reads. This cannot occur if str is
	 *             null.
	 * @throws ClassNotFoundException
	 *             propagated from readObject. This cannot occur if str is null.
	 */
	private void buildFromSorted(int _size, Iterator it, java.io.ObjectInputStream str, Integer defaultVal) throws java.io.IOException, ClassNotFoundException {
		this.size = _size;
		root = buildFromSorted(0, 0, _size - 1, computeRedLevel(_size), it, str, defaultVal);
	}

	/**
	 * Recursive "helper method" that does the real work of the previous method.
	 * Identically named parameters have identical definitions. Additional
	 * parameters are documented below. It is assumed that the comparator and
	 * size fields of the SumMap are already set prior to calling this method.
	 * (It ignores both fields.)
	 * 
	 * @param level
	 *            the current level of tree. Initial call should be 0.
	 * @param lo
	 *            the first element index of this subtree. Initial should be 0.
	 * @param hi
	 *            the last element index of this subtree. Initial should be
	 *            size-1.
	 * @param redLevel
	 *            the level at which nodes should be red. Must be equal to
	 *            computeRedLevel for tree of this size.
	 */
	private final Entry buildFromSorted(int level, int lo, int hi, int redLevel, Iterator it, java.io.ObjectInputStream str, Integer defaultVal) throws java.io.IOException, ClassNotFoundException {
		/*
		 * Strategy: The root is the middlemost element. To get to it, we have
		 * to first recursively construct the entire left subtree, so as to grab
		 * all of its elements. We can then proceed with right subtree.
		 * 
		 * The lo and hi arguments are the minimum and maximum indices to pull
		 * out of the iterator or stream for current subtree. They are not
		 * actually indexed, we just proceed sequentially, ensuring that items
		 * are extracted in corresponding order.
		 */

		if (hi < lo)
			return null;

		int mid = (lo + hi) / 2;

		Entry left = null;
		if (lo < mid)
			left = buildFromSorted(level + 1, lo, mid - 1, redLevel, it, str, defaultVal);

		// extract key and/or value from iterator or stream
		Order key;
		Integer value;
		if (it != null) {
			if (defaultVal == null) {
				Map.Entry<Order, Integer> entry = (Map.Entry) it.next();
				key = entry.getKey();
				value = entry.getValue();
			} else {
				key = (Order) it.next();
				value = defaultVal;
			}
		} else { // use stream
			key = (Order) str.readObject();
			value = (defaultVal != null ? defaultVal : (Integer) str.readObject());
		}

		Entry middle = new Entry(key, value, null);

		// color nodes in non-full bottommost level red
		if (level == redLevel)
			middle.color = RED;

		if (left != null) {
			middle.left = left;
			left.parent = middle;
		}

		if (mid < hi) {
			Entry right = buildFromSorted(level + 1, mid + 1, hi, redLevel, it, str, defaultVal);
			middle.right = right;
			right.parent = middle;
		}

		return middle;
	}

	/**
	 * Find the level down to which to assign all nodes BLACK. This is the last
	 * `full' level of the complete binary tree produced by buildTree. The
	 * remaining nodes are colored RED. (This makes a `nice' set of color
	 * assignments wrt future insertions.) This level number is computed by
	 * finding the number of splits needed to reach the zeroeth node. (The
	 * answer is ~lg(N), but in any case must be computed by same quick O(lg(N))
	 * loop.)
	 */
	private static int computeRedLevel(int sz) {
		int level = 0;
		for (int m = sz - 1; m >= 0; m = m / 2 - 1)
			level++;
		return level;
	}

	public boolean selfCheck() {
		int sum = 0;
		for (Map.Entry<Order, Integer> e : entrySet()) {
			assert e.getKey().getSize() == e.getValue() : "key/value mismatch key=" + e + " value=" + e.getValue();
			sum += e.getValue();
		}
		int rootSum = root != null ? root.sum : 0;
		assert sum == rootSum : "root " + rootSum + " != " + sum + " at " + modCount; 

		//traverse
		check(root);
		return true;
	}

	private void check(Entry p) {
		if (p == null)
			return;
		check(p.left);
		check(p.right);
		assert (p.left != null ? p.left.sum : 0) + (p.right != null ? p.right.sum : 0) + p.value == p.sum
		: "Failed sum for " + p + " at step " + modCount;
	}

	/**
	 * This is the key method added to Sun's TreeMap.  The root node contains the sum of the entire tree.  Nodes and branches 
	 * are removed until the sum equals the target.  This method runs in O(log n) time.
	 * @param target
	 * @return The total expense to reach the target or null if the target can't
	 *         be reached
	 */
	public Double totalExpense(int target) {
		Entry p = root;
		if (p == null)
			return null;
		int sum = root.sum;
		if (sum < target)
			return null;
		double totalExpense = root.totalExpense;
		while (p != null && sum > target) {
			if (p.right != null && sum - p.right.sum < target) {
				p = p.right;
				continue;
			}
			// if we can reach our target without the right side at all, then we don't need to traverse it.
			if (p.right != null && sum - p.right.sum >= target) {
				sum -= p.right.sum;
				totalExpense = totalExpense - p.right.totalExpense;
			}
			if (sum - p.value > target) { // remove the whole value of the current node
				sum -= p.value;
				totalExpense = totalExpense - p.getExpense();
			} else {
				// remove a partial node
				int leavesQty = sum - target;
				totalExpense = totalExpense - p.key.getPrice() * leavesQty;
				return totalExpense; // there can only be one partial fill, and
			}
			p = p.left;
		}
		assert sum == target		: "expected sum(" + sum + ")==target(" + target + ") at " + modCount;
		return totalExpense;
	}

}