package com.sciome.bmdexpress2.commandline.config.prefilter;

public class OriogenConfig extends PrefilterConfig
{
	private Boolean	mpc;
	private Integer	initialBootstraps	= 500;
	private Integer	maxBootstraps		= 1000;
	private Double	s0Adjustment		= 5.0;

	public Boolean getMpc()
	{
		return mpc;
	}

	public void setMpc(Boolean mpc)
	{
		this.mpc = mpc;
	}

	public Integer getInitialBootstraps()
	{
		return initialBootstraps;
	}

	public void setInitialBootstraps(Integer initialBootstraps)
	{
		this.initialBootstraps = initialBootstraps;
	}

	public Integer getMaxBootstraps()
	{
		return maxBootstraps;
	}

	public void setMaxBootstraps(Integer maxBootstraps)
	{
		this.maxBootstraps = maxBootstraps;
	}

	public Double getS0Adjustment()
	{
		return s0Adjustment;
	}

	public void setS0Adjustment(Double s0Adjustment)
	{
		this.s0Adjustment = s0Adjustment;
	}
}
