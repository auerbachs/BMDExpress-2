package com.sciome.bmdexpress2.mvp.presenter;

import com.google.common.eventbus.Subscribe;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;
import com.sciome.bmdexpress2.shared.eventbus.project.CloseApplicationRequestEvent;

@SuppressWarnings("restriction")
public abstract class PresenterBase<T>
{

	private T					view;
	private BMDExpressEventBus	eventBus;

	public PresenterBase(T view, BMDExpressEventBus eventBus)
	{

		this.view = view;
		this.eventBus = eventBus;

		eventBus.register(this);
	}

	/*
	 * Public Methods
	 */
	public void close()
	{
		destroy();
	}

	/*
	 * Protected Methods
	 */

	/*
	 * Get the current view
	 */
	protected T getView()
	{
		return view;
	}

	/*
	 * Get the event bus
	 */
	protected BMDExpressEventBus getEventBus()
	{
		return eventBus;
	}

	public void destroy()
	{
		eventBus.unregister(this);
	}

	@Subscribe
	public void onCloseApplicationRequest(CloseApplicationRequestEvent event)
	{

	}

}
