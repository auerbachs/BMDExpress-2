package com.sciome.bmdexpress2.commandline;

import com.sciome.bmdexpress2.mvp.model.IStatModelProcessable;
import com.sciome.bmdexpress2.mvp.model.stat.BMDResult;
import com.sciome.bmdexpress2.service.BMDAnalysisService;
import com.sciome.bmdexpress2.util.bmds.IBMDSToolProgress;
import com.sciome.bmdexpress2.util.curvep.GCurvePInputParameters;

/*
 * System.out.print
 */
public class NonParametricAnalysisRunner implements IBMDSToolProgress
{

	public BMDResult runBMDAnalysis(IStatModelProcessable processableData,
			GCurvePInputParameters inputParameters)
	{
		BMDAnalysisService service = new BMDAnalysisService();
		return service.bmdAnalysisGCurveP(processableData, inputParameters, this);
	}

	@Override
	public void updateProgress(String label, double value)
	{
		// System.out.print(StringUtils.rightPad(label + ": " + value, 80, " ") + "\r");

	}

	@Override
	public void clearProgress()
	{
		// TODO Auto-generated method stub

	}
}
