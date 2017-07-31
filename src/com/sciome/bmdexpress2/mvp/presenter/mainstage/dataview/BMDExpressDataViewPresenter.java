package com.sciome.bmdexpress2.mvp.presenter.mainstage.dataview;

import java.util.ArrayList;
import java.util.List;

import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisDataSet;
import com.sciome.bmdexpress2.mvp.presenter.PresenterBase;
import com.sciome.bmdexpress2.mvp.viewinterface.mainstage.dataview.IBMDExpressDataView;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;
import com.sciome.bmdexpress2.shared.eventbus.visualizations.ShowBMDAnalysisDataSetVisualizationsEvent;

public abstract class BMDExpressDataViewPresenter<T> extends PresenterBase<IBMDExpressDataView>
{
	public BMDExpressDataViewPresenter(IBMDExpressDataView view, BMDExpressEventBus eventBus)
	{
		super(view, eventBus);
		init();
	}

	private void init()
	{
	}

	public void showVisualizations(BMDExpressAnalysisDataSet dataSet)
	{

		List<BMDExpressAnalysisDataSet> results = new ArrayList<>();
		results.add(dataSet);
		getEventBus().post(new ShowBMDAnalysisDataSetVisualizationsEvent(results));

	}

}
