package com.sciome.filter.component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.AutoCompletionBinding.ISuggestionRequest;
import org.controlsfx.control.textfield.TextFields;

import com.sciome.filter.DataFilter;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

/*
 * define a simple ui thing that gives the user the ability
 * to supply values for a filterable key
 */
public class StringFilterComponent extends FilterComponent
{
	// the filterable
	// value;
	private Set<String>	suggestedValuesForFilter;

	private boolean		isClearing	= false;

	public StringFilterComponent(String key, DataFilterComponentListener dataFilterComponentListener,
			Class filterFieldClass, DataFilter df, Method method, FilterComponentContainer container)
	{
		super(key, dataFilterComponentListener, filterFieldClass, df, method, container);

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

		List<String> list = new ArrayList<>(dataFilterComponentListener.getItemsForKey(key));
		Collections.sort(list);

		VBox vbox = new VBox(8);

		if (list.size() <= 100)
			vbox.getChildren().add(useComboBox(list, key));
		else
			vbox.getChildren().add(userTextFieldWithCompletion(list, key));
		addFilterComponent(vbox);

		vbox.setStyle("-fx-border-color: black;" + "-fx-padding: 0 0 30 0;");
		VBox.setMargin(vbox, new Insets(15, 15, 15, 15));

	}

	private HBox useComboBox(List<String> list, String key)
	{
		// create the data to show in the CheckComboBox
		final ObservableList<String> strings = FXCollections.observableArrayList(list);

		Button clearButton = new Button("Clear");
		CheckComboBox<String> checkComboBox;
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
					keyLabel.setText(key + ": " + checkComboBox.getCheckModel().getCheckedItems().size()
							+ " selected.");
					suggestedValuesForFilter.clear();
					for (String str : checkComboBox.getCheckModel().getCheckedItems())
						suggestedValuesForFilter.add(str.toLowerCase());

					dataFilterComponentListener.dataFilterChanged();
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

		HBox hbox1 = new HBox(8);
		hbox1.getChildren().addAll(checkComboBox, clearButton);
		return hbox1;

	}

	private HBox userTextFieldWithCompletion(List<String> list, String key)
	{
		TextArea selectedStrings = new TextArea();
		selectedStrings.setEditable(true);
		TextField stringAutoCompleteSelector = new TextField();
		// create the data to show in the CheckComboBox
		final ObservableList<String> strings = FXCollections.observableArrayList(list);
		Set<String> possibleValues = new HashSet<>(list);

		Button clearButton = new Button("Clear");
		ComboBox<String> howtodostring;
		// Create the CheckComboBox with the data
		howtodostring = new ComboBox<String>(
				FXCollections.observableArrayList(Arrays.asList("begins with", "contains")));

		howtodostring.setValue("begins with");

		stringAutoCompleteSelector.setMaxWidth(300);
		selectedStrings.setMaxWidth(350);
		selectedStrings.setMaxHeight(75);
		TextFields.bindAutoCompletion(stringAutoCompleteSelector,
				new Callback<AutoCompletionBinding.ISuggestionRequest, Collection<String>>() {

					@Override
					public Collection<String> call(ISuggestionRequest param)
					{
						List<String> returnList = new ArrayList<>();
						for (String p : strings)
							if (howtodostring.getValue().equals("contains")
									&& p.toLowerCase().contains(param.getUserText().toLowerCase()))
								returnList.add(p);
							else if (howtodostring.getValue().equals("begins with")
									&& p.toLowerCase().startsWith(param.getUserText().toLowerCase()))
								returnList.add(p);

						return returnList;
					}
				});

		stringAutoCompleteSelector.textProperty().addListener((observable, oldValue, newValue) ->
		{
			if (newValue == null)
				return;

			if (newValue.trim().equals(""))
				return;
			if (!possibleValues.contains(newValue))
				return;
			selectedStrings.setText(newValue + "\n" + selectedStrings.getText());

		});

		selectedStrings.textProperty().addListener((observable, oldValue, newValue) ->
		{
			suggestedValuesForFilter.clear();
			for (String line : selectedStrings.getText().split("\n"))
				if (!line.trim().equals(""))
					suggestedValuesForFilter.add(line.trim().toLowerCase());

			doDelayedFilterChange(Arrays.asList(selectedStrings));

		});

		clearButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent e)
			{
				isClearing = true;
				suggestedValuesForFilter.clear();
				stringAutoCompleteSelector.clear();
				selectedStrings.clear();
				dataFilterComponentListener.dataFilterChanged();
				keyLabel.setText(key);
				isClearing = false;
			}

		});

		HBox hbox1 = new HBox(8);
		VBox vbox = new VBox();
		HBox hbox2 = new HBox();
		hbox2.getChildren().addAll(howtodostring, stringAutoCompleteSelector, clearButton);
		vbox.getChildren().addAll(hbox2, selectedStrings);
		hbox1.getChildren().addAll(vbox);
		return hbox1;
	}

	@Override
	public boolean isFilledOut()
	{
		if (suggestedValuesForFilter.size() > 0)
			return true;
		return false;
	}

	@Override
	public List<Object> getValues()
	{
		return new ArrayList<>(suggestedValuesForFilter);
	}
}
