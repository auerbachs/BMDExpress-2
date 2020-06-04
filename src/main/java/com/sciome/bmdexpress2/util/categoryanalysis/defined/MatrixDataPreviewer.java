/*
 * MatrixDataPreviewer.java      0.5    10/10/2006
 *
 * Copyright (c) 2006 CIIT Centers for Health Research
 * 6 Davis Drive, P.O. Box 12137, Research Triangle Park, NC USA 27709-2137
 * All rights reserved.
 *
 * This program is created for go term
 * It is used to view matrix data in table format.
 */

package com.sciome.bmdexpress2.util.categoryanalysis.defined;



import com.sciome.bmdexpress2.util.MatrixData;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

/**
 * The class for MatrixDataPreviewer
 *
 * @version 0.5, 06/24/2005
 * @author Longlong Yang
 */
public class MatrixDataPreviewer extends VBox 
{

	private String				name;
	private String[]			columnNames;
	private Object[]			longValues;
	private Object[][]			data;
	private CheckBox			yesBox;
	private Button				setButton;

	private MatrixData			matrixData;
	private ObservableList<Object[]> tableObservableData;

	private boolean				ok;

	/**
	 * Class constructor
	 * 
	 * @param sg
	 *            is a ScreenGui object
	 * @param adb
	 *            is a AccessDbase object
	 */
	public MatrixDataPreviewer(MatrixData matrix)
	{
		super();
		matrixData = matrix;
		name = matrix.getName();
		columnNames = matrix.getColumnNames();
		data = matrix.getData();
		init();
	}

	private void assignLongValues()
	{
		longValues = new Object[columnNames.length];

		for (int i = 0; i < columnNames.length; i++)
		{
			longValues[i] = columnNames[i];
		}
	}

	private void init()
	{
		if (data.length > 0)
		{
			assignLongValues();
			VBox pan = createPane();
			this.getChildren().add(pan);
		}
	}

	/**
	 * Create components for login interface
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private VBox createPane()
	{
	//	tModel = new NumeralTableModel(data, columnNames);

		
		TableView tableView = new TableView<>();
		VBox pan = new VBox();
		int i=0;
		for(String column: this.getMatrix().getColumnNames())
		{
			// Name column
			final int columnIndex = i;
			TableColumn tcID = new TableColumn(column);
			tcID.setEditable(false);
			tcID.setCellValueFactory(new Callback<CellDataFeatures<Object[], String>, ObservableValue<String>>() {
				@Override
				public ObservableValue<String> call(CellDataFeatures<Object[], String> p)
				{
					return new SimpleStringProperty((String) p.getValue()[columnIndex]);
				}
			});
	
			tcID.setMinWidth(90);
			tcID.setPrefWidth(90);
			tableView.getColumns().add(tcID);
			i++;
		}
		
		
		tableObservableData = FXCollections.observableArrayList(this.getMatrix().getData());
		tableView.setItems(tableObservableData);

		pan.getChildren().add(tableView);

		if (matrixData != null && matrixData.hasHeaders())
		{
			yesBox = new CheckBox("Yes");
			yesBox.setOnAction(value->
			{
				if(yesBox.isSelected())
					setButton.setDisable(false);
				else
					setButton.setDisable(true);
			});
			//yesBox.addActionListener(this);
			setButton = new Button("Use");
			setButton.setOnAction(value->
			{
				//redo the table
				yesBox.setSelected(false);	
				setButton.setDisable(true);
				for(int c=0; c< tableView.getColumns().size(); c++)
				{
					TableColumn tcID = (TableColumn)tableView.getColumns().get(c);
					tcID.setText(tableObservableData.get(0)[c].toString());
				}
				tableObservableData.remove(0);
				removeHeaders();
			});
			setButton.setDisable(true);
			//setButton.addActionListener(this);
			HBox setPane = new HBox();
			setPane.getChildren().add(new Label("Does the data contain column headers?"));
			setPane.getChildren().add(yesBox);
			setPane.getChildren().add(setButton);
			pan.getChildren().add(setPane);
			
			setPane.setSpacing(15.0);
			setPane.setAlignment(Pos.CENTER_LEFT);
		}
		
		pan.setSpacing(15.0);

		return pan;
	}

	private void removeHeaders()
	{
		matrixData.removeHeaders();
		//tModel.removeRow(0);
		//tModel.setColumnIdentifiers(matrixData.getColumnNames());
		setButton.setDisable(true);
		yesBox.setSelected(false);
	}

	public MatrixData getMatrix()
	{
		return matrixData;
	}

	/*
	public void actionPerformed(ActionEvent e)
	{
		String cmd = e.getActionCommand();

		if ("OK".equals(cmd))
		{
			ok = true;
			//getTopLevelAncestor().setVisible(false);
		}
		else if ("Cancel".equals(cmd))
		{
			matrixData = null;
			ok = true;
			//getTopLevelAncestor().setVisible(false);
		}
		else if (cmd.equals("Yes"))
		{
			if (yesBox.isSelected())
			{
				//setButton.setEnabled(true);
			}
			else
			{
				//setButton.setEnabled(false);
			}
		}
		else if (cmd.equals("Use"))
		{
			removeHeaders();
		}
		else
		{
			//JOptionPane.showMessageDialog(this, "Ask your program provider for help.");
		}
	}
	*/

}
