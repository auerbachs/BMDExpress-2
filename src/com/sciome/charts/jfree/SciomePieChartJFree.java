package com.sciome.charts.jfree;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

import com.sciome.bmdexpress2.mvp.model.ChartKey;
import com.sciome.charts.SciomeChartListener;
import com.sciome.charts.SciomePieChart;
import com.sciome.charts.data.ChartConfiguration;
import com.sciome.charts.data.ChartDataPack;

import javafx.scene.Node;
import javafx.scene.chart.PieChart;

public class SciomePieChartJFree extends SciomePieChart {

	public SciomePieChartJFree(Map<String, Double> pieDataMap, Map<String, List<Object>> pieObjectMap,
			List<ChartDataPack> packs, String title, SciomeChartListener listener) {
		super(pieDataMap, pieObjectMap, packs, title, listener);
	}

	@Override
	public void reactToChattingCharts() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void markData(Set<String> markings) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected Node generateChart(ChartKey[] keys, ChartConfiguration chartConfig) {
		DefaultPieDataset dataset = new DefaultPieDataset();
		Double total = 0.0;
		for (Entry<String, Double> entry : pieDataMap.entrySet())
		{
			total += entry.getValue();
		}

		DecimalFormat formatter = new DecimalFormat("#.#");
		for (Entry<String, Double> entry : pieDataMap.entrySet())
		{
			Double avg = entry.getValue() * 100.0 / total;
			dataset.setValue(entry.getKey() + "\n(" + formatter.format(avg) + "%)", entry.getValue());
		}
		
		JFreeChart chart = ChartFactory.createPieChart(getTitle(), dataset);
		
		SciomeChartViewer chartView = new SciomeChartViewer(chart);
		chartView.getCanvas().setOnScroll(null);
		return chartView;
	}


	@Override
	public List<String> getLinesToExport() {
		List<String> returnList = new ArrayList<>();
		StringBuilder sb = new StringBuilder();

		sb.append("label");
		sb.append("\t");
		sb.append("value");
		returnList.add(sb.toString());

		for (final PieChart.Data data : ((PieChart) getChart()).getData())
		{
			sb.setLength(0);
			String label = data.getName().replaceAll("\n", " ");
			sb.append(label);
			sb.append("\t");
			sb.append(data.getPieValue());

			returnList.add(sb.toString());

		}

		return returnList;
	}
}
