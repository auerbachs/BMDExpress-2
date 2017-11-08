package com.sciome.charts.javafx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sciome.charts.data.ChartConfiguration;
import com.sciome.charts.data.ChartData;
import com.sciome.charts.data.ChartDataPack;
import com.sciome.charts.export.ChartDataExporter;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.Chart;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.Glow;
import javafx.scene.layout.StackPane;

/*
 * 
 */
public class SciomeScatterChart extends ScrollableSciomeChart implements ChartDataExporter
{
	// map that keeps track of enough information to instantiate a node.
	// so we don't have to store 10,000 nodes in memory
	private Map<String, NodeInformation>	nodeInfoMap		= new HashMap<>();
	private Tooltip							toolTip			= new Tooltip("");

	private ScatterChart					schart;
	private String							key1;
	private String							key2;
	boolean									allowXLogAxis	= true;
	boolean									allowYLogAxis	= true;
	private final int						MAXITEMS		= 2500;
	private int								nodeCount;

	@SuppressWarnings("rawtypes")
	public SciomeScatterChart(String title, List<ChartDataPack> chartDataPacks, String key1, String key2,
			boolean allowXLogAxis, boolean allowYLogAxis, SciomeChartListener chartListener)
	{
		super(title, chartDataPacks, chartListener);

		this.key1 = key1;
		this.key2 = key2;

		chartableKeys = new String[] { key1, key2 };
		showLogAxes(allowXLogAxis, allowYLogAxis, false, false);
		initChart();

		logXAxis.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val)
			{
				initChart();
			}
		});

		logYAxis.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val)
			{
				initChart();
			}
		});

	}

	@SuppressWarnings("rawtypes")
	public SciomeScatterChart(String title, List<ChartDataPack> chartDataPacks, String key1, String key2,
			SciomeChartListener chartListener)
	{
		this(title, chartDataPacks, key1, key2, true, true, chartListener);
	}

	private void initChart()
	{
		seriesData.clear();
		setMaxGraphItems(MAXITEMS);
		showChart();
		intializeScrollableChart();

	}

	/*
	 * generate a histogram bar chart
	 */
	@Override
	protected Chart generateChart(String[] keys, ChartConfiguration chartConfig)
	{
		String key1 = keys[0];
		String key2 = keys[1];
		Double max1 = getMaxMax(key1);
		Double min1 = getMinMin(key1);

		Double max2 = getMaxMax(key2);
		Double min2 = getMinMin(key2);

		Double dataMin1 = min1;
		Double dataMin2 = min2;

		if (max1 <= min1)
		{
			max1 = 1.0;
			min1 = 0.1;
		}

		if (max2 <= min2)
		{
			max2 = 1.0;
			min2 = 0.1;
		}

		if (chartConfig != null && chartConfig.getMaxX() != null && chartConfig.getMinX() != null)
		{
			max1 = chartConfig.getMaxX();
			min1 = chartConfig.getMinX();
		}

		if (chartConfig != null && chartConfig.getMaxY() != null && chartConfig.getMinY() != null)
		{
			max2 = chartConfig.getMaxY();
			min2 = chartConfig.getMinY();
		}
		final Axis xAxis = SciomeNumberAxisGenerator.generateAxis(logXAxis.isSelected(), min1, max1,
				dataMin1);
		final Axis yAxis = SciomeNumberAxisGenerator.generateAxis(logYAxis.isSelected(), min2, max2,
				dataMin2);

		xAxis.setLabel(key1);
		yAxis.setLabel(key2);

		ScatterChart scatterChart = new ScatterChart(xAxis, yAxis);

		scatterChart.setTitle(key1 + " Vs. " + key2);

		// create count map because in multiple data comparison, I only care about
		// shared data labels
		Map<String, Integer> countMap = getCountMap();
		// Now put the data in a bucket
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
				if (nodecount > getMaxGraphItems() - 1 && !this.showAllCheckBox.isSelected())
					continue;
				nodecount++;
				count++;
				Double dataPointValue1 = (Double) chartData.getDataPoints().get(key1);
				Double dataPointValue2 = (Double) chartData.getDataPoints().get(key2);

				if (dataPointValue1 == null || dataPointValue2 == null)
					continue;
				SciomeData<Number, Number> xyData = new SciomeData<>(chartData.getDataPointLabel(),
						dataPointValue1, dataPointValue2, new ChartExtraValue(chartData.getDataPointLabel(),
								countMap.get(chartData.getDataPointLabel())));
				chartLabelSet.add(chartData.getDataPointLabel());
				nodeInfoMap.put(chartDataPack.getName() + chartData.getDataPointLabel(),
						new NodeInformation(chartData.getCharttableObject(), false));
				series1.getData().add(xyData);

			}
			toolTip.setStyle("-fx-font: 14 arial;  -fx-font-smoothing-type: lcd;");
			if (seriesData.size() > 0)
				sortSeriesWithPrimarySeries(series1, (SciomeSeries) (seriesData.get(0)));
			else
				sortSeriesX(series1);
			seriesData.add(series1);

			if (nodecount > getMaxGraphItems() - 1 && !this.showAllCheckBox.isSelected())
				break;

		}

		this.warningTooManyNodesLabel.setText("WARNING: Only showing " + MAXITEMS + " of " + totalnodecount
				+ " items in chart.  To view all, maximize and select \"Show All Nodes\"");
		if (nodecount < getMaxGraphItems() - 1)
			showTooManyNodes(false);
		else if (nodecount > getMaxGraphItems() - 1 && !this.showAllCheckBox.isSelected())
			showTooManyNodes(true);

		this.nodeCount = totalnodecount;
		return scatterChart;
	}

	private StackPane userObjectPane(Object object, boolean invisible)
	{

		StackPane node = new StackPane();
		node.setUserData(object);

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

		public Object	object;
		public boolean	invisible;

		public NodeInformation(Object o, boolean i)
		{
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

		return userObjectPane(nI.object, nI.invisible);
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

	/*
	 * implement the getting of lines that need to be exported.
	 */
	@Override
	public List<String> getLinesToExport()
	{
		XYChart xyChart = (XYChart) getChart();

		List<String> returnList = new ArrayList<>();
		StringBuilder sb = new StringBuilder();
		List<XYChart.Series> data = xyChart.getData();

		sb.append("series");
		sb.append("\t");
		sb.append("x");
		sb.append("\t");
		sb.append("y");
		sb.append("\t");
		sb.append("label");
		returnList.add(sb.toString());
		for (XYChart.Series seriesData : data)
		{

			System.out.println(seriesData.getName());
			for (Object d : seriesData.getData())
			{
				XYChart.Data xychartData = (XYChart.Data) d;
				ChartExtraValue extraValue = (ChartExtraValue) xychartData.getExtraValue();
				if (extraValue.label.equals("")) // this means it's a faked value for showing multiple
													// datasets together. skip it
					continue;
				sb.setLength(0);

				Double X = (Double) xychartData.getXValue();
				Double Y = (Double) xychartData.getYValue();
				StackPane node = (StackPane) xychartData.getNode();

				sb.append(seriesData.getName());

				sb.append("\t");

				sb.append(X);
				sb.append("\t");
				sb.append(Y);
				sb.append("\t");
				sb.append(node.getUserData().toString());

				returnList.add(sb.toString());

			}
		}

		return returnList;
	}
}
