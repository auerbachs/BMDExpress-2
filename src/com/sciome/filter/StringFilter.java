package com.sciome.filter;

/*
 * deals with String filtration.
 */
public class StringFilter<T> extends DataFilter<String, T>
{

	public StringFilter(DataFilterType dataFilterType, Class<T> filterableAnnotatedClass, String key,
			String value1)
	{
		super(dataFilterType, filterableAnnotatedClass, key, value1);
	}

	public StringFilter(DataFilterType dataFilterType, Class<T> filterableAnnotatedClass, String key,
			String value1, String value2)
	{
		super(dataFilterType, filterableAnnotatedClass, key, value1, value2);
	}

	@Override
	public boolean passesFilter(T object)
	{
		try
		{
			String objectValue = (String) filterAnnotationExtractor.getFilterableValue(object, key);
			switch (dataFilterType)
			{
				case EQUALS:
					return objectValue.equalsIgnoreCase(value1);
				case CONTAINS:
					return objectValue.toLowerCase().contains(value1.toLowerCase());
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
