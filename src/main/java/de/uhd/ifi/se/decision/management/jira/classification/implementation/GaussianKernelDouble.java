package de.uhd.ifi.se.decision.management.jira.classification.implementation;

import com.atlassian.gzipfilter.org.apache.commons.lang.ArrayUtils;

import smile.math.kernel.GaussianKernel;
import smile.math.kernel.MercerKernel;

/**
 * Wraps the PolynomialKernel class of the SMILE library to also accept Double[]
 * instead of only double[].
 */
public class GaussianKernelDouble<T extends Double> implements MercerKernel<Double[]> {

	private static final long serialVersionUID = 1L;
	private GaussianKernel gaussianKernel;

	public GaussianKernelDouble() {
		this.gaussianKernel = new GaussianKernel(1.0);
	}

	@Override
	public double k(Double[] x, Double[] y) {
		return gaussianKernel.k(ArrayUtils.toPrimitive(x), ArrayUtils.toPrimitive(y));
	}

	@Override
	public double[] kg(Double[] x, Double[] y) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MercerKernel<Double[]> of(double[] params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double[] hyperparameters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double[] lo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double[] hi() {
		// TODO Auto-generated method stub
		return null;
	}
}
