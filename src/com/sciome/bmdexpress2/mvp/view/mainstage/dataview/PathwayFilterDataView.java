package com.sciome.bmdexpress2.mvp.view.mainstage.dataview;

import com.sciome.bmdexpress2.mvp.model.prefilter.PathwayFilterResult;
import com.sciome.bmdexpress2.mvp.model.prefilter.PathwayFilterResults;
import com.sciome.bmdexpress2.mvp.presenter.mainstage.dataview.PathwayFilterDataViewPresenter;
import com.sciome.bmdexpress2.mvp.view.visualization.DataVisualizationView;
import com.sciome.bmdexpress2.mvp.view.visualization.PathwayFilterDataVisualizationView;
import com.sciome.bmdexpress2.mvp.viewinterface.mainstage.dataview.IBMDExpressDataView;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;

public class PathwayFilterDataView extends BMDExpressDataView<PathwayFilterResults>
		implements IBMDExpressDataView
{

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public PathwayFilterDataView(PathwayFilterResults pathwayFilterResults, String viewTypeKey)
	{

		super(PathwayFilterResult.class, pathwayFilterResults, viewTypeKey);
		presenter = new PathwayFilterDataViewPresenter(this, BMDExpressEventBus.getInstance());
		setUpTableView(pathwayFilterResults);
		presenter.showVisualizations(pathwayFilterResults);
	}

	@Override
	protected DataVisualizationView getDataVisualizationView()
	{
		return new PathwayFilterDataVisualizationView();
	}
}
