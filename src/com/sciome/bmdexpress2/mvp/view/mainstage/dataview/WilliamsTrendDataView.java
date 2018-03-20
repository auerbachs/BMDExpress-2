package com.sciome.bmdexpress2.mvp.view.mainstage.dataview;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisDataSet;
import com.sciome.bmdexpress2.mvp.model.prefilter.PrefilterResults;
import com.sciome.bmdexpress2.mvp.model.prefilter.WilliamsTrendResult;
import com.sciome.bmdexpress2.mvp.model.prefilter.WilliamsTrendResults;
import com.sciome.bmdexpress2.mvp.presenter.mainstage.dataview.WilliamsTrendDataViewPresenter;
import com.sciome.bmdexpress2.mvp.view.visualization.DataVisualizationView;
import com.sciome.bmdexpress2.mvp.view.visualization.WilliamsTrendDataVisualizationView;
import com.sciome.bmdexpress2.mvp.viewinterface.mainstage.dataview.IBMDExpressDataView;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;
import com.sciome.bmdexpress2.util.categoryanalysis.catmap.PathwayToGeneSymbolUtility;

public class WilliamsTrendDataView extends BMDExpressDataView<WilliamsTrendResults>
		implements IBMDExpressDataView
{

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public WilliamsTrendDataView(BMDExpressAnalysisDataSet williamsTrendResults, String viewTypeKey)
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
