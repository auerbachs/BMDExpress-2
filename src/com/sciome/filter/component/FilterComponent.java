package com.sciome.filter.component;

import java.lang.reflect.Method;
import java.util.List;

import com.sciome.filter.DataFilter;
import com.sciome.filter.DataFilterType;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
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

	protected DataFilterComponentListener	dataFilterComponentListener;
	protected Class							filterFieldClass;
	protected boolean						filterChangeInProgress;
	protected boolean						fireFilter;
	protected boolean						isUseable	= true;
	protected Label							keyLabel;
	private FilterComponentContainer		container;

	public FilterComponent(String key, DataFilterComponentListener dataFilterComponentListener,
			Class filterFieldClass, DataFilter df, Method method, FilterComponentContainer container)
	{
		super(20);
		this.container = container;
		filterChangeInProgress = false;
		this.dataFilter = df;
		this.method = method;
		this.dataFilterComponentListener = dataFilterComponentListener;
		this.filterKey = key;
		this.filterFieldClass = filterFieldClass;
		keyLabel = new Label(key);
		this.getChildren().add(keyLabel);

		init(key);
		if (df != null)
			initValues(df);

	}

	protected void addFilterComponent(VBox node)
	{
		Button closeButton = new Button("X");
		HBox hbox = new HBox();
		HBox h1 = new HBox();
		HBox h2 = new HBox();
		h1.setAlignment(Pos.CENTER_LEFT);
		h2.setAlignment(Pos.CENTER_RIGHT);
		h1.getChildren().add(keyLabel);
		h2.getChildren().add(closeButton);

		hbox.getChildren().addAll(h1, h2);
		closeButton.setAlignment(Pos.CENTER_RIGHT);
		keyLabel.setAlignment(Pos.CENTER_LEFT);
		HBox.setHgrow(hbox, Priority.ALWAYS);
		HBox.setHgrow(h2, Priority.ALWAYS);

		this.setFillWidth(true);

		closeButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event)
			{
				container.close(FilterComponent.this);

			}
		});
		node.getChildren().add(0, hbox);
		this.getChildren().add(node);
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

		container.filterChanged(controls);

	}

	public String getFilterKey()
	{
		return filterKey;
	}

	public boolean getIsUseable()
	{
		return isUseable;
	}

	public DataFilterType getDataFilterType()
	{
		return DataFilterType.BETWEEN;
	}
}
