package com.sciome.charts.jfree.dataset;

import java.util.ArrayList;
import java.util.List;

import org.jfree.data.UnknownKeyException;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.AbstractDataset;
import org.jfree.data.general.DatasetChangeEvent;

public class RangePlotDataset extends AbstractDataset implements CategoryDataset {

	private ArrayKeyedValues2D		data;
	
	public RangePlotDataset() {
		this.data = new ArrayKeyedValues2D();
	}
	
	/**
     * Returns the number of rows in the table.
     *
     * @return The row count.
     *
     * @see #getColumnCount()
     */
    @Override
    public int getRowCount() {
        return this.data.getRowCount();
    }

    /**
     * Returns the number of columns in the table.
     *
     * @return The column count.
     *
     * @see #getRowCount()
     */
    @Override
    public int getColumnCount() {
        return this.data.getColumnCount();
    }

    /**
     * Returns a value from the table.
     *
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     *
     * @return The value (possibly <code>null</code>).
     *
     * @see #addValue(Number, Comparable, Comparable)
     * @see #removeValue(Comparable, Comparable)
     */
    @Override
    public Number getValue(int row, int column) {
        return this.data.getValue(row, column).get(2);
    }

    public ArrayList<Number> getActualValue(int row, int column) {
    	return this.data.getValue(row, column);
    }
    
    /**
     * Returns the key for the specified row.
     *
     * @param row  the row index (zero-based).
     *
     * @return The row key.
     *
     * @see #getRowIndex(Comparable)
     * @see #getRowKeys()
     * @see #getColumnKey(int)
     */
    @Override
    public Comparable getRowKey(int row) {
        return this.data.getRowKey(row);
    }

    /**
     * Returns the row index for a given key.
     *
     * @param key  the row key (<code>null</code> not permitted).
     *
     * @return The row index.
     *
     * @see #getRowKey(int)
     */
    @Override
    public int getRowIndex(Comparable key) {
        // defer null argument check
        return this.data.getRowIndex(key);
    }

    /**
     * Returns the row keys.
     *
     * @return The keys.
     *
     * @see #getRowKey(int)
     */
    @Override
    public List getRowKeys() {
        return this.data.getRowKeys();
    }

    /**
     * Returns a column key.
     *
     * @param column  the column index (zero-based).
     *
     * @return The column key.
     *
     * @see #getColumnIndex(Comparable)
     */
    @Override
    public Comparable getColumnKey(int column) {
        return this.data.getColumnKey(column);
    }

    /**
     * Returns the column index for a given key.
     *
     * @param key  the column key (<code>null</code> not permitted).
     *
     * @return The column index.
     *
     * @see #getColumnKey(int)
     */
    @Override
    public int getColumnIndex(Comparable key) {
        // defer null argument check
        return this.data.getColumnIndex(key);
    }

    /**
     * Returns the column keys.
     *
     * @return The keys.
     *
     * @see #getColumnKey(int)
     */
    @Override
    public List getColumnKeys() {
        return this.data.getColumnKeys();
    }

    /**
     * Returns the value for a pair of keys.
     *
     * @param rowKey  the row key (<code>null</code> not permitted).
     * @param columnKey  the column key (<code>null</code> not permitted).
     *
     * @return The value (possibly <code>null</code>).
     *
     * @throws UnknownKeyException if either key is not defined in the dataset.
     *
     * @see #addValue(Number, Comparable, Comparable)
     */
    @Override
    public Number getValue(Comparable rowKey, Comparable columnKey) {
        return 0;
    }

    /**
     * Adds a value to the table.  Performs the same function as setValue().
     *
     * @param value  the value.
     * @param rowKey  the row key.
     * @param columnKey  the column key.
     *
     * @see #getValue(Comparable, Comparable)
     * @see #removeValue(Comparable, Comparable)
     */
    public void addValue(Number value, Comparable rowKey,
                         Comparable columnKey) {
//        this.data.addValue(value, rowKey, columnKey);
        fireDatasetChanged();
    }
    
    public void addActualValue(ArrayList<Number> value, Comparable rowKey, Comparable columnKey) {
        this.data.addValue(value, rowKey, columnKey);
        fireDatasetChanged();
    }

    /**
     * Adds a value to the table.
     *
     * @param value  the value.
     * @param rowKey  the row key.
     * @param columnKey  the column key.
     *
     * @see #getValue(Comparable, Comparable)
     */
    public void addValue(double value, Comparable rowKey,
                         Comparable columnKey) {
        addValue(new Double(value), rowKey, columnKey);
    }
    
    public void addValue(double[] values, Comparable rowKey, Comparable columnKey) {
    	
    }

    /**
     * Adds or updates a value in the table and sends a
     * {@link DatasetChangeEvent} to all registered listeners.
     *
     * @param value  the value (<code>null</code> permitted).
     * @param rowKey  the row key (<code>null</code> not permitted).
     * @param columnKey  the column key (<code>null</code> not permitted).
     *
     * @see #getValue(Comparable, Comparable)
     */
    public void setValue(Number value, Comparable rowKey,
                         Comparable columnKey) {
//        this.data.setValue(value, rowKey, columnKey);
        fireDatasetChanged();
    }

    /**
     * Adds or updates a value in the table and sends a
     * {@link DatasetChangeEvent} to all registered listeners.
     *
     * @param value  the value.
     * @param rowKey  the row key (<code>null</code> not permitted).
     * @param columnKey  the column key (<code>null</code> not permitted).
     *
     * @see #getValue(Comparable, Comparable)
     */
    public void setValue(double value, Comparable rowKey,
                         Comparable columnKey) {
        setValue(new Double(value), rowKey, columnKey);
    }

    /**
     * Adds the specified value to an existing value in the dataset (if the
     * existing value is <code>null</code>, it is treated as if it were 0.0).
     *
     * @param value  the value.
     * @param rowKey  the row key (<code>null</code> not permitted).
     * @param columnKey  the column key (<code>null</code> not permitted).
     *
     * @throws UnknownKeyException if either key is not defined in the dataset.
     */
    public void incrementValue(double value,
                               Comparable rowKey,
                               Comparable columnKey) {
        double existing = 0.0;
        Number n = getValue(rowKey, columnKey);
        if (n != null) {
            existing = n.doubleValue();
        }
        setValue(existing + value, rowKey, columnKey);
    }

    /**
     * Removes a value from the dataset and sends a {@link DatasetChangeEvent}
     * to all registered listeners.
     *
     * @param rowKey  the row key.
     * @param columnKey  the column key.
     *
     * @see #addValue(Number, Comparable, Comparable)
     */
    public void removeValue(Comparable rowKey, Comparable columnKey) {
        this.data.removeValue(rowKey, columnKey);
        fireDatasetChanged();
    }

    /**
     * Removes a row from the dataset and sends a {@link DatasetChangeEvent}
     * to all registered listeners.
     *
     * @param rowIndex  the row index.
     *
     * @see #removeColumn(int)
     */
    public void removeRow(int rowIndex) {
        this.data.removeRow(rowIndex);
        fireDatasetChanged();
    }

    /**
     * Removes a row from the dataset and sends a {@link DatasetChangeEvent}
     * to all registered listeners.
     *
     * @param rowKey  the row key.
     *
     * @see #removeColumn(Comparable)
     */
    public void removeRow(Comparable rowKey) {
        this.data.removeRow(rowKey);
        fireDatasetChanged();
    }

    /**
     * Removes a column from the dataset and sends a {@link DatasetChangeEvent}
     * to all registered listeners.
     *
     * @param columnIndex  the column index.
     *
     * @see #removeRow(int)
     */
    public void removeColumn(int columnIndex) {
        this.data.removeColumn(columnIndex);
        fireDatasetChanged();
    }

    /**
     * Removes a column from the dataset and sends a {@link DatasetChangeEvent}
     * to all registered listeners.
     *
     * @param columnKey  the column key (<code>null</code> not permitted).
     *
     * @see #removeRow(Comparable)
     *
     * @throws UnknownKeyException if <code>columnKey</code> is not defined
     *         in the dataset.
     */
    public void removeColumn(Comparable columnKey) {
        this.data.removeColumn(columnKey);
        fireDatasetChanged();
    }

    /**
     * Clears all data from the dataset and sends a {@link DatasetChangeEvent}
     * to all registered listeners.
     */
    public void clear() {
        this.data.clear();
        fireDatasetChanged();
    }

    /**
     * Tests this dataset for equality with an arbitrary object.
     *
     * @param obj  the object (<code>null</code> permitted).
     *
     * @return A boolean.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof CategoryDataset)) {
            return false;
        }
        CategoryDataset that = (CategoryDataset) obj;
        if (!getRowKeys().equals(that.getRowKeys())) {
            return false;
        }
        if (!getColumnKeys().equals(that.getColumnKeys())) {
            return false;
        }
        int rowCount = getRowCount();
        int colCount = getColumnCount();
        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < colCount; c++) {
                Number v1 = getValue(r, c);
                Number v2 = that.getValue(r, c);
                if (v1 == null) {
                    if (v2 != null) {
                        return false;
                    }
                }
                else if (!v1.equals(v2)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Returns a hash code for the dataset.
     *
     * @return A hash code.
     */
    @Override
    public int hashCode() {
        return this.data.hashCode();
    }
}
