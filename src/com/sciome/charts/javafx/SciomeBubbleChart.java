package com.sciome.charts.javafx;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sciome.charts.data.ChartConfiguration;
import com.sciome.charts.data.ChartData;
import com.sciome.charts.data.ChartDataPack;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.BubbleChart;
import javafx.scene.chart.Chart;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.Glow;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Ellipse;

/*
 * 
 */
public class SciomeBubbleChart extends ScrollableSciomeChart
{

	private static Double					BUBBLE_SCALE_FRACTION	= 8.0;

	private Tooltip							toolTip					= new Tooltip("");
	private final int						MAXITEMS				= 2500;

	// map that keeps track of enough information to instantiate a node.
	// so we don't have to store 10,000 nodes in memory
	private Map<String, NodeInformation>	nodeInfoMap				= new HashMap<>();

	private int								nodeCount;

	public SciomeBubbleChart(String title, List<ChartDataPack> chartDataPacks, String key1, String key2,
			String key3, SciomeChartListener chartListener)
	{
		super(title, chartDataPacks, chartListener);
		chartableKeys = new String[] { key1, key2, key3 };
		showChart();
		this.setMaxGraphItems(MAXITEMS);
		intializeScrollableChart();

	}

	/*
	 * generate bubble chart
	 */
	@Override
	protected Chart generateChart(String[] keys, ChartConfiguration chartConfig)
	{
		String key1 = keys[0];
		String key2 = keys[1];
		String key3 = keys[2];
		Double max1 = getMaxMax(key1);
		Double max2 = getMaxMax(key2);
		Double max3 = getMaxMax(key3);

		Double min1 = getMinMin(key1);
		Double min2 = getMinMin(key2);
		Double dataMin2 = min2;
		Double dataMin1 = min1;

		if (chartConfig != null && chartConfig.getMaxX() != null && chartConfig.getMinX() != null)
		{
			max2 = chartConfig.getMaxX();
			min2 = chartConfig.getMinX();
		}

		if (chartConfig != null && chartConfig.getMaxY() != null && chartConfig.getMinY() != null)
		{
			max1 = chartConfig.getMaxY();
			min1 = chartConfig.getMinY();
		}
		final Axis xAxis = SciomeNumberAxisGenerator.generateAxis(logXAxis.isSelected(), min1, max1,
				dataMin1);
		final Axis yAxis = SciomeNumberAxisGenerator.generateAxis(logYAxis.isSelected(), min2, max2,
				dataMin2);

		// LogarithmicAxis yAxis = new LogarithmicAxis();

		BubbleChart<Number, Number> blc = new BubbleChart<Number, Number>(xAxis, yAxis);

		xAxis.setLabel(key1);
		yAxis.setLabel(key2);
		blc.setTitle(key1 + " Vs. " + key2 + ": Bubble Size=" + key3);

		Double scaleValue = max2 / max1;
		Double bubbleScale = (1.0 / BUBBLE_SCALE_FRACTION) / (max3 / max1);

		// create count map because in multiple data comparison, I only care about
		// shared data labels
		Map<String, Integer> countMap = getCountMap();

		int maxPerPack = 0;
		if (chartDataPacks.size() > 0)
			maxPerPack = MAX_NODES / chartDataPacks.size();
		int nodecount = 0;
		int totalnodecount = 0;
		for (ChartDataPack chartDataPack : chartDataPacks)
		{
			SciomeSeries<Number, Number> series1 = new SciomeSeries<>(chartDataPack.getName());
			int count = 0;
			Set<String> chartLabelSet = new HashSet<>();
			for (ChartData chartData : chartDataPack.getChartData())
			{
				if (cancel)
					return null;

				totalnodecount++;
				// too many nodes
				if (nodecount > getMaxGraphItems() - 1 && !this.showAllCheckBox.isSelected())
					continue;
				if (chartData.getDataPoints().containsKey(key1) && chartData.getDataPoints().containsKey(key2)
						&& chartData.getDataPoints().containsKey(key3))
				{
					SciomeData<Number, Number> dataPoint = new SciomeData<>(chartData.getDataPointLabel(),
							(Double) chartData.getDataPoints().get(key1),
							(Double) chartData.getDataPoints().get(key2),
							new ChartExtraValue(chartData.getDataPointLabel(),
									countMap.get(chartData.getDataPointLabel())));
					nodeInfoMap.put(chartDataPack.getName() + chartData.getDataPointLabel(),
							new NodeInformation((Double) chartData.getDataPoints().get(key3) * bubbleScale,
									scaleValue, chartData.getCharttableObject(), false));
					series1.getData().add(dataPoint);
					chartLabelSet.add(chartData.getDataPointLabel());
					count++;
					nodecount++;

				}

			}

			if (seriesData.size() > 0)
				sortSeriesWithPrimarySeries(series1, (SciomeSeries) (seriesData.get(0)));
			else
				sortSeriesX(series1);
			seriesData.add(series1);

		}

		this.warningTooManyNodesLabel.setText("WARNING: Only showing " + MAXITEMS + " of " + totalnodecount
				+ " items in chart.  To view all, maximize and select \"Show All Nodes\"");

		if (nodecount < getMaxGraphItems() - 1)
			showTooManyNodes(false);
		else if (nodecount > getMaxGraphItems() - 1 && !this.showAllCheckBox.isSelected())
			showTooManyNodes(true);

		toolTip.setStyle("-fx-font: 14 arial;  -fx-font-smoothing-type: lcd;");;
		this.nodeCount = totalnodecount;
		return blc;

	}

	private StackPane bubblePane(Double bubbleSize, Double scaleValue, Object object, boolean invisible)
	{

		StackPane node = new StackPane();
		node.setMaxHeight(bubbleSize);
		node.setMinHeight(bubbleSize);
		node.setPrefHeight(bubbleSize);
		node.setMaxWidth(bubbleSize);
		node.setMinWidth(bubbleSize);
		node.setPrefWidth(bubbleSize);
		node.setShape(new Ellipse());
		node.setScaleShape(false);
		node.setScaleY(scaleValue * bubbleSize);
		node.setScaleX(bubbleSize);
		node.setUserData(object);
		node.setOpacity(0.75);
		toolTip.setConsumeAutoHidingEvents(false);
		if (invisible)
			node.setVisible(false);
		else
		{
			Tooltip.install(node, toolTip);

			node.setOnMouseEntered(new EventHandler<javafx.scene.input.MouseEvent>() {
				@Override
				public void handle(javafx.scene.input.MouseEvent arg0)
				{
					node.setEffect(new Glow());
					Object object = node.getUserData();
					if (object != null)
						toolTip.setText(String.valueOf(node.getUserData().toString()));

				}
			});

			// OnMouseExited
			node.setOnMouseExited(new EventHandler<javafx.scene.input.MouseEvent>() {
				@Override
				public void handle(javafx.scene.input.MouseEvent arg0)
				{
					node.setEffect(null);
				}
			});

			// OnMouseReleased
			node.setOnMouseReleased(new EventHandler<javafx.scene.input.MouseEvent>() {
				@Override
				public void handle(javafx.scene.input.MouseEvent mouseEvent)
				{
				}
			});
			node.setOnMouseClicked(new EventHandler<javafx.scene.input.MouseEvent>() {
				@Override
				public void handle(javafx.scene.input.MouseEvent mouseEvent)
				{
					node.setEffect(new Glow());
					Object object = node.getUserData();
					if (object != null)
						showObjectText(String.valueOf(node.getUserData().toString()));

					node.setEffect(null);
				}
			});
		}

		return node;
	}

	private class NodeInformation
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

	// never show all for this. because it's like a bar chart
	@Override
	public void setShowShowAll(boolean showshowall)
	{
		if (showshowall && getMaxGraphItems() < this.nodeCount)
			super.setShowShowAll(showshowall);
		else
			super.setShowShowAll(false);
	}

	@Override
	protected Node getNode(String seriesName, String dataPointLabel, int seriesIndex)
	{
		NodeInformation nI = nodeInfoMap.get(seriesName + dataPointLabel);

		return (bubblePane(nI.bubbleSize, nI.scaleValue, nI.object, nI.invisible));

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
		seriesData.clear();
		showChart();
		setMaxGraphItems(MAXITEMS);
		intializeScrollableChart();
	}

}
