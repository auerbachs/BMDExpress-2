package com.sciome.bmdexpress2.mvp.presenter.presenterbases;

import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;

public abstract class ServicePresenterBase<S, T> extends PresenterBase<S> {
	private T service;
	
	public ServicePresenterBase(S view, T service, BMDExpressEventBus eventBus) {
		super(view, eventBus);
		this.service = service;
	}
	
	public T getService() {
		return service;
	}
}
