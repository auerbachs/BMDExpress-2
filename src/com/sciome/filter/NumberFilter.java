package com.sciome.filter;

import java.util.List;

import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisDataSet;
import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisRow;

/*
 * deals with decimal filtration
 */
public class NumberFilter extends DataFilter<Number>
{

	public NumberFilter(DataFilterType dataFilterType, BMDExpressAnalysisDataSet dataset, String key,
			List<Object> values)
	{
		super(dataFilterType, dataset, key, values);
	}

	@Override
	public boolean passesFilter(BMDExpressAnalysisRow object)
	{

		try
		{
			Number value1 = (Number) values.get(0);
			Number value2 = (Number) values.get(1);
			Number objectValue = (Number) filterAnnotationExtractor.getFilterableValue(object, key);

			if (dataFilterType.equals(DataFilterType.BETWEEN))
				return objectValue.doubleValue() >= value1.doubleValue()
						&& objectValue.doubleValue() <= value2.doubleValue();
			else if (dataFilterType.equals(DataFilterType.EQUALS))
				return objectValue.equals(value1);
			else if (dataFilterType.equals(DataFilterType.GREATER_THAN))
				return objectValue.doubleValue() > value1.doubleValue();
			else if (dataFilterType.equals(DataFilterType.GREATER_THAN_EQUAL))
				return objectValue.doubleValue() >= value1.doubleValue();
			else if (dataFilterType.equals(DataFilterType.LESS_THAN))
				return objectValue.doubleValue() < value1.doubleValue();
			else if (dataFilterType.equals(DataFilterType.LESS_THAN_EQUAL))
				return objectValue.doubleValue() <= value1.doubleValue();
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
