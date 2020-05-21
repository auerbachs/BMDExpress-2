package com.sciome.bmdexpress2.mvp.model.category.identifier;

import java.io.Serializable;

public class GOCategoryIdentifier extends CategoryIdentifier implements Serializable
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 5804762560280067020L;

	private String				goLevel;

	public String getGoLevel()
	{
		return goLevel;
	}

	public void setGoLevel(String goLevel)
	{
		this.goLevel = goLevel;
	}

	@Override
	public String toString()
	{
		return this.getId() + " " + this.goLevel + " " + this.getTitle();
	}

}
