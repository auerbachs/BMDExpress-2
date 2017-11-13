package com.sciome.bmdexpress2.service;

import java.util.ArrayList;
import java.util.List;

import com.sciome.bmdexpress2.mvp.model.category.CategoryAnalysisResults;
import com.sciome.bmdexpress2.mvp.model.info.AnalysisInfo;
import com.sciome.bmdexpress2.mvp.model.stat.BMDResult;
import com.sciome.bmdexpress2.serviceInterface.ICategoryAnalysisService;
import com.sciome.bmdexpress2.shared.CategoryAnalysisEnum;
import com.sciome.bmdexpress2.util.categoryanalysis.CategoryAnalysisParameters;
import com.sciome.bmdexpress2.util.categoryanalysis.CategoryMapTool;
import com.sciome.bmdexpress2.util.categoryanalysis.ICategoryMapToolProgress;

public class CategoryAnalysisService implements ICategoryAnalysisService {

	@Override
	public CategoryAnalysisResults categoryAnalysis(CategoryAnalysisParameters params, BMDResult bmdResult,
			CategoryAnalysisEnum catAnalysisEnum, ICategoryMapToolProgress me) {

		AnalysisInfo analysisInfo = new AnalysisInfo();
		List<String> notes = new ArrayList<>();

		analysisInfo.setNotes(notes);

		CategoryMapTool catMapTool = new CategoryMapTool(params, bmdResult, catAnalysisEnum, me,
				analysisInfo);
		CategoryAnalysisResults categoryAnalysisResults = catMapTool.startAnalyses();
		categoryAnalysisResults.setBmdResult(bmdResult);
		categoryAnalysisResults.setAnalysisInfo(analysisInfo);
		return categoryAnalysisResults;
	}
	
}
