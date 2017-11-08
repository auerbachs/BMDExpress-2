package com.sciome.bmdexpress2.mvp.view.visualization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisDataSet;
import com.sciome.bmdexpress2.mvp.model.category.CategoryAnalysisResult;
import com.sciome.bmdexpress2.mvp.model.category.CategoryAnalysisResults;
import com.sciome.bmdexpress2.mvp.model.category.ReferenceGeneProbeStatResult;
import com.sciome.bmdexpress2.mvp.model.stat.ProbeStatResult;
import com.sciome.bmdexpress2.mvp.model.stat.StatResult;
import com.sciome.bmdexpress2.mvp.presenter.visualization.CategoryAnalysisDataVisualizationPresenter;
import com.sciome.bmdexpress2.mvp.viewinterface.visualization.IDataVisualizationView;
import com.sciome.bmdexpress2.service.VisualizationService;
import com.sciome.bmdexpress2.serviceInterface.IVisualizationService;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;
import com.sciome.charts.data.ChartDataPack;
import com.sciome.charts.javafx.SciomeAccumulationPlot;
import com.sciome.charts.javafx.SciomeBarChart;
import com.sciome.charts.javafx.SciomeRangePlot;
import com.sciome.charts.javafx.SciomeBubbleChart;
import com.sciome.charts.javafx.SciomeChartBase;
import com.sciome.charts.javafx.SciomeHistogram;
import com.sciome.charts.javafx.SciomePieChart;
import com.sciome.charts.javafx.SciomeScatterChart;
import com.sciome.filter.DataFilterPack;

/*
 * take care of charts and special view options for the Category Analysis visualizations
 */
public class CategoryAnalysisDataVisualizationView extends DataVisualizationView
		implements IDataVisualizationView
{
	private static final String BOXANDWHISKER = "Range Plot";
	private static final String ACCUMULATION_CHARTS = "Accumulation Charts";
	private static final String BMD_BMDL_BARCHARTS = "BMD and BMDL Bar Charts";
	private static final String BEST_MODEL_PIE = "Best Models Pie Chart";
	private static final String MEAN_HISTOGRAMS = "Mean Histograms";
	private static final String MEDIAN_HISTOGRAMS = "Median Histograms";
	private static final String BMD_BMDL_SCATTER = "BMD vs BMDL Scatter Plots";

	public CategoryAnalysisDataVisualizationView()
	{
		super();
		IVisualizationService service = new VisualizationService();
		presenter = new CategoryAnalysisDataVisualizationPresenter(this, service, BMDExpressEventBus.getInstance());

		chartCache.put(BOXANDWHISKER, new SciomeRangePlot("Range Plot", new ArrayList<>(),
				CategoryAnalysisResults.BMDL_MEDIAN, CategoryAnalysisResults.BMDU_MEDIAN, null,
				CategoryAnalysisResults.BMD_MEDIAN, null, CategoryAnalysisDataVisualizationView.this));

		chartCache.put(ACCUMULATION_CHARTS + "-" + CategoryAnalysisResults.BMD_MEDIAN,
				new SciomeAccumulationPlot("Accumulation", new ArrayList<>(),
						CategoryAnalysisResults.BMD_MEDIAN, 0.0, this));
		chartCache.put(ACCUMULATION_CHARTS + "-" + CategoryAnalysisResults.BMD_MEAN,
				new SciomeAccumulationPlot("Accumulation", new ArrayList<>(),
						CategoryAnalysisResults.BMD_MEAN, 0.0, this));

		chartCache.put(ACCUMULATION_CHARTS + "-" + CategoryAnalysisResults.BMDL_MEDIAN,
				new SciomeAccumulationPlot("Accumulation", new ArrayList<>(),
						CategoryAnalysisResults.BMDL_MEDIAN, 0.0, this));
		chartCache.put(ACCUMULATION_CHARTS + "-" + CategoryAnalysisResults.BMDL_MEAN,
				new SciomeAccumulationPlot("Accumulation", new ArrayList<>(),
						CategoryAnalysisResults.BMDL_MEAN, 0.0, this));

		chartCache.put(ACCUMULATION_CHARTS + "-" + CategoryAnalysisResults.BMDU_MEDIAN,
				new SciomeAccumulationPlot("Accumulation", new ArrayList<>(),
						CategoryAnalysisResults.BMDU_MEDIAN, 0.0, this));
		chartCache.put(ACCUMULATION_CHARTS + "-" + CategoryAnalysisResults.BMDU_MEAN,
				new SciomeAccumulationPlot("Accumulation", new ArrayList<>(),
						CategoryAnalysisResults.BMDU_MEAN, 0.0, this));

		chartCache.put(MEAN_HISTOGRAMS + "-" + CategoryAnalysisResults.BMD_MEAN,
				new SciomeHistogram("", new ArrayList<>(), CategoryAnalysisResults.BMD_MEAN, 20.0,
						CategoryAnalysisDataVisualizationView.this));
		chartCache.put(MEAN_HISTOGRAMS + "-" + CategoryAnalysisResults.BMDL_MEAN,
				new SciomeHistogram("", new ArrayList<>(), CategoryAnalysisResults.BMDL_MEAN, 20.0,
						CategoryAnalysisDataVisualizationView.this));
		chartCache.put(MEAN_HISTOGRAMS + "-" + CategoryAnalysisResults.BMDU_MEAN,
				new SciomeHistogram("", new ArrayList<>(), CategoryAnalysisResults.BMDU_MEAN, 20.0,
						CategoryAnalysisDataVisualizationView.this));
		chartCache.put(MEAN_HISTOGRAMS + "-" + CategoryAnalysisResults.BMD_FIFTH_MEAN,
				new SciomeHistogram("", new ArrayList<>(), CategoryAnalysisResults.BMD_FIFTH_MEAN, 20.0,
						CategoryAnalysisDataVisualizationView.this));
		chartCache.put(MEAN_HISTOGRAMS + "-" + CategoryAnalysisResults.BMD_TENTH_MEAN,
				new SciomeHistogram("", new ArrayList<>(), CategoryAnalysisResults.BMD_TENTH_MEAN, 20.0,
						CategoryAnalysisDataVisualizationView.this));

		chartCache.put(MEDIAN_HISTOGRAMS + "-" + CategoryAnalysisResults.BMD_MEDIAN,
				new SciomeHistogram("", new ArrayList<>(), CategoryAnalysisResults.BMD_MEDIAN, 20.0,
						CategoryAnalysisDataVisualizationView.this));
		chartCache.put(MEDIAN_HISTOGRAMS + "-" + CategoryAnalysisResults.BMDL_MEDIAN,
				new SciomeHistogram("", new ArrayList<>(), CategoryAnalysisResults.BMDL_MEDIAN, 20.0,
						CategoryAnalysisDataVisualizationView.this));
		chartCache.put(MEDIAN_HISTOGRAMS + "-" + CategoryAnalysisResults.BMDU_MEDIAN,
				new SciomeHistogram("", new ArrayList<>(), CategoryAnalysisResults.BMDU_MEDIAN, 20.0,
						CategoryAnalysisDataVisualizationView.this));

		chartCache.put(BMD_BMDL_BARCHARTS + "-" + CategoryAnalysisResults.BMD_MEDIAN,
				new SciomeBarChart("BarChart", new ArrayList<>(), CategoryAnalysisResults.BMD_MEDIAN,
						CategoryAnalysisDataVisualizationView.this));
		chartCache.put(BMD_BMDL_BARCHARTS + "-" + CategoryAnalysisResults.BMDL_MEDIAN,
				new SciomeBarChart("BarChart", new ArrayList<>(), CategoryAnalysisResults.BMDL_MEDIAN,
						CategoryAnalysisDataVisualizationView.this));
		chartCache.put(BMD_BMDL_BARCHARTS + "-" + CategoryAnalysisResults.BMDU_MEDIAN,
				new SciomeBarChart("BarChart", new ArrayList<>(), CategoryAnalysisResults.BMDU_MEDIAN,
						CategoryAnalysisDataVisualizationView.this));
		chartCache.put(BMD_BMDL_BARCHARTS + "-" + CategoryAnalysisResults.BMD_MEAN,
				new SciomeBarChart("BarChart", new ArrayList<>(), CategoryAnalysisResults.BMD_MEAN,
						CategoryAnalysisDataVisualizationView.this));
		chartCache.put(BMD_BMDL_BARCHARTS + "-" + CategoryAnalysisResults.BMDL_MEAN,
				new SciomeBarChart("BarChart", new ArrayList<>(), CategoryAnalysisResults.BMDL_MEAN,
						CategoryAnalysisDataVisualizationView.this));
		chartCache.put(BMD_BMDL_BARCHARTS + "-" + CategoryAnalysisResults.BMDU_MEAN,
				new SciomeBarChart("BarChart", new ArrayList<>(), CategoryAnalysisResults.BMDU_MEAN,
						CategoryAnalysisDataVisualizationView.this));

		chartCache.put(
				BMD_BMDL_SCATTER + "-" + CategoryAnalysisResults.BMD_MEDIAN
						+ CategoryAnalysisResults.BMDL_MEDIAN,
				new SciomeScatterChart("", new ArrayList<>(), CategoryAnalysisResults.BMD_MEDIAN,
						CategoryAnalysisResults.BMDL_MEDIAN, CategoryAnalysisDataVisualizationView.this));
		chartCache.put(
				BMD_BMDL_SCATTER + "-" + CategoryAnalysisResults.BMD_MEAN + CategoryAnalysisResults.BMDL_MEAN,
				new SciomeScatterChart("", new ArrayList<>(), CategoryAnalysisResults.BMD_MEAN,
						CategoryAnalysisResults.BMDL_MEAN, CategoryAnalysisDataVisualizationView.this));
		chartCache.put(
				BMD_BMDL_SCATTER + "-" + CategoryAnalysisResults.BMDU_MEAN + CategoryAnalysisResults.BMD_MEAN,
				new SciomeScatterChart("", new ArrayList<>(), CategoryAnalysisResults.BMDU_MEAN,
						CategoryAnalysisResults.BMD_MEAN, CategoryAnalysisDataVisualizationView.this));
		chartCache.put(
				BMD_BMDL_SCATTER + "-" + CategoryAnalysisResults.BMDU_MEAN
						+ CategoryAnalysisResults.BMDL_MEAN,
				new SciomeScatterChart("", new ArrayList<>(), CategoryAnalysisResults.BMDU_MEAN,
						CategoryAnalysisResults.BMDL_MEAN, CategoryAnalysisDataVisualizationView.this));

		chartCache.put("DEFAULT-Accumulation", new SciomeAccumulationPlot("Accumulation", new ArrayList<>(),
				CategoryAnalysisResults.BMD_MEDIAN, 0.0, this));
		chartCache.put(
				"DEFAULT-" + CategoryAnalysisResults.BMD_MEDIAN
						+ CategoryAnalysisResults.FISHERS_TWO_TAIL_NEG_LOG,
				new SciomeBubbleChart("", new ArrayList<>(), CategoryAnalysisResults.BMD_MEDIAN,
						CategoryAnalysisResults.FISHERS_TWO_TAIL_NEG_LOG, "Percentage",
						CategoryAnalysisDataVisualizationView.this));
		chartCache.put("DEFAULT-" + CategoryAnalysisResults.BMD_MEDIAN + CategoryAnalysisResults.BMDL_MEDIAN,
				new SciomeScatterChart("", new ArrayList<>(), CategoryAnalysisResults.BMD_MEDIAN,
						CategoryAnalysisResults.BMDL_MEDIAN, CategoryAnalysisDataVisualizationView.this));
		chartCache.put("DEFAULT-" + CategoryAnalysisResults.BMDU_MEDIAN + CategoryAnalysisResults.BMD_MEDIAN,
				new SciomeScatterChart("", new ArrayList<>(), CategoryAnalysisResults.BMDU_MEDIAN,
						CategoryAnalysisResults.BMD_MEDIAN, CategoryAnalysisDataVisualizationView.this));
		chartCache.put("DEFAULT-" + CategoryAnalysisResults.BMDU_MEDIAN + CategoryAnalysisResults.BMDL_MEDIAN,
				new SciomeScatterChart("", new ArrayList<>(), CategoryAnalysisResults.BMDU_MEDIAN,
						CategoryAnalysisResults.BMDL_MEDIAN, CategoryAnalysisDataVisualizationView.this));

	}

	@Override
	public void redrawCharts(DataFilterPack pack, List<String> selectedIds)
	{
		defaultDPack = pack;
		this.selectedIds = selectedIds;
		String chartKey = cBox.getSelectionModel().getSelectedItem();
		if (results == null || results.size() == 0)
			return;
		List<ChartDataPack> chartDataPacks = presenter.getCategoryResultsChartPackData(results, pack,
				selectedIds);

		chartsList = new ArrayList<>();
		if (chartKey.equals(BOXANDWHISKER))
		{
			SciomeChartBase chart = chartCache.get(BOXANDWHISKER);
			chart.redrawCharts(chartDataPacks);
			chartsList.add(chart);
		}
		else if (chartKey.equals(ACCUMULATION_CHARTS))
		{

			SciomeChartBase chart1 = chartCache
					.get(ACCUMULATION_CHARTS + "-" + CategoryAnalysisResults.BMD_MEDIAN);
			chart1.redrawCharts(chartDataPacks);
			chartsList.add(chart1);
			SciomeChartBase chart2 = chartCache
					.get(ACCUMULATION_CHARTS + "-" + CategoryAnalysisResults.BMD_MEAN);
			chart2.redrawCharts(chartDataPacks);
			chartsList.add(chart2);
			SciomeChartBase chart3 = chartCache
					.get(ACCUMULATION_CHARTS + "-" + CategoryAnalysisResults.BMDL_MEDIAN);
			chart3.redrawCharts(chartDataPacks);
			chartsList.add(chart3);
			SciomeChartBase chart4 = chartCache
					.get(ACCUMULATION_CHARTS + "-" + CategoryAnalysisResults.BMDL_MEAN);
			chart4.redrawCharts(chartDataPacks);
			chartsList.add(chart4);
			SciomeChartBase chart5 = chartCache
					.get(ACCUMULATION_CHARTS + "-" + CategoryAnalysisResults.BMDU_MEDIAN);
			chart5.redrawCharts(chartDataPacks);
			chartsList.add(chart5);
			SciomeChartBase chart6 = chartCache
					.get(ACCUMULATION_CHARTS + "-" + CategoryAnalysisResults.BMDU_MEAN);
			chart6.redrawCharts(chartDataPacks);
			chartsList.add(chart6);

		}
		else if (chartKey.equals(BEST_MODEL_PIE))
		{
			chartsList.add(new SciomePieChart(
					getBMDStatResultCountsFromCatAnalysis(results, pack, selectedIds, true), null,
					chartDataPacks, "BMDS Model Counts (unique)",
					CategoryAnalysisDataVisualizationView.this));
		}
		else if (chartKey.equals(MEAN_HISTOGRAMS))
		{

			SciomeChartBase chart1 = chartCache.get(MEAN_HISTOGRAMS + "-" + CategoryAnalysisResults.BMD_MEAN);
			chart1.redrawCharts(chartDataPacks);
			chartsList.add(chart1);
			SciomeChartBase chart2 = chartCache
					.get(MEAN_HISTOGRAMS + "-" + CategoryAnalysisResults.BMDL_MEAN);
			chart2.redrawCharts(chartDataPacks);
			chartsList.add(chart2);
			SciomeChartBase chart3 = chartCache
					.get(MEAN_HISTOGRAMS + "-" + CategoryAnalysisResults.BMDU_MEAN);
			chart3.redrawCharts(chartDataPacks);
			chartsList.add(chart3);
			SciomeChartBase chart4 = chartCache
					.get(MEAN_HISTOGRAMS + "-" + CategoryAnalysisResults.BMD_FIFTH_MEAN);
			chart4.redrawCharts(chartDataPacks);
			chartsList.add(chart4);
			SciomeChartBase chart5 = chartCache
					.get(MEAN_HISTOGRAMS + "-" + CategoryAnalysisResults.BMD_TENTH_MEAN);
			chart5.redrawCharts(chartDataPacks);
			chartsList.add(chart5);

		}
		else if (chartKey.equals(MEDIAN_HISTOGRAMS))
		{
			SciomeChartBase chart1 = chartCache
					.get(MEDIAN_HISTOGRAMS + "-" + CategoryAnalysisResults.BMD_MEDIAN);
			chart1.redrawCharts(chartDataPacks);
			chartsList.add(chart1);
			SciomeChartBase chart2 = chartCache
					.get(MEDIAN_HISTOGRAMS + "-" + CategoryAnalysisResults.BMDL_MEDIAN);
			chart2.redrawCharts(chartDataPacks);
			chartsList.add(chart2);
			SciomeChartBase chart3 = chartCache
					.get(MEDIAN_HISTOGRAMS + "-" + CategoryAnalysisResults.BMDU_MEDIAN);
			chart3.redrawCharts(chartDataPacks);
			chartsList.add(chart3);

		}
		else if (chartKey.equals(BMD_BMDL_BARCHARTS))
		{
			SciomeChartBase chart1 = chartCache
					.get(BMD_BMDL_BARCHARTS + "-" + CategoryAnalysisResults.BMD_MEDIAN);
			chart1.redrawCharts(chartDataPacks);
			chartsList.add(chart1);
			SciomeChartBase chart2 = chartCache
					.get(BMD_BMDL_BARCHARTS + "-" + CategoryAnalysisResults.BMDL_MEDIAN);
			chart2.redrawCharts(chartDataPacks);
			chartsList.add(chart2);
			SciomeChartBase chart3 = chartCache
					.get(BMD_BMDL_BARCHARTS + "-" + CategoryAnalysisResults.BMDU_MEDIAN);
			chart3.redrawCharts(chartDataPacks);
			chartsList.add(chart3);
			SciomeChartBase chart4 = chartCache
					.get(BMD_BMDL_BARCHARTS + "-" + CategoryAnalysisResults.BMD_MEAN);
			chart4.redrawCharts(chartDataPacks);
			chartsList.add(chart4);
			SciomeChartBase chart5 = chartCache
					.get(BMD_BMDL_BARCHARTS + "-" + CategoryAnalysisResults.BMDL_MEAN);
			chart5.redrawCharts(chartDataPacks);
			chartsList.add(chart5);
			SciomeChartBase chart6 = chartCache
					.get(BMD_BMDL_BARCHARTS + "-" + CategoryAnalysisResults.BMDU_MEAN);
			chart6.redrawCharts(chartDataPacks);
			chartsList.add(chart6);

		}
		else if (chartKey.equals(BMD_BMDL_SCATTER))
		{
			SciomeChartBase chart1 = chartCache.get(BMD_BMDL_SCATTER + "-"
					+ CategoryAnalysisResults.BMD_MEDIAN + CategoryAnalysisResults.BMDL_MEDIAN);
			chart1.redrawCharts(chartDataPacks);
			chartsList.add(chart1);
			SciomeChartBase chart2 = chartCache.get(BMD_BMDL_SCATTER + "-" + CategoryAnalysisResults.BMD_MEAN
					+ CategoryAnalysisResults.BMDL_MEAN);
			chart2.redrawCharts(chartDataPacks);
			chartsList.add(chart2);
			SciomeChartBase chart3 = chartCache.get(BMD_BMDL_SCATTER + "-" + CategoryAnalysisResults.BMDU_MEAN
					+ CategoryAnalysisResults.BMD_MEAN);
			chart3.redrawCharts(chartDataPacks);
			chartsList.add(chart3);
			SciomeChartBase chart4 = chartCache.get(BMD_BMDL_SCATTER + "-" + CategoryAnalysisResults.BMDU_MEAN
					+ CategoryAnalysisResults.BMDL_MEAN);
			chart4.redrawCharts(chartDataPacks);
			chartsList.add(chart4);

		}
		else
		{

			SciomeChartBase chart1 = chartCache.get("DEFAULT-Accumulation");
			chart1.redrawCharts(chartDataPacks);
			chartsList.add(chart1);
			SciomeChartBase chart2 = chartCache.get("DEFAULT-" + CategoryAnalysisResults.BMD_MEDIAN
					+ CategoryAnalysisResults.FISHERS_TWO_TAIL_NEG_LOG);
			chart2.redrawCharts(chartDataPacks);
			chartsList.add(chart2);
			SciomeChartBase chart3 = chartCache.get(
					"DEFAULT-" + CategoryAnalysisResults.BMD_MEDIAN + CategoryAnalysisResults.BMDL_MEDIAN);
			chart3.redrawCharts(chartDataPacks);
			chartsList.add(chart3);
			SciomeChartBase chart4 = chartCache.get(
					"DEFAULT-" + CategoryAnalysisResults.BMDU_MEDIAN + CategoryAnalysisResults.BMD_MEDIAN);
			chart4.redrawCharts(chartDataPacks);
			chartsList.add(chart4);
			SciomeChartBase chart5 = chartCache.get(
					"DEFAULT-" + CategoryAnalysisResults.BMDU_MEDIAN + CategoryAnalysisResults.BMDL_MEDIAN);
			chart5.redrawCharts(chartDataPacks);
			chartsList.add(chart5);

		}

		graphViewAnchorPane.getChildren().clear();
		showCharts();

	}

	@Override
	public List<String> getCannedCharts()
	{
		List<String> resultList = new ArrayList<>();
		resultList.add(DEFAULT_CHARTS);
		resultList.add(BOXANDWHISKER);
		resultList.add(BMD_BMDL_BARCHARTS);
		resultList.add(ACCUMULATION_CHARTS);
		resultList.add(BEST_MODEL_PIE);
		resultList.add(MEAN_HISTOGRAMS);
		resultList.add(MEDIAN_HISTOGRAMS);
		resultList.add(BMD_BMDL_SCATTER);

		return resultList;
	}

	private Map<String, Double> getBMDStatResultCountsFromCatAnalysis(
			List<BMDExpressAnalysisDataSet> catResultss, DataFilterPack pack, List<String> selectedIds2,
			boolean uniqueBMDCount)
	{
		Map<String, Double> mapCount = new HashMap<>();

		Set<ProbeStatResult> probeIdSet = new HashSet<>();
		for (BMDExpressAnalysisDataSet results : catResultss)
		{
			CategoryAnalysisResults catResults = (CategoryAnalysisResults) results;
			for (CategoryAnalysisResult catResult : catResults.getCategoryAnalsyisResults())
			{
				if (pack != null && !pack.passesFilter(catResult))
					continue;
				if (selectedIds2 != null && !selectedIds2.contains(catResult.getCategoryIdentifier().getId()))
					continue;
				if (catResult.getReferenceGeneProbeStatResults() == null)
					continue;
				for (ReferenceGeneProbeStatResult geneProbeStat : catResult
						.getReferenceGeneProbeStatResults())
				{
					for (ProbeStatResult probeStatResult : geneProbeStat.getProbeStatResults())
					{

						if (uniqueBMDCount && probeIdSet.contains(probeStatResult))
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
						probeIdSet.add(probeStatResult);
					}
				}
			}
		}

		return mapCount;
	}

}
