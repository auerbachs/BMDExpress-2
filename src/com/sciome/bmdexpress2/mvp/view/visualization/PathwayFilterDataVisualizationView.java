package com.sciome.bmdexpress2.mvp.view.visualization;

import java.util.ArrayList;
import java.util.List;

import com.sciome.bmdexpress2.mvp.model.prefilter.PathwayFilterResults;
import com.sciome.bmdexpress2.mvp.presenter.visualization.PathwayFilterDataVisualizationPresenter;
import com.sciome.bmdexpress2.mvp.viewinterface.visualization.IDataVisualizationView;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;
import com.sciome.charts.data.ChartDataPack;
import com.sciome.charts.javafx.SciomeBubbleChart;
import com.sciome.charts.javafx.SciomeHistogram;
import com.sciome.filter.DataFilterPack;

/*
 * take care of the pathway filter visualizations and special features.
 */
public class PathwayFilterDataVisualizationView extends DataVisualizationView
		implements IDataVisualizationView
{

	public PathwayFilterDataVisualizationView()
	{
		super();
		presenter = new PathwayFilterDataVisualizationPresenter(this, BMDExpressEventBus.getInstance());

	}

	@Override
	public void redrawCharts(DataFilterPack pack, List<String> selectedIds)
	{
		defaultDPack = pack;
		String chartKey = cBox.getSelectionModel().getSelectedItem();
		this.selectedIds = selectedIds;
		if (results == null || results.size() == 0)
			return;

		List<ChartDataPack> chartDataPacks = presenter.getCategoryResultsChartPackData(results, pack,
				selectedIds);

		SciomeBubbleChart blc = new SciomeBubbleChart("", chartDataPacks,
				PathwayFilterResults.UNADJUSTED_PVALUE, PathwayFilterResults.GENE_COUNT,
				PathwayFilterResults.PATHWAY_COUNT, this);

		SciomeHistogram barChart = new SciomeHistogram("", chartDataPacks,
				PathwayFilterResults.UNADJUSTED_PVALUE, 20.0, this);
		SciomeHistogram barChart1 = new SciomeHistogram("", chartDataPacks, PathwayFilterResults.FDR_PVALUE,
				20.0, this);
		SciomeHistogram barChart2 = new SciomeHistogram("", chartDataPacks, PathwayFilterResults.FWER_PVALUE,
				20.0, this);
		graphViewAnchorPane.getChildren().clear();

		chartsList = new ArrayList<>();

		chartsList.add(blc);
		chartsList.add(barChart);
		chartsList.add(barChart1);
		chartsList.add(barChart2);

		showCharts();

	}

	@Override
	public List<String> getCannedCharts()
	{
		List<String> resultList = new ArrayList<>();
		resultList.add(DEFAULT_CHARTS);
		return resultList;
	}

}
