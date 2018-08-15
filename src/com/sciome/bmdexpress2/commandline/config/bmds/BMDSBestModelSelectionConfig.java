package com.sciome.bmdexpress2.commandline.config.bmds;

public class BMDSBestModelSelectionConfig
{

	// 1 "nested chi square", 2 "lowest aic"
	private Integer	bestPolyTest;

	private Double	pValueCutoff;

	// 0 or 1
	private Boolean	flagHillWithKParameter;

	// 1,2,3 (lowest, 1/3 of, 1/2 of) positive dose
	private Integer	kParameterValue;

	// 1,2,3,4,5
	// include flagged hill, exclude flagged hill from best models,
	// exclude all hill from best models,
	// modify bmd if flagged hill as best model,
	// select next best model with pvalue > 0.05
	private Integer	bestModelSelectionWithFlaggedHill;

	// fraction of minimum BMD to set modified flagged hill to
	private Double	modifyFlaggedHillWithFractionMinBMD;

	// 1,2,3
	// 1 Compute and utilize in best model selection
	// 2 Compute but ignore non-convergence in best model selection
	// 3 Do not compute
	private Integer	bestModelSelectionBMDLandBMDUConvergence;

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

	public Boolean getFlagHillWithKParameter()
	{
		return flagHillWithKParameter;
	}

	public void setFlagHillWithKParameter(Boolean flagHillWithKParameter)
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

	public Integer getBestModelSelectionBMDLandBMDUConvergence()
	{
		return bestModelSelectionBMDLandBMDUConvergence;
	}

	public void setBestModelSelectionBMDLandBMDUConvergence(Integer bestModelSelectionBMDLandBMDUConvergence)
	{
		this.bestModelSelectionBMDLandBMDUConvergence = bestModelSelectionBMDLandBMDUConvergence;
	}

}
