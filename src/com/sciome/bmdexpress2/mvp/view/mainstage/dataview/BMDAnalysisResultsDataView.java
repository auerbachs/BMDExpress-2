package com.sciome.bmdexpress2.mvp.view.mainstage.dataview;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisDataSet;
import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisRow;
import com.sciome.bmdexpress2.mvp.model.CombinedDataSet;
import com.sciome.bmdexpress2.mvp.model.CombinedRow;
import com.sciome.bmdexpress2.mvp.model.stat.BMDResult;
import com.sciome.bmdexpress2.mvp.model.stat.ProbeStatResult;
import com.sciome.bmdexpress2.mvp.presenter.mainstage.dataview.BMDAnalysisResultsDataViewPresenter;
import com.sciome.bmdexpress2.mvp.view.mainstage.CurveFitView;
import com.sciome.bmdexpress2.mvp.view.visualization.BMDAnalysisResultsDataVisualizationView;
import com.sciome.bmdexpress2.mvp.view.visualization.DataVisualizationView;
import com.sciome.bmdexpress2.mvp.viewinterface.mainstage.dataview.IBMDExpressDataView;
import com.sciome.bmdexpress2.shared.BMDExpressFXUtils;
import com.sciome.bmdexpress2.shared.BMDExpressProperties;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;
import com.sciome.bmdexpress2.util.categoryanalysis.catmap.PathwayToGeneSymbolUtility;

import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

public class BMDAnalysisResultsDataView extends BMDExpressDataView<BMDResult> implements IBMDExpressDataView
{

	private BMDTableCallBack probeIDCellFactory;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public BMDAnalysisResultsDataView(BMDExpressAnalysisDataSet bmdResult, String viewTypeKey)
	{
		super(ProbeStatResult.class, bmdResult, viewTypeKey);

		try
		{
			presenter = new BMDAnalysisResultsDataViewPresenter(this, BMDExpressEventBus.getInstance());
			
			if (bmdResult.getColumnHeader().size() == 0)
				return;

			//Add any new columns to the map and list
			columnMap = BMDExpressProperties.getInstance().getTableInformation().getBmdMap();
			columnOrder = BMDExpressProperties.getInstance().getTableInformation().getBmdOrder();
			for(String header : bmdResult.getColumnHeader()) {
				if(!columnMap.containsKey(header)) {
					columnMap.put(header, true);
				}
				if(!columnOrder.contains(header)) {
					if(header.equals("Analysis"))
						columnOrder.add(0, header);
					else
						columnOrder.add(header);
				}
			}
			
			setUpTableView(bmdResult);
			setUpTableListeners();

			setCellFactory();
			
			presenter.showVisualizations(bmdResult);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	protected void setCellFactory() {
		if(columnMap.get("Probe ID")) {
			int probeIDColumn = columnOrder.indexOf("Probe ID");
			
			TableColumn tc = tableView.getColumns().get(probeIDColumn);
	
			if (bmdAnalysisDataSet instanceof BMDResult)
			{
				// Create a CellFactory for the probeid. The reason for this is to allow us to detect user
				// mouse
				// click
				// inside the cell so we can show the curve
				probeIDCellFactory = new BMDTableCallBack((BMDResult) bmdAnalysisDataSet);
				tc.setCellFactory(probeIDCellFactory);
			}
			else if (bmdAnalysisDataSet instanceof CombinedDataSet)
			{
				probeIDCellFactory = new BMDTableCallBack(null);
				tc.setCellFactory(probeIDCellFactory);
			}
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void close()
	{
		try
		{
			if (tableView != null && tableView.getColumns().size() > 0)
			{
				int probeIDColumn = 0;
				if (bmdAnalysisDataSet instanceof CombinedDataSet)
					probeIDColumn = 1;
				TableColumn tc = tableView.getColumns().get(probeIDColumn);
				tc.setCellFactory(null);
			}
			if (probeIDCellFactory != null)
				probeIDCellFactory.close();
			super.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	@Override
	protected DataVisualizationView getDataVisualizationView()
	{
		return new BMDAnalysisResultsDataVisualizationView();
	}

	private class BMDTableCallBack implements Callback<TableColumn, TableCell>
	{
		BMDResult					bmdResult;
		private CurveFitView	modelGraphics;

		public BMDTableCallBack(BMDResult bmdr)
		{
			bmdResult = bmdr;
		}

		public void close()
		{
			bmdResult = null;
		}

		@Override
		public TableCell call(TableColumn param)
		{

			TableCell cell = new TableCell<ProbeStatResult, String>() {

				// must override drawing the cell so we can color it blue.
				@Override
				public void updateItem(String item, boolean empty)
				{
					super.updateItem(item, empty);
					setTextFill(javafx.scene.paint.Color.BLUE);
					setText(empty ? null : getString());
					setGraphic(null);
				}

				private String getString()
				{
					return getItem() == null ? "" : getItem().toString();
				}
			};

			// add mouse click event handler.
			cell.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent event)
				{
					if (event.getClickCount() != 1)
					{
						return;
					}
					TableCell c = (TableCell) event.getSource();
					BMDExpressAnalysisRow row = (BMDExpressAnalysisRow) c.getTableRow().getItem();
					ProbeStatResult item = (ProbeStatResult) row.getObject();

					if (item == null)
					{
						return;
					}
					if (modelGraphics != null)
					{
						if (item.getBestStatResult() != null)
							modelGraphics.setSelectedModel(item.getBestStatResult().toString());
						else
							modelGraphics.setSelectedModel(item.getStatResults().get(0).toString());
						modelGraphics.setSelectedProbe(item.getProbeResponse().getProbe());
					}
					else
					{
						// we are making a strong assumption that the parent for a
						// row here in this context is a BMDResult
						if (row instanceof CombinedRow)
							bmdResult = (BMDResult) ((CombinedRow) row).getParentObject();
						showGraphView(bmdResult, item);
					}

				}
			});
			return cell;
		}

		/*
		 * after user clicks on a probe id then we show the curve fit view
		 */
		private void showGraphView(BMDResult bmdResult, ProbeStatResult probeStatResult)
		{
			String name = "";
			if (bmdResult != null)
				name = bmdResult.getName();
			else
				name = probeStatResult.getChartableDataLabel();

			try
			{
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/curvefit.fxml"));

				Stage stage = BMDExpressFXUtils.getInstance().generateStage("Curve Viewer: " + name);
				new Stage(StageStyle.DECORATED);
				// stage.setAlwaysOnTop(true);
				stage.setScene(new Scene((AnchorPane) loader.load()));
				CurveFitView controller = loader.<CurveFitView> getController();
				controller.initData(bmdResult, probeStatResult);

				stage.sizeToScene();
				stage.show();
				stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
					@Override
					public void handle(WindowEvent we)
					{
						modelGraphics = null;
					}
				});

			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
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
					.getdbToPathwaytoGeneSet(((BMDResult) obj).getDoseResponseExperiment());
		}
		catch (Exception e)
		{

		}
		return new HashMap<>();
	}
}
