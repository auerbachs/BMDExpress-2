package com.sciome.charts.jfree;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.fx.interaction.ChartMouseEventFX;
import org.jfree.chart.fx.interaction.ChartMouseListenerFX;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
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

public class SciomeBarChartJFree extends SciomeChartBase<String, Number> implements ChartDataExporter {

	private static final int 		MAX_NODES_SHOWN = 10;
	private SlidingCategoryDataset 	slidingDataset;
	private JFreeChart				chart;
	private int 					firstValue;

	public SciomeBarChartJFree(String title, List<ChartDataPack> chartDataPacks, ChartKey key,
			SciomeChartListener chartListener) {
		super(title, chartDataPacks, new ChartKey[] { key }, true, false, chartListener);
		// this chart defines how the axes can be edited by the user in the chart
		// configuration.
		showLogAxes(false, true, false, true);
		firstValue = 0;

		getLogYAxis().selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) {
				initChart();
			}
		});
		getLockYAxis().selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) {
				initChart();
			}
		});

		initChart();
	}

	@Override
	public void reactToChattingCharts() {
		// nothing for now
	}

	@Override
	public void markData(Set<String> markings) {
		// not markable for now
	}

	@Override
	protected Node generateChart(ChartKey[] keys, ChartConfiguration chartConfig) {
		ChartKey key1 = keys[0];

		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		for (SciomeSeries<String, Number> series : getSeriesData()) {
			String seriesName = series.getName();
			for (SciomeData<String, Number> chartData : series.getData()) {
				String domainvalue = chartData.getXValue();
				double rangevalue = chartData.getYValue().doubleValue();
				dataset.addValue(rangevalue, seriesName, domainvalue);
			}
		}
		slidingDataset = new SlidingCategoryDataset(dataset, 0, MAX_NODES_SHOWN);

		chart = ChartFactory.createBarChart("Multiple Data Viewer: " + key1.toString(), "Category",
				key1.toString(), slidingDataset, PlotOrientation.VERTICAL, true, true, false);
		CategoryPlot plot = chart.getCategoryPlot();
		plot.setRangePannable(true);
		plot.setRangeAxis(SciomeNumberAxisGeneratorJFree.generateAxis(getLogYAxis().isSelected(), key1.toString()));
		if(getLockYAxis().isSelected()) {
			plot.getRangeAxis().setAutoRange(false);
			plot.getRangeAxis().setRange(new Range(0, getMaxMax(key1)));
		} else {
			plot.getRangeAxis().setAutoRange(true);
		}
		
		plot.getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.DOWN_90);

		setSliders(dataset.getColumnCount());

		BarRenderer renderer = (BarRenderer) plot.getRenderer();
		// Set tooltip string
		StandardCategoryToolTipGenerator tooltipGenerator = new StandardCategoryToolTipGenerator() {
			@Override
			public String generateToolTip(CategoryDataset dataset, int series, int item) {
				Object object = ((ChartExtraValue) getSeriesData().get(series).getData().get(firstValue + item).getExtraValue()).userData;
				if(object != null) {
					return object.toString();
				}
				return "";
			}
		};
		renderer.setDefaultToolTipGenerator(tooltipGenerator);

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
	protected boolean isXAxisDefineable() {
		return false;
	}

	@Override
	protected boolean isYAxisDefineable() {
		return true;
	}

	@Override
	protected void redrawChart() {
		initChart();
	}

	/*
	 * implement the getting of lines that need to be exported.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<String> getLinesToExport() {

		List<String> returnList = new ArrayList<>();
		StringBuilder sb = new StringBuilder();

		sb.append("series");
		sb.append("\t");
		sb.append("x");
		sb.append("\t");
		sb.append("y");
		sb.append("\t");
		sb.append("component");
		returnList.add(sb.toString());
		for (SciomeSeries<String, Number> sData : getSeriesData()) {
			for (SciomeData<String, Number> xychartData : sData.getData()) {
				ChartExtraValue extraValue = (ChartExtraValue) xychartData.getExtraValue();
				String X = (String) xychartData.getXValue();

				Double Y = (Double) xychartData.getYValue();

				if (extraValue.userData == null) // this means it's a faked value for showing multiple
													// datasets together. skip it
					continue;
				sb.setLength(0);

				sb.append(sData.getName());
				sb.append("\t");
				sb.append(X);
				sb.append("\t");
				sb.append(Y);
				sb.append("\t");

				sb.append(extraValue.userData.toString());

				returnList.add(sb.toString());

			}
		}

		return returnList;

	}

	@Override
	protected void convertChartDataPacksToSciomeSeries(ChartKey[] keys, List<ChartDataPack> chartPacks) {
		ChartKey key = keys[0];
		Map<String, Integer> countMap = getCountMap();

		List<SciomeSeries<String, Number>> seriesData = new ArrayList<>();
		for (ChartDataPack chartDataPack : getChartDataPacks()) {
			SciomeSeries<String, Number> series1 = new SciomeSeries<>(chartDataPack.getName());

			Set<String> chartLabelSet = new HashSet<>();
			for (ChartData chartData : chartDataPack.getChartData()) {
				Double dataPointValue = (Double) chartData.getDataPoints().get(key);
				if (dataPointValue == null)
					continue;
				SciomeData<String, Number> xyData = new SciomeData<>(chartData.getDataPointLabel(),
						chartData.getDataPointLabel(), dataPointValue,
						new ChartExtraValue(chartData.getDataPointLabel(), countMap.get(chartData.getDataPointLabel()),
								chartData.getCharttableObject()));

				series1.getData().add(xyData);
				chartLabelSet.add(chartData.getDataPointLabel());
			}

			// add empty values for multiple datasets. When comparing multiple
			// data sets, it comes in handy for scrolling to just have empty
			// data points when the data set doesn't represent a label
			for (String chartedKey : countMap.keySet()) {
				if (!chartLabelSet.contains(chartedKey)) {
					SciomeData<String, Number> xyData = new SciomeData<>(chartedKey, chartedKey, 0.0,
							new ChartExtraValue(chartedKey, countMap.get(chartedKey), null));

					series1.getData().add(xyData);
				}
			}
			if(seriesData.size() > 0)
				sortSeriesWithPrimarySeries(series1, (SciomeSeries) (seriesData.get(0)));
			else
				sortSeriesY(series1);
			
			seriesData.add(series1);

		}
		setSeriesData(seriesData);
	}

	/*
	 * 
	 */
	protected Map<String, Integer> getCountMap() {
		// create count map because in multiple data comparison, I only care about
		// shared data labels
		Map<String, Integer> countMap = new HashMap<>();
		int maxPerPack = 0;
		if (getChartDataPacks().size() > 0)
			maxPerPack = 30000 / getChartDataPacks().size();

		for (ChartDataPack chartDataPack : getChartDataPacks()) {
			int count = 0;
			for (ChartData chartData : chartDataPack.getChartData()) {
				if (countMap.get(chartData.getDataPointLabel()) == null) {
					countMap.put(chartData.getDataPointLabel(), 1);
				} else {
					countMap.put(chartData.getDataPointLabel(), countMap.get(chartData.getDataPointLabel()) + 1);
				}
				count++;
				if (count > maxPerPack)
					break;
			}
		}
		return countMap;
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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void sortSeriesY(SciomeSeries series1) {
		series1.getData().sort(new Comparator<SciomeData>() {

			@Override
			public int compare(SciomeData o1, SciomeData o2) {
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

	private void initChart() {
		showChart();
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
}
