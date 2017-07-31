package com.sciome.bmdexpress2.mvp.model.category;

import java.io.Serializable;
import java.util.List;

import com.sciome.bmdexpress2.mvp.model.category.identifier.GOCategoryIdentifier;

public class GOAnalysisResult extends CategoryAnalysisResult implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5751742481718891865L;

	@Override
	public List<String> generateColumnHeader()
	{
		List<String> headers = super.generateColumnHeader();

		headers.add(0, "GO Term Name");
		headers.add(0, "GO Level");
		headers.add(0, "Go Accession");

		return headers;
	}

	@Override
	public void createRowData()
	{
		// do not create the row if it already exists. saves time
		if (row == null)
		{
			GOCategoryIdentifier goCatID = (GOCategoryIdentifier) categoryIdentifier;
			super.createRowData();
			row.add(0, goCatID.getTitle());
			row.add(0, goCatID.getGoLevel());
			row.add(0, goCatID.getId());
		}

	}

	@Override
	public String toString()
	{
		return categoryIdentifier.toString();
	}

}
