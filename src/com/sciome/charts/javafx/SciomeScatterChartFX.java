package com.sciome.charts.javafx;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.sciome.charts.SciomeChartListener;
import com.sciome.charts.SciomeScatterChart;
import com.sciome.charts.data.ChartConfiguration;
import com.sciome.charts.data.ChartData;
import com.sciome.charts.data.ChartDataPack;
import com.sciome.charts.export.ChartDataExporter;
import com.sciome.charts.utils.SciomeNumberAxisGenerator;

import javafx.event.EventHandler;
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
public class SciomeScatterChartFX extends SciomeScatterChart implements ChartDataExporter
{

	@SuppressWarnings("rawtypes")
	public SciomeScatterChartFX(String title, List<ChartDataPack> chartDataPacks, String key1, String key2,
			boolean allowXLogAxis, boolean allowYLogAxis, SciomeChartListener chartListener)
	{
		super(title, chartDataPacks, key1, key2, allowXLogAxis, allowYLogAxis, chartListener);
	}

	@SuppressWarnings("rawtypes")
	public SciomeScatterChartFX(String title, List<ChartDataPack> chartDataPacks, String key1, String key2,
			SciomeChartListener chartListener)
	{
		this(title, chartDataPacks, key1, key2, true, true, chartListener);
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

		int nodecount = 0;
		int totalnodecount = 0;
		for (ChartDataPack chartDataPack : chartDataPacks)
		{

			XYChart.Series series = new XYChart.Series();
			series.setName(chartDataPack.getName());

			Set<String> chartLabelSet = new HashSet<>();
			for (ChartData chartData : chartDataPack.getChartData())
			{
				if (cancel)
					return null;
				totalnodecount++;

				nodecount++;
				Double dataPointValue1 = (Double) chartData.getDataPoints().get(key1);
				Double dataPointValue2 = (Double) chartData.getDataPoints().get(key2);

				if (dataPointValue1 == null || dataPointValue2 == null)
					continue;

				XYChart.Data theData = new XYChart.Data(dataPointValue1, dataPointValue2);
				theData.setExtraValue(new ChartExtraValue(chartData.getDataPointLabel(), 0,
						chartData.getCharttableObject()));
				theData.setNode(userObjectPane(chartData.getCharttableObject(), false));

				chartLabelSet.add(chartData.getDataPointLabel());

				series.getData().add(theData);

			}
			toolTip.setStyle("-fx-font: 14 arial;  -fx-font-smoothing-type: lcd;");
			scatterChart.getData().add(series);
			if (nodecount > getMaxGraphItems() - 1)
				break;

		}

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

	/*
	 * implement the getting of lines that need to be exported.
	 */
	@Override
	public List<String> getLinesToExport()
	{

		List<String> returnList = new ArrayList<>();
		StringBuilder sb = new StringBuilder();
		XYChart xyChart = (XYChart) getChart();
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

			for (Object d : seriesData.getData())
			{
				sb.setLength(0);
				XYChart.Data xychartData = (XYChart.Data) d;
				ChartExtraValue extraValue = (ChartExtraValue) xychartData.getExtraValue();
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
				sb.append(extraValue.userData.toString());

				returnList.add(sb.toString());

			}
		}

		return returnList;
	}
}
