package com.sciome.bmdexpress2.mvp.model.stat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FunlResult extends StatResult
{

	private static final long serialVersionUID = -527776055122273597L;

	/**
	 * GeneId
	 */

	public FunlResult()
	{
		super();
	}

	@Override
	public List<String> getColumnNames()
	{

		return new ArrayList<String>(Arrays.asList("Funl BMD", "Funl BMDL", "Funl BMDU", "Funl fitPValue",
				"Funl fitLogLikelihood", "Funl AIC", "Funl adverseDirection", "Funl BMD/BMDL",
				"Funl Parameter 1", "Funl Parameter 2", "Funl Parameter 3", "Funl Parameter 4",
				"Funl Parameter 5", "Funl Parameter 6", "Funl Execution Complete"));

	}

	@Override
	public List<Object> getRow()
	{
		Double param1 = null;
		Double param2 = null;
		Double param3 = null;
		Double param4 = null;
		Double param5 = null;
		Double param6 = null;
		if (curveParameters != null)
		{
			param1 = curveParameters[0];
			param2 = curveParameters[1];
			param3 = curveParameters[2];
			param4 = curveParameters[3];
			param5 = curveParameters[4];
			param6 = curveParameters[5];
		}

		return new ArrayList<Object>(Arrays.asList((this.getBMD()), (this.getBMDL()), (this.getBMDU()),
				(this.getFitPValue()), (this.getFitLogLikelihood()), (this.getAIC()),
				(this.getAdverseDirection()), (this.getBMDdiffBMDL()), param1, param2, param3, param4, param5,
				param6, this.getSuccess()));

	}

	@Override
	public String toString()
	{
		return "Funl";
	}

	@Override
	public List<String> getParametersNames()
	{
		return new ArrayList<String>(
				Arrays.asList("funl 1", "funl 2", "funl 3", "funl 4", "funl 5", "funl 6"));
	}

	@Override
	public double getResponseAt(double dose)
	{
		Double param1 = curveParameters[0];
		Double param2 = curveParameters[1];
		Double param3 = curveParameters[2];
		Double param4 = curveParameters[3];
		Double param5 = curveParameters[4];
		Double param6 = curveParameters[5];

		return param1 + param2 * Math.exp(-(Math.exp(param6)) * Math.pow(param5-dose, 2))
				* (1 / (1 + Math.exp(-(dose - param3) / param4)));
//      a1 + a2 *exp(- exp(a6) * (a5-dose)^2) * (1/(1+exp(-(dose-a3)/a4).  

	}

	@Override
	public String getFormulaText()
	{
		return "A[1] + A[2]*exp(- exp(A[6]) * (A[5]-doses)^2)*(1/(1+exp(-(doses-A[3])/A[4])))";
		
	}

	@Override
	public String getEquation()
	{
		Double param1 = curveParameters[0];
		Double param2 = curveParameters[1];
		Double param3 = curveParameters[2];
		Double param4 = curveParameters[3];
		Double param5 = curveParameters[4];
		Double param6 = curveParameters[5];
		return param1 + "+ " + param2 + "*EXP((DOSE-" + param5 + ")^2*(-" + param6 + "))*(1/(1+EXP(-(DOSE-"
				+ param3 + ")/" + param4 + ")))";
	}

}
