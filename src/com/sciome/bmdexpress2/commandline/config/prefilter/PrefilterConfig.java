package com.sciome.bmdexpress2.commandline.config.prefilter;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@JsonTypeInfo(use = Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonSubTypes({ @Type(value = ANOVAConfig.class, name = "anova"),
				@Type(value = WilliamsConfig.class, name = "williams"),
				@Type(value = OriogenConfig.class, name = "oriogen")})
public abstract class PrefilterConfig
{

	// name of expression data to prefilter
	private String	inputName;

	// name of output for the analysis
	private String	outputName;

	private Double	pValueCutoff;
	private Boolean	useMultipleTestingCorrection	= false;
	private Boolean	filterOutControlGenes			= false;

	private Boolean	useFoldChange					= false;

	private Double	foldChange;
	private Double	foldChangeLoel;
	private Double	pValueLoel;
	
	private Boolean	tTest							= false;

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

	public Double getpValueCutoff()
	{
		return pValueCutoff;
	}

	public void setpValueCutoff(Double pValueCutoff)
	{
		this.pValueCutoff = pValueCutoff;
	}

	public Boolean getUseMultipleTestingCorrection()
	{
		return useMultipleTestingCorrection;
	}

	public void setUseMultipleTestingCorrection(Boolean useMultipleTestingCorrection)
	{
		this.useMultipleTestingCorrection = useMultipleTestingCorrection;
	}

	public Boolean getFilterOutControlGenes()
	{
		return filterOutControlGenes;
	}

	public void setFilterOutControlGenes(Boolean filterOutControlGenes)
	{
		this.filterOutControlGenes = filterOutControlGenes;
	}

	public Boolean getUseFoldChange()
	{
		return useFoldChange;
	}

	public void setUseFoldChange(Boolean userFoldChange)
	{
		this.useFoldChange = userFoldChange;
	}

	public Double getFoldChange()
	{
		return foldChange;
	}

	public void setFoldChange(Double foldChange)
	{
		this.foldChange = foldChange;
	}

	public Double getFoldChangeLoel() 
	{
		return foldChangeLoel;
	}

	public void setFoldChangeLoel(Double foldChangeLoel) 
	{
		this.foldChangeLoel = foldChangeLoel;
	}

	public Double getpValueLoel() 
	{
		return pValueLoel;
	}

	public void setpValueLoel(Double pValueLoel) 
	{
		this.pValueLoel = pValueLoel;
	}

	public Boolean gettTest() {
		return tTest;
	}

	public void settTest(Boolean tTest) {
		this.tTest = tTest;
	}
}
