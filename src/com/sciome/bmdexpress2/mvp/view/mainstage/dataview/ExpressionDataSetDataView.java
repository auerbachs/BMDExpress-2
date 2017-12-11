package com.sciome.bmdexpress2.mvp.view.mainstage.dataview;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import com.sciome.bmdexpress2.mvp.model.DoseResponseExperiment;
import com.sciome.bmdexpress2.mvp.model.probe.ProbeResponse;
import com.sciome.bmdexpress2.mvp.presenter.mainstage.dataview.ExpressionDataSetDataViewPresenter;
import com.sciome.bmdexpress2.mvp.view.visualization.DataVisualizationView;
import com.sciome.bmdexpress2.mvp.viewinterface.mainstage.dataview.IBMDExpressDataView;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;

/*
 * Show the Expression Data.  currently this implementation
 * deals with its own table view because DoseResponseExperiment
 */
public class ExpressionDataSetDataView extends BMDExpressDataView<ProbeResponse>
		implements IBMDExpressDataView
{

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ExpressionDataSetDataView(DoseResponseExperiment doseResponseExperiement, String viewTypeKey)
	{
		super(ProbeResponse.class, doseResponseExperiement, viewTypeKey);

		presenter = new ExpressionDataSetDataViewPresenter(this, BMDExpressEventBus.getInstance());

		if (doseResponseExperiement.getColumnHeader().size() == 0)
			return;
		setUpTableView(doseResponseExperiement);

		splitPaneMain.getItems().remove(dataVisualizationNode);
		splitPane.getItems().remove(filtrationNode);
		hideFilter.setDisable(true);
		hideTable.setDisable(true);
		hideCharts.setDisable(true);
		enableFilterCheckBox.setDisable(true);
		drawSelectedIdsCheckBox.setDisable(true);

	}

	@Override
	protected DataVisualizationView getDataVisualizationView()
	{
		return null;
	}

	@Override
	public Set<String> getItemsForMethod(Method method)
	{
		Set<String> items = new HashSet<>();

		return items;
	}

}
