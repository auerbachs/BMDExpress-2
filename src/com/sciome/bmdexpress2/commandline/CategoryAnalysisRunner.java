package com.sciome.bmdexpress2.commandline;

import com.sciome.bmdexpress2.mvp.model.category.CategoryAnalysisResults;
import com.sciome.bmdexpress2.mvp.model.stat.BMDResult;
import com.sciome.bmdexpress2.service.CategoryAnalysisService;
import com.sciome.bmdexpress2.shared.CategoryAnalysisEnum;
import com.sciome.bmdexpress2.util.categoryanalysis.CategoryAnalysisParameters;

public class CategoryAnalysisRunner
{

	public CategoryAnalysisResults runCategoryAnalysis(BMDResult bmdResult,
			CategoryAnalysisEnum catAnalysisEnum, CategoryAnalysisParameters params)
	{
		CategoryAnalysisService service = new CategoryAnalysisService();
		return service.categoryAnalysis(params, bmdResult, catAnalysisEnum, null);
	}

}
