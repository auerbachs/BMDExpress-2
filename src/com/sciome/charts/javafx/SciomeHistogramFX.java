package com.sciome.charts.javafx;

import java.util.List;
import java.util.Set;

import com.sciome.bmdexpress2.mvp.model.ChartKey;
import com.sciome.charts.SciomeChartListener;
import com.sciome.charts.SciomeHistogram;
import com.sciome.charts.data.ChartConfiguration;
import com.sciome.charts.data.ChartDataPack;
import com.sciome.charts.export.ChartDataExporter;
import com.sciome.charts.model.SciomeData;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.Chart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.Glow;
import javafx.scene.layout.StackPane;

/*
 * 
 */
public class SciomeHistogramFX extends SciomeHistogram implements ChartDataExporter
{

	public SciomeHistogramFX(String title, List<ChartDataPack> chartDataPacks, ChartKey key,
			Double bucketsize, SciomeChartListener chartListener)
	{
		super(title, chartDataPacks, key, bucketsize, false, false, chartListener);

	}

	/*
	 * generate a histogram bar chart
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected Chart generateChart(ChartKey[] keys, ChartConfiguration chartConfig)
	{
		ChartKey key = keys[0];

		final Axis xAxis = new CategoryAxis();

		final Axis yAxis = SciomeNumberAxisGeneratorFX.generateAxis(getLogYAxis().isSelected(), null, null,
				null);

		xAxis.setLabel(key.toString());
		yAxis.setLabel("Count");

		XYChart.Series<String, Number> series1 = new XYChart.Series<>();
		series1.setName(key.toString());

		for (Object sciomeDataObj : getSeriesData().get(0).getData())
		{
			SciomeData<String, Number> sciomeData = (SciomeData<String, Number>) sciomeDataObj;
			XYChart.Data<String, Number> data = new XYChart.Data<>(sciomeData.getXValue(),
					sciomeData.getYValue().doubleValue());
			data.setNode(userObjectPane(sciomeData.getExtraValue()));
			series1.getData().add(data);
		}

		BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
		barChart.setLegendVisible(false);
		barChart.setCategoryGap(0.0);
		barChart.setTitle(key + " Histogram");
		barChart.setBarGap(0.0);

		barChart.getData().add(series1);

		for (Series<String, Number> seriesData : barChart.getData())
		{
			for (final XYChart.Data<String, Number> data : seriesData.getData())
			{
				Node node = data.getNode();
				Tooltip toolTip = new Tooltip("");
				toolTip.setStyle("-fx-font: 14 arial;  -fx-font-smoothing-type: lcd;");;
				Tooltip.install(node, toolTip);

				node.setOnMouseEntered(new EventHandler<javafx.scene.input.MouseEvent>() {
					@Override
					public void handle(javafx.scene.input.MouseEvent arg0)
					{
						node.setEffect(new Glow());

						List<Object> objects = (List<Object>) node.getUserData();
						if (objects != null)
							toolTip.setText(String.valueOf(joinObjects(objects, MAX_TOOL_TIP_SHOWS)));

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

						List<Object> objects = (List<Object>) node.getUserData();
						if (objects != null)
							showObjectText(String.valueOf(joinAllObjects(objects)));

						node.setEffect(null);
					}
				});

			}
		}

		return barChart;
	}

	private StackPane userObjectPane(Object object)
	{

		StackPane returnPane = new StackPane();
		returnPane.setUserData(object);
		return returnPane;
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
