package com.sciome.charts.javafx;

import java.util.List;

import com.sciome.charts.data.ChartConfiguration;
import com.sciome.charts.data.ChartDataPack;

import javafx.scene.chart.Chart;

/*
 * 
 */
public class SciomeLineChart extends SciomeChartBase
{

	public SciomeLineChart(String title, List<ChartDataPack> chartDataPacks,
			SciomeChartListener chartListener)
	{
		super(title, chartDataPacks, chartListener);
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

}
