package com.sciome.charts.data;

import java.util.Map;

import com.sciome.bmdexpress2.mvp.model.ChartKey;

/*
 * A Data Row that consists of multipe data points.  The map represents the dataPoints.  
 * The object represents the object that contains methods to grab those datapoints
 */
public class ChartData
{
	private Object					charttableObject;
	private String					dataPointLabel;
	private Map<ChartKey, Double>	dataPoints;

	public Object getCharttableObject()
	{
		return charttableObject;
	}

	public void setCharttableObject(Object charttableObject)
	{
		this.charttableObject = charttableObject;
	}

	public Map<ChartKey, Double> getDataPoints()
	{
		return dataPoints;
	}

	public void setDataPoints(Map<ChartKey, Double> dataPoints)
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
