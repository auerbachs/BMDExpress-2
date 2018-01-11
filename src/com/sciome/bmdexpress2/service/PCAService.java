package com.sciome.bmdexpress2.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.linear.MatrixUtils;

import com.sciome.bmdexpress2.mvp.model.DoseResponseExperiment;
import com.sciome.bmdexpress2.mvp.model.pca.PCAResult;
import com.sciome.bmdexpress2.mvp.model.pca.PCAResults;
import com.sciome.bmdexpress2.mvp.model.probe.ProbeResponse;
import com.sciome.bmdexpress2.mvp.model.probe.Treatment;
import com.sciome.bmdexpress2.serviceInterface.IPCAService;
import com.sciome.commons.math.PCA.PCA;
import com.sciome.commons.math.PCA.PCA.CovarianceType;

public class PCAService implements IPCAService {

	@Override
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
		
		PCA pca = new PCA(MatrixUtils.createRealMatrix(numericMatrix).transpose(), CovarianceType.COVARIANCE, 4);
		PCAResults pcaResults = new PCAResults();
		List<PCAResult> pcaResultList = new ArrayList<PCAResult>();
		for(int i = 0; i < pca.getTransformedData().getRowDimension(); i++) {
			PCAResult singleRow = new PCAResult();
			singleRow.setDosage(doseVector[i] + " - " + doseResponseExperiment.getTreatments().get(i).getName());
			List<Float> floatList = new ArrayList<Float>();
			for(int j = 0; j < pca.getTransformedData().getColumnDimension(); j++) {
				floatList.add((float) pca.getTransformedData().getEntry(i, j));
			}
			singleRow.setPrincipleComponents(floatList);
			pcaResultList.add(singleRow);
		}
		
		pcaResults.setName(doseResponseExperiment.getName() + "_PCA");
		pcaResults.setPcaResults(pcaResultList);
		return pcaResults;
	}
}
