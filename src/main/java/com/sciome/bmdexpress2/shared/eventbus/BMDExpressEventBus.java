package com.sciome.bmdexpress2.shared.eventbus;

import com.google.common.eventbus.EventBus;

public class BMDExpressEventBus extends EventBus
{

	private static BMDExpressEventBus instance = null;

	protected BMDExpressEventBus()
	{
	}

	public static BMDExpressEventBus getInstance()
	{
		if (instance == null)
		{
			instance = new BMDExpressEventBus();
		}
		return instance;
	}
}
