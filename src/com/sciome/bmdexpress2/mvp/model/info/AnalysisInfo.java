package com.sciome.bmdexpress2.mvp.model.info;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonTypeInfo(use = Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@ref")
public class AnalysisInfo implements Serializable
{

	private static final long	serialVersionUID	= 6852936561184606211L;

	private List<String>		notes;
	private Long				id					= null;

	public AnalysisInfo()
	{

	}

	public AnalysisInfo(AnalysisInfo analysisInfo)
	{
		notes = new ArrayList<>();
		for (String note : analysisInfo.getNotes())
			notes.add(note);
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

	public List<String> getNotes()
	{
		return notes;
	}

	public void setNotes(List<String> notes)
	{
		this.notes = notes;
	}

}
