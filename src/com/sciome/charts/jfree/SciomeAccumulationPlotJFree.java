package com.sciome.charts.jfree;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Shape;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYPointerAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.fx.interaction.ChartMouseEventFX;
import org.jfree.chart.fx.interaction.ChartMouseListenerFX;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.util.ShapeUtilities;

import com.sciome.bmdexpress2.mvp.model.ChartKey;
import com.sciome.charts.SciomeAccumulationPlot;
import com.sciome.charts.SciomeChartListener;
import com.sciome.charts.data.ChartConfiguration;
import com.sciome.charts.data.ChartDataPack;
import com.sciome.charts.model.SciomeSeries;

import javafx.scene.Node;
import javafx.scene.input.MouseButton;

public class SciomeAccumulationPlotJFree extends SciomeAccumulationPlot
{

	public SciomeAccumulationPlotJFree(String title, List<ChartDataPack> chartDataPacks, ChartKey key,
			Double bucketsize, SciomeChartListener chartListener)
	{
		super(title, chartDataPacks, key, bucketsize, chartListener);
	}

	@Override
	protected Node generateChart(ChartKey[] keys, ChartConfiguration chartConfig)
	{
		ChartKey key1 = keys[0];
		String key2 = "Accumulation";
		Double min1 = getMinMin(key1);
		Double min2 = 0.0;
		Double max1 = getMaxMax(key1);
		Double max2 = 0.0;

		DefaultXYDataset dataset = new DefaultXYDataset();
		// Create chart
		JFreeChart chart = ChartFactory.createXYLineChart(key1 + " Accumulation Plot", key1.toString(), key2,
				dataset, PlotOrientation.VERTICAL, true, true, false);
		XYPlot plot = (XYPlot) chart.getPlot();
		plot.clearAnnotations();
		for (SciomeSeries<Number, Number> series : getSeriesData())
		{
			double[] domains = new double[series.getData().size()];
			double[] ranges = new double[series.getData().size()];
			int i = 0;
			for (Object chartData : series.getData())
			{
				AccumulationData value = (AccumulationData) chartData;
				double domainvalue = value.getXValue().doubleValue();
				double rangevalue = value.getYValue().doubleValue();
				domains[i] = domainvalue;
				ranges[i++] = rangevalue;
				if (rangevalue > max2)
					max2 = rangevalue;
				String label = getLabelIfNeedHighlighting((List<Object>) (value.getExtraValue()));

				if (!label.equals(""))
				{
					XYPointerAnnotation ann = new XYPointerAnnotation(label, value.getXValue().doubleValue(),
							value.getYValue().doubleValue(), Math.PI * 4 / 3);
					ann.setBaseRadius(40.0);
					ann.setTipRadius(5);
					ann.setTextAnchor(TextAnchor.HALF_ASCENT_RIGHT);

					plot.addAnnotation(ann);
				}
			}
			dataset.addSeries(series.getName(), new double[][] { domains, ranges });
		}

		// plot.setForegroundAlpha(0.1f);
		plot.setDomainPannable(true);
		plot.setRangePannable(true);
		plot.setDomainAxis(
				SciomeNumberAxisGeneratorJFree.generateAxis(getLogXAxis().isSelected(), key1.toString()));
		plot.setRangeAxis(SciomeNumberAxisGeneratorJFree.generateAxis(getLogYAxis().isSelected(), key2));

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

		plot.setRenderer(new XYLineAndShapeRenderer(true, true) {

			@Override
			public Shape getItemShape(int row, int col)
			{

				AccumulationData data = (AccumulationData) getSeriesData().get(row).getData().get(col);
				List<Object> objects = (List<Object>) (data.getExtraValue());
				if (objectsNeedHighlighting(objects) != 0)
				{
					return ShapeUtilities.createDiagonalCross(5, 5);
				}
				else
					return super.getItemShape(row, col);
			}

			// add a little transparency to the items painted
			// @Override
			// public Paint getItemPaint(int row, int col)
			// {

			// Color c = (Color) super.getItemPaint(row, col);
			// return new Color(c.getRed(), c.getGreen(), c.getBlue(), 100);
			// }

			@Override
			public Paint getItemFillPaint(int row, int column)
			{
				AccumulationData data = (AccumulationData) getSeriesData().get(row).getData().get(column);
				List<Object> objects = (List<Object>) (data.getExtraValue());
				int highlighting = objectsNeedHighlighting(objects);
				if (highlighting == 1)
					return Color.yellow;
				else if (highlighting == -1)
					return Color.BLUE;
				else
				{
					return super.getItemFillPaint(row, column);
				}
			}

		});

		XYLineAndShapeRenderer renderer = ((XYLineAndShapeRenderer) plot.getRenderer());
		renderer.setDefaultShapesVisible(true);
		renderer.setDrawOutlines(true);
		renderer.setUseFillPaint(true);
		renderer.setDefaultShapesFilled(true);
		renderer.setDefaultFillPaint(Color.white);
		renderer.setSeriesStroke(0, new BasicStroke(3.0f));
		renderer.setSeriesOutlineStroke(0, new BasicStroke(2.0f));
		renderer.setDefaultItemLabelsVisible(true);

		ItemLabelPosition position1 = new ItemLabelPosition(ItemLabelAnchor.OUTSIDE10,
				TextAnchor.BOTTOM_RIGHT);
		// renderer.setlabel
		renderer.setDefaultNegativeItemLabelPosition(position1, true);
		renderer.setDefaultPositiveItemLabelPosition(position1, true);
		Color fontColor = new Color(0, 0, 0, 255);
		renderer.setDefaultItemLabelPaint(fontColor);
		Font font = new Font("Courier New", Font.BOLD, 12);
		renderer.setDefaultItemLabelFont(font, true);

		// Set tooltip string
		XYToolTipGenerator tooltipGenerator = new XYToolTipGenerator() {
			@Override
			public String generateToolTip(XYDataset dataset, int series, int item)
			{
				AccumulationData data = (AccumulationData) getSeriesData().get(series).getData().get(item);
				List<Object> objects = (List<Object>) (data.getExtraValue());
				return String.valueOf(joinObjects(objects, data.getYValue().doubleValue(),
						data.getValuesList(), key1.toString(), MAX_TO_POPUP));
			}
		};
		renderer.setDefaultToolTipGenerator(tooltipGenerator);
		plot.setBackgroundPaint(Color.white);
		// chart.getPlot().setForegroundAlpha(0.5f);

		// Create Panel
		SciomeChartViewer chartView = new SciomeChartViewer(chart);

		// LogarithmicAxis yAxis = new LogarithmicAxis();
		chartView.addChartMouseListener(new ChartMouseListenerFX() {

			@Override
			public void chartMouseClicked(ChartMouseEventFX e)
			{
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

	@Override
	public void recieveChatFromOtherChart(List<Object> conversation)
	{
		// TODO Auto-generated method stub

	}

}
