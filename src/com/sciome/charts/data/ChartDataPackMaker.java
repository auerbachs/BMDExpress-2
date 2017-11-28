package com.sciome.charts.data;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import com.sciome.charts.annotation.ChartableData;
import com.sciome.charts.annotation.ChartableDataLabel;
import com.sciome.charts.annotation.ChartableDataPointLabel;
import com.sciome.filter.DataFilterPack;

/*
 * Make a package of data that can be passed to charts.
 * Takes a dataFilterPack which defines filters for the objects that are used to create the data.
 * 
 */
public class ChartDataPackMaker<T, S>
{

	GenericChartDataExtractor	chartDataExtractor;
	private DataFilterPack		dataFilterPack;

	public ChartDataPackMaker(DataFilterPack dataFilterPack)
	{
		this.dataFilterPack = dataFilterPack;
	}

	/*
	 * generate data packs. loop through the objects and pass them to the generateDataPack method.
	 */
	public List<ChartDataPack> generateDataPacks(List<T> objectsForChart)
	{
		List<ChartDataPack> dataPackList = new ArrayList<>();

		for (Object obj : objectsForChart)
		{
			ChartDataPack dataPack = generateDataPack(obj);
			if (dataPack != null)
				dataPackList.add(dataPack);
		}

		return dataPackList;
	}

	/*
	 * use annotation and reflection to figure out which method returns a list of objects that have data
	 * points.
	 */
	public ChartDataPack generateDataPack(Object object)
	{
		// get the method that returns the chartable data points.
		Method getChartableDataMethod = GenericChartDataExtractor.getAnnotatedMethod(object.getClass(),
				ChartableData.class);

		if (getChartableDataMethod == null)
			return null;

		// now invoke the metohd to get a list of objects
		// with the datpoints.
		Object chartableDataObjects = null;
		try
		{
			chartableDataObjects = getChartableDataMethod.invoke(object);
		}
		catch (Exception e)
		{}

		// make sure we are getting a List back.
		if (chartableDataObjects == null || !(chartableDataObjects instanceof List))
			return null;

		// make sure the list is not empty.
		if (((List) chartableDataObjects).size() == 0)
		{
			return null;
		}

		// get the class of the first object of the list. we are hoping
		// that it is one that has ChartableDataPoint annotations.
		Class chartableDataObjectClass = ((List) chartableDataObjects).get(0).getClass();

		// create the chart extractor class
		GenericChartDataExtractor chartDataExtractor = new GenericChartDataExtractor(
				chartableDataObjectClass);

		List<ChartData> chartDataList = new ArrayList<>();

		// The keys are part of the ChartableDataPoint definition
		List<String> dataPointKeys = chartDataExtractor.getKeys();

		for (Object dataPointObject : ((List) chartableDataObjects))
		{
			// does it pass the filter?
			if (dataFilterPack != null && !dataFilterPack.passesFilter(dataPointObject))
			{
				continue;
			}

			// start seting up the ChartData object.
			ChartData<S> chartData = new ChartData<>();
			chartData.setCharttableObject((S) dataPointObject);
			chartData.setDataPoints(new HashMap<>());

			// grabe the name of this data
			Method chartableDataPointLabelMethod = GenericChartDataExtractor
					.getAnnotatedMethod(dataPointObject.getClass(), ChartableDataPointLabel.class);

			// if no name then forget about adding this to the set.
			if (chartableDataPointLabelMethod == null)
				continue;
			try
			{
				chartData.setDataPointLabel((String) chartableDataPointLabelMethod.invoke(dataPointObject));
			}
			catch (Exception e)
			{
				// if there is a problem calling the method, then forget about it.
				continue;
			}

			// look at the keys and start getting the values via the key's referenced method
			for (String key : chartDataExtractor.getKeys())
			{
				Double value = chartDataExtractor.getDataPointValue(dataPointObject, key);
				if (value == null)
					continue;

				// put the values in the ChartData data point Map.
				chartData.getDataPoints().put(key, value);
			}

			// now add this record to chartdatalist
			// chartData contains several values which can be plotted based on
			// programatic or user selection.
			chartDataList.add(chartData);

		}

		chartDataList.sort(new Comparator<ChartData>() {

			@Override
			public int compare(ChartData o1, ChartData o2)
			{
				return o1.getDataPointLabel().compareTo(o2.getDataPointLabel());
			}
		});

		// finish building the chartdatpack
		ChartDataPack chartDataPack = new ChartDataPack(chartDataList, dataPointKeys);

		Method getChartableDataNameMethod = GenericChartDataExtractor.getAnnotatedMethod(object.getClass(),
				ChartableDataLabel.class);

		if (getChartableDataNameMethod == null)
			return null;

		String dataPackName = "";
		try
		{
			dataPackName = (String) getChartableDataNameMethod.invoke(object);
		}
		catch (Exception e)
		{}
		chartDataPack.setName(dataPackName);

		return chartDataPack;

	}

}
