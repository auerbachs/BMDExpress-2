package com.sciome.bmdexpress2.mvp.model.stat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PowerResult extends StatResult
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2275549172312801367L;

	public PowerResult()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<String> getColumnNames()
	{
		return new ArrayList<String>(Arrays.asList("Power BMD", "Power BMDL", "Power BMDU", "Power fitPValue",
				"Power fitLogLikelihood", "Power AIC", "Power adverseDirection", "Power BMD/BMDL",
				"Power Parameter control", "Power Parameter slope", "Power Parameter power", "Power Success"));

	}

	@Override
	public List<Object> getRow()
	{
		Double param1 = null;
		Double param2= null;
		Double param3 = null;
		if(curveParameters !=null)
		{
			param1 = curveParameters[0];
			param2 = curveParameters[1];
			param3 = curveParameters[2];
		}
		return new ArrayList<Object>(Arrays.asList((this.getBMD()), (this.getBMDL()), (this.getBMDU()),
				(this.getFitPValue()), (this.getFitLogLikelihood()), (this.getAIC()),
				(this.getAdverseDirection()), (this.getBMD() / this.getBMDL()), param1,
				param2, param3, this.getSuccess()));
	}

	@Override
	public String toString()
	{
		return "Power";
	}

	@Override
	public List<String> getParametersNames()
	{
		return new ArrayList<String>(Arrays.asList("control", "slope", "power"));
	}

}
