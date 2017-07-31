package com.sciome.bmdexpress2.mvp.model.category;

import java.io.Serializable;
import java.util.List;

import com.sciome.bmdexpress2.mvp.model.category.identifier.GenericCategoryIdentifier;

public class PathwayAnalysisResult extends CategoryAnalysisResult implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7831296139399726937L;

	@Override
	public List<String> generateColumnHeader()
	{
		List<String> headers = super.generateColumnHeader();

		headers.add(0, "Pathway Name");
		headers.add(0, "Pathway ID");

		return headers;
	}

	@Override
	public void createRowData()
	{
		if (row == null)
		{
			GenericCategoryIdentifier catID = (GenericCategoryIdentifier) categoryIdentifier;
			super.createRowData();
			row.add(0, catID.getTitle());
			row.add(0, catID.getId());
		}

	}

	@Override
	public String toString()
	{
		return this.categoryIdentifier.toString();
	}

}
