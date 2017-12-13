package com.sciome.charts;

import java.util.List;

import com.sciome.bmdexpress2.mvp.model.ChartKey;
import com.sciome.charts.data.ChartConfiguration;
import com.sciome.charts.data.ChartDataPack;

import javafx.scene.chart.Chart;

public abstract class SciomeHeatMap extends SciomeChartBase<String, String>
{

	public SciomeHeatMap(String title, List<ChartDataPack> chartDataPacks, SciomeChartListener chartListener)
	{
		super(title, chartDataPacks, new ChartKey[] {}, chartListener);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Chart generateChart(ChartKey[] keys, ChartConfiguration chartConfig)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isXAxisDefineable()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean isYAxisDefineable()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void redrawChart()
	{
		// TODO Auto-generated method stub

	}

	/*
	 * fill up the sciome series data structure so implementing classes can use it to create charts
	 */
	@Override
	protected void convertChartDataPacksToSciomeSeries(ChartKey[] keys, List<ChartDataPack> chartPacks)
	{

	}

}
