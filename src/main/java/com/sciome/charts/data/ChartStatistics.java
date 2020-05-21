package com.sciome.charts.data;

/*
 * some basic statistic values that charts can use to set up axis
 */
public class ChartStatistics
{

	private Double	min;
	private Double	max;
	private Double	mean;
	private Double	median;

	public Double getMin()
	{
		return min;
	}

	public void setMin(Double min)
	{
		this.min = min;
	}

	public Double getMax()
	{
		return max;
	}

	public void setMax(Double max)
	{
		this.max = max;
	}

	public Double getMean()
	{
		return mean;
	}

	public void setMean(Double mean)
	{
		this.mean = mean;
	}

	public Double getMedian()
	{
		return median;
	}

	public void setMedian(Double median)
	{
		this.median = median;
	}

}
