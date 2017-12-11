package com.sciome.filter.component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.sciome.filter.DataFilter;

import javafx.scene.layout.GridPane;

/*
 * define a simple ui thing that gives the user the ability
 * to supply values for a filterable key
 */
public class IntegerFilterComponent extends NumericFilterComponent
{

	public IntegerFilterComponent(String key, Integer row, GridPane grid,
			DataFilterComponentListener dataFilterComponentListener, Class filterFieldClass, DataFilter df,
			Method method)
	{
		super(key, row, grid, dataFilterComponentListener, filterFieldClass, df, method);
	}

	@Override
	public List<Object> getValues()
	{
		List<Object> values = new ArrayList<>();
		try
		{
			if (value1.getText() == null || value1.getText().equals(""))
				values.add(new Integer(0));
			else
				values.add(Integer.valueOf(value1.getText()));

			if (value2.getText() == null || value2.getText().equals(""))
				values.add(new Integer(0));
			else
				values.add(Integer.valueOf(value2.getText()));
		}
		catch (Exception e)
		{}
		return values;
	}

}
