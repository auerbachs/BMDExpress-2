package com.sciome.bmdexpress2.util.bmds.shared;

public class PolyModel extends StatModel
{

	private int degree;

	public PolyModel()
	{
		super();
		setName("poly");
	}

	public int getDegree()
	{
		return degree;

	}

	public void setDegree(int degree)
	{
		this.degree = degree;
		if (degree == 1)
		{
			setName("linear");
		}
		else
		{
			setName("poly " + degree);
		}
	}

}
