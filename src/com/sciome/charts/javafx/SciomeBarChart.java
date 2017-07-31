package com.sciome.charts.javafx;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sciome.charts.data.ChartConfiguration;
import com.sciome.charts.data.ChartData;
import com.sciome.charts.data.ChartDataPack;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.Chart;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.Glow;
import javafx.scene.layout.StackPane;

/*
 * 
 */
public class SciomeBarChart extends ScrollableSciomeChart
{

	// map that keeps track of enough information to instantiate a node.
	// so we don't have to store 10,000 nodes in memory
	private Map<String, NodeInformation>	nodeInfoMap	= new HashMap<>();
	private Tooltip							toolTip		= new Tooltip("");
	private final int						MAXITEMS	= 50;

	public SciomeBarChart(String title, List<ChartDataPack> chartDataPacks, String key,
			SciomeChartListener chartListener)
	{
		super(title, chartDataPacks, chartListener);
		chartableKeys = new String[] { key };
		showLogAxes(false, true, false, true);

		logYAxis.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val)
			{
				initChart();
			}
		});
		lockYAxis.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val)
			{
				initChart();
			}
		});

		initChart();
		intializeScrollableChart();
		setShowShowAll(false);
	}

	private void initChart()
	{
		seriesData.clear();
		showChart();
		setMaxGraphItems(MAXITEMS);
		intializeScrollableChart();
	}

	// never show all for this. because it's like a bar chart
	@Override
	public void setShowShowAll(boolean showshowall)
	{
		super.setShowShowAll(false);
	}

	/*
	 * generate a bar chart
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected Chart generateChart(String[] keys, ChartConfiguration chartConfig)
	{
		String key = keys[0];
		Double max = getMaxMax(key);
		Double min = getMinMin(key);
		Double dataMin = min;

		CategoryAxis xAxis = new CategoryAxis();

		if (!lockYAxis.isSelected())
		{
			if (chartConfig != null && chartConfig.getMaxY() != null && chartConfig.getMinY() != null)
			{
				max = chartConfig.getMaxY();
				min = chartConfig.getMinY();
			}
		}
		final Axis yAxis;
		yAxis = SciomeNumberAxisGenerator.generateAxis(logYAxis.isSelected(), min, max, dataMin);

		xAxis.setLabel("Category");
		yAxis.setLabel(key);
		BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);

		barChart.setTitle("Multiple Data Viewer: " + key);
		barChart.setBarGap(0.0);
		barChart.setCategoryGap(3.0);
		// Now put the data in a bucket

		// create count map because in multiple data comparison, I only care about
		// shared data labels
		Map<String, Integer> countMap = getCountMap();

		int maxPerPack = 0;
		if (chartDataPacks.size() > 0)
			maxPerPack = MAX_NODES / chartDataPacks.size();
		for (ChartDataPack chartDataPack : chartDataPacks)
		{
			SciomeSeries<String, Number> series1 = new SciomeSeries<>(chartDataPack.getName());

			Set<String> chartLabelSet = new HashSet<>();
			int count = 0;
			for (ChartData chartData : chartDataPack.getChartData())
			{
				if (cancel)
					return null;
				Double dataPointValue = (Double) chartData.getDataPoints().get(key);
				if (dataPointValue == null)
					continue;
				SciomeData<String, Number> xyData = new SciomeData<>(chartData.getDataPointLabel(),
						chartData.getDataPointLabel(), dataPointValue, new ChartExtraValue(
								chartData.getDataPointLabel(), countMap.get(chartData.getDataPointLabel())));

				series1.getData().add(xyData);

				chartLabelSet.add(chartData.getDataPointLabel());
				nodeInfoMap.put(chartDataPack.getName() + chartData.getDataPointLabel(),
						new NodeInformation(chartData.getCharttableObject(), false));
				count++;
				// too many nodes
				if (count > maxPerPack)
					break;

			}

			// add empty values for multiple datasets. When comparing multiple
			// data sets, it comes in handy for scrolling to just have empty
			// data points when the data set doesn't represent a label
			for (String chartedKey : countMap.keySet())
			{
				if (cancel)
					return null;
				if (!chartLabelSet.contains(chartedKey))
				{
					SciomeData<String, Number> xyData = new SciomeData<>(chartedKey, chartedKey, 0.0,
							new ChartExtraValue(chartedKey, countMap.get(chartedKey)));

					series1.getData().add(xyData);
					nodeInfoMap.put(chartDataPack.getName() + chartedKey, new NodeInformation(null, true));
				}
			}

			if (seriesData.size() > 0)
				sortSeriesWithPrimarySeries(series1, (SciomeSeries) (seriesData.get(0)));
			else
				sortSeriesY(series1);
			seriesData.add(series1);

		}

		toolTip.setStyle("-fx-font: 14 arial;  -fx-font-smoothing-type: lcd;");

		return barChart;
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

	@Override
	protected Node getNode(String seriesName, String dataPointLabel, int seriesIndex)
	{
		NodeInformation nI = nodeInfoMap.get(seriesName + dataPointLabel);

		return userObjectPane(nI.object, nI.invisible);
	}

	@Override
	protected boolean isXAxisDefineable()
	{
		return false;
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

}
