package com.sciome.bmdexpress2.mvp.view.visualization;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.sciome.bmdexpress2.mvp.model.ChartKey;
import com.sciome.bmdexpress2.mvp.model.prefilter.PrefilterResults;
import com.sciome.bmdexpress2.mvp.presenter.visualization.OneWayANOVADataVisualizationPresenter;
import com.sciome.bmdexpress2.mvp.viewinterface.visualization.IDataVisualizationView;
import com.sciome.bmdexpress2.service.VisualizationService;
import com.sciome.bmdexpress2.serviceInterface.IVisualizationService;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;
import com.sciome.charts.SciomeChartBase;
import com.sciome.charts.data.ChartDataPack;
import com.sciome.charts.jfree.SciomeHistogramJFree;
import com.sciome.charts.jfree.SciomeScatterChartJFree;
import com.sciome.filter.DataFilterPack;

public class OriogenDataVisualizationView extends DataVisualizationView implements IDataVisualizationView
{
	private static final String	UNADJUSTED_PVALUE_HISTOGRAM			= "Unadjusted P-Value Histogram";
	private static final String	ADJUSTED_PVALUE_HISTOGRAM			= "Adjusted P-Value Histogram";
	private static final String	BEST_FOLD_CHANGE_HISTOGRAM			= "Best Fold Change Histogram";
	private static final String	BEST_FOLD_CHANGE_UNSIGNED_HISTOGRAM	= "Best Fold Change (Unsigned) Histogram";

	public OriogenDataVisualizationView()
	{
		super();
		IVisualizationService service = new VisualizationService();
		presenter = new OneWayANOVADataVisualizationPresenter(this, service,
				BMDExpressEventBus.getInstance());

		chartCache.put(UNADJUSTED_PVALUE_HISTOGRAM + "-" + PrefilterResults.UNADJUSTED_PVALUE,
				new SciomeHistogramJFree("", new ArrayList<>(),
						new ChartKey(PrefilterResults.UNADJUSTED_PVALUE, null), 20.0, this));

		chartCache.put(ADJUSTED_PVALUE_HISTOGRAM + "-" + PrefilterResults.ADJUSTED_PVALUE,
				new SciomeHistogramJFree("", new ArrayList<>(),
						new ChartKey(PrefilterResults.ADJUSTED_PVALUE, null), 20.0, this));

		chartCache.put(BEST_FOLD_CHANGE_HISTOGRAM + "-" + PrefilterResults.BEST_FOLD_CHANGE,
				new SciomeHistogramJFree("", new ArrayList<>(),
						new ChartKey(PrefilterResults.BEST_FOLD_CHANGE, null), 20.0, this));

		chartCache.put(BEST_FOLD_CHANGE_UNSIGNED_HISTOGRAM + "-" + PrefilterResults.BEST_FOLD_CHANGE_ABS,
				new SciomeHistogramJFree("", new ArrayList<>(),
						new ChartKey(PrefilterResults.BEST_FOLD_CHANGE_ABS, null), 20.0, this));

		chartCache.put(
				"DEFAULT-" + PrefilterResults.BEST_FOLD_CHANGE + PrefilterResults.NEG_LOG_ADJUSTED_PVALUE,
				new SciomeScatterChartJFree("", new ArrayList<>(),
						new ChartKey(PrefilterResults.BEST_FOLD_CHANGE, null),
						new ChartKey(PrefilterResults.NEG_LOG_ADJUSTED_PVALUE, null), false, true, this));
		chartCache.put(
				"DEFAULT-" + PrefilterResults.BEST_FOLD_CHANGE + PrefilterResults.NEG_LOG_UNADJUSTED_PVALUE,
				new SciomeScatterChartJFree("", new ArrayList<>(),
						new ChartKey(PrefilterResults.BEST_FOLD_CHANGE, null),
						new ChartKey(PrefilterResults.NEG_LOG_UNADJUSTED_PVALUE, null), false, true, this));

	}

	@Override
	public void redrawCharts(DataFilterPack pack, List<String> selectedIds)
	{
		String chartKey = cBox.getSelectionModel().getSelectedItem();
		defaultDPack = pack;
		this.selectedIds = selectedIds;
		if (results == null || results.size() == 0)
			return;

		Set<ChartKey> mathedKeys = new HashSet<>();
		mathedKeys.add(new ChartKey(PrefilterResults.ADJUSTED_PVALUE, ChartKey.NEGLOG));
		mathedKeys.add(new ChartKey(PrefilterResults.UNADJUSTED_PVALUE, ChartKey.NEGLOG));
		List<ChartDataPack> chartDataPacks = presenter.getCategoryResultsChartPackData(results, pack,
				selectedIds, mathedKeys, new ChartKey(PrefilterResults.PROBE_ID, null));
		chartsList = new ArrayList<>();

		if (chartKey.equals(UNADJUSTED_PVALUE_HISTOGRAM))
		{
			SciomeChartBase chart = chartCache
					.get(UNADJUSTED_PVALUE_HISTOGRAM + "-" + PrefilterResults.UNADJUSTED_PVALUE);
			chart.redrawCharts(chartDataPacks);

			chartsList.add(chart);
		}
		else if (chartKey.equals(ADJUSTED_PVALUE_HISTOGRAM))
		{
			SciomeChartBase chart = chartCache
					.get(ADJUSTED_PVALUE_HISTOGRAM + "-" + PrefilterResults.ADJUSTED_PVALUE);
			chart.redrawCharts(chartDataPacks);
			chartsList.add(chart);
		}
		else if (chartKey.equals(BEST_FOLD_CHANGE_HISTOGRAM))
		{
			SciomeChartBase chart = chartCache
					.get(BEST_FOLD_CHANGE_HISTOGRAM + "-" + PrefilterResults.BEST_FOLD_CHANGE);
			chart.redrawCharts(chartDataPacks);
			chartsList.add(chart);
		}
		else if (chartKey.equals(BEST_FOLD_CHANGE_UNSIGNED_HISTOGRAM))
		{
			SciomeChartBase chart = chartCache
					.get(BEST_FOLD_CHANGE_UNSIGNED_HISTOGRAM + "-" + PrefilterResults.BEST_FOLD_CHANGE_ABS);
			chart.redrawCharts(chartDataPacks);
			chartsList.add(chart);
		}
		else
		{
			SciomeChartBase chart = chartCache.get("DEFAULT-" + PrefilterResults.BEST_FOLD_CHANGE
					+ PrefilterResults.NEG_LOG_ADJUSTED_PVALUE);
			chart.redrawCharts(chartDataPacks);
			SciomeChartBase chart2 = chartCache.get("DEFAULT-" + PrefilterResults.BEST_FOLD_CHANGE
					+ PrefilterResults.NEG_LOG_UNADJUSTED_PVALUE);
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
