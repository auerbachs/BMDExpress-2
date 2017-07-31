package com.sciome.bmdexpress2.mvp.model.category.identifier;

import java.io.Serializable;

/*
 * A base class to represent the necessary components of identifying a Category.
 * Currently, GO/Pathway/Defined exist.  Each implement their unique aspects.
 */
public abstract class CategoryIdentifier implements Serializable
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 7552682549771420023L;
	private String				id;
	private String				title;

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

}
