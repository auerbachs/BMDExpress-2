package com.sciome.charts.jfree;

import java.awt.Color;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.fx.ChartViewer;
import org.jfree.chart.fx.interaction.ChartMouseEventFX;
import org.jfree.chart.fx.interaction.ChartMouseListenerFX;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBubbleRenderer;
import org.jfree.data.xy.DefaultXYZDataset;
import org.jfree.data.xy.XYDataset;

import com.sciome.bmdexpress2.mvp.model.ChartKey;
import com.sciome.charts.SciomeBubbleChart;
import com.sciome.charts.SciomeChartListener;
import com.sciome.charts.data.ChartConfiguration;
import com.sciome.charts.data.ChartDataPack;
import com.sciome.charts.model.SciomeData;
import com.sciome.charts.model.SciomeSeries;

import javafx.scene.input.MouseButton;

public class SciomeBubbleChartJFree extends SciomeBubbleChart
{

	public SciomeBubbleChartJFree(String title, List<ChartDataPack> chartDataPacks, ChartKey key1,
			ChartKey key2, ChartKey key3, SciomeChartListener chartListener)
	{
		super(title, chartDataPacks, key1, key2, key3, chartListener);
		showChart();
	}

	@Override
	protected ChartViewer generateChart(ChartKey[] keys, ChartConfiguration chartConfig)
	{
		ChartKey key1 = keys[0];
		ChartKey key2 = keys[1];
		ChartKey key3 = keys[2];
		Double max1 = getMaxMax(key1);
		Double max2 = getMaxMax(key2);
		Double min1 = getMinMin(key1);
		Double min2 = getMinMin(key2);

		Double scaleValue = max2 / max1;

		DefaultXYZDataset dataset = new DefaultXYZDataset();

		for (SciomeSeries<Number, Number> series : getSeriesData())
		{
			double[] domains = new double[series.getData().size()];
			double[] ranges = new double[series.getData().size()];
			double[] bubbles = new double[series.getData().size()];
			int i = 0;
			for (SciomeData<Number, Number> chartData : series.getData())
			{
				double domainvalue = chartData.getXValue().doubleValue();
				double rangevalue = chartData.getYValue().doubleValue();
				double bubblesize = ((BubbleChartExtraData) chartData.getExtraValue()).bubbleSize;
				domains[i] = domainvalue;
				ranges[i] = rangevalue;
				bubbles[i++] = bubblesize * scaleValue;

			}
			dataset.addSeries(series.getName(), new double[][] { domains, ranges, bubbles });
		}

		// Create chart
		JFreeChart chart = ChartFactory.createBubbleChart(
				key1.toString() + " Vs. " + key2.toString() + ": Bubble Size=" + key3.toString(),
				key1.toString(), key2.toString(), dataset, PlotOrientation.VERTICAL, true, true, false);

		// Set plot parameters
		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setForegroundAlpha(0.1f);
		plot.setDomainPannable(false);
		plot.setRangePannable(false);
		plot.setDomainAxis(
				SciomeNumberAxisGeneratorJFree.generateAxis(getLogXAxis().isSelected(), key1.toString()));
		plot.setRangeAxis(
				SciomeNumberAxisGeneratorJFree.generateAxis(getLogYAxis().isSelected(), key2.toString()));

		// Only want to zoom in if we any values have been set in chartConfig
		if (chartConfig != null)
		{
			// Find the values for the min and max values for x and y
			if (chartConfig.getMaxX() != null && chartConfig.getMinX() != null)
			{
				max1 = chartConfig.getMaxX();
				min1 = chartConfig.getMinX();
			}

			if (chartConfig.getMaxY() != null && chartConfig.getMinY() != null)
			{
				max2 = chartConfig.getMaxY();
				min2 = chartConfig.getMinY();
			}
			if (min1.equals(max1))
			{
				min1 -= 1;
			}
			else if (min1 > max1)
			{
				min1 = 0.0;
				max1 = 0.1;
			}
			if (min2.equals(max2))
			{
				min2 -= 1;
			}
			else if (min2 > max2)
			{
				min2 = 0.0;
				max2 = 0.1;
			}

			// Set the domain and range based on these x and y values
			NumberAxis range = (NumberAxis) plot.getRangeAxis();
			NumberAxis domain = (NumberAxis) plot.getDomainAxis();
			domain.setRange(min1, max1);
			range.setRange(min2, max2);
		}

		XYBubbleRenderer renderer = ((XYBubbleRenderer) plot.getRenderer());

		// Set tooltip string
		XYToolTipGenerator tooltipGenerator = new XYToolTipGenerator() {
			@Override
			public String generateToolTip(XYDataset dataset, int series, int item)
			{
				return ((BubbleChartExtraData) getSeriesData().get(series).getData().get(item)
						.getExtraValue()).userData.toString();
			}
		};
		renderer.setDefaultToolTipGenerator(tooltipGenerator);
		plot.setBackgroundPaint(Color.white);
		chart.getPlot().setForegroundAlpha(.5f);

		// Create Panel
		SciomeChartViewer chartView = new SciomeChartViewer(chart);
		// Add plot point clicking interaction
		chartView.addChartMouseListener(new ChartMouseListenerFX() {

			@Override
			public void chartMouseClicked(ChartMouseEventFX e)
			{
				System.out.println("X: " + e.getTrigger().getX());
				System.out.println("Y: " + e.getTrigger().getY());
				if (e.getEntity() != null && e.getEntity().getToolTipText() != null // Check to see if an
																					// entity was clicked
						&& e.getTrigger().getButton().equals(MouseButton.PRIMARY)) // Check to see if it was
																					// the left mouse button
																					// clicked
					showObjectText(e.getEntity().getToolTipText());
			}

			@Override
			public void chartMouseMoved(ChartMouseEventFX e)
			{
				// ignore for now
			}
		});

		return chartView;
	}

}
