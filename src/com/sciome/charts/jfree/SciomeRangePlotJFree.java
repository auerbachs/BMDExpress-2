package com.sciome.charts.jfree;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.fx.interaction.ChartMouseEventFX;
import org.jfree.chart.fx.interaction.ChartMouseListenerFX;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRendererState;
import org.jfree.chart.renderer.category.MinMaxCategoryRenderer;
import org.jfree.data.Range;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.category.SlidingCategoryDataset;

import com.sciome.bmdexpress2.mvp.model.ChartKey;
import com.sciome.charts.SciomeChartBase;
import com.sciome.charts.SciomeChartListener;
import com.sciome.charts.data.ChartConfiguration;
import com.sciome.charts.data.ChartData;
import com.sciome.charts.data.ChartDataPack;
import com.sciome.charts.export.ChartDataExporter;
import com.sciome.charts.model.SciomeData;
import com.sciome.charts.model.SciomeSeries;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseButton;

public class SciomeRangePlotJFree extends SciomeChartBase<String, Number> implements ChartDataExporter{
	private static final int		MAX_NODES_SHOWN = 10;
	
	private JFreeChart 					chart;
	private SlidingCategoryDataset 		slidingDataset;
	private int 						firstValue;
	
	public SciomeRangePlotJFree(String title, List<ChartDataPack> chartDataPacks, ChartKey minKey, ChartKey maxKey,
			ChartKey lowKey, ChartKey highKey, ChartKey middleKey, SciomeChartListener chartListener) {
		super(title, chartDataPacks, new ChartKey[] { minKey, maxKey, lowKey, highKey, middleKey }, true, false, chartListener);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void reactToChattingCharts() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void markData(Set markings) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected Node generateChart(ChartKey[] keys, ChartConfiguration chartConfiguration) {
		ChartKey minKey = keys[0];
		ChartKey maxKey = keys[1];
		ChartKey lowKey = keys[2];
		ChartKey highKey = keys[3];
		ChartKey middleKey = keys[4];
		
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for (SciomeSeries<String, Number> series : getSeriesData()) {
			String seriesName = series.getName();
			for (SciomeData<String, Number> chartData : series.getData()) {
				RangePlotExtraValue value = (RangePlotExtraValue)chartData.getExtraValue();
				dataset.addValue(value.getMin(), seriesName + "Min", chartData.getXValue());
				dataset.addValue(value.getMax(), seriesName + "Max", chartData.getXValue());
				dataset.addValue(value.getMid(), seriesName + "Mid", chartData.getXValue());
			}
		}
		slidingDataset = new SlidingCategoryDataset(dataset, 0, MAX_NODES_SHOWN);

		chart = ChartFactory.createBarChart("Range Plot", "BMDL Median, BMD Median, BMDU ", "Category",
				slidingDataset, PlotOrientation.VERTICAL, true, true, false);
		CategoryPlot plot = chart.getCategoryPlot();
		plot.setRangePannable(true);
		plot.setRangeAxis(SciomeNumberAxisGeneratorJFree.generateAxis(getLogYAxis().isSelected(), "BMDL Median, BMD Median, BMDU "));
		if(getLockXAxis().isSelected()) {
			plot.getRangeAxis().setAutoRange(false);
			plot.getRangeAxis().setRange(new Range(0, getMaxMax(maxKey)));
		} else {
			plot.getRangeAxis().setAutoRange(true);
		}
		
		plot.getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.DOWN_90);

		setSliders(dataset.getColumnCount());
		
		RangePlotRenderer renderer = new RangePlotRenderer();
		// Set tooltip string
//		StandardCategoryToolTipGenerator tooltipGenerator = new StandardCategoryToolTipGenerator() {
//			@Override
//			public String generateToolTip(CategoryDataset dataset, int series, int item) {
//				Object object = ((ChartExtraValue) getSeriesData().get(series).getData().get(firstValue + item).getExtraValue()).userData;
//				if(object != null) {
//					return object.toString();
//				}
//				return "";
//			}
//		};
//		renderer.setDefaultToolTipGenerator(tooltipGenerator);
		plot.setRenderer(renderer);
		
		plot.setBackgroundPaint(Color.white);

		// Create Panel
		SciomeChartViewer chartView = new SciomeChartViewer(chart);

		// Add plot point clicking interaction
		chartView.addChartMouseListener(new ChartMouseListenerFX() {

			@Override
			public void chartMouseClicked(ChartMouseEventFX e) {
				if (e.getEntity() != null && e.getEntity().getToolTipText() != null // Check to see if an
																					// entity was clicked
						&& e.getTrigger().getButton().equals(MouseButton.PRIMARY)) // Check to see if it was
																					// the left mouse button
																					// clicked
					showObjectText(e.getEntity().getToolTipText());
			}

			@Override
			public void chartMouseMoved(ChartMouseEventFX e) {
				// ignore for now
			}
		});

		return chartView;
	}

	@Override
	public List<String> getLinesToExport() {
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
	protected boolean isXAxisDefineable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean isYAxisDefineable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void redrawChart() {
		// TODO Auto-generated method stub
	}

	@Override
	protected void convertChartDataPacksToSciomeSeries(ChartKey[] keys, List chartPacks) {
		ChartKey minKey = keys[0];
		ChartKey maxKey = keys[1];
		ChartKey lowKey = keys[2];
		ChartKey key = keys[3];
		ChartKey middleKey = keys[4];
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

		Double sum = 0.0;
		int count = 0;
		for (ChartDataPack chartDataPack : getChartDataPacks())
		{
			for (ChartData chartData : chartDataPack.getChartData())
			{
				Double dataPointValue = (Double) chartData.getDataPoints().get(key);
				if (dataPointValue != null)
					sum += dataPointValue;

				count++;
			}
		}

		Double avg = 0.0;
		if (count > 0)
			avg = sum / count;

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

				sum += dataPointValue;

				Double dataPointValueMinKey = (Double) chartData.getDataPoints().get(minKey);
				Double dataPointValueMaxKey = (Double) chartData.getDataPoints().get(maxKey);
				Double dataPointValueMiddleKey = (Double) chartData.getDataPoints().get(key);
				
				chartLabelSet.add(chartData.getDataPointLabel());
				SciomeData<String, Number> xyData = new SciomeData<>(chartData.getDataPointLabel(),
						 chartData.getDataPointLabel(), dataPointValue,
						new RangePlotExtraValue(chartData.getDataPointLabel(),
								countMap.get(chartData.getDataPointLabel()), dataPointValueMinKey,
								dataPointValueMaxKey, dataPointValueMiddleKey,
								chartData.getCharttableObject().toString(), chartData.getCharttableObject()));

				series1.getData().add(xyData);
			}

			// add empty values for multiple datasets. When comparing multiple
			// data sets, it comes in handy for scrolling to just have empty
			// data points when the data set doesn't represent a label
			for (String chartedKey : countMap.keySet())
			{
				if (!chartLabelSet.contains(chartedKey))
				{
					SciomeData<String, Number> xyData = new SciomeData<>(chartedKey, chartedKey, avg, 
							new RangePlotExtraValue(chartedKey, countMap.get(chartedKey), avg, avg, avg, "",
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
	
	private void setSliders(double numValues) {
		Slider slider = new Slider(0, numValues - 10, 0);
		slider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number oldValue, Number newValue) {
				firstValue = newValue.intValue();
				slidingDataset.setFirstCategoryIndex(firstValue);
			}
		});

		sethSlider(slider);
	}
	
	/** Data extra values for storing close, high and low. */
	@SuppressWarnings("rawtypes")
	private class RangePlotExtraValue extends ChartExtraValue
	{
		private Double	min;
		private Double	max;
		private Double	high;
		private String	description;

		public RangePlotExtraValue(String label, Integer count, Double min, Double max, Double mid,
				String description, Object userData)
		{
			super(label, count, userData);
			this.min = min;
			this.max = max;
			this.high = mid;
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
			return high;
		}

		public String getDescription()
		{
			return description;
		}

	}
	
	private class RangePlotRenderer extends MinMaxCategoryRenderer {
		private int lastCategory = -1;
		private double min = -1;
		private double max = -1;
		@Override
		public void drawItem(Graphics2D g2, CategoryItemRendererState state,
	            Rectangle2D dataArea, CategoryPlot plot, CategoryAxis domainAxis,
	            ValueAxis rangeAxis, CategoryDataset dataset, int row, int column,
	            int pass) {
			// first check the number we are plotting...
	        Number value = dataset.getValue(row, column);
	        if (value != null) {
	            // current data point...
	            double x1 = domainAxis.getCategoryMiddle(column, getColumnCount(),
	                    dataArea, plot.getDomainAxisEdge());
	            double y1 = rangeAxis.valueToJava2D(value.doubleValue(), dataArea,
	                    plot.getRangeAxisEdge());
	            Shape hotspot = new Rectangle2D.Double(x1 - 4, y1 - 4, 8.0, 8.0);

	            g2.setPaint(getItemPaint(row, column));
	            g2.setStroke(getItemStroke(row, column));

	            PlotOrientation orient = plot.getOrientation();
	            if (orient == PlotOrientation.VERTICAL) {
	                this.getObjectIcon().paintIcon(null, g2, (int) x1, (int) y1);
	            } else {
	            	this.getObjectIcon().paintIcon(null, g2, (int) y1, (int) x1);
	            }

	            if (lastCategory == column) {
	                if (min > value.doubleValue()) {
	                    min = value.doubleValue();
	                }
	                if (max < value.doubleValue()) {
	                    max = value.doubleValue();
	                }

	                // last series, so we are ready to draw the min and max
	                if (dataset.getRowCount() - 1 == row) {
	                    g2.setPaint(this.getGroupPaint());
	                    g2.setStroke(this.getGroupStroke());
	                    double minY = rangeAxis.valueToJava2D(this.min, dataArea,
	                            plot.getRangeAxisEdge());
	                    double maxY = rangeAxis.valueToJava2D(this.max, dataArea,
	                            plot.getRangeAxisEdge());

	                    if (orient == PlotOrientation.VERTICAL) {
	                        g2.draw(new Line2D.Double(x1, minY, x1, maxY));
	                        this.getMinIcon().paintIcon(null, g2, (int) x1, (int) minY);
	                        this.getMaxIcon().paintIcon(null, g2, (int) x1, (int) maxY);
	                    }
	                    else {
	                        g2.draw(new Line2D.Double(minY, x1, maxY, x1));
	                        this.getMinIcon().paintIcon(null, g2, (int) minY, (int) x1);
	                        this.getMaxIcon().paintIcon(null, g2, (int) maxY, (int) x1);
	                    }
	                }
	            }
	            else {  // reset the min and max
	                this.lastCategory = column;
	                this.min = value.doubleValue();
	                this.max = value.doubleValue();
	            }

	            // connect to the previous point
	            if (this.isDrawLines()) {
	                if (column != 0) {
	                    Number previousValue = dataset.getValue(row, column - 1);
	                    if (previousValue != null) {
	                        // previous data point...
	                        double previous = previousValue.doubleValue();
	                        double x0 = domainAxis.getCategoryMiddle(column - 1,
	                                getColumnCount(), dataArea,
	                                plot.getDomainAxisEdge());
	                        double y0 = rangeAxis.valueToJava2D(previous, dataArea,
	                                plot.getRangeAxisEdge());
	                        g2.setPaint(getItemPaint(row, column));
	                        g2.setStroke(getItemStroke(row, column));
	                        Line2D line;
	                        if (orient == PlotOrientation.VERTICAL) {
	                            line = new Line2D.Double(x0, y0, x1, y1);
	                        }
	                        else {
	                            line = new Line2D.Double(y0, x0, y1, x1);
	                        }
	                        g2.draw(line);
	                    }
	                }
	            }

	            // add an item entity, if this information is being collected
	            EntityCollection entities = state.getEntityCollection();
	            if (entities != null) {
	                addItemEntity(entities, dataset, row, column, hotspot);
	            }
	        }
		}
	}
}
