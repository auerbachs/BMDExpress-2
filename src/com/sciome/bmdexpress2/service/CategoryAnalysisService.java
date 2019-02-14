package com.sciome.bmdexpress2.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sciome.bmdexpress2.mvp.model.category.CategoryAnalysisResult;
import com.sciome.bmdexpress2.mvp.model.category.CategoryAnalysisResults;
import com.sciome.bmdexpress2.mvp.model.category.ivive.IVIVEResult;
import com.sciome.bmdexpress2.mvp.model.info.AnalysisInfo;
import com.sciome.bmdexpress2.mvp.model.stat.BMDResult;
import com.sciome.bmdexpress2.serviceInterface.ICategoryAnalysisService;
import com.sciome.bmdexpress2.shared.CategoryAnalysisEnum;
import com.sciome.bmdexpress2.util.categoryanalysis.CategoryAnalysisParameters;
import com.sciome.bmdexpress2.util.categoryanalysis.CategoryMapTool;
import com.sciome.bmdexpress2.util.categoryanalysis.ICategoryMapToolProgress;
import com.sciome.commons.math.httk.calc.calc_analytic_css.Model;
import com.sciome.commons.math.httk.calc.calc_analytic_css.Units;
import com.sciome.commons.math.httk.calc.calc_mc_oral_equiv;

public class CategoryAnalysisService implements ICategoryAnalysisService
{
	@Override
	public CategoryAnalysisResults categoryAnalysis(CategoryAnalysisParameters params, BMDResult bmdResult,
			CategoryAnalysisEnum catAnalysisEnum, ICategoryMapToolProgress me)
	{
		long startTime = System.currentTimeMillis();
		AnalysisInfo analysisInfo = new AnalysisInfo();
		List<String> notes = new ArrayList<>();

		analysisInfo.setNotes(notes);

		CategoryMapTool catMapTool = new CategoryMapTool(params, bmdResult, catAnalysisEnum, me,
				analysisInfo);
		CategoryAnalysisResults categoryAnalysisResults = catMapTool.startAnalyses();
		categoryAnalysisResults.setBmdResult(bmdResult);
		categoryAnalysisResults.setAnalysisInfo(analysisInfo);

		calculateIVIVE(categoryAnalysisResults, params);
		
		long endTime = System.currentTimeMillis();

		long runTime = endTime - startTime;
		categoryAnalysisResults.getAnalysisInfo().getNotes()
				.add("Total Run Time: " + runTime / 1000 + " seconds");
		return categoryAnalysisResults;
	}

	private void calculateIVIVE(CategoryAnalysisResults results, CategoryAnalysisParameters params) {
		List<List<Double>> concentrations = new ArrayList<List<Double>>();
		for(CategoryAnalysisResult catResult : results.getCategoryAnalsyisResults()) {
			List<Double> rowConcentrations = new ArrayList<Double>();
			
			rowConcentrations.add(catResult.getBmdMean());
			rowConcentrations.add(catResult.getBmdlMean());
			rowConcentrations.add(catResult.getBmduMean());
			rowConcentrations.add(catResult.getBmdMedian());
			rowConcentrations.add(catResult.getBmdlMedian());
			rowConcentrations.add(catResult.getBmduMedian());
			rowConcentrations.add(catResult.getBmdMinimum());
			rowConcentrations.add(catResult.getBmdlMinimum());
			rowConcentrations.add(catResult.getBmduMinimum());
			
			concentrations.add(rowConcentrations);
		}
		 
		Map<Model, List<List<Double>>> doses = calc_mc_oral_equiv.calcMultiple(concentrations, params.getModels(), params.getCompound(), .95, "Human", Units.MGPERL, Units.MOL, true);
		
		for(int i = 0; i < results.getCategoryAnalsyisResults().size(); i++) {
			Map<Model, IVIVEResult> iviveResults = new HashMap<Model, IVIVEResult>();
			for(Model model : params.getModels()) {
				IVIVEResult result = new IVIVEResult();
				result.setBmdMeanDose(doses.get(model).get(i).get(0));
				result.setBmdlMeanDose(doses.get(model).get(i).get(1));
				result.setBmduMeanDose(doses.get(model).get(i).get(2));
				result.setBmdMedianDose(doses.get(model).get(i).get(3));
				result.setBmdlMedianDose(doses.get(model).get(i).get(4));
				result.setBmduMedianDose(doses.get(model).get(i).get(5));
				result.setBmdMinimumDose(doses.get(model).get(i).get(6));
				result.setBmdlMinimumDose(doses.get(model).get(i).get(7));
				result.setBmduMinimumDose(doses.get(model).get(i).get(8));
				
				iviveResults.put(model, result);
			}
			results.getCategoryAnalsyisResults().get(i).setIvive(iviveResults);
		}
		
	}
	
}
