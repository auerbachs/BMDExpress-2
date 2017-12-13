package com.sciome.bmdexpress2.mvp.view.mainstage.dataview;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
		Set<String> items = new HashSet<>();
		OriogenResults oriogenResults = (OriogenResults) this.bmdAnalysisDataSet;
		// load transient variables iwth this hacky cal
		oriogenResults.getColumnHeader();

		for (OriogenResult oriogenResult : oriogenResults.getOriogenResults())
		{
			try
			{
				Object value = method.invoke(oriogenResult, null);
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
		OriogenResults oriogenResults = (OriogenResults) this.bmdAnalysisDataSet;
		// load transient variables iwth this hacky cal
		oriogenResults.getColumnHeader();

		for (OriogenResult oriogenResult : oriogenResults.getOriogenResults())
		{
			try
			{
				Object value = method.invoke(oriogenResults, null);
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
