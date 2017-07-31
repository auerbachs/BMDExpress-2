package com.sciome.bmdexpress2.mvp.model.stat;

import java.io.Serializable;
import java.util.List;

import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisRow;
import com.sciome.charts.annotation.ChartableDataPoint;
import com.sciome.filter.annotation.Filterable;

/*
 * base class for the statistical curve fitting/bmd models being ran on the data.
 */
public abstract class StatResult extends BMDExpressAnalysisRow implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -97859231381250568L;

	private double BMD;
	private double BMDL;
	private double BMDU;
	private double fitPValue;
	private double fitLogLikelihood;
	private double AIC;
	private short adverseDirection;

	public double[] curveParameters;

	@Filterable(key = BMDResult.BMD)
	@ChartableDataPoint(key = BMDResult.BMD)
	public double getBMD()
	{
		return BMD;
	}

	public void setBMD(double bMD)
	{
		BMD = bMD;
	}

	@Filterable(key = BMDResult.BMDL)
	@ChartableDataPoint(key = BMDResult.BMDL)
	public double getBMDL()
	{
		return BMDL;
	}

	public void setBMDL(double bMDL)
	{
		BMDL = bMDL;
	}

	@Filterable(key = BMDResult.BMDU)
	@ChartableDataPoint(key = BMDResult.BMDU)
	public double getBMDU()
	{
		return BMDU;
	}

	public void setBMDU(double bMD)
	{
		BMDU = bMD;
	}

	@Filterable(key = BMDResult.FIT_PVALUE)
	@ChartableDataPoint(key = BMDResult.FIT_PVALUE)
	public double getFitPValue()
	{
		return fitPValue;
	}

	public void setFitPValue(double fitPValue)
	{
		this.fitPValue = fitPValue;
	}

	@Filterable(key = BMDResult.FIT_LOG_LIKELIHOOD)
	@ChartableDataPoint(key = BMDResult.FIT_LOG_LIKELIHOOD)
	public double getFitLogLikelihood()
	{
		return fitLogLikelihood;
	}

	public void setFitLogLikelihood(double fitLogLikelihood)
	{
		this.fitLogLikelihood = fitLogLikelihood;
	}

	public double getAIC()
	{
		return AIC;
	}

	public void setAIC(double aIC)
	{
		AIC = aIC;
	}

	public short getAdverseDirection()
	{
		return adverseDirection;
	}

	public void setAdverseDirection(short adverseDirection)
	{
		this.adverseDirection = adverseDirection;
	}

	public double[] getCurveParameters()
	{
		return curveParameters;
	}

	public void setCurveParameters(double[] curveParameters)
	{
		this.curveParameters = curveParameters;
	}

	@Filterable(key = BMDResult.BMD_BMDL_RATIO)
	@ChartableDataPoint(key = BMDResult.BMD_BMDL_RATIO)
	public double getBMDdiffBMDL()
	{
		return BMD / BMDL;
	}

	@Filterable(key = BMDResult.BMDU_BMDL_RATIO)
	@ChartableDataPoint(key = BMDResult.BMDU_BMDL_RATIO)
	public double getBMDUdiffBMDL()
	{
		if (BMDU == 0)
			return -9999;
		return BMDU / BMDL;
	}

	@Filterable(key = BMDResult.BMDU_BMD_RATIO)
	@ChartableDataPoint(key = BMDResult.BMDU_BMD_RATIO)
	public double getBMDUdiffBMD()
	{
		if (BMDU == 0)
			return -9999;
		return BMDU / BMD;
	}

	public abstract List<String> getColumnNames();

	@Override
	public abstract List<Object> getRow();

	public abstract List<String> getParametersNames();

}
