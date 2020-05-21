package com.sciome.bmdexpress2.shared.eventbus.visualizations;

import java.util.List;

import com.sciome.bmdexpress2.mvp.model.category.CategoryAnalysisResults;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBase;

public class ShowCategoryAnalysisResultVisualizationsEvent
		extends BMDExpressEventBase<List<CategoryAnalysisResults>>
{

	public ShowCategoryAnalysisResultVisualizationsEvent(List<CategoryAnalysisResults> payload)
	{
		super(payload);
	}
}
