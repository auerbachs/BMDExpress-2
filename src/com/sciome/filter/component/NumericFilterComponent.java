package com.sciome.filter.component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.sciome.filter.DataFilter;
import com.sciome.filter.DataFilterType;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Orientation;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;

/*
 * define a simple ui thing that gives the user the ability
 * to supply values for a filterable key
 */
public class NumericFilterComponent extends FilterComponent
{

	public NumericFilterComponent(String key, Integer row, GridPane grid,
			DataFilterComponentListener dataFilterComponentListener, Class filterFieldClass, DataFilter df,
			Method method)
	{
		super(key, row, grid, dataFilterComponentListener, filterFieldClass, df, method);
	}

	@Override
	protected void initValues(DataFilter df)
	{
		value1.setText(df.getValues().get(0).toString());
		value2.setText(df.getValues().get(1).toString());
		cBox.getSelectionModel().select(df.getDataFilterType());
		if (df.getDataFilterType() == DataFilterType.BETWEEN)
		{
			value2.setVisible(true);
			between.setVisible(true);
		}
	}

	@Override
	protected void init(GridPane grid, String key, Integer row)
	{
		Label keyLabel = new Label(key);
		grid.add(keyLabel, 0, row, 4, 1);

		cBox = new ComboBox<>();
		if (filterFieldClass.equals(String.class))
			cBox.getItems().setAll(DataFilterType.CONTAINS);
		else
			cBox.getItems().setAll(DataFilterType.values());

		grid.add(cBox, 0, row + 1);
		value1 = new TextField();
		value1.setMaxWidth(50.0);
		between = new Label("and");
		between.setFont(new Font(9.0));
		value2 = new TextField();
		value2.setMaxWidth(50.0);

		grid.add(value1, 1, row + 1);
		grid.add(between, 2, row + 1);
		grid.add(value2, 3, row + 1);
		grid.add(new Separator(Orientation.HORIZONTAL), 0, row + 2, 4, 1);

		between.setVisible(false);
		value2.setVisible(false);
		initListener();
	}

	private void initListener()
	{
		cBox.valueProperty().addListener(new ChangeListener<DataFilterType>() {

			@Override
			public void changed(ObservableValue<? extends DataFilterType> observable, DataFilterType oldValue,
					DataFilterType newValue)
			{
				if (cBox.getSelectionModel().getSelectedItem() == DataFilterType.BETWEEN)
				{
					between.setVisible(true);
					value2.setVisible(true);
				}
				else
				{
					between.setVisible(false);
					value2.setVisible(false);
				}

				// only fire the update if a value is present
				if (!value1.getText().equals(""))
				{
					if (!value1.getStyleClass().contains("textboxfilterchanged"))
						value1.getStyleClass().add("textboxfilterchanged");
					if (!value2.getStyleClass().contains("textboxfilterchanged"))
						value2.getStyleClass().add("textboxfilterchanged");
					if (!value2.isVisible())
						doDelayedFilterChange();
					if (value2.isVisible() && !value2.getText().equals(""))
						doDelayedFilterChange();
				}
			}

		});
		value1.textProperty().addListener((observable, oldValue, newValue) ->
		{
			if (!value1.getStyleClass().contains("textboxfilterchanged"))
				value1.getStyleClass().add("textboxfilterchanged");
			doDelayedFilterChange();
		});
		value2.textProperty().addListener((observable, oldValue, newValue) ->
		{
			if (!value2.getStyleClass().contains("textboxfilterchanged"))
				value2.getStyleClass().add("textboxfilterchanged");
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
		if (cBox.getSelectionModel().getSelectedItem() == null)
			return false;

		if (value1.getText() == null || value1.getText().equals(""))
			return false;

		if (cBox.getSelectionModel().getSelectedItem() == DataFilterType.BETWEEN
				&& (value2.getText() == null || value2.getText().equals("")))
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

}
