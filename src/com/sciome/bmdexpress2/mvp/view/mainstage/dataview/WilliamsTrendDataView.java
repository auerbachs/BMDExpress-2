package com.sciome.bmdexpress2.mvp.view.mainstage.dataview;

import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisDataSet;
import com.sciome.bmdexpress2.mvp.model.prefilter.WilliamsTrendResult;
import com.sciome.bmdexpress2.mvp.model.prefilter.WilliamsTrendResults;
import com.sciome.bmdexpress2.mvp.presenter.mainstage.dataview.WilliamsTrendDataViewPresenter;
import com.sciome.bmdexpress2.mvp.view.visualization.DataVisualizationView;
import com.sciome.bmdexpress2.mvp.view.visualization.WilliamsTrendDataVisualizationView;
import com.sciome.bmdexpress2.mvp.viewinterface.mainstage.dataview.IBMDExpressDataView;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;

public class WilliamsTrendDataView extends BMDExpressDataView<WilliamsTrendResults>
		implements IBMDExpressDataView
{

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public WilliamsTrendDataView(BMDExpressAnalysisDataSet williamsTrendResults, String viewTypeKey)
	{
		super(WilliamsTrendResult.class, williamsTrendResults, viewTypeKey);
		presenter = new WilliamsTrendDataViewPresenter(this, BMDExpressEventBus.getInstance());
		setUpTableView(williamsTrendResults);
		presenter.showVisualizations(williamsTrendResults);

	}

	@Override
	protected DataVisualizationView getDataVisualizationView()
	{
		return new WilliamsTrendDataVisualizationView();
	}

}
