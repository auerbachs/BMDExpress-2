package com.sciome.bmdexpress2.shared.eventbus.analysis;

import com.sciome.bmdexpress2.shared.CategoryAnalysisEnum;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBase;

public class CategoryAnalysisRequestEvent extends BMDExpressEventBase<CategoryAnalysisEnum>
{

	public CategoryAnalysisRequestEvent(CategoryAnalysisEnum payload)
	{
		super(payload);
	}
}
