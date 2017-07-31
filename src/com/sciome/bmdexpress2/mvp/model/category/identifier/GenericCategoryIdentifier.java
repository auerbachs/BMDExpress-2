package com.sciome.bmdexpress2.mvp.model.category.identifier;

import java.io.Serializable;

public class GenericCategoryIdentifier extends CategoryIdentifier implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5804762560280067020L;

	@Override
	public String toString()
	{
		return this.getId() + " " + this.getTitle();
	}

}
