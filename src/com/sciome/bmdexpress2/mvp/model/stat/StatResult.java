package com.sciome.bmdexpress2.mvp.model.stat;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisRow;

/*
 * base class for the statistical curve fitting/bmd models being ran on the data.
 */
@JsonTypeInfo(use = Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonSubTypes({ @Type(value = HillResult.class, name = "hill"),
		@Type(value = PolyResult.class, name = "poly"),
		@Type(value = ExponentialResult.class, name = "exponential"),
		@Type(value = PowerResult.class, name = "power"),
		@Type(value = GCurvePResult.class, name = "gcurvep") })
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@ref")
public abstract class StatResult extends BMDExpressAnalysisRow implements Serializable
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -97859231381250568L;

	private double				BMD;
	private double				BMDL;
	private double				BMDU;
	private double				fitPValue;
	private double				fitLogLikelihood;
	private double				AIC;
	private short				adverseDirection;
	private String				success;

	public double[]				curveParameters;

	private Long				id;

	@JsonIgnore
	public String getModel()
	{
		return this.toString();
	}

	@JsonIgnore
	public Long getID()
	{
		return id;
	}

	public void setID(Long id)
	{
		this.id = id;
	}

	public double getBMD()
	{
		if (BMD == -9999 || Double.isInfinite(BMD))
			return Double.NaN;
		return BMD;
	}

	public void setBMD(double bMD)
	{
		if (Double.isInfinite(bMD))
			BMD = Double.NaN;
		else
			BMD = bMD;
	}

	public double getBMDL()
	{
		if (BMDL == -9999 || Double.isInfinite(BMDL))
			return Double.NaN;
		return BMDL;
	}

	public void setBMDL(double bMDL)
	{
		if (Double.isInfinite(bMDL))
			BMDL = Double.NaN;
		else
			BMDL = bMDL;
	}

	public double getBMDU()
	{
		if (BMDU == -9999 || Double.isInfinite(BMDU))
			return Double.NaN;
		return BMDU;
	}

	public void setBMDU(double bMD)
	{
		if (Double.isInfinite(bMD))
			BMDU = Double.NaN;
		else
			BMDU = bMD;
	}

	public double getFitPValue()
	{
		return fitPValue;
	}

	public void setFitPValue(double fitPValue)
	{
		this.fitPValue = fitPValue;
	}

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

	public String getSuccess()
	{
		return success;
	}

	public void setSuccess(String success)
	{
		this.success = success;
	}

	@JsonIgnore
	public double getBMDdiffBMDL()
	{
		if (BMDL == -9999 || BMDL == 0.0)
			return Double.NaN;
		return BMD / BMDL;
	}

	@JsonIgnore
	public double getBMDUdiffBMDL()
	{
		if (BMDU == 0)
			return Double.NaN;
		if (BMDU == -9999 || BMDL == -9999)
			return -Double.NaN;
		return BMDU / BMDL;
	}

	@JsonIgnore
	public double getBMDUdiffBMD()
	{
		if (BMDU == 0)
			return Double.NaN;

		if (BMDU == -9999)
			return Double.NaN;
		return BMDU / BMD;
	}

	@JsonIgnore
	public abstract List<String> getColumnNames();

	@Override
	@JsonIgnore
	public abstract List<Object> getRow();

	@JsonIgnore
	public abstract List<String> getParametersNames();

	@Override
	public Object getObject()
	{
		return this;
	}

}
