package com.sciome.bmdexpress2.shared.eventbus;

public abstract class BMDExpressEventBase<T>
{

	private T payload;

	public BMDExpressEventBase(T payload)
	{
		this.payload = payload;
	}

	public T GetPayload()
	{
		return payload;
	}
}
