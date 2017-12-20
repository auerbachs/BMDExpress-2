package com.sciome.bmdexpress2.mvp.view.mainstage.dataview;

import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisDataSet;
import com.sciome.bmdexpress2.mvp.model.probe.ProbeResponse;
import com.sciome.bmdexpress2.mvp.presenter.mainstage.dataview.ExpressionDataSetDataViewPresenter;
import com.sciome.bmdexpress2.mvp.view.visualization.DataVisualizationView;
import com.sciome.bmdexpress2.mvp.view.visualization.PCADataVisualizationView;
import com.sciome.bmdexpress2.mvp.viewinterface.mainstage.dataview.IBMDExpressDataView;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

/*
 * Show the Expression Data.  currently this implementation
 * deals with its own table view because DoseResponseExperiment
 */
public class ExpressionDataSetDataView extends BMDExpressDataView<ProbeResponse>
		implements IBMDExpressDataView
{

	protected Button											calculatePCA;
	
	private final String 										CALCULATE_PCA = "Calculate PCA";
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ExpressionDataSetDataView(BMDExpressAnalysisDataSet doseResponseExperiement, String viewTypeKey)
	{
		super(ProbeResponse.class, doseResponseExperiement, viewTypeKey);

		calculatePCA = new Button(CALCULATE_PCA);
		calculatePCA.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e)
			{
				presenter.showVisualizations(doseResponseExperiement);
			}
		});
		topHBox.getChildren().add(calculatePCA);
		
		
		presenter = new ExpressionDataSetDataViewPresenter(this, BMDExpressEventBus.getInstance());

		if (doseResponseExperiement.getColumnHeader().size() == 0)
			return;
		setUpTableView(doseResponseExperiement);
		
		//Disable filtering options for now
		splitPane.getItems().remove(filtrationNode);
		hideFilter.setDisable(true);
		enableFilterCheckBox.setDisable(true);
	}

	@Override
	protected DataVisualizationView getDataVisualizationView()
	{
		return new PCADataVisualizationView();
	}
}