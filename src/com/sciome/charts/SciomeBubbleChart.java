package com.sciome.charts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.sciome.bmdexpress2.mvp.model.ChartKey;
import com.sciome.charts.data.ChartData;
import com.sciome.charts.data.ChartDataPack;
import com.sciome.charts.export.ChartDataExporter;
import com.sciome.charts.model.SciomeData;
import com.sciome.charts.model.SciomeSeries;

import javafx.scene.control.Tooltip;

/*
 * 
 */
public abstract class SciomeBubbleChart extends SciomeChartBase<Number, Number> implements ChartDataExporter
{

	protected static Double	BUBBLE_SCALE_FRACTION	= 8.0;

	protected Tooltip		toolTip					= new Tooltip("");

	private int				nodeCount;

	public SciomeBubbleChart(String title, List<ChartDataPack> chartDataPacks, ChartKey key1, ChartKey key2,
			ChartKey key3, boolean allowXAxisSlider, boolean allowYAxisSlider,
			SciomeChartListener chartListener)
	{
		super(title, chartDataPacks, new ChartKey[] { key1, key2, key3 }, allowXAxisSlider, allowYAxisSlider,
				chartListener);
		showChart();

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
		initChart();

	}

	private void initChart()
	{
		showChart();
	}

	protected class BubbleChartExtraData extends ChartExtraValue
	{
		public Double bubbleSize;

		public BubbleChartExtraData(String l, Integer c, Object u, Double bubbleSize)
		{
			super(l, c, u);
			this.bubbleSize = bubbleSize;
		}
	}

	/*
	 * fill up the sciome series data structure so implementing classes can use it to create charts
	 */
	@Override
	protected void convertChartDataPacksToSciomeSeries(ChartKey[] keys, List<ChartDataPack> chartPacks)
	{
		ChartKey key1 = keys[0];
		ChartKey key2 = keys[1];
		ChartKey key3 = keys[2];
		List<ChartKey> keyList = Arrays.asList(key1, key2, key3);
		Double max1 = getMaxMax(key1);
		Double max3 = getMaxMax(key3);
		Double bubbleScale = (1.0 / BUBBLE_SCALE_FRACTION) / (max3 / max1);

		int nodecount = 0;
		List<SciomeSeries<Number, Number>> seriesData = new ArrayList<>();
		for (ChartDataPack chartDataPack : getChartDataPacks())
		{

			SciomeSeries<Number, Number> series = new SciomeSeries<>();
			series.setName(chartDataPack.getName());

			Set<String> chartLabelSet = new HashSet<>();

			for (ChartData chartData : chartDataPack.getChartData())
			{
				if (chartData.getDataPoints().containsKey(key1) && chartData.getDataPoints().containsKey(key2)
						&& chartData.getDataPoints().containsKey(key3))
				{
					if (!keysCheckOut(keyList, chartData))
						continue;
					SciomeData<Number, Number> theData = new SciomeData<>(chartData.getDataPointLabel(),
							(Double) chartData.getDataPoints().get(key1),
							(Double) chartData.getDataPoints().get(key2),
							new BubbleChartExtraData(chartData.getDataPointLabel(), 0,
									chartData.getCharttableObject(),
									(Double) chartData.getDataPoints().get(key3) * bubbleScale));
					series.getData().add(theData);

					chartLabelSet.add(chartData.getDataPointLabel());
					nodecount++;

				}
			}
			seriesData.add(series);
		}
		setSeriesData(seriesData);
	}

	/*
	 * implement the getting of lines that need to be exported.
	 */
	@Override
	public List<String> getLinesToExport()
	{

		List<String> returnList = new ArrayList<>();
		StringBuilder sb = new StringBuilder();

		sb.append("series");
		sb.append("\t");
		sb.append("x");
		sb.append("\t");
		sb.append("y");
		sb.append("\t");
		sb.append("bubble size");
		sb.append("\t");
		sb.append("label");
		returnList.add(sb.toString());
		for (SciomeSeries<Number, Number> seriesData : getSeriesData())
		{
			for (SciomeData<Number, Number> xychartData : seriesData.getData())
			{
				BubbleChartExtraData extraValue = (BubbleChartExtraData) xychartData.getExtraValue();
				if (extraValue.label.equals("")) // this means it's a faked value for showing multiple
													// datasets together. skip it
					continue;
				sb.setLength(0);

				Double X = (Double) xychartData.getXValue();
				Double Y = (Double) xychartData.getYValue();

				sb.append(seriesData.getName());

				sb.append("\t");

				sb.append(X);
				sb.append("\t");
				sb.append(Y);
				sb.append("\t");
				sb.append(extraValue.bubbleSize);
				sb.append("\t");
				sb.append(extraValue.userData);

				returnList.add(sb.toString());

			}
		}

		return returnList;
	}

}
