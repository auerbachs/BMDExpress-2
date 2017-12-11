package com.sciome.filter.component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sciome.bmdexpress2.shared.BMDExpressProperties;
import com.sciome.filter.DataFilter;
import com.sciome.filter.DataFilterPack;
import com.sciome.filter.DataFilterType;
import com.sciome.filter.GenericFilterAnnotationExtractor;
import com.sciome.filter.IntegerFilter;
import com.sciome.filter.NumberFilter;
import com.sciome.filter.StringFilter;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

/*
 * javafx node that displays a bunch of filter fields. 
 */
public class FilterCompentsNode extends VBox
{
	private List<FilterComponent>				filterComponents;
	private GenericFilterAnnotationExtractor	filterAnnotationExtractor;
	private Class								filterableClass;
	private DataFilterComponentListener			dataFilterComponentListener;
	private Button								addRemoveFilterButton;

	private List<String>						visibleFilterNodes	= new ArrayList<>();

	// use this map to easily assoicate existing data filter with a key
	private Map<String, DataFilter>				dataFilterMap		= new HashMap<>();
	private ScrollPane							filterNodeScrollPane;

	//
	public FilterCompentsNode(Class filterableClass, DataFilterComponentListener dataFilterComponentListener)
	{
		super();

		this.filterableClass = filterableClass;
		this.dataFilterComponentListener = dataFilterComponentListener;

		init();

	}

	public FilterCompentsNode(Class filterableClass, DataFilterComponentListener dataFilterComponentListener,
			DataFilterPack dPack)
	{
		super();

		this.filterableClass = filterableClass;
		this.dataFilterComponentListener = dataFilterComponentListener;

		if (dPack != null)
		{
			for (DataFilter df : dPack.getDataFilters())
				dataFilterMap.put(df.getKey(), df);
		}

		init();

	}

	@SuppressWarnings("restriction")
	private void init()
	{

		visibleFilterNodes = BMDExpressProperties.getInstance().getFilters(filterableClass.getName());

		filterAnnotationExtractor = new GenericFilterAnnotationExtractor(filterableClass);
		List<String> sortedKeyList = filterAnnotationExtractor.getKeys();
		Collections.sort(sortedKeyList);
		addRemoveFilterButton = new Button("Add/Remove Filters");

		addRemoveFilterButton.setOnAction(new EventHandler<ActionEvent>() {
			@SuppressWarnings("restriction")
			@Override
			public void handle(ActionEvent e)
			{
				customChoiceDialogForDataSet(sortedKeyList, visibleFilterNodes, "Add/Remove Filters");

				// update properties
				BMDExpressProperties.getInstance().updateFilter(filterableClass.getName(),
						visibleFilterNodes);

				// update view
				updateFilterNodes();
			}
		});
		this.getChildren().add(addRemoveFilterButton);
		filterNodeScrollPane = new ScrollPane();
		updateFilterNodes();
		this.getChildren().add(filterNodeScrollPane);

	}

	private void updateFilterNodes()
	{
		GridPane grid = new GridPane();

		grid.setHgap(2);
		grid.setVgap(2);
		grid.setPadding(new Insets(10, 10, 10, 10));
		Integer row = 0;
		filterComponents = new ArrayList<>();

		List<String> sortedKeyList = new ArrayList<>(visibleFilterNodes);
		Collections.sort(sortedKeyList);
		for (String key : sortedKeyList)
		{

			// create filtercomponent and pass it a grid so it puts itself in it.
			// also give it the listener so it can attach it's own components to it
			// and send messages back home so the calling object can do what it does,
			// like get an updated datafilterpack so it can update the ui.
			FilterComponent fc = null;

			// if there is already a datafilter associated with this object,
			// use it.
			DataFilter df = dataFilterMap.get(key);
			if (filterAnnotationExtractor.getReturnType(key) != null)
				fc = FilterComponentFactory.createFilterComponent(key, row, grid, dataFilterComponentListener,
						filterAnnotationExtractor.getReturnType(key), df,
						filterAnnotationExtractor.getMethod(key));

			// if this key only produces null values due to compatibility issues,
			// it's not "useable" hence the isuseable flag.
			if (fc != null && fc.getIsUseable())
			{
				filterComponents.add(fc);

				row++;
				row++;
				row++;
				row++;
			}

		}
		grid.add(new Label(" "), 0, row++, 4, 1);
		filterNodeScrollPane.setContent(grid);
	}

	/*
	 * get the data filter pack for the current values in entered into the filtercomponents.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public DataFilterPack getFilterDataPack()
	{
		List<DataFilter> dataFilters = new ArrayList<>();
		for (FilterComponent fc : filterComponents)
		{
			if (!fc.isFilledOut())
				continue;
			DataFilter df = null;
			Class returnType = filterAnnotationExtractor.getReturnType(fc.getFilterKey());
			if (returnType.equals(String.class))
			{
				df = new StringFilter<>(DataFilterType.EQUALS, filterableClass, fc.getFilterKey(),
						fc.getValues());
			}
			else if (returnType.equals(Integer.class))
			{
				df = new IntegerFilter<>(DataFilterType.BETWEEN, filterableClass, fc.getFilterKey(),
						fc.getValues());
			}
			else
			{
				try
				{
					df = new NumberFilter<>(DataFilterType.EQUALS, filterableClass, fc.getFilterKey(),
							fc.getValues());
				}
				catch (Exception e)
				{
					continue;
				}

			}
			dataFilters.add(df);
		}

		DataFilterPack dFP = new DataFilterPack("Data Filter Pack", dataFilters);

		return dFP;
	}

	@SuppressWarnings("restriction")
	private void customChoiceDialogForDataSet(List<String> dataSet, List<String> visible, String title)
	{
		Dialog<List<String>> dialog = new Dialog<>();
		dialog.setTitle(title);

		Set<String> visibleSet = new HashSet<>(visible);
		List<CheckBox> checkBoxes = new ArrayList<>();
		// Set the button types.
		ButtonType doneButtonType = new ButtonType("Done", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(doneButtonType, ButtonType.CANCEL);
		dialog.setResizable(true);

		ScrollPane sp = new ScrollPane();
		sp.setMaxHeight(500.0);
		sp.setPrefHeight(500.0);

		VBox vb = new VBox();

		sp.setContent(vb);

		int i = 0;
		for (String obj : dataSet)
		{

			CheckBox cb = new CheckBox(obj.toString());
			vb.getChildren().add(cb);
			checkBoxes.add(cb);
			if (visibleSet.contains(obj))
				cb.setSelected(true);
		}

		dialog.getDialogPane().setContent(sp);
		dialog.getDialogPane().setMinWidth(500.0);
		// Convert the result to a username-password-pair when the login button is clicked.
		dialog.setResultConverter(dialogButton ->
		{
			if (dialogButton == doneButtonType)
			{
				visible.clear();
				for (CheckBox cb : checkBoxes)
				{
					if (cb.isSelected())
						visible.add(cb.getText());
				}
			}
			return null;
		});

		dialog.showAndWait();

	}

}
