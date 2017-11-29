package com.sciome.charts.javafx;

import java.util.List;

import com.sciome.charts.SciomeAccumulationPlot;
import com.sciome.charts.SciomeChartListener;
import com.sciome.charts.data.ChartConfiguration;
import com.sciome.charts.data.ChartDataPack;
import com.sciome.charts.model.SciomeSeries;

import javafx.event.EventHandler;
import javafx.scene.chart.Axis;
import javafx.scene.chart.Chart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.Glow;
import javafx.scene.layout.StackPane;

/*
 * 
 */
public class SciomeAccumulationPlotFX extends SciomeAccumulationPlot
{

	public SciomeAccumulationPlotFX(String title, List<ChartDataPack> chartDataPacks, String key,
			Double bucketsize, SciomeChartListener chartListener)
	{
		super(title, chartDataPacks, key, bucketsize, chartListener);

	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected Chart generateChart(String[] keys, ChartConfiguration chartConfig)
	{
		String key1 = keys[0];
		String key2 = "Accumulation";
		Double dataMin = getMinMin(key1);
		// final NumberAxis xAxis = new NumberAxis();
		// final LogarithmicAxis yAxis = new LogarithmicAxis();
		final Axis yAxis;
		final Axis xAxis;

		yAxis = SciomeNumberAxisGeneratorFX.generateAxis(getLogYAxis().isSelected(),
				(chartConfig == null ? null : chartConfig.getMinY()),
				chartConfig == null ? null : chartConfig.getMaxY(), 1.0);
		xAxis = SciomeNumberAxisGeneratorFX.generateAxis(getLogXAxis().isSelected(),
				chartConfig == null ? null : chartConfig.getMinX(),
				chartConfig == null ? null : chartConfig.getMaxX(), dataMin);

		xAxis.setLabel(key1);
		yAxis.setLabel(key2);
		// creating the chart
		final LineChart<Number, Number> lineChart = new LineChart<Number, Number>(xAxis, yAxis);

		lineChart.setTitle(key1 + " Accumulation Plot");
		// defining a series

		try
		{
			for (SciomeSeries sciomeSeries : getSeriesData())
			{
				XYChart.Series series = new XYChart.Series();
				series.setName(sciomeSeries.getName());

				for (Object objValue : sciomeSeries.getData())
				{
					AccumulationData value = (AccumulationData) objValue;

					XYChart.Data theData = new XYChart.Data(value.getXValue(), value.getYValue());
					theData.setExtraValue(value.getExtraValue());
					theData.setNode(userObjectPane(value.getExtraValue(), value.getYValue().doubleValue(),
							value.getValuesList(), key1));
					series.getData().add(theData);

				}
				lineChart.getData().add(series);

			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return lineChart;
	}

	private StackPane userObjectPane(Object object, Double accumulation, List<Double> values, String key)
	{

		StackPane returnPane = new StackPane();
		returnPane.setUserData(object);
		Tooltip.install(returnPane, toolTip);

		returnPane.setOnMouseEntered(new EventHandler<javafx.scene.input.MouseEvent>() {
			@Override
			public void handle(javafx.scene.input.MouseEvent arg0)
			{
				returnPane.setEffect(new Glow());

				@SuppressWarnings("unchecked")
				List<Object> objects = (List<Object>) returnPane.getUserData();
				if (objects != null)
					toolTip.setText(
							String.valueOf(joinObjects(objects, accumulation, values, key, MAX_TO_POPUP)));

			}
		});

		// OnMouseExited
		returnPane.setOnMouseExited(new EventHandler<javafx.scene.input.MouseEvent>() {
			@Override
			public void handle(javafx.scene.input.MouseEvent arg0)
			{
				returnPane.setEffect(null);
			}
		});

		// OnMouseReleased
		returnPane.setOnMouseReleased(new EventHandler<javafx.scene.input.MouseEvent>() {
			@Override
			public void handle(javafx.scene.input.MouseEvent mouseEvent)
			{
			}
		});

		returnPane.setOnMouseClicked(new EventHandler<javafx.scene.input.MouseEvent>() {
			@Override
			public void handle(javafx.scene.input.MouseEvent mouseEvent)
			{
				@SuppressWarnings("unchecked")
				List<Object> objects = (List<Object>) returnPane.getUserData();
				if (objects != null)
					showObjectText(String.valueOf(joinAllObjects(objects, accumulation, values, key)));

				returnPane.setEffect(null);
			}
		});

		return returnPane;
	}

}
