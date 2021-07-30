package com.sciome.bmdexpress2.mvp.model;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sciome.bmdexpress2.mvp.model.category.CategoryAnalysisResults;
import com.sciome.bmdexpress2.mvp.model.prefilter.CurveFitPrefilterResults;
import com.sciome.bmdexpress2.mvp.model.prefilter.OneWayANOVAResults;
import com.sciome.bmdexpress2.mvp.model.prefilter.OriogenResults;
import com.sciome.bmdexpress2.mvp.model.prefilter.WilliamsTrendResults;
import com.sciome.bmdexpress2.mvp.model.stat.BMDResult;

/*
 * hold various experiments and analysis results.
 */
public class BMDProject implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -9098616640985905463L;

	// The name of this project
	private String name;

	// Store each of the four analysis results in a map accessible by a String value
	private List<DoseResponseExperiment> doseResponseExperiments = new ArrayList<>();
	private List<OneWayANOVAResults> oneWayANOVAResults = new ArrayList<>();
	private List<WilliamsTrendResults> williamsTrendResults = new ArrayList<>();
	private List<CurveFitPrefilterResults> curveFitPrefilterResults = new ArrayList<>();
	private List<OriogenResults> oriogenResults = new ArrayList<>();
	private List<BMDResult> bMDResult = new ArrayList<>();
	private List<CategoryAnalysisResults> categoryAnalysisResults = new ArrayList<>();

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public List<DoseResponseExperiment> getDoseResponseExperiments()
	{
		return doseResponseExperiments;
	}

	public void setDoseResponseExperiments(List<DoseResponseExperiment> doseResponseExperiments)
	{
		this.doseResponseExperiments = doseResponseExperiments;
	}

	public List<OneWayANOVAResults> getOneWayANOVAResults()
	{
		return oneWayANOVAResults;
	}

	public void setOneWayANOVAResults(List<OneWayANOVAResults> oneWayANOVAResults)
	{
		this.oneWayANOVAResults = oneWayANOVAResults;
	}

	public List<WilliamsTrendResults> getWilliamsTrendResults()
	{
		// since williams trend test is a new structure, we must check for null in case an older project file
		// is loaded and
		// the trend test is null. The rest of the project expects an empty list.
		if (williamsTrendResults == null)
			williamsTrendResults = new ArrayList<>();
		return williamsTrendResults;
	}

	public void setWilliamsTrendResults(List<WilliamsTrendResults> williamsTrendResults)
	{
		this.williamsTrendResults = williamsTrendResults;
	}

	public List<CurveFitPrefilterResults> getCurveFitPrefilterResults()
	{
		// since williams trend test is a new structure, we must check for null in case an older project file
		// is loaded and
		// the trend test is null. The rest of the project expects an empty list.
		if (curveFitPrefilterResults == null)
			curveFitPrefilterResults = new ArrayList<>();
		return curveFitPrefilterResults;
	}

	public void setCurveFitPrefilterResults(List<CurveFitPrefilterResults> curveFitPrefilterResults)
	{
		this.curveFitPrefilterResults = curveFitPrefilterResults;
	}

	public List<OriogenResults> getOriogenResults()
	{
		// since oriogen test is a new structure, we must check for null in case an older project file is
		// loaded and
		// the trend test is null. The rest of the project expects an empty list.
		if (oriogenResults == null)
			oriogenResults = new ArrayList<>();
		return oriogenResults;
	}

	public void setOriogenResults(List<OriogenResults> oriogenResults)
	{
		this.oriogenResults = oriogenResults;
	}

	public List<BMDResult> getbMDResult()
	{
		return bMDResult;
	}

	public void setbMDResult(List<BMDResult> bMDResult)
	{
		this.bMDResult = bMDResult;
	}

	public List<CategoryAnalysisResults> getCategoryAnalysisResults()
	{
		return categoryAnalysisResults;
	}

	public void setCategoryAnalysisResults(List<CategoryAnalysisResults> categoryAnalysisResults)
	{
		this.categoryAnalysisResults = categoryAnalysisResults;
	}

	@JsonIgnore
	public boolean isProjectEmpty()
	{
		if (doseResponseExperiments.size() > 0 || oneWayANOVAResults.size() > 0
				|| williamsTrendResults.size() > 0 || oriogenResults.size() > 0 || bMDResult.size() > 0
				|| categoryAnalysisResults.size() > 0)
			return false;
		return true;
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException
	{

		in.defaultReadObject();

		if (williamsTrendResults == null)
			williamsTrendResults = new ArrayList<>();

		if (oriogenResults == null)
			oriogenResults = new ArrayList<>();

		// now make sure all the names are unique
		for (OneWayANOVAResults data : oneWayANOVAResults)
			giveBMDAnalysisUniqueName(data, data.getName(), 1);
		for (WilliamsTrendResults data : williamsTrendResults)
			giveBMDAnalysisUniqueName(data, data.getName(), 1);
		for (OriogenResults data : oriogenResults)
			giveBMDAnalysisUniqueName(data, data.getName(), 1);
		for (BMDResult data : bMDResult)
			giveBMDAnalysisUniqueName(data, data.getName(), 1);
		for (DoseResponseExperiment data : doseResponseExperiments)
			giveBMDAnalysisUniqueName(data, data.getName(), 1);
		for (CategoryAnalysisResults data : categoryAnalysisResults)
			giveBMDAnalysisUniqueName(data, data.getName(), 1);

	}

	@SuppressWarnings("unchecked")
	public void giveBMDAnalysisUniqueName(BMDExpressAnalysisDataSet dataSet, String proposedName)
	{
		giveBMDAnalysisUniqueName(dataSet, proposedName, 0);

	}

	@SuppressWarnings("unchecked")
	private void giveBMDAnalysisUniqueName(BMDExpressAnalysisDataSet dataSet, String proposedName,
			int allowed)
	{
		Set<String> names = new HashSet<>();
		List<BMDExpressAnalysisDataSet> dataSetWithNames = null;
		if (dataSet instanceof WilliamsTrendResults)
			dataSetWithNames = (List<BMDExpressAnalysisDataSet>) (List<?>) williamsTrendResults;
		else if (dataSet instanceof OneWayANOVAResults)
			dataSetWithNames = (List<BMDExpressAnalysisDataSet>) (List<?>) oneWayANOVAResults;
		else if (dataSet instanceof BMDResult)
			dataSetWithNames = (List<BMDExpressAnalysisDataSet>) (List<?>) bMDResult;
		else if (dataSet instanceof OriogenResults)
			dataSetWithNames = (List<BMDExpressAnalysisDataSet>) (List<?>) oriogenResults;
		else if (dataSet instanceof CategoryAnalysisResults)
			dataSetWithNames = (List<BMDExpressAnalysisDataSet>) (List<?>) categoryAnalysisResults;
		else if (dataSet instanceof DoseResponseExperiment)
			dataSetWithNames = (List<BMDExpressAnalysisDataSet>) (List<?>) doseResponseExperiments;

		int count = 0;
		for (BMDExpressAnalysisDataSet ds : dataSetWithNames)
		{
			names.add(ds.getName());
			if (ds.getName().equals(proposedName))
				count++;
		}
		if (count > allowed)
		{
			String appendage = "";
			int i = 1;
			while (names.contains(proposedName + appendage))
			{
				appendage = "_" + i;
				i++;
			}
			proposedName += appendage;
		}
		dataSet.setName(proposedName);

	}

}
