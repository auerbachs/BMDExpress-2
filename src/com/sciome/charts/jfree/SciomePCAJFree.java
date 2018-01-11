package com.sciome.charts.jfree;

import java.awt.Color;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.controlsfx.control.RangeSlider;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.event.ChartChangeEvent;
import org.jfree.chart.event.ChartChangeListener;
import org.jfree.chart.fx.interaction.ChartMouseEventFX;
import org.jfree.chart.fx.interaction.ChartMouseListenerFX;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.Range;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;

import com.sciome.bmdexpress2.mvp.model.ChartKey;
import com.sciome.charts.SciomeChartListener;
import com.sciome.charts.SciomePCA;
import com.sciome.charts.data.ChartConfiguration;
import com.sciome.charts.data.ChartDataPack;
import com.sciome.charts.model.SciomeData;
import com.sciome.charts.model.SciomeSeries;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;

public class SciomePCAJFree extends SciomePCA
{
	private JFreeChart	chart;
	private double		lowX;
	private double		lowY;
	private double		highX;
	private double		highY;

	public SciomePCAJFree(String title, List<ChartDataPack> chartDataPacks, ChartKey key1, ChartKey key2,
			boolean allowXLogAxis, boolean allowYLogAxis, SciomeChartListener chartListener)
	{
		super(title, chartDataPacks, key1, key2, allowXLogAxis, allowYLogAxis, true, true, chartListener);

	}

	public SciomePCAJFree(String title, List<ChartDataPack> chartDataPacks, ChartKey key1, ChartKey key2,
			SciomeChartListener chartListener)
	{
		this(title, chartDataPacks, key1, key2, true, true, chartListener);
	}

	@Override
	protected Node generateChart(ChartKey[] keys, ChartConfiguration chartConfig)
	{
		ChartKey key1 = keys[0];
		ChartKey key2 = keys[1];
		Double max1 = getMaxMax(key1);
		Double min1 = getMinMin(key1);

		Double max2 = getMaxMax(key2);
		Double min2 = getMinMin(key2);

		DefaultXYDataset dataset = new DefaultXYDataset();

		for (SciomeSeries<Number, Number> series : getSeriesData())
		{
			double[] domains = new double[series.getData().size()];
			double[] ranges = new double[series.getData().size()];

			int i = 0;
			for (SciomeData<Number, Number> chartData : series.getData())
			{
				double domainvalue = chartData.getXValue().doubleValue();
				double rangevalue = chartData.getYValue().doubleValue();
				domains[i] = domainvalue;
				ranges[i++] = rangevalue;
			}
			dataset.addSeries(series.getName(), new double[][] { domains, ranges });
		}

		chart = ChartFactory.createScatterPlot(key1.toString() + " Vs. " + key2.toString(), key1.toString(),
				key2.toString(), dataset, PlotOrientation.VERTICAL, true, true, false);
		XYPlot plot = chart.getXYPlot();
		plot.setForegroundAlpha(0.1f);
		plot.setDomainPannable(true);
		plot.setRangePannable(true);
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
		setSliders(min1, max1, min2, max2);

		ChangeListener<Number> listener = new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue,
					Number newValue)
			{
				plot.getRangeAxis().setRange(newValue.doubleValue(), newValue.doubleValue());
			}
		};

		XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
		// Set tooltip string
		XYToolTipGenerator tooltipGenerator = new XYToolTipGenerator() {
			@Override
			public String generateToolTip(XYDataset dataset, int series, int item)
			{
				return ((ChartExtraValue) getSeriesData().get(series).getData().get(item)
						.getExtraValue()).userData.toString();
			}
		};
		renderer.setDefaultToolTipGenerator(tooltipGenerator);
		plot.setBackgroundPaint(Color.white);
		plot.setForegroundAlpha(0.5f);

		// Order the elements in the legend from smallest to largest
		LegendItemCollection collection = renderer.getLegendItems();
		TreeSet<Double> set = new TreeSet<Double>();
		LegendItemCollection items = new LegendItemCollection();
		for (int i = 0; i < collection.getItemCount(); i++)
		{
			set.add(Double.parseDouble(collection.get(i).getLabel()));
		}
		Iterator<Double> it = set.iterator();
		while (it.hasNext())
		{
			Double first = it.next();
			for (int j = 0; j < collection.getItemCount(); j++)
			{
				if (collection.get(j).getLabel().equals("" + first))
				{
					items.add(collection.get(j));
				}
			}
		}
		plot.setFixedLegendItems(items);

		chart.addChangeListener(new ChartChangeListener() {
			@Override
			public void chartChanged(ChartChangeEvent event)
			{
				if (event.getChart() != null)
				{
					Range xAxis = event.getChart().getXYPlot().getDomainAxis().getRange();
					Range yAxis = event.getChart().getXYPlot().getRangeAxis().getRange();
					((RangeSlider)gethSlider()).setLowValue(xAxis.getLowerBound());
					((RangeSlider)gethSlider()).setHighValue(xAxis.getUpperBound());
					((RangeSlider)getvSlider()).setLowValue(yAxis.getLowerBound());
					((RangeSlider)getvSlider()).setHighValue(yAxis.getUpperBound());
				}
			}
		});

		// Create Panel
		SciomeChartViewer chartView = new SciomeChartViewer(chart);

		// Add plot point clicking interaction
		chartView.addChartMouseListener(new ChartMouseListenerFX() {

			@Override
			public void chartMouseClicked(ChartMouseEventFX e)
			{
				if (e.getEntity() != null && e.getEntity().getToolTipText() != null // Check to see if an
																					// entity was clicked
						&& e.getTrigger().getButton().equals(MouseButton.PRIMARY)
						&& e.getTrigger().isShiftDown()) // Check to see if it was
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

	@Override
	public void recieveChatFromOtherChart(List<Object> conversation)
	{
		// TODO Auto-generated method stub

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

	private void setSliders(double minX, double maxX, double minY, double maxY)
	{
		lowX = minX;
		highX = maxX;
		lowY = minY;
		highY = maxY;

		RangeSlider hSlider = new RangeSlider(minX, maxX, minX, maxX);
		RangeSlider vSlider = new RangeSlider(minY, maxY, minY, maxY);
		vSlider.setOrientation(Orientation.VERTICAL);
		hSlider.lowValueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number oldValue, Number newValue)
			{
				lowX = newValue.doubleValue();
				if (lowX != highX)
					chart.getXYPlot().getDomainAxis().setRange(new Range(lowX, highX));
			}
		});

		hSlider.highValueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number oldValue, Number newValue)
			{
				highX = newValue.doubleValue();
				if (lowX != highX)
					chart.getXYPlot().getDomainAxis().setRange(new Range(lowX, highX));
			}
		});

		vSlider.lowValueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number oldValue, Number newValue)
			{
				lowY = newValue.doubleValue();
				if (lowY != highY)
					chart.getXYPlot().getRangeAxis().setRange(new Range(lowY, highY));
			}
		});

		vSlider.highValueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number oldValue, Number newValue)
			{
				highY = newValue.doubleValue();
				if (lowY != highY)
					chart.getXYPlot().getRangeAxis().setRange(new Range(lowY, highY));
			}
		});

		sethSlider(hSlider);
		setvSlider(vSlider);
	}

}
