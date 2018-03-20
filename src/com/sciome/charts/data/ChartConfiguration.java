package com.sciome.charts.data;

/*
 * chart configuration class.  This stores data that the user sets to view the chart.
 * currently it is only concerned with the ranges of the axes.
 */
public class ChartConfiguration
{

	private Double	minX;
	private Double	maxX;

	private Double	minY;
	private Double	maxY;

	public Double getMinX()
	{
		return minX;
	}

	public void setMinX(Double minX)
	{
		this.minX = minX;
	}

	public Double getMaxX()
	{
		return maxX;
	}

	public void setMaxX(Double maxX)
	{
		this.maxX = maxX;
	}

	public Double getMinY()
	{
		return minY;
	}

	public void setMinY(Double minY)
	{
		this.minY = minY;
	}

	public Double getMaxY()
	{
		return maxY;
	}

	public void setMaxY(Double maxY)
	{
		this.maxY = maxY;
	}

}
