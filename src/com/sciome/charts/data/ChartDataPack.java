package com.sciome.charts.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * A list of data that can be charted.  ChartData contains various data points.
 */
public class ChartDataPack
{
	private List<ChartData>					chartData;
	private List<String>					charttableKeys;
	private Map<String, ChartStatistics>	chartStatMap	= new HashMap<>();
	private String							name;

	public ChartDataPack(List<ChartData> cData, List<String> cKeys)
	{
		this.chartData = cData;
		this.charttableKeys = cKeys;
		calculateStatsForPack();

	}

	private void calculateStatsForPack()
	{
		for (String key : charttableKeys)
		{
			List<Double> values = getSortedValueArray(key);
			computeStats(values, key);
		}

	}

	public void recomputeStats()
	{
		calculateStatsForPack();
	}

	private void computeStats(List<Double> values, String key)
	{
		ChartStatistics chartStats = new ChartStatistics();
		chartStats.setMax(0.0);
		chartStats.setMin(0.0);
		chartStats.setMean(0.0);
		chartStats.setMedian(0.0);
		chartStatMap.put(key, chartStats);
		if (values.size() == 0)
			return;

		chartStats.setMax(values.get(values.size() - 1));
		chartStats.setMin(values.get(0));
		chartStats.setMedian(values.get(values.size() / 2));
		double sum = 0.0;
		for (Double value : values)
		{
			sum += value;
		}
		chartStats.setMean(sum / (double) values.size());

	}

	private List<Double> getSortedValueArray(String key)
	{
		List<Double> values = new ArrayList<>();
		for (ChartData data : chartData)
		{
			Map<String, Double> mappedDataPoints = data.getDataPoints();
			if (mappedDataPoints.containsKey(key))
			{
				values.add(mappedDataPoints.get(key));
			}
		}

		Collections.sort(values);
		return values;
	}

	public List<ChartData> getChartData()
	{
		return chartData;
	}

	public void setChartData(List<ChartData> chartData)
	{
		this.chartData = chartData;
	}

	public List<String> getCharttableKeys()
	{
		return charttableKeys;
	}

	public void setCharttableKeys(List<String> charttableKeys)
	{
		this.charttableKeys = charttableKeys;
	}

	public Map<String, ChartStatistics> getChartStatMap()
	{
		return chartStatMap;
	}

	public void setChartStatMap(Map<String, ChartStatistics> chartStatMap)
	{
		this.chartStatMap = chartStatMap;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

}
