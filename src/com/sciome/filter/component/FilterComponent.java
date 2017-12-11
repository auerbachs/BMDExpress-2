package com.sciome.filter.component;

import java.lang.reflect.Method;
import java.util.List;

import com.sciome.filter.DataFilter;
import com.sciome.filter.DataFilterType;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/*
 * define a simple ui thing that gives the user the ability
 * to supply values for a filterable key
 */
public abstract class FilterComponent
{
	protected String						filterKey;
	// this is the method that is invoked on the filterable classes.
	// it is here so we can call it from the DataFilterComponentListener
	// to get suggestions for String selection filters
	protected Method						method;						// method that
																		// returns
	protected DataFilter					dataFilter;

	protected TextField						value1;
	protected TextField						value2;
	protected Label							between;
	protected ComboBox<DataFilterType>		cBox;
	protected DataFilterComponentListener	dataFilterComponentListener;
	protected Class							filterFieldClass;
	protected boolean						filterChangeInProgress;
	protected boolean						fireFilter;

	public FilterComponent(String key, Integer row, GridPane grid,
			DataFilterComponentListener dataFilterComponentListener, Class filterFieldClass, DataFilter df,
			Method method)
	{
		super();
		filterChangeInProgress = false;
		this.dataFilter = df;
		this.method = method;
		this.dataFilterComponentListener = dataFilterComponentListener;
		this.filterKey = key;
		this.filterFieldClass = filterFieldClass;
		init(grid, key, row);
		if (df != null)
			initValues(df);

	}

	protected abstract void init(GridPane grid, String key, Integer row);

	protected abstract void initValues(DataFilter df);

	public abstract boolean isFilledOut();

	// a list can be interpreted as different things.
	// for strings, it could be a list of possible values
	// for numerics it would be an inclusive range
	public abstract List<Object> getValues();

	public String getFilterKey()
	{
		return filterKey;
	}

	public TextField getValue1()
	{
		return value1;
	}

	public TextField getValue2()
	{
		return value2;
	}

	public ComboBox<DataFilterType> getcBox()
	{
		return cBox;
	}

}
