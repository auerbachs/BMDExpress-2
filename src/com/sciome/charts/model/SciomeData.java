package com.sciome.charts.model;

public class SciomeData<X, Y>
{
	private String	name;
	private Object	extraValue;
	private X		xValue;
	private Y		yValue;

	public SciomeData(String n, X x, Y y, Object o)
	{
		name = n;
		xValue = x;
		yValue = y;
		extraValue = o;
	}

	public String getName()
	{
		return name;
	}

	public Object getExtraValue()
	{
		return extraValue;
	}

	public X getXValue()
	{
		return xValue;
	}

	public Y getYValue()
	{
		return yValue;
	}

}
