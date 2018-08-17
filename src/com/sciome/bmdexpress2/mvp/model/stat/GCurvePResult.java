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

	public GCurvePResult()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<String> getColumnNames()
	{
		return new ArrayList<String>(
				Arrays.asList("GCurveP BMD", "GCurveP BMDL", "GCurveP BMDU", "GCurveP fitPValue",
						"GCurveP adverseDirection", "GCurveP BMD/BMDL", "GCurveP Execution Complete"));

	}

	@Override
	public List<Object> getRow()
	{

		return new ArrayList<Object>(
				Arrays.asList((this.getBMD()), (this.getBMDL()), (this.getBMDU()), (this.getFitPValue()),
						(this.getAdverseDirection()), (this.getBMDdiffBMDL()), this.getSuccess()));
	}

	@Override
	public String toString()
	{
		return "GCurveP";
	}

	@Override
	public List<String> getParametersNames()
	{
		return new ArrayList<String>();
	}

}
