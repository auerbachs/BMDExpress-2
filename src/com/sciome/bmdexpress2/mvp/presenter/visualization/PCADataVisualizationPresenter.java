package com.sciome.bmdexpress2.mvp.presenter.visualization;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.linear.MatrixUtils;

import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisDataSet;
import com.sciome.bmdexpress2.mvp.model.DoseResponseExperiment;
import com.sciome.bmdexpress2.mvp.model.pca.PCAResult;
import com.sciome.bmdexpress2.mvp.model.pca.PCAResults;
import com.sciome.bmdexpress2.mvp.model.probe.ProbeResponse;
import com.sciome.bmdexpress2.mvp.model.probe.Treatment;
import com.sciome.bmdexpress2.mvp.viewinterface.visualization.IDataVisualizationView;
import com.sciome.bmdexpress2.service.PCAService;
import com.sciome.bmdexpress2.serviceInterface.IVisualizationService;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;
import com.sciome.commons.math.PCA.PCA;
import com.sciome.commons.math.PCA.PCA.CovarianceType;

public class PCADataVisualizationPresenter extends DataVisualizationPresenter {

	public PCADataVisualizationPresenter(IDataVisualizationView view, IVisualizationService service,
			BMDExpressEventBus eventBus) {
		super(view, service, eventBus);
	}

	public PCAResults calculatePCA(DoseResponseExperiment doseResponseExperiment) {
		List<ProbeResponse> responses = doseResponseExperiment.getProbeResponses();
		List<Treatment> treatments = doseResponseExperiment.getTreatments();
		double[][] numericMatrix = new double[responses.size()][responses.get(0).getResponses().size()];
		double[] doseVector = new double[treatments.size()];
		
		//Fill numeric matrix
		for(int i = 0; i < numericMatrix.length; i++) {
			for(int j = 0; j < numericMatrix[i].length; j++) {
				numericMatrix[i][j] = responses.get(i).getResponses().get(j);
			}
		}
		//Fill doseVector
		for(int i = 0; i < doseVector.length; i++) {
			doseVector[i] = treatments.get(i).getDose();
		}
		
		PCAService service = new PCAService();
		return service.calculatePCA(numericMatrix, doseVector, doseResponseExperiment.getName());
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
