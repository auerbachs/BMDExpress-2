package com.sciome.charts.javafx;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.sciome.charts.SciomeBubbleChart;
import com.sciome.charts.SciomeChartListener;
import com.sciome.charts.data.ChartConfiguration;
import com.sciome.charts.data.ChartData;
import com.sciome.charts.data.ChartDataPack;
import com.sciome.charts.export.ChartDataExporter;
import com.sciome.charts.utils.SciomeNumberAxisGenerator;

import javafx.event.EventHandler;
import javafx.scene.chart.Axis;
import javafx.scene.chart.BubbleChart;
import javafx.scene.chart.Chart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.Glow;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Ellipse;

/*
 * 
 */
public class SciomeBubbleChartFX extends SciomeBubbleChart implements ChartDataExporter
{

	// map that keeps track of enough information to instantiate a node.
	// so we don't have to store 10,000 nodes in memory

	public SciomeBubbleChartFX(String title, List<ChartDataPack> chartDataPacks, String key1, String key2,
			String key3, SciomeChartListener chartListener)
	{
		super(title, chartDataPacks, key1, key2, key3, chartListener);

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

		int maxPerPack = 0;
		int nodecount = 0;
		int totalnodecount = 0;
		for (ChartDataPack chartDataPack : chartDataPacks)
		{

			XYChart.Series series = new XYChart.Series();
			series.setName(chartDataPack.getName());

			int count = 0;
			Set<String> chartLabelSet = new HashSet<>();

			for (ChartData chartData : chartDataPack.getChartData())
			{
				if (cancel)
					return null;

				totalnodecount++;
				// too many nodes
				if (nodecount > getMaxGraphItems() - 1)
					continue;
				if (chartData.getDataPoints().containsKey(key1) && chartData.getDataPoints().containsKey(key2)
						&& chartData.getDataPoints().containsKey(key3))
				{

					XYChart.Data theData = new XYChart.Data((Double) chartData.getDataPoints().get(key1),
							(Double) chartData.getDataPoints().get(key2));
					theData.setExtraValue(new BubbleChartExtraData(chartData.getDataPointLabel(), 0,
							chartData.getCharttableObject(),
							(Double) chartData.getDataPoints().get(key3) * bubbleScale));
					theData.setNode(bubblePane((Double) chartData.getDataPoints().get(key3) * bubbleScale,
							scaleValue, chartData.getCharttableObject(), false));
					series.getData().add(theData);

					chartLabelSet.add(chartData.getDataPointLabel());
					count++;
					nodecount++;

				}
			}

			blc.getData().add(series);

		}

		toolTip.setStyle("-fx-font: 14 arial;  -fx-font-smoothing-type: lcd;");;
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
		sb.append("bubble size");
		sb.append("\t");
		sb.append("label");
		returnList.add(sb.toString());
		for (XYChart.Series seriesData : data)
		{
			for (Object d : seriesData.getData())
			{
				XYChart.Data xychartData = (XYChart.Data) d;
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
