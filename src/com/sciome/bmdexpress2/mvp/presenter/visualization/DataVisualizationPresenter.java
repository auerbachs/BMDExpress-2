package com.sciome.bmdexpress2.mvp.presenter.visualization;

import java.util.ArrayList;
import java.util.List;

import com.google.common.eventbus.Subscribe;
import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisDataSet;
import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisRow;
import com.sciome.bmdexpress2.mvp.model.BMDProject;
import com.sciome.bmdexpress2.mvp.model.stat.BMDResult;
import com.sciome.bmdexpress2.mvp.presenter.PresenterBase;
import com.sciome.bmdexpress2.mvp.viewinterface.visualization.IDataVisualizationView;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;
import com.sciome.bmdexpress2.shared.eventbus.project.GiveMeProjectRequest;
import com.sciome.bmdexpress2.shared.eventbus.project.HeresYourProjectEvent;
import com.sciome.bmdexpress2.shared.eventbus.visualizations.ShowBMDAnalysisDataSetVisualizationsEvent;
import com.sciome.charts.ChartDataPackMaker;
import com.sciome.charts.data.ChartDataPack;
import com.sciome.filter.DataFilterPack;

public abstract class DataVisualizationPresenter extends PresenterBase<IDataVisualizationView>
{
	protected BMDProject	bmdProject	= null;
	boolean					drawn		= false;

	public DataVisualizationPresenter(IDataVisualizationView view, BMDExpressEventBus eventBus)
	{
		super(view, eventBus);
		init();
	}

	public void initData(BMDProject bmdProject)
	{
		this.bmdProject = bmdProject;

	}

	private void init()
	{
		getEventBus().post(new GiveMeProjectRequest("please"));
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

	public List<ChartDataPack> getCategoryResultsChartPackData(List<BMDExpressAnalysisDataSet> catResults,
			DataFilterPack pack, List<String> selectedIds)
	{
		ChartDataPackMaker<BMDExpressAnalysisDataSet, BMDExpressAnalysisRow> chartDataPackMaker = new ChartDataPackMaker<>(
				pack);
		List<ChartDataPack> chartDataPacks = chartDataPackMaker.generateDataPacks(catResults);
		removeSelectedIds(chartDataPacks, selectedIds);
		return chartDataPacks;
	}

	public abstract List<BMDExpressAnalysisDataSet> getResultsFromProject(
			List<BMDExpressAnalysisDataSet> exclude);

	/*
	 * get list of bmdresults minus excluded
	 */
	public List<BMDExpressAnalysisDataSet> getResultsFromProject1(List<BMDExpressAnalysisDataSet> exclude)
	{
		List<BMDExpressAnalysisDataSet> returnList = new ArrayList<>();

		if (bmdProject != null && bmdProject.getbMDResult() != null)
		{
			for (BMDResult bmdResults : bmdProject.getbMDResult())
				returnList.add(bmdResults);
		}

		for (BMDExpressAnalysisDataSet dataSet : exclude)
		{
			returnList.remove(dataSet);
		}

		return returnList;
	}

	@Subscribe
	public void onShowBMDAnalysisDataSet(ShowBMDAnalysisDataSetVisualizationsEvent event)
	{
		if (!drawn)
			getView().drawResults(event.GetPayload());

		drawn = true;
	}

	@Subscribe
	public void onRecieveProject(HeresYourProjectEvent event)
	{
		this.bmdProject = event.GetPayload();
	}
}
