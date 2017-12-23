package com.sciome.charts.jfree;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.fx.interaction.ChartMouseEventFX;
import org.jfree.chart.fx.interaction.ChartMouseListenerFX;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.xy.XYDataset;

import com.sciome.bmdexpress2.mvp.model.ChartKey;
import com.sciome.charts.SciomeChartListener;
import com.sciome.charts.SciomeHistogram;
import com.sciome.charts.data.ChartConfiguration;
import com.sciome.charts.data.ChartData;
import com.sciome.charts.data.ChartDataPack;
import com.sciome.charts.export.ChartDataExporter;
import com.sciome.charts.model.SciomeData;
import com.sciome.charts.model.SciomeSeries;

import javafx.scene.Node;
import javafx.scene.input.MouseButton;

public class SciomeHistogramJFree extends SciomeHistogram implements ChartDataExporter
{

	public SciomeHistogramJFree(String title, List<ChartDataPack> chartDataPacks, ChartKey key,
			Double bucketsize, SciomeChartListener chartListener)
	{
		super(title, chartDataPacks, key, bucketsize, chartListener);
	}

	@Override
	protected Node generateChart(ChartKey[] keys, ChartConfiguration chartConfig)
	{
		ChartKey key = keys[0];

		// Create dataset
		HistogramDataset dataset = new HistogramDataset();
		for (ChartDataPack chartDataPack : getChartDataPacks())
		{
			double[] ranges = new double[chartDataPack.getChartData().size()];
			int i = 0;
			for (ChartData chartData : chartDataPack.getChartData())
			{
				Double dataPoint = (Double) chartData.getDataPoints().get(key);
				if (dataPoint == null)
					continue;
				ranges[i++] = dataPoint.doubleValue();
			}
			if (ranges.length > 0)
				dataset.addSeries(chartDataPack.getName(), ranges, (int) bucketsize.doubleValue());
		}

		// Create chart
		JFreeChart chart = ChartFactory.createHistogram(key.toString() + " Histogram", key.toString(),
				"Count", dataset, PlotOrientation.VERTICAL, true, true, false);

		// Set plot parameters
		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setForegroundAlpha(0.1f);
		plot.setDomainPannable(true);
		plot.setRangePannable(true);
		plot.setRangeAxis(
				SciomeNumberAxisGeneratorJFree.generateAxis(getLogYAxis().isSelected(), key.toString()));

		// Set renderer parameters
		XYBarRenderer renderer = ((XYBarRenderer) plot.getRenderer());
		// Set tooltip string
		XYToolTipGenerator tooltipGenerator = new XYToolTipGenerator() {
			@Override
			public String generateToolTip(XYDataset dataset, int series, int item)
			{
				List<Object> objects = (List<Object>) (getSeriesData().get(series).getData().get(item)
						.getExtraValue());
				return String.valueOf(joinAllObjects(objects));
			}
		};
		renderer.setSeriesFillPaint(0, Color.white);
		renderer.setDefaultOutlinePaint(Color.black);
		renderer.setDefaultToolTipGenerator(tooltipGenerator);
		renderer.setBarPainter(new StandardXYBarPainter());
		renderer.setShadowVisible(false);
		plot.setBackgroundPaint(Color.white);
		chart.getPlot().setForegroundAlpha(0.5f);

		// Create Custom ChartViewer to deal with zooming
		SciomeChartViewer chartView = new SciomeChartViewer(chart);

		// Add plot point clicking interaction
		chartView.addChartMouseListener(new ChartMouseListenerFX() {

			@SuppressWarnings("unchecked")
			@Override
			public void chartMouseClicked(ChartMouseEventFX e)
			{
				if (e.getEntity() != null && e.getEntity().getToolTipText() != null // Check to see if an
																					// entity was clicked
						&& e.getTrigger().getButton().equals(MouseButton.PRIMARY)) // Check to see if it was
																					// the left mouse button
																					// clicked
				{
					int seriesIndex = ((XYItemEntity) e.getEntity()).getSeriesIndex();
					int item = ((XYItemEntity) e.getEntity()).getItem();
					// get the objects associated with with the click and post it to the other charts
					// so they can highlight it.
					List<Object> objects = (List<Object>) getSeriesData().get(seriesIndex).getData().get(item)
							.getExtraValue();
					postObjectsForChattingCharts(objects);
					if (e.getTrigger().getClickCount() == 2)
						showObjectText(e.getEntity().getToolTipText());
				}
			}

			@Override
			public void chartMouseMoved(ChartMouseEventFX e)
			{
				// ignore for now
			}
		});

		return chartView;
	}

	// This method is overridden since jfree has it's own implementation for histograms that generates
	// buckets by itself which we need to map our objects to
	@Override
	protected void convertChartDataPacksToSciomeSeries(ChartKey[] keys, List<ChartDataPack> chartPacks)
	{
		ChartKey key = keys[0];
		// Now put the data in a bucket

		// We currently need to generate the jfree dataset to create the SciomeSeries for the purpose of
		// being able to map each item with the extravalue object for the tooltip
		HistogramDataset dataset = new HistogramDataset();
		for (ChartDataPack chartDataPack : getChartDataPacks())
		{
			double[] values = new double[chartDataPack.getChartData().size()];
			int i = 0;
			for (ChartData chartData : chartDataPack.getChartData())
			{
				Double dataPoint = (Double) chartData.getDataPoints().get(key);
				if (dataPoint == null)
					continue;
				values[i++] = dataPoint.doubleValue();
			}
			if (values.length > 0)
				dataset.addSeries(key.toString(), values, (int) bucketsize.doubleValue());
		}

		List<SciomeSeries<String, Number>> seriesData = new ArrayList<>();

		for (int i = 0; i < dataset.getSeriesCount(); i++)
		{
			List<List<Object>> bucketObjects = new ArrayList<>();
			if (bucketsize != null)
			{
				for (int j = 0; j < bucketsize.intValue(); j++)
					bucketObjects.add(new ArrayList<>());
			}
			// Loop through all the data points and put them into the appropriate bucket
			for (ChartData data : getChartDataPacks().get(i).getChartData())
			{
				Double dataPoint = (Double) (data.getDataPoints().get(key));
				if (dataPoint != null)
				{
					for (int j = 0; j < bucketsize.intValue(); j++)
					{
						if (dataPoint > dataset.getStartX(i, j).doubleValue()
								&& dataPoint < dataset.getEndX(i, j).doubleValue())
						{
							bucketObjects.get(j).add(data.getCharttableObject());
						}
					}
				}
			}

			SciomeSeries<String, Number> series = new SciomeSeries<>();
			series.setName(getChartDataPacks().get(i).getName());

			// Create the SciomeData object for each
			DecimalFormat df = new DecimalFormat("#.###");
			for (int j = 0; j < bucketsize.intValue(); j++)
			{
				SciomeData<String, Number> data = new SciomeData<>("", "" + dataset.getX(i, j),
						dataset.getY(i, j), bucketObjects.get(j));
				series.getData().add(data);
			}
			if (series.getData().size() > 0)
				seriesData.add(series);
		}
		setSeriesData(seriesData);
	}

	@Override
	protected void reactToChattingCharts()
	{
		// TODO Auto-generated method stub

	}

}
