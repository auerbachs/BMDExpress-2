package com.sciome.filter.component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.sciome.filter.DataFilter;
import com.sciome.filter.DataFilterType;

/*
 * define a simple ui thing that gives the user the ability
 * to supply values for a filterable key
 */
public class IntegerFilterComponent extends NumericFilterComponent
{

	public IntegerFilterComponent(String key, DataFilterComponentListener dataFilterComponentListener,
			Class filterFieldClass, DataFilter df, Method method, FilterComponentContainer container)
	{
		super(key, dataFilterComponentListener, filterFieldClass, df, method, container);
	}

	@Override
	protected void initValues(DataFilter df)
	{

		dataFilterType.setValue(df.getDataFilterType());

		// between filters can be a little more tricky.
		// if min/max of dataset then they may be stored as minvalue or maxvalue of integer
		// no need to set default values in that case
		if (getDataFilterType().equals(DataFilterType.BETWEEN)
				&& (df.getValues().get(0).equals(Integer.MIN_VALUE)
						|| df.getValues().get(1).equals(Integer.MAX_VALUE)))
			return;

		value1.setText(df.getValues().get(0).toString());
		value2.setText(df.getValues().get(1).toString());
	}

	@Override
	public List<Object> getValues()
	{
		List<Object> values = new ArrayList<>();
		try
		{
			if (value1.getText() == null || value1.getText().equals(""))
				values.add(new Integer(0));
			else if (Double.valueOf(value1.getText()) <= hSlider.getMin())
				values.add(-Integer.MIN_VALUE);
			else
				values.add((int) (((Number) Double.valueOf(value1.getText())).doubleValue()));

			if (value2.getText() == null || value2.getText().equals(""))
				values.add(new Integer(0));
			else if (Double.valueOf(value2.getText()) >= hSlider.getMax())
				values.add(Integer.MAX_VALUE);
			else
				values.add((int) (((Number) Double.valueOf(value2.getText())).doubleValue()));
		}
		catch (Exception e)
		{
			// e.printStackTrace();
		}
		return values;
	}

}
