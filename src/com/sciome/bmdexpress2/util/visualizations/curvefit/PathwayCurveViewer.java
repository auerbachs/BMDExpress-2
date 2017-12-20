/*
 * ModelGraphics.java
 *
 * Created on 10/17/2007
 *
 * Uses the JFreeChart libraries to create graphs based on
 * Benchmark Dose Analyses done in BMDExpress and provides the user with
 * the ability to edit various aspects of the resulting graph.
 *
 * Modified by Longlong Yang, 11/13/2008
 * parameters below with pValue added to index = 2
 * So the first three values are BMD, BMDL, pValue
 */

/*
 * @author  ehealy
 */
package com.sciome.bmdexpress2.util.visualizations.curvefit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.AutoCompletionBinding.ISuggestionRequest;
import org.controlsfx.control.textfield.TextFields;

import com.sciome.bmdexpress2.mvp.model.category.CategoryAnalysisResult;
import com.sciome.bmdexpress2.mvp.model.category.CategoryAnalysisResults;
import com.sciome.bmdexpress2.mvp.model.category.ReferenceGeneProbeStatResult;
import com.sciome.bmdexpress2.mvp.model.stat.BMDResult;
import com.sciome.bmdexpress2.mvp.model.stat.ProbeStatResult;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

/**
 */
public class PathwayCurveViewer extends VBox
{

	private List<String>							pathways						= new ArrayList<>();
	Set<String>										pathwaySet						= new HashSet<>();
	// textfield to select pathway for whose genes to plot on curve viewer
	private TextField								pathwayAutoCompleteTextField	= new TextField();
	// which categoryanalysis results to include. this is likley equivalent to which chemical. you might
	// want to view more than one chemical
	private CheckComboBox<CategoryAnalysisResults>	categoryAnalysisResultsCombo	= new CheckComboBox<>();

	// the genes that are available to curveplot based on pathway and categoryanalysisresults selected.
	private CheckComboBox<String>					geneCombo						= new CheckComboBox<>();

	private List<CategoryAnalysisResults>			categoryAnalysisResults;
	private JFreeCurve								jfreeCurve;
	private HBox									hbox;

	public PathwayCurveViewer(List<CategoryAnalysisResults> categoryAnalysisResults)
	{
		super(8);
		this.categoryAnalysisResults = categoryAnalysisResults;
		// load available pathways
		for (CategoryAnalysisResults results : categoryAnalysisResults)
			for (CategoryAnalysisResult result : results.getCategoryAnalsyisResults())
				pathwaySet.add(result.getCategoryDescription().toLowerCase());
		pathways.addAll(pathwaySet);
		Collections.sort(pathways);
		ComboBox<String> howtodostring;
		// Create the CheckComboBox with the data
		howtodostring = new ComboBox<String>(
				FXCollections.observableArrayList(Arrays.asList("begins with", "contains")));

		howtodostring.setValue("begins with");

		boolean areAnyBMDResultNull = false;
		for (CategoryAnalysisResults r : categoryAnalysisResults)
			if (r.getBmdResult() == null)
				areAnyBMDResultNull = true;
		if (areAnyBMDResultNull)
		{
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Not Available For Your Datq");
			alert.setHeaderText("Not Available For Your Datq");
			alert.setContentText(
					"The category analysis curve viewer is not available for at least one of these datasets because the data was generated with an older version of BMDExpress2.  Please rerun category analysis on the bmdanalysis results to be able to view them in curve viewer.");

			alert.showAndWait();
			return;
		}
		hbox = new HBox(5);
		Label l = new Label("Define Data:  ");
		hbox.getChildren().addAll(l, howtodostring, pathwayAutoCompleteTextField);
		l.setAlignment(Pos.CENTER_LEFT);
		this.getChildren().addAll(hbox);

		TextFields.bindAutoCompletion(pathwayAutoCompleteTextField,
				new Callback<AutoCompletionBinding.ISuggestionRequest, Collection<String>>() {

					@Override
					public Collection<String> call(ISuggestionRequest param)
					{
						List<String> returnList = new ArrayList<>();
						for (String p : pathways)
							if (howtodostring.getValue().equals("contains")
									&& p.toLowerCase().contains(param.getUserText().toLowerCase()))
								returnList.add(p);
							else if (howtodostring.getValue().equals("begins with")
									&& p.toLowerCase().startsWith(param.getUserText().toLowerCase()))
								returnList.add(p);
						return returnList;
					}
				});

		pathwayAutoCompleteTextField.textProperty().addListener((observable, oldValue, newValue) ->
		{
			if (newValue == null)
				return;
			if (pathwaySet.contains(newValue))
			{
				hbox.getChildren().remove(categoryAnalysisResultsCombo);
				hbox.getChildren().remove(geneCombo);
				categoryAnalysisResultsCombo = new CheckComboBox<>();
				categoryAnalysisResultsCombo.setMaxWidth(200);
				this.categoryAnalysisResultsCombo.getItems()
						.setAll(getCatResultsThatContainPathways(newValue));
				hbox.getChildren().add(categoryAnalysisResultsCombo);
				setUpCategoryAnalysisComboListener();
			}

		});

		geneCombo.setMaxWidth(200);
		categoryAnalysisResultsCombo.setMaxWidth(200);

	}

	private void setUpCategoryAnalysisComboListener()
	{
		categoryAnalysisResultsCombo.getCheckModel().getCheckedItems()
				.addListener(new ListChangeListener<CategoryAnalysisResults>() {
					public void onChanged(ListChangeListener.Change<? extends CategoryAnalysisResults> c)
					{
						hbox.getChildren().remove(geneCombo);
						geneCombo = new CheckComboBox<>();
						geneCombo.getItems()
								.setAll(getGenesThatAreInPathwayAndCategoryResults(
										pathwayAutoCompleteTextField.getText(), categoryAnalysisResultsCombo
												.getCheckModel().getCheckedItems()));
						hbox.getChildren().add(geneCombo);
						geneCombo.setMaxWidth(200);

						setUpGeneComboListener();

					}

				});

	}

	private void setUpGeneComboListener()
	{
		geneCombo.getCheckModel().getCheckedItems().addListener(new ListChangeListener<String>() {
			public void onChanged(ListChangeListener.Change<? extends String> c)
			{
				Map<BMDResult, Set<ProbeStatResult>> bmdResultMap = getBMDResultsForCurveView(
						pathwayAutoCompleteTextField.getText(),
						categoryAnalysisResultsCombo.getCheckModel().getCheckedItems(),
						geneCombo.getCheckModel().getCheckedItems());

				// remove the chart from the view (if it exists)
				if (jfreeCurve != null)
					PathwayCurveViewer.this.getChildren().remove(jfreeCurve.getChartViewer());
				if (bmdResultMap.keySet().size() > 0)
				{
					jfreeCurve = new JFreeCurve(null, pathwayAutoCompleteTextField.getText(), bmdResultMap);
					// add newly created chart to the view
					PathwayCurveViewer.this.getChildren().add(jfreeCurve.getChartViewer());
				}
				else
					jfreeCurve = null;

			}
		});

	}

	// given a pathway name (one that was selected)
	// provide a list of CategroyAnalysisResults
	private List<CategoryAnalysisResults> getCatResultsThatContainPathways(String pathway)
	{
		Set<CategoryAnalysisResults> catResults = new HashSet<>();
		for (CategoryAnalysisResults results : categoryAnalysisResults)
			for (CategoryAnalysisResult result : results.getCategoryAnalsyisResults())
				if (result.getCategoryDescription().equalsIgnoreCase(pathway))
				{
					catResults.add(results);
					break;
				}

		List<CategoryAnalysisResults> returnList = new ArrayList<>(catResults);
		returnList.sort(new Comparator<CategoryAnalysisResults>() {

			@Override
			public int compare(CategoryAnalysisResults o1, CategoryAnalysisResults o2)
			{
				return o1.getName().compareTo(o2.getName());
			}
		});
		return returnList;
	}

	// get a list of genes based on the pathway that was selected and the
	// category results that are selected
	private List<String> getGenesThatAreInPathwayAndCategoryResults(String pathway,
			List<CategoryAnalysisResults> catResults)
	{

		Set<String> geneSet = new HashSet<>();
		for (CategoryAnalysisResults results : catResults)
			for (CategoryAnalysisResult result : results.getCategoryAnalsyisResults())
				if (result.getCategoryDescription().equalsIgnoreCase(pathway))
				{
					if (result.getReferenceGeneProbeStatResults() == null)
						continue;
					for (ReferenceGeneProbeStatResult rgps : result.getReferenceGeneProbeStatResults())
					{
						if (rgps.getReferenceGene() != null
								&& rgps.getReferenceGene().getGeneSymbol() != null)
							geneSet.add(rgps.getReferenceGene().getGeneSymbol());
					}
					// catResults.add(results);
					break;
				}

		List<String> returnList = new ArrayList<>(geneSet);
		Collections.sort(returnList);
		return returnList;
	}

	// return a map that contains bmdresult to probestatresult sets that will be plotted
	// on the curve plotter. they will be of a pathway and represented in a list
	// of categoryanalysisresults.
	private Map<BMDResult, Set<ProbeStatResult>> getBMDResultsForCurveView(String pathway,
			List<CategoryAnalysisResults> catResults, List<String> genes)
	{
		Set<String> geneSet = new HashSet<>(genes);
		Map<BMDResult, Set<ProbeStatResult>> returnMap = new HashMap<>();
		for (CategoryAnalysisResults results : catResults)
			for (CategoryAnalysisResult result : results.getCategoryAnalsyisResults())
				if (result.getCategoryDescription().equalsIgnoreCase(pathway))
				{
					if (result.getReferenceGeneProbeStatResults() == null)
						continue;
					for (ReferenceGeneProbeStatResult rgps : result.getReferenceGeneProbeStatResults())
					{
						if (rgps.getReferenceGene() != null && rgps.getReferenceGene().getGeneSymbol() != null
								&& geneSet.contains(rgps.getReferenceGene().getGeneSymbol()))
						{
							if (returnMap.get(results.getBmdResult()) == null)
								returnMap.put(results.getBmdResult(), new HashSet<>());
							returnMap.get(results.getBmdResult()).addAll(rgps.getProbeStatResults());
						}
					}
					break;
				}

		List<String> returnList = new ArrayList<>(geneSet);
		Collections.sort(returnList);

		return returnMap;
	}

}