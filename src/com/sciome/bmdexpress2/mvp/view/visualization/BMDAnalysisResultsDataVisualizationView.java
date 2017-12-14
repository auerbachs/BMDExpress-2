package com.sciome.bmdexpress2.mvp.view.visualization;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisDataSet;
import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisRow;
import com.sciome.bmdexpress2.mvp.model.ChartKey;
import com.sciome.bmdexpress2.mvp.model.stat.BMDResult;
import com.sciome.bmdexpress2.mvp.presenter.visualization.BMDAnalysisResultsDataVisualizationPresenter;
import com.sciome.bmdexpress2.mvp.viewinterface.visualization.IDataVisualizationView;
import com.sciome.bmdexpress2.service.VisualizationService;
import com.sciome.bmdexpress2.serviceInterface.IVisualizationService;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;
import com.sciome.bmdexpress2.util.categoryanalysis.catmap.PathwayToGeneSymbolUtility;
import com.sciome.charts.SciomeAccumulationPlot;
import com.sciome.charts.SciomeChartBase;
import com.sciome.charts.data.ChartDataPack;
import com.sciome.charts.javafx.SciomePieChartFX;
import com.sciome.charts.jfree.SciomeAccumulationPlotJFree;
import com.sciome.charts.jfree.SciomeHistogramJFree;
import com.sciome.charts.jfree.SciomeScatterChartJFree;
import com.sciome.filter.DataFilterPack;

/*
 * take care of the BMDResults visualizations and special features.
 */
public class BMDAnalysisResultsDataVisualizationView extends DataVisualizationView
		implements IDataVisualizationView
{
	private static final String						ACCUMULATION_CHARTS				= "Accumulation Charts";
	private final static String						BMDL_HISTOGRAM					= "BMDL Histogram";
	private final static String						BMDU_HISTOGRAM					= "BMDU Histogram";
	private final static String						FIT_PVALUE_HISTOGRAM			= "Fit P-Value Histogram";
	private final static String						FIT_LOG_LIKELIHOOD_HISTOGRAM	= "Log Likelihood Histogram";

	private Map<String, Map<String, Set<String>>>	dbToPathwayToGeneSymboles;

	// to make it quicker, define a list of keys that will be used to
	// generate the charttable data. if this is null or empty, then
	// values for all data cells are generated.
	private Set<ChartKey>							useTheseKeysOnly;

	public BMDAnalysisResultsDataVisualizationView()
	{
		super();
		IVisualizationService service = new VisualizationService();
		presenter = new BMDAnalysisResultsDataVisualizationPresenter(this, service,
				BMDExpressEventBus.getInstance());

		chartCache.put(BMDL_HISTOGRAM + "-" + BMDResult.BMDL, new SciomeHistogramJFree("", new ArrayList<>(),
				new ChartKey(BMDResult.BMDL, null), 20.0, BMDAnalysisResultsDataVisualizationView.this));
		chartCache.put(BMDU_HISTOGRAM + "-" + BMDResult.BMDU, new SciomeHistogramJFree("", new ArrayList<>(),
				new ChartKey(BMDResult.BMDU, null), 20.0, BMDAnalysisResultsDataVisualizationView.this));
		chartCache.put(FIT_PVALUE_HISTOGRAM + "-" + BMDResult.BEST_FITPVALUE,
				new SciomeHistogramJFree("", new ArrayList<>(), new ChartKey(BMDResult.BEST_FITPVALUE, null),
						20.0, BMDAnalysisResultsDataVisualizationView.this));

		chartCache.put(ACCUMULATION_CHARTS + "-" + BMDResult.BMDL, new SciomeAccumulationPlotJFree(
				"Accumulation", new ArrayList<>(), new ChartKey(BMDResult.BMDL, null), 0.0, this));
		chartCache.put(ACCUMULATION_CHARTS + "-" + BMDResult.BMD, new SciomeAccumulationPlotJFree(
				"Accumulation", new ArrayList<>(), new ChartKey(BMDResult.BMD, null), 0.0, this));
		chartCache.put(ACCUMULATION_CHARTS + "-" + BMDResult.BMDU, new SciomeAccumulationPlotJFree(
				"Accumulation", new ArrayList<>(), new ChartKey(BMDResult.BMDU, null), 0.0, this));

		chartCache.put(FIT_LOG_LIKELIHOOD_HISTOGRAM + "-" + BMDResult.BEST_LOGLIKLIHOOD,
				new SciomeHistogramJFree("", new ArrayList<>(),
						new ChartKey(BMDResult.BEST_LOGLIKLIHOOD, null), 20.0,
						BMDAnalysisResultsDataVisualizationView.this));

		chartCache.put("DEFAULT-" + BMDResult.BMD + BMDResult.BMDL,
				new SciomeScatterChartJFree("", new ArrayList<>(), new ChartKey(BMDResult.BMD, null),
						new ChartKey(BMDResult.BMDL, null), BMDAnalysisResultsDataVisualizationView.this));
		chartCache.put("DEFAULT-" + BMDResult.BMD, new SciomeHistogramJFree("", new ArrayList<>(),
				new ChartKey(BMDResult.BMD, null), 20.0, BMDAnalysisResultsDataVisualizationView.this));

		// this chart view is only using these chartkeys. let's reduce memory and processing by specifying
		// these
		// up front. But remember, if you add a new chart or specifiy new keys to use, then this needs to be
		// updated.
		useTheseKeysOnly = new HashSet<>();
		useTheseKeysOnly.addAll(Arrays.asList(new ChartKey(BMDResult.BMDL, null),
				new ChartKey(BMDResult.BEST_FITPVALUE, null), new ChartKey(BMDResult.BMD, null),
				new ChartKey(BMDResult.BMDU, null), new ChartKey(BMDResult.BEST_LOGLIKLIHOOD, null)));

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

		// this is needed becasue there are transient fields that need to be initialized.
		// don;t like how this work as it leads to null pointers. need to look into architecture
		Object obj = results.get(0).getObject();
		if (results.get(0).getObject() instanceof List)
			obj = ((List) results.get(0).getObject()).get(0);
		dbToPathwayToGeneSymboles = PathwayToGeneSymbolUtility.getInstance()
				.getdbToPathwaytoGeneSet(((BMDResult) obj));

		for (BMDExpressAnalysisDataSet result : results)
			if (result instanceof BMDResult)
				((BMDResult) result).getColumnHeader();
		List<ChartDataPack> chartDataPacks = presenter.getCategoryResultsChartPackData(results, pack,
				selectedIds, useTheseKeysOnly, null, new ChartKey(BMDResult.PROBE_ID, null));

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
			SciomeChartBase chart = chartCache.get(FIT_PVALUE_HISTOGRAM + "-" + BMDResult.BEST_FITPVALUE);
			chartsList.add(chart);
			chart.redrawCharts(chartDataPacks);
		}
		else if (chartKey.equals(FIT_LOG_LIKELIHOOD_HISTOGRAM))
		{
			SciomeChartBase chart = chartCache
					.get(FIT_LOG_LIKELIHOOD_HISTOGRAM + "-" + BMDResult.BEST_LOGLIKLIHOOD);
			chartsList.add(chart);
			chart.redrawCharts(chartDataPacks);
		}
		else if (chartKey.equals(ACCUMULATION_CHARTS))
		{
			// accumulation charts take a complex map so user
			// can select pathways that will ultimately highlight genes in
			// in accumulation chart
			SciomeChartBase chart1 = chartCache.get(ACCUMULATION_CHARTS + "-" + BMDResult.BMDL);
			chartsList.add(chart1);
			((SciomeAccumulationPlot) chart1).setdbToPathwayToGeneSet(dbToPathwayToGeneSymboles);
			chart1.redrawCharts(chartDataPacks);

			SciomeChartBase chart2 = chartCache.get(ACCUMULATION_CHARTS + "-" + BMDResult.BMD);
			chartsList.add(chart2);
			chart2.redrawCharts(chartDataPacks);
			((SciomeAccumulationPlot) chart2).setdbToPathwayToGeneSet(dbToPathwayToGeneSymboles);
			SciomeChartBase chart3 = chartCache.get(ACCUMULATION_CHARTS + "-" + BMDResult.BMDU);
			chartsList.add(chart3);
			chart3.redrawCharts(chartDataPacks);
			((SciomeAccumulationPlot) chart3).setdbToPathwayToGeneSet(dbToPathwayToGeneSymboles);
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

		for (BMDExpressAnalysisDataSet dataset : bmdResultss)
		{

			for (BMDExpressAnalysisRow row : dataset.getAnalysisRows())
			{

				String bestModel = null;;
				try
				{
					if (pack != null && !pack.passesFilter(row))
						continue;
					if (selectedIds2 != null
							&& !selectedIds2.contains(dataset.getValueForRow(row, BMDResult.PROBE_ID)))
						continue;
					bestModel = dataset.getValueForRow(row, BMDResult.BEST_MODEL).toString();
				}
				catch (Exception e)
				{}
				if (bestModel == null)
					continue;
				if (mapCount.containsKey(bestModel))
				{
					mapCount.put(bestModel, mapCount.get(bestModel) + 1.0);
				}
				else
				{
					mapCount.put(bestModel, 1.0);
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
		returnList.add(ACCUMULATION_CHARTS);
		returnList.add(BMDL_HISTOGRAM);
		returnList.add(BMDU_HISTOGRAM);
		returnList.add(FIT_PVALUE_HISTOGRAM);
		returnList.add(FIT_LOG_LIKELIHOOD_HISTOGRAM);
		return returnList;
	}

}
