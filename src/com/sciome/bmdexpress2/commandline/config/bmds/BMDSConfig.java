package com.sciome.bmdexpress2.commandline.config.bmds;

import java.util.ArrayList;
import java.util.List;

public class BMDSConfig
{

	// this could be dose response data or prefiltered data.
	private String					inputCategory;

	// name of the data set that is being input to bmds analysis.
	private String					inputName;

	// name of output for the analysis
	private String					outputName;

	private List<BMDSModelConfig>	modelConfigs	= new ArrayList<>();

	// 1 "nested chi square", 2 "lowest aic"
	private Integer					bestPolyTest;

	private Double					pValueCutoff;

	// 0 or 1
	private Integer					flagHillWithKParameter;
	// 1,2,3 (lowest, 1/3 of, 1/2 of) positive dose
	private Integer					kParameterValue;
	// 1,2,3,4,5
	// include flagged hill, exclude flagged hill from best models,
	// exclude all hill from best models,
	// modify bmd if flagged hill as best model,
	// select next best model with pvalue > 0.05
	private Integer					bestModelSelectionWithFlaggedHill;

	// fraction of minimum BMD to set modified flagged hill to
	private Double					modifyFlaggedHillWithFractionMinBMD;

	private Integer					numberOfThreads	= 1;

	public List<BMDSModelConfig> getModelConfigs()
	{
		return modelConfigs;
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

	public void setModelConfigs(List<BMDSModelConfig> modelConfigs)
	{
		this.modelConfigs = modelConfigs;
	}

	public Integer getBestPolyTest()
	{
		return bestPolyTest;
	}

	public void setBestPolyTest(Integer bestPolyTest)
	{
		this.bestPolyTest = bestPolyTest;
	}

	public Double getpValueCutoff()
	{
		return pValueCutoff;
	}

	public void setpValueCutoff(Double pValueCutoff)
	{
		this.pValueCutoff = pValueCutoff;
	}

	public Integer getFlagHillWithKParameter()
	{
		return flagHillWithKParameter;
	}

	public void setFlagHillWithKParameter(Integer flagHillWithKParameter)
	{
		this.flagHillWithKParameter = flagHillWithKParameter;
	}

	public Integer getkParameterValue()
	{
		return kParameterValue;
	}

	public void setkParameterValue(Integer kParameterValue)
	{
		this.kParameterValue = kParameterValue;
	}

	public Integer getBestModelSelectionWithFlaggedHill()
	{
		return bestModelSelectionWithFlaggedHill;
	}

	public void setBestModelSelectionWithFlaggedHill(Integer bestModelSelectionWithFlaggedHill)
	{
		this.bestModelSelectionWithFlaggedHill = bestModelSelectionWithFlaggedHill;
	}

	public Double getModifyFlaggedHillWithFractionMinBMD()
	{
		return modifyFlaggedHillWithFractionMinBMD;
	}

	public void setModifyFlaggedHillWithFractionMinBMD(Double modifyFlaggedHillWithFractionMinBMD)
	{
		this.modifyFlaggedHillWithFractionMinBMD = modifyFlaggedHillWithFractionMinBMD;
	}

}
