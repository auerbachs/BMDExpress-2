package com.sciome.filter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/*
 * deals with String filtration.
 */
public class StringFilter<T> extends DataFilter<String, T>
{

	public StringFilter(DataFilterType dataFilterType, Class<T> filterableAnnotatedClass, String key,
			List<Object> value1)
	{
		super(dataFilterType, filterableAnnotatedClass, key, value1);
	}

	@Override
	public boolean passesFilter(T object)
	{
		Set<Object> stringSet = new HashSet<>(getValues());
		try
		{
			String objectValue = ((String) filterAnnotationExtractor.getFilterableValue(object, key))
					.toLowerCase();
			switch (dataFilterType)
			{
				case EQUALS:
					return stringSet.contains(objectValue);
				case CONTAINS:
					for (Object obj : stringSet)
						if (objectValue.contains(((String) obj).toLowerCase()))
							return true;
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
