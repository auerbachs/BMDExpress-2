package com.sciome.charts;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sciome.charts.data.ChartDataPack;
import com.sciome.charts.export.ChartDataExporter;

import javafx.scene.control.Tooltip;

/*
 * 
 */
public abstract class SciomeBubbleChart extends SciomeChartBase implements ChartDataExporter
{

	protected static Double					BUBBLE_SCALE_FRACTION	= 8.0;

	protected Tooltip						toolTip					= new Tooltip("");
	protected final int						MAXITEMS				= 2500;
	protected Map<String, NodeInformation>	nodeInfoMap				= new HashMap<>();

	private int								nodeCount;

	public SciomeBubbleChart(String title, List<ChartDataPack> chartDataPacks, String key1, String key2,
			String key3, SciomeChartListener chartListener)
	{
		super(title, chartDataPacks, chartListener);
		chartableKeys = new String[] { key1, key2, key3 };
		showChart();
		this.setMaxGraphItems(MAXITEMS);

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
		setMaxGraphItems(MAXITEMS);
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

	protected class NodeInformation
	{
		public Double	bubbleSize;
		public Double	scaleValue;
		public Object	object;
		public boolean	invisible;

		public NodeInformation(Double b, Double s, Object o, boolean i)
		{
			bubbleSize = b;
			scaleValue = s;
			object = o;
			invisible = i;
		}
	}

}
