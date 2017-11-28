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

/*
 * 
 */
public abstract class SciomeBarChart extends ScrollableSciomeChartFX<String, Number>
		implements ChartDataExporter
{

	protected Tooltip	toolTip		= new Tooltip("");
	protected final int	MAXITEMS	= 50;

	public SciomeBarChart(String title, List<ChartDataPack> chartDataPacks, String key,
			SciomeChartListener chartListener)
	{
		super(title, chartDataPacks, new String[] { key }, chartListener);
		showLogAxes(false, true, false, true);

		logYAxis.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val)
			{
				initChart();
			}
		});
		lockYAxis.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val)
			{
				initChart();
			}
		});

		initChart();
		intializeScrollableChart();
		setShowShowAll(false);
	}

	private void initChart()
	{
		// this.getSeriesData().clear();
		showChart();
		setMaxGraphItems(MAXITEMS);
		intializeScrollableChart();
	}

	// never show all for this. because it's like a bar chart
	@Override
	public void setShowShowAll(boolean showshowall)
	{
		super.setShowShowAll(false);
	}

	@Override
	protected boolean isXAxisDefineable()
	{
		return false;
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
		sb.append("component");
		returnList.add(sb.toString());
		for (Object obj : this.getSeriesData())
		{
			SciomeSeries sData = (SciomeSeries) obj;
			for (Object d : sData.getData())
			{
				SciomeData xychartData = (SciomeData) d;
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

	/*
	 * fill up the sciome series data structure so implementing classes can use it to create charts
	 */
	@Override
	protected void convertChartDataPacksToSciomeSeries(String[] keys, List<ChartDataPack> chartPacks)
	{
		String key = keys[0];
		Map<String, Integer> countMap = getCountMap();

		int maxPerPack = 0;
		if (getChartDataPacks().size() > 0)
			maxPerPack = MAX_NODES / getChartDataPacks().size();
		List<SciomeSeries<String, Number>> seriesData = new ArrayList<>();
		for (ChartDataPack chartDataPack : getChartDataPacks())
		{
			SciomeSeries<String, Number> series1 = new SciomeSeries<>(chartDataPack.getName());

			Set<String> chartLabelSet = new HashSet<>();
			int count = 0;
			for (ChartData chartData : chartDataPack.getChartData())
			{
				Double dataPointValue = (Double) chartData.getDataPoints().get(key);
				if (dataPointValue == null)
					continue;
				SciomeData<String, Number> xyData = new SciomeData<>(chartData.getDataPointLabel(),
						chartData.getDataPointLabel(), dataPointValue,
						new ChartExtraValue(chartData.getDataPointLabel(),
								countMap.get(chartData.getDataPointLabel()),
								chartData.getCharttableObject()));

				series1.getData().add(xyData);

				chartLabelSet.add(chartData.getDataPointLabel());
				putNodeInformation(chartDataPack.getName() + chartData.getDataPointLabel(),
						new NodeInformation(chartData.getCharttableObject(), false));
				count++;
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
					SciomeData<String, Number> xyData = new SciomeData<>(chartedKey, chartedKey, 0.0,
							new ChartExtraValue(chartedKey, countMap.get(chartedKey), null));

					series1.getData().add(xyData);
					putNodeInformation(chartDataPack.getName() + chartedKey, new NodeInformation(null, true));
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

}
