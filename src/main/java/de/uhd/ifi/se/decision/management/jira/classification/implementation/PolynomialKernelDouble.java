package de.uhd.ifi.se.decision.management.jira.classification.implementation;

import com.atlassian.gzipfilter.org.apache.commons.lang.ArrayUtils;
import smile.math.kernel.MercerKernel;
import smile.math.kernel.PolynomialKernel;


/**
 * Wraps the PolynomialKernel class of the SMILE library to also accept Double[] instead of only doublr[].
 */
public class PolynomialKernelDouble implements MercerKernel<Double[]> {

    MercerKernel PolyKernel;

    public PolynomialKernelDouble(Integer degree){
        this.PolyKernel = new PolynomialKernel(degree);
    }

    @Override
    public double k(Double[] t1, Double[] t2) {
        return this.PolyKernel.k(ArrayUtils.toPrimitive(t1), ArrayUtils.toPrimitive(t2));
    }
}
