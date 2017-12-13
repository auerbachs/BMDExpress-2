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

			return objectValue >= value1 && objectValue <= value2;
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
