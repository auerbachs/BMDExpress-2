package com.sciome.bmdexpress2.mvp.presenter.visualization;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.common.eventbus.Subscribe;
import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisDataSet;
import com.sciome.bmdexpress2.mvp.model.BMDProject;
import com.sciome.bmdexpress2.mvp.model.ChartKey;
import com.sciome.bmdexpress2.mvp.model.stat.BMDResult;
import com.sciome.bmdexpress2.mvp.presenter.presenterbases.ServicePresenterBase;
import com.sciome.bmdexpress2.mvp.viewinterface.visualization.IDataVisualizationView;
import com.sciome.bmdexpress2.serviceInterface.IVisualizationService;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;
import com.sciome.bmdexpress2.shared.eventbus.project.GiveMeProjectRequest;
import com.sciome.bmdexpress2.shared.eventbus.project.HeresYourProjectEvent;
import com.sciome.bmdexpress2.shared.eventbus.visualizations.ShowBMDAnalysisDataSetVisualizationsEvent;
import com.sciome.charts.data.ChartDataPack;
import com.sciome.filter.DataFilterPack;

public abstract class DataVisualizationPresenter
		extends ServicePresenterBase<IDataVisualizationView, IVisualizationService>
{
	protected BMDProject	bmdProject	= null;
	boolean					drawn		= false;

	public DataVisualizationPresenter(IDataVisualizationView view, IVisualizationService service,
			BMDExpressEventBus eventBus)
	{
		super(view, service, eventBus);
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

	public List<ChartDataPack> getBMDAnalysisDataSetChartDataPack(List<BMDExpressAnalysisDataSet> catResults,
			DataFilterPack pack, Set<ChartKey> useTheseKeysOnly, Set<ChartKey> mathedKeys, ChartKey label)
	{
		return getService().getBMDExpressAnalysisChartDataPack(catResults, pack, useTheseKeysOnly, mathedKeys,
				label);
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
		try
		{
			if (!drawn)
				getView().drawResults(event.GetPayload());

			drawn = true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Subscribe
	public void onRecieveProject(HeresYourProjectEvent event)
	{
		this.bmdProject = event.GetPayload();
	}
}
