package com.sciome.bmdexpress2.mvp.presenter.mainstage.dataview;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisDataSet;
import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisRow;
import com.sciome.bmdexpress2.mvp.model.DoseResponseExperiment;
import com.sciome.bmdexpress2.mvp.presenter.presenterbases.ServicePresenterBase;
import com.sciome.bmdexpress2.mvp.viewinterface.mainstage.dataview.IBMDExpressDataView;
import com.sciome.bmdexpress2.serviceInterface.IProjectNavigationService;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;
import com.sciome.bmdexpress2.shared.eventbus.visualizations.ShowBMDAnalysisDataSetVisualizationsEvent;
import com.sciome.filter.DataFilterPack;

import javafx.collections.transformation.FilteredList;

public abstract class BMDExpressDataViewPresenter<T> extends ServicePresenterBase<IBMDExpressDataView, IProjectNavigationService>
{
	public BMDExpressDataViewPresenter(IBMDExpressDataView view, IProjectNavigationService service, BMDExpressEventBus eventBus)
	{
		super(view, service, eventBus);
	}

	public void showVisualizations(BMDExpressAnalysisDataSet dataSet)
	{
		List<BMDExpressAnalysisDataSet> results = new ArrayList<>();
		results.add(dataSet);
		getEventBus().post(new ShowBMDAnalysisDataSetVisualizationsEvent(results));
	}
	
	public void exportResults(BMDExpressAnalysisDataSet bmdResults, File selectedFile) {
		//Dose response experiments can't be filtered so use exporting from project navigation service instead
		if(bmdResults instanceof DoseResponseExperiment) {
			getService().exportDoseResponseExperiment((DoseResponseExperiment)bmdResults, selectedFile);
		} else {
			getService().exportBMDExpressAnalysisDataSet(bmdResults, selectedFile);
		}
	}
	
	public void exportFilteredResults(BMDExpressAnalysisDataSet bmdResults, FilteredList<BMDExpressAnalysisRow> filteredResults, File selectedFile, DataFilterPack pack)
	{
		getService().exportFilteredResults(bmdResults, filteredResults, selectedFile, pack);
	}
}
