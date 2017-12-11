package com.sciome.bmdexpress2.mvp.view.mainstage.dataview;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import com.sciome.bmdexpress2.mvp.model.prefilter.OriogenResult;
import com.sciome.bmdexpress2.mvp.model.prefilter.OriogenResults;
import com.sciome.bmdexpress2.mvp.presenter.mainstage.dataview.OriogenDataViewPresenter;
import com.sciome.bmdexpress2.mvp.view.visualization.DataVisualizationView;
import com.sciome.bmdexpress2.mvp.view.visualization.OriogenDataVisualizationView;
import com.sciome.bmdexpress2.mvp.viewinterface.mainstage.dataview.IBMDExpressDataView;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;

public class OriogenDataView extends BMDExpressDataView<OriogenResults> implements IBMDExpressDataView
{

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

	@Override
	public Set<String> getItemsForMethod(Method method)
	{
		OriogenResults oriogenResults = (OriogenResults) this.bmdAnalysisDataSet;
		// load transient variables iwth this hacky cal
		oriogenResults.getColumnHeader();
		Set<String> items = new HashSet<>();
		for (OriogenResult oriogenResult : oriogenResults.getOriogenResults())
		{
			try
			{
				Object value = method.invoke(oriogenResult, null);
				if (value != null)
					items.add(value.toString());
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		return items;
	}

}
