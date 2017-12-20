package com.sciome.bmdexpress2.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.linear.MatrixUtils;

import com.sciome.bmdexpress2.mvp.model.pca.PCAResult;
import com.sciome.bmdexpress2.mvp.model.pca.PCAResults;
import com.sciome.bmdexpress2.serviceInterface.IPCAService;
import com.sciome.commons.math.PCA.PCA;
import com.sciome.commons.math.PCA.PCA.CovarianceType;

public class PCAService implements IPCAService{

	@Override
	public PCAResults calculatePCA(double[][] numericMatrix, double[] doseVector, String name) {
		PCA pca = new PCA(MatrixUtils.createRealMatrix(numericMatrix).transpose(), CovarianceType.COVARIANCE, 4);
		PCAResults pcaResults = new PCAResults();
		List<PCAResult> pcaResultList = new ArrayList<PCAResult>();
		for(int i = 0; i < pca.getTransformedData().getRowDimension(); i++) {
			PCAResult singleRow = new PCAResult();
			singleRow.setDosage("" + doseVector[i]);
			List<Float> floatList = new ArrayList<Float>();
			for(int j = 0; j < pca.getTransformedData().getColumnDimension(); j++) {
				floatList.add((float) pca.getTransformedData().getEntry(i, j));
			}
			singleRow.setPrincipleComponents(floatList);
			pcaResultList.add(singleRow);
		}
		pcaResults.setPcaResults(pcaResultList);
		pcaResults.setName(name + "_PCA");
		return pcaResults;
	}
}
