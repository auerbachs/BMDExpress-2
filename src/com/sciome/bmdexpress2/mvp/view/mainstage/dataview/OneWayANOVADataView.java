package com.sciome.bmdexpress2.mvp.view.mainstage.dataview;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

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

	@Override
	public Set<String> getItemsForMethod(Method method)
	{
		OneWayANOVAResults oneWayANOVAResults = (OneWayANOVAResults) this.bmdAnalysisDataSet;
		// load transient variables iwth this hacky cal
		oneWayANOVAResults.getColumnHeader();
		Set<String> items = new HashSet<>();
		for (OneWayANOVAResult onewayResult : oneWayANOVAResults.getOneWayANOVAResults())
		{
			try
			{
				Object value = method.invoke(onewayResult, null);
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
