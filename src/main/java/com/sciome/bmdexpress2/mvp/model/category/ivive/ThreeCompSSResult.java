package com.sciome.bmdexpress2.mvp.model.category.ivive;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonTypeInfo(use = Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@ref")
public class ThreeCompSSResult extends IVIVEResult implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7875855749569665093L;

	@Override
	public String getName()
	{
		// return "ThreeCompSS";
		return "Oral Equivalent Dose";
	}

}
