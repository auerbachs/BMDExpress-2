package com.sciome.bmdexpress2.mvp.model.refgene;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

/*
 * super class to represent a "Gene".  Current system only represents Entrez genes, 
 * but in the future it would be nice to be able to add references from other sources.
 */
@JsonTypeInfo(use = Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonSubTypes({ @Type(value = EntrezGene.class, name = "entrez"),
		@Type(value = CustomGene.class, name = "custom") })
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@ref")
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
