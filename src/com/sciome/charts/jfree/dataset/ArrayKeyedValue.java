package com.sciome.charts.jfree.dataset;

import java.util.ArrayList;

import org.jfree.chart.util.Args;
import org.jfree.data.DefaultKeyedValue;

public class ArrayKeyedValue {
	/** For serialization. */
    private static final long serialVersionUID = -7388924517460437712L;

    /** The key. */
    private Comparable key;

    /** The value. */
    private ArrayList<Number> value;

    /**
     * Creates a new (key, value) item.
     *
     * @param key  the key (should be immutable, {@code null} not
     *         permitted).
     * @param value  the value ({@code null} permitted).
     */
    public ArrayKeyedValue(Comparable key, ArrayList<Number> value) {
        Args.nullNotPermitted(key, "key");
        this.key = key;
        this.value = value;
    }

    /**
     * Returns the key.
     *
     * @return The key (never {@code null}).
     */
    public Comparable getKey() {
        return this.key;
    }

    /**
     * Returns the value.
     *
     * @return The value (possibly {@code null}).
     */
    public ArrayList<Number> getValue() {
        return this.value;
    }

    /**
     * Sets the value.
     *
     * @param value  the value ({@code null} permitted).
     */
    public synchronized void setValue(ArrayList<Number> value) {
        this.value = value;
    }

    /**
     * Tests this key-value pair for equality with an arbitrary object.
     *
     * @param obj  the object ({@code null} permitted).
     *
     * @return A boolean.
     */
//    public boolean equals(Object obj) {
//        if (obj == this) {
//            return true;
//        }
//        if (!(obj instanceof DefaultKeyedValue)) {
//            return false;
//        }
//        DefaultKeyedValue that = (DefaultKeyedValue) obj;
//
//        if (!this.key.equals(that.key)) {
//            return false;
//        }
//        if (this.value != null
//                ? !this.value.equals(that.value) : that.value != null) {
//            return false;
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
        int result;
        result = (this.key != null ? this.key.hashCode() : 0);
        result = 29 * result + (this.value != null ? this.value.hashCode() : 0);
        return result;
    }

    /**
     * Returns a clone.  It is assumed that both the key and value are
     * immutable objects, so only the references are cloned, not the objects
     * themselves.
     *
     * @return A clone.
     *
     * @throws CloneNotSupportedException Not thrown by this class, but
     *         subclasses (if any) might.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        return (DefaultKeyedValue) super.clone();
    }

    /**
     * Returns a string representing this instance, primarily useful for
     * debugging.
     *
     * @return A string.
     */
    @Override
    public String toString() {
        return "(" + this.key.toString() + ", " + this.value.toString() + ")";
    }

}
