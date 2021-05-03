package com.sciome.bmdexpress2.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sciome.bmdexpress2.mvp.model.category.CategoryAnalysisResult;
import com.sciome.bmdexpress2.mvp.model.category.CategoryAnalysisResults;
import com.sciome.bmdexpress2.mvp.model.category.ivive.ForwardPKResult;
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
import com.sciome.bmdexpress2.util.categoryanalysis.IVIVEParameters.ConcentrationUnits;
import com.sciome.commons.math.httk.calc.calc_analytic_css.Model;
import com.sciome.commons.math.httk.calc.calc_analytic_css.Units;
import com.sciome.commons.math.httk.calc.calc_mc_oral_equiv;
import com.sciome.commons.math.httk.calc.get_cmax_bycas;

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

		System.out.println(params.getIviveParameters());
		if (params.getIviveParameters() != null) {
			String species = params.getIviveParameters().getSpecies();
			categoryAnalysisResults.getAnalysisInfo().getNotes().add("IVIVE Compound Name: " + params.getIviveParameters().getCompound().getName());
			categoryAnalysisResults.getAnalysisInfo().getNotes().add("IVIVE Compound CASRN: " + params.getIviveParameters().getCompound().getCAS());
			categoryAnalysisResults.getAnalysisInfo().getNotes().add("IVIVE Compound SMILES: " + params.getIviveParameters().getCompound().getSMILES());
			categoryAnalysisResults.getAnalysisInfo().getNotes().add("IVIVE Compound MW: " + params.getIviveParameters().getCompound().getMW());
			if(params.getIviveParameters().getCompound().getMWSource() != null)
				categoryAnalysisResults.getAnalysisInfo().getNotes().add("IVIVE Compound MW Source: " + params.getIviveParameters().getCompound().getMWSource().getName());
			categoryAnalysisResults.getAnalysisInfo().getNotes().add("IVIVE Compound LogP: " + params.getIviveParameters().getCompound().getLogP());
			if(params.getIviveParameters().getCompound().getLogPSource() != null)
				categoryAnalysisResults.getAnalysisInfo().getNotes().add("IVIVE Compound LogP Source: " + params.getIviveParameters().getCompound().getLogPSource().getName());
			categoryAnalysisResults.getAnalysisInfo().getNotes().add("IVIVE Compound pKa Donor: " + params.getIviveParameters().getCompound().getpKaDonors());
			if(params.getIviveParameters().getCompound().getpKaDonorsSource() != null)
				categoryAnalysisResults.getAnalysisInfo().getNotes().add("IVIVE Compound pKa Donor Source: " + params.getIviveParameters().getCompound().getpKaDonorsSource().getName());
			categoryAnalysisResults.getAnalysisInfo().getNotes().add("IVIVE Compound pKa Acceptor: " + params.getIviveParameters().getCompound().getpKaAcceptors());
			if(params.getIviveParameters().getCompound().getpKaAcceptorsSource() != null)
				categoryAnalysisResults.getAnalysisInfo().getNotes().add("IVIVE Compound pKa Acceptor Source: " + params.getIviveParameters().getCompound().getpKaAcceptorsSource().getName());
			categoryAnalysisResults.getAnalysisInfo().getNotes().add("IVIVE Compound CLint: " + params.getIviveParameters().getCompound().getInVitroParam(species, "Clint"));
//			if(params.getIviveParameters().getCompound().getIVdataSourceForSpecies(species, "Clint") != null)
//				categoryAnalysisResults.getAnalysisInfo().getNotes().add("IVIVE Compound CLint Source: " + params.getIviveParameters().getCompound().getIVdataSourceForSpecies(species, "Clint").getName());
			categoryAnalysisResults.getAnalysisInfo().getNotes().add("IVIVE Compound Fup: " + params.getIviveParameters().getCompound().getInVitroParam(species, "Funbound.plasma"));
//			if(params.getIviveParameters().getCompound().getIVdataSourceForSpecies(species, "Funbound.plasma") != null)
//				categoryAnalysisResults.getAnalysisInfo().getNotes().add("IVIVE Compound Fup Source: " + params.getIviveParameters().getCompound().getIVdataSourceForSpecies(species, "Funbound.plasma").getName());
			categoryAnalysisResults.getAnalysisInfo().getNotes().add("IVIVE Species: " + params.getIviveParameters().getSpecies());
			if(params.getIviveParameters().isInvivo()) {
				calculateInVivoToInVitro(categoryAnalysisResults, bmdResult, params.getIviveParameters());
				categoryAnalysisResults.getAnalysisInfo().getNotes().add("IVIVE Dose Spacing: " + params.getIviveParameters().getDoseSpacing());
				categoryAnalysisResults.getAnalysisInfo().getNotes().add("IVIVE Final Time: " + params.getIviveParameters().getFinalTime());
				categoryAnalysisResults.getAnalysisInfo().getNotes().add("IVIVE Input Units: " + params.getIviveParameters().getConcentrationUnits());
				categoryAnalysisResults.getAnalysisInfo().getNotes().add("IVIVE Output Units: " + params.getIviveParameters().getDoseUnits());
			} else {
				calculateInVitroToInVivo(categoryAnalysisResults, params.getIviveParameters());
				categoryAnalysisResults.getAnalysisInfo().getNotes().add("IVIVE Quantile: " + params.getIviveParameters().getQuantile());
				categoryAnalysisResults.getAnalysisInfo().getNotes().add("IVIVE Input Units: " + params.getIviveParameters().getDoseUnits());
				categoryAnalysisResults.getAnalysisInfo().getNotes().add("IVIVE Output Units: " + params.getIviveParameters().getConcentrationUnits());
			}
		}
		
		if (params.getDeduplicateGeneSets())
			categoryAnalysisResults.deDuplicateGeneSets();

		long endTime = System.currentTimeMillis();

		long runTime = endTime - startTime;
		categoryAnalysisResults.getAnalysisInfo().getNotes()
				.add("Total Run Time: " + runTime / 1000 + " seconds");
		return categoryAnalysisResults;
	}

	private void calculateInVitroToInVivo(CategoryAnalysisResults results, IVIVEParameters params)
	{
		List<List<Double>> concentrations = new ArrayList<List<Double>>();
		for (CategoryAnalysisResult catResult : results.getCategoryAnalsyisResults())
		{
			List<Double> rowConcentrations = new ArrayList<Double>();

			catResult.calculate5and10Percentiles();

			double multiplicationFactor = 1.0;
			if(params.getConcentrationUnits().equals(ConcentrationUnits.nM)) {
				multiplicationFactor = 1.0 / 1e3;
			} else if(params.getConcentrationUnits().equals(ConcentrationUnits.pM)) {
				multiplicationFactor = 1.0 / 1e6;
			}
			rowConcentrations.add(catResult.getBmdMean() != null ? catResult.getBmdMean() * multiplicationFactor : null);
			rowConcentrations.add(catResult.getBmdlMean() != null ? catResult.getBmdlMean() * multiplicationFactor : null);
			rowConcentrations.add(catResult.getBmduMean() != null ? catResult.getBmduMean() * multiplicationFactor : null);
			rowConcentrations.add(catResult.getBmdMedian() != null ? catResult.getBmdMedian() * multiplicationFactor : null);
			rowConcentrations.add(catResult.getBmdlMedian() != null ? catResult.getBmdlMedian() * multiplicationFactor : null);
			rowConcentrations.add(catResult.getBmduMedian() != null ? catResult.getBmduMedian() * multiplicationFactor : null);
			rowConcentrations.add(catResult.getBmdMinimum() != null ? catResult.getBmdMinimum() * multiplicationFactor : null);
			rowConcentrations.add(catResult.getBmdlMinimum() != null ? catResult.getBmdlMinimum() * multiplicationFactor : null);
			rowConcentrations.add(catResult.getBmduMinimum() != null ? catResult.getBmduMinimum() * multiplicationFactor : null);
			rowConcentrations.add(catResult.getBmdFifthPercentile() != null
					? catResult.getBmdFifthPercentile() * multiplicationFactor
					: null);
			rowConcentrations.add(catResult.getBmdlFifthPercentile() != null
					? catResult.getBmdlFifthPercentile() * multiplicationFactor
					: null);
			rowConcentrations.add(catResult.getBmduFifthPercentile() != null
					? catResult.getBmduFifthPercentile() * multiplicationFactor
					: null);
			rowConcentrations.add(catResult.getBmdTenthPercentile() != null
					? catResult.getBmdTenthPercentile() * multiplicationFactor
					: null);
			rowConcentrations.add(catResult.getBmdlTenthPercentile() != null
					? catResult.getBmdlTenthPercentile() * multiplicationFactor
					: null);
			rowConcentrations.add(catResult.getBmduTenthPercentile() != null
					? catResult.getBmduTenthPercentile() * multiplicationFactor
					: null);
			

			concentrations.add(rowConcentrations);
		}
		Map<Model, List<List<Double>>> doses = calc_mc_oral_equiv.calcMultiple(concentrations,
				params.getModels(), params.getCompound(), params.getQuantile(), params.getSpecies(), Units.UM, params.getDoseUnits(), true);

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

	private void calculateInVivoToInVitro(CategoryAnalysisResults results, BMDResult bmdResult, IVIVEParameters params) {
		List<List<Double>> doses = new ArrayList<List<Double>>();
		for (CategoryAnalysisResult catResult : results.getCategoryAnalsyisResults())
		{
			List<Double> rowDoses = new ArrayList<Double>();

			catResult.calculate5and10Percentiles();
			
			rowDoses.add(catResult.getBmdMean());
			rowDoses.add(catResult.getBmdlMean());
			rowDoses.add(catResult.getBmduMean());
			rowDoses.add(catResult.getBmdMedian());
			rowDoses.add(catResult.getBmdlMedian());
			rowDoses.add(catResult.getBmduMedian());
			rowDoses.add(catResult.getBmdMinimum());
			rowDoses.add(catResult.getBmdlMinimum());
			rowDoses.add(catResult.getBmduMinimum());
			rowDoses.add(catResult.getBmdFifthPercentile());
			rowDoses.add(catResult.getBmdlFifthPercentile());
			rowDoses.add(catResult.getBmduFifthPercentile());
			rowDoses.add(catResult.getBmdTenthPercentile());
			rowDoses.add(catResult.getBmdlTenthPercentile());
			rowDoses.add(catResult.getBmduTenthPercentile());
			
			doses.add(rowDoses);
		}


		for (int i = 0; i < results.getCategoryAnalsyisResults().size(); i++)
		{
			List<Double> cmax = new ArrayList<Double>();
			for(int j = 0; j < doses.get(i).size(); j++) {
				if(doses.get(i).get(j) != null) {
					cmax.add(get_cmax_bycas.calc(params.getCompound(), doses.get(i).get(j), 
						bmdResult.getDoseResponseExperiment().getUniqueDoses().size()-1, //subtract 1 to ignore the control dose.
						(int)params.getDoseSpacing(), params.getFinalTime(), params.getSpecies()));
				} else {
					cmax.add(null);
				}
			}
			List<IVIVEResult> iviveResults = new ArrayList<IVIVEResult>();
			IVIVEResult result = new ForwardPKResult();

			result.setBmdMeanDose(cmax.get(0));
			result.setBmdlMeanDose(cmax.get(1));
			result.setBmduMeanDose(cmax.get(2));
			result.setBmdMedianDose(cmax.get(3));
			result.setBmdlMedianDose(cmax.get(4));
			result.setBmduMedianDose(cmax.get(5));
			result.setBmdMinimumDose(cmax.get(6));
			result.setBmdlMinimumDose(cmax.get(7));
			result.setBmduMinimumDose(cmax.get(8));
			result.setBmdFifthPercentile(cmax.get(9));
			result.setBmdlFifthPercentile(cmax.get(10));
			result.setBmduFifthPercentile(cmax.get(11));
			result.setBmdTenthPercentile(cmax.get(12));
			result.setBmdlTenthPercentile(cmax.get(13));
			result.setBmduTenthPercentile(cmax.get(14));

			iviveResults.add(result);
			results.getCategoryAnalsyisResults().get(i).setIvive(iviveResults);
		}
	}
	
}
