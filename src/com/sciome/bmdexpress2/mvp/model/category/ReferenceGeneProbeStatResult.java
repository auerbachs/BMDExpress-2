package com.sciome.bmdexpress2.mvp.model.category;

import java.io.Serializable;
import java.util.List;

import com.sciome.bmdexpress2.mvp.model.refgene.ReferenceGene;
import com.sciome.bmdexpress2.mvp.model.stat.ProbeStatResult;

public class ReferenceGeneProbeStatResult implements Serializable
{

	/**
	 * 
	 */
	private static final long		serialVersionUID	= 4185688837390020975L;
	private ReferenceGene			referenceGene;
	private List<ProbeStatResult>	probeStatResults;
	private AdverseDirectionEnum	adverseDirection;
	private Double					conflictMinCorrelation;

	public ReferenceGene getReferenceGene()
	{
		return referenceGene;
	}

	public void setReferenceGene(ReferenceGene referenceGene)
	{
		this.referenceGene = referenceGene;
	}

	public List<ProbeStatResult> getProbeStatResults()
	{
		return probeStatResults;
	}

	public void setProbeStatResults(List<ProbeStatResult> probeStatResults)
	{
		this.probeStatResults = probeStatResults;
	}

	public Double getConflictMinCorrelation()
	{
		return conflictMinCorrelation;
	}

	public void setConflictMinCorrelation(Double conflictMinCorrelation)
	{
		this.conflictMinCorrelation = conflictMinCorrelation;
	}

	public AdverseDirectionEnum getAdverseDirection()
	{
		return adverseDirection;
	}

	public void setAdverseDirection(AdverseDirectionEnum adverseDirection)
	{
		this.adverseDirection = adverseDirection;
	}

}
