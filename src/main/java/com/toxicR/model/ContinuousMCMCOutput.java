package com.toxicR.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonSetter;

public class ContinuousMCMCOutput
{

	private Integer samples;
	private Integer nparms;
	private List<Double> BMDS;
	private List<Double> parms;

	public Integer getSamples()
	{
		return samples;
	}

	public void setSamples(Integer samples)
	{
		this.samples = samples;
	}

	public Integer getNparms()
	{
		return nparms;
	}

	public void setNparms(Integer nparms)
	{
		this.nparms = nparms;
	}

	public List<Double> getBMDS()
	{
		return BMDS;
	}

	@JsonSetter("BMDS")
	public void setBMDS(List<Double> bMDS)
	{
		BMDS = bMDS;
	}

	public List<Double> getParms()
	{
		return parms;
	}

	public void setParms(List<Double> parms)
	{
		this.parms = parms;
	}

}
