package com.sciome.charts.venndis;

import java.util.List;
import java.util.Set;

import com.sciome.bmdexpress2.mvp.model.ChartKey;
import com.sciome.charts.SciomeChartBase;
import com.sciome.charts.SciomeChartListener;
import com.sciome.charts.data.ChartConfiguration;

import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

public class SciomeVennDiagram extends SciomeChartBase{

	public SciomeVennDiagram(String title, List chartDataPacks, ChartKey key, SciomeChartListener chartListener) {
		super(title, chartDataPacks, new ChartKey[] {key}, chartListener);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void reactToChattingCharts() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void markData(Set markings) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected Node generateChart(ChartKey[] keys, ChartConfiguration chartConfiguration) {
		BorderPane borderPane = new BorderPane();

		//Make venn calc and set data
		VennCalc vennCalc = new VennCalc();
		vennCalc.setA("A\na\nb\nc");
		vennCalc.setB("B\nd\ne\na");
		vennCalc.countVenn();
		vennCalc.setEulerType();
				
		//Make appropriate euler or venn diagram
		VennDiagram diagram;
		switch(getSeriesData().size()) {
			case 1:
				diagram = new Euler1(borderPane, vennCalc);
				break;
			case 2:
				diagram = new Euler2(borderPane, vennCalc);
				break;
			case 3:
				diagram = new Euler3(borderPane, vennCalc);
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
		//For testing only
		Venn2 euler = new Venn2(borderPane, vennCalc);
		return borderPane;
	}

	@Override
	protected boolean isXAxisDefineable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean isYAxisDefineable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void redrawChart() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void convertChartDataPacksToSciomeSeries(ChartKey[] keys, List chartPacks) {
		// TODO Auto-generated method stub
		
	}

}
