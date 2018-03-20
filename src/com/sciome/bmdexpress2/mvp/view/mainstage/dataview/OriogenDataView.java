package com.sciome.bmdexpress2.mvp.view.mainstage.dataview;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisDataSet;
import com.sciome.bmdexpress2.mvp.model.prefilter.OriogenResult;
import com.sciome.bmdexpress2.mvp.model.prefilter.OriogenResults;
import com.sciome.bmdexpress2.mvp.model.prefilter.PrefilterResults;
import com.sciome.bmdexpress2.mvp.presenter.mainstage.dataview.OriogenDataViewPresenter;
import com.sciome.bmdexpress2.mvp.view.visualization.DataVisualizationView;
import com.sciome.bmdexpress2.mvp.view.visualization.OriogenDataVisualizationView;
import com.sciome.bmdexpress2.mvp.viewinterface.mainstage.dataview.IBMDExpressDataView;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;
import com.sciome.bmdexpress2.util.categoryanalysis.catmap.PathwayToGeneSymbolUtility;

public class OriogenDataView extends BMDExpressDataView<OriogenResults> implements IBMDExpressDataView
{

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public OriogenDataView(BMDExpressAnalysisDataSet oriogenResults, String viewTypeKey)
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
