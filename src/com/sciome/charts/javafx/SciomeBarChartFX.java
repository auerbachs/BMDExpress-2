package com.sciome.charts.javafx;

import java.util.List;

import com.sciome.bmdexpress2.mvp.model.ChartKey;
import com.sciome.charts.SciomeBarChart;
import com.sciome.charts.SciomeChartListener;
import com.sciome.charts.data.ChartConfiguration;
import com.sciome.charts.data.ChartDataPack;

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
public class SciomeBarChartFX extends SciomeBarChart
{

	public SciomeBarChartFX(String title, List<ChartDataPack> chartDataPacks, ChartKey key,
			SciomeChartListener chartListener)
	{
		super(title, chartDataPacks, key, chartListener);

	}

	/*
	 * generate a bar chart
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected Chart generateChart(ChartKey[] keys, ChartConfiguration chartConfig)
	{
		ChartKey key = keys[0];
		Double max = getMaxMax(key);
		Double min = getMinMin(key);
		Double dataMin = min;

		CategoryAxis xAxis = new CategoryAxis();

		if (!getLockXAxis().isSelected())
		{
			if (chartConfig != null && chartConfig.getMaxY() != null && chartConfig.getMinY() != null)
			{
				max = chartConfig.getMaxY();
				min = chartConfig.getMinY();
			}
		}
		final Axis yAxis;
		yAxis = SciomeNumberAxisGeneratorFX.generateAxis(getLogYAxis().isSelected(), min, max, dataMin);

		xAxis.setLabel("Category");
		yAxis.setLabel(key.toString());
		BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);

		barChart.setTitle("Multiple Data Viewer: " + key);
		barChart.setBarGap(0.0);
		barChart.setCategoryGap(3.0);
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

	@Override
	protected Node getNode(String seriesName, String dataPointLabel, int seriesIndex)
	{
		NodeInformation nI = getNodeInformation(seriesName + dataPointLabel);

		return userObjectPane(nI.object, nI.invisible);
	}

	@Override
	protected void reactToChattingCharts()
	{
		// TODO Auto-generated method stub

	}

}
