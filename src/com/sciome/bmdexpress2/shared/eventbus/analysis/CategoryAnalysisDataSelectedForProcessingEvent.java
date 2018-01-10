package com.sciome.bmdexpress2.shared.eventbus.analysis;

import com.sciome.bmdexpress2.mvp.model.category.CategoryAnalysisResults;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBase;

public class CategoryAnalysisDataSelectedForProcessingEvent extends BMDExpressEventBase<CategoryAnalysisResults>
{

	public CategoryAnalysisDataSelectedForProcessingEvent(CategoryAnalysisResults payload)
	{
		super(payload);
	}
}
