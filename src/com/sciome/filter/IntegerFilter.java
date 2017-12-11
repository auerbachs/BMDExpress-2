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
			switch (dataFilterType)
			{
				case EQUALS:
					return objectValue.equals(value1);
				case CONTAINS:
					return objectValue.toString().contains(value1.toString());
				case BETWEEN:
					return objectValue >= value1 && objectValue <= value2;
				case LESS_THAN:
					return objectValue < value1;
				case GREATER_THAN:
					return objectValue > value1;
				case LESS_THAN_EQUAL:
					return objectValue <= value1;
				case GREATER_THAN_EQUAL:
					return objectValue >= value1;
				default:
					break;
			}
		}
		catch (Exception e)
		{

		}

		return false;
	}

}
