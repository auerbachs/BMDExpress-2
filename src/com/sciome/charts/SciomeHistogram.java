package com.sciome.charts;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.sciome.charts.data.ChartData;
import com.sciome.charts.data.ChartDataPack;
import com.sciome.charts.export.ChartDataExporter;
import com.sciome.charts.model.SciomeData;
import com.sciome.charts.model.SciomeSeries;

/*
 * 
 */
public abstract class SciomeHistogram extends SciomeChartBase<String, Number> implements ChartDataExporter
{

	protected Double	bucketsize			= 20.0;
	protected final int	MAX_TOOL_TIP_SHOWS	= 20;

	public SciomeHistogram(String title, List<ChartDataPack> chartDataPacks, String key, Double bucketsize,
			SciomeChartListener chartListener)
	{

		super(title, chartDataPacks, new String[] { key }, chartListener);
		this.bucketsize = bucketsize;
		showChart();

	}

	protected String joinAllObjects(List<Object> objects)
	{
		return joinObjects(objects, 2000000000);

	}

	protected String joinObjects(List<Object> objects, int max)
	{
		StringBuilder sb = new StringBuilder();

		int i = 0;
		for (Object obj : objects)
		{
			sb.append(obj);
			sb.append("\n");
			if (i >= max)
			{
				sb.append("....");
				break;
			}
			i++;
		}
		return sb.toString();
	}

	@Override
	protected boolean isXAxisDefineable()
	{
		return false;
	}

	@Override
	protected boolean isYAxisDefineable()
	{
		return false;
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
	protected void convertChartDataPacksToSciomeSeries(String[] keys, List<ChartDataPack> chartPacks)
	{

		String key = keys[0];
		Double max = getMaxMax(key);
		Double min = getMinMin(key);

		if (bucketsize == null)
			bucketsize = 20.0;
		Double bucketSize = (max - min) / bucketsize;
		List<Double> dataPointCounts = new ArrayList<Double>(bucketsize.intValue());

		List<List<Object>> bucketObjects = new ArrayList<>();
		for (int i = 0; i <= bucketsize.intValue(); i++)
			bucketObjects.add(new ArrayList<>());

		List<Double> xDataPoints = new ArrayList<Double>(bucketsize.intValue());
		for (int i = 0; i <= bucketsize.intValue(); i++)
		{
			xDataPoints.add(min + bucketSize * i);
			dataPointCounts.add(0.0);
		}

		// Now put the data in a bucket
		for (ChartDataPack chartDataPack : getChartDataPacks())
		{
			for (ChartData chartData : chartDataPack.getChartData())
			{
				Double dataPoint = (Double) chartData.getDataPoints().get(key);
				if (dataPoint == null)
					continue;

				// which bin?
				for (int i = 0; i <= bucketsize.intValue(); i++)
				{
					if (dataPoint <= min + bucketSize * i)
					{
						dataPointCounts.set(i, dataPointCounts.get(i) + 1.0);
						bucketObjects.get(i).add(chartData.getCharttableObject());
						break;
					}
				}
			}
		}
		List<SciomeSeries<String, Number>> seriesData = new ArrayList<>();
		SciomeSeries<String, Number> series1 = new SciomeSeries<>();
		series1.setName(key);

		DecimalFormat df = new DecimalFormat("#.###");
		for (int i = 0; i <= bucketsize.intValue(); i++)
		{
			SciomeData<String, Number> data = new SciomeData<>("", df.format(xDataPoints.get(i)),
					dataPointCounts.get(i), bucketObjects.get(i));
			series1.getData().add(data);
		}
		seriesData.add(series1);
		setSeriesData(seriesData);

	}

	/*
	 * implement the getting of lines that need to be exported.
	 */
	@Override
	public List<String> getLinesToExport()
	{

		List<String> returnList = new ArrayList<>();
		StringBuilder sb = new StringBuilder();

		sb.append("x");
		sb.append("\t");
		sb.append("y");
		sb.append("\t");
		sb.append("components delimited by ///");
		returnList.add(sb.toString());
		for (SciomeSeries<String, Number> seriesData : getSeriesData())
		{
			for (SciomeData<String, Number> xychartData : seriesData.getData())
			{
				sb.setLength(0);
				String X = (String) xychartData.getXValue();

				Double Y = (Double) xychartData.getYValue();

				List<Object> objects = (List) xychartData.getExtraValue();

				StringBuilder components = new StringBuilder();
				for (Object obj : objects)
				{
					if (components.length() > 0)
						components.append("///");
					components.append(obj.toString());
				}

				sb.append(X);
				sb.append("\t");
				sb.append(Y);
				sb.append("\t");

				sb.append(components);

				returnList.add(sb.toString());

			}
		}

		return returnList;

	}

}
