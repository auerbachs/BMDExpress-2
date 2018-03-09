package com.sciome.filter.component;

import java.lang.reflect.Method;

import com.sciome.filter.DataFilter;

/*
 * 
 */
public class FilterComponentFactory
{
	public static FilterComponent createFilterComponent(String key,
			DataFilterComponentListener dataFilterComponentListener, Class filterFieldClass, DataFilter df,
			Method method, FilterComponentContainer container)
	{
		if (filterFieldClass.equals(Integer.class))
			return new IntegerFilterComponent(key, dataFilterComponentListener, filterFieldClass, df, method,
					container);
		else if (filterFieldClass.equals(Float.class) || filterFieldClass.equals(Double.class)
				|| filterFieldClass.equals(Number.class))
			return new NumericFilterComponent(key, dataFilterComponentListener, filterFieldClass, df, method,
					container);
		else // when in doubt, default to string filter component
			return new StringFilterComponent(key, dataFilterComponentListener, filterFieldClass, df, method,
					container);
	}

}
