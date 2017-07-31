package com.sciome.bmdexpress2.mvp.model.probe;

import java.io.Serializable;

public class Probe implements Serializable
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 990818274070898388L;
	String						id;

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	@Override
	public String toString()
	{
		return id;
	}

}
