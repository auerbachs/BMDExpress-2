package com.sciome.charts.jfree;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.Icon;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.fx.interaction.ChartMouseEventFX;
import org.jfree.chart.fx.interaction.ChartMouseListenerFX;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryMarker;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRendererState;
import org.jfree.chart.renderer.category.MinMaxCategoryRenderer;
import org.jfree.chart.ui.Layer;
import org.jfree.data.Range;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.SlidingCategoryDataset;

import com.sciome.bmdexpress2.mvp.model.ChartKey;
import com.sciome.charts.SciomeChartBase;
import com.sciome.charts.SciomeChartListener;
import com.sciome.charts.data.ChartConfiguration;
import com.sciome.charts.data.ChartData;
import com.sciome.charts.data.ChartDataPack;
import com.sciome.charts.export.ChartDataExporter;
import com.sciome.charts.jfree.dataset.RangePlotDataset;
import com.sciome.charts.model.SciomeData;
import com.sciome.charts.model.SciomeSeries;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseButton;

public class SciomeRangePlotJFree extends SciomeChartBase<String, Number> implements ChartDataExporter
{
	private static final int		MAX_NODES_SHOWN	= 10;

	private JFreeChart				chart;
	private SlidingCategoryDataset	slidingDataset;

	public SciomeRangePlotJFree(String title, List<ChartDataPack> chartDataPacks, ChartKey minKey,
			ChartKey midKey, ChartKey maxKey, SciomeChartListener chartListener)
	{
		super(title, chartDataPacks, new ChartKey[] { minKey, maxKey, midKey }, true,
				false, chartListener);

		// this chart defines how the axes can be edited by the user in the chart configuration.
		showLogAxes(false, true, false, true);
		showChart();

		getLogYAxis().selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val)
			{
				showChart();
			}
		});

		getLockYAxis().selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val)
			{
				showChart();
			}
		});
	}

	@Override
	public void reactToChattingCharts()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void markData(Set markings)
	{
		// TODO Auto-generated method stub

	}

	@Override
	protected Node generateChart(ChartKey[] keys, ChartConfiguration chartConfig)
	{
		ChartKey minKey = keys[0];
		ChartKey maxKey = keys[1];
		ChartKey midKey = keys[2];

		RangePlotDataset dataset = new RangePlotDataset();
		for (SciomeSeries<String, Number> series : getSeriesData())
		{
			String seriesName = series.getName();
			for (SciomeData<String, Number> chartData : series.getData())
			{
				RangePlotExtraValue value = (RangePlotExtraValue) chartData.getExtraValue();
				ArrayList<Number> values = new ArrayList<Number>();
				values.add(value.getMin());
				values.add(value.getMid());
				values.add(value.getMax());
				dataset.addActualValue(values, seriesName, chartData.getXValue());
				dataset.addValue(value.getMin(), seriesName, chartData.getXValue());
				dataset.addValue(value.getMax(), seriesName, chartData.getXValue());
				dataset.addValue(value.getMid(), seriesName, chartData.getXValue());
			}
		}
		slidingDataset = new SlidingCategoryDataset(dataset, 0, MAX_NODES_SHOWN);

		String rangeAxisLabel = minKey.toString() + ", " + midKey.toString() + ", " + maxKey.toString();
		chart = ChartFactory.createBarChart("Range Plot", rangeAxisLabel, "Category",
				slidingDataset, PlotOrientation.HORIZONTAL, true, true, false);
		CategoryPlot plot = chart.getCategoryPlot();
		plot.setRangePannable(true);
		plot.setRangeAxis(SciomeNumberAxisGeneratorJFree.generateAxis(getLogYAxis().isSelected(),
				rangeAxisLabel));

		if (getLockYAxis().isSelected() || getLogYAxis().isSelected())
		{
			plot.getRangeAxis().setAutoRange(false);
			double maxRange = getMaxMax(maxKey);
			double minRange = getMinMin(minKey);
			if (minRange < 0)
				minRange = 0;
			if (maxRange > 0)
				plot.getRangeAxis().setRange(new Range(minRange, getMaxMax(maxKey)));
			else
				plot.getRangeAxis().setAutoRange(true);
		}
		else
		{
			plot.getRangeAxis().setAutoRange(true);
		}

		if (plot.getOrientation() == PlotOrientation.VERTICAL)
			plot.getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.DOWN_90);
		for (int i = 0; i < dataset.getColumnCount(); i++)
		{
			if (i % 2 == 0)
				plot.addDomainMarker(new CategoryMarker(dataset.getColumnKey(i),
						new Color(200, 200, 200, 100), new BasicStroke(10000)), Layer.BACKGROUND);
			else
				plot.addDomainMarker(new CategoryMarker(dataset.getColumnKey(i),
						new Color(240, 240, 240, 100), new BasicStroke(10000)), Layer.BACKGROUND);
		}

		setSliders(dataset.getColumnCount());

		RangePlotRenderer renderer = new RangePlotRenderer();
		renderer.setSeriesOutlinePaint(0, Color.BLACK);
		// Set tooltip string
		StandardCategoryToolTipGenerator tooltipGenerator = new StandardCategoryToolTipGenerator() {
			@Override
			public String generateToolTip(CategoryDataset dataset, int series, int item)
			{
				Object object = ((ChartExtraValue) getSeriesData().get(series).getData()
						.get(slidingDataset.getFirstCategoryIndex() + item).getExtraValue()).userData;
				if (object != null)
				{
					return object.toString();
				}
				return "";
			}
		};
		renderer.setDefaultToolTipGenerator(tooltipGenerator);
		plot.setRenderer(renderer);

		plot.setBackgroundPaint(Color.white);

		// Create Panel
		SciomeChartViewer chartView = new SciomeChartViewer(chart);

		// Add plot point clicking interaction
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
	public List<String> getLinesToExport()
	{
		List<String> returnList = new ArrayList<>();
		StringBuilder sb = new StringBuilder();

		sb.append("series");
		sb.append("\t");
		sb.append("y");
		sb.append("\t");
		sb.append("min");
		sb.append("\t");
		sb.append("value");
		sb.append("\t");
		sb.append("max");
		sb.append("\t");
		sb.append("component");
		returnList.add(sb.toString());
		for (SciomeSeries<String, Number> sData : getSeriesData())
		{
			for (SciomeData<String, Number> xychartData : sData.getData())
			{
				RangePlotExtraValue extraValue = (RangePlotExtraValue) xychartData.getExtraValue();
				if (extraValue.getDescription().equals("")) // this means it's a faked value for showing
															// multiple
					// datasets together. skip it
					continue;
				sb.setLength(0);

				String X = (String) xychartData.getXValue();
				Double Y = (Double) xychartData.getYValue();

				sb.append(sData.getName());
				sb.append("\t");
				sb.append(Y);
				sb.append("\t");

				sb.append(extraValue.getMin());
				sb.append("\t");
				sb.append(X);
				sb.append("\t");
				sb.append(extraValue.getMax());
				sb.append("\t");
				sb.append(extraValue.getDescription());

				returnList.add(sb.toString());

			}
		}

		return returnList;
	}

	@Override
	protected boolean isXAxisDefineable()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean isYAxisDefineable()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void redrawChart()
	{
		// TODO Auto-generated method stub
	}

	@Override
	protected void convertChartDataPacksToSciomeSeries(ChartKey[] keys, List chartPacks)
	{
		ChartKey minKey = keys[0];
		ChartKey maxKey = keys[1];
		ChartKey key = keys[2];
		Double axisMin = getMinMin(minKey);
		Double axisMax = getMaxMax(maxKey);
		if (axisMax == 0.0)
			axisMax = getMaxMax(key);

		if (axisMax < axisMin)
		{
			axisMax = 1.0;
			axisMin = 0.1;
		}

		// Now put the data in a bucket

		// create count map because in multiple data comparison, I only care about
		// shared data labels
		Map<String, Integer> countMap = getCountMap();

		for (ChartDataPack chartDataPack : getChartDataPacks())
		{
			for (ChartData chartData : chartDataPack.getChartData())
			{
				Double dataPointValue = (Double) chartData.getDataPoints().get(key);
			}
		}

		List<SciomeSeries<String, Number>> seriesData = new ArrayList<>();
		for (ChartDataPack chartDataPack : getChartDataPacks())
		{
			SciomeSeries<String, Number> series1 = new SciomeSeries<>(chartDataPack.getName());

			Set<String> chartLabelSet = new HashSet<>();

			for (ChartData chartData : chartDataPack.getChartData())
			{
				Double dataPointValue = (Double) chartData.getDataPoints().get(key);

				if (dataPointValue == null)
					continue;

				Double dataPointValueMinKey = (Double) chartData.getDataPoints().get(minKey);
				Double dataPointValueMaxKey = (Double) chartData.getDataPoints().get(maxKey);
				Double dataPointValueMiddleKey = (Double) chartData.getDataPoints().get(key);
				
				//Check to ensure the values are actually in order of min < mid < max
				SciomeData<String, Number> xyData;
				if(dataPointValueMinKey < dataPointValueMiddleKey &&
					dataPointValueMiddleKey < dataPointValueMaxKey) {
					xyData = new SciomeData<>(chartData.getDataPointLabel(),
							chartData.getDataPointLabel(), dataPointValue,
							new RangePlotExtraValue(chartData.getDataPointLabel(),
									countMap.get(chartData.getDataPointLabel()), dataPointValueMinKey,
									dataPointValueMaxKey, dataPointValueMiddleKey,
									chartData.getCharttableObject().toString(), 
									chartData.getCharttableObject()));
				} else {
					//If not then don't show the point
					xyData = new SciomeData<>(chartData.getDataPointLabel(),
							chartData.getDataPointLabel(), dataPointValue,
							new RangePlotExtraValue(chartData.getDataPointLabel(),
									countMap.get(chartData.getDataPointLabel()), 
									new Double(0), new Double(0), new Double(0),
									chartData.getCharttableObject().toString(), 
									chartData.getCharttableObject()));
				}
				
				chartLabelSet.add(chartData.getDataPointLabel());

				series1.getData().add(xyData);
			}

			// add empty values for multiple datasets. When comparing multiple
			// data sets, it comes in handy for scrolling to just have empty
			// data points when the data set doesn't represent a label
			for (String chartedKey : countMap.keySet())
			{
				if (!chartLabelSet.contains(chartedKey))
				{
					SciomeData<String, Number> xyData = new SciomeData<>(chartedKey, chartedKey, 0.0,
							new RangePlotExtraValue(chartedKey, countMap.get(chartedKey), 0.0, 0.0, 0.0, "",
									null));

					series1.getData().add(xyData);
				}
			}

			if (seriesData.size() > 0)
				sortSeriesWithPrimarySeries(series1, (SciomeSeries) (seriesData.get(0)));
			else
				sortSeriesY(series1);

			seriesData.add(series1);

		}

		setSeriesData(seriesData);
	}

	private Map<String, Integer> getCountMap()
	{
		// create count map because in multiple data comparison, I only care about
		// shared data labels
		Map<String, Integer> countMap = new HashMap<>();
		for (ChartDataPack chartDataPack : getChartDataPacks())
		{
			for (ChartData chartData : chartDataPack.getChartData())
			{
				if (countMap.get(chartData.getDataPointLabel()) == null)
				{
					countMap.put(chartData.getDataPointLabel(), 1);
				}
				else
				{
					countMap.put(chartData.getDataPointLabel(),
							countMap.get(chartData.getDataPointLabel()) + 1);
				}
			}
		}
		return countMap;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void sortSeriesY(SciomeSeries series1)
	{
		series1.getData().sort(new Comparator<SciomeData>() {

			@Override
			public int compare(SciomeData o1, SciomeData o2)
			{
				int c;
				ChartExtraValue ce2 = (ChartExtraValue) o2.getExtraValue();
				ChartExtraValue ce1 = (ChartExtraValue) o1.getExtraValue();
				Double value2 = (Double) o2.getYValue();
				Double value1 = (Double) o1.getYValue();
				c = value1.compareTo(value2);
				if (c == 0)
					c = ce1.label.compareTo(ce2.label);
				return c;
			}
		});
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void sortSeriesWithPrimarySeries(SciomeSeries series1, SciomeSeries primarySeries)
	{
		Map<String, Integer> indexMap = new HashMap<>();
		int i = 0;
		for (Object sd : primarySeries.getData())
			indexMap.put(((SciomeData) sd).getName(), i++);

		series1.getData().sort(new Comparator<SciomeData>() {

			@Override
			public int compare(SciomeData o1, SciomeData o2)
			{
				if (indexMap.containsKey(o1.getName()) && indexMap.containsKey(o2.getName()))
					return indexMap.get(o1.getName()).compareTo(indexMap.get(o2.getName()));
				else if (indexMap.containsKey(o1.getName()) && !indexMap.containsKey(o2.getName()))
					return 1;
				else if (indexMap.containsKey(o2.getName()) && !indexMap.containsKey(o1.getName()))
					return -1;
				else
					return 0;
			}
		});

	}

	private void setSliders(double numValues)
	{
		Slider slider = new Slider(0, numValues - 10, 0);
		slider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number oldValue, Number newValue)
			{
				slidingDataset.setFirstCategoryIndex(newValue.intValue());
			}
		});

		sethSlider(slider);
	}

	/** Data extra values for storing close, high and low. */
	private class RangePlotExtraValue extends ChartExtraValue
	{
		private Double	min;
		private Double	max;
		private Double	mid;
		private String	description;

		public RangePlotExtraValue(String label, Integer count, Double min, Double max, Double mid,
				String description, Object userData)
		{
			super(label, count, userData);
			this.min = min;
			this.max = max;
			this.mid = mid;
			this.description = description;
		}

		public Double getMin()
		{
			return min;
		}

		public Double getMax()
		{
			return max;
		}

		public Double getMid()
		{
			return mid;
		}

		public String getDescription()
		{
			return description;
		}

	}

	private class RangePlotRenderer extends MinMaxCategoryRenderer
	{
		@Override
		public void drawItem(Graphics2D g2, CategoryItemRendererState state, Rectangle2D dataArea,
				CategoryPlot plot, CategoryAxis domainAxis, ValueAxis rangeAxis, CategoryDataset dataset,
				int row, int column, int pass)
		{

			RangePlotDataset rangePlotDataset = (RangePlotDataset) ((SlidingCategoryDataset) dataset)
					.getUnderlyingDataset();
			ArrayList<Number> value = rangePlotDataset.getActualValue(row,
					column + ((SlidingCategoryDataset) dataset).getFirstCategoryIndex());

			double min = value.get(0).doubleValue();
			double mid = value.get(1).doubleValue();
			double max = value.get(2).doubleValue();

			if (!(min == 0 && min == mid && min == max))
			{
				double minY = rangeAxis.valueToJava2D(min, dataArea, plot.getRangeAxisEdge());
				double maxY = rangeAxis.valueToJava2D(max, dataArea, plot.getRangeAxisEdge());

				double midValue = rangeAxis.valueToJava2D(mid, dataArea, plot.getRangeAxisEdge());

				double start = domainAxis.getCategoryStart(column, getColumnCount(), dataArea,
						plot.getDomainAxisEdge());
				double end = domainAxis.getCategoryEnd(column, getColumnCount(), dataArea,
						plot.getDomainAxisEdge());
				double xWidth = (end - start) / (rangePlotDataset.getRowCount() + 1);

				for (int i = 0; i < value.size(); i++)
				{
					// g2.setPaint(getItemPaint(row, column));
					g2.setStroke(getItemStroke(row, column));

					double x1 = domainAxis.getCategoryStart(column, getColumnCount(), dataArea,
							plot.getDomainAxisEdge()) + (xWidth * (row + 1));
					double y1 = rangeAxis.valueToJava2D(value.get(i).doubleValue(), dataArea,
							plot.getRangeAxisEdge());
					Shape hotspot = new Rectangle2D.Double(x1 - 4, y1 - 4, 8.0, 8.0);

					PlotOrientation orient = plot.getOrientation();
					if (orient == PlotOrientation.VERTICAL)
					{
						this.getObjectIcon().paintIcon(null, g2, (int) x1, (int) y1);
					}
					else
					{
						this.getObjectIcon().paintIcon(null, g2, (int) y1, (int) x1);
						hotspot = new Rectangle2D.Double(y1 - 4, x1 - 4, 8.0, 8.0);
					}

					if (orient == PlotOrientation.VERTICAL)
					{
						g2.draw(new Line2D.Double(x1, minY, x1, maxY));
						getIcon(getItemShape(row, column), getItemPaint(row, column),
								getItemOutlinePaint(row, column)).paintIcon(null, g2, (int) x1, (int) minY);
						getIcon(getItemShape(row, column), getItemPaint(row, column),
								getItemOutlinePaint(row, column)).paintIcon(null, g2, (int) x1, (int) maxY);
						this.getMaxIcon().paintIcon(null, g2, (int) x1, (int) midValue);

					}
					else
					{
						g2.draw(new Line2D.Double(minY, x1, maxY, x1));
						getIcon(getItemShape(row, column), getItemPaint(row, column),
								getItemOutlinePaint(row, column)).paintIcon(null, g2, (int) minY, (int) x1);
						getIcon(getItemShape(row, column), getItemPaint(row, column),
								getItemOutlinePaint(row, column)).paintIcon(null, g2, (int) maxY, (int) x1);
						this.getMaxIcon().paintIcon(null, g2, (int) midValue, (int) x1);
					}
					EntityCollection entities = state.getEntityCollection();
					if (entities != null)
					{
						addItemEntity(entities, dataset, row, column, hotspot);
					}
				}
			}
		}
	}

	private Icon getIcon(Shape shape, final Paint fillPaint, final Paint outlinePaint)
	{

		final int width = shape.getBounds().width;
		final int height = shape.getBounds().height;
		final GeneralPath path = new GeneralPath(shape);
		return new Icon() {
			@Override
			public void paintIcon(Component c, Graphics g, int x, int y)
			{
				Graphics2D g2 = (Graphics2D) g;
				path.transform(AffineTransform.getTranslateInstance(x, y));
				if (fillPaint != null)
				{
					g2.setPaint(fillPaint);
					g2.fill(path);
				}
				if (outlinePaint != null)
				{
					g2.setPaint(outlinePaint);
					g2.draw(path);
				}
				path.transform(AffineTransform.getTranslateInstance(-x, -y));
			}

			@Override
			public int getIconWidth()
			{
				return width;
			}

			@Override
			public int getIconHeight()
			{
				return height;
			}
		};
	}
}
