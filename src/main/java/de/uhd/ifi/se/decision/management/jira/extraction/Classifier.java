package de.uhd.ifi.se.decision.management.jira.extraction;

import com.atlassian.gzipfilter.org.apache.commons.lang.ArrayUtils;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import mst.In;
import smile.classification.SVM;
import smile.math.kernel.BinarySparseGaussianKernel;
import smile.math.kernel.MercerKernel;
import weka.core.SerializationHelper;


import java.io.File;
import java.util.List;

public abstract class Classifier {

    public static final String DEFAULT_PATH = DecisionKnowledgeClassifier.DEFAULT_DIR +
            File.separator;


    protected MercerKernel kernel;
    protected SVM<Double[]> model;
    protected Integer epochs;
    protected boolean modelIsTrained;
    protected Integer numClasses;


    public Classifier(Integer numClasses) {
        this(0.5, 0.5, 3, numClasses);
    }

    public Classifier(SVM<Double[]> svm, Integer numClasses) {
        this(new BinarySparseGaussianKernel(0.5), svm, 3, numClasses);
    }

    public Classifier(SVM<Double[]> svm, Integer epochs, Integer numClasses) {
        this(new BinarySparseGaussianKernel(0.5), svm, epochs, numClasses);
    }

    public Classifier(Double c, Double sigma, Integer epochs, Integer numClasses) {
        this(c, new BinarySparseGaussianKernel(sigma), epochs, numClasses);
    }

    public Classifier(Double c, MercerKernel kernel, Integer epochs, Integer numClasses) {
        this(kernel, new SVM<Double[]>(kernel, c, numClasses, SVM.Multiclass.ONE_VS_ONE), epochs, numClasses);

    }

    public Classifier(MercerKernel kernel, SVM<Double[]> model, Integer epochs, Integer numClasses) {
        this.kernel = kernel;
        this.model = model;
        this.epochs = epochs;
        this.modelIsTrained = false;
        this.numClasses = numClasses;
    }


    /**
     * @param trainingFile
     */
    public abstract void train(File trainingFile);

    /**
     * @param features
     * @param labels
     */
    public void train(Double[][] features, Integer[] labels) {
        for (int i = 0; i < this.epochs; i++) {
            this.model.learn(features, ArrayUtils.toPrimitive(labels));
        }
        this.model.finish();
        this.modelIsTrained = true;
    }

    /**
     * @param features
     * @param label
     */
    public void train(Double[] features, Integer label) {
        this.model.learn(features, label);
        this.modelIsTrained = true;
    }

    /**
     * @param feature
     * @return probabilities of the labels
     */
    public Double[] predictProbabilities(Double[] feature) throws Exception {
        if (this.modelIsTrained) {
            Double[] probabilities = new Double[this.numClasses];
            this.model.predict(feature, ArrayUtils.toPrimitive(probabilities));
            return probabilities;
        } else {
            throw new Exception("Classifier has not been trained!");
        }

    }

    /**
     * @param filePathAndName
     * @throws Exception
     */
    public void saveToFile(String filePathAndName) throws Exception {
        SerializationHelper.write(filePathAndName, this.model);
    }

    /**
     * @throws Exception
     */
    public abstract void saveToFile() throws Exception;

    /**
     * @param filePathAndName
     * @throws Exception
     */
    public void loadFromFile(String filePathAndName) throws Exception {
        this.model = (SVM<Double[]>) SerializationHelper.read(filePathAndName);
    }

    /**
     * @throws Exception
     */
    public abstract void loadFromFile() throws Exception;


}
