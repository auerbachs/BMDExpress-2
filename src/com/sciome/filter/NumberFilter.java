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

			if (objectValue.doubleValue() < 0)
				System.out.println();
			return objectValue.doubleValue() >= value1.doubleValue()
					&& objectValue.doubleValue() <= value2.doubleValue();
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
