package com.sciome.bmdexpress2.serviceInterface;

import java.util.List;

import com.sciome.bmdexpress2.mvp.model.category.CategoryAnalysisResults;
import com.sciome.bmdexpress2.mvp.model.stat.BMDResult;
import com.sciome.bmdexpress2.shared.CategoryAnalysisEnum;
import com.sciome.bmdexpress2.util.categoryanalysis.CategoryAnalysisParameters;
import com.sciome.bmdexpress2.util.categoryanalysis.ICategoryMapToolProgress;

public interface ICategoryAnalysisService {
	public CategoryAnalysisResults categoryAnalysis(CategoryAnalysisParameters params, BMDResult bmdResult, CategoryAnalysisEnum catAnalysisEnum, 
													ICategoryMapToolProgress me);
}
