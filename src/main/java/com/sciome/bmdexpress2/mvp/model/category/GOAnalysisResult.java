package com.sciome.bmdexpress2.mvp.model.category;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

		headers.add(0, CategoryAnalysisResults.CATEGORY_DESCRIPTION);
		headers.add(0, "GO Level");
		headers.add(0, CategoryAnalysisResults.CATEGORY_ID);

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
			row.add(0, Integer.parseInt(goCatID.getGoLevel()));
			row.add(0, goCatID.getId());
		}

	}

	@Override
	public String toString()
	{
		return categoryIdentifier.toString();
	}

	@JsonIgnore
	@Override
	public Object getObject()
	{
		return this;
	}

}
