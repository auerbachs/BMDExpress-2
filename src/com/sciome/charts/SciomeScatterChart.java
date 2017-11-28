package com.sciome.charts;

import java.util.ArrayList;
import java.util.List;

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
public abstract class SciomeScatterChart extends SciomeChartBase<Number, Number> implements ChartDataExporter
{

	protected Tooltip toolTip = new Tooltip("");

	@SuppressWarnings("rawtypes")
	public SciomeScatterChart(String title, List<ChartDataPack> chartDataPacks, String key1, String key2,
			boolean allowXLogAxis, boolean allowYLogAxis, SciomeChartListener chartListener)
	{
		super(title, chartDataPacks, new String[] { key1, key2 }, chartListener);

		// this chart defines how the axes can be edited by the user in the chart configuration.
		showLogAxes(allowXLogAxis, allowYLogAxis, false, false);
		initChart();

		// re init the chart when users click log x axis
		getLogXAxis().selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val)
			{
				initChart();
			}
		});

		// re init the chart when users click log y axis
		getLogYAxis().selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val)
			{
				initChart();
			}
		});

	}

	@SuppressWarnings("rawtypes")
	public SciomeScatterChart(String title, List<ChartDataPack> chartDataPacks, String key1, String key2,
			SciomeChartListener chartListener)
	{
		this(title, chartDataPacks, key1, key2, true, true, chartListener);
	}

	private void initChart()
	{
		showChart();
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
		initChart();
	}

	/*
	 * fill up the sciome series data structure so implementing classes can use it to create charts
	 */
	@Override
	protected void convertChartDataPacksToSciomeSeries(String[] keys, List<ChartDataPack> chartPacks)
	{
		String key1 = keys[0];
		String key2 = keys[1];

		List<SciomeSeries<Number, Number>> seriesData = new ArrayList<>();
		for (ChartDataPack chartDataPack : getChartDataPacks())
		{

			SciomeSeries<Number, Number> series = new SciomeSeries<>();
			series.setName(chartDataPack.getName());

			for (ChartData chartData : chartDataPack.getChartData())
			{
				Double dataPointValue1 = (Double) chartData.getDataPoints().get(key1);
				Double dataPointValue2 = (Double) chartData.getDataPoints().get(key2);

				if (dataPointValue1 == null || dataPointValue2 == null)
					continue;

				SciomeData<Number, Number> theData = new SciomeData<>(chartData.getDataPointLabel(),
						dataPointValue1, dataPointValue2, new ChartExtraValue(chartData.getDataPointLabel(),
								0, chartData.getCharttableObject()));

				series.getData().add(theData);

			}
			seriesData.add(series);

		}
		setSeriesData(seriesData);

	}

	/*
	 * implement the getting of lines that need to be exported.
	 */
	@SuppressWarnings("unchecked")
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
		sb.append("label");
		returnList.add(sb.toString());
		for (SciomeSeries<Number, Number> seriesData : getSeriesData())
		{
			for (SciomeData<Number, Number> xychartData : seriesData.getData())
			{
				sb.setLength(0);
				ChartExtraValue extraValue = (ChartExtraValue) xychartData.getExtraValue();
				if (extraValue.label.equals("")) // this means it's a faked value for showing multiple
													// datasets together. skip it
					continue;
				sb.setLength(0);

				Double X = (Double) xychartData.getXValue();
				Double Y = (Double) xychartData.getYValue();
				sb.append(seriesData.getName());
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

}
