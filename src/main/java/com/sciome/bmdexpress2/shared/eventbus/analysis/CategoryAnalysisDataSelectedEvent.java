package com.sciome.bmdexpress2.shared.eventbus.analysis;

import com.sciome.bmdexpress2.mvp.model.category.CategoryAnalysisResults;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBase;

public class CategoryAnalysisDataSelectedEvent extends BMDExpressEventBase<CategoryAnalysisResults>
{

	public CategoryAnalysisDataSelectedEvent(CategoryAnalysisResults payload)
	{
		super(payload);
	}
}
