package com.sciome.bmdexpress2.mvp.model.refgene;

import java.io.Serializable;

/*
 * super class to represent a "Gene".  Current system only represents Entrez genes, 
 * but in the future it would be nice to be able to add references from other sources.
 */
public abstract class ReferenceGene implements Serializable
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 2581814953187400525L;
	String						id;
	String						geneSymbol;

	public abstract ReferenceGeneSource getSource();

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getGeneSymbol()
	{
		if (geneSymbol == null)
			return "";
		return geneSymbol;
	}

	public void setGeneSymbol(String geneSymbol)
	{
		this.geneSymbol = geneSymbol;
	}

}
