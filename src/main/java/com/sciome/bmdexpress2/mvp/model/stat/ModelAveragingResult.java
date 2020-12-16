package com.sciome.bmdexpress2.mvp.model.stat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModelAveragingResult extends StatResult
{

	private static final long serialVersionUID = -527776055122273597L;

	/**
	 * GeneId
	 */

	private List<StatResult> modelResults;
	private List<Double> posteriorProbabilities;

	public ModelAveragingResult()
	{
		super();
	}

	@Override
	public List<String> getColumnNames()
	{

		List<String> returnList = new ArrayList<String>(Arrays.asList("MA BMD", "MA BMDL", "MA BMDU",
				"MA adverseDirection", "MA BMD/BMDL", "MA Execution Complete"));
		for (StatResult sr : modelResults)
			returnList.add("MA " + sr.getModel() + " Posterior Probability");
		return returnList;

	}

	@Override
	public List<Object> getRow()
	{
		List<Object> returnList = new ArrayList<Object>(
				Arrays.asList((this.getBMD()), (this.getBMDL()), (this.getBMDU()), this.getAdverseDirection(),

						(this.getBMDdiffBMDL()), this.getSuccess()));

		returnList.addAll(posteriorProbabilities);
		return returnList;

	}

	public List<StatResult> getModelResults()
	{
		return modelResults;
	}

	public void setModelResults(List<StatResult> modelResults)
	{
		this.modelResults = modelResults;
	}

	public List<Double> getPosteriorProbabilities()
	{
		return posteriorProbabilities;
	}

	public void setPosteriorProbabilities(List<Double> posteriorProbabilities)
	{
		this.posteriorProbabilities = posteriorProbabilities;
	}

	@Override
	public String toString()
	{
		return "Model Average";
	}

	@Override
	public List<String> getParametersNames()
	{
		return new ArrayList<String>();
	}

	@Override
	public double getResponseAt(double dose)
	{
		int i = 0;
		Double sum = 0.0;
		for (StatResult model : this.modelResults)
		{
			if (posteriorProbabilities.get(i) > 0.0)
				sum += model.getResponseAt(dose) * posteriorProbabilities.get(i);
			i++;
		}

		return sum.doubleValue();

	}

	@Override
	public String getFormulaText()
	{
		return "Model Average";
	}

	@Override
	public String getEquation()
	{
		String returnStr = "";
		int i = 0;
		for (StatResult sr : modelResults)
			returnStr += "(PP " + sr.getModel() + ": " + this.posteriorProbabilities.get(i++) + "),  ";

		return returnStr.replaceAll("\\,  $", "");
	}

}
