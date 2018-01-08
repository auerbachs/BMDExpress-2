package com.sciome.charts;

import java.util.List;
import java.util.Map;

import com.sciome.bmdexpress2.mvp.model.ChartKey;
import com.sciome.charts.data.ChartDataPack;
import com.sciome.charts.export.ChartDataExporter;

import javafx.scene.control.Label;

/*
 * 
 */
public abstract class SciomePieChart extends SciomeChartBase<Number, Number> implements ChartDataExporter
{

	protected Map<String, Double>		pieDataMap;
	protected Map<String, List<Object>>	pieObjectMap;
	protected final Label				caption	= new Label("");

	public SciomePieChart(Map<String, Double> pieDataMap, Map<String, List<Object>> pieObjectMap,
			List<ChartDataPack> packs, String title, SciomeChartListener listener)
	{
		super(title, packs, new ChartKey[] {}, listener);
		this.pieDataMap = pieDataMap;
		this.pieObjectMap = pieObjectMap;

		showChart(caption);

	}

	public void redrawPieChart(Map<String, Double> pieDataMap, Map<String, List<Object>> pieObjectMap)
	{
		this.pieDataMap = pieDataMap;
		this.pieObjectMap = pieObjectMap;
		showChart(caption);
	}

	@Override
	protected boolean isXAxisDefineable()
	{
		return false;
	}

	@Override
	protected boolean isYAxisDefineable()
	{
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
