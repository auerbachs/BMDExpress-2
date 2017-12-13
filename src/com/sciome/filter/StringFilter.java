package com.sciome.filter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisDataSet;
import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisRow;

/*
 * deals with String filtration.
 */
public class StringFilter extends DataFilter<String>
{

	public StringFilter(DataFilterType dataFilterType, BMDExpressAnalysisDataSet dataset, String key,
			List<Object> value1)
	{
		super(dataFilterType, dataset, key, value1);
	}

	@Override
	public boolean passesFilter(BMDExpressAnalysisRow object)
	{
		Set<Object> stringSet = new HashSet<>(getValues());
		try
		{

			String objectValue = (filterAnnotationExtractor.getFilterableValue(object, key)).toString()
					.toLowerCase();
			switch (dataFilterType)
			{
				case EQUALS:
					return stringSet.contains(objectValue);
				case CONTAINS:
					for (Object obj : stringSet)
						if (objectValue.contains(obj.toString().toLowerCase()))
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
