package com.sciome.charts.jfree;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.AbstractXYAnnotation;
import org.jfree.chart.annotations.XYDrawableAnnotation;
import org.jfree.chart.annotations.XYPointerAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.block.ColorBlock;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.fx.ChartViewer;
import org.jfree.chart.fx.interaction.ChartMouseEventFX;
import org.jfree.chart.fx.interaction.ChartMouseListenerFX;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBubbleRenderer;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.data.xy.DefaultXYZDataset;
import org.jfree.data.xy.XYDataset;

import com.sciome.bmdexpress2.mvp.model.ChartKey;
import com.sciome.bmdexpress2.mvp.model.IMarkable;
import com.sciome.charts.SciomeBubbleChart;
import com.sciome.charts.SciomeChartListener;
import com.sciome.charts.data.ChartConfiguration;
import com.sciome.charts.data.ChartDataPack;
import com.sciome.charts.model.SciomeData;
import com.sciome.charts.model.SciomeSeries;

import javafx.scene.input.MouseButton;

public class SciomeBubbleChartJFree extends SciomeBubbleChart
{

	private List<AbstractXYAnnotation>	chattingAnnotations	= new ArrayList<>();
	private List<AbstractXYAnnotation>	markedAnnotations	= new ArrayList<>();
	private JFreeChart					chart;

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
		chart = ChartFactory.createBubbleChart(
				key1.toString() + " Vs. " + key2.toString() + ": Bubble Size=" + key3.toString(),
				key1.toString(), key2.toString(), dataset, PlotOrientation.VERTICAL, true, true, false);

		// Set plot parameters
		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setForegroundAlpha(0.8f);
		plot.setDomainPannable(false);
		plot.setRangePannable(false);
		plot.setDomainAxis(
				SciomeNumberAxisGeneratorJFree.generateAxis(getLogXAxis().isSelected(), key1.toString()));
		plot.setRangeAxis(
				SciomeNumberAxisGeneratorJFree.generateAxis(getLogYAxis().isSelected(), key2.toString()));
		// add the annotations (if they exist). This is mainly for when the user
		// changes the axis or chartconfiguration
		if (chattingAnnotations != null)
			for (AbstractXYAnnotation ann : chattingAnnotations)
				plot.addAnnotation(ann);
		if (markedAnnotations != null)
			for (AbstractXYAnnotation ann : markedAnnotations)
				plot.addAnnotation(ann);

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
		chart.getPlot().setForegroundAlpha(.8f);

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
				{
					int seriesIndex = ((XYItemEntity) e.getEntity()).getSeriesIndex();
					int item = ((XYItemEntity) e.getEntity()).getItem();

					// get the object associated with with the click and post it to the other charts
					// so they can highlight it.
					Object userData = ((BubbleChartExtraData) getSeriesData().get(seriesIndex).getData()
							.get(item).getExtraValue()).userData;
					postObjectsForChattingCharts(Arrays.asList(userData));
					if (e.getTrigger().getClickCount() == 2)
						showObjectText(e.getEntity().getToolTipText());

				}
				else
					postObjectsForChattingCharts(new ArrayList<>());
			}

			@Override
			public void chartMouseMoved(ChartMouseEventFX e)
			{
				// ignore for now
			}
		});

		return chartView;
	}

	@Override
	protected void reactToChattingCharts()
	{
		for (AbstractXYAnnotation annotation : chattingAnnotations)
			((XYPlot) chart.getXYPlot()).removeAnnotation(annotation, false);
		Set<String> conversationalSet = new HashSet<>();
		for (Object obj : getConversationalObjects())
			conversationalSet.add(obj.toString().toLowerCase());
		chattingAnnotations.clear();
		for (SciomeSeries<Number, Number> series : getSeriesData())
		{
			for (SciomeData<Number, Number> chartData : series.getData())
			{
				if (conversationalSet.contains(
						((BubbleChartExtraData) chartData.getExtraValue()).userData.toString().toLowerCase()))
				{
					XYDrawableAnnotation ann = new XYDrawableAnnotation(chartData.getXValue().doubleValue(),
							chartData.getYValue().doubleValue(), 10, 10, new ColorBlock(Color.pink, 10, 10));
					XYDrawableAnnotation ann2 = new XYDrawableAnnotation(chartData.getXValue().doubleValue(),
							chartData.getYValue().doubleValue(), 12, 12, new ColorBlock(Color.BLACK, 12, 12));

					// ann2 will give us black outline
					chattingAnnotations.add(ann2);
					chattingAnnotations.add(ann);
					((XYPlot) chart.getXYPlot()).addAnnotation(ann2, false);
					((XYPlot) chart.getXYPlot()).addAnnotation(ann, false);
					if (((BubbleChartExtraData) chartData.getExtraValue()).userData instanceof IMarkable)
					{
						IMarkable markable = (IMarkable) ((BubbleChartExtraData) chartData
								.getExtraValue()).userData;
						XYPointerAnnotation labelann = new XYPointerAnnotation(markable.getMarkableLabel(),
								chartData.getXValue().doubleValue(), chartData.getYValue().doubleValue(),
								Math.PI * 4 / 3);
						labelann.setBaseRadius(40.0);
						labelann.setLabelOffset(5.0);
						labelann.setBackgroundPaint(Color.pink);
						labelann.setOutlineVisible(true);
						labelann.setFont(new java.awt.Font("Courier New", java.awt.Font.PLAIN, 12));
						labelann.setTipRadius(5);
						labelann.setTextAnchor(TextAnchor.HALF_ASCENT_RIGHT);
						chattingAnnotations.add(labelann);
						((XYPlot) chart.getXYPlot()).addAnnotation(labelann, false);
					}
				}
			}
		}
		chart.fireChartChanged();
	}

	@Override
	public void markData(Set<String> markings)
	{
		for (AbstractXYAnnotation annotation : markedAnnotations)
			((XYPlot) chart.getXYPlot()).removeAnnotation(annotation, false);

		for (SciomeSeries<Number, Number> series : getSeriesData())
		{
			for (SciomeData<Number, Number> chartData : series.getData())
			{
				if (((BubbleChartExtraData) chartData.getExtraValue()).userData instanceof IMarkable)
				{
					IMarkable markable = (IMarkable) ((BubbleChartExtraData) chartData
							.getExtraValue()).userData;

					if (!dataIsMarked(markings, markable.getMarkableKeys()))
						continue;
					XYDrawableAnnotation ann = new XYDrawableAnnotation(chartData.getXValue().doubleValue(),
							chartData.getYValue().doubleValue(), 15, 15,
							new ColorBlock(markable.getMarkableColor(), 15, 15));
					XYDrawableAnnotation ann2 = new XYDrawableAnnotation(chartData.getXValue().doubleValue(),
							chartData.getYValue().doubleValue(), 17, 17, new ColorBlock(Color.BLACK, 17, 17));

					XYPointerAnnotation labelann = new XYPointerAnnotation(markable.getMarkableLabel(),
							chartData.getXValue().doubleValue(), chartData.getYValue().doubleValue(),
							Math.PI * 4 / 3);
					labelann.setBaseRadius(40.0);
					labelann.setLabelOffset(5.0);
					labelann.setBackgroundPaint(Color.yellow);
					labelann.setOutlineVisible(true);
					labelann.setFont(new java.awt.Font("Courier New", java.awt.Font.PLAIN, 12));
					labelann.setTipRadius(5);
					labelann.setTextAnchor(TextAnchor.HALF_ASCENT_RIGHT);

					// ann2 will give us black outline
					markedAnnotations.add(ann2);
					markedAnnotations.add(ann);
					markedAnnotations.add(labelann);
					((XYPlot) chart.getXYPlot()).addAnnotation(ann2, false);
					((XYPlot) chart.getXYPlot()).addAnnotation(ann, false);
					((XYPlot) chart.getXYPlot()).addAnnotation(labelann, false);
				}
			}
		}
		chart.fireChartChanged();

	}

	private boolean dataIsMarked(Set<String> markings, Set<String> data)
	{
		for (String d : data)
			if (markings.contains(d))
				return true;
		return false;
	}

}
