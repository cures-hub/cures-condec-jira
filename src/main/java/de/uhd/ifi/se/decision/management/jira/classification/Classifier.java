package de.uhd.ifi.se.decision.management.jira.classification;

import com.atlassian.gzipfilter.org.apache.commons.lang.ArrayUtils;
import de.uhd.ifi.se.decision.management.jira.classification.implementation.PolynomialKernelDouble;
import smile.classification.SVM;
import smile.math.kernel.MercerKernel;
import weka.core.SerializationHelper;


import java.io.File;
import java.util.List;

public abstract class Classifier {

    public static final String DEFAULT_PATH = DecisionKnowledgeClassifier.DEFAULT_DIR +
            File.separator;


    protected SVM<Double[]> model;
    private Integer epochs;
    private boolean modelIsTrained;
    private Integer numClasses;


    public Classifier(Integer numClasses) {
        this(0.5, 2, 3, numClasses);
    }

    public Classifier(SVM<Double[]> svm, Integer numClasses) {
        this(new PolynomialKernelDouble(2), svm, 3, numClasses);
    }

    public Classifier(SVM<Double[]> svm, Integer epochs, Integer numClasses) {
        this(new PolynomialKernelDouble(2), svm, epochs, numClasses);
    }

    public Classifier(Double c, Integer degrees, Integer epochs, Integer numClasses) {
        this(c, new PolynomialKernelDouble(degrees), epochs, numClasses);
    }

    public Classifier(Double c, MercerKernel kernel, Integer epochs, Integer numClasses) {
        this(kernel, new SVM<Double[]>(kernel, c, numClasses), epochs, numClasses);

    }

    public Classifier(MercerKernel kernel, SVM<Double[]> model, Integer epochs, Integer numClasses) {
        this.model = model;
        this.epochs = epochs;
        this.modelIsTrained = false;
        this.numClasses = numClasses;
    }


    /**
     * Trains the model using supervised training data, features and labels.
     * @param features
     * @param labels
     */
    public void train(Double[][] features, Integer[] labels) {
        for (int i = 0; i < this.epochs; i++) {
            this.model.learn(features, ArrayUtils.toPrimitive(labels));
        }
        this.model.trainPlattScaling(features, ArrayUtils.toPrimitive(labels));

        this.model.finish();
        this.modelIsTrained = true;
    }

    /**
     * Trains the model using supervised training data, features and labels.
     *
     * @param features
     * @param labels
     */
    public void train(List<List<Double>> features, List<Integer> labels) {
        Double[][] featuresArray = new Double[features.size()][features.get(0).size()];
        for (int i = 0; i < features.size(); i++) {
            featuresArray[i] = features.get(i).toArray(Double[]::new);
        }
        this.train(featuresArray,
                labels.toArray(Integer[]::new));
    }

    /**
     * Trains the model using supervised training data, features and labels.
     *
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
    public double[] predictProbabilities(Double[] feature) throws Exception {
        if (this.modelIsTrained) {
            double[] probabilities = new double[this.numClasses];
            this.model.predict(feature, probabilities);
            return probabilities;
        } else {
            throw new InstantiationError("Classifier has not been trained!");
        }

    }

    /**
     * Saves model to a file. So that it can be loaded at a later time.
     *
     * @param filePathAndName
     * @throws Exception
     */
    public void saveToFile(String filePathAndName) throws Exception {
        SerializationHelper.write(filePathAndName, this.model);
    }

    /**
     * Saves model to a file. So that it can be loaded at a later time.
     *
     * @throws Exception
     */
    public abstract void saveToFile() throws Exception;

    /**
     * Loads pre-trained model from file.
     *
     * @param filePathAndName
     * @throws Exception
     */
    public void loadFromFile(String filePathAndName) throws Exception {
        SVM<Double[]> oldModel = this.model;
        try {
            this.model = (SVM<Double[]>) SerializationHelper.read(filePathAndName);
        } catch (Exception e) {
            this.model = oldModel;
        }

    }

    /**
     * Loads pre-trained model from file.
     *
     * @throws Exception
     */
    public abstract void loadFromFile() throws Exception;

    public Integer getNumClasses() {
        return numClasses;
    }
}

