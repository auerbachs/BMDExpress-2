package com.sciome.charts.javafx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import com.sciome.charts.data.ChartConfiguration;
import com.sciome.charts.data.ChartData;
import com.sciome.charts.data.ChartDataPack;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.chart.Axis;
import javafx.scene.chart.Chart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.Glow;
import javafx.scene.layout.StackPane;

/*
 * 
 */
public class SciomeAccumulationPlot extends SciomeChartBase
{
	private final static Integer	MAX_ACCUMULATION_BEFORE_MODULUS	= 300;
	private final static Integer	MOD_AFTER_REACH_MAX				= 20;
	private final static Integer	MAX_TO_POPUP					= 100;
	private final static Integer	MAX_PREV_OBJECTS_TO_STORE		= 0;
	private CheckBox				unBinCheckBox					= new CheckBox("Show All Data");
	private Tooltip					toolTip							= new Tooltip("");

	public SciomeAccumulationPlot(String title, List<ChartDataPack> chartDataPacks, String key,
			Double bucketsize, SciomeChartListener chartListener)
	{
		super(title, chartDataPacks, chartListener);

		logXAxis.setSelected(true);
		logYAxis.setSelected(false);
		this.chartableKeys = new String[] { key };
		showLogAxes(true, true, false, false, Arrays.asList(unBinCheckBox));
		showChart();

		logXAxis.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val)
			{
				showChart();
			}
		});

		logYAxis.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val)
			{
				showChart();
			}
		});

		unBinCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val)
			{
				showChart();
			}
		});

	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected Chart generateChart(String[] keys, ChartConfiguration chartConfig)
	{
		String key = keys[0];
		Double dataMin = getMinMin(key);
		// final NumberAxis xAxis = new NumberAxis();
		// final LogarithmicAxis yAxis = new LogarithmicAxis();
		final Axis yAxis;
		final Axis xAxis;

		yAxis = SciomeNumberAxisGenerator.generateAxis(logYAxis.isSelected(),
				(chartConfig == null ? null : chartConfig.getMinY()),
				chartConfig == null ? null : chartConfig.getMaxY(), 1.0);
		xAxis = SciomeNumberAxisGenerator.generateAxis(logXAxis.isSelected(),
				chartConfig == null ? null : chartConfig.getMinX(),
				chartConfig == null ? null : chartConfig.getMaxX(), dataMin);

		xAxis.setLabel(key);
		yAxis.setLabel("Accumulation");
		// creating the chart
		final LineChart<Number, Number> lineChart = new LineChart<Number, Number>(xAxis, yAxis);

		lineChart.setTitle(key + " Accumulation Plot");
		// defining a series

		for (ChartDataPack chartDataPack : chartDataPacks)
		{
			List<ChartData> doubleList = new ArrayList<>();
			for (ChartData chartData : chartDataPack.getChartData())
			{
				if (cancel)
					return null;
				Double dataPoint = (Double) chartData.getDataPoints().get(key);
				if (dataPoint == null)
					continue;
				doubleList.add(chartData);
			}

			doubleList.sort(new Comparator<ChartData>() {
				@Override
				public int compare(ChartData o1, ChartData o2)
				{
					return ((Double) o1.getDataPoints().get(key))
							.compareTo((Double) o2.getDataPoints().get(key));
				}
			});

			int i = 0;
			XYChart.Series series = new XYChart.Series();
			series.setName(chartDataPack.getName());
			Double accumulation = 0.0;

			int count = 0;
			Double currentValue = null;
			List<Object> charttableObjectsMasterList = new ArrayList<>();
			List<Object> charttableObjects = new ArrayList<>();
			/*
			 * start adding accumulation values
			 */

			// a list that will store the values associated with the object.
			List<Double> valuesList = new ArrayList<>();
			for (ChartData value : doubleList)
			{
				Double newValue = (Double) value.getDataPoints().get(key);
				if (!newValue.equals(currentValue) && currentValue != null)
				{
					if (unBinCheckBox.isSelected() || count < MAX_ACCUMULATION_BEFORE_MODULUS
							|| (count >= MAX_ACCUMULATION_BEFORE_MODULUS && i % MOD_AFTER_REACH_MAX == 0))
					{
						if (charttableObjects.size() == 1) // we are in the area before the modulus kicks in.
						{
							int adds = 0;
							int j = i - 1;
							while (adds < MAX_PREV_OBJECTS_TO_STORE && j >= 0)
							{
								charttableObjects.add(charttableObjectsMasterList.get(j));
								j--;
								adds++;
							}

						}
						XYChart.Data theData = new XYChart.Data(currentValue, accumulation);
						theData.setNode(userObjectPane(charttableObjects, accumulation, valuesList, key));
						series.getData().add(theData);
						charttableObjects = new ArrayList<>();
						valuesList = new ArrayList<>();

					}

					count++;
				}
				valuesList.add(newValue);
				charttableObjects.add(value.getCharttableObject());
				charttableObjectsMasterList.add(value.getCharttableObject());
				accumulation++;
				currentValue = newValue;
				i++;
			}
			// get the last one
			if (currentValue != null)
			{
				XYChart.Data theData = new XYChart.Data(currentValue, accumulation);
				theData.setNode(userObjectPane(charttableObjects, accumulation, valuesList, key));
				series.getData().add(theData);
			}
			lineChart.getData().add(series);

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
				Object object = returnPane.getUserData();

				List<Object> objects = (List) returnPane.getUserData();
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
				List<Object> objects = (List) returnPane.getUserData();
				if (objects != null)
					showObjectText(String.valueOf(joinAllObjects(objects, accumulation, values, key)));

				returnPane.setEffect(null);
			}
		});

		return returnPane;
	}

	// join objects together to show a list of lines that show values of the points being plotted
	// the objects and values need to be in alignment to ensure that the value they represent
	// is properly appended next to it in the popup.
	private String joinObjects(List<Object> objects, Double accumulation, List<Double> values, String key,
			int max)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("Accumulation = ");
		sb.append(accumulation);
		sb.append("\n");
		int i = 0;
		for (Object obj : objects)
		{
			sb.append(obj);
			sb.append("  ");
			sb.append(key);
			sb.append(" = ");
			sb.append(values.get(i));
			sb.append(System.getProperty("line.separator"));
			if (i > max)
			{
				sb.append("....");
				break;
			}
			i++;
		}
		return sb.toString();
	}

	private String joinAllObjects(List<Object> objects, Double accumulation, List<Double> values, String key)
	{
		return joinObjects(objects, accumulation, values, key, 2000000000);

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
		showChart();

	}

}
