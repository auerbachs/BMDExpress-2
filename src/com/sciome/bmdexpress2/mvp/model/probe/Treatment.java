package com.sciome.bmdexpress2.mvp.model.probe;

import java.io.Serializable;

public class Treatment implements Serializable
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 6820414158363926713L;

	String						name;

	Float						dose;

	public Treatment(String name, Float dose)
	{
		super();
		this.name = name;
		this.dose = dose;
	}

	public Treatment()
	{

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
