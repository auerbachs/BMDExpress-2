package com.sciome.bmdexpress2.commandline;

import java.util.List;

import com.sciome.bmdexpress2.mvp.model.IStatModelProcessable;
import com.sciome.bmdexpress2.mvp.presenter.prefilter.OneWayANOVAPresenter;
import com.sciome.bmdexpress2.mvp.viewinterface.prefilter.IOneWayANOVAView;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;

public class ANOVARunner implements IOneWayANOVAView
{
	public void runBMDAnalysis(IStatModelProcessable processableData, double pCutOff,
			boolean multipleTestingCorrection, boolean filterOutControlGenes, boolean useFoldFilter,
			String foldFilterValue, boolean isLogTransformation, double baseValue)
	{
		OneWayANOVAPresenter presenter = new OneWayANOVAPresenter(this, BMDExpressEventBus.getInstance());
		presenter.performOneWayANOVA(processableData, pCutOff, multipleTestingCorrection,
				filterOutControlGenes, useFoldFilter, foldFilterValue, isLogTransformation, baseValue);

	}

	@Override
	public void closeWindow()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void initData(List<IStatModelProcessable> processableData,
			List<IStatModelProcessable> processableDatas)
	{
		// TODO Auto-generated method stub

	}

}
