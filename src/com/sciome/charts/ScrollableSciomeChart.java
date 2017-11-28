package com.sciome.charts;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sciome.charts.data.ChartData;
import com.sciome.charts.data.ChartDataPack;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;

/*
 * provide common support for scrolling through chart data. Platform chooser
 */
public abstract class ScrollableSciomeChart<X, Y> extends SciomeChartBase
{

	// map that keeps track of enough information to instantiate a node.
	// so we don't have to store large amounts of nodes in memory for scrolling
	private Map<String, NodeInformation>	nodeInfoMap			= new HashMap<>();

	private Slider							slider;
	protected CheckBox						showAllCheckBox;
	protected List<SciomeSeries<X, Y>>		seriesData			= new ArrayList<>();

	// depending on what type of chart, this boolean will
	// tell the system to add data to front of what will be displayed and scrolled through.
	// this is relevent for box and whisker charts where we want the first value to be shown at the top
	protected boolean						addDataAtTop		= false;
	private int								maxSlider;
	final protected int						MAX_NODES			= 300000;
	private ChangeListener<Number>			sliderChangeListener;
	private int								currentSliderValue	= 1;

	public ScrollableSciomeChart(String title, List<ChartDataPack> chartDataPacks,
			SciomeChartListener chartListener)
	{
		super(title, chartDataPacks, chartListener);

		slider = new Slider();
		slider.setOrientation(Orientation.HORIZONTAL);
		showAllCheckBox = new CheckBox(
				"Show all graph nodes.  (The application may slow down if the dataset is huge) ");
		addComponentToEnd(slider);

		showAllCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val)
			{

				if (new_val)
				{
					seriesData.clear();
					showChart();
					warningTooManyNodesLabel.setVisible(false);
					intializeScrollableChart();
				}
				else
				{
					seriesData.clear();
					showChart();
					warningTooManyNodesLabel.setVisible(true);
					intializeScrollableChart();
				}
			}
		});
	}

	@Override
	public void redrawCharts(List<ChartDataPack> chartDataPacks)
	{
		seriesData.clear();
		super.redrawCharts(chartDataPacks);
		slider.valueProperty().removeListener(sliderChangeListener);
		intializeScrollableChart();
	}

	protected void intializeScrollableChart()
	{

		int max = 0;

		for (SciomeSeries<X, Y> series : seriesData)
		{
			int countofthings = 0;
			for (SciomeData d : series.data)
			{
				countofthings++;
			}
			if (countofthings > max)
				max = countofthings;

		}

		if (max - getMaxGraphItems() > 0)
		{
			slider.setMin(1);
			slider.setMax(max - getMaxGraphItems());
			if (!showAllCheckBox.isSelected())
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
		for (SciomeSeries series : seriesData)
		{
			Series newSeries = new Series();
			newSeries.setName(series.name);

			// a hack to deal with scatter chart not showing legend braphic
			if (getChart() instanceof ScatterChart)
				newSeries.getData().add(new XYChart.Data<>());

			((XYChart) getChart()).getData().add(newSeries);
		}
		resetChart(1);
		slider.setValue(1);

	}

	private void resetChart(int slidervalue)
	{

		if (getChart() == null)
			return;
		if (((XYChart<X, Y>) getChart()).getData() == null)
			((XYChart<X, Y>) getChart()).setData(FXCollections.observableList(new ArrayList<>()));
		int i = 0;
		((XYChart) getChart()).setAnimated(false);
		this.currentSliderValue = slidervalue;
		for (Series series : ((XYChart<X, Y>) getChart()).getData())
		{
			series.getData().clear();
		}
		int totalitemsadded = 0;
		Set<String> labelSet = new HashSet<>();
		int maxg = getMaxGraphItems();
		int maxgwithSlider = (int) slider.getMax() + getMaxGraphItems();

		// This means that we are not showing the "showAll" check box. which
		// really means show all. So we reset the max values such that the loop
		// can be correct.
		if (componentIsVisible(showAllCheckBox) && showAllCheckBox.isSelected())
		{
			for (SciomeSeries<X, Y> series : seriesData)
			{
				if (series.getData().size() > maxg)
				{
					maxg += series.getData().size();
					maxgwithSlider = (int) slider.getMax() + series.getData().size();
				}
			}
		}

		for (i = slidervalue - 1; i < maxgwithSlider; i++)
		{
			if (cancel)
				return;
			int seriesindex = 0;
			for (SciomeSeries<X, Y> series : seriesData)
			{
				if (i < series.data.size() && i >= 0)
				{
					SciomeData data = series.data.get(i);
					if (totalitemsadded > maxg)
					{
						if (data.extraValue != null && !labelSet.contains(data.extraValue.toString()))
							continue;
					}
					XYChart.Data xyData = new XYChart.Data<>(data.xValue, data.yValue);
					xyData.setNode(getNode(series.name, data.extraValue.toString(), seriesindex));
					int indextoadd = ((XYChart<X, Y>) getChart()).getData().get(seriesindex).getData().size();
					if (this.addDataAtTop)
						indextoadd = 0;
					((XYChart<X, Y>) getChart()).getData().get(seriesindex).getData().add(indextoadd, xyData);
					xyData.setExtraValue(data.getExtraValue());

					if (data.extraValue != null)
						labelSet.add(data.extraValue.toString());
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
		if (chartDataPacks.size() > 0)
			maxPerPack = MAX_NODES / chartDataPacks.size();

		for (ChartDataPack chartDataPack : chartDataPacks)
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
		series1.data.sort(new Comparator<SciomeData>() {

			@Override
			public int compare(SciomeData o1, SciomeData o2)
			{
				int c;
				ChartExtraValue ce2 = (ChartExtraValue) o2.extraValue;
				ChartExtraValue ce1 = (ChartExtraValue) o1.extraValue;
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
		for (Object sd : primarySeries.data)
			indexMap.put(((SciomeData) sd).getName(), i++);

		series1.data.sort(new Comparator<SciomeData>() {

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
		series1.data.sort(new Comparator<SciomeData>() {

			@Override
			public int compare(SciomeData o1, SciomeData o2)
			{
				int c;
				ChartExtraValue ce2 = (ChartExtraValue) o2.extraValue;
				ChartExtraValue ce1 = (ChartExtraValue) o1.extraValue;
				Double value2 = (Double) o2.getxValue();
				Double value1 = (Double) o1.getxValue();
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
		series1.data.sort(new Comparator<SciomeData>() {

			@Override
			public int compare(SciomeData o1, SciomeData o2)
			{
				int c;
				ChartExtraValue ce2 = (ChartExtraValue) o2.extraValue;
				ChartExtraValue ce1 = (ChartExtraValue) o1.extraValue;
				Double value2 = (Double) o2.getyValue();
				Double value1 = (Double) o1.getyValue();
				c = value1.compareTo(value2);
				if (c == 0)
					c = ce1.label.compareTo(ce2.label);
				return c;
			}
		});

	}

	public void setShowShowAll(boolean showAll)
	{
		if (!showAll)
		{
			removeComponent(showAllCheckBox);
			warningTooManyNodesLabel.setVisible(true);
			// slider.setVisible(true);
			showAllCheckBox.setSelected(false);
			// resetChart(1);
		}
		else
		{
			addComponentToTop(showAllCheckBox);
			resetChart(currentSliderValue);
		}
	}

	/*
	 * implementing classes must return a node. It should store enough information to return a node based on
	 * the seriesname and the data point label
	 */
	protected abstract Node getNode(String seriesName, String dataPointLabel, int seriesIndx);

	/*
	 * create an object to store series data. javafx series data takes up too much space..especially when
	 * there are lots of data
	 */
	protected static class SciomeSeries<X, Y>
	{
		private String					name;
		private List<SciomeData<X, Y>>	data	= new ArrayList<>();

		public SciomeSeries(String n)
		{
			name = n;
		}

		public String getName()
		{
			return name;
		}

		public List<SciomeData<X, Y>> getData()
		{
			return data;
		}

	}

	/*
	 * store objects in custom object. Create javafx data objects as needed because they are expensive. this
	 * object is cheap.
	 */
	protected static class SciomeData<X, Y>
	{
		private String	name;
		private Object	extraValue;
		private X		xValue;
		private Y		yValue;

		public SciomeData(String n, X x, Y y, Object o)
		{
			name = n;
			xValue = x;
			yValue = y;
			extraValue = o;
		}

		public String getName()
		{
			return name;
		}

		public Object getExtraValue()
		{
			return extraValue;
		}

		public X getxValue()
		{
			return xValue;
		}

		public Y getyValue()
		{
			return yValue;
		}

	}

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
