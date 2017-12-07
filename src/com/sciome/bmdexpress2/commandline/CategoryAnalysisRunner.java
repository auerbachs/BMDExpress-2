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
		//this populates certain transient properties (fold change, prefilter p values...) carried over from prefilter.
		// this is not a good thing, need to figure out how to automatically refresh
		// when running in the GUI, clicking on the bmdResult triggers this method
		bmdResult.refreshTableData();
		CategoryAnalysisService service = new CategoryAnalysisService();
		return service.categoryAnalysis(params, bmdResult, catAnalysisEnum, null);
	}

}
