package com.sciome.charts;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

import com.sciome.bmdexpress2.mvp.model.ChartKey;
import com.sciome.charts.data.ChartData;
import com.sciome.charts.data.ChartDataPack;
import com.sciome.charts.export.ChartDataExporter;
import com.sciome.charts.model.SciomeData;
import com.sciome.charts.model.SciomeSeries;

public abstract class SciomeDensityChart extends SciomeChartBase<Number, Number> implements ChartDataExporter {
	
	private final static int NUM_X_VALUES = 500;
	
	public SciomeDensityChart(String title, List<ChartDataPack> chartDataPacks, ChartKey key,
			SciomeChartListener chartListener) {
		super(title, chartDataPacks, new ChartKey[] {key} , true, false, chartListener);
	}

	@Override
	protected boolean isXAxisDefineable() {
		return false;
	}

	@Override
	protected boolean isYAxisDefineable() {
		return false;
	}

	@Override
	protected void redrawChart() {
		showChart();
	}

	@Override
	protected void convertChartDataPacksToSciomeSeries(ChartKey[] keys, List<ChartDataPack> chartPacks) {
		ChartKey key = keys[0];
		Double max = getMaxMax(key);
		
		List<SciomeSeries<Number, Number>> seriesData = new ArrayList<>();
		for (ChartDataPack chartDataPack : getChartDataPacks())
		{
			SciomeSeries<Number, Number> series = new SciomeSeries<>();
			series.setName(chartDataPack.getName());

			double[] data = new double[chartDataPack.getChartData().size()];
			
			int count = 0;
			for (ChartData chartData : chartDataPack.getChartData())
			{
				Double dataPointValue = (Double) chartData.getDataPoints().get(key);

				if (dataPointValue != null) 
					data[count] = dataPointValue;
				
				count++;
			}

			StandardDeviation std = new StandardDeviation();
			double bandwidth = 1.06 * std.evaluate(data) * Math.pow(data.length, (-1/5));
			bandwidth /= 10;
			
			for(int i = 1; i < NUM_X_VALUES; i++) {
				double x = i * (max/NUM_X_VALUES);
				double sum = 0;
				for(int j = 0; j < data.length; j++) {
					double gaussVal = gaussian((x - data[j]) / bandwidth);
					sum += gaussVal;
				}
				double y = (1/(data.length * bandwidth)) * sum;
				
				SciomeData<Number, Number> point = new SciomeData<>("", x,
						y, chartDataPack.getName());
				
				series.getData().add(point);
			}
			
			seriesData.add(series);

		}
		setSeriesData(seriesData);
	}

	
	private double gaussian(double u) {
		return (Math.exp(((-u * u)/2.0)))/(Math.sqrt(2 * Math.PI));
	}
}
