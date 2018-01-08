package com.sciome.bmdexpress2.mvp.view.visualization;

import java.util.ArrayList;
import java.util.List;

import com.sciome.bmdexpress2.mvp.model.ChartKey;
import com.sciome.bmdexpress2.mvp.model.prefilter.PrefilterResults;
import com.sciome.bmdexpress2.mvp.presenter.visualization.WilliamsTrendDataVisualizationPresenter;
import com.sciome.bmdexpress2.mvp.viewinterface.visualization.IDataVisualizationView;
import com.sciome.bmdexpress2.service.VisualizationService;
import com.sciome.bmdexpress2.serviceInterface.IVisualizationService;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;
import com.sciome.charts.SciomeChartBase;
import com.sciome.charts.data.ChartDataPack;
import com.sciome.charts.jfree.SciomeHistogramJFree;
import com.sciome.charts.jfree.SciomeScatterChartJFree;
import com.sciome.filter.DataFilterPack;

public class WilliamsTrendDataVisualizationView extends DataVisualizationView
		implements IDataVisualizationView
{
	private static final String	UNADJUSTED_PVALUE_HISTOGRAM			= "Unadjusted P-Value Histogram";
	private static final String	ADJUSTED_PVALUE_HISTOGRAM			= "Adjusted P-Value Histogram";
	private static final String	BEST_FOLD_CHANGE_HISTOGRAM			= "Best Fold Change Histogram";
	private static final String	BEST_FOLD_CHANGE_UNSIGNED_HISTOGRAM	= "Best Fold Change (Unsigned) Histogram";

	public WilliamsTrendDataVisualizationView()
	{
		super();
		IVisualizationService service = new VisualizationService();

		presenter = new WilliamsTrendDataVisualizationPresenter(this, service,
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

		chartCache.put("DEFAULT-" + PrefilterResults.BEST_FOLD_CHANGE + PrefilterResults.ADJUSTED_PVALUE,
				new SciomeScatterChartJFree("", new ArrayList<>(),
						new ChartKey(PrefilterResults.BEST_FOLD_CHANGE, null),
						new ChartKey(PrefilterResults.ADJUSTED_PVALUE, ChartKey.NEGLOG), false, true, this));
		chartCache.put("DEFAULT-" + PrefilterResults.BEST_FOLD_CHANGE + PrefilterResults.UNADJUSTED_PVALUE,
				new SciomeScatterChartJFree("", new ArrayList<>(),
						new ChartKey(PrefilterResults.BEST_FOLD_CHANGE, null),
						new ChartKey(PrefilterResults.UNADJUSTED_PVALUE, ChartKey.NEGLOG), false, true,
						this));

	}

	@Override
	public void redrawCharts(DataFilterPack pack)
	{
		String chartKey = cBox.getSelectionModel().getSelectedItem();
		defaultDPack = pack;
		if (results == null || results.size() == 0)
			return;
		chartsList = new ArrayList<>();

		if (chartKey.equals(UNADJUSTED_PVALUE_HISTOGRAM))
		{
			SciomeChartBase chart = chartCache
					.get(UNADJUSTED_PVALUE_HISTOGRAM + "-" + PrefilterResults.UNADJUSTED_PVALUE);

			chartsList.add(chart);
		}
		else if (chartKey.equals(ADJUSTED_PVALUE_HISTOGRAM))
		{
			SciomeChartBase chart = chartCache
					.get(ADJUSTED_PVALUE_HISTOGRAM + "-" + PrefilterResults.ADJUSTED_PVALUE);
			chartsList.add(chart);
		}
		else if (chartKey.equals(BEST_FOLD_CHANGE_HISTOGRAM))
		{
			SciomeChartBase chart = chartCache
					.get(BEST_FOLD_CHANGE_HISTOGRAM + "-" + PrefilterResults.BEST_FOLD_CHANGE);
			chartsList.add(chart);
		}
		else if (chartKey.equals(BEST_FOLD_CHANGE_UNSIGNED_HISTOGRAM))
		{
			SciomeChartBase chart = chartCache
					.get(BEST_FOLD_CHANGE_UNSIGNED_HISTOGRAM + "-" + PrefilterResults.BEST_FOLD_CHANGE_ABS);
			chartsList.add(chart);
		}
		else
		{
			SciomeChartBase chart = chartCache
					.get("DEFAULT-" + PrefilterResults.BEST_FOLD_CHANGE + PrefilterResults.ADJUSTED_PVALUE);
			SciomeChartBase chart2 = chartCache
					.get("DEFAULT-" + PrefilterResults.BEST_FOLD_CHANGE + PrefilterResults.UNADJUSTED_PVALUE);
			chartsList.add(chart);
			chartsList.add(chart2);
		}
		List<ChartDataPack> chartDataPacks = presenter.getBMDAnalysisDataSetChartDataPack(results, pack,
				getUsedChartKeys(), getMathedChartKeys(), new ChartKey(PrefilterResults.PROBE_ID, null));
		showCharts(chartDataPacks);

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
