package com.sciome.bmdexpress2.mvp.model.stat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExponentialResult extends StatResult
{

	private static final long serialVersionUID = -527776055122273597L;

	/**
	 * 
	 */
	private int option;

	public ExponentialResult()
	{
		super();
	}

	public int getOption()
	{
		return option;
	}

	public void setOption(int o)
	{
		this.option = o;
	}

	@Override
	public List<String> getColumnNames()
	{
		String expName = "Exp " + option;
		List<String> returnList = new ArrayList<String>(Arrays.asList(expName + " BMD", expName + " BMDL",
				expName + " BMDU", "Exp " + option + " fitPValue", expName + " fitLogLikelihood",
				expName + " AIC", expName + " adverseDirection", expName + " BMD/BMDL"));

		List<String> parameters = this.getParametersNames();
		for (String parameter : parameters)
			returnList.add(expName + " Parameter " + parameter);

		return returnList;

	}

	@Override
	public List<Object> getRow()
	{
		List<Object> returnList = new ArrayList<Object>(Arrays.asList((this.getBMD()), (this.getBMDL()),
				(this.getBMDU()), (this.getFitPValue()), (this.getFitLogLikelihood()), (this.getAIC()),
				(this.getAdverseDirection()), (this.getBMD() / this.getBMDL())));
		int pcount = 2;
		int start = 1;
		// option 2 and 3 have a "sign" as a parameter too.
		// the first parameter array is sign. this is not needed for option 4 and 5 because
		// the sign is part of the formula.
		if (option == 2 || option == 3)
			start = 0;
		if (option == 3 || option == 4)
			pcount = 3;
		else if (option == 5)
			pcount = 4;
		for (int i = start; i <= pcount; i++)
		{if(curveParameters !=null)
			returnList.add(new Double(this.curveParameters[i]));
		else
			returnList.add(null);
		}

		return returnList;
	}

	@Override
	public String toString()
	{
		return "Exp " + option;
	}

	@Override
	public List<String> getParametersNames()
	{
		if (option == 2)
		{
			return new ArrayList<String>(Arrays.asList("sign", "a", "b"));
		}
		else if (option == 3)
		{
			return new ArrayList<String>(Arrays.asList("sign", "a", "b", "d"));
		}
		else if (option == 4)
		{
			return new ArrayList<String>(Arrays.asList("a", "b", "c"));
		}
		else if (option == 5)
		{
			return new ArrayList<String>(Arrays.asList("a", "b", "c", "d"));
		}
		else
			return new ArrayList<>();
	}

}
