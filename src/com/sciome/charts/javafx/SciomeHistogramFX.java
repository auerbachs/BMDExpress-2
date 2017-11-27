package com.sciome.charts.javafx;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.sciome.charts.SciomeChartListener;
import com.sciome.charts.SciomeHistogram;
import com.sciome.charts.data.ChartConfiguration;
import com.sciome.charts.data.ChartData;
import com.sciome.charts.data.ChartDataPack;
import com.sciome.charts.export.ChartDataExporter;
import com.sciome.charts.utils.SciomeNumberAxisGenerator;

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

	public SciomeHistogramFX(String title, List<ChartDataPack> chartDataPacks, String key, Double bucketsize,
			SciomeChartListener chartListener)
	{
		super(title, chartDataPacks, key, bucketsize, chartListener);

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

	private StackPane userObjectPane(Object object)
	{

		StackPane returnPane = new StackPane();
		returnPane.setUserData(object);
		return returnPane;
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

		sb.append("x");
		sb.append("\t");
		sb.append("y");
		sb.append("\t");
		sb.append("components delimited by ///");
		returnList.add(sb.toString());
		for (XYChart.Series seriesData : data)
		{

			for (Object d : seriesData.getData())
			{
				XYChart.Data xychartData = (XYChart.Data) d;

				sb.setLength(0);
				String X = (String) xychartData.getXValue();

				Double Y = (Double) xychartData.getYValue();

				Node node = xychartData.getNode();
				List<Object> objects = (List) node.getUserData();

				StringBuilder components = new StringBuilder();
				for (Object obj : objects)
				{
					if (components.length() > 0)
						components.append("///");
					components.append(obj.toString());
				}

				sb.append(X);
				sb.append("\t");
				sb.append(Y);
				sb.append("\t");

				sb.append(components);

				returnList.add(sb.toString());

			}
		}

		return returnList;

	}

}
