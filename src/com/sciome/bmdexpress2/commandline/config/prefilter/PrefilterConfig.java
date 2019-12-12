package com.sciome.bmdexpress2.commandline.config.prefilter;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@JsonTypeInfo(use = Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonSubTypes({ @Type(value = ANOVAConfig.class, name = "anova"),
		@Type(value = WilliamsConfig.class, name = "williams"),
		@Type(value = OriogenConfig.class, name = "oriogen") })
public abstract class PrefilterConfig
{

	// name of expression data to prefilter
	private String	inputName;

	// name of output for the analysis
	private String	outputName;

	private Double	pValueCutoff					= 0.05;
	private Boolean	useMultipleTestingCorrection	= false;
	private Boolean	filterOutControlGenes			= true;

	private Boolean	useFoldChange					= true;

	private Double	foldChange						= 2.0;
	private Double	foldChangeLotel					= 1.5;
	private Double	pValueLotel						= 0.05;

	private Integer	numberOfThreads					= 1;

	// 1,2
	// 1 Dunnett's test
	// 2 T-Test
	private Integer	lotelTest						= 1;

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

	public Double getFoldChangeLotel()
	{
		return foldChangeLotel;
	}

	public void setFoldChangeLotel(Double foldChangeLoel)
	{
		this.foldChangeLotel = foldChangeLoel;
	}

	public Double getpValueLotel()
	{
		return pValueLotel;
	}

	public void setpValueLotel(Double pValueLoel)
	{
		this.pValueLotel = pValueLoel;
	}

	public Integer getlotelTest()
	{
		return lotelTest;
	}

	public void setLotelTest(Integer tTest)
	{
		this.lotelTest = tTest;
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
