package com.sciome.bmdexpress2.mvp.view.visualization;

import java.util.ArrayList;
import java.util.List;

import com.sciome.bmdexpress2.mvp.model.ChartKey;
import com.sciome.bmdexpress2.mvp.model.prefilter.CurveFitPrefilterResults;
import com.sciome.bmdexpress2.mvp.model.prefilter.PrefilterResults;
import com.sciome.bmdexpress2.mvp.presenter.visualization.OneWayANOVADataVisualizationPresenter;
import com.sciome.bmdexpress2.mvp.viewinterface.visualization.IDataVisualizationView;
import com.sciome.bmdexpress2.service.VisualizationService;
import com.sciome.bmdexpress2.serviceInterface.IVisualizationService;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;
import com.sciome.charts.SciomeChartBase;
import com.sciome.charts.data.ChartDataPack;
import com.sciome.charts.jfree.SciomeAccumulationPlotJFree;
import com.sciome.charts.jfree.SciomeHistogramJFree;
import com.sciome.charts.jfree.SciomeScatterChartJFree;
import com.sciome.charts.venndis.SciomeVennDiagram;
import com.sciome.filter.DataFilterPack;

public class CurveFitPrefilterDataVisualizationView extends DataVisualizationView
		implements IDataVisualizationView
{

	private static final String BEST_FOLD_CHANGE_HISTOGRAM = "Max Fold Change Histogram";
	private static final String BEST_FOLD_CHANGE_UNSIGNED_HISTOGRAM = "Max Fold Change (Unsigned) Histogram";
	private static final String PREFILTER_VENN_DIAGRAM = "Venn Diagaram";

	public CurveFitPrefilterDataVisualizationView()
	{
		super();
		IVisualizationService service = new VisualizationService();
		presenter = new OneWayANOVADataVisualizationPresenter(this, service,
				BMDExpressEventBus.getInstance());

		chartCache.put(BEST_FOLD_CHANGE_HISTOGRAM + "-" + PrefilterResults.BEST_FOLD_CHANGE,
				new SciomeHistogramJFree("", new ArrayList<>(),
						new ChartKey(PrefilterResults.BEST_FOLD_CHANGE, null), 20.0, this));

		chartCache.put(BEST_FOLD_CHANGE_UNSIGNED_HISTOGRAM + "-" + PrefilterResults.BEST_FOLD_CHANGE_ABS,
				new SciomeHistogramJFree("", new ArrayList<>(),
						new ChartKey(PrefilterResults.BEST_FOLD_CHANGE_ABS, null), 20.0, this));

		chartCache.put("DEFAULT-Accumulation", new SciomeAccumulationPlotJFree("Accumulation",
				new ArrayList<>(), new ChartKey(CurveFitPrefilterResults.BEST_BMD, null), 0.0, this));

		chartCache.put("DEFAULT-" + CurveFitPrefilterResults.BEST_BMD + CurveFitPrefilterResults.BEST_BMDL,
				new SciomeScatterChartJFree("", new ArrayList<>(),
						new ChartKey(CurveFitPrefilterResults.BEST_BMD, null),
						new ChartKey(CurveFitPrefilterResults.BEST_BMDL, null), this));
		chartCache.put(PREFILTER_VENN_DIAGRAM, new SciomeVennDiagram("", new ArrayList<>(),
				new ChartKey(PrefilterResults.PROBE_ID, null), this));
	}

	@Override
	public void redrawCharts(DataFilterPack pack)
	{
		String chartKey = cBox.getSelectionModel().getSelectedItem();
		defaultDPack = pack;
		if (results == null || results.size() == 0)
			return;

		chartsList = new ArrayList<>();

		if (chartKey.equals(BEST_FOLD_CHANGE_HISTOGRAM))
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
		else if (chartKey.equals(PREFILTER_VENN_DIAGRAM))
		{
			SciomeChartBase chart = chartCache.get(PREFILTER_VENN_DIAGRAM);
			chartsList.add(chart);
		}
		else
		{
			SciomeChartBase chart = chartCache.get("DEFAULT-Accumulation");
			SciomeChartBase chart2 = chartCache
					.get("DEFAULT-" + CurveFitPrefilterResults.BEST_BMD + CurveFitPrefilterResults.BEST_BMDL);
			chartsList.add(chart);
			chartsList.add(chart2);
		}

		graphViewAnchorPane.getChildren().clear();

		List<ChartDataPack> chartDataPacks = presenter.getBMDAnalysisDataSetChartDataPack(results, pack,
				getUsedChartKeys(), getMathedChartKeys(), new ChartKey(PrefilterResults.PROBE_ID, null));
		showCharts(chartDataPacks);

	}

	@Override
	public List<String> getCannedCharts()
	{
		List<String> resultList = new ArrayList<>();
		resultList.add(DEFAULT_CHARTS);
		resultList.add(BEST_FOLD_CHANGE_HISTOGRAM);
		resultList.add(BEST_FOLD_CHANGE_UNSIGNED_HISTOGRAM);
		resultList.add(PREFILTER_VENN_DIAGRAM);

		return resultList;

	}

}
