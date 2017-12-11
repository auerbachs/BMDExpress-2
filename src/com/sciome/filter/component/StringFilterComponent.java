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
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.GridPane;

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

	public StringFilterComponent(String key, Integer row, GridPane grid,
			DataFilterComponentListener dataFilterComponentListener, Class filterFieldClass, DataFilter df,
			Method method)
	{
		super(key, row, grid, dataFilterComponentListener, filterFieldClass, df, method);

	}

	@Override
	protected void initValues(DataFilter df)
	{
	}

	@Override
	protected void init(GridPane grid, String key, Integer row)
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
		checkComboBox.setMaxWidth(100.0);
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

		grid.add(keyLabel, 0, row, 4, 1);
		grid.add(checkComboBox, 0, row + 1, 3, 1);
		grid.add(clearButton, 3, row + 1);
		Separator sep = new Separator(Orientation.HORIZONTAL);
		grid.add(sep, 0, row + 2, 4, 1);

		GridPane.setMargin(sep, new Insets(0, 0, 10, 0));

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
