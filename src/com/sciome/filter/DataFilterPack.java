package com.sciome.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisRow;
import com.sciome.bmdexpress2.mvp.model.IMarkable;

/*
 * Data Filter package that contains a list of filters.  It also has method to 
 * see if an object passes the filters
 */
public class DataFilterPack
{

	private String				name;
	private List<DataFilter>	dataFilters;
	private Set<String>			markedData;

	public DataFilterPack()
	{

	}

	public DataFilterPack(String name, List<DataFilter> dataFilters, Set<String> markedData)
	{
		super();
		this.markedData = markedData;
		this.name = name;
		this.dataFilters = dataFilters;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public List<DataFilter> getDataFilters()
	{
		return dataFilters;
	}

	public void setDataFilters(List<DataFilter> dataFilters)
	{
		this.dataFilters = dataFilters;
	}

	public void setMarkedData(Set<String> marked)
	{
		this.markedData = marked;
	}

	/*
	 * take this object and see if it passes the filter
	 */
	public boolean passesFilter(BMDExpressAnalysisRow record)
	{
		if (dataFilters.isEmpty())
			return true;
		if (dataFilters == null)
			return false;
		for (DataFilter df : dataFilters)
		{
			// it is marked then do not filter it out
			if (isMarked(record))
				return true;
			if (!df.passesFilter(record))
				return false;
		}

		return true;
	}

	private boolean isMarked(BMDExpressAnalysisRow record)
	{
		if (this.markedData.isEmpty())
			return false;
		if (record.getObject() instanceof IMarkable)
		{
			Set<String> markableKeys = ((IMarkable) record.getObject()).getMarkableKeys();
			for (String key : markableKeys)
				if (this.markedData.contains(key))
					return true;
		}
		return false;
	}

	public DataFilterPack copy()
	{
		List<DataFilter> dfCopies = new ArrayList<>();
		DataFilterPack dp = new DataFilterPack();
		dp.setName(this.getName());
		if (this.getDataFilters() != null)
			for (DataFilter df : this.getDataFilters())
				dfCopies.add(df.copy());

		dp.setDataFilters(dfCopies);

		return dp;
	}

	public Set<String> getMarkedData()
	{
		return markedData;
	}

}
