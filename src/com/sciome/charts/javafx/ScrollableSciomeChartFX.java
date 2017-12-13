package com.sciome.charts.javafx;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sciome.bmdexpress2.mvp.model.ChartKey;
import com.sciome.charts.SciomeChartBase;
import com.sciome.charts.SciomeChartListener;
import com.sciome.charts.data.ChartData;
import com.sciome.charts.data.ChartDataPack;
import com.sciome.charts.model.SciomeData;
import com.sciome.charts.model.SciomeSeries;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Slider;

/*
 * provide common support for scrolling through chart data. Platform chooser
 */
public abstract class ScrollableSciomeChartFX<X, Y> extends SciomeChartBase<X, Y>
{

	// map that keeps track of enough information to instantiate a node.
	// so we don't have to store large amounts of nodes in memory for scrolling
	private Map<String, NodeInformation>	nodeInfoMap			= new HashMap<>();

	private Slider							slider;

	// depending on what type of chart, this boolean will
	// tell the system to add data to front of what will be displayed and scrolled through.
	// this is relevent for box and whisker charts where we want the first value to be shown at the top
	protected boolean						addDataAtTop		= false;
	private int								maxSlider;
	final protected int						MAX_NODES			= 300000;
	private ChangeListener<Number>			sliderChangeListener;
	private int								currentSliderValue	= 1;

	public ScrollableSciomeChartFX(String title, List<ChartDataPack> chartDataPacks, ChartKey[] keys,
			SciomeChartListener chartListener)
	{
		super(title, chartDataPacks, keys, chartListener);

		slider = new Slider();
		slider.setOrientation(Orientation.HORIZONTAL);
		addComponentToEnd(slider);

	}

	@Override
	public void redrawCharts(List<ChartDataPack> chartDataPacks)
	{
		this.getSeriesData().clear();
		super.redrawCharts(chartDataPacks);
		slider.valueProperty().removeListener(sliderChangeListener);
		intializeScrollableChart();
	}

	@SuppressWarnings("unchecked")
	protected void intializeScrollableChart()
	{

		int max = 0;

		for (SciomeSeries<X, Y> series : getSeriesData())
		{
			int countofthings = 0;
			for (SciomeData<X, Y> d : series.getData())
				countofthings++;
			if (countofthings > max)
				max = countofthings;

		}

		if (max - getMaxGraphItems() > 0)
		{
			slider.setMin(1);
			slider.setMax(max - getMaxGraphItems());

			addComponentToEnd(slider);
			maxSlider = max - getMaxGraphItems();
		}
		else
		{
			removeComponent(slider);
		}

		sliderChangeListener = new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val)
			{
				resetChart(new_val.intValue());
			}
		};
		slider.valueProperty().addListener(sliderChangeListener);

		// initialize series.
		for (SciomeSeries<X, Y> series : getSeriesData())
		{
			Series<X, Y> newSeries = new Series<>();
			newSeries.setName(series.getName());

			// a hack to deal with scatter chart not showing legend braphic
			if (getChart() instanceof ScatterChart)
				newSeries.getData().add(new XYChart.Data<>());

			((XYChart<X, Y>) getChart()).getData().add(newSeries);
		}
		resetChart(1);
		slider.setValue(1);

	}

	@SuppressWarnings("unchecked")
	private void resetChart(int slidervalue)
	{

		if (getChart() == null)
			return;
		if (((XYChart<X, Y>) getChart()).getData() == null)
			((XYChart<X, Y>) getChart()).setData(FXCollections.observableList(new ArrayList<>()));
		int i = 0;
		((XYChart) getChart()).setAnimated(false);
		this.currentSliderValue = slidervalue;
		for (Series<X, Y> series : ((XYChart<X, Y>) getChart()).getData())
		{
			series.getData().clear();
		}
		int totalitemsadded = 0;
		Set<String> labelSet = new HashSet<>();
		int maxg = getMaxGraphItems();
		int maxgwithSlider = (int) slider.getMax() + getMaxGraphItems();

		for (i = slidervalue - 1; i < maxgwithSlider; i++)
		{
			int seriesindex = 0;
			for (SciomeSeries<X, Y> series : getSeriesData())
			{
				if (i < series.getData().size() && i >= 0)
				{
					SciomeData<X, Y> data = series.getData().get(i);
					if (totalitemsadded > maxg)
					{
						if (data.getExtraValue() != null
								&& !labelSet.contains(data.getExtraValue().toString()))
							continue;
					}
					XYChart.Data<X, Y> xyData = new XYChart.Data<>(data.getXValue(), data.getYValue());
					xyData.setNode(getNode(series.getName(), data.getExtraValue().toString(), seriesindex));
					int indextoadd = ((XYChart<X, Y>) getChart()).getData().get(seriesindex).getData().size();
					if (this.addDataAtTop)
						indextoadd = 0;
					((XYChart<X, Y>) getChart()).getData().get(seriesindex).getData().add(indextoadd, xyData);
					xyData.setExtraValue(data.getExtraValue());

					if (data.getExtraValue() != null)
						labelSet.add(data.getExtraValue().toString());
				}
				seriesindex++;
			}
			totalitemsadded++;

		}

	}

	/*
	 * 
	 */
	protected Map<String, Integer> getCountMap()
	{
		// create count map because in multiple data comparison, I only care about
		// shared data labels
		Map<String, Integer> countMap = new HashMap<>();
		int maxPerPack = 0;
		if (getChartDataPacks().size() > 0)
			maxPerPack = MAX_NODES / getChartDataPacks().size();

		for (ChartDataPack chartDataPack : getChartDataPacks())
		{
			int count = 0;
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
				count++;
				if (count > maxPerPack)
					break;
			}
		}
		return countMap;
	}

	/*
	 * the goal of this sorting mechanism is to make multiple comparison objects appear in the order of high
	 * density to low density.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void sortSeriesWithExtraValue(SciomeSeries series1)
	{
		series1.getData().sort(new Comparator<SciomeData>() {

			@Override
			public int compare(SciomeData o1, SciomeData o2)
			{
				int c;
				ChartExtraValue ce2 = (ChartExtraValue) o2.getExtraValue();
				ChartExtraValue ce1 = (ChartExtraValue) o1.getExtraValue();
				c = ce2.count.compareTo(ce1.count);
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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void sortSeriesX(SciomeSeries series1)
	{
		series1.getData().sort(new Comparator<SciomeData>() {

			@Override
			public int compare(SciomeData o1, SciomeData o2)
			{
				int c;
				ChartExtraValue ce2 = (ChartExtraValue) o2.getExtraValue();
				ChartExtraValue ce1 = (ChartExtraValue) o1.getExtraValue();
				Double value2 = (Double) o2.getXValue();
				Double value1 = (Double) o1.getXValue();
				c = value1.compareTo(value2);
				if (c == 0)
					c = ce1.label.compareTo(ce2.label);
				return c;
			}
		});

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void sortSeriesY(SciomeSeries series1)
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

	/*
	 * implementing classes must return a node. It should store enough information to return a node based on
	 * the seriesname and the data point label
	 */
	protected abstract Node getNode(String seriesName, String dataPointLabel, int seriesIndx);

	protected void putNodeInformation(String key, NodeInformation ni)
	{
		nodeInfoMap.put(key, ni);
	}

	protected NodeInformation getNodeInformation(String key)
	{
		return nodeInfoMap.get(key);
	}

	protected class NodeInformation
	{

		public Object	object;
		public boolean	invisible;

		public NodeInformation(Object o, boolean i)
		{
			object = o;
			invisible = i;
		}
	}

}
