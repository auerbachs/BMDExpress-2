package com.sciome.charts.jfree;

import java.awt.Color;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.fx.ChartViewer;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBubbleRenderer;
import org.jfree.data.xy.DefaultXYZDataset;

import com.sciome.charts.SciomeChartBase;
import com.sciome.charts.SciomeChartListener;
import com.sciome.charts.data.ChartConfiguration;
import com.sciome.charts.data.ChartData;
import com.sciome.charts.data.ChartDataPack;

import javafx.scene.layout.HBox;

public class JFreeBubbleChart extends SciomeChartBase
{

	private static Double BUBBLE_SCALE_FRACTION = 8.0;

	public JFreeBubbleChart(String title, List<ChartDataPack> chartDataPacks, String key1, String key2,
			String key3, SciomeChartListener chartListener)
	{
		super(title, chartDataPacks, chartListener);
		HBox hbox = new HBox();
		chartableKeys = new String[] { key1, key2, key3 };
		showChart();
	}

	@Override
	protected ChartViewer generateChart(String[] keys, ChartConfiguration chartConfig)
	{

		String key1 = keys[0];
		String key2 = keys[1];
		String key3 = keys[2];
		Double max1 = getMaxMax(key1);
		Double max2 = getMaxMax(key2);
		Double max3 = getMaxMax(key3);

		Double min1 = getMinMin(key1);
		Double min2 = getMinMin(key2);

		Double scaleValue = max2 / max1;
		Double bubbleScale = (1.0 / BUBBLE_SCALE_FRACTION) / (max3 / max1);

		DefaultXYZDataset dataset = new DefaultXYZDataset();

		for (ChartDataPack chartDataPack : chartDataPacks)
		{
			int count = 0;
			for (ChartData chartData : chartDataPack.getChartData())
				if (chartData.getDataPoints().containsKey(key1) && chartData.getDataPoints().containsKey(key2)
						&& chartData.getDataPoints().containsKey(key3))
					count++;

			double[] domains = new double[count];
			double[] ranges = new double[count];
			double[] bubbles = new double[count];
			int i = 0;
			for (ChartData chartData : chartDataPack.getChartData())
			{

				if (chartData.getDataPoints().containsKey(key1) && chartData.getDataPoints().containsKey(key2)
						&& chartData.getDataPoints().containsKey(key3))
				{
					double domainvalue = ((Double) chartData.getDataPoints().get(key1)).doubleValue();
					double rangevalue = ((Double) chartData.getDataPoints().get(key2)).doubleValue();
					double bubblesize = Math
							.log(((Double) chartData.getDataPoints().get(key3)).doubleValue());
					domains[i] = domainvalue;
					ranges[i] = rangevalue;
					bubbles[i++] = bubblesize;
				}
			}
			dataset.addSeries(chartDataPack.getName(), new double[][] { domains, ranges, bubbles });

		}

		// Create chart
		JFreeChart chart = ChartFactory.createBubbleChart(key1 + " Vs. " + key2 + ": Bubble Size=" + key3,
				key1, key2, dataset, PlotOrientation.VERTICAL, true, true, false);
		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setForegroundAlpha(0.1f);
		plot.setDomainPannable(true);
		plot.setRangePannable(true);
		NumberAxis domain = (NumberAxis) plot.getDomainAxis();
		if (min1.equals(max1))
		{
			min1 -= 1;
		}
		else if (min1 > max1)
		{
			min1 = 0.0;
			max1 = 0.1;
		}

		domain.setRange(min1.doubleValue(), max1.doubleValue());

		// Set range for Y-Axis
		NumberAxis range = (NumberAxis) plot.getRangeAxis();
		if (min2.equals(max2))
		{
			min2 -= 1;
		}
		else if (min2 > max2)
		{
			min2 = 0.0;
			max2 = 0.1;
		}
		range.setRange(min2.doubleValue(), max2.doubleValue());

		XYBubbleRenderer renderer = ((XYBubbleRenderer) plot.getRenderer());

		renderer.setSeriesPaint(0, new Color(0.0f, 0.0f, .82f, .3f));
		plot.setBackgroundPaint(Color.white);
		chart.getPlot().setForegroundAlpha(0.1f);

		// Create Panel
		ChartViewer chartView = new ChartViewer(chart);

		// LogarithmicAxis yAxis = new LogarithmicAxis();

		domain.setLabel(key1);
		range.setLabel(key2);

		return chartView;
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
