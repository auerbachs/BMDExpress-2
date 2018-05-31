package com.sciome.charts.venndis;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.sciome.bmdexpress2.mvp.model.ChartKey;
import com.sciome.charts.SciomeChartBase;
import com.sciome.charts.SciomeChartListener;
import com.sciome.charts.data.ChartConfiguration;
import com.sciome.charts.data.ChartData;
import com.sciome.charts.data.ChartDataPack;
import com.sciome.charts.model.SciomeData;
import com.sciome.charts.model.SciomeSeries;

import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

public class SciomeVennDiagram extends SciomeChartBase<String, Number> {

	public SciomeVennDiagram(String title, List chartDataPacks, ChartKey key, SciomeChartListener chartListener) {
		super(title, chartDataPacks, new ChartKey[] {key}, chartListener);
	}

	@Override
	public void reactToChattingCharts() {
		//Not needed for this graph
	}

	@Override
	public void markData(Set markings) {
		//Not needed for this graph
	}

	@Override
	protected Node generateChart(ChartKey[] keys, ChartConfiguration chartConfiguration) {
		BorderPane borderPane = new BorderPane();

		//Make venn calc and set data
		VennCalc vennCalc = new VennCalc();

		int count = 1;
		for(SciomeSeries<String, Number> series : getSeriesData())
		{
			StringBuilder dataBuilder = new StringBuilder();
			for(SciomeData<String, Number> data : series.getData()) 
			{
				dataBuilder.append(data.getXValue() + "\n");
			}
			switch(count)
			{
				case 1:
					vennCalc.setA(dataBuilder.toString());
					vennCalc.setHeaderA(series.getName());
					break;
				case 2:
					vennCalc.setB(dataBuilder.toString());
					vennCalc.setHeaderB(series.getName());
					break;
				case 3:
					vennCalc.setC(dataBuilder.toString());
					vennCalc.setHeaderC(series.getName());
					break;
				case 4:
					vennCalc.setD(dataBuilder.toString());
					vennCalc.setHeaderD(series.getName());
					break;
				case 5:
					vennCalc.setE(dataBuilder.toString());
					vennCalc.setHeaderE(series.getName());
					break;
			}
			count++;
		}
		vennCalc.countVenn();
		vennCalc.setEulerType();
		
		//Make appropriate euler or venn diagram
		VennDiagram diagram;
		switch(getSeriesData().size()) {
			case 1:
				diagram = new Euler1(borderPane, vennCalc);
				break;
			case 2:
				//This means all intersections have data (2 circles)
				if(vennCalc.getVennType() == 7) {
					diagram = new Venn2(borderPane, vennCalc);
				} else {
					diagram = new Euler2(borderPane, vennCalc);
				}
				break;
			case 3:
				//This means all intersections have data (3 circles)
				if(vennCalc.getVennType() == 127) {
					diagram = new Venn3(borderPane, vennCalc);
				} else {
					diagram = new Euler3(borderPane, vennCalc);
				}
				break;
			case 4:
				diagram = new Venn4(borderPane, vennCalc);
				break;
			case 5:
				diagram = new Venn5(borderPane, vennCalc);
				break;
			default:
				break;
		}
		
		return borderPane;
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
		
	}

	@Override
	protected void convertChartDataPacksToSciomeSeries(ChartKey[] keys, List chartPacks) {
		List<SciomeSeries<String, Number>> seriesData = new ArrayList<>();
		
		for (ChartDataPack chartDataPack : getChartDataPacks())
		{
			SciomeSeries<String, Number> series = new SciomeSeries<>();
			series.setName(chartDataPack.getName());
			for(ChartData chartData : chartDataPack.getChartData())
			{
				String dataPoint = chartData.getDataPointLabel();
				SciomeData<String, Number> theData = new SciomeData<>(dataPoint, dataPoint, 0, new ChartExtraValue(dataPoint,
								0, chartData.getCharttableObject()));
				series.getData().add(theData);
			}
			seriesData.add(series);
		}
		setSeriesData(seriesData);
	}
	
}