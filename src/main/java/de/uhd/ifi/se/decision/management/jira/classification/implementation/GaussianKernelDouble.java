package de.uhd.ifi.se.decision.management.jira.classification.implementation;

import com.atlassian.gzipfilter.org.apache.commons.lang.ArrayUtils;
import smile.math.kernel.GaussianKernel;
import smile.math.kernel.MercerKernel;


/**
 * Wraps the PolynomialKernel class of the SMILE library to also accept Double[] instead of only double[].
 */
public class GaussianKernelDouble<T extends Double> implements MercerKernel<Double[]> {

	private GaussianKernel gaussianKernel;

	public GaussianKernelDouble() {
		this.gaussianKernel = new GaussianKernel(1.0);
	}

	@Override
	public double k(Double[] x, Double[] y) {
		return gaussianKernel.k(ArrayUtils.toPrimitive(x),ArrayUtils.toPrimitive(y));
	}
}
