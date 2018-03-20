package com.sciome.charts.jfree.dataset;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.jfree.chart.util.Args;
import org.jfree.chart.util.SortOrder;
import org.jfree.data.KeyedValueComparator;
import org.jfree.data.KeyedValueComparatorType;
import org.jfree.data.UnknownKeyException;

public class ArrayKeyedValues {
	
    /** For serialization. */
    private static final long serialVersionUID = 8468154364608194797L;

    /** Storage for the keys. */
    private ArrayList keys;

    /** Storage for the values. */
    private ArrayList values;

    /**
     * Contains (key, Integer) mappings, where the Integer is the index for
     * the key in the list.
     */
    private HashMap indexMap;

  /**
     * Creates a new collection (initially empty).
     */
    public ArrayKeyedValues() {
        this.keys = new ArrayList();
        this.values = new ArrayList();
        this.indexMap = new HashMap();
    }

    /**
     * Returns the number of items (values) in the collection.
     *
     * @return The item count.
     */
    public int getItemCount() {
        return this.indexMap.size();
    }

    /**
     * Returns a value.
     *
     * @param item  the item of interest (zero-based index).
     *
     * @return The value (possibly {@code null}).
     *
     * @throws IndexOutOfBoundsException if {@code item} is out of bounds.
     */
    public ArrayList<Number> getValue(int item) {
        return (ArrayList<Number>) this.values.get(item);
    }

    /**
     * Returns a key.
     *
     * @param index  the item index (zero-based).
     *
     * @return The row key.
     *
     * @throws IndexOutOfBoundsException if {@code item} is out of bounds.
     */
    public Comparable getKey(int index) {
        return (Comparable) this.keys.get(index);
    }

    /**
     * Returns the index for a given key.
     *
     * @param key  the key ({@code null} not permitted).
     *
     * @return The index, or {@code -1} if the key is not recognised.
     *
     * @throws IllegalArgumentException if {@code key} is
     *     {@code null}.
     */
    public int getIndex(Comparable key) {
        Args.nullNotPermitted(key, "key");
        final Integer i = (Integer) this.indexMap.get(key);
        if (i == null) {
            return -1;  // key not found
        }
        return i.intValue();
    }

    /**
     * Returns the keys for the values in the collection.
     *
     * @return The keys (never {@code null}).
     */
    public List getKeys() {
        return (List) this.keys.clone();
    }

    /**
     * Returns the value for a given key.
     *
     * @param key  the key ({@code null} not permitted).
     *
     * @return The value (possibly {@code null}).
     *
     * @throws UnknownKeyException if the key is not recognised.
     *
     * @see #getValue(int)
     */
    public ArrayList<Number> getValue(Comparable key) {
        int index = getIndex(key);
        if (index < 0) {
            throw new UnknownKeyException("Key not found: " + key);
        }
        return getValue(index);
    }

    /**
     * Updates an existing value, or adds a new value to the collection.
     *
     * @param key  the key ({@code null} not permitted).
     * @param value  the value.
     *
     * @see #addValue(Comparable, Number)
     */
    public void addValue(Comparable key, double value) {
        addValue(key, new Double(value));
    }

    /**
     * Adds a new value to the collection, or updates an existing value.
     * This method passes control directly to the
     * {@link #setValue(Comparable, Number)} method.
     *
     * @param key  the key ({@code null} not permitted).
     * @param value  the value ({@code null} permitted).
     */
    public void addValue(Comparable key, ArrayList<Number> value) {
        setValue(key, value);
    }

    /**
     * Updates an existing value, or adds a new value to the collection.
     *
     * @param key  the key ({@code null} not permitted).
     * @param value  the value ({@code null} permitted).
     */
    public void setValue(Comparable key, ArrayList<Number> value) {
        Args.nullNotPermitted(key, "key");
        int keyIndex = getIndex(key);
        if (keyIndex >= 0) {
            this.keys.set(keyIndex, key);
            this.values.set(keyIndex, value);
        }
        else {
            this.keys.add(key);
            this.values.add(value);
            this.indexMap.put(key, new Integer(this.keys.size() - 1));
        }
    }

    /**
     * Inserts a new value at the specified position in the dataset or, if
     * there is an existing item with the specified key, updates the value
     * for that item and moves it to the specified position.
     *
     * @param position  the position (in the range 0 to getItemCount()).
     * @param key  the key ({@code null} not permitted).
     * @param value  the value.
     *
     * @since 1.0.6
     */
    public void insertValue(int position, Comparable key, double value) {
        insertValue(position, key, new Double(value));
    }

    /**
     * Inserts a new value at the specified position in the dataset or, if
     * there is an existing item with the specified key, updates the value
     * for that item and moves it to the specified position.
     *
     * @param position  the position (in the range 0 to getItemCount()).
     * @param key  the key ({@code null} not permitted).
     * @param value  the value ({@code null} permitted).
     *
     * @since 1.0.6
     */
    public void insertValue(int position, Comparable key, Number value) {
        if (position < 0 || position > getItemCount()) {
            throw new IllegalArgumentException("'position' out of bounds.");
        }
        Args.nullNotPermitted(key, "key");
        int pos = getIndex(key);
        if (pos == position) {
            this.keys.set(pos, key);
            this.values.set(pos, value);
        }
        else {
            if (pos >= 0) {
                this.keys.remove(pos);
                this.values.remove(pos);
            }

            this.keys.add(position, key);
            this.values.add(position, value);
            rebuildIndex();
        }
    }

    /**
     * Rebuilds the key to indexed-position mapping after an positioned insert
     * or a remove operation.
     */
    private void rebuildIndex () {
        this.indexMap.clear();
        for (int i = 0; i < this.keys.size(); i++) {
            final Object key = this.keys.get(i);
            this.indexMap.put(key, new Integer(i));
        }
    }

    /**
     * Removes a value from the collection.
     *
     * @param index  the index of the item to remove (in the range
     *     {@code 0} to {@code getItemCount() -1}).
     *
     * @throws IndexOutOfBoundsException if {@code index} is not within
     *     the specified range.
     */
    public void removeValue(int index) {
        this.keys.remove(index);
        this.values.remove(index);
        rebuildIndex();
    }

    /**
     * Removes a value from the collection.
     *
     * @param key  the item key ({@code null} not permitted).
     *
     * @throws IllegalArgumentException if {@code key} is
     *     {@code null}.
     * @throws UnknownKeyException if {@code key} is not recognised.
     */
    public void removeValue(Comparable key) {
        int index = getIndex(key);
        if (index < 0) {
            throw new UnknownKeyException("The key (" + key
                    + ") is not recognised.");
        }
        removeValue(index);
    }

    /**
     * Clears all values from the collection.
     *
     * @since 1.0.2
     */
    public void clear() {
        this.keys.clear();
        this.values.clear();
        this.indexMap.clear();
    }

    /**
     * Sorts the items in the list by key.
     *
     * @param order  the sort order ({@code null} not permitted).
     */
    public void sortByKeys(SortOrder order) {
        final int size = this.keys.size();
        final ArrayKeyedValue[] data = new ArrayKeyedValue[size];

        for (int i = 0; i < size; i++) {
            data[i] = new ArrayKeyedValue((Comparable) this.keys.get(i),
                    (ArrayList<Number>) this.values.get(i));
        }

        Comparator comparator = new KeyedValueComparator(
                KeyedValueComparatorType.BY_KEY, order);
        Arrays.sort(data, comparator);
        clear();

        for (int i = 0; i < data.length; i++) {
            final ArrayKeyedValue value = data[i];
            addValue(value.getKey(), value.getValue());
        }
    }

    /**
     * Sorts the items in the list by value.  If the list contains
     * {@code null} values, they will sort to the end of the list,
     * irrespective of the sort order.
     *
     * @param order  the sort order ({@code null} not permitted).
     */
    public void sortByValues(SortOrder order) {
        final int size = this.keys.size();
        final ArrayKeyedValue[] data = new ArrayKeyedValue[size];
        for (int i = 0; i < size; i++) {
            data[i] = new ArrayKeyedValue((Comparable) this.keys.get(i),
                    (ArrayList<Number>) this.values.get(i));
        }

        Comparator comparator = new KeyedValueComparator(
                KeyedValueComparatorType.BY_VALUE, order);
        Arrays.sort(data, comparator);

        clear();
        for (int i = 0; i < data.length; i++) {
            final ArrayKeyedValue value = data[i];
            addValue(value.getKey(), value.getValue());
        }
    }

//    /**
//     * Tests if this object is equal to another.
//     *
//     * @param obj  the object ({@code null} permitted).
//     *
//     * @return A boolean.
//     */
//    @Override
//    public boolean equals(Object obj) {
//        if (obj == this) {
//            return true;
//        }
//
//        if (!(obj instanceof KeyedValues)) {
//            return false;
//        }
//
//        KeyedValues that = (KeyedValues) obj;
//        int count = getItemCount();
//        if (count != that.getItemCount()) {
//            return false;
//        }
//
//        for (int i = 0; i < count; i++) {
//            Comparable k1 = getKey(i);
//            Comparable k2 = that.getKey(i);
//            if (!k1.equals(k2)) {
//                return false;
//            }
//            Number v1 = getValue(i);
//            Number v2 = that.getValue(i);
//            if (v1 == null) {
//                if (v2 != null) {
//                    return false;
//                }
//            }
//            else {
//                if (!v1.equals(v2)) {
//                    return false;
//                }
//            }
//        }
//        return true;
//    }

    /**
     * Returns a hash code.
     *
     * @return A hash code.
     */
    @Override
    public int hashCode() {
        return (this.keys != null ? this.keys.hashCode() : 0);
    }
}