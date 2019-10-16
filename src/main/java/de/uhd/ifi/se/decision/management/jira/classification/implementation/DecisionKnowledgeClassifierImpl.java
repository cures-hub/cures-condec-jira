package de.uhd.ifi.se.decision.management.jira.classification.implementation;

import com.atlassian.gzipfilter.org.apache.commons.lang.ArrayUtils;
import de.uhd.ifi.se.decision.management.jira.classification.DecisionKnowledgeClassifier;
import de.uhd.ifi.se.decision.management.jira.classification.preprocessing.Preprocessor;
import de.uhd.ifi.se.decision.management.jira.classification.preprocessing.PreprocessorImpl;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

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
    //private static final String[] KNOWLEDGE_TYPES = {"isAlternative", "isPro", "isCon", "isDecision", "isIssue"};

    /*
    public DecisionKnowledgeClassifierImpl(BinaryClassifierImplementation binaryClassifier, FineGrainedClassifierImpl fineGrainedClassifier) {
        this.binaryClassifier = binaryClassifier;
        this.fineGrainedClassifier = fineGrainedClassifier;
        this.preprocessor = new PreprocessorImpl();
    }
     */

    private static DecisionKnowledgeClassifierImpl instance;

    private DecisionKnowledgeClassifierImpl() {
        this(new PreprocessorImpl());
    }

    private DecisionKnowledgeClassifierImpl(Preprocessor pp) {
        loadDefaultBinaryClassifier();
        loadDefaultFineGrainedClassifier();
        this.preprocessor = pp;

    }

    public static DecisionKnowledgeClassifierImpl getInstance(){
        if (instance == null){
            instance = new DecisionKnowledgeClassifierImpl();
        }
        return instance;
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
        List<Boolean> binaryPredictionResults = new ArrayList<Boolean>();

        try {
            // Classify string instances
            for (String stringToBeClassified : stringsToBeClassified) {
                List<List<Double>> features = preprocess(stringToBeClassified);
                double[] predictionResult = new double[this.binaryClassifier.getNumClasses()];
                //Make predictions for each nGram; then determine maximum probability of all added together.
                for (List<Double> feature : features) {
                    double[] currentPredictionResult = binaryClassifier.predictProbabilities(feature.toArray(Double[]::new));
                    IntStream.range(0, predictionResult.length)
                            .forEach(i -> predictionResult[i] = predictionResult[i] + currentPredictionResult[i]);
                }
                boolean predictedIsRelevant = binaryClassifier.isRelevant(ArrayUtils.toObject(predictionResult));

                binaryPredictionResults.add(predictedIsRelevant);
            }
        } catch (Exception e) {
            LOGGER.error("Binary classification failed. Message: " + e.getMessage());
            return new ArrayList<Boolean>();
        }

        return binaryPredictionResults;

    }

    @Override
    public void trainBinaryClassifier(List<List<Double>> features, List<Integer> labels) {
        this.binaryClassifier.train(features, labels);

    }

    @Override
    public List<KnowledgeType> makeFineGrainedPredictions(List<String> stringsToBeClassified) {
        List<KnowledgeType> fineGrainedPredictionResults = new ArrayList<KnowledgeType>();

        try {

            // Classify string instances
            for (String stringToBeClassified : stringsToBeClassified) {
                List<List<Double>> features = preprocess(stringToBeClassified);
                double[] predictionResult = new double[this.fineGrainedClassifier.getNumClasses()];
                //Make predictions for each nGram; then determine maximum probability of all added together.
                for (List<Double> feature : features) {
                    double[] currentPredictionResult = fineGrainedClassifier.predictProbabilities(feature.toArray(Double[]::new));
                    IntStream.range(0, predictionResult.length)
                            .forEach(i -> predictionResult[i] = predictionResult[i] + currentPredictionResult[i]);
                }
                KnowledgeType predictedKnowledgeType = fineGrainedClassifier.mapIndexToKnowledgeType(
                        fineGrainedClassifier.maxAtInArray(ArrayUtils.toObject(predictionResult))
                );
                fineGrainedPredictionResults.add(predictedKnowledgeType);

            }
        } catch (Exception e) {
            LOGGER.error("Fine grained classification failed. Message: " + e.getMessage());
            return null;
        }

        return fineGrainedPredictionResults;
    }

    @Override
    public void trainFineGrainedClassifier(List<List<Double>> features, List<Integer> labels) {
        this.fineGrainedClassifier.train(features, labels);
    }


    @Override
    public List<List<Double>> preprocess(String stringsToBePreprocessed) {
        return this.preprocessor.preprocess(stringsToBePreprocessed);
    }

    @Override
    public Map<String, List> preprocess(List<String> stringsToBePreprocessed, List labels) {
        List preprocessedSentences = new ArrayList();
        List updatedLabels = new ArrayList();
        Map preprocessedFeaturesWithLabels = new HashMap();
        for (int i = 0; i < stringsToBePreprocessed.size(); i++) {
            List preprocessedSentence = this.preprocessor.preprocess(stringsToBePreprocessed.get(i));
            for (int _i = 0; _i < preprocessedSentence.size(); _i++) {
                updatedLabels.add(labels.get(i));
            }
            preprocessedSentences.addAll(preprocessedSentence);
        }
        preprocessedFeaturesWithLabels.put("labels", updatedLabels);
        preprocessedFeaturesWithLabels.put("features", preprocessedSentences);
        return preprocessedFeaturesWithLabels;
    }


    @Override
    public BinaryClassifierImplementation getBinaryClassifier() {
        return this.binaryClassifier;
    }


    @Override
    public FineGrainedClassifierImpl getFineGrainedClassifier() {
        return this.fineGrainedClassifier;
    }

}
