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
			switch (dataFilterType)
			{
				case EQUALS:
					return objectValue.equals(value1);
				case CONTAINS:
					return objectValue.toString().contains(value1.toString());
				case BETWEEN:
					return objectValue.doubleValue() >= value1.doubleValue()
							&& objectValue.doubleValue() <= value2.doubleValue();
				case LESS_THAN:
					return objectValue.doubleValue() < value1.doubleValue();
				case GREATER_THAN:
					return objectValue.doubleValue() > value1.doubleValue();
				case LESS_THAN_EQUAL:
					return objectValue.doubleValue() <= value1.doubleValue();
				case GREATER_THAN_EQUAL:
					return objectValue.doubleValue() >= value1.doubleValue();
				default:
					break;
			}
		}
		catch (Exception e)
		{
			// e.printStackTrace();
		}

		return false;
	}

}
