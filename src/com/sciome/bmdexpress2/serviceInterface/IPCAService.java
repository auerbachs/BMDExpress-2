package com.sciome.bmdexpress2.serviceInterface;

import com.sciome.bmdexpress2.mvp.model.pca.PCAResults;

public interface IPCAService {
	public PCAResults calculatePCA(double[][] numericMatrix, double[] doseVector, String name);
}
