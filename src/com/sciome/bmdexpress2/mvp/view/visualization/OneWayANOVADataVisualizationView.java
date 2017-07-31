package com.sciome.bmdexpress2.mvp.view.visualization;

import java.util.ArrayList;
import java.util.List;

import com.sciome.bmdexpress2.mvp.model.prefilter.OneWayANOVAResults;
import com.sciome.bmdexpress2.mvp.presenter.visualization.OneWayANOVADataVisualizationPresenter;
import com.sciome.bmdexpress2.mvp.viewinterface.visualization.IDataVisualizationView;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;
import com.sciome.charts.data.ChartDataPack;
import com.sciome.charts.javafx.SciomeChartBase;
import com.sciome.charts.javafx.SciomeHistogram;
import com.sciome.charts.javafx.SciomeScatterChart;
import com.sciome.filter.DataFilterPack;

public class OneWayANOVADataVisualizationView extends DataVisualizationView implements IDataVisualizationView
{

	private static final String	UNADJUSTED_PVALUE_HISTOGRAM			= "Unadjusted P-Value Histogram";
	private static final String	ADJUSTED_PVALUE_HISTOGRAM			= "Adjusted P-Value Histogram";
	private static final String	BEST_FOLD_CHANGE_HISTOGRAM			= "Best Fold Change Histogram";
	private static final String	BEST_FOLD_CHANGE_UNSIGNED_HISTOGRAM	= "Best Fold Change (Unsigned) Histogram";

	public OneWayANOVADataVisualizationView()
	{
		super();
		presenter = new OneWayANOVADataVisualizationPresenter(this, BMDExpressEventBus.getInstance());

		chartCache.put(UNADJUSTED_PVALUE_HISTOGRAM + "-" + OneWayANOVAResults.UNADJUSTED_PVALUE,
				new SciomeHistogram("", new ArrayList<>(), OneWayANOVAResults.UNADJUSTED_PVALUE, 20.0, this));

		chartCache.put(ADJUSTED_PVALUE_HISTOGRAM + "-" + OneWayANOVAResults.ADJUSTED_PVALUE,
				new SciomeHistogram("", new ArrayList<>(), OneWayANOVAResults.ADJUSTED_PVALUE, 20.0, this));

		chartCache.put(BEST_FOLD_CHANGE_HISTOGRAM + "-" + OneWayANOVAResults.BEST_FOLD_CHANGE,
				new SciomeHistogram("", new ArrayList<>(), OneWayANOVAResults.BEST_FOLD_CHANGE, 20.0, this));

		chartCache.put(BEST_FOLD_CHANGE_UNSIGNED_HISTOGRAM + "-" + OneWayANOVAResults.BEST_FOLD_CHANGE_ABS,
				new SciomeHistogram("", new ArrayList<>(), OneWayANOVAResults.BEST_FOLD_CHANGE_ABS, 20.0,
						this));

		chartCache.put(
				"DEFAULT-" + OneWayANOVAResults.BEST_FOLD_CHANGE + OneWayANOVAResults.NEG_LOG_ADJUSTED_PVALUE,
				new SciomeScatterChart("", new ArrayList<>(), OneWayANOVAResults.BEST_FOLD_CHANGE,
						OneWayANOVAResults.NEG_LOG_ADJUSTED_PVALUE, false, true, this));
		chartCache.put(
				"DEFAULT-" + OneWayANOVAResults.BEST_FOLD_CHANGE
						+ OneWayANOVAResults.NEG_LOG_UNADJUSTED_PVALUE,
				new SciomeScatterChart("", new ArrayList<>(), OneWayANOVAResults.BEST_FOLD_CHANGE,
						OneWayANOVAResults.NEG_LOG_UNADJUSTED_PVALUE, false, true, this));

	}

	@Override
	public void redrawCharts(DataFilterPack pack, List<String> selectedIds)
	{
		String chartKey = cBox.getSelectionModel().getSelectedItem();
		defaultDPack = pack;
		this.selectedIds = selectedIds;
		if (results == null || results.size() == 0)
			return;

		List<ChartDataPack> chartDataPacks = presenter.getCategoryResultsChartPackData(results, pack,
				selectedIds);
		chartsList = new ArrayList<>();

		if (chartKey.equals(UNADJUSTED_PVALUE_HISTOGRAM))
		{
			SciomeChartBase chart = chartCache
					.get(UNADJUSTED_PVALUE_HISTOGRAM + "-" + OneWayANOVAResults.UNADJUSTED_PVALUE);
			chart.redrawCharts(chartDataPacks);

			chartsList.add(chart);
		}
		else if (chartKey.equals(ADJUSTED_PVALUE_HISTOGRAM))
		{
			SciomeChartBase chart = chartCache
					.get(ADJUSTED_PVALUE_HISTOGRAM + "-" + OneWayANOVAResults.ADJUSTED_PVALUE);
			chart.redrawCharts(chartDataPacks);
			chartsList.add(chart);
		}
		else if (chartKey.equals(BEST_FOLD_CHANGE_HISTOGRAM))
		{
			SciomeChartBase chart = chartCache
					.get(BEST_FOLD_CHANGE_HISTOGRAM + "-" + OneWayANOVAResults.BEST_FOLD_CHANGE);
			chart.redrawCharts(chartDataPacks);
			chartsList.add(chart);
		}
		else if (chartKey.equals(BEST_FOLD_CHANGE_UNSIGNED_HISTOGRAM))
		{
			SciomeChartBase chart = chartCache
					.get(BEST_FOLD_CHANGE_UNSIGNED_HISTOGRAM + "-" + OneWayANOVAResults.BEST_FOLD_CHANGE_ABS);
			chart.redrawCharts(chartDataPacks);
			chartsList.add(chart);
		}
		else
		{
			SciomeChartBase chart = chartCache.get("DEFAULT-" + OneWayANOVAResults.BEST_FOLD_CHANGE
					+ OneWayANOVAResults.NEG_LOG_ADJUSTED_PVALUE);
			chart.redrawCharts(chartDataPacks);
			SciomeChartBase chart2 = chartCache.get("DEFAULT-" + OneWayANOVAResults.BEST_FOLD_CHANGE
					+ OneWayANOVAResults.NEG_LOG_UNADJUSTED_PVALUE);
			chart2.redrawCharts(chartDataPacks);
			chartsList.add(chart);
			chartsList.add(chart2);
		}

		graphViewAnchorPane.getChildren().clear();
		showCharts();

	}

	@Override
	public List<String> getCannedCharts()
	{
		List<String> resultList = new ArrayList<>();
		resultList.add(DEFAULT_CHARTS);
		resultList.add(UNADJUSTED_PVALUE_HISTOGRAM);
		resultList.add(ADJUSTED_PVALUE_HISTOGRAM);
		resultList.add(BEST_FOLD_CHANGE_HISTOGRAM);
		resultList.add(BEST_FOLD_CHANGE_UNSIGNED_HISTOGRAM);

		return resultList;

	}

}
