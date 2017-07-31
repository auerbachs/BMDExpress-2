package com.sciome.charts.javafx;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.sciome.charts.data.ChartConfiguration;
import com.sciome.charts.data.ChartData;
import com.sciome.charts.data.ChartDataPack;

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
public class SciomeHistogram extends SciomeChartBase
{

	private Double		bucketsize			= 20.0;
	private final int	MAX_TOOL_TIP_SHOWS	= 20;

	public SciomeHistogram(String title, List<ChartDataPack> chartDataPacks, String key, Double bucketsize,
			SciomeChartListener chartListener)
	{
		super(title, chartDataPacks, chartListener);
		this.bucketsize = bucketsize;
		chartableKeys = new String[] { key };
		showChart();

	}

	/*
	 * generate a histogram bar chart
	 */
	@Override
	protected Chart generateChart(String[] keys, ChartConfiguration chartConfig)
	{
		String key = keys[0];
		Double max = getMaxMax(key);
		Double min = getMinMin(key);

		final Axis xAxis = new CategoryAxis();

		final Axis yAxis = SciomeNumberAxisGenerator.generateAxis(logYAxis.isSelected(), null, null, null);

		xAxis.setLabel(key);
		yAxis.setLabel("Count");

		Double bucketSize = (max - min) / bucketsize;
		List<Double> dataPointCounts = new ArrayList<Double>(bucketsize.intValue());

		List<List<Object>> bucketObjects = new ArrayList<>();
		for (int i = 0; i <= bucketsize.intValue(); i++)
		{
			bucketObjects.add(new ArrayList<>());
		}

		List<Double> xDataPoints = new ArrayList<Double>(bucketsize.intValue());
		for (int i = 0; i <= bucketsize.intValue(); i++)
		{
			xDataPoints.add(min + bucketSize * i);
			dataPointCounts.add(0.0);
		}

		// Now put the data in a bucket
		for (ChartDataPack chartDataPack : chartDataPacks)
		{
			for (ChartData chartData : chartDataPack.getChartData())
			{
				if (cancel)
					return null;
				Double dataPoint = (Double) chartData.getDataPoints().get(key);
				if (dataPoint == null)
					continue;

				// which bin?
				for (int i = 0; i <= bucketsize.intValue(); i++)
				{
					if (dataPoint <= min + bucketSize * i)
					{
						dataPointCounts.set(i, dataPointCounts.get(i) + 1.0);
						bucketObjects.get(i).add(chartData.getCharttableObject());
						break;
					}
				}
			}
		}

		XYChart.Series<String, Number> series1 = new XYChart.Series<>();
		series1.setName(key);

		DecimalFormat df = new DecimalFormat("#.###");
		for (int i = 0; i <= bucketsize.intValue(); i++)
		{
			XYChart.Data data = new XYChart.Data(df.format(xDataPoints.get(i)), dataPointCounts.get(i));

			data.setNode(userObjectPane(bucketObjects.get(i)));
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
			for (final XYChart.Data data : seriesData.getData())
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

						List<Object> objects = (List) node.getUserData();
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

						List<Object> objects = (List) node.getUserData();
						if (objects != null)
							showObjectText(String.valueOf(joinAllObjects(objects)));

						node.setEffect(null);
					}
				});

			}
		}

		return barChart;
	}

	private String joinAllObjects(List<Object> objects)
	{
		return joinObjects(objects, 2000000000);

	}

	private String joinObjects(List<Object> objects, int max)
	{
		StringBuilder sb = new StringBuilder();

		int i = 0;
		for (Object obj : objects)
		{
			sb.append(obj);
			sb.append("\n");
			if (i >= max)
			{
				sb.append("....");
				break;
			}
			i++;
		}
		return sb.toString();
	}

	private StackPane userObjectPane(Object object)
	{

		StackPane returnPane = new StackPane();
		returnPane.setUserData(object);
		return returnPane;
	}

	@Override
	protected boolean isXAxisDefineable()
	{
		return false;
	}

	@Override
	protected boolean isYAxisDefineable()
	{
		return false;
	}

	@Override
	protected void redrawChart()
	{
		showChart();

	}
}
