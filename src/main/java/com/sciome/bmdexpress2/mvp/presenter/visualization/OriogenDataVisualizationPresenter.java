package com.sciome.bmdexpress2.mvp.presenter.visualization;

import java.util.ArrayList;
import java.util.List;

import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisDataSet;
import com.sciome.bmdexpress2.mvp.model.prefilter.OriogenResults;
import com.sciome.bmdexpress2.mvp.viewinterface.visualization.IDataVisualizationView;
import com.sciome.bmdexpress2.serviceInterface.IVisualizationService;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;

public class OriogenDataVisualizationPresenter extends DataVisualizationPresenter{
	public OriogenDataVisualizationPresenter(IDataVisualizationView view, IVisualizationService service, BMDExpressEventBus eventBus)
	{
		super(view, service, eventBus);
	}

	@Override
	public List<BMDExpressAnalysisDataSet> getResultsFromProject(List<BMDExpressAnalysisDataSet> exclude)
	{
		List<BMDExpressAnalysisDataSet> returnList = new ArrayList<>();

		if (bmdProject != null && bmdProject.getOriogenResults() != null)
		{
			for (OriogenResults oriogenResults : bmdProject.getOriogenResults())
				returnList.add(oriogenResults);
		}

		for (BMDExpressAnalysisDataSet dataSet : exclude)
		{
			returnList.remove(dataSet);
		}

		return returnList;
	}
}
