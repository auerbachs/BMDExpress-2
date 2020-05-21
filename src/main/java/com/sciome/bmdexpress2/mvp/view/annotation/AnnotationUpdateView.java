package com.sciome.bmdexpress2.mvp.view.annotation;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisRow;
import com.sciome.bmdexpress2.mvp.model.chip.ChipInfo;
import com.sciome.bmdexpress2.mvp.presenter.annotation.AnnotationUpdatePresenter;
import com.sciome.bmdexpress2.mvp.view.BMDExpressViewBase;
import com.sciome.bmdexpress2.mvp.viewinterface.annotation.IAnnotationUpdateView;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Callback;

public class AnnotationUpdateView extends BMDExpressViewBase implements IAnnotationUpdateView, Initializable
{

	@FXML
	private TableView annotationUpdateTableView;

	@FXML
	private ProgressBar progressBar;
	@FXML
	private Label progressLabel;

	@FXML
	private Button updateButton;
	@FXML
	private Button doneButton;

	private List<Object[]> tableData;
	private ObservableList<Object[]> tableObservableData;

	AnnotationUpdatePresenter presenter;

	public AnnotationUpdateView()
	{
		this(BMDExpressEventBus.getInstance());
	}

	/*
	 * Event bus is passed as an argument so the unit tests can pass their own custom eventbus
	 */
	public AnnotationUpdateView(BMDExpressEventBus eventBus)
	{
		super();
		presenter = new AnnotationUpdatePresenter(this, eventBus);

	}

	@Override
	public void handle_updateButton()
	{
		this.updateButton.setDisable(true);
		presenter.processUpdate(tableData);
	}

	@Override
	public void handle_doneButton()
	{
		closeWindow();

	}

	@Override
	public void beginUpdateProgress()
	{
		this.progressBar.setVisible(true);
		this.progressBar.setProgress(0.0);
		this.doneButton.setDisable(true);
		this.progressLabel.setText("Starting");
	}

	@Override
	public void updateProgress(String labelText, double value)
	{
		this.progressBar.setProgress(value);
		this.progressLabel.setText(labelText);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void updateTableView(List<Object[]> tableData)
	{
		this.progressBar.setProgress(0.0);
		this.progressBar.setVisible(false);
		this.progressLabel.setText("Select data to update.");
		this.annotationUpdateTableView.setVisible(true);
		this.updateButton.setDisable(false);
		this.doneButton.setDisable(false);
		annotationUpdateTableView.getItems().clear();
		annotationUpdateTableView.getColumns().clear();
		annotationUpdateTableView.setEditable(true);

		// change the first column to an observable value
		for (Object[] objects : tableData)
		{
			objects[0] = new SimpleBooleanProperty((Boolean) objects[0]);
		}

		// Select column
		TableColumn tc = new TableColumn("Select");
		tc.setEditable(true);
		tc.setCellValueFactory(new Callback<CellDataFeatures<Object[], Boolean>, ObservableValue<Boolean>>() {

			private SimpleBooleanProperty booleanProperty;

			@Override
			public ObservableValue<Boolean> call(CellDataFeatures<Object[], Boolean> p)
			{

				return (SimpleBooleanProperty) p.getValue()[0];
			}
		});

		tc.setCellFactory(new Callback<TableColumn<Object[], Boolean>, TableCell<Object[], Boolean>>() {
			@Override
			public TableCell<Object[], Boolean> call(TableColumn<Object[], Boolean> tableColumn)
			{
				return new CheckBoxTableCell<>();
			}
		});

		tc.setMinWidth(90);
		tc.setPrefWidth(90);
		// tc.setCellFactory(CheckBoxTableCell.forTableColumn(tc));

		annotationUpdateTableView.getColumns().add(tc);

		// Name column
		TableColumn tcID = new TableColumn("ID");
		tcID.setEditable(false);
		tcID.setCellValueFactory(new Callback<CellDataFeatures<Object[], String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<Object[], String> p)
			{
				return new SimpleStringProperty((String) p.getValue()[1]);
			}
		});

		tcID.setCellFactory(new AnnotationTableCallBack());
		tcID.setMinWidth(90);
		tcID.setPrefWidth(90);
		annotationUpdateTableView.getColumns().add(tcID);

		// Name column
		TableColumn tcName = new TableColumn("Name");
		tcName.setEditable(false);
		tcName.setCellValueFactory(
				new Callback<CellDataFeatures<Object[], String>, ObservableValue<String>>() {
					@Override
					public ObservableValue<String> call(CellDataFeatures<Object[], String> p)
					{
						return new SimpleStringProperty((String) p.getValue()[2]);
					}
				});
		tcName.setMinWidth(90);
		tcName.setPrefWidth(90);
		annotationUpdateTableView.getColumns().add(tcName);

		// Provider column
		TableColumn tcProvider = new TableColumn("Provider");
		tcProvider.setEditable(false);
		tcProvider.setCellValueFactory(
				new Callback<CellDataFeatures<Object[], String>, ObservableValue<String>>() {
					@Override
					public ObservableValue<String> call(CellDataFeatures<Object[], String> p)
					{
						return new SimpleStringProperty((String) p.getValue()[3]);
					}
				});
		tcProvider.setMinWidth(90);
		tcProvider.setPrefWidth(90);
		annotationUpdateTableView.getColumns().add(tcProvider);

		// Provider column
		TableColumn speciesColumn = new TableColumn("Species");
		speciesColumn.setEditable(false);
		speciesColumn.setCellValueFactory(
				new Callback<CellDataFeatures<Object[], String>, ObservableValue<String>>() {
					@Override
					public ObservableValue<String> call(CellDataFeatures<Object[], String> p)
					{
						return new SimpleStringProperty((String) p.getValue()[4]);
					}
				});
		speciesColumn.setMinWidth(90);
		speciesColumn.setPrefWidth(90);
		annotationUpdateTableView.getColumns().add(speciesColumn);

		// Size column
		TableColumn tcSize = new TableColumn("Size");
		tcSize.setEditable(false);
		tcSize.setCellValueFactory(
				new Callback<CellDataFeatures<Object[], String>, ObservableValue<String>>() {
					@Override
					public ObservableValue<String> call(CellDataFeatures<Object[], String> p)
					{
						return new SimpleStringProperty((String) p.getValue()[5]);
					}
				});
		tcSize.setMinWidth(90);
		tcSize.setPrefWidth(90);
		annotationUpdateTableView.getColumns().add(tcSize);

		// LastUpdated column
		TableColumn tcLastUpdated = new TableColumn("Last Updated Date");
		tcLastUpdated.setEditable(false);
		tcLastUpdated.setCellValueFactory(
				new Callback<CellDataFeatures<Object[], String>, ObservableValue<String>>() {
					@Override
					public ObservableValue<String> call(CellDataFeatures<Object[], String> p)
					{
						return new SimpleStringProperty((String) p.getValue()[6]);
					}
				});
		tcLastUpdated.setMinWidth(120);
		tcLastUpdated.setPrefWidth(120);
		annotationUpdateTableView.getColumns().add(tcLastUpdated);

		// LocalUpdated column
		TableColumn tcLocalUpdate = new TableColumn("Local Updated Date");
		tcLocalUpdate.setEditable(false);
		tcLocalUpdate.setCellValueFactory(
				new Callback<CellDataFeatures<Object[], String>, ObservableValue<String>>() {
					@Override
					public ObservableValue<String> call(CellDataFeatures<Object[], String> p)
					{
						return new SimpleStringProperty((String) p.getValue()[7]);
					}
				});
		tcLocalUpdate.setMinWidth(120);
		tcLocalUpdate.setPrefWidth(120);
		annotationUpdateTableView.getColumns().add(tcLocalUpdate);

		tableObservableData = FXCollections.observableArrayList(tableData);
		annotationUpdateTableView.setItems(tableObservableData);

		this.tableData = tableData;

	}

	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		this.updateButton.setDisable(true);
		this.doneButton.setDisable(true);
		this.progressBar.setVisible(true);
		this.progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
		this.progressLabel.setText("Gathering information.");
		presenter.initTableView();

	}

	@Override
	public void finishUpdate()
	{
		this.progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
		this.progressLabel.setText("Finishing");
		presenter.initTableView();

	}

	@Override
	public void closeWindow()
	{
		Stage stage = (Stage) doneButton.getScene().getWindow();
		this.close();
		stage.close();

	}

	@Override
	public void close()
	{
		if (presenter != null)
		{
			presenter.close();
		}

	}

	final class AnnotationTableMouseEvent implements EventHandler<MouseEvent>
	{

		@Override
		public void handle(MouseEvent event)
		{
			if (event.getClickCount() != 1)
			{
				return;
			}
			TableCell c = (TableCell) event.getSource();
			String id = (String) ((Object[]) c.getTableRow().getItem())[1];
			String provider = (String) ((Object[]) c.getTableRow().getItem())[3];

			if (id == null)
				return;

			try
			{
				String url = ChipInfo.getURL(provider, id);
				if (!url.equals(""))
					java.awt.Desktop.getDesktop().browse(new URI(url));
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

		}

	}

	final class AnnotationTableCallBack implements Callback<TableColumn, TableCell>
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
			cell.addEventFilter(MouseEvent.MOUSE_CLICKED, new AnnotationTableMouseEvent());
			return cell;
		}
	}

}
