package com.sciome.charts.javafx;

import java.util.List;
import java.util.Set;

import com.sciome.bmdexpress2.mvp.model.ChartKey;
import com.sciome.charts.SciomeChartListener;
import com.sciome.charts.SciomeHeatMap;
import com.sciome.charts.data.ChartConfiguration;
import com.sciome.charts.data.ChartDataPack;

import javafx.scene.chart.Chart;

public class SciomeHeatMapFX extends SciomeHeatMap
{

	public SciomeHeatMapFX(String title, List<ChartDataPack> chartDataPacks,
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
	protected void reactToChattingCharts()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void markData(Set<String> markings)
	{
		// TODO Auto-generated method stub

	}

}
