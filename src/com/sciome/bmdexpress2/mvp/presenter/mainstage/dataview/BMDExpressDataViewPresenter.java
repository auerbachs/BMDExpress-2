package com.sciome.bmdexpress2.mvp.presenter.mainstage.dataview;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisDataSet;
import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisRow;
import com.sciome.bmdexpress2.mvp.presenter.presenterbases.PresenterBase;
import com.sciome.bmdexpress2.mvp.viewinterface.mainstage.dataview.IBMDExpressDataView;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;
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
	
	public void exportFilteredResults(BMDExpressAnalysisDataSet bmdResults, FilteredList<BMDExpressAnalysisRow> filteredResults, File selectedFile)
	{
		try
		{
			BufferedWriter writer = new BufferedWriter(new FileWriter(selectedFile), 1024 * 2000);
			writer.write(String.join("\n", bmdResults.getAnalysisInfo().getNotes()) + "\n");
			writer.write(String.join("\t", bmdResults.getColumnHeader()) + "\n");
			writer.write(exportFilteredBMDExpressAnalysisDataSet(filteredResults));
			writer.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	//This method is copied from ProjectNavigationView (might be a better way)
	private String exportFilteredBMDExpressAnalysisDataSet(FilteredList<BMDExpressAnalysisRow> filteredResults)
	{
		StringBuffer sb = new StringBuffer();

		for (BMDExpressAnalysisRow result : filteredResults)
		{
			sb.append(joinRowData(result.getRow(), "\t") + "\n");
		}
		return sb.toString();
	}

	//This method is copied from ProjectNavigationView (might be a better way)
	private String joinRowData(List<Object> datas, String delimiter)
	{
		StringBuffer bf = new StringBuffer();
		int i = 0;
		if (datas == null)
		{
			return "";
		}
		for (Object data : datas)
		{
			if (data != null)
			{
				bf.append(data);
			}

			if (i < datas.size())
			{
				bf.append(delimiter);
			}
		}

		return bf.toString();
	}
}
