package com.sciome.bmdexpress2.commandline;

import java.util.List;

import com.sciome.bmdexpress2.mvp.model.IStatModelProcessable;
import com.sciome.bmdexpress2.mvp.model.prefilter.WilliamsTrendResults;
import com.sciome.bmdexpress2.mvp.presenter.prefilter.WilliamsTrendPresenter;
import com.sciome.bmdexpress2.mvp.viewinterface.prefilter.IWilliamsTrendView;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;

public class WilliamsTrendRunner implements IWilliamsTrendView {

	public WilliamsTrendResults runWilliamsTrendFilter(IStatModelProcessable processableData, double pCutOff,
			boolean multipleTestingCorrection, boolean filterOutControlGenes, boolean useFoldFilter,
			String foldFilterValue, String outputName)
	{
		WilliamsTrendPresenter presenter = new WilliamsTrendPresenter(this, BMDExpressEventBus.getInstance());
		WilliamsTrendResults results = presenter.performWilliamsTrend(processableData, pCutOff,
				multipleTestingCorrection, filterOutControlGenes, useFoldFilter, foldFilterValue);

		if (outputName != null)
			results.setName(outputName);
		return results;

	}
	
	@Override
	public void closeWindow() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initData(List<IStatModelProcessable> processableData, List<IStatModelProcessable> processableDatas) {
		// TODO Auto-generated method stub
		
	}

}
