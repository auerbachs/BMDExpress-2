package com.sciome.charts.jfree.violin;

import java.util.ArrayList;
import java.util.List;

import com.sciome.bmdexpress2.mvp.model.ChartKey;
import com.sciome.charts.SciomeChartListener;
import com.sciome.charts.data.ChartData;
import com.sciome.charts.data.ChartDataPack;
import com.sciome.charts.model.SciomeData;
import com.sciome.charts.model.SciomeSeries;

public class SciomeViolinPlotJFree extends SciomeViolinPlot
{

	public SciomeViolinPlotJFree(String title, List<ChartDataPack> chartDataPacks, ChartKey key,
			SciomeChartListener chartListener)
	{
		super(title, chartDataPacks, key , true, false, chartListener);
	}


	@Override
	protected void convertChartDataPacksToSciomeSeries(ChartKey[] keys, List<ChartDataPack> chartPacks)
	{
		ChartKey key = keys[0];

		List<SciomeSeries<String, List<Double>>> seriesData = new ArrayList<>();
		for (ChartDataPack chartDataPack : getChartDataPacks())
		{
			SciomeSeries<String, List<Double>> series = new SciomeSeries<>(chartDataPack.getName());

			for (ChartData chartData : chartDataPack.getChartData())
			{
				List<Double> dataPoint = chartData.getDataPointLists().get(key);

				if (dataPoint == null)
					continue;

				SciomeData<String, List<Double>> xyData = new SciomeData(chartData.getDataPointLabel(),
						chartData.getDataPointLabel(), dataPoint, chartData.getCharttableObject());

				series.getData().add(xyData);
			}
			seriesData.add(series);
		}

		setSeriesData(seriesData);
	}
}