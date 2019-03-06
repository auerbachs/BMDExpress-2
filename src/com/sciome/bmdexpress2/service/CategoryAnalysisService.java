package com.sciome.bmdexpress2.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sciome.bmdexpress2.mvp.model.category.CategoryAnalysisResult;
import com.sciome.bmdexpress2.mvp.model.category.CategoryAnalysisResults;
import com.sciome.bmdexpress2.mvp.model.category.ivive.IVIVEResult;
import com.sciome.bmdexpress2.mvp.model.category.ivive.OneCompResult;
import com.sciome.bmdexpress2.mvp.model.category.ivive.PBTKResult;
import com.sciome.bmdexpress2.mvp.model.category.ivive.ThreeCompResult;
import com.sciome.bmdexpress2.mvp.model.category.ivive.ThreeCompSSResult;
import com.sciome.bmdexpress2.mvp.model.info.AnalysisInfo;
import com.sciome.bmdexpress2.mvp.model.stat.BMDResult;
import com.sciome.bmdexpress2.serviceInterface.ICategoryAnalysisService;
import com.sciome.bmdexpress2.shared.CategoryAnalysisEnum;
import com.sciome.bmdexpress2.util.categoryanalysis.CategoryAnalysisParameters;
import com.sciome.bmdexpress2.util.categoryanalysis.CategoryMapTool;
import com.sciome.bmdexpress2.util.categoryanalysis.ICategoryMapToolProgress;
import com.sciome.bmdexpress2.util.categoryanalysis.IVIVEParameters;
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

		if (params.getIviveParameters() != null && params.getIviveParameters().getModels() != null
				&& !params.getIviveParameters().getModels().isEmpty())
			calculateIVIVE(categoryAnalysisResults, params.getIviveParameters());

		long endTime = System.currentTimeMillis();

		long runTime = endTime - startTime;
		categoryAnalysisResults.getAnalysisInfo().getNotes()
				.add("Total Run Time: " + runTime / 1000 + " seconds");
		return categoryAnalysisResults;
	}

	private void calculateIVIVE(CategoryAnalysisResults results, IVIVEParameters params)
	{
		List<List<Double>> concentrations = new ArrayList<List<Double>>();
		for (CategoryAnalysisResult catResult : results.getCategoryAnalsyisResults())
		{
			List<Double> rowConcentrations = new ArrayList<Double>();

			catResult.calculate5and10Percentiles();

			switch (params.getUnits())
			{
				case nM:
					rowConcentrations
							.add(catResult.getBmdMean() != null ? catResult.getBmdMean() * 1e3 : null);
					rowConcentrations
							.add(catResult.getBmdlMean() != null ? catResult.getBmdlMean() * 1e3 : null);
					rowConcentrations
							.add(catResult.getBmduMean() != null ? catResult.getBmduMean() * 1e3 : null);
					rowConcentrations
							.add(catResult.getBmdMedian() != null ? catResult.getBmdMedian() * 1e3 : null);
					rowConcentrations
							.add(catResult.getBmdlMedian() != null ? catResult.getBmdlMedian() * 1e3 : null);
					rowConcentrations
							.add(catResult.getBmduMedian() != null ? catResult.getBmduMedian() * 1e3 : null);
					rowConcentrations
							.add(catResult.getBmdMinimum() != null ? catResult.getBmdMinimum() * 1e3 : null);
					rowConcentrations.add(
							catResult.getBmdlMinimum() != null ? catResult.getBmdlMinimum() * 1e3 : null);
					rowConcentrations.add(
							catResult.getBmduMinimum() != null ? catResult.getBmduMinimum() * 1e3 : null);
					rowConcentrations.add(catResult.getBmdFifthPercentile() != null
							? catResult.getBmdFifthPercentile() * 1e3
							: null);
					rowConcentrations.add(catResult.getBmdlFifthPercentile() != null
							? catResult.getBmdlFifthPercentile() * 1e3
							: null);
					rowConcentrations.add(catResult.getBmduFifthPercentile() != null
							? catResult.getBmduFifthPercentile() * 1e3
							: null);
					rowConcentrations.add(catResult.getBmdTenthPercentile() != null
							? catResult.getBmdTenthPercentile() * 1e3
							: null);
					rowConcentrations.add(catResult.getBmdlTenthPercentile() != null
							? catResult.getBmdlTenthPercentile() * 1e3
							: null);
					rowConcentrations.add(catResult.getBmduTenthPercentile() != null
							? catResult.getBmduTenthPercentile() * 1e3
							: null);
					break;
				case pM:
					rowConcentrations
							.add(catResult.getBmdMean() != null ? catResult.getBmdMean() * 1e6 : null);
					rowConcentrations
							.add(catResult.getBmdlMean() != null ? catResult.getBmdlMean() * 1e6 : null);
					rowConcentrations
							.add(catResult.getBmduMean() != null ? catResult.getBmduMean() * 1e6 : null);
					rowConcentrations
							.add(catResult.getBmdMedian() != null ? catResult.getBmdMedian() * 1e6 : null);
					rowConcentrations
							.add(catResult.getBmdlMedian() != null ? catResult.getBmdlMedian() * 1e6 : null);
					rowConcentrations
							.add(catResult.getBmduMedian() != null ? catResult.getBmduMedian() * 1e6 : null);
					rowConcentrations
							.add(catResult.getBmdMinimum() != null ? catResult.getBmdMinimum() * 1e6 : null);
					rowConcentrations.add(
							catResult.getBmdlMinimum() != null ? catResult.getBmdlMinimum() * 1e6 : null);
					rowConcentrations.add(
							catResult.getBmduMinimum() != null ? catResult.getBmduMinimum() * 1e6 : null);
					rowConcentrations.add(catResult.getBmdFifthPercentile() != null
							? catResult.getBmdFifthPercentile() * 1e6
							: null);
					rowConcentrations.add(catResult.getBmdlFifthPercentile() != null
							? catResult.getBmdlFifthPercentile() * 1e6
							: null);
					rowConcentrations.add(catResult.getBmduFifthPercentile() != null
							? catResult.getBmduFifthPercentile() * 1e6
							: null);
					rowConcentrations.add(catResult.getBmdTenthPercentile() != null
							? catResult.getBmdTenthPercentile() * 1e6
							: null);
					rowConcentrations.add(catResult.getBmdlTenthPercentile() != null
							? catResult.getBmdlTenthPercentile() * 1e6
							: null);
					rowConcentrations.add(catResult.getBmduTenthPercentile() != null
							? catResult.getBmduTenthPercentile() * 1e6
							: null);
					break;
				case uM:
					rowConcentrations.add(catResult.getBmdMean());
					rowConcentrations.add(catResult.getBmdlMean());
					rowConcentrations.add(catResult.getBmduMean());
					rowConcentrations.add(catResult.getBmdMedian());
					rowConcentrations.add(catResult.getBmdlMedian());
					rowConcentrations.add(catResult.getBmduMedian());
					rowConcentrations.add(catResult.getBmdMinimum());
					rowConcentrations.add(catResult.getBmdlMinimum());
					rowConcentrations.add(catResult.getBmduMinimum());
					rowConcentrations.add(catResult.getBmdFifthPercentile());
					rowConcentrations.add(catResult.getBmdlFifthPercentile());
					rowConcentrations.add(catResult.getBmduFifthPercentile());
					rowConcentrations.add(catResult.getBmdTenthPercentile());
					rowConcentrations.add(catResult.getBmdlTenthPercentile());
					rowConcentrations.add(catResult.getBmduTenthPercentile());
					break;
				default:
					rowConcentrations.add(catResult.getBmdlMean());
					rowConcentrations.add(catResult.getBmduMean());
					rowConcentrations.add(catResult.getBmdMedian());
					rowConcentrations.add(catResult.getBmdlMedian());
					rowConcentrations.add(catResult.getBmduMedian());
					rowConcentrations.add(catResult.getBmdMinimum());
					rowConcentrations.add(catResult.getBmdlMinimum());
					rowConcentrations.add(catResult.getBmduMinimum());
					rowConcentrations.add(catResult.getBmdFifthPercentile());
					rowConcentrations.add(catResult.getBmdlFifthPercentile());
					rowConcentrations.add(catResult.getBmduFifthPercentile());
					rowConcentrations.add(catResult.getBmdTenthPercentile());
					rowConcentrations.add(catResult.getBmdlTenthPercentile());
					rowConcentrations.add(catResult.getBmduTenthPercentile());
					break;
			}

			concentrations.add(rowConcentrations);
		}

		Map<Model, List<List<Double>>> doses = calc_mc_oral_equiv.calcMultiple(concentrations,
				params.getModels(), params.getCompound(), .95, "Human", Units.UM, Units.MOL, true);

		for (int i = 0; i < results.getCategoryAnalsyisResults().size(); i++)
		{
			List<IVIVEResult> iviveResults = new ArrayList<IVIVEResult>();
			for (Model model : params.getModels())
			{
				IVIVEResult result;

				if (model.equals(Model.ONECOMP))
					result = new OneCompResult();
				else if (model.equals(Model.PBTK))
					result = new PBTKResult();
				else if (model.equals(Model.THREECOMP))
					result = new ThreeCompResult();
				else
					result = new ThreeCompSSResult();

				result.setBmdMeanDose(doses.get(model).get(i).get(0));
				result.setBmdlMeanDose(doses.get(model).get(i).get(1));
				result.setBmduMeanDose(doses.get(model).get(i).get(2));
				result.setBmdMedianDose(doses.get(model).get(i).get(3));
				result.setBmdlMedianDose(doses.get(model).get(i).get(4));
				result.setBmduMedianDose(doses.get(model).get(i).get(5));
				result.setBmdMinimumDose(doses.get(model).get(i).get(6));
				result.setBmdlMinimumDose(doses.get(model).get(i).get(7));
				result.setBmduMinimumDose(doses.get(model).get(i).get(8));
				result.setBmdFifthPercentile(doses.get(model).get(i).get(9));
				result.setBmdlFifthPercentile(doses.get(model).get(i).get(10));
				result.setBmduFifthPercentile(doses.get(model).get(i).get(11));
				result.setBmdTenthPercentile(doses.get(model).get(i).get(12));
				result.setBmdlTenthPercentile(doses.get(model).get(i).get(13));
				result.setBmduTenthPercentile(doses.get(model).get(i).get(14));

				iviveResults.add(result);
			}
			results.getCategoryAnalsyisResults().get(i).setIvive(iviveResults);
		}

	}

}
