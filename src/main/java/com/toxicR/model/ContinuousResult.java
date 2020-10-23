package com.toxicR.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonSetter;

public class ContinuousResult
{

	private Integer model;
	private Integer dist;
	private Integer nparms;
	private Double max;
	private Integer distNumE;
	private List<Double> parms;
	private List<Double> cov;
	private List<Double> bmdDist;

	public Integer getModel()
	{
		return model;
	}

	public void setModel(Integer model)
	{
		this.model = model;
	}

	public Integer getDist()
	{
		return dist;
	}

	public void setDist(Integer dist)
	{
		this.dist = dist;
	}

	public Integer getNparms()
	{
		return nparms;
	}

	public void setNparms(Integer nparms)
	{
		this.nparms = nparms;
	}

	public List<Double> getParms()
	{
		return parms;
	}

	public void setParms(List<Double> parms)
	{
		this.parms = parms;
	}

	public List<Double> getCov()
	{
		return cov;
	}

	public void setCov(List<Double> cov)
	{
		this.cov = cov;
	}

	public Double getMax()
	{
		return max;
	}

	public void setMax(Double max)
	{
		this.max = max;
	}

	public Integer getDistNumE()
	{
		return distNumE;
	}

	@JsonSetter("dist_numE")
	public void setDistNumE(Integer distNumE)
	{
		this.distNumE = distNumE;
	}

	public List<Double> getBmdDist()
	{
		return bmdDist;
	}

	@JsonSetter("bmd_dist")
	public void setBmdDist(List<Double> bmdDist)
	{
		this.bmdDist = bmdDist;
	}

}
