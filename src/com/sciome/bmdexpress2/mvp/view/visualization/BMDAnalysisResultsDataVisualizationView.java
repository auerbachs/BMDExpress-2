package com.sciome.bmdexpress2.mvp.view.visualization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisDataSet;
import com.sciome.bmdexpress2.mvp.model.stat.BMDResult;
import com.sciome.bmdexpress2.mvp.model.stat.ProbeStatResult;
import com.sciome.bmdexpress2.mvp.model.stat.StatResult;
import com.sciome.bmdexpress2.mvp.presenter.visualization.BMDAnalysisResultsDataVisualizationPresenter;
import com.sciome.bmdexpress2.mvp.viewinterface.visualization.IDataVisualizationView;
import com.sciome.bmdexpress2.service.VisualizationService;
import com.sciome.bmdexpress2.serviceInterface.IVisualizationService;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;
import com.sciome.charts.SciomeChartBase;
import com.sciome.charts.data.ChartDataPack;
import com.sciome.charts.javafx.SciomeHistogramFX;
import com.sciome.charts.javafx.SciomePieChartFX;
import com.sciome.charts.jfree.SciomeScatterChartJFree;
import com.sciome.filter.DataFilterPack;

/*
 * take care of the BMDResults visualizations and special features.
 */
public class BMDAnalysisResultsDataVisualizationView extends DataVisualizationView
		implements IDataVisualizationView
{
	private final static String	BMDL_HISTOGRAM					= "BMDL Histogram";
	private final static String	BMDU_HISTOGRAM					= "BMDU Histogram";
	private final static String	FIT_PVALUE_HISTOGRAM			= "Fit P-Value Histogram";
	private final static String	FIT_LOG_LIKELIHOOD_HISTOGRAM	= "Log Likelihood Histogram";

	public BMDAnalysisResultsDataVisualizationView()
	{
		super();
		IVisualizationService service = new VisualizationService();
		presenter = new BMDAnalysisResultsDataVisualizationPresenter(this, service, BMDExpressEventBus.getInstance());

		chartCache.put(BMDL_HISTOGRAM + "-" + BMDResult.BMDL, new SciomeHistogramFX("", new ArrayList<>(),
				BMDResult.BMDL, 20.0, BMDAnalysisResultsDataVisualizationView.this));
		chartCache.put(BMDU_HISTOGRAM + "-" + BMDResult.BMDU, new SciomeHistogramFX("", new ArrayList<>(),
				BMDResult.BMDU, 20.0, BMDAnalysisResultsDataVisualizationView.this));
		chartCache.put(FIT_PVALUE_HISTOGRAM + "-" + BMDResult.FIT_PVALUE, new SciomeHistogramFX("",
				new ArrayList<>(), BMDResult.FIT_PVALUE, 20.0, BMDAnalysisResultsDataVisualizationView.this));

		chartCache.put(FIT_LOG_LIKELIHOOD_HISTOGRAM + "-" + BMDResult.FIT_LOG_LIKELIHOOD,
				new SciomeHistogramFX("", new ArrayList<>(), BMDResult.FIT_LOG_LIKELIHOOD, 20.0,
						BMDAnalysisResultsDataVisualizationView.this));

		chartCache.put("DEFAULT-" + BMDResult.BMD + BMDResult.BMDL,
				new SciomeScatterChartJFree("", new ArrayList<>(), BMDResult.BMD, BMDResult.BMDL,
						BMDAnalysisResultsDataVisualizationView.this));
		chartCache.put("DEFAULT-" + BMDResult.BMD, new SciomeHistogramFX("", new ArrayList<>(), BMDResult.BMD,
				20.0, BMDAnalysisResultsDataVisualizationView.this));

	}

	@Override
	public void redrawCharts(DataFilterPack pack, List<String> selectedIds)
	{
		String chartKey = cBox.getSelectionModel().getSelectedItem();
		defaultDPack = pack;
		this.selectedIds = selectedIds;
		if (results == null || results.size() == 0)
			return;

		chartsList = new ArrayList<>();
		List<ChartDataPack> chartDataPacks = presenter.getCategoryResultsChartPackData(results, pack,
				selectedIds);

		if (chartKey.equals(BMDL_HISTOGRAM))
		{
			SciomeChartBase chart = chartCache.get(BMDL_HISTOGRAM + "-" + BMDResult.BMDL);
			chartsList.add(chart);
			chart.redrawCharts(chartDataPacks);
		}
		else if (chartKey.equals(BMDU_HISTOGRAM))
		{
			SciomeChartBase chart = chartCache.get(BMDU_HISTOGRAM + "-" + BMDResult.BMDU);
			chartsList.add(chart);
			chart.redrawCharts(chartDataPacks);
		}
		else if (chartKey.equals(FIT_PVALUE_HISTOGRAM))
		{
			SciomeChartBase chart = chartCache.get(FIT_PVALUE_HISTOGRAM + "-" + BMDResult.FIT_PVALUE);
			chartsList.add(chart);
			chart.redrawCharts(chartDataPacks);
		}
		else if (chartKey.equals(FIT_LOG_LIKELIHOOD_HISTOGRAM))
		{
			SciomeChartBase chart = chartCache
					.get(FIT_LOG_LIKELIHOOD_HISTOGRAM + "-" + BMDResult.FIT_LOG_LIKELIHOOD);
			chartsList.add(chart);
			chart.redrawCharts(chartDataPacks);
		}
		else
		{
			chartsList.add(new SciomePieChartFX(
					BMDAnalysisResultsDataVisualizationView.this.getBMDStatResultCounts(results, pack,
							selectedIds),
					null, chartDataPacks, "BMDS Model Counts", BMDAnalysisResultsDataVisualizationView.this));

			SciomeChartBase chart1 = chartCache.get("DEFAULT-" + BMDResult.BMD + BMDResult.BMDL);
			chartsList.add(chart1);
			chart1.redrawCharts(chartDataPacks);

			SciomeChartBase chart2 = chartCache.get("DEFAULT-" + BMDResult.BMD);
			chartsList.add(chart2);
			chart2.redrawCharts(chartDataPacks);
		}
		graphViewAnchorPane.getChildren().clear();
		graphViewAnchorPane.getChildren().clear();
		showCharts();

	}

	private Map<String, Double> getBMDStatResultCounts(List<BMDExpressAnalysisDataSet> bmdResultss,
			DataFilterPack pack, List<String> selectedIds2)
	{
		Map<String, Double> mapCount = new HashMap<>();

		for (BMDExpressAnalysisDataSet row : bmdResultss)
		{
			if (!(row instanceof BMDResult))
				continue;
			BMDResult bmdResults = (BMDResult) row;
			for (ProbeStatResult probeStatResult : bmdResults.getProbeStatResults())
			{
				if (pack != null && !pack.passesFilter(probeStatResult))
					continue;
				if (selectedIds2 != null
						&& !selectedIds2.contains(probeStatResult.getProbeResponse().getProbe().getId()))
					continue;

				StatResult result = probeStatResult.getBestStatResult();
				if (result == null)
					continue;
				if (mapCount.containsKey(result.toString()))
				{
					mapCount.put(result.toString(), mapCount.get(result.toString()) + 1.0);
				}
				else
				{
					mapCount.put(result.toString(), 1.0);
				}

			}
		}

		return mapCount;
	}

	@Override
	public List<String> getCannedCharts()
	{
		List<String> returnList = new ArrayList<>();
		returnList.add(DEFAULT_CHARTS);
		returnList.add(BMDL_HISTOGRAM);
		returnList.add(BMDU_HISTOGRAM);
		returnList.add(FIT_PVALUE_HISTOGRAM);
		returnList.add(FIT_LOG_LIKELIHOOD_HISTOGRAM);
		return returnList;
	}
}
