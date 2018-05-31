package com.sciome.bmdexpress2.mvp.presenter.visualization;

import java.util.ArrayList;
import java.util.List;

import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisDataSet;
import com.sciome.bmdexpress2.mvp.model.DoseResponseExperiment;
import com.sciome.bmdexpress2.mvp.model.pca.IntensityResult;
import com.sciome.bmdexpress2.mvp.model.pca.IntensityResults;
import com.sciome.bmdexpress2.mvp.model.pca.PCAResults;
import com.sciome.bmdexpress2.mvp.model.probe.ProbeResponse;
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
	
	public List<BMDExpressAnalysisDataSet> calculateIntensity(DoseResponseExperiment doseResponseExperiment) {
		List<BMDExpressAnalysisDataSet> intensityResults = new ArrayList<BMDExpressAnalysisDataSet>();
		for(int i = 0; i < doseResponseExperiment.getTreatments().size(); i++)
		{
			IntensityResults singleResult = new IntensityResults();
			List<IntensityResult> intensityResultList = new ArrayList<IntensityResult>();
			int windowSize = 1;
			for(int j = 0; j < doseResponseExperiment.getProbeResponses().size(); j++) {
				ProbeResponse response = doseResponseExperiment.getProbeResponses().get(j);
				IntensityResult row = new IntensityResult();
				if(j < doseResponseExperiment.getProbeResponses().size() - windowSize) {
					float sum = 0;
					for(int k = 0; k < windowSize; k++) {
						sum += (doseResponseExperiment.getProbeResponses().get(j + k).getResponses().get(i));
					}
					row.setResponse(sum / windowSize);
				} else {
					row.setResponse((float)((response.getResponses().get(i))));
				}
				row.setName(response.getProbe().getId());
				intensityResultList.add(row);
			}
			singleResult.setName(doseResponseExperiment.getTreatments().get(i).getName());
			singleResult.setIntensityResults(intensityResultList);
			intensityResults.add(singleResult);
		}
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
