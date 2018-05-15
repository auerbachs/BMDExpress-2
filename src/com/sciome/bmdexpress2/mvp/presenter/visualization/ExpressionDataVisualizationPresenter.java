package com.sciome.bmdexpress2.mvp.presenter.visualization;

import java.util.ArrayList;
import java.util.List;

import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisDataSet;
import com.sciome.bmdexpress2.mvp.model.DoseResponseExperiment;
import com.sciome.bmdexpress2.mvp.model.pca.IntensityResult;
import com.sciome.bmdexpress2.mvp.model.pca.IntensityResults;
import com.sciome.bmdexpress2.mvp.model.pca.PCAResult;
import com.sciome.bmdexpress2.mvp.model.pca.PCAResults;
import com.sciome.bmdexpress2.mvp.viewinterface.visualization.IDataVisualizationView;
import com.sciome.bmdexpress2.service.PCAService;
import com.sciome.bmdexpress2.serviceInterface.IVisualizationService;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;

public class ExpressionDataVisualizationPresenter extends DataVisualizationPresenter {

	public ExpressionDataVisualizationPresenter(IDataVisualizationView view, IVisualizationService service,
			BMDExpressEventBus eventBus) {
		super(view, service, eventBus);
	}

	public PCAResults calculatePCA(DoseResponseExperiment doseResponseExperiment) {
		PCAService pcaService = new PCAService();
		return pcaService.calculatePCA(doseResponseExperiment);
	}
	
	public IntensityResults calculateIntensity(DoseResponseExperiment doseResponseExperiment) {
		IntensityResults intensityResults = new IntensityResults();
		List<IntensityResult> intensityResultList = new ArrayList<IntensityResult>();
		for(int i = 0; i < doseResponseExperiment.getProbeResponses().size(); i++)
		{
			for(int j = 0; j < doseResponseExperiment.getTreatments().size(); j++) {
				IntensityResult row = new IntensityResult();
				row.setName(doseResponseExperiment.getProbeResponses().get(i).getProbe().getId());
				row.setResponse((float)(Math.log10(doseResponseExperiment.getProbeResponses().get(i).getResponses().get(j))/Math.log10(2)));
				intensityResultList.add(row);
			}
		}
		intensityResults.setName(doseResponseExperiment.getName());
		intensityResults.setIntensityResults(intensityResultList);
		return intensityResults;
	}
	
	@Override
	public List<BMDExpressAnalysisDataSet> getResultsFromProject(List<BMDExpressAnalysisDataSet> exclude) {
		List<BMDExpressAnalysisDataSet> returnList = new ArrayList<>();

		if (bmdProject != null && bmdProject.getDoseResponseExperiments() != null)
		{
			for (DoseResponseExperiment doseResponseExperiment : bmdProject.getDoseResponseExperiments())
				returnList.add(doseResponseExperiment);
		}

		for (BMDExpressAnalysisDataSet dataSet : exclude)
		{
			returnList.remove(dataSet);
		}

		return returnList;
	}
}
