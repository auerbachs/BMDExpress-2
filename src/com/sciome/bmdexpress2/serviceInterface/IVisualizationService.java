package com.sciome.bmdexpress2.serviceInterface;

import java.util.List;

import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisDataSet;
import com.sciome.charts.data.ChartDataPack;
import com.sciome.filter.DataFilterPack;

public interface IVisualizationService {
	public List<ChartDataPack> getCategoryResultsChartPackData(List<BMDExpressAnalysisDataSet> catResults,
			DataFilterPack pack, List<String> selectedIds);
}
