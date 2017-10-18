package com.sciome.bmdexpress2.mvp.view.mainstage.dataview;

import com.sciome.bmdexpress2.mvp.model.prefilter.OriogenResult;
import com.sciome.bmdexpress2.mvp.model.prefilter.OriogenResults;
import com.sciome.bmdexpress2.mvp.presenter.mainstage.dataview.OriogenDataViewPresenter;
import com.sciome.bmdexpress2.mvp.view.visualization.DataVisualizationView;
import com.sciome.bmdexpress2.mvp.view.visualization.OriogenDataVisualizationView;
import com.sciome.bmdexpress2.mvp.viewinterface.mainstage.dataview.IBMDExpressDataView;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;

public class OriogenDataView extends BMDExpressDataView<OriogenResults> implements IBMDExpressDataView {
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public OriogenDataView(OriogenResults oriogenResults, String viewTypeKey)
	{
		super(OriogenResult.class, oriogenResults, viewTypeKey);
		presenter = new OriogenDataViewPresenter(this, BMDExpressEventBus.getInstance());
		setUpTableView(oriogenResults);
		presenter.showVisualizations(oriogenResults);

	}

	@Override
	protected DataVisualizationView getDataVisualizationView()
	{
		return new OriogenDataVisualizationView();
	}
}
