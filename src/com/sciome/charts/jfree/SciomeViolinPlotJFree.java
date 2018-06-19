package com.sciome.charts.jfree;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.plot.DrawingSupplier;
import org.jfree.data.Range;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.SlidingCategoryDataset;

import com.sciome.bmdexpress2.mvp.model.ChartKey;
import com.sciome.bmdexpress2.util.ShapeCreator;
import com.sciome.charts.SciomeChartBase;
import com.sciome.charts.SciomeChartListener;
import com.sciome.charts.data.ChartConfiguration;
import com.sciome.charts.data.ChartData;
import com.sciome.charts.data.ChartDataPack;
import com.sciome.charts.jfree.violin.ViolinCategoryDataset;
import com.sciome.charts.jfree.violin.ViolinItem;
import com.sciome.charts.jfree.violin.ViolinRenderer;
import com.sciome.charts.model.SciomeData;
import com.sciome.charts.model.SciomeSeries;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.Slider;

public class SciomeViolinPlotJFree extends SciomeChartBase<String, List<Double>> {
	private static final int		MAX_NODES_SHOWN	= 5;
	
	private JFreeChart chart;
	private SlidingCategoryDataset	slidingDataset;
	
	public SciomeViolinPlotJFree(String title, List<ChartDataPack> chartDataPacks, ChartKey key,
			SciomeChartListener chartListener) {
		super(title, chartDataPacks, new ChartKey[] {key}, true, false, chartListener);
	}

	@Override
	public void reactToChattingCharts() {
		
	}

	@Override
	public void markData(Set<String> markings) {
		
	}

	@Override
	protected Node generateChart(ChartKey[] keys, ChartConfiguration chartConfiguration) {
		ChartKey key = keys[0];
		
		ViolinCategoryDataset dataset = new ViolinCategoryDataset();

		for (SciomeSeries<String, List<Double>> series : getSeriesData())
		{
			String seriesName = series.getName();
			for (SciomeData<String, List<Double>> chartData : series.getData())
			{
				List value = chartData.getYValue();
				if(value != null) {
					dataset.add(value, seriesName, chartData.getXValue());
				}
			}
		}
		
		slidingDataset = new SlidingCategoryDataset(dataset, 0, MAX_NODES_SHOWN);
		setSliders(dataset.getColumnCount());
		
		//Set axis
		CategoryAxis xAxis = new CategoryAxis();
		ValueAxis yAxis = SciomeNumberAxisGeneratorJFree.generateAxis(getLogYAxis().isSelected(), key.toString());

		ViolinRenderer renderer = new ViolinRenderer();
		
		// Set tooltip string
		CategoryToolTipGenerator tooltipGenerator = new CategoryToolTipGenerator() {
			@Override
			public String generateToolTip(CategoryDataset dataset, int row, int column) {
				return getSeriesData().get(row).getData().get(column).getExtraValue().toString();
			}
		};
		renderer.setDefaultToolTipGenerator(tooltipGenerator);
		renderer.setSeriesFillPaint(0, Color.white);
		renderer.setDefaultOutlinePaint(Color.black);
		
		// Set plot parameters
		CategoryPlot plot = new CategoryPlot(slidingDataset, xAxis, yAxis, renderer);
		plot.setForegroundAlpha(0.8f);
		plot.setRangePannable(false);
		plot.setBackgroundPaint(Color.white);
		
		//Set default legend items
		LegendItemCollection chartLegend = new LegendItemCollection();
		Shape shape = new Rectangle(10, 10);
		DrawingSupplier supplier = new DefaultDrawingSupplier();
		for(int i = 0; i < getSeriesData().size(); i++)
			chartLegend.add(new LegendItem(getSeriesData().get(i).getName(), null, null, null, shape, supplier.getNextPaint()));
		
	
		chartLegend.add(new LegendItem("10th Ranked Gene BMD", null, null, null, new Ellipse2D.Double(0, 0, 10, 10), Color.black));
		chartLegend.add(new LegendItem("25th Ranked Gene BMD", null, null, null, new Rectangle(10, 2), Color.black));
		chartLegend.add(new LegendItem("1th % Gene BMD", null, null, null, shape, Color.black));
		chartLegend.add(new LegendItem("5th % Gene BMD", null, null, null, ShapeCreator.createDiamond(10 * Math.sqrt(2), 10 * Math.sqrt(2)), Color.black));
		chartLegend.add(new LegendItem("10th % Gene BMD", null, null, null, ShapeCreator.createDiagonalCross(5, 2), Color.black));
		plot.setFixedLegendItems(chartLegend);
		
		// Create chart
		chart = new JFreeChart(key.getKey() + " Violins", JFreeChart.DEFAULT_TITLE_FONT, plot, true);
		chart.getPlot().setForegroundAlpha(.8f);
		setRange();

		// Create Panel
		SciomeChartViewer chartView = new SciomeChartViewer(chart);

		return chartView;
	}

	@Override
	protected boolean isXAxisDefineable() {
		return false;
	}

	@Override
	protected boolean isYAxisDefineable() {
		return false;
	}

	@Override
	protected void redrawChart() {
		showChart();
	}

	@Override
	protected void convertChartDataPacksToSciomeSeries(ChartKey[] keys, List<ChartDataPack> chartPacks) {
		ChartKey key = keys[0];
		
		List<SciomeSeries<String, List<Double>>> seriesData = new ArrayList<>();
		for (ChartDataPack chartDataPack : getChartDataPacks())
		{
			SciomeSeries<String, List<Double>> series = new SciomeSeries<>(chartDataPack.getName());

			for (ChartData chartData : chartDataPack.getChartData())
			{
				List<Double> dataPoint =  chartData.getDataPointLists().get(key);
				
				if(dataPoint == null)
					continue;
				
				SciomeData<String, List<Double>> xyData = new SciomeData(chartData.getDataPointLabel(),
						chartData.getDataPointLabel(), dataPoint, chartData.getCharttableObject());

				series.getData().add(xyData);
			}
			seriesData.add(series);
		}


		setSeriesData(seriesData);
	}
	
	private void setSliders(double numValues)
	{
		Slider slider = new Slider(0, numValues - MAX_NODES_SHOWN, 0);
		slider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number oldValue, Number newValue)
			{
				slidingDataset.setFirstCategoryIndex(newValue.intValue());
				setRange();
			}
		});

		sethSlider(slider);
	}
	
	private void setRange()
	{
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;
		ViolinCategoryDataset dataset = (ViolinCategoryDataset)slidingDataset.getUnderlyingDataset();
		for(int i = 0; i < getSeriesData().size(); i++) {
			int first = slidingDataset.getFirstCategoryIndex();
			for(int j = 0; j < MAX_NODES_SHOWN; j++) {
				ViolinItem item;
				try {
					item = dataset.getItem(i, first + j);
				} catch(Exception e) {
					continue;
				}
				if(item.getMaxOutlier().doubleValue() > max)
					max = item.getMaxOutlier().doubleValue();
				if(item.getMinOutlier().doubleValue() < min)
					min = item.getMinOutlier().doubleValue();
			}
		}
		((CategoryPlot)chart.getPlot()).getRangeAxis().setRange(new Range(min, max));
	}
}