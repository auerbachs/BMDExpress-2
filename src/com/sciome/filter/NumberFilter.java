package com.sciome.filter;

/*
 * deals with decimal filtration
 */
public class NumberFilter<T> extends DataFilter<Number, T>
{

	public NumberFilter(DataFilterType dataFilterType, Class<T> filterableAnnotatedClass, String key,
			Number value1)
	{
		super(dataFilterType, filterableAnnotatedClass, key, value1);
	}

	public NumberFilter(DataFilterType dataFilterType, Class<T> filterableAnnotatedClass, String key,
			Number value1, Number value2)
	{
		super(dataFilterType, filterableAnnotatedClass, key, value1, value2);
	}

	@Override
	public boolean passesFilter(T object)
	{

		try
		{
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

		}

		return false;
	}

}
