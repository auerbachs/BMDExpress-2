package com.sciome.charts;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sciome.charts.data.ChartData;
import com.sciome.charts.data.ChartDataPack;
import com.sciome.charts.export.ChartDataExporter;
import com.sciome.charts.javafx.ScrollableSciomeChartFX;
import com.sciome.charts.model.SciomeData;
import com.sciome.charts.model.SciomeSeries;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Tooltip;

public abstract class SciomeRangePlot extends ScrollableSciomeChartFX<Number, String>
		implements ChartDataExporter
{

	protected Tooltip	toolTip		= new Tooltip("");
	protected final int	MAXITEMS	= 20;

	@SuppressWarnings("unchecked")
	public SciomeRangePlot(String title, List<ChartDataPack> chartDataPacks, String minKey, String maxKey,
			String lowKey, String highKey, String middleKey, SciomeChartListener chartListener)
	{
		super(title, chartDataPacks, new String[] { minKey, maxKey, lowKey, highKey, middleKey },
				chartListener);

		this.addDataAtTop = true;

		getLogXAxis().selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val)
			{
				initChart();
			}
		});
		getLockXAxis().selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val)
			{
				initChart();
			}
		});

		// this chart defines how the axes can be edited by the user in the chart configuration.
		showLogAxes(true, false, true, false);
		initChart();
	}

	private void initChart()
	{
		showChart();
		setMaxGraphItems(MAXITEMS);
		intializeScrollableChart();
	}

	@Override
	protected boolean isXAxisDefineable()
	{
		return true;
	}

	@Override
	protected boolean isYAxisDefineable()
	{
		return false;
	}

	@Override
	protected void redrawChart()
	{
		initChart();

	}

	/*
	 * fill up the sciome series data structure so implementing classes can use it to create charts
	 */
	@Override
	protected void convertChartDataPacksToSciomeSeries(String[] keys, List<ChartDataPack> chartPacks)
	{
		String minKey = keys[0];
		String maxKey = keys[1];
		String lowKey = keys[2];
		String key = keys[3];
		String middleKey = keys[4];
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

		int maxPerPack = 0;
		if (getChartDataPacks().size() > 0)
			maxPerPack = MAX_NODES / getChartDataPacks().size();
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

		List<SciomeSeries<Number, String>> seriesData = new ArrayList<>();
		for (ChartDataPack chartDataPack : getChartDataPacks())
		{
			SciomeSeries<Number, String> series1 = new SciomeSeries<>(chartDataPack.getName());

			Set<String> chartLabelSet = new HashSet<>();

			for (ChartData chartData : chartDataPack.getChartData())
			{
				Double dataPointValue = (Double) chartData.getDataPoints().get(key);

				if (dataPointValue == null)
					continue;

				sum += dataPointValue;

				Double dataPointValueMinKey = (Double) chartData.getDataPoints().get(minKey);
				Double dataPointValueMaxKey = (Double) chartData.getDataPoints().get(maxKey);
				Double dataPointValueMiddleKey = (Double) chartData.getDataPoints().get(middleKey);

				chartLabelSet.add(chartData.getDataPointLabel());
				SciomeData<Number, String> xyData = new SciomeData<>(chartData.getDataPointLabel(),
						dataPointValue, chartData.getDataPointLabel(),
						new RangePlotExtraValue(chartData.getDataPointLabel(),
								countMap.get(chartData.getDataPointLabel()), dataPointValueMinKey,
								dataPointValueMaxKey, dataPointValueMiddleKey,
								chartData.getCharttableObject().toString(), chartData.getCharttableObject()));

				series1.getData().add(xyData);

				putNodeInformation(chartDataPack.getName() + chartData.getDataPointLabel(),
						new NodeInformation(chartData.getCharttableObject(), false));

				// too many nodes
				if (count > maxPerPack)
					break;

			}

			// add empty values for multiple datasets. When comparing multiple
			// data sets, it comes in handy for scrolling to just have empty
			// data points when the data set doesn't represent a label
			for (String chartedKey : countMap.keySet())
			{
				if (!chartLabelSet.contains(chartedKey))
				{
					SciomeData<Number, String> xyData = new SciomeData<>(chartedKey, avg, chartedKey,
							new RangePlotExtraValue(chartedKey, countMap.get(chartedKey), avg, avg, avg, "",
									null));

					series1.getData().add(xyData);
					putNodeInformation(chartDataPack.getName() + chartedKey, new NodeInformation(null, true));
				}
			}

			if (seriesData.size() > 0)
				sortSeriesWithPrimarySeries(series1, (SciomeSeries) (seriesData.get(0)));
			else
				sortSeriesX(series1);

			seriesData.add(series1);

		}

		setSeriesData(seriesData);

	}

	/** Data extra values for storing close, high and low. */
	@SuppressWarnings("rawtypes")
	protected class RangePlotExtraValue extends ChartExtraValue
	{
		private Double	min;
		private Double	max;
		private Double	high;
		private String	description;

		public RangePlotExtraValue(String label, Integer count, Double min, Double max, Double high,
				String description, Object userData)
		{
			super(label, count, userData);
			this.min = min;
			this.max = max;
			this.high = high;
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

		public Double getHigh()
		{
			return high;
		}

		public String getDescription()
		{
			return description;
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
		for (Object obj : getSeriesData())
		{
			SciomeSeries sData = (SciomeSeries) obj;
			for (Object d : sData.getData())
			{
				SciomeData xychartData = (SciomeData) d;
				RangePlotExtraValue extraValue = (RangePlotExtraValue) xychartData.getExtraValue();
				if (extraValue.getDescription().equals("")) // this means it's a faked value for showing
															// multiple
					// datasets together. skip it
					continue;
				sb.setLength(0);

				Double X = (Double) xychartData.getXValue();
				String Y = (String) xychartData.getYValue();

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

}
