package com.sciome.bmdexpress2.commandline.config.prefilter;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@JsonTypeInfo(use = Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonSubTypes({ @Type(value = ANOVAConfig.class, name = "anova") })
public abstract class PrefilterConfig
{

	// name of expression data to prefilter
	private String	inputName;

	// name of output for the analysis
	private String	outputName;

	private Double	pValueCutoff;
	private Integer	useMultipleTestingCorrection;
	private Integer	filterOutControlGenes;

	private Integer	useFoldChange;
	// 0=no log, 1=log2, 2=log10, 3=lognatural
	private Integer	logTransformationOfData;
	private Double	foldChange;

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

	public Integer getUseMultipleTestingCorrection()
	{
		return useMultipleTestingCorrection;
	}

	public void setUseMultipleTestingCorrection(Integer useMultipleTestingCorrection)
	{
		this.useMultipleTestingCorrection = useMultipleTestingCorrection;
	}

	public Integer getFilterOutControlGenes()
	{
		return filterOutControlGenes;
	}

	public void setFilterOutControlGenes(Integer filterOutControlGenes)
	{
		this.filterOutControlGenes = filterOutControlGenes;
	}

	public Integer getUseFoldChange()
	{
		return useFoldChange;
	}

	public void setUseFoldChange(Integer userFoldChange)
	{
		this.useFoldChange = userFoldChange;
	}

	public Integer getLogTransformationOfData()
	{
		return logTransformationOfData;
	}

	public void setLogTransformationOfData(Integer logTransformationOfData)
	{
		this.logTransformationOfData = logTransformationOfData;
	}

	public Double getFoldChange()
	{
		return foldChange;
	}

	public void setFoldChange(Double foldChange)
	{
		this.foldChange = foldChange;
	}

}
