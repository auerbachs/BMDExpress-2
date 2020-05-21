package com.sciome.bmdexpress2.mvp.presenter.mainstage;

import com.google.common.eventbus.Subscribe;
import com.sciome.bmdexpress2.mvp.presenter.presenterbases.PresenterBase;
import com.sciome.bmdexpress2.mvp.viewinterface.mainstage.ICurveFitView;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;
import com.sciome.bmdexpress2.shared.eventbus.project.BMDProjectLoadedEvent;
import com.sciome.bmdexpress2.shared.eventbus.project.CloseProjectRequestEvent;

public class CurveFitPresenter extends PresenterBase<ICurveFitView>
{

	public CurveFitPresenter(ICurveFitView view, BMDExpressEventBus eventBus)
	{
		super(view, eventBus);
		init();
	}

	private void init()
	{
	}

	@Subscribe
	public void onProjectLoadedEvent(BMDProjectLoadedEvent event)
	{
		getView().closeWindow();
	}

	@Subscribe
	public void onProjectClosedEvent(CloseProjectRequestEvent event)
	{
		getView().closeWindow();
	}
}
