/*
 * MultiInputPane.java
 *
 * Copyright (c) 2004, 2005 - Russell C. Bjork
 * Modified on Dec 9, 2005 by Longlong Yang
 */

package com.sciome.bmdexpress2.util.categoryanalysis.defined;


import java.util.Vector;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

/**
 * This is a utility class for displaying a dialog that asks for multiple values. Based on ideas in Wu's
 * javabook.MultiInputBox class and on ideas in javax.swing.JOptionPane
 */
public class MultiInputPane extends VBox 
{
	private int				FLAG		= -1;
	private ComboBox<String>[]		comboBoxes;
	private Node[]	fieldsOrBoxes;
	private int[]			indices;
	private Vector<String>	selectedItems;
	private boolean			ok;

	private final int		maxColumn	= 100;



	/**
	 * Constructor used by matching initial values to prompts for example just focus on fewer colums of
	 * prompts than mutiple columns of initials
	 *
	 * @param prompts
	 *            the prompts to display
	 * @param initialValues
	 *            the initial values to display for each item - this parameter can be null, in which case no
	 *            initial values are specified; or individual elements can be null, indicating that no initial
	 *            value is specified for a particular field
	 * @type type is used to distinguish this function from the above
	 */
	public MultiInputPane(String message, String[] prompts, String[] initialValues, int type)
	{
		super();
		FLAG = type;
		

		if (message != null && message.length() > 1)
		{
			Label msgLabel = new Label(message);
			
			this.getChildren().add(msgLabel);
		}

		int length = prompts.length;
		comboBoxes = new ComboBox[length];
		indices = new int[length];
		selectedItems = new Vector<String>();
		GridPane centerPane = new GridPane();

		for (int i = 0; i < length; i++)
		{
			
			
			comboBoxes[i] = new ComboBox<>();
			comboBoxes[i].setItems(FXCollections.observableArrayList(initialValues));
			comboBoxes[i].setId(Integer.toString(i));
			comboBoxes[i].setValue(prompts[i]); 
			
			
			indices[i] = comboBoxes[i].getSelectionModel().getSelectedIndex();
			final int cindex = i;
			comboBoxes[i].valueProperty().addListener((options, oldValue, newValue) -> {
				//getTopLevelAncestor().setVisible(false);
				procesSelection(comboBoxes[cindex]);
			});
			selectedItems.add(initialValues[i]);
			
			Label l = new Label(prompts[i]);
			
			GridPane.setRowIndex(l, i);
			GridPane.setRowIndex(comboBoxes[i], i);
			GridPane.setColumnIndex(l, 0);
			GridPane.setColumnIndex(comboBoxes[i], 1);
			
			centerPane.getChildren().addAll(l, comboBoxes[i]);
			
			
		}

		this.getChildren().add(centerPane);

	}

	


	public void procesSelection(ComboBox<String> box)
	{
		int max = box.getItems().size(); // indices.length;
		int n = Integer.parseInt(box.getId());
		int idx = box.getSelectionModel().getSelectedIndex();
		String value =  box.getSelectionModel().getSelectedItem();

		if (n > 0)
		{
			for (int i = 0; i < n; i++)
			{
				if (indices[i] == idx)
				{
					String msg = "You couldn't select the value twice: " + box.getSelectionModel().getSelectedItem();
					// JOptionPane.showMessageDialog(this, msg, "Repeat Match", JOptionPane.WARNING_MESSAGE);

					box.getSelectionModel().select(indices[n]); 
					return;
				}
			}
		}

		indices[n] = idx;
		selectedItems.setElementAt(value, n);

		/*
		 * Re-assign new item to a box if it's element has been selected by a previous box to automatically
		 * avoid repeats
		 */
		for (int i = n + 1; i < indices.length; i++)
		{
			if (selectedItems.get(i).equals(value))
			{
				for (int j = i + 1; j % max < max; j++)
				{
					j = j % max;
					String value2 =  box.getItems().get(j);

					if (!selectedItems.contains(value2))
					{
						selectedItems.setElementAt(value2, i);
						indices[i] = j;
						comboBoxes[i].getSelectionModel().select(j);
						i = indices.length;
						break;
					}
				}
			}
		}

	}

	public int[] getIndices()
	{
		return indices;
	}

		

	



}
