/**
 * InputParameters.java
 *
 * Created 11/9/2010
 * @Author Longlong Yang
 *
 * Copyright � 2010 The Hamner Institute for Health Sciences
 * 6 Davis Drive, P.O. Box 12137, Research Triangle Park, NC USA 27709-2137
 * All rights reserved.
 */

package com.sciome.bmdexpress2.util.bmds;

public class ModelInputParameters
{
	private int			inputType			= 0;
	private int			observations		= 0;
	private int			polyDegree			= 2;
	private int			adversDirection		= 0;
	private int			iterations			= 250;
	private int			bmdlCalculation		= 1;
	private int			bmdCalculation		= 1;
	private int			restrictPolyCoef	= 0;
	private int			restrictN			= 1;
	private int			restrictBetas		= 1;
	private int			restirctPower		= 1;
	private int			append				= 0;
	private int			smooth				= 0;
	private int			bmrType				= 1;
	private int			constantVariance	= 0;
	private int			riskType			= 0;
	private int			initialParams		= 0;

	private double		relFuncConvergence	= 1.0e-8;
	private double		bmrLevel			= 1.349;
	private double		paramConvergence	= 1.0e-8;
	private double		confidence			= 0.95;
	private double		alpha				= -9999;
	private double		rho					= 0;															// depends
																											// on
																											// 'constantVariance'
	private double		negative			= -9999;

	// for Power model
	private double		control				= -9999;
	private double		slope				= -9999;
	private double		power				= -9999;

	// for Hill model
	private double		intercept			= -9999;
	private double		v					= -9999;
	private double		n					= -9999;
	private double		k					= -9999;

	// for Multistage model
	private double		background			= -9999;
	private double		beta1				= -9999;
	private double		beta2				= -9999;

	// threading
	private int			numThreads;
	private int			killTime;

	private String		defNegative			= "-9999";

	private String[][]	columns				= { { "DOSE", "RESPONSE" }, { "DOSE", "NI", "MEAN", "STD" } };

	public int getInputType()
	{
		return inputType;
	}

	public void setInputType(int inputType)
	{
		this.inputType = inputType;
	}

	public int getObservations()
	{
		return observations;
	}

	public void setObservations(int observations)
	{
		this.observations = observations;
	}

	public int getPolyDegree()
	{
		return polyDegree;
	}

	public void setPolyDegree(int polyDegree)
	{
		this.polyDegree = polyDegree;
	}

	public int getAdversDirection()
	{
		return adversDirection;
	}

	public void setAdversDirection(int adversDirection)
	{
		this.adversDirection = adversDirection;
	}

	public int getIterations()
	{
		return iterations;
	}

	public void setIterations(int iterations)
	{
		this.iterations = iterations;
	}

	public int getBmdlCalculation()
	{
		return bmdlCalculation;
	}

	public void setBmdlCalculation(int bmdlCalculation)
	{
		this.bmdlCalculation = bmdlCalculation;
	}

	public int getBmdCalculation()
	{
		return bmdCalculation;
	}

	public void setBmdCalculation(int bmdCalculation)
	{
		this.bmdCalculation = bmdCalculation;
	}

	public int getRestrictPolyCoef()
	{
		return restrictPolyCoef;
	}

	public void setRestrictPolyCoef(int restrictPolyCoef)
	{
		this.restrictPolyCoef = restrictPolyCoef;
	}

	public int getRestrictN()
	{
		return restrictN;
	}

	public void setRestrictN(int restrictN)
	{
		this.restrictN = restrictN;
	}

	public int getRestrictBetas()
	{
		return restrictBetas;
	}

	public void setRestrictBetas(int restrictBetas)
	{
		this.restrictBetas = restrictBetas;
	}

	public int getRestirctPower()
	{
		return restirctPower;
	}

	public void setRestirctPower(int restirctPower)
	{
		this.restirctPower = restirctPower;
	}

	public int getAppend()
	{
		return append;
	}

	public void setAppend(int append)
	{
		this.append = append;
	}

	public int getSmooth()
	{
		return smooth;
	}

	public void setSmooth(int smooth)
	{
		this.smooth = smooth;
	}

	public int getBmrType()
	{
		return bmrType;
	}

	public void setBmrType(int bmrType)
	{
		this.bmrType = bmrType;
	}

	public int getConstantVariance()
	{
		return constantVariance;
	}

	public void setConstantVariance(int constantVariance)
	{
		this.constantVariance = constantVariance;
	}

	public int getRiskType()
	{
		return riskType;
	}

	public void setRiskType(int riskType)
	{
		this.riskType = riskType;
	}

	public int getInitialParams()
	{
		return initialParams;
	}

	public void setInitialParams(int initialParams)
	{
		this.initialParams = initialParams;
	}

	public double getRelFuncConvergence()
	{
		return relFuncConvergence;
	}

	public void setRelFuncConvergence(double relFuncConvergence)
	{
		this.relFuncConvergence = relFuncConvergence;
	}

	public double getBmrLevel()
	{
		return bmrLevel;
	}

	public void setBmrLevel(double bmrLevel)
	{
		this.bmrLevel = bmrLevel;
	}

	public double getParamConvergence()
	{
		return paramConvergence;
	}

	public void setParamConvergence(double paramConvergence)
	{
		this.paramConvergence = paramConvergence;
	}

	public double getConfidence()
	{
		return confidence;
	}

	public void setConfidence(double confidence)
	{
		this.confidence = confidence;
	}

	public double getAlpha()
	{
		return alpha;
	}

	public void setAlpha(double alpha)
	{
		this.alpha = alpha;
	}

	public double getRho()
	{
		return rho;
	}

	public void setRho(double rho)
	{
		this.rho = rho;
	}

	public double getNegative()
	{
		return negative;
	}

	public void setNegative(double negative)
	{
		this.negative = negative;
	}

	public double getControl()
	{
		return control;
	}

	public void setControl(double control)
	{
		this.control = control;
	}

	public double getSlope()
	{
		return slope;
	}

	public void setSlope(double slope)
	{
		this.slope = slope;
	}

	public double getPower()
	{
		return power;
	}

	public void setPower(double power)
	{
		this.power = power;
	}

	public double getIntercept()
	{
		return intercept;
	}

	public void setIntercept(double intercept)
	{
		this.intercept = intercept;
	}

	public double getV()
	{
		return v;
	}

	public void setV(double v)
	{
		this.v = v;
	}

	public double getN()
	{
		return n;
	}

	public void setN(double n)
	{
		this.n = n;
	}

	public double getK()
	{
		return k;
	}

	public void setK(double k)
	{
		this.k = k;
	}

	public double getBackground()
	{
		return background;
	}

	public void setBackground(double background)
	{
		this.background = background;
	}

	public double getBeta1()
	{
		return beta1;
	}

	public void setBeta1(double beta1)
	{
		this.beta1 = beta1;
	}

	public double getBeta2()
	{
		return beta2;
	}

	public void setBeta2(double beta2)
	{
		this.beta2 = beta2;
	}

	public int getNumThreads()
	{
		return numThreads;
	}

	public void setNumThreads(int numThreads)
	{
		this.numThreads = numThreads;
	}

	public String getDefNegative()
	{
		return defNegative;
	}

	public void setDefNegative(String defNegative)
	{
		this.defNegative = defNegative;
	}

	public String[][] getColumns()
	{
		return columns;
	}

	public void setColumns(String[][] columns)
	{
		this.columns = columns;
	}

	public int getKillTime() {
		return killTime;
	}

	public void setKillTime(int killTime) {
		this.killTime = killTime;
	}
}
