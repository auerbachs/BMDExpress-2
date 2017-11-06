package com.sciome.bmdexpress2.mvp.presenter;

import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;

public abstract class PrefilterPresenterBase<S, T> extends PresenterBase<S> {
	private T service;
	
	public PrefilterPresenterBase(S view, T service, BMDExpressEventBus eventBus) {
		super(view, eventBus);
		this.service = service;
	}
	
	public T getService() {
		return service;
	}
}
