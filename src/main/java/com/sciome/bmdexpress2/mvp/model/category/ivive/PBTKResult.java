package com.sciome.bmdexpress2.mvp.model.category.ivive;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@JsonTypeInfo(use = Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@ref")
public class PBTKResult extends IVIVEResult implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7832001964437073931L;

	public String getName() {
		return "PBTK";
	}
}
