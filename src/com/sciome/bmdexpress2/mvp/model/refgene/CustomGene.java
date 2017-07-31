package com.sciome.bmdexpress2.mvp.model.refgene;

import java.io.Serializable;

public class CustomGene extends ReferenceGene implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6117190123999368904L;

	@Override
	public ReferenceGeneSource getSource()
	{
		return ReferenceGeneSource.CUSTOM;
	}

}
