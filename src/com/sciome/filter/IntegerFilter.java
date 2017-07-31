package com.sciome.filter;

/*
 * deals with integer comparison for data filtration
 */
public class IntegerFilter<T> extends DataFilter<Integer, T>
{

	public IntegerFilter(DataFilterType dataFilterType, Class<T> filterableAnnotatedClass, String key,
			Integer value1)
	{
		super(dataFilterType, filterableAnnotatedClass, key, value1);
	}

	public IntegerFilter(DataFilterType dataFilterType, Class<T> filterableAnnotatedClass, String key,
			Integer value1, Integer value2)
	{
		super(dataFilterType, filterableAnnotatedClass, key, value1, value2);
	}

	@Override
	public boolean passesFilter(T object)
	{

		try
		{
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
