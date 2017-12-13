package com.sciome.filter.component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sciome.bmdexpress2.shared.BMDExpressProperties;
import com.sciome.filter.DataFilter;
import com.sciome.filter.DataFilterPack;
import com.sciome.filter.DataFilterType;
import com.sciome.filter.GenericFilterAnnotationExtractor;
import com.sciome.filter.IntegerFilter;
import com.sciome.filter.NumberFilter;
import com.sciome.filter.StringFilter;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

/*
 * javafx node that displays a bunch of filter fields. 
 */
public class FilterCompentsNode extends VBox implements FilterComponentContainer
{
	private static final String					ADD_FILTER			= "--ADD FILTER--";
	private List<FilterComponent>				filterComponents;
	private GenericFilterAnnotationExtractor	filterAnnotationExtractor;
	private Class								filterableClass;
	private DataFilterComponentListener			dataFilterComponentListener;
	private ComboBox<String>					addRemoveFilterCombo;

	private List<String>						visibleFilterNodes	= new ArrayList<>();

	// use this map to easily assoicate existing data filter with a key
	private Map<String, DataFilter>				dataFilterMap		= new HashMap<>();
	private ScrollPane							filterNodeScrollPane;
	private VBox								filterComponentVbox;

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
		// addRemoveFilterButton = new Button("Add/Remove Filters");
		addRemoveFilterCombo = new ComboBox<>();

		addRemoveFilterCombo.getItems().addAll(sortedKeyList);
		addRemoveFilterCombo.getItems().add(0, ADD_FILTER);
		addRemoveFilterCombo.setValue(ADD_FILTER);
		for (String node : visibleFilterNodes)
			addRemoveFilterCombo.getItems().remove(node);

		addRemoveFilterCombo.valueProperty().addListener(new ChangeListener<String>() {
			boolean removing = false;

			@Override
			public void changed(ObservableValue ov, String t, String t1)
			{
				if (removing || t1 == null || t1.equals("") || t1.equals(ADD_FILTER))
					return;

				visibleFilterNodes.add(t1);

				Platform.runLater(new Runnable() {
					@Override
					public void run()
					{
						removing = true;
						addRemoveFilterCombo.getItems().remove(t1);
						addRemoveFilterCombo.setValue(ADD_FILTER);
						removing = false;
					}
				});

				BMDExpressProperties.getInstance().updateFilter(filterableClass.getName(),
						visibleFilterNodes);

				// update view
				FilterComponent fc = getFilterComponent(t1);
				if (fc != null)
				{
					filterComponentVbox.getChildren().add(0, fc);
					filterComponents.add(fc);
				}

				dataFilterComponentListener.dataFilterChanged();
			}
		});

		this.getChildren().add(addRemoveFilterCombo);
		filterNodeScrollPane = new ScrollPane();
		updateFilterNodes();
		this.getChildren().add(filterNodeScrollPane);

	}

	private void updateFilterNodes()
	{
		filterComponentVbox = new VBox();
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
			FilterComponent fc = getFilterComponent(key);

			// if this key only produces null values due to compatibility issues,
			// it's not "useable" hence the isuseable flag.
			if (fc != null && fc.getIsUseable())
				filterComponents.add(fc);

		}
		filterComponentVbox.getChildren().addAll(filterComponents);
		filterNodeScrollPane.setContent(filterComponentVbox);
	}

	private FilterComponent getFilterComponent(String key)
	{
		// if there is already a datafilter associated with this object,
		// use it
		DataFilter df = dataFilterMap.get(key);
		if (filterAnnotationExtractor.getReturnType(key) != null)
			return FilterComponentFactory.createFilterComponent(key, dataFilterComponentListener,
					filterAnnotationExtractor.getReturnType(key), df,
					filterAnnotationExtractor.getMethod(key), this);
		else
			return null;
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
			if (returnType.equals(Integer.class))
			{
				df = new IntegerFilter<>(DataFilterType.BETWEEN, filterableClass, fc.getFilterKey(),
						fc.getValues());
			}
			else if (returnType.equals(Float.class) || returnType.equals(Double.class)
					|| returnType.equals(Number.class))
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
			else
				df = new StringFilter<>(DataFilterType.EQUALS, filterableClass, fc.getFilterKey(),
						fc.getValues());
			dataFilters.add(df);
			dataFilterMap.put(df.getKey(), df);
		}

		DataFilterPack dFP = new DataFilterPack("Data Filter Pack", dataFilters);

		return dFP;
	}

	@Override
	public void close(FilterComponent fc)
	{
		filterComponentVbox.getChildren().remove(fc);
		List<String> items = new ArrayList<>(addRemoveFilterCombo.getItems());
		items.add(fc.getFilterKey());
		Collections.sort(items);
		addRemoveFilterCombo.getItems().clear();
		addRemoveFilterCombo.getItems().addAll(items);
		addRemoveFilterCombo.setValue(ADD_FILTER);
		filterComponents.remove(fc);

		visibleFilterNodes.remove(fc.getFilterKey());
		BMDExpressProperties.getInstance().updateFilter(filterableClass.getName(), visibleFilterNodes);
		dataFilterComponentListener.dataFilterChanged();

	}

}
