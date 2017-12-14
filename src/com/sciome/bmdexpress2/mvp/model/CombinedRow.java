package com.sciome.bmdexpress2.mvp.model;

import java.util.ArrayList;
import java.util.List;

public class CombinedRow extends BMDExpressAnalysisRow
{

	private Object			object;

	// an object that contains this row. This is useful
	// for things like BMDResult containing probestatresults and you want
	// to get the dose responses for that probe stat results
	private Object			parentObject;
	private List<Object>	row;

	public CombinedRow(Object obj, Object parent)
	{
		object = obj;
		parentObject = parent;
		row = new ArrayList<>();
	}

	@Override
	public Object getObject()
	{
		return object;
	}

	public Object getParentObject()
	{
		return parentObject;
	}

	@Override
	public List<Object> getRow()
	{
		return row;
	}

}