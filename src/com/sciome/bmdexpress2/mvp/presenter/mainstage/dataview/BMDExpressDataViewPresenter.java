package com.sciome.bmdexpress2.mvp.presenter.mainstage.dataview;

import java.util.ArrayList;
import java.util.List;

import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisDataSet;
import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisRow;
import com.sciome.bmdexpress2.mvp.presenter.presenterbases.PresenterBase;
import com.sciome.bmdexpress2.mvp.viewinterface.mainstage.dataview.IBMDExpressDataView;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;
import com.sciome.bmdexpress2.shared.eventbus.analysis.DataFilteredEvent;
import com.sciome.bmdexpress2.shared.eventbus.visualizations.ShowBMDAnalysisDataSetVisualizationsEvent;

import javafx.collections.transformation.FilteredList;

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

	public void postFilterEvent(FilteredList<BMDExpressAnalysisRow> filteredData)
	{
		getEventBus().post(new DataFilteredEvent(filteredData));
	}
}
