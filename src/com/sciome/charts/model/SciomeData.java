package com.sciome.charts.model;

/*
 * This is a generic data representation.  it is ment to store the name of a datpoint,
 * the x value, y value and an extra value which can be any object.  
 * The type and use of the extravalue will be handled by the implementing chart class.
 * 
 * it is ultimately used to relate data to the chart nodes.  This will ultimately allow
 * communication channels to be built and allow charts to be more interactive with the user 
 * and other ui charts/components
 */
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
