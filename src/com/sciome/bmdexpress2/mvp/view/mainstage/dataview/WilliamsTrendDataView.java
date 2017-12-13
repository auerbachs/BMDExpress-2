package com.sciome.bmdexpress2.mvp.view.mainstage.dataview;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
	public WilliamsTrendDataView(WilliamsTrendResults williamsTrendResults, String viewTypeKey)
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

	@Override
	public Set<String> getItemsForMethod(Method method)
	{
		WilliamsTrendResults williamsTrendResults = (WilliamsTrendResults) this.bmdAnalysisDataSet;
		// load transient variables iwth this hacky cal
		williamsTrendResults.getColumnHeader();
		Set<String> items = new HashSet<>();
		for (WilliamsTrendResult williams : williamsTrendResults.getWilliamsTrendResults())
		{
			try
			{
				Object value = method.invoke(williams, null);
				if (value != null && !value.toString().trim().equals(""))
					items.add(value.toString().trim());
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		return items;
	}

	@Override
	public List<Object> getRangeForMethod(Method method)
	{
		List<Object> returnList = new ArrayList<>();
		Object min = null;
		Object max = null;
		WilliamsTrendResults williamsTrendResults = (WilliamsTrendResults) this.bmdAnalysisDataSet;
		// load transient variables iwth this hacky cal
		williamsTrendResults.getColumnHeader();
		Set<String> items = new HashSet<>();
		for (WilliamsTrendResult williams : williamsTrendResults.getWilliamsTrendResults())
		{
			try
			{
				Object value = method.invoke(williams, null);
				if (value != null)
				{
					if (min == null)
					{
						min = value;
						max = value;
						continue;
					}

					if (compareToNumericValues(value, min) == -1)
						min = value;
					if (compareToNumericValues(value, max) == 1)
						max = value;
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		returnList.add(min);
		returnList.add(max);

		return returnList;
	}

}
