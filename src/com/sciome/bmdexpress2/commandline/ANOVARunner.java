package com.sciome.bmdexpress2.commandline;

import java.util.List;

import com.sciome.bmdexpress2.mvp.model.IStatModelProcessable;
import com.sciome.bmdexpress2.mvp.model.prefilter.OneWayANOVAResults;
import com.sciome.bmdexpress2.mvp.presenter.prefilter.OneWayANOVAPresenter;
import com.sciome.bmdexpress2.mvp.viewinterface.prefilter.IOneWayANOVAView;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;

/*
 * use the presenter to run the one way anova as it would be run from the view.
 */
public class ANOVARunner implements IOneWayANOVAView
{
	public OneWayANOVAResults runBMDAnalysis(IStatModelProcessable processableData, double pCutOff,
			boolean multipleTestingCorrection, boolean filterOutControlGenes, boolean useFoldFilter,
			String foldFilterValue, String outputName)
	{
		OneWayANOVAPresenter presenter = new OneWayANOVAPresenter(this, BMDExpressEventBus.getInstance());
		OneWayANOVAResults results = presenter.performOneWayANOVA(processableData, pCutOff,
				multipleTestingCorrection, filterOutControlGenes, useFoldFilter, foldFilterValue);

		if (outputName != null)
			results.setName(outputName);
		return results;

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
