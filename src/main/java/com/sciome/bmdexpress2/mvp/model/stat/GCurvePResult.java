package com.sciome.bmdexpress2.mvp.model.stat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GCurvePResult extends StatResult
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2275549172312801367L;

	private double BMDauc;
	private double BMDLauc;
	private double BMDUauc;
	private double BMDwAuc;
	private double BMDLwAuc;
	private double BMDUwAuc;
	private double bmr;
	private List<Float> correctedDoseResponseOffsetValues;
	private List<Float> weightedAverages;
	private List<Float> weightedStdDeviations;
	private Double adjustedControlDoseValue;

	public GCurvePResult()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<String> getColumnNames()
	{
		List<String> l = new ArrayList<String>(Arrays.asList("GCurveP BMD", "GCurveP BMDL", "GCurveP BMDU",
				"GCurveP BMR", "GCurveP fitValue", "GCurveP BMD AUC", "GCurveP BMDL AUC", "GCurveP BMDU AUC",
				"GCurveP BMD wAUC", "GCurveP BMDL wAUC", "GCurveP BMDU wAUC", "GCurveP adverseDirection",
				"GCurveP BMD/BMDL", "GCurveP Execution Complete"));
		if (weightedAverages != null && weightedAverages.size() > 0 && weightedAverages.get(0) != null)
			l.add("GCurveP Baseline");

		if (weightedStdDeviations != null && weightedStdDeviations.size() > 0
				&& weightedStdDeviations.get(0) != null)
			l.add("GCurveP Weighted STD DEV");

		return l;

	}

	@Override
	public List<Object> getRow()
	{

		List<Object> l = new ArrayList<Object>(
				Arrays.asList((this.getBMD()), (this.getBMDL()), (this.getBMDU()), this.getBmr(),
						(this.getFitPValue()), (this.getBMDauc()), (this.getBMDLauc()), (this.getBMDUauc()),
						(this.getBMDwAuc()), (this.getBMDLwAuc()), (this.getBMDUwAuc()),
						(this.getAdverseDirection()), (this.getBMDdiffBMDL()), this.getSuccess()));
		if (weightedAverages != null && weightedAverages.size() > 0 && weightedAverages.get(0) != null)
			l.add(this.weightedAverages.get(0));
		if (weightedStdDeviations != null && weightedStdDeviations.size() > 0
				&& weightedStdDeviations.get(0) != null)
			l.add(this.weightedStdDeviations.get(0));

		return l;
	}

	@Override
	public String toString()
	{
		return "GCurveP";
	}

	public Double getAdjustedControlDoseValue()
	{
		return adjustedControlDoseValue;
	}

	public void setAdjustedControlDoseValue(Double adjustedControlDoseValue)
	{
		this.adjustedControlDoseValue = adjustedControlDoseValue;
	}

	@Override
	public List<String> getParametersNames()
	{
		return new ArrayList<String>();
	}

	public double getBMDauc()
	{
		return BMDauc;
	}

	public void setBMDauc(double bMDauc)
	{
		BMDauc = bMDauc;
	}

	public double getBMDLauc()
	{
		return BMDLauc;
	}

	public void setBMDLauc(double bMDLauc)
	{
		BMDLauc = bMDLauc;
	}

	public double getBMDUauc()
	{
		return BMDUauc;
	}

	public void setBMDUauc(double bMDUauc)
	{
		BMDUauc = bMDUauc;
	}

	public double getBMDwAuc()
	{
		return BMDwAuc;
	}

	public void setBMDwAuc(double bMDwAuc)
	{
		BMDwAuc = bMDwAuc;
	}

	public double getBMDLwAuc()
	{
		return BMDLwAuc;
	}

	public void setBMDLwAuc(double bMDLwAuc)
	{
		BMDLwAuc = bMDLwAuc;
	}

	public double getBMDUwAuc()
	{
		return BMDUwAuc;
	}

	public void setBMDUwAuc(double bMDUwAuc)
	{
		BMDUwAuc = bMDUwAuc;
	}

	public List<Float> getCorrectedDoseResponseOffsetValues()
	{
		return correctedDoseResponseOffsetValues;
	}

	public void setCorrectedDoseResponseOffsetValues(List<Float> correctedDoseResponseValues)
	{
		this.correctedDoseResponseOffsetValues = correctedDoseResponseValues;
	}

	public double getBmr()
	{
		return bmr;
	}

	public void setBmr(double bmr)
	{
		this.bmr = bmr;
	}

	public List<Float> getWeightedAverages()
	{
		return weightedAverages;
	}

	public void setWeightedAverages(List<Float> weightedAverages)
	{
		this.weightedAverages = weightedAverages;
	}

	public List<Float> getWeightedStdDeviations()
	{
		return weightedStdDeviations;
	}

	public void setWeightedStdDeviations(List<Float> weightedStdDeviations)
	{
		this.weightedStdDeviations = weightedStdDeviations;
	}

	@Override
	public double getResponseAt(double d)
	{

		// TODO Auto-generated method stub
		return -9999;
	}

	@Override
	public String getFormulaText()
	{
		return "none";
	}

	@Override
	public String getEquation()
	{
		return "GCurveP";
	}

}
