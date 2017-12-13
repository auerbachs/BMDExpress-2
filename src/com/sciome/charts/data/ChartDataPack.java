package com.sciome.charts.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sciome.bmdexpress2.mvp.model.ChartKey;

/*
 * A list of data that can be charted.  ChartData contains various data points.
 */
public class ChartDataPack
{
	private List<ChartData>					chartData;
	private List<ChartKey>					charttableKeys;
	private Map<ChartKey, ChartStatistics>	chartStatMap	= new HashMap<>();
	private String							name;

	public ChartDataPack(List<ChartData> cData, List<ChartKey> cKeys)
	{
		this.chartData = cData;
		this.charttableKeys = cKeys;
		calculateStatsForPack();

	}

	private void calculateStatsForPack()
	{
		for (ChartKey key : charttableKeys)
		{
			List<Double> values = getSortedValueArray(key);
			computeStats(values, key);
		}

	}

	public void recomputeStats()
	{
		calculateStatsForPack();
	}

	private void computeStats(List<Double> values, ChartKey key)
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

	private List<Double> getSortedValueArray(ChartKey key)
	{
		List<Double> values = new ArrayList<>();
		for (ChartData data : chartData)
		{
			Map<ChartKey, Double> mappedDataPoints = data.getDataPoints();

			if (mappedDataPoints.containsKey(key))
			{
				values.add(mappedDataPoints.get(key).doubleValue());
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

	public List<ChartKey> getCharttableKeys()
	{
		return charttableKeys;
	}

	public void setCharttableKeys(List<ChartKey> charttableKeys)
	{
		this.charttableKeys = charttableKeys;
	}

	public Map<ChartKey, ChartStatistics> getChartStatMap()
	{
		return chartStatMap;
	}

	public void setChartStatMap(Map<ChartKey, ChartStatistics> chartStatMap)
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
