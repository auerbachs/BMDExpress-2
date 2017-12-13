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
			DataFilterPack pack, List<String> selectedIds, Set<ChartKey> mathedChartKeys, ChartKey labelKey)
	{
		ChartDataPackMaker chartDataPackMaker = new ChartDataPackMaker(pack);
		List<ChartDataPack> chartDataPacks = chartDataPackMaker.generateDataPacks(catResults, mathedChartKeys,
				labelKey);
		removeSelectedIds(chartDataPacks, selectedIds);
		return chartDataPacks;
	}

	/*
	 * look at the data point label of each data row. if it's in the selected id's list, then allow it to
	 * pass.
	 */
	private void removeSelectedIds(List<ChartDataPack> chartDataPacks, List<String> selectedIds)
	{
		// don't worry about it if the ids list is null
		if (selectedIds == null)
			return;
		for (int i = 0; i < chartDataPacks.size(); i++)
		{
			int chartDataSize = chartDataPacks.get(i).getChartData().size();
			for (int j = 0; j < chartDataSize; j++)
			{
				if (!selectedIds.contains(chartDataPacks.get(i).getChartData().get(j).getDataPointLabel()))
				{
					chartDataPacks.get(i).getChartData().remove(j);
					j--;
					chartDataSize--;
				}
			}
			// now that the data has changed, recomput the basic stats.
			chartDataPacks.get(i).recomputeStats();

		}

	}
}
