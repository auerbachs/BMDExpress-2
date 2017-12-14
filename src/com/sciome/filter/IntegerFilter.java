package com.sciome.filter;

import java.util.List;

import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisDataSet;
import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisRow;

/*
 * deals with integer comparison for data filtration
 */
public class IntegerFilter extends DataFilter<Integer>
{

	public IntegerFilter(DataFilterType dataFilterType, BMDExpressAnalysisDataSet dataset, String key,
			List<Object> value1)
	{
		super(dataFilterType, dataset, key, value1);
	}

	@Override
	public boolean passesFilter(BMDExpressAnalysisRow object)
	{

		try
		{
			Integer value1 = (Integer) values.get(0);
			Integer value2 = (Integer) values.get(1);
			Integer objectValue = (Integer) filterAnnotationExtractor.getFilterableValue(object, key);

			if (dataFilterType.equals(DataFilterType.BETWEEN))
				return objectValue >= value1 && objectValue <= value2;
			else if (dataFilterType.equals(DataFilterType.EQUALS))
				return objectValue.equals(value1);
			else if (dataFilterType.equals(DataFilterType.GREATER_THAN))
				return objectValue > value1;
			else if (dataFilterType.equals(DataFilterType.GREATER_THAN_EQUAL))
				return objectValue >= value1;
			else if (dataFilterType.equals(DataFilterType.LESS_THAN))
				return objectValue < value1;
			else if (dataFilterType.equals(DataFilterType.LESS_THAN_EQUAL))
				return objectValue <= value1;

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
