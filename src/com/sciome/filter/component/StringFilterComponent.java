package com.sciome.filter.component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.controlsfx.control.CheckComboBox;

import com.sciome.filter.DataFilter;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/*
 * define a simple ui thing that gives the user the ability
 * to supply values for a filterable key
 */
public class StringFilterComponent extends FilterComponent
{
	// the filterable
	// value;
	private Set<String>				suggestedValuesForFilter;
	private CheckComboBox<String>	checkComboBox;
	private boolean					isClearing	= false;

	public StringFilterComponent(String key, DataFilterComponentListener dataFilterComponentListener,
			Class filterFieldClass, DataFilter df, Method method)
	{
		super(key, dataFilterComponentListener, filterFieldClass, df, method);

	}

	@Override
	protected void initValues(DataFilter df)
	{
	}

	@Override
	protected void init(String key)
	{
		if (suggestedValuesForFilter == null)
			suggestedValuesForFilter = new HashSet<>();

		List<String> list = new ArrayList<>(dataFilterComponentListener.getItemsForMethod(method));
		Collections.sort(list);
		// create the data to show in the CheckComboBox
		final ObservableList<String> strings = FXCollections.observableArrayList(list);
		Label keyLabel = new Label(key);
		Button clearButton = new Button("Clear");

		// Create the CheckComboBox with the data
		checkComboBox = new CheckComboBox<String>(strings);
		checkComboBox.setMinWidth(100.0);
		checkComboBox.setMaxWidth(300.0);
		checkComboBox.getCheckModel().clearChecks();

		for (String s : strings)
			if (suggestedValuesForFilter.contains(s.toLowerCase()))
				checkComboBox.getCheckModel().check(s);

		if (checkComboBox.getCheckModel().getCheckedItems().size() != suggestedValuesForFilter.size())
		{
			suggestedValuesForFilter.clear();
			suggestedValuesForFilter.addAll(checkComboBox.getCheckModel().getCheckedItems());
		}

		if (checkComboBox.getCheckModel().getCheckedItems().size() > 0)
			keyLabel.setText(
					key + ": " + +checkComboBox.getCheckModel().getCheckedItems().size() + " selected.");
		// and listen to the relevant events (e.g. when the selected indices or
		// selected items change).
		checkComboBox.getCheckModel().getCheckedItems().addListener(new ListChangeListener<String>() {
			public void onChanged(ListChangeListener.Change<? extends String> c)
			{
				if (!isClearing)
				{
					for (String str : checkComboBox.getCheckModel().getCheckedItems())
						suggestedValuesForFilter.add(str.toLowerCase());
					dataFilterComponentListener.dataFilterChanged();
					keyLabel.setText(key + ": " + checkComboBox.getCheckModel().getCheckedItems().size()
							+ " selected.");
				}
			}
		});

		clearButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent e)
			{
				isClearing = true;

				checkComboBox.getCheckModel().clearChecks();
				suggestedValuesForFilter.clear();
				dataFilterComponentListener.dataFilterChanged();
				keyLabel.setText(key);
				isClearing = false;
			}

		});

		VBox vbox = new VBox(8);
		HBox hbox1 = new HBox(8);

		vbox.getChildren().add(keyLabel);
		hbox1.getChildren().addAll(checkComboBox, clearButton);

		vbox.getChildren().add(hbox1);
		this.getChildren().addAll(vbox);

		vbox.setStyle("-fx-border-color: black;" + "-fx-padding: 0 0 30 0;");
		VBox.setMargin(vbox, new Insets(15, 15, 15, 15));

	}

	@Override
	public boolean isFilledOut()
	{
		if (checkComboBox.getCheckModel().getCheckedItems().size() > 0)
			return true;
		return false;
	}

	@Override
	public List<Object> getValues()
	{
		return new ArrayList<>(suggestedValuesForFilter);
	}
}
