package com.sciome.bmdexpress2.mvp.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sciome.bmdexpress2.mvp.model.info.AnalysisInfo;

public abstract class BMDExpressAnalysisDataSet
{

	// map the header to the column number for quicker access of data points finally
	private Map<String, Integer> headerToIndex;

	public abstract List<String> getColumnHeader();

	public abstract List<Object> getColumnHeader2();

	public abstract AnalysisInfo getAnalysisInfo();

	public abstract String getName();

	public abstract void setName(String name);

	public abstract List<BMDExpressAnalysisRow> getAnalysisRows();

	// lazy load the headerToIndex map.
	public Integer getIndexForKey(String key)
	{
		if (headerToIndex == null)
		{
			headerToIndex = new HashMap<>();
			int i = 0;
			for (String str : getColumnHeader())
				headerToIndex.put(str, i++);
		}

		return headerToIndex.get(key);

	}

	public Object getValueForHeaderAt(ChartKey string, int i)
	{
		try
		{
			return getAnalysisRows().get(i).getRow().get(getIndexForKey(string.getKey()).intValue());
		}
		catch (Exception e)
		{
			// e.printStackTrace();
		}
		return null;
	}

	public Class getHeaderClass(String key)
	{
		if (this.getAnalysisRows().size() == 0)
			return null;
		try
		{
			for (BMDExpressAnalysisRow row : this.getAnalysisRows())
				if (row.getRow().get(getIndexForKey(key)) != null)
					return row.getRow().get(getIndexForKey(key)).getClass();
		}
		catch (Exception e)
		{

		}

		return null;

	}

	public Object getValueForRow(BMDExpressAnalysisRow object, String key) throws Exception
	{
		return object.getRow().get(getIndexForKey(key));
	}

}
