package com.toxicR.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonSetter;

public class ContinuousResultMA
{

	Integer nmodels;
	List<ContinuousResult> models;
	Integer distNumE;
	List<Double> postProbs;
	List<Double> bmdDist;

	public Integer getNmodels()
	{
		return nmodels;
	}

	public void setNmodels(Integer nmodels)
	{
		this.nmodels = nmodels;
	}

	public List<ContinuousResult> getModels()
	{
		return models;
	}

	public void setModels(List<ContinuousResult> models)
	{
		this.models = models;
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

	public List<Double> getPostProbs()
	{
		return postProbs;
	}

	@JsonSetter("post_probs")
	public void setPostProbs(List<Double> postProbs)
	{
		this.postProbs = postProbs;
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
