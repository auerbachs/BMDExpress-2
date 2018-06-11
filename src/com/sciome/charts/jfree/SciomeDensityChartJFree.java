package com.sciome.charts.jfree;

import java.awt.Color;
import java.util.List;
import java.util.Set;

import org.controlsfx.control.RangeSlider;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.event.ChartChangeEvent;
import org.jfree.chart.event.ChartChangeListener;
import org.jfree.chart.fx.ChartViewer;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.data.Range;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;

import com.sciome.bmdexpress2.mvp.model.ChartKey;
import com.sciome.charts.SciomeChartListener;
import com.sciome.charts.SciomeDensityChart;
import com.sciome.charts.data.ChartConfiguration;
import com.sciome.charts.data.ChartDataPack;
import com.sciome.charts.model.SciomeData;
import com.sciome.charts.model.SciomeSeries;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class SciomeDensityChartJFree extends SciomeDensityChart {
	private JFreeChart	chart;
	private double		lowX;
	private double		highX;
	
	public SciomeDensityChartJFree(String title, List<ChartDataPack> chartDataPacks, ChartKey key,
			SciomeChartListener chartListener) {
		super(title, chartDataPacks, key, chartListener);
		showLogAxes(false, true, false, false);

		getLogYAxis().selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val)
			{
				showChart();
			}
		});
	}
	
	protected ChartViewer generateChart(ChartKey[] keys, ChartConfiguration chartConfig)
	{
		ChartKey key = keys[0];

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
				ranges[i] = rangevalue;
				i++;
			}
			dataset.addSeries(series.getName(), new double[][] { domains, ranges });
		}

		//Set axis
		ValueAxis xAxis = new NumberAxis();
		ValueAxis yAxis = SciomeNumberAxisGeneratorJFree.generateAxis(getLogYAxis().isSelected(), "Density");
		
		XYSplineRenderer renderer = new XYSplineRenderer();
		
		// Set tooltip string
		XYToolTipGenerator tooltipGenerator = new XYToolTipGenerator() {
			@Override
			public String generateToolTip(XYDataset dataset, int series, int item)
			{
				return getSeriesData().get(series).getData().get(item).getExtraValue().toString();
			}
		};
		renderer.setDefaultToolTipGenerator(tooltipGenerator);
		renderer.setSeriesFillPaint(0, Color.white);
		renderer.setDefaultOutlinePaint(Color.black);
		for(int i = 0; i < getSeriesData().size(); i++)
		{
			renderer.setSeriesShapesVisible(i, false);
		}
		
		// Set plot parameters
		XYPlot plot = new XYPlot(dataset, xAxis, yAxis, renderer);
		plot.setForegroundAlpha(0.8f);
		plot.setDomainPannable(false);
		plot.setRangePannable(false);
		plot.setBackgroundPaint(Color.white);
		
		double min = getMinMin(key);
		double max = getMaxMax(key);
		plot.getDomainAxis().setRange(new Range(min, max));
		setSliders(min, max);
		
		// Create chart
		chart = new JFreeChart("Intensity Histogram", JFreeChart.DEFAULT_TITLE_FONT, plot, true);
		chart.getPlot().setForegroundAlpha(.8f);

		chart.addChangeListener(new ChartChangeListener() {
			@Override
			public void chartChanged(ChartChangeEvent event)
			{
				if (event.getChart() != null)
				{
					Range xAxis = event.getChart().getXYPlot().getDomainAxis().getRange();
					((RangeSlider) gethSlider()).setLowValue(xAxis.getLowerBound());
					((RangeSlider) gethSlider()).setHighValue(xAxis.getUpperBound());
				}
			}
		});

		// Create Panel
		SciomeChartViewer chartView = new SciomeChartViewer(chart);

		return chartView;
	}

	@Override
	public List<String> getLinesToExport() {
		return null;
	}

	@Override
	public void reactToChattingCharts() {
		
	}

	@Override
	public void markData(Set<String> markings) {
		
	}
	
	private void setSliders(double minX, double maxX)
	{
		lowX = minX;
		highX = maxX;

		RangeSlider hSlider = new RangeSlider(minX, maxX, minX, maxX);
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

		sethSlider(hSlider);
	}
}
