package com.sciome.bmdexpress2.service;

import java.util.List;
import java.util.Set;

import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisDataSet;
import com.sciome.bmdexpress2.mvp.model.ChartKey;
import com.sciome.bmdexpress2.serviceInterface.IVisualizationService;
import com.sciome.charts.data.ChartDataPack;
import com.sciome.charts.data.ChartDataPackMaker;
import com.sciome.filter.DataFilterPack;

public class VisualizationService implements IVisualizationService
{

	@Override
	public List<ChartDataPack> getBMDExpressAnalysisChartDataPack(List<BMDExpressAnalysisDataSet> catResults,
			DataFilterPack pack, Set<ChartKey> useTheseKeysOnly, Set<ChartKey> mathedChartKeys,
			ChartKey labelKey)
	{
		ChartDataPackMaker chartDataPackMaker = new ChartDataPackMaker(pack);
		List<ChartDataPack> chartDataPacks = chartDataPackMaker.generateDataPacks(catResults,
				useTheseKeysOnly, mathedChartKeys, labelKey);
		return chartDataPacks;
	}

}
