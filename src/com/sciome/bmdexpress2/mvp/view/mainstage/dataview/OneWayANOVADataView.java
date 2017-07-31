package com.sciome.bmdexpress2.mvp.view.mainstage.dataview;

import com.sciome.bmdexpress2.mvp.model.prefilter.OneWayANOVAResult;
import com.sciome.bmdexpress2.mvp.model.prefilter.OneWayANOVAResults;
import com.sciome.bmdexpress2.mvp.presenter.mainstage.dataview.OneWayANOVADataViewPresenter;
import com.sciome.bmdexpress2.mvp.view.visualization.DataVisualizationView;
import com.sciome.bmdexpress2.mvp.view.visualization.OneWayANOVADataVisualizationView;
import com.sciome.bmdexpress2.mvp.viewinterface.mainstage.dataview.IBMDExpressDataView;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;

public class OneWayANOVADataView extends BMDExpressDataView<OneWayANOVAResults> implements IBMDExpressDataView
{

	// OneWayANOVADataViewPresenter presenter;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public OneWayANOVADataView(OneWayANOVAResults oneWayANOVAResults, String viewTypeKey)
	{
		super(OneWayANOVAResult.class, oneWayANOVAResults, viewTypeKey);
		presenter = new OneWayANOVADataViewPresenter(this, BMDExpressEventBus.getInstance());
		setUpTableView(oneWayANOVAResults);
		presenter.showVisualizations(oneWayANOVAResults);

	}

	@Override
	protected DataVisualizationView getDataVisualizationView()
	{
		return new OneWayANOVADataVisualizationView();
	}

}
