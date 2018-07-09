package com.sciome.bmdexpress2.mvp.view.mainstage.dataview;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisDataSet;
import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisRow;
import com.sciome.bmdexpress2.mvp.model.CombinedDataSet;
import com.sciome.bmdexpress2.mvp.model.category.CategoryAnalysisResult;
import com.sciome.bmdexpress2.mvp.model.category.CategoryAnalysisResults;
import com.sciome.bmdexpress2.mvp.model.category.GOAnalysisResult;
import com.sciome.bmdexpress2.mvp.model.category.PathwayAnalysisResult;
import com.sciome.bmdexpress2.mvp.presenter.mainstage.dataview.CategoryAnalysisDataViewPresenter;
import com.sciome.bmdexpress2.mvp.view.visualization.CategoryAnalysisDataVisualizationView;
import com.sciome.bmdexpress2.mvp.view.visualization.DataVisualizationView;
import com.sciome.bmdexpress2.mvp.viewinterface.mainstage.dataview.IBMDExpressDataView;
import com.sciome.bmdexpress2.shared.BMDExpressConstants;
import com.sciome.bmdexpress2.shared.BMDExpressProperties;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;
import com.sciome.bmdexpress2.util.categoryanalysis.catmap.PathwayToGeneSymbolUtility;

import javafx.event.EventHandler;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

public class CategoryAnalysisDataView extends BMDExpressDataView<CategoryAnalysisResults>
		implements IBMDExpressDataView
{

	private Callback<TableColumn, TableCell> categoryCellFactory;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public CategoryAnalysisDataView(BMDExpressAnalysisDataSet categoryAnalysisResults, String viewTypeKey)
	{
		super(CategoryAnalysisResult.class, categoryAnalysisResults, viewTypeKey);
		presenter = new CategoryAnalysisDataViewPresenter(this, BMDExpressEventBus.getInstance());

		//Add any new columns to the map and list
		columnMap = BMDExpressProperties.getInstance().getTableInformation().getCategoryAnalysisMap();
		columnOrder = BMDExpressProperties.getInstance().getTableInformation().getCategoryAnalysisOrder();
		for(String header : categoryAnalysisResults.getColumnHeader()) {
			if(!columnMap.containsKey(header)) {
				columnMap.put(header, true);
			}
			if(!columnOrder.contains(header)) {
				columnOrder.add(header);
			}
		}

		setUpTableView(categoryAnalysisResults);
		setUpTableListeners();
		if (categoryAnalysisResults.getColumnHeader().size() == 0)
			return;
		
		setCellFactory();
		
		presenter.showVisualizations(categoryAnalysisResults);
	}

	@Override
	protected Map<String, Map<String, Set<String>>> fillUpDBToPathwayGeneSymbols()
	{

		try
		{
			Object obj = bmdAnalysisDataSet.getObject();
			if (bmdAnalysisDataSet.getObject() instanceof List)
				obj = ((List) bmdAnalysisDataSet.getObject()).get(0);
			return PathwayToGeneSymbolUtility.getInstance().getdbToPathwaytoGeneSet(
					((CategoryAnalysisResults) obj).getBmdResult().getDoseResponseExperiment());
		}
		catch (Exception e)
		{

		}
		return new HashMap<>();

	}
	
	@Override
	protected void setCellFactory() 
	{
		if(columnMap.get("GO/Pathway/Gene Set ID")) {
			// Create a CellFactory for the category id
			categoryCellFactory = new CategoryTableCallBack();
			
			int pathwayColumn = columnOrder.indexOf("GO/Pathway/Gene Set ID");
			
			TableColumn tc = tableView.getColumns().get(pathwayColumn);
			tc.setCellFactory(categoryCellFactory);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void close()
	{
		if (tableView != null && tableView.getColumns().size() > 0)
		{
			int pathwayColumn = 0;
			if (bmdAnalysisDataSet instanceof CombinedDataSet)
				pathwayColumn = 1;
			TableColumn tc = tableView.getColumns().get(pathwayColumn);
			tc.setCellFactory(null);
		}
		super.close();

	}

	@Override
	protected DataVisualizationView getDataVisualizationView()
	{
		return new CategoryAnalysisDataVisualizationView();
	}

	private class CategoryTableMousEvent implements EventHandler<MouseEvent>
	{

		@Override
		public void handle(MouseEvent event)
		{
			if (event.getClickCount() != 1)
			{
				return;
			}
			TableCell c = (TableCell) event.getSource();
			BMDExpressAnalysisRow item = (BMDExpressAnalysisRow) c.getTableRow().getItem();

			if (item == null)
				return;

			try
			{

				if (item.getObject() instanceof GOAnalysisResult)
					java.awt.Desktop.getDesktop()
							.browse(new URI(BMDExpressConstants.getInstance().GO_WEB + bmdAnalysisDataSet
									.getValueForRow(item, CategoryAnalysisResults.CATEGORY_ID).toString()));
				else if (item.getObject() instanceof PathwayAnalysisResult)
					java.awt.Desktop.getDesktop()
							.browse(new URI(BMDExpressConstants.getInstance().PATHWAY_WEB + bmdAnalysisDataSet
									.getValueForRow(item, CategoryAnalysisResults.CATEGORY_ID).toString()));
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (URISyntaxException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	private class CategoryTableCallBack implements Callback<TableColumn, TableCell>
	{

		@Override
		public TableCell call(TableColumn param)
		{
			TableCell cell = new TableCell<BMDExpressAnalysisRow, String>() {

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
			cell.addEventFilter(MouseEvent.MOUSE_CLICKED, new CategoryTableMousEvent());
			return cell;
		}

	}

}
