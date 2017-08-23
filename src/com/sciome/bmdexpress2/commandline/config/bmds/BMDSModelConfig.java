package com.sciome.bmdexpress2.commandline.config.bmds;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@JsonTypeInfo(use = Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonSubTypes({ @Type(value = PolyConfig.class, name = "poly"),
		@Type(value = PowerConfig.class, name = "power"),
		@Type(value = ExponentialConfig.class, name = "exp"),
		@Type(value = HillConfig.class, name = "hill") })
public abstract class BMDSModelConfig
{

	private Integer	maxIterations;
	private Double	confidenceLevel;

	// 1 or 0
	private Integer	constantVariance;

	private Double	bmrFactor;

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

	public Integer getConstantVariance()
	{
		return constantVariance;
	}

	public void setConstantVariance(Integer constantVariance)
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

}
