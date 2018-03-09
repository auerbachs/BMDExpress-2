package com.sciome.charts;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.sciome.bmdexpress2.mvp.model.ChartKey;
import com.sciome.charts.data.ChartData;
import com.sciome.charts.data.ChartDataPack;
import com.sciome.charts.export.ChartDataExporter;
import com.sciome.charts.model.SciomeData;
import com.sciome.charts.model.SciomeSeries;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Tooltip;

/*
 * 
 */
public abstract class SciomeAccumulationPlot extends SciomeChartBase<Number, Number>
		implements ChartDataExporter
{
	protected final static Integer	MAX_ACCUMULATION_BEFORE_MODULUS	= 300;
	protected final static Integer	MOD_AFTER_REACH_MAX				= 20;
	protected final static Integer	MAX_TO_POPUP					= 100;
	protected final static Integer	MAX_PREV_OBJECTS_TO_STORE		= 0;
	// protected CheckBox unBinCheckBox = new CheckBox(
	// "Show All Data");
	protected Tooltip				toolTip							= new Tooltip("");

	protected Set<String>			genesToHighLight				= new HashSet<>();

	public SciomeAccumulationPlot(String title, List<ChartDataPack> chartDataPacks, ChartKey key,
			Double bucketsize, boolean allowXAxisSlider, boolean allowYAxisSlider,
			SciomeChartListener chartListener)
	{
		super(title, chartDataPacks, new ChartKey[] { key }, allowXAxisSlider, allowYAxisSlider,
				chartListener);

		getLogXAxis().setSelected(true);
		getLogYAxis().setSelected(false);
		// this chart defines how the axes can be edited by the user in the chart configuration.
		showLogAxes(true, true, false, false);
		showChart();

		getLogXAxis().selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val)
			{
				showChart();
			}
		});

		getLogYAxis().selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val)
			{
				showChart();
			}
		});

	}

	// join objects together to show a list of lines that show values of the points being plotted
	// the objects and values need to be in alignment to ensure that the value they represent
	// is properly appended next to it in the popup.
	protected String joinObjects(List<Object> objects, Double accumulation, List<Double> values, String key,
			int max)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("Accumulation = ");
		sb.append(accumulation);
		sb.append("\n");
		int i = 0;
		for (Object obj : objects)
		{
			sb.append(obj);
			sb.append("  ");
			sb.append(key);
			sb.append(" = ");
			sb.append(values.get(i));
			sb.append(System.getProperty("line.separator"));
			if (i > max)
			{
				sb.append("....");
				break;
			}
			i++;
		}
		return sb.toString();
	}

	protected String joinAllObjects(List<Object> objects, Double accumulation, List<Double> values,
			String key)
	{
		return joinObjects(objects, accumulation, values, key, 2000000000);

	}

	@Override
	protected boolean isXAxisDefineable()
	{
		return true;
	}

	@Override
	protected boolean isYAxisDefineable()
	{
		return true;
	}

	@Override
	protected void redrawChart()
	{
		showChart();

	}

	/*
	 * fill up the sciome series data structure so implementing classes can use it to create charts
	 */
	@Override
	protected void convertChartDataPacksToSciomeSeries(ChartKey[] keys, List<ChartDataPack> chartPacks)
	{

		ChartKey key = keys[0];
		List<SciomeSeries<Number, Number>> seriesData = new ArrayList<>();

		for (ChartDataPack chartDataPack : getChartDataPacks())
		{
			List<ChartData> doubleList = new ArrayList<>();
			for (ChartData chartData : chartDataPack.getChartData())
			{
				Double dataPoint = (Double) chartData.getDataPoints().get(key);
				if (dataPoint == null)
					continue;
				doubleList.add(chartData);
			}

			doubleList.sort(new Comparator<ChartData>() {
				@Override
				public int compare(ChartData o1, ChartData o2)
				{
					return ((Double) o1.getDataPoints().get(key))
							.compareTo((Double) o2.getDataPoints().get(key));
				}
			});

			int i = 0;
			SciomeSeries<Number, Number> series = new SciomeSeries<>();
			series.setName(chartDataPack.getName());
			Double accumulation = 0.0;

			int count = 0;
			Double currentValue = null;
			List<Object> charttableObjectsMasterList = new ArrayList<>();
			List<Object> charttableObjects = new ArrayList<>();
			/*
			 * start adding accumulation values
			 */

			// a list that will store the values associated with the object.
			List<Double> valuesList = new ArrayList<>();
			for (ChartData value : doubleList)
			{
				Double newValue = (Double) value.getDataPoints().get(key);
				if (!newValue.equals(currentValue) && currentValue != null)
				{

					if (charttableObjects.size() == 1) // we are in the area before the modulus kicks in.
					{
						int adds = 0;
						int j = i - 1;
						while (adds < MAX_PREV_OBJECTS_TO_STORE && j >= 0)
						{
							charttableObjects.add(charttableObjectsMasterList.get(j));
							j--;
							adds++;
						}
					}
					AccumulationData theData = new AccumulationData("", currentValue, accumulation,
							charttableObjects, valuesList);
					series.getData().add(theData);
					charttableObjects = new ArrayList<>();
					valuesList = new ArrayList<>();
				}

				count++;
				valuesList.add(newValue);
				charttableObjects.add(value.getCharttableObject());
				charttableObjectsMasterList.add(value.getCharttableObject());
				accumulation++;
				currentValue = newValue;
				i++;
			}
			// get the last one
			if (currentValue != null)
			{
				AccumulationData theData = new AccumulationData("", currentValue, accumulation,
						charttableObjects, valuesList);
				series.getData().add(theData);
			}
			seriesData.add(series);

		}

		setSeriesData(seriesData);

	}

	/*
	 * extend the SciomeData object so we can store a list of values associated with the Object (which is a
	 * list of categories)
	 */
	protected class AccumulationData extends SciomeData<Number, Number>
	{

		private List<Double> valuesList;

		public AccumulationData(String n, Number x, Number y, Object o, List<Double> v)
		{
			super(n, x, y, o);
			this.valuesList = v;
		}

		public List<Double> getValuesList()
		{
			return valuesList;
		}

	}

	/*
	 * implement the getting of lines that need to be exported.
	 */
	@Override
	public List<String> getLinesToExport()
	{

		List<String> returnList = new ArrayList<>();
		StringBuilder sb = new StringBuilder();

		sb.append("series");
		sb.append("\t");
		sb.append("x");
		sb.append("\t");
		sb.append("y");
		sb.append("\t");
		sb.append("components delimited by ///");
		returnList.add(sb.toString());
		for (SciomeSeries<Number, Number> seriesData : getSeriesData())
		{
			for (SciomeData<Number, Number> xychartData : seriesData.getData())
			{
				sb.setLength(0);
				Double X = xychartData.getXValue().doubleValue();
				Double Y = xychartData.getYValue().doubleValue();
				List extraValue = (List) xychartData.getExtraValue();

				StringBuilder components = new StringBuilder();
				for (Object obj : extraValue)
				{
					if (components.length() > 0)
						components.append("///");
					components.append(obj.toString());
				}

				sb.append(seriesData.getName());
				sb.append("\t");
				sb.append(X);
				sb.append("\t");
				sb.append(Y);
				sb.append("\t");
				sb.append(components.toString());

				returnList.add(sb.toString());

			}
		}

		return returnList;

	}

}
