package com.sciome.charts.javafx;

import java.util.List;

import com.sciome.bmdexpress2.mvp.model.ChartKey;
import com.sciome.charts.SciomeChartListener;
import com.sciome.charts.SciomeLineChart;
import com.sciome.charts.data.ChartConfiguration;
import com.sciome.charts.data.ChartDataPack;

import javafx.scene.chart.Chart;

/*
 * 
 */
public class SciomeLineChartFX extends SciomeLineChart
{

	public SciomeLineChartFX(String title, List<ChartDataPack> chartDataPacks,
			SciomeChartListener chartListener)
	{
		super(title, chartDataPacks, chartListener);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Chart generateChart(ChartKey[] keys, ChartConfiguration chartConfig)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void recieveChatFromOtherChart(List<Object> conversation)
	{
		// TODO Auto-generated method stub

	}

}
