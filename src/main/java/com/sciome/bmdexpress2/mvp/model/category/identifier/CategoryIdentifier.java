package com.sciome.bmdexpress2.mvp.model.category.identifier;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

/*
 * A base class to represent the necessary components of identifying a Category.
 * Currently, GO/Pathway/Defined exist.  Each implement their unique aspects.
 */
@JsonTypeInfo(use = Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonSubTypes({ @Type(value = GenericCategoryIdentifier.class, name = "generic"),
		@Type(value = GOCategoryIdentifier.class, name = "go") })
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@ref")
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
