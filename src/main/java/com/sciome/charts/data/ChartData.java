package com.sciome.charts.data;

import java.util.List;
import java.util.Map;

import com.sciome.bmdexpress2.mvp.model.ChartKey;

/*
 * A Data Row that consists of multipe data points.  The map represents the dataPoints.  
 * The object represents the object that contains methods to grab those datapoints
 */
public class ChartData
{
	private Object						charttableObject;
	private String						dataPointLabel;
	private Map<ChartKey, Double>		dataPoints;
	private Map<ChartKey, List<Double>>	dataPointLists;

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

	public Map<ChartKey, List<Double>> getDataPointLists()
	{
		return dataPointLists;
	}

	public void setDataPointLists(Map<ChartKey, List<Double>> dataPointList)
	{
		this.dataPointLists = dataPointList;
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
