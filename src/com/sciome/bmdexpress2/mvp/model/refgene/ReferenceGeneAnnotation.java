package com.sciome.bmdexpress2.mvp.model.refgene;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.sciome.bmdexpress2.mvp.model.probe.Probe;

@JsonTypeInfo(use = Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@ref")
public class ReferenceGeneAnnotation implements Serializable
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 5430084156366556181L;

	private Probe				probe;
	private List<ReferenceGene>	referenceGenes;

	public Probe getProbe()
	{
		return probe;
	}

	public void setProbe(Probe probe)
	{
		this.probe = probe;
	}

	public List<ReferenceGene> getReferenceGenes()
	{
		return referenceGenes;
	}

	public void setReferenceGenes(List<ReferenceGene> referenceGenes)
	{
		this.referenceGenes = referenceGenes;
	}

}
