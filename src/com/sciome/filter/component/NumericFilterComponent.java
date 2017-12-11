package com.sciome.filter.component;

import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.controlsfx.control.RangeSlider;

import com.sciome.filter.DataFilter;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/*
 * define a simple ui thing that gives the user the ability
 * to supply values for a filterable key
 */
public class NumericFilterComponent extends FilterComponent
{

	private boolean		isInteger;
	private boolean		textEditingSlider	= false;
	private RangeSlider	hSlider;

	public NumericFilterComponent(String key, Integer row, GridPane grid,
			DataFilterComponentListener dataFilterComponentListener, Class filterFieldClass, DataFilter df,
			Method method)
	{
		super(key, row, grid, dataFilterComponentListener, filterFieldClass, df, method);
	}

	@Override
	protected void initValues(DataFilter df)
	{

		textEditingSlider = true;
		try
		{
			value1.setText(formatDecimal(Double.valueOf(df.getValues().get(0).toString()).doubleValue()));
			value2.setText(formatDecimal(Double.valueOf(df.getValues().get(1).toString()).doubleValue()));
		}
		catch (Exception e)
		{

		}
		textEditingSlider = false;
	}

	@Override
	protected void init(GridPane grid, String key, Integer row)
	{

		List<Object> range = dataFilterComponentListener.getRangeForMethod(method);
		if (range.get(0) == null)
		{
			isUseable = false;
			return; // there is nothing we can do because the data doesn't have values for this
		}
		isInteger = false;
		Label keyLabel = new Label(key);
		grid.add(keyLabel, 0, row, 4, 1);

		if (range.get(0) != null && range.get(0) instanceof Integer)
			isInteger = true;
		double min = getMin(range);
		double max = getMax(range);

		try
		{
			value1 = new TextField(formatDecimal(min));
			value1.setMinWidth(75.0);
			value2 = new TextField(formatDecimal(max));
			value2.setMinWidth(75.0);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		hSlider = new RangeSlider(min, max, min, max);
		hSlider.setShowTickMarks(true);
		if (isInteger)
		{
			hSlider.setMajorTickUnit(1.0);
			hSlider.setMinorTickCount(0);
			hSlider.setSnapToTicks(true);

		}

		hSlider.lowValueProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue,
					Number newValue)
			{
				if (!textEditingSlider)
					value1.setText(formatDecimal(newValue.doubleValue()));
			}
		});

		hSlider.highValueProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue,
					Number newValue)
			{
				if (!textEditingSlider)
					value2.setText(formatDecimal(newValue.doubleValue()));

			}
		});

		// hSlider.setShowTickLabels(true);
		// hSlider.setBlockIncrement(value);
		grid.add(value1, 1, row + 1, 2, 1);
		grid.add(value2, 3, row + 1, 2, 1);

		grid.add(hSlider, 1, row + 2, 4, 1);

		Separator sep = new Separator(Orientation.HORIZONTAL);
		grid.add(sep, 0, row + 3, 4, 1);
		GridPane.setMargin(sep, new Insets(0, 0, 10, 0));

		value2.setVisible(true);
		initListener();
	}

	private double getMin(List<Object> range)
	{

		if (range.get(0) instanceof Integer)
			return ((Integer) range.get(0)).doubleValue();
		else
			return ((Number) range.get(0)).doubleValue();
	}

	private double getMax(List<Object> range)
	{

		if (range.get(1) instanceof Integer)
			return ((Integer) range.get(1)).doubleValue();
		else
			return ((Number) range.get(1)).doubleValue();
	}

	private void initListener()
	{

		value1.textProperty().addListener((observable, oldValue, newValue) ->
		{
			if (!value1.getStyleClass().contains("textboxfilterchanged"))
				value1.getStyleClass().add("textboxfilterchanged");

			textEditingSlider = true;
			try
			{
				if (Double.valueOf(value1.getText()) <= hSlider.getHighValue()
						&& Double.valueOf(value1.getText()) >= hSlider.getMin())
				{
					hSlider.setLowValue(Double.valueOf(value1.getText()));
				}
			}
			catch (Exception e)
			{

			}
			textEditingSlider = false;

			doDelayedFilterChange();
		});
		value2.textProperty().addListener((observable, oldValue, newValue) ->
		{
			if (!value2.getStyleClass().contains("textboxfilterchanged"))
				value2.getStyleClass().add("textboxfilterchanged");

			textEditingSlider = true;
			try
			{
				if (Double.valueOf(value2.getText()) >= hSlider.getLowValue()
						&& Double.valueOf(value2.getText()) <= hSlider.getMax())
				{
					hSlider.setHighValue(Double.valueOf(value2.getText()));
				}
			}
			catch (Exception e)
			{

			}
			textEditingSlider = false;
			doDelayedFilterChange();
		});
	}

	/*
	 * when user types new filter, try to delay for one second before doing the datafilterchanged if the user
	 * types data before filter is fired, then this will wait before firing off filter.
	 */
	private void doDelayedFilterChange()
	{
		fireFilter = true;
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
							value1.getStyleClass().remove("textboxfilterchanged");
							value2.getStyleClass().remove("textboxfilterchanged");
							filterChangeInProgress = false;
						}
					});

				}
			}).start();

		}

	}

	@Override
	public boolean isFilledOut()
	{

		if (value1.getText() == null || value1.getText().equals(""))
			return false;

		if ((value2.getText() == null || value2.getText().equals("")))
			return false;
		return true;

	}

	@Override
	public List<Object> getValues()
	{
		List<Object> values = new ArrayList<>();
		try
		{
			if (value1.getText() == null || value1.getText().equals(""))
				values.add("0");
			else
				values.add(Double.valueOf(value1.getText()));

			if (value2.getText() == null || value2.getText().equals(""))
				values.add(0.0);
			else
				values.add(Double.valueOf(value2.getText()));
		}
		catch (Exception e)
		{}
		return values;
	}

	private String formatDecimal(double value)
	{
		if (value == 0)
			return "0";
		else if (isInteger)
			return new DecimalFormat("#").format(value);
		else if (Math.abs(value) < 0.0001)
		{
			String pounds = "#.####";
			double v = Math.abs(value);
			while (v < .0001)
			{
				v *= 10;
				pounds += "#";
			}
			return new DecimalFormat(pounds + "E0").format(value);
		}
		else
			return new DecimalFormat("#.####").format(value);
	}

}
