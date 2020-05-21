package com.sciome.filter.component;

import java.lang.reflect.Method;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.controlsfx.control.RangeSlider;

import com.sciome.filter.DataFilter;
import com.sciome.filter.DataFilterType;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/*
 * define a simple ui thing that gives the user the ability
 * to supply values for a filterable key
 */
public class NumericFilterComponent extends FilterComponent
{

	private boolean						isInteger;
	private boolean						textEditingSlider	= false;
	protected RangeSlider				hSlider;
	protected TextField					value1;
	protected TextField					value2;

	protected ComboBox<DataFilterType>	dataFilterType;

	public NumericFilterComponent(String key, DataFilterComponentListener dataFilterComponentListener,
			Class filterFieldClass, DataFilter df, Method method, FilterComponentContainer container)
	{
		super(key, dataFilterComponentListener, filterFieldClass, df, method, container);
	}

	@Override
	protected void initValues(DataFilter df)
	{

		// this could mean that the filter doesn't exist for this data set
		if (dataFilterType == null)
			return;
		dataFilterType.setValue(df.getDataFilterType());
		// between filters can be a little more tricky.
		// if min/max of dataset then they may be stored as -infinity or +infinity of integer
		// no need to set default values in that case
		if (getDataFilterType().equals(DataFilterType.BETWEEN)
				&& (df.getValues().get(0).equals(Double.NEGATIVE_INFINITY)
						|| df.getValues().get(1).equals(Double.POSITIVE_INFINITY)))
			return;

		value1.setText(df.getValues().get(0).toString());
		value2.setText(df.getValues().get(1).toString());
	}

	@Override
	protected void init(String key)
	{

		List<Object> range = dataFilterComponentListener.getRangeForKey(key);
		if (range.get(0) == null)
		{
			isUseable = false;
			return; // there is nothing we can do because the data doesn't have values for this
		}
		isInteger = false;

		if (range.get(0) != null && range.get(0) instanceof Integer)
			isInteger = true;
		double min = getMin(range);
		double max = getMax(range);

		try
		{
			value1 = new TextField(formatDecimal(min, RoundingMode.FLOOR));
			value1.setMinWidth(100.0);
			value1.setMaxWidth(100.0);
			value2 = new TextField(formatDecimal(max, RoundingMode.CEILING));
			value2.setMinWidth(100.0);
			value2.setMaxWidth(100.0);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		hSlider = new RangeSlider(min, max, min, max);
		// hSlider.setShowTickMarks(true);
		hSlider.setMinWidth(300);
		hSlider.setMaxWidth(300);
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
					value1.setText(formatDecimal(newValue.doubleValue(), RoundingMode.FLOOR));
			}
		});

		hSlider.highValueProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue,
					Number newValue)
			{
				if (!textEditingSlider)
					value2.setText(formatDecimal(newValue.doubleValue(), RoundingMode.CEILING));

			}
		});

		dataFilterType = new ComboBox<>();
		dataFilterType.getItems().addAll(DataFilterType.values());
		dataFilterType.getItems().remove(DataFilterType.CONTAINS);
		dataFilterType.setValue(DataFilterType.BETWEEN);

		dataFilterType.valueProperty().addListener(new ChangeListener<DataFilterType>() {

			@Override
			public void changed(ObservableValue<? extends DataFilterType> observable, DataFilterType oldValue,
					DataFilterType newValue)
			{
				if (newValue.equals(DataFilterType.BETWEEN))
				{
					value2.setVisible(true);
					hSlider.setVisible(true);
				}
				else
				{
					value2.setVisible(false);
					hSlider.setVisible(false);
				}

				if (!value1.getText().trim().equals(""))
					doDelayedFilterChange(Arrays.asList(value1, value2));

			}
		});

		// hSlider.setShowTickLabels(true);
		// hSlider.setBlockIncrement(value);

		VBox vbox = new VBox(8);
		HBox hbox1 = new HBox(8);

		hbox1.getChildren().addAll(dataFilterType, value1, value2);

		vbox.getChildren().addAll(hbox1, hSlider);
		addFilterComponent(vbox);
		VBox.setMargin(vbox, new Insets(15, 15, 15, 15));

		value2.setVisible(true);
		vbox.setStyle("-fx-border-color: black;" + "-fx-padding: 0 0 30 0;");
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

			doDelayedFilterChange(Arrays.asList(value1, value2));
		});
		value2.textProperty().addListener((observable, oldValue, newValue) ->
		{
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
			doDelayedFilterChange(Arrays.asList(value1, value2));
		});
	}

	@Override
	public boolean isFilledOut()
	{

		if (value1.getText() == null || value1.getText().equals(""))
			return false;

		if (value2.isVisible() && (value2.getText() == null || value2.getText().equals("")))
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
			else if (Double.valueOf(value1.getText()) <= hSlider.getMin())
				values.add(Double.NEGATIVE_INFINITY);
			else
				values.add(Double.valueOf(value1.getText()));

			if (value2.getText() == null || value2.getText().equals(""))
				values.add(0.0);
			else if (Double.valueOf(value2.getText()) >= hSlider.getMax())
				values.add(Double.POSITIVE_INFINITY);
			else
				values.add(Double.valueOf(value2.getText()));
		}
		catch (Exception e)
		{
			// e.printStackTrace();
		}
		return values;
	}

	private String formatDecimal(double value, RoundingMode roundingMode)
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
			DecimalFormat df = new DecimalFormat(pounds + "E0");
			df.setRoundingMode(roundingMode);
			return df.format(value);
		}
		else
		{
			DecimalFormat df = new DecimalFormat("#.####");
			df.setRoundingMode(roundingMode);
			return df.format(value);
		}
	}

	@Override
	public DataFilterType getDataFilterType()
	{
		if (dataFilterType.getValue() == null || dataFilterType.getValue().toString().equals(""))
			return super.getDataFilterType();

		return dataFilterType.getValue();
	}

}
