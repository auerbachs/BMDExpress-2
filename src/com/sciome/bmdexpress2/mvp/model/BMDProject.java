package com.sciome.bmdexpress2.mvp.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sciome.bmdexpress2.mvp.model.category.CategoryAnalysisResults;
import com.sciome.bmdexpress2.mvp.model.prefilter.OneWayANOVAResults;
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
	private static final long				serialVersionUID		= -9098616640985905463L;

	// The name of this project
	private String							name;

	// Store each of the four analysis results in a map accessible by a String value
	private List<DoseResponseExperiment>	doseResponseExperiments	= new ArrayList<>();
	private List<OneWayANOVAResults>		oneWayANOVAResults		= new ArrayList<>();
	private List<WilliamsTrendResults>		williamsTrendResults	= new ArrayList<>();
	private List<BMDResult>					bMDResult				= new ArrayList<>();
	private List<CategoryAnalysisResults>	categoryAnalysisResults	= new ArrayList<>();

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
		return williamsTrendResults;
	}

	public void setWilliamsTrendResults(List<WilliamsTrendResults> williamsTrendResults)
	{
		this.williamsTrendResults = williamsTrendResults;
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
		if (doseResponseExperiments.size() > 0 || oneWayANOVAResults.size() > 0 || williamsTrendResults.size() > 0 ||
				bMDResult.size() > 0 || categoryAnalysisResults.size() > 0)
			return false;
		return true;
	}

}
