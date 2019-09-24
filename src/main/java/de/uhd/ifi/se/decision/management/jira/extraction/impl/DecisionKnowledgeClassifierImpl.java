package de.uhd.ifi.se.decision.management.jira.extraction.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.uhd.ifi.se.decision.management.jira.extraction.Preprocessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uhd.ifi.se.decision.management.jira.extraction.DecisionKnowledgeClassifier;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Class to identify decision knowledge in natural language texts using a binary
 * and fine grained supervised classifiers.
 */
public class DecisionKnowledgeClassifierImpl implements DecisionKnowledgeClassifier {

    private Preprocessor preprocessor;
    private BinaryClassifierImplementation binaryClassifier;
    private FineGrainedClassifierImpl fineGrainedClassifier;

    protected static final Logger LOGGER = LoggerFactory.getLogger(DecisionKnowledgeClassifierImpl.class);

    /**
     * The knowledge types need to be present in the weka classifier. They do not
     * relate to tags like {Issue}.
     */
    private static final String[] KNOWLEDGE_TYPES = {"isAlternative", "isPro", "isCon", "isDecision", "isIssue"};

    public DecisionKnowledgeClassifierImpl(BinaryClassifierImplementation binaryClassifier, FineGrainedClassifierImpl fineGrainedClassifier) {
        this.binaryClassifier = binaryClassifier;
        this.fineGrainedClassifier = fineGrainedClassifier;
        this.preprocessor = new PreprocessorImpl();
    }

    public DecisionKnowledgeClassifierImpl() {
        loadDefaultBinaryClassifier();
        loadDefaultFineGrainedClassifier();
        this.preprocessor = new PreprocessorImpl();

    }

    private void loadDefaultBinaryClassifier() {
        this.binaryClassifier = new BinaryClassifierImplementation();
        try {
            this.binaryClassifier.loadFromFile();
        } catch (Exception e) {
            System.err.println("Could not load a binary classifier from File.");
        }
    }

    private void loadDefaultFineGrainedClassifier() {
        this.fineGrainedClassifier = new FineGrainedClassifierImpl(5);
        try {
            this.fineGrainedClassifier.loadFromFile();
        } catch (Exception e) {
            System.err.println("Could not load a fine-grained classifier from File.");
        }
    }

    @Override
    public List<Boolean> makeBinaryPredictions(List<String> stringsToBeClassified) {
        List<List<Double>> instances = preprocess(stringsToBeClassified);
        List<Boolean> binaryPredictionResults = new ArrayList<Boolean>();

        try {
            for (List<Double> instance : instances) {
                boolean predictionResult = binaryClassifier.predictIsKnowledge(instance);
                binaryPredictionResults.add(predictionResult);
            }
        } catch (Exception e) {
            LOGGER.error("Binary classification failed. Message: " + e.getMessage());
            return new ArrayList<Boolean>();
        }

        return binaryPredictionResults;

    }

    @Override
    public void trainBinaryClassifier(Double[][] features, Integer[] labels) {
        this.binaryClassifier.train(features, labels);
    }

    @Override
    public void updateBinaryClassifier(Double[] feature, Integer label) {
        this.binaryClassifier.train(feature, label);
    }

    public List<KnowledgeType> makeFineGrainedPredictions(List<String> stringsToBeClassified) {
        List<List<Double>> instances = preprocess(stringsToBeClassified);
        List<KnowledgeType> fineGrainedPredictionResults = new ArrayList<KnowledgeType>();

        try {

            // Classify string instances
            for (List<Double> instance : instances) {
                KnowledgeType predictionResult = fineGrainedClassifier.predictKnowledgeType(instance);
                fineGrainedPredictionResults.add(predictionResult);
            }
        } catch (Exception e) {
            LOGGER.error("Fine grained classification failed. Message: " + e.getMessage());
            return null;
        }

        return fineGrainedPredictionResults;
    }

    @Override
    public void trainFineGrainedClassifier(Double[][] features, Integer[] labels) {
        this.fineGrainedClassifier.train(features, labels);
    }

    @Override
    public void updateFineGrainedClassifier(Double[] feature, Integer label) {
        this.fineGrainedClassifier.train(feature, label);
    }

    @Override
    public List<Double> preprocess(String stringsToBePreprocessed) {
        return this.preprocessor.preprocess(stringsToBePreprocessed);
    }

    @Override
    public Map<String, List> preprocess(List<String> stringsToBePreprocessed, List labels) {
        List preprocessedSentences = new ArrayList();
        List updatedLabels = new ArrayList();
        Map preprocessedFeaturesWithLabels = new HashMap();
        for (int i = 0; i< stringsToBePreprocessed.size(); i++) {
            List preprocessedSentence = this.preprocessor.preprocess(stringsToBePreprocessed.get(i));
            for(int _i = 0; _i < preprocessedSentence.size(); _i++){
                updatedLabels.add(labels.get(i));
            }
            preprocessedSentences.addAll(preprocessedSentence);
        }
        preprocessedFeaturesWithLabels.put("labels", updatedLabels);
        preprocessedFeaturesWithLabels.put("features", preprocessedSentences);
        return preprocessedFeaturesWithLabels;
    }


    public List<List<Double>> preprocess(List<String> stringsToBePreprocessed) {
        List preprocessedSentences = new ArrayList();
        for (int i = 0; i< stringsToBePreprocessed.size(); i++) {
            List preprocessedSentence = this.preprocessor.preprocess(stringsToBePreprocessed.get(i));

            preprocessedSentences.addAll(preprocessedSentence);
        }
        return preprocessedSentences;
    }


    @Override
    public void setBinaryClassifier(BinaryClassifierImplementation binaryClassifier) {
        this.binaryClassifier = binaryClassifier;
    }

    @Override
    public BinaryClassifierImplementation getBinaryClassifier() {
        return this.binaryClassifier;
    }

    @Override
    public void setFineGrainedClassifier(FineGrainedClassifierImpl fineGrainedClassifier) {
        this.fineGrainedClassifier = fineGrainedClassifier;
    }

    @Override
    public FineGrainedClassifierImpl getFineGrainedClassifier() {
        return this.fineGrainedClassifier;
    }


    private Instances createDatasetForBinaryClassification(List<String> stringsToBeClassified) {
        List<Attribute> wekaAttributes = createBinaryAttributes();
        Instances datasetForBinaryClassification = new Instances("sentences", (ArrayList<Attribute>) wekaAttributes,
                1000000);

        datasetForBinaryClassification.setClassIndex(datasetForBinaryClassification.numAttributes() - 1);
        for (String string : stringsToBeClassified) {
            DenseInstance instance = new DenseInstance(2);
            instance.setValue(wekaAttributes.get(0), string);
            datasetForBinaryClassification.add(instance);
        }
        return datasetForBinaryClassification;
    }

    private List<Attribute> createBinaryAttributes() {
        List<Attribute> wekaAttributes = new ArrayList<Attribute>();
        wekaAttributes.add(new Attribute("sentence", (List<String>) null));
        wekaAttributes.add(new Attribute("isRelevant", createClassAttributeList()));
        return wekaAttributes;
    }

    private List<String> createClassAttributeList() {
        // Declare Class value with {0,1} as possible values
        List<String> relevantAttribute = new ArrayList<String>();
        relevantAttribute.add("0");
        relevantAttribute.add("1");
        return relevantAttribute;
    }

    @Deprecated
    private Instances createDatasetForFineGrainedClassification(List<String> stringsToBeClassified) {
        List<Attribute> wekaAttributes = new ArrayList<Attribute>();

        // Declare Class value with {0,1} as possible values
        for (int i = 0; i < KNOWLEDGE_TYPES.length; i++) {
            wekaAttributes.add(new Attribute(KNOWLEDGE_TYPES[i], createClassAttributeList(), i));
        }

        // Declare text attribute to hold the message (free form text)
        Attribute attributeText = new Attribute("sentence", (List<String>) null, 5);

        // Declare the feature vector
        wekaAttributes.add(attributeText);
        Instances data = new Instances("sentences: -C 5 ", (ArrayList<Attribute>) wekaAttributes, 1000000);

        for (String string : stringsToBeClassified) {
            Instance instance = new DenseInstance(6);
            instance.setValue(attributeText, string);
            data.add(instance);
        }
        return data;
    }

}
