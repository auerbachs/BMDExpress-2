package com.sciome.filter;

import java.util.List;

/*
 * deals with decimal filtration
 */
public class NumberFilter<T> extends DataFilter<Number, T>
{

	public NumberFilter(DataFilterType dataFilterType, Class<T> filterableAnnotatedClass, String key,
			List<Object> values)
	{
		super(dataFilterType, filterableAnnotatedClass, key, values);
	}

	@Override
	public boolean passesFilter(T object)
	{

		try
		{
			Number value1 = (Number) values.get(0);
			Number value2 = (Number) values.get(1);
			Number objectValue = (Number) filterAnnotationExtractor.getFilterableValue(object, key);

			return objectValue.doubleValue() >= value1.doubleValue()
					&& objectValue.doubleValue() <= value2.doubleValue();
		}
		catch (Exception e)
		{
			// e.printStackTrace();
		}

		// pass it by default if it get's here. excpetion was caught. this could mean the filter has null
		// values or is defective
		return true;
	}

}
