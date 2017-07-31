package com.sciome.charts.data;

import java.util.Map;

/*
 * A Data Row that consists of multipe data points.  The map represents the dataPoints.  
 * The object represents the object that contains methods to grab those datapoints
 */
public class ChartData<T>
{
	private T					charttableObject;
	private String				dataPointLabel;
	private Map<String, Number>	dataPoints;

	public T getCharttableObject()
	{
		return charttableObject;
	}

	public void setCharttableObject(T charttableObject)
	{
		this.charttableObject = charttableObject;
	}

	public Map<String, Number> getDataPoints()
	{
		return dataPoints;
	}

	public void setDataPoints(Map<String, Number> dataPoints)
	{
		this.dataPoints = dataPoints;
	}

	public String getDataPointLabel()
	{
		return dataPointLabel;
	}

	public void setDataPointLabel(String dataPointLabel)
	{
		this.dataPointLabel = dataPointLabel;
	}

}
