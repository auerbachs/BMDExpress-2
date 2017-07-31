package com.sciome.bmdexpress2.mvp.model.info;

import java.io.Serializable;
import java.util.List;

public class AnalysisInfo implements Serializable
{
	private List<String> notes;

	public List<String> getNotes()
	{
		return notes;
	}

	public void setNotes(List<String> notes)
	{
		this.notes = notes;
	}

}
