package com.sciome.charts.javafx;

import java.util.List;
import java.util.Set;

import com.sciome.bmdexpress2.mvp.model.ChartKey;
import com.sciome.charts.SciomeBubbleChart;
import com.sciome.charts.SciomeChartListener;
import com.sciome.charts.data.ChartConfiguration;
import com.sciome.charts.data.ChartDataPack;
import com.sciome.charts.export.ChartDataExporter;
import com.sciome.charts.model.SciomeData;
import com.sciome.charts.model.SciomeSeries;

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

	public SciomeBubbleChartFX(String title, List<ChartDataPack> chartDataPacks, ChartKey key1, ChartKey key2,
			ChartKey key3, SciomeChartListener chartListener)
	{
		super(title, chartDataPacks, key1, key2, key3, false, false, chartListener);

	}

	/*
	 * generate bubble chart
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected Chart generateChart(ChartKey[] keys, ChartConfiguration chartConfig)
	{
		ChartKey key1 = keys[0];
		ChartKey key2 = keys[1];
		ChartKey key3 = keys[2];
		Double max1 = getMaxMax(key1);
		Double max2 = getMaxMax(key2);

		Double min1 = getMinMin(key1);
		Double min2 = getMinMin(key2);
		Double dataMin2 = min2;
		Double dataMin1 = min1;

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
		final Axis xAxis = SciomeNumberAxisGeneratorFX.generateAxis(getLogXAxis().isSelected(), min1, max1,
				dataMin1);
		final Axis yAxis = SciomeNumberAxisGeneratorFX.generateAxis(getLogYAxis().isSelected(), min2, max2,
				dataMin2);

		// LogarithmicAxis yAxis = new LogarithmicAxis();

		BubbleChart<Number, Number> blc = new BubbleChart<Number, Number>(xAxis, yAxis);

		xAxis.setLabel(key1.toString());
		yAxis.setLabel(key2.toString());
		blc.setTitle(key1 + " Vs. " + key2 + ": Bubble Size=" + key3);

		Double scaleValue = max2 / max1;

		for (SciomeSeries<Number, Number> sciomeSeries : getSeriesData())
		{

			XYChart.Series<Number, Number> series = new XYChart.Series<>();
			series.setName(sciomeSeries.getName());

			for (Object chartDataObj : sciomeSeries.getData())
			{
				SciomeData<Number, Number> chartData = (SciomeData<Number, Number>) chartDataObj;

				XYChart.Data<Number, Number> theData = new XYChart.Data<>(chartData.getXValue(),
						chartData.getYValue());
				theData.setExtraValue(chartData.getExtraValue());
				theData.setNode(bubblePane(((BubbleChartExtraData) chartData.getExtraValue()).bubbleSize,
						scaleValue, ((BubbleChartExtraData) chartData.getExtraValue()).userData, false));
				series.getData().add(theData);
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

	@Override
	public void reactToChattingCharts()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void markData(Set<String> markings)
	{
		// TODO Auto-generated method stub

	}

}
