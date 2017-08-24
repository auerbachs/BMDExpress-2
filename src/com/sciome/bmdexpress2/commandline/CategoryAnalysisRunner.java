package com.sciome.bmdexpress2.commandline;

import java.util.ArrayList;
import java.util.List;

import com.sciome.bmdexpress2.mvp.model.category.CategoryAnalysisResults;
import com.sciome.bmdexpress2.mvp.model.info.AnalysisInfo;
import com.sciome.bmdexpress2.mvp.model.stat.BMDResult;
import com.sciome.bmdexpress2.shared.CategoryAnalysisEnum;
import com.sciome.bmdexpress2.util.categoryanalysis.CategoryAnalysisParameters;
import com.sciome.bmdexpress2.util.categoryanalysis.CategoryMapTool;
import com.sciome.bmdexpress2.util.categoryanalysis.ICategoryMapToolProgress;

public class CategoryAnalysisRunner implements ICategoryMapToolProgress
{

	@Override
	public void updateProgress(String label, double value)
	{
		// TODO Auto-generated method stub

	}

	public CategoryAnalysisResults runCategoryAnalysis(BMDResult bmdResult,
			CategoryAnalysisEnum catAnalysisEnum, CategoryAnalysisParameters params)
	{
		AnalysisInfo analysisInfo = new AnalysisInfo();
		List<String> notes = new ArrayList<>();

		analysisInfo.setNotes(notes);

		CategoryMapTool catMapTool = new CategoryMapTool(params, bmdResult, catAnalysisEnum, this,
				analysisInfo);
		CategoryAnalysisResults categoryAnalysisResults = catMapTool.startAnalyses();

		categoryAnalysisResults.setAnalysisInfo(analysisInfo);

		return categoryAnalysisResults;
	}

}
