package com.sciome.bmdexpress2.mvp.model.category.ivive;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@JsonTypeInfo(use = Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@ref")
public class OneCompResult extends IVIVEResult implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1819881696670459616L;

	@Override
	public String getName() {
		return "OneComp";
	}

}