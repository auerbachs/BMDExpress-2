package com.sciome.bmdexpress2.mvp.model.probe;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonTypeInfo(use = Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@ref")
public class Treatment implements Serializable
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 6820414158363926713L;

	String						name;

	Float						dose;

	private Long				id;

	public Treatment(String name, Float dose)
	{
		super();
		this.name = name;
		this.dose = dose;
	}

	public Treatment()
	{

	}

	@JsonIgnore
	public Long getID()
	{
		return id;
	}

	public void setID(Long id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Float getDose()
	{
		return dose;
	}

	public void setDose(Float dose)
	{
		this.dose = dose;
	}

}
