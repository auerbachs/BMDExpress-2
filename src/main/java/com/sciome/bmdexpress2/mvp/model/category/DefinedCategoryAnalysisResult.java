package com.sciome.bmdexpress2.mvp.model.category;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sciome.bmdexpress2.mvp.model.category.identifier.GenericCategoryIdentifier;

public class DefinedCategoryAnalysisResult extends CategoryAnalysisResult implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4039580979254405490L;

	@Override
	public List<String> generateColumnHeader()
	{
		List<String> headers = super.generateColumnHeader();

		headers.add(0, CategoryAnalysisResults.CATEGORY_DESCRIPTION);
		headers.add(0, CategoryAnalysisResults.CATEGORY_ID);

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

	@JsonIgnore
	@Override
	public Object getObject()
	{
		return this;
	}

}
