package com.sciome.charts.jfree;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.fx.ChartViewer;
import org.jfree.chart.fx.interaction.ChartMouseEventFX;
import org.jfree.chart.fx.interaction.ChartMouseListenerFX;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;

import com.sciome.charts.SciomeAccumulationPlot;
import com.sciome.charts.SciomeChartListener;
import com.sciome.charts.data.ChartConfiguration;
import com.sciome.charts.data.ChartDataPack;
import com.sciome.charts.model.SciomeSeries;

import javafx.scene.Node;
import javafx.scene.input.MouseButton;

public class SciomeAccumulationPlotJFree extends SciomeAccumulationPlot{

	public SciomeAccumulationPlotJFree(String title, List<ChartDataPack> chartDataPacks, String key, Double bucketsize,
			SciomeChartListener chartListener) {
		super(title, chartDataPacks, key, bucketsize, chartListener);
	}

	@Override
	protected Node generateChart(String[] keys, ChartConfiguration chartConfig) {
		String key1 = keys[0];
		String key2 = "Accumulation";
		Double min = getMinMin(key1);

		DefaultXYDataset dataset = new DefaultXYDataset();
		
		for (SciomeSeries<Number, Number> series : getSeriesData())
		{
			double[] domains = new double[series.getData().size()];
			double[] ranges = new double[series.getData().size()];
			int i = 0;
			for (Object chartData : series.getData())
			{
				AccumulationData value = (AccumulationData)chartData;
				double domainvalue = value.getXValue().doubleValue();
				double rangevalue = value.getYValue().doubleValue();
				domains[i] = domainvalue;
				ranges[i++] = rangevalue;
			}
			dataset.addSeries(series.getName(), new double[][] { domains, ranges });
		}

		// Create chart
		JFreeChart chart = ChartFactory.createXYLineChart(key1 + "Accumulation Plot",
				key1, key2, dataset, PlotOrientation.VERTICAL, true, true, false);
		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setForegroundAlpha(0.1f);
		plot.setDomainPannable(true);
		plot.setRangePannable(true);
		plot.setDomainAxis(SciomeNumberAxisGeneratorJFree.generateAxis(getLogXAxis().isSelected()));
		plot.setRangeAxis(SciomeNumberAxisGeneratorJFree.generateAxis(getLogYAxis().isSelected()));
		
		XYLineAndShapeRenderer renderer = ((XYLineAndShapeRenderer) plot.getRenderer());
		renderer.setBaseShapesVisible(true);
		renderer.setDrawOutlines(true);
		renderer.setUseFillPaint(true);
        renderer.setBaseFillPaint(Color.white);
        renderer.setSeriesStroke(0, new BasicStroke(3.0f));
        renderer.setSeriesOutlineStroke(0, new BasicStroke(2.0f));
        renderer.setSeriesShape(0, new Ellipse2D.Double(-5.0, -5.0, 10.0, 10.0));
		renderer.setSeriesPaint(0, new Color(0.0f, 0.0f, .82f, .3f));
		
		//Set tooltip string
		XYToolTipGenerator tooltipGenerator = new XYToolTipGenerator()
		{
			@Override
			public String generateToolTip(XYDataset dataset, int series, int item) {
				AccumulationData data = (AccumulationData)getSeriesData().get(series).getData().get(item);
				List<Object> objects = (List<Object>)(data.getExtraValue());
				return String.valueOf(joinObjects(objects, data.getYValue().doubleValue(),
						data.getValuesList(), key1, MAX_TO_POPUP));
			}
		};
		renderer.setBaseToolTipGenerator(tooltipGenerator);
		plot.setBackgroundPaint(Color.white);
		chart.getPlot().setForegroundAlpha(0.1f);

		// Create Panel
		ChartViewer chartView = new ChartViewer(chart);

		// LogarithmicAxis yAxis = new LogarithmicAxis();
		chartView.addChartMouseListener(new ChartMouseListenerFX() {

			@Override
			public void chartMouseClicked(ChartMouseEventFX e) {
				if(e.getEntity() != null && e.getEntity().getToolTipText() != null //Check to see if an entity was clicked
						&& e.getTrigger().getButton().equals(MouseButton.PRIMARY)) //Check to see if it was the left mouse button clicked
				showObjectText(e.getEntity().getToolTipText());
			}

			@Override
			public void chartMouseMoved(ChartMouseEventFX e) {
				//ignore for now
			}
		});

		return chartView;
	}

	
}
