package com.sciome.bmdexpress2.mvp.presenter.visualization;

import java.util.ArrayList;
import java.util.List;

import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisDataSet;
import com.sciome.bmdexpress2.mvp.model.category.CategoryAnalysisResults;
import com.sciome.bmdexpress2.mvp.viewinterface.visualization.IDataVisualizationView;
import com.sciome.bmdexpress2.serviceInterface.IVisualizationService;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;

public class CategoryAnalysisDataVisualizationPresenter extends DataVisualizationPresenter
{

	public CategoryAnalysisDataVisualizationPresenter(IDataVisualizationView view, IVisualizationService service,
			BMDExpressEventBus eventBus)
	{
		super(view, service, eventBus);
	}

	@Override
	public List<BMDExpressAnalysisDataSet> getResultsFromProject(List<BMDExpressAnalysisDataSet> exclude)
	{
		List<BMDExpressAnalysisDataSet> returnList = new ArrayList<>();

		if (bmdProject != null && bmdProject.getCategoryAnalysisResults() != null)
		{
			for (CategoryAnalysisResults catResults : bmdProject.getCategoryAnalysisResults())
				returnList.add(catResults);
		}

		for (BMDExpressAnalysisDataSet dataSet : exclude)
		{
			returnList.remove(dataSet);
		}

		return returnList;
	}

}
