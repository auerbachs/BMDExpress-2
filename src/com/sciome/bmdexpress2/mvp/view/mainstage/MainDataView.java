package com.sciome.bmdexpress2.mvp.view.mainstage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisDataSet;
import com.sciome.bmdexpress2.mvp.model.DoseResponseExperiment;
import com.sciome.bmdexpress2.mvp.model.category.CategoryAnalysisResults;
import com.sciome.bmdexpress2.mvp.model.prefilter.OneWayANOVAResults;
import com.sciome.bmdexpress2.mvp.model.prefilter.OriogenResults;
import com.sciome.bmdexpress2.mvp.model.prefilter.WilliamsTrendResults;
import com.sciome.bmdexpress2.mvp.model.stat.BMDResult;
import com.sciome.bmdexpress2.mvp.presenter.mainstage.MainDataPresenter;
import com.sciome.bmdexpress2.mvp.view.BMDExpressViewBase;
import com.sciome.bmdexpress2.mvp.view.mainstage.dataview.BMDAnalysisResultsDataView;
import com.sciome.bmdexpress2.mvp.view.mainstage.dataview.BMDExpressDataView;
import com.sciome.bmdexpress2.mvp.view.mainstage.dataview.CategoryAnalysisDataView;
import com.sciome.bmdexpress2.mvp.view.mainstage.dataview.ExpressionDataSetDataView;
import com.sciome.bmdexpress2.mvp.view.mainstage.dataview.OneWayANOVADataView;
import com.sciome.bmdexpress2.mvp.view.mainstage.dataview.OriogenDataView;
import com.sciome.bmdexpress2.mvp.view.mainstage.dataview.WilliamsTrendDataView;
import com.sciome.bmdexpress2.mvp.viewinterface.mainstage.IMainDataView;
import com.sciome.bmdexpress2.shared.BMDExpressFXUtils;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class MainDataView extends BMDExpressViewBase implements IMainDataView, Initializable
{

	@FXML
	private AnchorPane			tableAnchorPane;
	// table for showing matrix data in a paginator
	private BMDExpressDataView	spreadSheetTableView;

	MainDataPresenter			presenter;

	public MainDataView()
	{
		this(BMDExpressEventBus.getInstance());
	}

	/*
	 * Event bus is passed as an argument so the unit tests can pass their own custom eventbus
	 */
	public MainDataView(BMDExpressEventBus eventBus)
	{
		super();
		presenter = new MainDataPresenter(this, eventBus);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		
	}

	@Override
	public void loadDoseResponseExperiment(DoseResponseExperiment doseResponseExperiement)
	{
		// clear data in tableview if it is not null
		if (spreadSheetTableView != null)
		{
			spreadSheetTableView.close();
		}
		spreadSheetTableView = new ExpressionDataSetDataView(doseResponseExperiement, "main");
		updateSpreadSheet();

	}

	@Override
	public void loadOneWayANOVAAnalysis(OneWayANOVAResults oneWayANOVAResults)
	{
		// clear data in tableview if it is not null
		if (spreadSheetTableView != null)
		{
			spreadSheetTableView.close();
		}

		spreadSheetTableView = new OneWayANOVADataView(oneWayANOVAResults, "main");

		updateSpreadSheet();

	}

	@Override
	public void loadWilliamsTrendAnalysis(WilliamsTrendResults williamsTrendResults)
	{
		// clear data in tableview if it is not null
		if (spreadSheetTableView != null)
		{
			spreadSheetTableView.close();
		}

		spreadSheetTableView = new WilliamsTrendDataView(williamsTrendResults, "main");

		updateSpreadSheet();

	}
	
	@Override
	public void loadOriogenAnalysis(OriogenResults oriogenResults) {
		// clear data in tableview if it is not null
		if (spreadSheetTableView != null)
		{
			spreadSheetTableView.close();
		}

		spreadSheetTableView = new OriogenDataView(oriogenResults, "main");

		updateSpreadSheet();
	}
	
	@Override
	public void loadBMDResultAnalysis(BMDResult bMDAnalsyisResults)
	{

		// clear data in tableview if it is not null
		if (spreadSheetTableView != null)
		{
			spreadSheetTableView.close();
		}
		spreadSheetTableView = new BMDAnalysisResultsDataView(bMDAnalsyisResults, "main");
		updateSpreadSheet();

	}

	@Override
	public void loadCategoryAnalysis(CategoryAnalysisResults categoryAnalysisResults)
	{

		// clear data in tableview if it is not null
		if (spreadSheetTableView != null)
		{
			spreadSheetTableView.close();
		}
		spreadSheetTableView = new CategoryAnalysisDataView(categoryAnalysisResults, "main");

		updateSpreadSheet();

	}

	private void updateSpreadSheet()
	{
		tableAnchorPane.getChildren().clear();
		if (spreadSheetTableView == null)
			return;
		tableAnchorPane.getChildren().add(spreadSheetTableView);

		AnchorPane.setBottomAnchor(spreadSheetTableView, 5.0);
		AnchorPane.setTopAnchor(spreadSheetTableView, 5.0);
		AnchorPane.setLeftAnchor(spreadSheetTableView, 5.0);
		AnchorPane.setRightAnchor(spreadSheetTableView, 5.0);

	}

	@Override
	public void showBMDExpressAnalysisInSeparateWindow(BMDExpressAnalysisDataSet dataSet)
	{
		BMDExpressDataView tableView = null;
		String resultDesc = "BMD Analysis Results: ";
		if (dataSet instanceof BMDResult)
			tableView = new BMDAnalysisResultsDataView((BMDResult) dataSet, "spreadsheet");
		else if (dataSet instanceof OneWayANOVAResults)
		{
			tableView = new OneWayANOVADataView((OneWayANOVAResults) dataSet, "spreadsheet");
			resultDesc = "One Way ANOVA Results: ";
		}
		else if (dataSet instanceof WilliamsTrendResults)
		{
			tableView = new WilliamsTrendDataView((WilliamsTrendResults) dataSet, "spreadsheet");
			resultDesc = "William's Trend Results: ";
		}
		else if (dataSet instanceof OriogenResults)
		{
			tableView = new OriogenDataView((OriogenResults) dataSet, "spreadsheet");
			resultDesc = "Oriogen Results: ";
		}
		else if (dataSet instanceof CategoryAnalysisResults)
		{
			tableView = new CategoryAnalysisDataView((CategoryAnalysisResults) dataSet, "spreadsheet");
			resultDesc = "Category Analysis Results: ";
		}

		showTableView(tableView, resultDesc + dataSet.getName());

	}

	@Override
	public void showExpressDataInSeparateWindow(DoseResponseExperiment experiment)
	{

		BMDExpressDataView tableView = new ExpressionDataSetDataView(experiment, "spreadsheet");

		showTableView(tableView, "Dose Response Experiment: " + experiment.getName());

	}

	private void showTableView(BMDExpressDataView dataTableView, String title)
	{
		try
		{

			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/spreadsheet.fxml"));

			Stage stage = BMDExpressFXUtils.getInstance().generateStage(title);
			stage.setScene(new Scene((AnchorPane) loader.load()));

			stage.getScene().getStylesheets()
					.add(getClass().getResource("/fxml/application.css").toExternalForm());

			SpreadSheetView viewCode = loader.<SpreadSheetView> getController();
			// The true means that we are not running "select models only mode"
			viewCode.initData(dataTableView);
			stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				@Override
				public void handle(WindowEvent event)
				{
					viewCode.close();
				}
			});
			stage.sizeToScene();
			stage.show();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void clearTableView()
	{
		if (spreadSheetTableView != null)
		{
			spreadSheetTableView.close();
		}
		updateSpreadSheet();
		spreadSheetTableView = null;

	}

	@Override
	public void close()
	{
		if (presenter != null)
			presenter.destroy();

	}
}
