package com.sciome.bmdexpress2.commandline.config.bmds;

public class BMDSInputConfig
{

	private Integer maxIterations = 250;
	private Double confidenceLevel = 0.95;

	private Boolean constantVariance = true;

	private Boolean restrictPower = true;

	private Double bmrFactor = 1.021;

	private Integer bmrType = 1;

	// only relevant for the new version
	private Integer bmdUBmdLEstimationMethod = 1; // 1 for epa profiling, 2 for Wald/EWald

	public Integer getMaxIterations()
	{
		return maxIterations;
	}

	public void setMaxIterations(Integer maxIterations)
	{
		this.maxIterations = maxIterations;
	}

	public Double getConfidenceLevel()
	{
		return confidenceLevel;
	}

	public void setConfidenceLevel(Double confidenceLevel)
	{
		this.confidenceLevel = confidenceLevel;
	}

	public Boolean getConstantVariance()
	{
		return constantVariance;
	}

	public void setConstantVariance(Boolean constantVariance)
	{
		this.constantVariance = constantVariance;
	}

	public Double getBmrFactor()
	{
		return bmrFactor;
	}

	public void setBmrFactor(Double bmrFactor)
	{
		this.bmrFactor = bmrFactor;
	}

	public Boolean getRestrictPower()
	{
		return restrictPower;
	}

	public void setRestrictPower(Boolean restrict)
	{
		this.restrictPower = restrict;
	}

	public Integer getBmrType()
	{
		return bmrType;
	}

	public void setBmrType(Integer bmrType)
	{
		this.bmrType = bmrType;
	}

	public Integer getBmdUBmdLEstimationMethod()
	{
		return bmdUBmdLEstimationMethod;
	}

	public void setBmdUBmdLEstimationMethod(Integer bmdUBmdLEstimationMethod)
	{
		this.bmdUBmdLEstimationMethod = bmdUBmdLEstimationMethod;
	}

}
