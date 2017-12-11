package com.sciome.filter.component;

import java.lang.reflect.Method;

import com.sciome.filter.DataFilter;

import javafx.scene.layout.GridPane;

/*
 * 
 */
public class FilterComponentFactory
{
	public static FilterComponent createFilterComponent(String key, Integer row, GridPane grid,
			DataFilterComponentListener dataFilterComponentListener, Class filterFieldClass, DataFilter df,
			Method method)
	{
		if (filterFieldClass.equals(String.class))
			return new StringFilterComponent(key, row, grid, dataFilterComponentListener, filterFieldClass,
					df, method);
		else if (filterFieldClass.equals(Integer.class))
			return new IntegerFilterComponent(key, row, grid, dataFilterComponentListener, filterFieldClass,
					df, method);
		else
			return new NumericFilterComponent(key, row, grid, dataFilterComponentListener, filterFieldClass,
					df, method);
	}

}
