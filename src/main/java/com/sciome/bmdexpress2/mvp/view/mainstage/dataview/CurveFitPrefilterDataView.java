package com.sciome.bmdexpress2.mvp.view.mainstage.dataview;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisDataSet;
import com.sciome.bmdexpress2.mvp.model.prefilter.CurveFitPrefilterResult;
import com.sciome.bmdexpress2.mvp.model.prefilter.PrefilterResults;
import com.sciome.bmdexpress2.mvp.model.prefilter.WilliamsTrendResults;
import com.sciome.bmdexpress2.mvp.presenter.mainstage.dataview.CurveFitPrefilterDataViewPresenter;
import com.sciome.bmdexpress2.mvp.view.visualization.DataVisualizationView;
import com.sciome.bmdexpress2.mvp.view.visualization.WilliamsTrendDataVisualizationView;
import com.sciome.bmdexpress2.mvp.viewinterface.mainstage.dataview.IBMDExpressDataView;
import com.sciome.bmdexpress2.shared.BMDExpressProperties;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;
import com.sciome.bmdexpress2.util.categoryanalysis.catmap.PathwayToGeneSymbolUtility;

public class CurveFitPrefilterDataView extends BMDExpressDataView<WilliamsTrendResults>
		implements IBMDExpressDataView
{

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public CurveFitPrefilterDataView(BMDExpressAnalysisDataSet curveFitPrefilterResults, String viewTypeKey)
	{
		super(CurveFitPrefilterResult.class, curveFitPrefilterResults, viewTypeKey);
		presenter = new CurveFitPrefilterDataViewPresenter(this, BMDExpressEventBus.getInstance());

		// Add any new columns to the map and list
		columnMap = BMDExpressProperties.getInstance().getTableInformation().getWilliamsTrendMap();
		columnOrder = BMDExpressProperties.getInstance().getTableInformation().getWilliamsTrendOrder();
		for (String header : curveFitPrefilterResults.getColumnHeader())
		{
			if (!columnMap.containsKey(header))
			{
				columnMap.put(header, true);
			}
			if (!columnOrder.contains(header))
			{
				if (header.equals("Analysis"))
					columnOrder.add(0, header);
				else
					columnOrder.add(header);
			}
		}

		setUpTableView(curveFitPrefilterResults);
		setUpTableListeners();
		presenter.showVisualizations(curveFitPrefilterResults);

	}

	@Override
	protected void setCellFactory()
	{
		// Do nothing for now
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
