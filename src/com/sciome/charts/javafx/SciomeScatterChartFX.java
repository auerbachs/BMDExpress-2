package com.sciome.charts.javafx;

import java.util.List;
import java.util.Set;

import com.sciome.bmdexpress2.mvp.model.ChartKey;
import com.sciome.charts.SciomeChartListener;
import com.sciome.charts.SciomeScatterChart;
import com.sciome.charts.data.ChartConfiguration;
import com.sciome.charts.data.ChartDataPack;
import com.sciome.charts.export.ChartDataExporter;
import com.sciome.charts.model.SciomeData;
import com.sciome.charts.model.SciomeSeries;

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
	public SciomeScatterChartFX(String title, List<ChartDataPack> chartDataPacks, ChartKey key1,
			ChartKey key2, boolean allowXLogAxis, boolean allowYLogAxis, SciomeChartListener chartListener)
	{
		super(title, chartDataPacks, key1, key2, allowXLogAxis, allowYLogAxis, chartListener);
	}

	public SciomeScatterChartFX(String title, List<ChartDataPack> chartDataPacks, ChartKey key1,
			ChartKey key2, SciomeChartListener chartListener)
	{
		this(title, chartDataPacks, key1, key2, true, true, chartListener);
	}

	/*
	 * generate a histogram bar chart
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected Chart generateChart(ChartKey[] keys, ChartConfiguration chartConfig)
	{
		ChartKey key1 = keys[0];
		ChartKey key2 = keys[1];
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
		final Axis xAxis = SciomeNumberAxisGeneratorFX.generateAxis(getLogXAxis().isSelected(), min1, max1,
				dataMin1);
		final Axis yAxis = SciomeNumberAxisGeneratorFX.generateAxis(getLogYAxis().isSelected(), min2, max2,
				dataMin2);

		xAxis.setLabel(key1.toString());
		yAxis.setLabel(key2.toString());

		ScatterChart<Number, Number> scatterChart = new ScatterChart<>(xAxis, yAxis);

		scatterChart.setTitle(key1 + " Vs. " + key2);

		for (SciomeSeries<Number, Number> sciomeSeriesData : getSeriesData())
		{
			XYChart.Series<Number, Number> series = new XYChart.Series<>();
			series.setName(sciomeSeriesData.getName());

			for (Object chartDataObj : sciomeSeriesData.getData())
			{
				SciomeData<Number, Number> chartData = (SciomeData<Number, Number>) chartDataObj;

				XYChart.Data<Number, Number> theData = new XYChart.Data<>(chartData.getXValue(),
						chartData.getYValue());
				theData.setExtraValue(chartData.getExtraValue());
				theData.setNode(
						userObjectPane(((ChartExtraValue) chartData.getExtraValue()).userData, false));
				series.getData().add(theData);
			}
			toolTip.setStyle("-fx-font: 14 arial;  -fx-font-smoothing-type: lcd;");
			scatterChart.getData().add(series);

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
