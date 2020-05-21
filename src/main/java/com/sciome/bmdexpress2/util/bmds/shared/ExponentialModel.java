package com.sciome.bmdexpress2.util.bmds.shared;

public class ExponentialModel extends StatModel
{

	private int option;

	public ExponentialModel()
	{
		super();
		setName("exponential");
	}

	public void setOption(int i)
	{
		option = i;
		setName("exponential " + i);

	}

	public int getOption()
	{
		return option;
	}

}
