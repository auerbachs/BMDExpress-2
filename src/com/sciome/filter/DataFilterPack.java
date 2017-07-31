package com.sciome.filter;

import java.util.List;

/*
 * Data Filter package that contains a list of filters.  It also has method to 
 * see if an object passes the filters
 */
public class DataFilterPack
{

	private String				name;
	private List<DataFilter>	dataFilters;

	public DataFilterPack(String name, List<DataFilter> dataFilters)
	{
		super();
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

	/*
	 * take this object and see if it passes the filter
	 */
	public boolean passesFilter(Object record)
	{
		if (dataFilters.isEmpty())
			return true;
		if (dataFilters == null)
			return false;
		for (DataFilter df : dataFilters)
		{
			if (!df.passesFilter(record))
				return false;
		}

		return true;
	}

}
