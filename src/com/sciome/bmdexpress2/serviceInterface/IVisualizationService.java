package com.sciome.bmdexpress2.serviceInterface;

import java.util.List;
import java.util.Set;

import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisDataSet;
import com.sciome.bmdexpress2.mvp.model.ChartKey;
import com.sciome.charts.data.ChartDataPack;
import com.sciome.filter.DataFilterPack;

public interface IVisualizationService
{

	List<ChartDataPack> getBMDExpressAnalysisChartDataPack(List<BMDExpressAnalysisDataSet> catResults,
			DataFilterPack pack, List<String> selectedIds, Set<ChartKey> mathedChartKeys, ChartKey labelKey);
}
