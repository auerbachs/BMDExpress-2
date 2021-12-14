package com.sciome.bmdexpress2.mvp.model.stat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HillResult extends StatResult
{

	private static final long serialVersionUID = -527776055122273597L;
	/**
	 * GeneId
	 */

	private short kFlag;

	public HillResult()
	{
		super();
	}

	public short getkFlag()
	{
		return kFlag;
	}

	public void setkFlag(short kFlag)
	{
		this.kFlag = kFlag;
	}

	@Override
	public List<String> getColumnNames()
	{

		return new ArrayList<String>(Arrays.asList("Hill BMD", "Hill BMDL", "Hill BMDU", "Hill fitPValue",
				"Hill fitLogLikelihood", "Hill AIC", "Hill adverseDirection", "Hill BMD/BMDL", "Flagged Hill",
				"Hill Parameter Intercept", "Hill Parameter v", "Hill Parameter n", "Hill Parameter k",
				"Hill Execution Complete"));

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

		return new ArrayList<Object>(Arrays.asList((this.getBMD()), (this.getBMDL()), (this.getBMDU()),
				(this.getFitPValue()), (this.getFitLogLikelihood()), (this.getAIC()),
				(this.getAdverseDirection()), (this.getBMDdiffBMDL()), (this.getkFlag()), param1, param2,
				param3, param4, this.getSuccess()));

	}

	@Override
	public String toString()
	{
		return "Hill";
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
		double theDose = dose; // Math.log(dose + Math.sqrt(dose * dose + 1.0));
		double nom = curveParameters[base + 1] * Math.pow(theDose, curveParameters[base + 2]);
		double denom = Math.pow(curveParameters[base + 3], curveParameters[base + 2])
				+ Math.pow(theDose, curveParameters[base + 2]);

		return curveParameters[base] + nom / denom;
	}

	@Override
	public String getFormulaText()
	{
		return "intercept + v * dose^n/(k^n + dose^n)";
	}

	@Override
	public String getEquation()
	{
		int base = 0;
		StringBuilder sb = new StringBuilder("RESPONSE = " + curveParameters[base]);
		if (curveParameters[base + 1] >= 0)
		{
			sb.append(" + " + curveParameters[base + 1] + " * DOSE^");
		}
		else
		{
			sb.append(" " + curveParameters[base + 1] + " * DOSE^");
		}

		String paramN = Double.toString(curveParameters[base + 2]);

		if (curveParameters[base + 2] < 0)
		{
			paramN = "(" + curveParameters[base + 2] + ")";
		}

		sb.append(paramN + "/(");

		if (curveParameters[base + 3] >= 0)
		{
			sb.append(curveParameters[base + 3] + "^" + paramN + " + DOSE^" + paramN + ")");
		}
		else
		{
			sb.append("(" + curveParameters[base + 2] + ")^" + paramN + " + DOSE^" + paramN + ")");
		}
		return sb.toString();
	}

}
