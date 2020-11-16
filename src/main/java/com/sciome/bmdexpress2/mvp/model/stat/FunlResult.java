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
				"Funl Parameter Intercept", "Funl Parameter v", "Funl Parameter n", "Funl Parameter k",
				"Funl Execution Complete"));

	}

	@Override
	public List<Object> getRow()
	{
		Double param1 = null;
		Double param2 = null;
		Double param3 = null;
		Double param4 = null;
		if (curveParameters != null)
		{
			param1 = curveParameters[0];
			param2 = curveParameters[1];
			param3 = curveParameters[2];
			param4 = curveParameters[3];
		}

		return new ArrayList<Object>(
				Arrays.asList((this.getBMD()), (this.getBMDL()), (this.getBMDU()), (this.getFitPValue()),
						(this.getFitLogLikelihood()), (this.getAIC()), (this.getAdverseDirection()),
						(this.getBMDdiffBMDL()), param1, param2, param3, param4, this.getSuccess()));

	}

	@Override
	public String toString()
	{
		return "Funl";
	}

	@Override
	public List<String> getParametersNames()
	{
		return new ArrayList<String>(Arrays.asList("intercept", "v-parameter", "n-parameter", "k-parameter"));
	}

	@Override
	public double getResponseAt(double dose)
	{
		int base = 0;
		double nom = curveParameters[base + 1] * Math.pow(dose, curveParameters[base + 2]);
		double denom = Math.pow(curveParameters[base + 3], curveParameters[base + 2])
				+ Math.pow(dose, curveParameters[base + 2]);

		return curveParameters[base] + nom / denom;
	}

}
