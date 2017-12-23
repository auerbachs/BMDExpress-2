package com.sciome.bmdexpress2.mvp.view.visualization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisDataSet;
import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisRow;
import com.sciome.bmdexpress2.mvp.model.ChartKey;
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
import com.sciome.bmdexpress2.util.categoryanalysis.catmap.PathwayToGeneSymbolUtility;
import com.sciome.bmdexpress2.util.visualizations.curvefit.PathwayCurveViewer;
import com.sciome.charts.SciomeAccumulationPlot;
import com.sciome.charts.SciomeChartBase;
import com.sciome.charts.data.ChartDataPack;
import com.sciome.charts.javafx.SciomeBarChartFX;
import com.sciome.charts.javafx.SciomePieChartFX;
import com.sciome.charts.javafx.SciomeRangePlotFX;
import com.sciome.charts.jfree.SciomeAccumulationPlotJFree;
import com.sciome.charts.jfree.SciomeBubbleChartJFree;
import com.sciome.charts.jfree.SciomeHistogramJFree;
import com.sciome.charts.jfree.SciomeScatterChartJFree;
import com.sciome.filter.DataFilterPack;

/*
 * take care of charts and special view options for the Category Analysis visualizations
 * 
 */
public class CategoryAnalysisDataVisualizationView extends DataVisualizationView
		implements IDataVisualizationView
{

	private static final String						CURVEPLOT			= "Curve Overlay";
	private static final String						RANGEPLOT			= "Range Plot";
	private static final String						BUBBLE_CHART		= "Bubble Chart";
	private static final String						ACCUMULATION_CHARTS	= "Accumulation Charts";
	private static final String						BMD_BMDL_BARCHARTS	= "BMD and BMDL Bar Charts";
	private static final String						BEST_MODEL_PIE		= "Best Models Pie Chart";
	private static final String						MEAN_HISTOGRAMS		= "Mean Histograms";
	private static final String						MEDIAN_HISTOGRAMS	= "Median Histograms";
	private static final String						BMD_BMDL_SCATTER	= "BMD vs BMDL Scatter Plots";
	private Map<String, Map<String, Set<String>>>	dbToPathwayToGeneSymboles;

	public CategoryAnalysisDataVisualizationView()
	{
		super();
		IVisualizationService service = new VisualizationService();
		presenter = new CategoryAnalysisDataVisualizationPresenter(this, service,
				BMDExpressEventBus.getInstance());

		chartCache.put("PIE-CHART",
				new SciomePieChartFX(getBMDStatResultCountsFromCatAnalysis(results, null, true), null, null,
						"BMDS Model Counts (unique)", CategoryAnalysisDataVisualizationView.this));

		chartCache.put(RANGEPLOT,
				new SciomeRangePlotFX("Range Plot", new ArrayList<>(),
						new ChartKey(CategoryAnalysisResults.BMDL_MEDIAN, null),
						new ChartKey(CategoryAnalysisResults.BMDU_MEDIAN, null), null,
						new ChartKey(CategoryAnalysisResults.BMD_MEDIAN, null), null,
						CategoryAnalysisDataVisualizationView.this));

		chartCache.put(ACCUMULATION_CHARTS + "-" + CategoryAnalysisResults.BMD_MEDIAN,
				new SciomeAccumulationPlotJFree("Accumulation", new ArrayList<>(),
						new ChartKey(CategoryAnalysisResults.BMD_MEDIAN, null), 0.0, this));
		chartCache.put(ACCUMULATION_CHARTS + "-" + CategoryAnalysisResults.BMD_MEAN,
				new SciomeAccumulationPlotJFree("Accumulation", new ArrayList<>(),
						new ChartKey(CategoryAnalysisResults.BMD_MEAN, null), 0.0, this));

		chartCache.put(ACCUMULATION_CHARTS + "-" + CategoryAnalysisResults.BMDL_MEDIAN,
				new SciomeAccumulationPlotJFree("Accumulation", new ArrayList<>(),
						new ChartKey(CategoryAnalysisResults.BMDL_MEDIAN, null), 0.0, this));
		chartCache.put(ACCUMULATION_CHARTS + "-" + CategoryAnalysisResults.BMDL_MEAN,
				new SciomeAccumulationPlotJFree("Accumulation", new ArrayList<>(),
						new ChartKey(CategoryAnalysisResults.BMDL_MEAN, null), 0.0, this));

		chartCache.put(ACCUMULATION_CHARTS + "-" + CategoryAnalysisResults.BMDU_MEDIAN,
				new SciomeAccumulationPlotJFree("Accumulation", new ArrayList<>(),
						new ChartKey(CategoryAnalysisResults.BMDU_MEDIAN, null), 0.0, this));
		chartCache.put(ACCUMULATION_CHARTS + "-" + CategoryAnalysisResults.BMDU_MEAN,
				new SciomeAccumulationPlotJFree("Accumulation", new ArrayList<>(),
						new ChartKey(CategoryAnalysisResults.BMDU_MEAN, null), 0.0, this));

		chartCache.put(MEAN_HISTOGRAMS + "-" + CategoryAnalysisResults.BMD_MEAN,
				new SciomeHistogramJFree("", new ArrayList<>(),
						new ChartKey(CategoryAnalysisResults.BMD_MEAN, null), 20.0,
						CategoryAnalysisDataVisualizationView.this));
		chartCache.put(MEAN_HISTOGRAMS + "-" + CategoryAnalysisResults.BMDL_MEAN,
				new SciomeHistogramJFree("", new ArrayList<>(),
						new ChartKey(CategoryAnalysisResults.BMDL_MEAN, null), 20.0,
						CategoryAnalysisDataVisualizationView.this));
		chartCache.put(MEAN_HISTOGRAMS + "-" + CategoryAnalysisResults.BMDU_MEAN,
				new SciomeHistogramJFree("", new ArrayList<>(),
						new ChartKey(CategoryAnalysisResults.BMDU_MEAN, null), 20.0,
						CategoryAnalysisDataVisualizationView.this));
		chartCache.put(MEAN_HISTOGRAMS + "-" + CategoryAnalysisResults.BMD_FIFTH_MEAN,
				new SciomeHistogramJFree("", new ArrayList<>(),
						new ChartKey(CategoryAnalysisResults.BMD_FIFTH_MEAN, null), 20.0,
						CategoryAnalysisDataVisualizationView.this));
		chartCache.put(MEAN_HISTOGRAMS + "-" + CategoryAnalysisResults.BMD_TENTH_MEAN,
				new SciomeHistogramJFree("", new ArrayList<>(),
						new ChartKey(CategoryAnalysisResults.BMD_TENTH_MEAN, null), 20.0,
						CategoryAnalysisDataVisualizationView.this));

		chartCache.put(MEDIAN_HISTOGRAMS + "-" + CategoryAnalysisResults.BMD_MEDIAN,
				new SciomeHistogramJFree("", new ArrayList<>(),
						new ChartKey(CategoryAnalysisResults.BMD_MEDIAN, null), 20.0,
						CategoryAnalysisDataVisualizationView.this));
		chartCache.put(MEDIAN_HISTOGRAMS + "-" + CategoryAnalysisResults.BMDL_MEDIAN,
				new SciomeHistogramJFree("", new ArrayList<>(),
						new ChartKey(CategoryAnalysisResults.BMDL_MEDIAN, null), 20.0,
						CategoryAnalysisDataVisualizationView.this));
		chartCache.put(MEDIAN_HISTOGRAMS + "-" + CategoryAnalysisResults.BMDU_MEDIAN,
				new SciomeHistogramJFree("", new ArrayList<>(),
						new ChartKey(CategoryAnalysisResults.BMDU_MEDIAN, null), 20.0,
						CategoryAnalysisDataVisualizationView.this));

		chartCache.put(BMD_BMDL_BARCHARTS + "-" + CategoryAnalysisResults.BMD_MEDIAN,
				new SciomeBarChartFX("BarChart", new ArrayList<>(),
						new ChartKey(CategoryAnalysisResults.BMD_MEDIAN, null),
						CategoryAnalysisDataVisualizationView.this));
		chartCache.put(BMD_BMDL_BARCHARTS + "-" + CategoryAnalysisResults.BMDL_MEDIAN,
				new SciomeBarChartFX("BarChart", new ArrayList<>(),
						new ChartKey(CategoryAnalysisResults.BMDL_MEDIAN, null),
						CategoryAnalysisDataVisualizationView.this));
		chartCache.put(BMD_BMDL_BARCHARTS + "-" + CategoryAnalysisResults.BMDU_MEDIAN,
				new SciomeBarChartFX("BarChart", new ArrayList<>(),
						new ChartKey(CategoryAnalysisResults.BMDU_MEDIAN, null),
						CategoryAnalysisDataVisualizationView.this));
		chartCache.put(BMD_BMDL_BARCHARTS + "-" + CategoryAnalysisResults.BMD_MEAN,
				new SciomeBarChartFX("BarChart", new ArrayList<>(),
						new ChartKey(CategoryAnalysisResults.BMD_MEAN, null),
						CategoryAnalysisDataVisualizationView.this));
		chartCache.put(BMD_BMDL_BARCHARTS + "-" + CategoryAnalysisResults.BMDL_MEAN,
				new SciomeBarChartFX("BarChart", new ArrayList<>(),
						new ChartKey(CategoryAnalysisResults.BMDL_MEAN, null),
						CategoryAnalysisDataVisualizationView.this));
		chartCache.put(BMD_BMDL_BARCHARTS + "-" + CategoryAnalysisResults.BMDU_MEAN,
				new SciomeBarChartFX("BarChart", new ArrayList<>(),
						new ChartKey(CategoryAnalysisResults.BMDU_MEAN, null),
						CategoryAnalysisDataVisualizationView.this));

		chartCache.put(
				BMD_BMDL_SCATTER + "-" + CategoryAnalysisResults.BMD_MEDIAN
						+ CategoryAnalysisResults.BMDL_MEDIAN,
				new SciomeScatterChartJFree("", new ArrayList<>(),
						new ChartKey(CategoryAnalysisResults.BMD_MEDIAN, null),
						new ChartKey(CategoryAnalysisResults.BMDL_MEDIAN, null),
						CategoryAnalysisDataVisualizationView.this));
		chartCache.put(
				BMD_BMDL_SCATTER + "-" + CategoryAnalysisResults.BMD_MEAN + CategoryAnalysisResults.BMDL_MEAN,
				new SciomeScatterChartJFree("", new ArrayList<>(),
						new ChartKey(CategoryAnalysisResults.BMD_MEAN, null),
						new ChartKey(CategoryAnalysisResults.BMDL_MEAN, null),
						CategoryAnalysisDataVisualizationView.this));
		chartCache.put(
				BMD_BMDL_SCATTER + "-" + CategoryAnalysisResults.BMDU_MEAN + CategoryAnalysisResults.BMD_MEAN,
				new SciomeScatterChartJFree("", new ArrayList<>(),
						new ChartKey(CategoryAnalysisResults.BMDU_MEAN, null),
						new ChartKey(CategoryAnalysisResults.BMD_MEAN, null),
						CategoryAnalysisDataVisualizationView.this));
		chartCache.put(
				BMD_BMDL_SCATTER + "-" + CategoryAnalysisResults.BMDU_MEAN
						+ CategoryAnalysisResults.BMDL_MEAN,
				new SciomeScatterChartJFree("", new ArrayList<>(),
						new ChartKey(CategoryAnalysisResults.BMDU_MEAN, null),
						new ChartKey(CategoryAnalysisResults.BMDL_MEAN, null),
						CategoryAnalysisDataVisualizationView.this));

		chartCache.put(
				BUBBLE_CHART + "-" + CategoryAnalysisResults.BMD_MEDIAN
						+ CategoryAnalysisResults.FISHERS_TWO_TAIL_NEG_LOG,
				new SciomeBubbleChartJFree("", new ArrayList<>(),
						new ChartKey(CategoryAnalysisResults.BMD_MEDIAN, null),
						new ChartKey(CategoryAnalysisResults.FISHERS_TWO_TAIL, ChartKey.NEGLOG),
						new ChartKey("Percentage", null), CategoryAnalysisDataVisualizationView.this));

		chartCache.put("DEFAULT-Accumulation", new SciomeAccumulationPlotJFree("Accumulation",
				new ArrayList<>(), new ChartKey(CategoryAnalysisResults.BMD_MEDIAN, null), 0.0, this));

		chartCache.put("DEFAULT-" + CategoryAnalysisResults.BMD_MEDIAN + CategoryAnalysisResults.BMDL_MEDIAN,
				new SciomeScatterChartJFree("", new ArrayList<>(),
						new ChartKey(CategoryAnalysisResults.BMD_MEDIAN, null),
						new ChartKey(CategoryAnalysisResults.BMDL_MEDIAN, null),
						CategoryAnalysisDataVisualizationView.this));

	}

	@Override
	public void redrawCharts(DataFilterPack pack)
	{
		// set this to false by default.
		// but if the user wants to see curve overlay, then we will
		/// set this to true and not view custom charts because
		// we want all the real estate we can get
		ignoreCustomCharts = false;
		try
		{
			Object obj = results.get(0).getObject();
			if (results.get(0).getObject() instanceof List)
				obj = ((List) results.get(0).getObject()).get(0);
			dbToPathwayToGeneSymboles = PathwayToGeneSymbolUtility.getInstance().getdbToPathwaytoGeneSet(
					((CategoryAnalysisResults) obj).getBmdResult().getDoseResponseExperiment());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		defaultDPack = pack;
		String chartKey = cBox.getSelectionModel().getSelectedItem();
		if (results == null || results.size() == 0)
			return;

		chartsList = new ArrayList<>();
		if (chartKey.equals(RANGEPLOT))
		{
			SciomeChartBase chart = chartCache.get(RANGEPLOT);
			chartsList.add(chart);
		}
		else if (chartKey.equals(ACCUMULATION_CHARTS))
		{

			SciomeChartBase chart1 = chartCache
					.get(ACCUMULATION_CHARTS + "-" + CategoryAnalysisResults.BMD_MEDIAN);
			((SciomeAccumulationPlot) chart1).setdbToPathwayToGeneSet(this.dbToPathwayToGeneSymboles);
			chartsList.add(chart1);
			SciomeChartBase chart2 = chartCache
					.get(ACCUMULATION_CHARTS + "-" + CategoryAnalysisResults.BMD_MEAN);
			((SciomeAccumulationPlot) chart2).setdbToPathwayToGeneSet(this.dbToPathwayToGeneSymboles);
			chartsList.add(chart2);
			SciomeChartBase chart3 = chartCache
					.get(ACCUMULATION_CHARTS + "-" + CategoryAnalysisResults.BMDL_MEDIAN);
			((SciomeAccumulationPlot) chart3).setdbToPathwayToGeneSet(this.dbToPathwayToGeneSymboles);
			chartsList.add(chart3);
			SciomeChartBase chart4 = chartCache
					.get(ACCUMULATION_CHARTS + "-" + CategoryAnalysisResults.BMDL_MEAN);
			((SciomeAccumulationPlot) chart4).setdbToPathwayToGeneSet(this.dbToPathwayToGeneSymboles);
			chartsList.add(chart4);
			SciomeChartBase chart5 = chartCache
					.get(ACCUMULATION_CHARTS + "-" + CategoryAnalysisResults.BMDU_MEDIAN);
			((SciomeAccumulationPlot) chart5).setdbToPathwayToGeneSet(this.dbToPathwayToGeneSymboles);
			chartsList.add(chart5);
			SciomeChartBase chart6 = chartCache
					.get(ACCUMULATION_CHARTS + "-" + CategoryAnalysisResults.BMDU_MEAN);
			((SciomeAccumulationPlot) chart6).setdbToPathwayToGeneSet(this.dbToPathwayToGeneSymboles);
			chartsList.add(chart6);

		}

		else if (chartKey.equals(MEAN_HISTOGRAMS))
		{

			SciomeChartBase chart1 = chartCache.get(MEAN_HISTOGRAMS + "-" + CategoryAnalysisResults.BMD_MEAN);
			chartsList.add(chart1);
			SciomeChartBase chart2 = chartCache
					.get(MEAN_HISTOGRAMS + "-" + CategoryAnalysisResults.BMDL_MEAN);
			chartsList.add(chart2);
			SciomeChartBase chart3 = chartCache
					.get(MEAN_HISTOGRAMS + "-" + CategoryAnalysisResults.BMDU_MEAN);
			chartsList.add(chart3);
			SciomeChartBase chart4 = chartCache
					.get(MEAN_HISTOGRAMS + "-" + CategoryAnalysisResults.BMD_FIFTH_MEAN);
			chartsList.add(chart4);
			SciomeChartBase chart5 = chartCache
					.get(MEAN_HISTOGRAMS + "-" + CategoryAnalysisResults.BMD_TENTH_MEAN);
			chartsList.add(chart5);

		}
		else if (chartKey.equals(MEDIAN_HISTOGRAMS))
		{
			SciomeChartBase chart1 = chartCache
					.get(MEDIAN_HISTOGRAMS + "-" + CategoryAnalysisResults.BMD_MEDIAN);
			chartsList.add(chart1);
			SciomeChartBase chart2 = chartCache
					.get(MEDIAN_HISTOGRAMS + "-" + CategoryAnalysisResults.BMDL_MEDIAN);
			chartsList.add(chart2);
			SciomeChartBase chart3 = chartCache
					.get(MEDIAN_HISTOGRAMS + "-" + CategoryAnalysisResults.BMDU_MEDIAN);
			chartsList.add(chart3);

		}
		else if (chartKey.equals(BMD_BMDL_BARCHARTS))
		{
			SciomeChartBase chart1 = chartCache
					.get(BMD_BMDL_BARCHARTS + "-" + CategoryAnalysisResults.BMD_MEDIAN);
			chartsList.add(chart1);
			SciomeChartBase chart2 = chartCache
					.get(BMD_BMDL_BARCHARTS + "-" + CategoryAnalysisResults.BMDL_MEDIAN);
			chartsList.add(chart2);
			SciomeChartBase chart3 = chartCache
					.get(BMD_BMDL_BARCHARTS + "-" + CategoryAnalysisResults.BMDU_MEDIAN);
			chartsList.add(chart3);
			SciomeChartBase chart4 = chartCache
					.get(BMD_BMDL_BARCHARTS + "-" + CategoryAnalysisResults.BMD_MEAN);
			chartsList.add(chart4);
			SciomeChartBase chart5 = chartCache
					.get(BMD_BMDL_BARCHARTS + "-" + CategoryAnalysisResults.BMDL_MEAN);
			chartsList.add(chart5);
			SciomeChartBase chart6 = chartCache
					.get(BMD_BMDL_BARCHARTS + "-" + CategoryAnalysisResults.BMDU_MEAN);
			chartsList.add(chart6);

		}
		else if (chartKey.equals(BMD_BMDL_SCATTER))
		{
			SciomeChartBase chart1 = chartCache.get(BMD_BMDL_SCATTER + "-"
					+ CategoryAnalysisResults.BMD_MEDIAN + CategoryAnalysisResults.BMDL_MEDIAN);
			chartsList.add(chart1);
			SciomeChartBase chart2 = chartCache.get(BMD_BMDL_SCATTER + "-" + CategoryAnalysisResults.BMD_MEAN
					+ CategoryAnalysisResults.BMDL_MEAN);
			chartsList.add(chart2);
			SciomeChartBase chart3 = chartCache.get(BMD_BMDL_SCATTER + "-" + CategoryAnalysisResults.BMDU_MEAN
					+ CategoryAnalysisResults.BMD_MEAN);
			chartsList.add(chart3);
			SciomeChartBase chart4 = chartCache.get(BMD_BMDL_SCATTER + "-" + CategoryAnalysisResults.BMDU_MEAN
					+ CategoryAnalysisResults.BMDL_MEAN);
			chartsList.add(chart4);

		}
		else if (chartKey.equals(BUBBLE_CHART))
		{
			SciomeChartBase chart2 = chartCache.get(BUBBLE_CHART + "-" + CategoryAnalysisResults.BMD_MEDIAN
					+ CategoryAnalysisResults.FISHERS_TWO_TAIL_NEG_LOG);
			chartsList.add(chart2);
		}
		else if (chartKey.equals(CURVEPLOT))
		{
			ignoreCustomCharts = true;
			chartsList.add(new PathwayCurveViewer(results, pack));

		}
		else if (chartKey.equals(DEFAULT_CHARTS))
		{
			SciomeChartBase chart1 = chartCache.get("DEFAULT-Accumulation");
			chartsList.add(chart1);
			((SciomeAccumulationPlot) chart1).setdbToPathwayToGeneSet(this.dbToPathwayToGeneSymboles);

			SciomeChartBase chart3 = chartCache.get(
					"DEFAULT-" + CategoryAnalysisResults.BMD_MEDIAN + CategoryAnalysisResults.BMDL_MEDIAN);
			chartsList.add(chart3);

		}

		List<ChartDataPack> chartDataPacks = presenter.getBMDAnalysisDataSetChartDataPack(results, pack,
				getUsedChartKeys(), getMathedChartKeys(),
				new ChartKey(CategoryAnalysisResults.CATEGORY_ID, null));

		// add straggler pie charts that need the chartdatapacks as input.
		if (chartKey.equals(BEST_MODEL_PIE))
		{
			SciomePieChartFX chart = (SciomePieChartFX) chartCache.get("PIE-CHART");
			chart.redrawPieChart(getBMDStatResultCountsFromCatAnalysis(results, pack, true), null);
			chartsList.add(chart);
		}

		showCharts(chartDataPacks);

	}

	@Override
	public List<String> getCannedCharts()
	{
		List<String> resultList = new ArrayList<>();
		resultList.add(DEFAULT_CHARTS);
		resultList.add(CURVEPLOT);
		resultList.add(RANGEPLOT);
		resultList.add(BUBBLE_CHART);
		resultList.add(BMD_BMDL_BARCHARTS);
		resultList.add(ACCUMULATION_CHARTS);
		resultList.add(BEST_MODEL_PIE);
		resultList.add(MEAN_HISTOGRAMS);
		resultList.add(MEDIAN_HISTOGRAMS);
		resultList.add(BMD_BMDL_SCATTER);

		return resultList;
	}

	private Map<String, Double> getBMDStatResultCountsFromCatAnalysis(
			List<BMDExpressAnalysisDataSet> catResultss, DataFilterPack pack, boolean uniqueBMDCount)
	{
		Map<String, Double> mapCount = new HashMap<>();
		if (catResultss == null)
			return mapCount;
		Set<ProbeStatResult> probeIdSet = new HashSet<>();
		for (BMDExpressAnalysisDataSet results : catResultss)
		{
			for (BMDExpressAnalysisRow row : results.getAnalysisRows())
			{
				CategoryAnalysisResult catResult = (CategoryAnalysisResult) row.getObject();
				if (pack != null && !pack.passesFilter(row))
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
