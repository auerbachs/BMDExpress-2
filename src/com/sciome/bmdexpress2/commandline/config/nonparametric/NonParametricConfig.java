package com.sciome.bmdexpress2.commandline.config.nonparametric;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@JsonTypeInfo(use = Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonSubTypes({ @Type(value = GCurvePConfig.class, name = "gcurvep") })
public abstract class NonParametricConfig
{
	private Integer	bootstraps			= 1000;
	private Double	bmrFactor			= 3.0;
	private Double	pValueConfidence	= 0.05;

	// this could be dose response data or prefiltered data.
	private String	inputCategory;
	// name of the data set that is being input to bmds analysis.
	private String	inputName;

	// name of output for the analysis
	private String	outputName;

	private Integer	numberOfThreads		= 1;

	public Integer getBootStraps()
	{
		return bootstraps;
	}

	public void setBootStraps(Integer bootstraps)
	{
		this.bootstraps = bootstraps;
	}

	public Integer getBootstraps()
	{
		return bootstraps;
	}

	public void setBootstraps(Integer bootstraps)
	{
		this.bootstraps = bootstraps;
	}

	public Double getBmrFactor()
	{
		return bmrFactor;
	}

	public void setBmrFactor(Double bmrFactor)
	{
		this.bmrFactor = bmrFactor;
	}

	public Double getpValueConfidence()
	{
		return pValueConfidence;
	}

	public void setpValueConfidence(Double pValueConfidence)
	{
		this.pValueConfidence = pValueConfidence;
	}

	public String getInputCategory()
	{
		return inputCategory;
	}

	public void setInputCategory(String inputCategory)
	{
		this.inputCategory = inputCategory;
	}

	public String getInputName()
	{
		return inputName;
	}

	public void setInputName(String inputName)
	{
		this.inputName = inputName;
	}

	public String getOutputName()
	{
		return outputName;
	}

	public void setOutputName(String outputName)
	{
		this.outputName = outputName;
	}

	public Integer getNumberOfThreads()
	{
		return numberOfThreads;
	}

	public void setNumberOfThreads(Integer numberOfThreads)
	{
		this.numberOfThreads = numberOfThreads;
	}

}
