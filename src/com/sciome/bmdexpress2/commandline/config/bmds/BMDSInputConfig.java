package com.sciome.bmdexpress2.commandline.config.bmds;

public class BMDSInputConfig
{

	private Integer	maxIterations;
	private Double	confidenceLevel;

	private Boolean	constantVariance;

	private Boolean	restrictPower;

	private Double	bmrFactor;

	private Integer	bmrType;

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

}
