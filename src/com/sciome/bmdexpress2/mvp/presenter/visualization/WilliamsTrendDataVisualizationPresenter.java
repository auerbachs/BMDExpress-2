package com.sciome.bmdexpress2.mvp.presenter.visualization;

import java.util.ArrayList;
import java.util.List;

import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisDataSet;
import com.sciome.bmdexpress2.mvp.model.prefilter.WilliamsTrendResults;
import com.sciome.bmdexpress2.mvp.viewinterface.visualization.IDataVisualizationView;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;

public class WilliamsTrendDataVisualizationPresenter extends DataVisualizationPresenter{
	public WilliamsTrendDataVisualizationPresenter(IDataVisualizationView view, BMDExpressEventBus eventBus)
	{
		super(view, eventBus);
	}

	@Override
	public List<BMDExpressAnalysisDataSet> getResultsFromProject(List<BMDExpressAnalysisDataSet> exclude)
	{
		List<BMDExpressAnalysisDataSet> returnList = new ArrayList<>();

		if (bmdProject != null && bmdProject.getWilliamsTrendResults() != null)
		{
			for (WilliamsTrendResults williamsResults : bmdProject.getWilliamsTrendResults())
				returnList.add(williamsResults);
		}

		for (BMDExpressAnalysisDataSet dataSet : exclude)
		{
			returnList.remove(dataSet);
		}

		return returnList;
	}
}
