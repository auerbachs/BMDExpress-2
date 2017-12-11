package com.sciome.filter;

import java.util.List;

/*
 * deals with integer comparison for data filtration
 */
public class IntegerFilter<T> extends DataFilter<Integer, T>
{

	public IntegerFilter(DataFilterType dataFilterType, Class<T> filterableAnnotatedClass, String key,
			List<Object> value1)
	{
		super(dataFilterType, filterableAnnotatedClass, key, value1);
	}

	@Override
	public boolean passesFilter(T object)
	{

		try
		{
			Integer value1 = (Integer) values.get(0);
			Integer value2 = (Integer) values.get(1);
			Integer objectValue = (Integer) filterAnnotationExtractor.getFilterableValue(object, key);

			return objectValue >= value1 && objectValue <= value2;
		}
		catch (Exception e)
		{
			// e.printStackTrace();
		}

		// pass it by default if it get's here. excpetion was caught. this could mean the filter has null
		// values or is defective
		return false;
	}

}
