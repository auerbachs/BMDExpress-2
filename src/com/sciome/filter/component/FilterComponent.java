package com.sciome.filter.component;

import java.lang.reflect.Method;
import java.util.List;

import com.sciome.filter.DataFilter;

import javafx.application.Platform;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/*
 * define a simple ui thing that gives the user the ability
 * to supply values for a filterable key
 */
public abstract class FilterComponent extends VBox
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
	protected DataFilterComponentListener	dataFilterComponentListener;
	protected Class							filterFieldClass;
	protected boolean						filterChangeInProgress;
	protected boolean						fireFilter;
	protected boolean						isUseable	= true;

	public FilterComponent(String key, DataFilterComponentListener dataFilterComponentListener,
			Class filterFieldClass, DataFilter df, Method method)
	{
		super(20);
		filterChangeInProgress = false;
		this.dataFilter = df;
		this.method = method;
		this.dataFilterComponentListener = dataFilterComponentListener;
		this.filterKey = key;
		this.filterFieldClass = filterFieldClass;
		init(key);
		if (df != null)
			initValues(df);

	}

	protected abstract void init(String key);

	protected abstract void initValues(DataFilter df);

	public abstract boolean isFilledOut();

	// a list can be interpreted as different things.
	// for strings, it could be a list of possible values
	// for numerics it would be an inclusive range
	public abstract List<Object> getValues();

	/*
	 * when user types new filter, try to delay for one second before doing the datafilterchanged if the user
	 * types data before filter is fired, then this will wait before firing off filter.
	 */
	protected void doDelayedFilterChange(List<Control> controls)
	{
		fireFilter = true;
		for (Control control : controls)
			if (!control.getStyleClass().contains("textboxfilterchanged"))
				control.getStyleClass().add("textboxfilterchanged");
		if (!filterChangeInProgress)
		{
			filterChangeInProgress = true;
			new Thread(new Runnable() {

				@Override
				public void run()
				{
					while (fireFilter)
					{
						fireFilter = false; // set his global variable to false.
						try
						{
							Thread.sleep(1000);
						}
						catch (InterruptedException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
					Platform.runLater(new Runnable() {
						@Override
						public void run()
						{
							dataFilterComponentListener.dataFilterChanged();
							for (Control control : controls)
								control.getStyleClass().remove("textboxfilterchanged");
							filterChangeInProgress = false;
						}
					});

				}
			}).start();

		}
	}

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

	public boolean getIsUseable()
	{
		return isUseable;
	}
}
