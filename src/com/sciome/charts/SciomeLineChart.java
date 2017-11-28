package com.sciome.charts;

import java.util.List;

import com.sciome.charts.data.ChartConfiguration;
import com.sciome.charts.data.ChartDataPack;

import javafx.scene.chart.Chart;

/*
 * 
 */
public abstract class SciomeLineChart extends SciomeChartBase<Number, Number>
{

	public SciomeLineChart(String title, List<ChartDataPack> chartDataPacks,
			SciomeChartListener chartListener)
	{
		super(title, chartDataPacks, new String[] {}, chartListener);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Chart generateChart(String[] keys, ChartConfiguration chartConfig)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isXAxisDefineable()
	{
		return true;
	}

	@Override
	protected boolean isYAxisDefineable()
	{
		return true;
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
	protected void convertChartDataPacksToSciomeSeries(String[] keys, List<ChartDataPack> chartPacks)
	{

	}

}
