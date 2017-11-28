package com.sciome.charts.model;

import java.util.ArrayList;
import java.util.List;

/*
 * generic series representation.  The individual chart implementations will use data in this object
 * to create the implementation specific data representation.
 */
public class SciomeSeries<X, Y>
{
	private String					name;
	private List<SciomeData<X, Y>>	data	= new ArrayList<>();

	public SciomeSeries()
	{
		name = "";
	}

	public SciomeSeries(String n)
	{
		name = n;
	}

	public String getName()
	{
		return name;
	}

	public List<SciomeData<X, Y>> getData()
	{
		return data;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public void setData(List<SciomeData<X, Y>> data)
	{
		this.data = data;
	}

}
