package com.sciome.bmdexpress2.mvp.view.mainstage.dataview;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisDataSet;
import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisRow;
import com.sciome.bmdexpress2.mvp.model.CombinedDataSet;
import com.sciome.bmdexpress2.mvp.presenter.mainstage.dataview.BMDExpressDataViewPresenter;
import com.sciome.bmdexpress2.mvp.view.visualization.DataVisualizationView;
import com.sciome.bmdexpress2.mvp.viewinterface.mainstage.dataview.IBMDExpressDataView;
import com.sciome.bmdexpress2.shared.BMDExpressProperties;
import com.sciome.bmdexpress2.shared.TableViewCache;
import com.sciome.filter.DataFilter;
import com.sciome.filter.DataFilterPack;
import com.sciome.filter.component.DataFilterComponentListener;
import com.sciome.filter.component.FilterComponentsNode;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

/*
 * base class for viewing bmdexpressdata
 * data view represents a filter panel, table view and visualization view
 */
public abstract class BMDExpressDataView<T> extends VBox
		implements DataFilterComponentListener, IBMDExpressDataView
{
	protected TableView<BMDExpressAnalysisRow>					tableView		= null;
	protected HBox												topHBox;
	protected Label												totalItemsLabel;
	protected CheckBox											enableFilterCheckBox;
	protected SplitPane											splitPaneMain;
	protected SplitPane											splitPane;
	protected FilterComponentsNode								filtrationNode;
	protected Button											hideFilter;
	protected Button											hideTable;
	protected Button											hideCharts;
	protected Node												dataVisualizationNode;

	private final String										HIDE_TABLE		= "Hide Table";
	private final String										SHOW_TABLE		= "Show Table";
	private final String										HIDE_FILTER		= "Hide Filter";
	private final String										SHOW_FILTER		= "Show Filter";
	private final String										HIDE_CHART		= "Hide Charts";
	private final String										SHOW_CHART		= "Show Charts";
	private final String										APPPLY_FILTER	= "Apply Filter";

	private FilteredList<BMDExpressAnalysisRow>					filteredData;
	private BMDExpressAnalysisDataSet							analysisDataSet;
	protected DataVisualizationView								dataVisualizationController;
	protected BMDExpressDataViewPresenter<IBMDExpressDataView>	presenter;
	private Class<?>											filterableClass;
	protected BMDExpressAnalysisDataSet							bmdAnalysisDataSet;
	private DataFilterPack										defaultDPack	= null;
	protected ObservableList<BMDExpressAnalysisRow>				rawTableData	= null;

	@SuppressWarnings("unchecked")
	public BMDExpressDataView(Class<?> filterableClass, BMDExpressAnalysisDataSet bmdAnalysisDataSet,
			String viewTypeKey)
	{
		super();
		try
		{
			this.bmdAnalysisDataSet = bmdAnalysisDataSet;
			splitPane = new SplitPane();
			this.filterableClass = filterableClass;
			splitPane.setOrientation(Orientation.HORIZONTAL);
			splitPaneMain = new SplitPane();
			splitPaneMain.setOrientation(Orientation.VERTICAL);
			topHBox = new HBox();

			this.setStyle("-fx-background-color: white;");
			topHBox.setAlignment(Pos.CENTER_LEFT);

			// tableView = new TableView<>();
			if (viewTypeKey.equals("main") && !(bmdAnalysisDataSet instanceof CombinedDataSet))
				tableView = TableViewCache.getInstance()
						.getTableView(viewTypeKey + bmdAnalysisDataSet.getName());
			else
				tableView = new TableView<>();

			VBox.setMargin(topHBox, new Insets(5.0));

			SplitPane.setResizableWithParent(splitPaneMain, true);

			splitPane.getItems().add(splitPaneMain);
			hideFilter = new Button(SHOW_FILTER);

			defaultDPack = BMDExpressProperties.getInstance().getDataFilterPackMap(filterableClass.getName());
			// initialize the data filters in this data filter pack. If they were deserialized from disk
			// then they need to get a data set attached to them.
			if (defaultDPack != null && defaultDPack.getDataFilters() != null)
				for (DataFilter df : defaultDPack.getDataFilters())
				{
					df.setBmdanalysisDataSet(bmdAnalysisDataSet);
					df.init();
				}

			filtrationNode = new FilterComponentsNode(bmdAnalysisDataSet, filterableClass, this, defaultDPack);
			filtrationNode.init();

			if (!BMDExpressProperties.getInstance().isHideFilter())
			{
				hideFilter.setText(HIDE_FILTER);
				splitPane.getItems().add(filtrationNode);
			}

			hideTable = new Button(SHOW_TABLE);
			if (!BMDExpressProperties.getInstance().isHideTable())
			{
				hideTable.setText(HIDE_TABLE);
				splitPaneMain.getItems().add(tableView);
			}
			hideCharts = new Button(SHOW_CHART);
			if (!BMDExpressProperties.getInstance().isHideCharts())
			{
				hideCharts.setText(HIDE_CHART);
			}

			totalItemsLabel = new Label("");
			enableFilterCheckBox = new CheckBox(APPPLY_FILTER);
			enableFilterCheckBox.setSelected(BMDExpressProperties.getInstance().isApplyFilter());

			topHBox.getChildren().add(totalItemsLabel);
			topHBox.getChildren().add(enableFilterCheckBox);
			topHBox.getChildren().add(hideFilter);
			topHBox.getChildren().add(hideTable);
			topHBox.getChildren().add(hideCharts);
			topHBox.setSpacing(20.0);

			this.getChildren().add(topHBox);
			this.getChildren().add(splitPane);
			VBox.setVgrow(topHBox, Priority.NEVER);
			VBox.setVgrow(splitPane, Priority.ALWAYS);
			SplitPane.setResizableWithParent(filtrationNode, true);
			splitPane.setDividerPosition(0, .7);

			hideFilter.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent e)
				{
					if (hideFilter.getText().equals(HIDE_FILTER))
					{
						splitPane.getItems().remove(filtrationNode);
						hideFilter.setText(SHOW_FILTER);
						BMDExpressProperties.getInstance().setHideFilter(true);
					}
					else
					{
						splitPane.getItems().add(filtrationNode);
						splitPane.setDividerPosition(0, .7);
						hideFilter.setText(HIDE_FILTER);
						BMDExpressProperties.getInstance().setHideFilter(false);
					}
				}
			});

			hideTable.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent e)
				{
					if (hideTable.getText().equals(HIDE_TABLE))
					{
						splitPaneMain.getItems().remove(tableView);
						hideTable.setText(SHOW_TABLE);
						BMDExpressProperties.getInstance().setHideTable(true);
					}
					else
					{
						splitPaneMain.getItems().add(tableView);
						hideTable.setText(HIDE_TABLE);
						BMDExpressProperties.getInstance().setHideTable(false);
					}
				}
			});

			hideCharts.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent e)
				{
					if (hideCharts.getText().equals(HIDE_CHART))
					{
						splitPaneMain.getItems().remove(dataVisualizationNode);
						hideCharts.setText(SHOW_CHART);
						BMDExpressProperties.getInstance().setHideCharts(true);
					}
					else
					{
						splitPaneMain.getItems().add(0, dataVisualizationNode);
						SplitPane.setResizableWithParent(dataVisualizationNode, true);
						hideCharts.setText(HIDE_CHART);
						BMDExpressProperties.getInstance().setHideCharts(false);
					}
				}
			});

			defaultDPack = filtrationNode.getFilterDataPack();
			showDataVisualization(defaultDPack);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	/*
	 * This method is called by the child class if the child class wants to set up the table in the default
	 * matter.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void setUpTableView(BMDExpressAnalysisDataSet analysisDataSet)
	{
		this.bmdAnalysisDataSet = analysisDataSet;
		List<String> columnHeaders = analysisDataSet.getColumnHeader();
		List<Object> columnHeaders2 = analysisDataSet.getColumnHeader2();
		// create the table view.
		this.analysisDataSet = analysisDataSet;
		// tableView.getColumns().clear();
		if (tableView.getColumns().size() != columnHeaders.size())
		{

			tableView.getColumns().clear();

			for (int i = 0; i < columnHeaders.size(); i++)
			{

				TableColumn tc = null;
				TableColumn tcSub = null;
				final int colNo = i;

				tc = new TableColumn(columnHeaders.get(i));

				if (columnHeaders2 != null)
				{
					tcSub = new TableColumn(columnHeaders2.get(i).toString());
					tc.getColumns().addAll(tcSub);
					tcSub.setMinWidth(90);
					tcSub.setPrefWidth(90);
					tableView.getColumns().add(tc);
					tcSub.setCellValueFactory(new TableCellCallBack(colNo));
				}
				else
				{
					tc.setMinWidth(90);
					tc.setPrefWidth(90);
					tableView.getColumns().add(tc);
					tc.setCellValueFactory(new TableCellCallBack(colNo));
				}

			}
		}

		tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		totalItemsLabel.setText("Total Items: " + analysisDataSet.getAnalysisRows().size());

		// put the displayable data into a sortable and filterable list.
		rawTableData = FXCollections.observableArrayList(analysisDataSet.getAnalysisRows());
		filteredData = new FilteredList<>(rawTableData, p -> true);
		SortedList<BMDExpressAnalysisRow> sortedData = new SortedList<>(filteredData);
		sortedData.comparatorProperty().bind(tableView.comparatorProperty());
		tableView.setItems(sortedData);

		enableFilterCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val)
			{
				dataFilterChanged();
				BMDExpressProperties.getInstance().setApplyFilter(enableFilterCheckBox.isSelected());
			}
		});

		if (defaultDPack != null && enableFilterCheckBox.isSelected())
			filterTable(defaultDPack);

		if (enableFilterCheckBox.isSelected())
		{
			totalItemsLabel.setText(
					"Total Items: " + filteredData.size() + "/" + analysisDataSet.getAnalysisRows().size());
		}
		else
		{
			totalItemsLabel.setText("Total Items: " + analysisDataSet.getAnalysisRows().size());
		}
	}

	@Override
	public void dataFilterChanged()
	{

		DataFilterPack dataFilterPack = filtrationNode.getFilterDataPack();

		filterTable(filtrationNode.getFilterDataPack());

		BMDExpressProperties.getInstance().putDataFilterPackMap(filterableClass.getName(), dataFilterPack);

		redrawVisualizations();
	}

	/*
	 * draw the visualations. This is called in response to something happening on the screen. If the user
	 * selects items in the table and only wants to visualize selected items, this is called...for instance.
	 */
	private void redrawVisualizations()
	{
		DataFilterPack dataFilterPack = filtrationNode.getFilterDataPack();

		List<String> localSelectedIds = null;
		// check to see whether or not to only draw the selected items in the chart visualizations.

		if (enableFilterCheckBox.isSelected())
		{
			totalItemsLabel.setText(
					"Total Items: " + filteredData.size() + "/" + analysisDataSet.getAnalysisRows().size());
			dataVisualizationController.redrawCharts(dataFilterPack, localSelectedIds);
		}
		else
		{
			totalItemsLabel.setText("Total Items: " + analysisDataSet.getAnalysisRows().size());
			dataVisualizationController.redrawCharts(null, localSelectedIds);
		}
	}

	private void filterTable(DataFilterPack pack)
	{

		filteredData.setPredicate(record ->
		{
			try
			{
				if (!enableFilterCheckBox.isSelected())
					return true;
				// If filter text is empty, display all persons.
				if (pack == null || pack.getDataFilters() == null || pack.getDataFilters().isEmpty())
				{
					return true;
				}

				return pack.passesFilter(record);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			return true;
		});

	}

	protected abstract DataVisualizationView getDataVisualizationView();

	private void showDataVisualization(DataFilterPack dPack)
	{
		if (dataVisualizationController != null)
			dataVisualizationController.close();
		try
		{
			dataVisualizationController = getDataVisualizationView();
			if (dataVisualizationController != null)
			{
				dataVisualizationNode = dataVisualizationController.getNode();

				if (enableFilterCheckBox.isSelected())
					dataVisualizationController.initData(dPack);
				splitPaneMain.getItems().remove(dataVisualizationNode);

				if (!BMDExpressProperties.getInstance().isHideCharts())
				{
					splitPaneMain.getItems().add(0, dataVisualizationNode);
					SplitPane.setResizableWithParent(dataVisualizationNode, true);
				}
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	@Override
	public void close()
	{
		try
		{
			if (rawTableData != null)
				rawTableData.clear();
			splitPaneMain.getItems().remove(tableView);
			// for (TableColumn tc : tableView.getColumns())
			// tc.setCellValueFactory(null);
			splitPane.getItems().remove(splitPaneMain);
			splitPane.getItems().remove(filtrationNode);

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			tableView = null;
		}

		if (dataVisualizationController != null)
			dataVisualizationController.close();

		if (presenter != null)
			presenter.destroy();

	}

	@Override
	public Set<String> getItemsForKey(String method)
	{
		bmdAnalysisDataSet.getColumnHeader();
		Set<String> items = new HashSet<>();
		for (BMDExpressAnalysisRow row : bmdAnalysisDataSet.getAnalysisRows())
		{
			try
			{
				Object value = bmdAnalysisDataSet.getValueForRow(row, method);
				if (value != null && !value.toString().trim().equals(""))
					items.add(value.toString().trim());
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		return items;
	}

	@Override
	public void saveDataFilter(String key, DataFilterPack filterPack)
	{
		BMDExpressProperties.getInstance().putDataFilterPackMap(key, filterPack);
		BMDExpressProperties.getInstance().saveDefaultFilter(key);
	}

	@Override
	public List getRangeForKey(String method)
	{
		List<Object> returnList = new ArrayList<>();

		bmdAnalysisDataSet.getColumnHeader();
		Set<String> items = new HashSet<>();
		Object min = null;
		Object max = null;
		for (BMDExpressAnalysisRow row : bmdAnalysisDataSet.getAnalysisRows())
		{
			try
			{
				Object value = bmdAnalysisDataSet.getValueForRow(row, method);

				// don't mess with weird double values.
				if (value instanceof Double
						&& (((Double) value) == null || ((Double) value).equals(Double.NaN)
								|| ((Double) value).equals(Double.NEGATIVE_INFINITY)
								|| ((Double) value).equals(Double.POSITIVE_INFINITY)))
					continue;
				if (value != null && value.toString().equals(""))
					continue;
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

	protected int compareToNumericValues(Object o1, Object o2)
	{
		if (o1 instanceof Integer && o2 instanceof Integer)
			return ((Integer) o1).compareTo(((Integer) o2));
		else if (o1 instanceof Number && o2 instanceof Number)
			return Double.valueOf(((Number) o1).doubleValue())
					.compareTo(Double.valueOf(((Number) o2).doubleValue()));
		else
			return o1.toString().compareTo(o2.toString());

	}
}

final class TableCellCallBack
		implements Callback<CellDataFeatures<BMDExpressAnalysisRow, Object>, ObservableValue<Object>>
{

	private int colNo = 0;

	public TableCellCallBack(int col)
	{
		colNo = col;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public ObservableValue<Object> call(CellDataFeatures<BMDExpressAnalysisRow, Object> p)
	{
		try
		{
			return new SimpleObjectProperty(p.getValue().getRow().get(colNo));
		}
		catch (Exception exception)
		{
			exception.printStackTrace();
		}

		return new SimpleObjectProperty("null");
	}

}
