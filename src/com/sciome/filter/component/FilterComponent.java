package com.sciome.filter.component;

import com.sciome.filter.DataFilter;
import com.sciome.filter.DataFilterType;

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
public class FilterComponent
{
	private String						filterKey;
	private TextField					value1;
	private TextField					value2;
	private Label						between;
	private ComboBox<DataFilterType>	cBox;
	private DataFilterComponentListener	dataFilterComponentListener;
	private Class						filterFieldClass;

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

	public FilterComponent(String key, Integer row, GridPane grid,
			DataFilterComponentListener dataFilterComponentListener, Class filterFieldClass, DataFilter df)
	{
		super();
		this.dataFilterComponentListener = dataFilterComponentListener;
		this.filterKey = key;
		this.filterFieldClass = filterFieldClass;
		init(grid, key, row);

		initValues(df);
		initListener();
	}

	private void initValues(DataFilter df)
	{
		value1.setText(df.getValue1().toString());
		value2.setText(df.getValue2().toString());
		cBox.getSelectionModel().select(df.getDataFilterType());
		if (df.getDataFilterType() == DataFilterType.BETWEEN)
		{
			value2.setVisible(true);
			between.setVisible(true);
		}
	}

	public FilterComponent(String key, Integer row, GridPane grid,
			DataFilterComponentListener dataFilterComponentListener, Class filterFieldClass)
	{

		super();
		this.dataFilterComponentListener = dataFilterComponentListener;
		this.filterKey = key;
		this.filterFieldClass = filterFieldClass;
		init(grid, key, row);

		initListener();

	}

	private void init(GridPane grid, String key, Integer row)
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
				dataFilterComponentListener.dataFilterChanged();
			}

		});
		value1.textProperty().addListener((observable, oldValue, newValue) ->
		{
			dataFilterComponentListener.dataFilterChanged();
		});
		value2.textProperty().addListener((observable, oldValue, newValue) ->
		{
			dataFilterComponentListener.dataFilterChanged();
		});
	}

	public String getValueOne()
	{
		if (value1.getText() == null || value1.getText().equals(""))
			return "0";

		return value1.getText();
	}

	public String getValueTwo()
	{
		if (value2.getText() == null || value2.getText().equals(""))
			return "0";

		return value2.getText();
	}

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
}
