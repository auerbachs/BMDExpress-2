package com.sciome.bmdexpress2.mvp.model.refgene;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class EntrezGene extends ReferenceGene implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6117190123999368904L;

	@Override
	@JsonIgnore
	public ReferenceGeneSource getSource()
	{
		return ReferenceGeneSource.ENTREZ;
	}

}
