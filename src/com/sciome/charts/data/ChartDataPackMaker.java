package com.sciome.charts.data;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisDataSet;
import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisRow;
import com.sciome.bmdexpress2.mvp.model.ChartKey;
import com.sciome.bmdexpress2.mvp.model.CombinedDataSet;
import com.sciome.filter.DataFilterPack;

/*
 * Make a package of data that can be passed to charts.
 * Takes a dataFilterPack which defines filters for the objects that are used to create the data.
 * 
 */
public class ChartDataPackMaker
{

	private DataFilterPack dataFilterPack;

	public ChartDataPackMaker(DataFilterPack dataFilterPack)
	{
		this.dataFilterPack = dataFilterPack;
	}

	/*
	 * generate data packs. loop through the objects and pass them to the generateDataPack method.
	 */
	public List<ChartDataPack> generateDataPacks(List<BMDExpressAnalysisDataSet> objectsForChart,
			Set<ChartKey> useTheseKeysOnly, Set<ChartKey> mathedChartKeys, ChartKey labelKey)
	{
		List<ChartDataPack> dataPackList = new ArrayList<>();

		List<BMDExpressAnalysisDataSet> splitCombined = new ArrayList<>();
		if (objectsForChart.get(0) instanceof CombinedDataSet)
		{
			String currAnalysis = "";
			CombinedDataSet currSet = null;
			for (BMDExpressAnalysisRow row : ((CombinedDataSet) objectsForChart.get(0)).getAnalysisRows())
			{
				// assume the first column is anlaysis name and analyses are grouped together.
				if (!currAnalysis.equals(row.getRow().get(0).toString()))
				{
					currAnalysis = row.getRow().get(0).toString();
					currSet = new CombinedDataSet(
							((CombinedDataSet) objectsForChart.get(0)).getColumnHeader(), currAnalysis);

					splitCombined.add(currSet);
				}
				if (currSet != null)
					currSet.getAnalysisRows().add(row);

			}
		}
		if (splitCombined.size() > 0)
			objectsForChart = splitCombined;
		for (BMDExpressAnalysisDataSet obj : objectsForChart)
		{
			ChartDataPack dataPack = generateDataPack(obj, useTheseKeysOnly, mathedChartKeys, labelKey);
			if (dataPack != null)
				dataPackList.add(dataPack);
		}

		return dataPackList;
	}

	/*
	 * use annotation and reflection to figure out which method returns a list of objects that have data
	 * points.
	 */
	public ChartDataPack generateDataPack(BMDExpressAnalysisDataSet object, Set<ChartKey> useTheseKeysOnly,
			Set<ChartKey> mathedChartKeys, ChartKey labelKey)
	{

		Set<ChartKey> chartKeys = new HashSet<>();
		if (useTheseKeysOnly == null || useTheseKeysOnly.size() == 0)
			for (String header : object.getColumnHeader())
				chartKeys.add(new ChartKey(header, null));
		else
			chartKeys.addAll(useTheseKeysOnly);

		if (mathedChartKeys != null)
			chartKeys.addAll(mathedChartKeys);

		List<ChartData> chartDataList = new ArrayList<>();
		int i = -1;
		for (BMDExpressAnalysisRow row : object.getAnalysisRows())
		{

			i++;
			// filter out rows that do not pass the filter criteria
			if (dataFilterPack != null && !dataFilterPack.passesFilter(row))
				continue;

			ChartData chartData = new ChartData();

			chartData.setCharttableObject(row.getObject());
			chartData.setDataPoints(new HashMap<>());
			String label = "";

			Object labelObject = object.getValueForHeaderAt(labelKey, i);
			if (labelObject == null)
				continue;
			label = labelObject.toString();
			chartData.setDataPointLabel(label);

			for (ChartKey key : chartKeys)
			{

				Object value = object.getValueForHeaderAt(key, i);
				if (value == null)
					continue;
				Double doubleValue = null;
				if (value instanceof Integer)
					doubleValue = ((Integer) value).doubleValue();
				else if (value instanceof Number)
					doubleValue = ((Number) value).doubleValue();
				else
					continue;

				// this will apply math transformation to the value
				doubleValue = key.getValue(doubleValue);

				chartData.getDataPoints().put(key, doubleValue);

			}
			chartDataList.add(chartData);

		}

		chartDataList.sort(new Comparator<ChartData>() {

			@Override
			public int compare(ChartData o1, ChartData o2)
			{
				return o1.getDataPointLabel().toLowerCase().compareTo(o2.getDataPointLabel().toLowerCase());
			}
		});

		ChartDataPack chartDataPack = new ChartDataPack(chartDataList, new ArrayList<>(chartKeys));
		chartDataPack.setName(object.getName());

		return chartDataPack;

	}

}
