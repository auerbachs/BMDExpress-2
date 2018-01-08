package com.sciome.bmdexpress2.mvp.view.mainstage.dataview;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisDataSet;
import com.sciome.bmdexpress2.mvp.model.prefilter.OneWayANOVAResult;
import com.sciome.bmdexpress2.mvp.model.prefilter.OneWayANOVAResults;
import com.sciome.bmdexpress2.mvp.model.prefilter.PrefilterResults;
import com.sciome.bmdexpress2.mvp.presenter.mainstage.dataview.OneWayANOVADataViewPresenter;
import com.sciome.bmdexpress2.mvp.view.visualization.DataVisualizationView;
import com.sciome.bmdexpress2.mvp.view.visualization.OneWayANOVADataVisualizationView;
import com.sciome.bmdexpress2.mvp.viewinterface.mainstage.dataview.IBMDExpressDataView;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;
import com.sciome.bmdexpress2.util.categoryanalysis.catmap.PathwayToGeneSymbolUtility;

public class OneWayANOVADataView extends BMDExpressDataView<OneWayANOVAResults> implements IBMDExpressDataView
{

	// OneWayANOVADataViewPresenter presenter;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public OneWayANOVADataView(BMDExpressAnalysisDataSet oneWayANOVAResults, String viewTypeKey)
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
	protected Map<String, Map<String, Set<String>>> fillUpDBToPathwayGeneSymbols()
	{

		try
		{
			Object obj = bmdAnalysisDataSet.getObject();
			if (bmdAnalysisDataSet.getObject() instanceof List)
				obj = ((List) bmdAnalysisDataSet.getObject()).get(0);
			return PathwayToGeneSymbolUtility.getInstance()
					.getdbToPathwaytoGeneSet(((PrefilterResults) obj).getDoseResponseExperiement());
		}
		catch (Exception e)
		{

		}
		return new HashMap<>();
	}

}
