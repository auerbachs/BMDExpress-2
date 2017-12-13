package com.sciome.filter.component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisDataSet;
import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisRow;

public class FilterDataExtractor
{

	private BMDExpressAnalysisDataSet filterableDataSet;

	public FilterDataExtractor(BMDExpressAnalysisDataSet filterableDataSet)
	{
		this.filterableDataSet = filterableDataSet;
	}

	public Object getFilterableValue(BMDExpressAnalysisRow object, String key)
	{
		try
		{
			return filterableDataSet.getValueForRow(object, key);
		}
		catch (Exception e)
		{

		}
		return null;
	}

	public Class getReturnType(String key)
	{
		return filterableDataSet.getHeaderClass(key);
	}

	public Method getMethod(String key)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public List<String> getKeys()
	{
		return new ArrayList<>(filterableDataSet.getColumnHeader());
	}

}
