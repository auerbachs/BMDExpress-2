package com.sciome.charts.jfree.violin;

import java.util.ArrayList;
import java.util.List;

import org.jfree.chart.JFreeChart;
import org.jfree.data.category.SlidingCategoryDataset;

import com.sciome.bmdexpress2.mvp.model.ChartKey;
import com.sciome.charts.SciomeChartListener;
import com.sciome.charts.data.ChartData;
import com.sciome.charts.data.ChartDataPack;
import com.sciome.charts.model.SciomeData;
import com.sciome.charts.model.SciomeSeries;

public class SciomeViolinPlotDatasetJFree extends SciomeViolinPlot
{
	public SciomeViolinPlotDatasetJFree(String title, List<ChartDataPack> chartDataPacks, ChartKey key,
			SciomeChartListener chartListener)
	{
		super(title, chartDataPacks, key, true, false, chartListener);
	}

	@Override
	protected void convertChartDataPacksToSciomeSeries(ChartKey[] keys, List<ChartDataPack> chartPacks)
	{
		ChartKey key = keys[0];

		List<SciomeSeries<String, List<Double>>> seriesData = new ArrayList<>();
		SciomeSeries<String, List<Double>> series = new SciomeSeries<>("");
		for (ChartDataPack chartDataPack : getChartDataPacks())
		{
			List<Double> valuesForViolin = new ArrayList<Double>();
			for (ChartData chartData : chartDataPack.getChartData())
			{
				Double dataPoint = chartData.getDataPoints().get(key);

				if (dataPoint == null)
					continue;

				valuesForViolin.add(dataPoint);
			}
			//If there are no data points for a violin, move on to the next one
			if(valuesForViolin.size() == 0)
				continue;
			
			SciomeData<String, List<Double>> xyData = new SciomeData(chartDataPack.getName(),
					chartDataPack.getName(), valuesForViolin, chartDataPack.getChartData().get(0).getCharttableObject());
			series.getData().add(xyData);
			seriesData.add(series);
		}

		setSeriesData(seriesData);
	}
}