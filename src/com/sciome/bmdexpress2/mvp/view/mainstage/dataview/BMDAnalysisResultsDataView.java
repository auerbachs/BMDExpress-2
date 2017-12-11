package com.sciome.bmdexpress2.mvp.view.mainstage.dataview;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.sciome.bmdexpress2.mvp.model.stat.BMDResult;
import com.sciome.bmdexpress2.mvp.model.stat.ProbeStatResult;
import com.sciome.bmdexpress2.mvp.presenter.mainstage.dataview.BMDAnalysisResultsDataViewPresenter;
import com.sciome.bmdexpress2.mvp.view.mainstage.CurveFitView;
import com.sciome.bmdexpress2.mvp.view.visualization.BMDAnalysisResultsDataVisualizationView;
import com.sciome.bmdexpress2.mvp.view.visualization.DataVisualizationView;
import com.sciome.bmdexpress2.mvp.viewinterface.mainstage.dataview.IBMDExpressDataView;
import com.sciome.bmdexpress2.shared.BMDExpressFXUtils;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;
import com.sciome.bmdexpress2.util.visualizations.curvefit.ModelGraphics;
import com.sciome.bmdexpress2.util.visualizations.curvefit.ModelGraphicsEvent;

import javafx.application.Platform;
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
	public BMDAnalysisResultsDataView(BMDResult bmdResult, String viewTypeKey)
	{
		super(ProbeStatResult.class, bmdResult, viewTypeKey);

		presenter = new BMDAnalysisResultsDataViewPresenter(this, BMDExpressEventBus.getInstance());

		// Create a CellFactory for the probeid. The reason for this is to allow us to detect user mouse click
		// inside the cell so we can show the curve
		probeIDCellFactory = new BMDTableCallBack(bmdResult);

		if (bmdResult.getColumnHeader().size() == 0)
			return;
		setUpTableView(bmdResult);
		TableColumn tc = tableView.getColumns().get(0);
		tc.setCellFactory(probeIDCellFactory);

		presenter.showVisualizations(bmdResult);

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void close()
	{
		if (tableView != null && tableView.getColumns().size() > 0)
		{
			TableColumn tc = tableView.getColumns().get(0);
			tc.setCellFactory(null);
		}
		probeIDCellFactory.close();
		super.close();

	}

	@Override
	protected DataVisualizationView getDataVisualizationView()
	{
		return new BMDAnalysisResultsDataVisualizationView();
	}

	@Override
	public Set<String> getItemsForMethod(Method method)
	{
		BMDResult bmdResult = (BMDResult) this.bmdAnalysisDataSet;

		// due to transient fields, need to initiate
		// them with this method. this is not a great way to do it because
		// this call is scattered through the code. need to deal with this
		bmdResult.getColumnHeader();
		Set<String> items = new HashSet<>();
		for (ProbeStatResult psr : bmdResult.getProbeStatResults())
		{
			try
			{
				Object value = method.invoke(psr, null);
				if (value != null)
					items.add(value.toString());
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		return items;
	}

	@Override
	public List getRangeForMethod(Method method)
	{
		List<Object> returnList = new ArrayList<>();

		BMDResult bmdResult = (BMDResult) this.bmdAnalysisDataSet;

		// due to transient fields, need to initiate
		// them with this method. this is not a great way to do it because
		// this call is scattered through the code. need to deal with this
		bmdResult.getColumnHeader();
		Set<String> items = new HashSet<>();
		Object min = null;
		Object max = null;
		for (ProbeStatResult psr : bmdResult.getProbeStatResults())
		{
			try
			{
				Object value = method.invoke(psr, null);
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

final class BMDTableCallBack implements Callback<TableColumn, TableCell>
{
	BMDResult				bmdResult;
	private ModelGraphics	modelGraphics;

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
				ProbeStatResult item = (ProbeStatResult) c.getTableRow().getItem();

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
					showGraphView(bmdResult, item);
				}

			}
		});
		return cell;
	}

	/*
	 * after user clicks on a probe id then we show the curve fit view which is a swingnode based thing.
	 */
	private void showGraphView(BMDResult bmdResult, ProbeStatResult probeStatResult)
	{

		try
		{
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/curvefit.fxml"));

			Stage stage = BMDExpressFXUtils.getInstance()
					.generateStage("Curve Viewer: " + bmdResult.getName());
			new Stage(StageStyle.DECORATED);
			// stage.setAlwaysOnTop(true);
			stage.setScene(new Scene((AnchorPane) loader.load()));
			CurveFitView controller = loader.<CurveFitView> getController();
			if (modelGraphics == null)
			{
				modelGraphics = new ModelGraphics(bmdResult, probeStatResult.getBestStatResult(),
						new ModelGraphicsEvent() {

							@Override
							public void closeModelGraphics()
							{
								modelGraphics = null;
								Platform.runLater(new Runnable() {

									@Override
									public void run()
									{
										stage.close();
									}
								});

							}
						});

			}
			controller.initData(bmdResult, probeStatResult, modelGraphics);

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
