package de.uhd.ifi.se.decision.management.jira.classification;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.classification.preprocessing.PreprocessedData;
import de.uhd.ifi.se.decision.management.jira.classification.preprocessing.Preprocessor;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

/**
 * Tries to identify decision knowledge in natural language texts using a binary
 * and fine grained supervised classifier.
 */
public class DecisionKnowledgeClassifier {
	private static final Logger LOGGER = LoggerFactory.getLogger(DecisionKnowledgeClassifier.class);

	private BinaryClassifier binaryClassifier;
	private FineGrainedClassifier fineGrainedClassifier;

	/**
	 * @issue What is the best place to store the supervised text classifier related
	 *        data?
	 * @decision Store the data for the text classifier in
	 *           JiraHome/data/condec-plugin/classifier!
	 * @pro Similar as for the git repositories.
	 */
	public static String CLASSIFIER_DIRECTORY = ComponentGetter.PLUGIN_HOME + "classifier" + File.separator;

	public static DecisionKnowledgeClassifier instance;

	private DecisionKnowledgeClassifier() {
		FileManager.copyDefaultTrainingDataToClassifierDirectory();
		Preprocessor.copyDefaultPreprocessingDataToFile();
		loadDefaultBinaryClassifier();
		loadDefaultFineGrainedClassifier();
	}

	public static DecisionKnowledgeClassifier getInstance() {
		if (instance == null) {
			instance = new DecisionKnowledgeClassifier();
		}
		return instance;
	}

	private void loadDefaultBinaryClassifier() {
		this.binaryClassifier = new BinaryClassifier();
		/*
		 * try { //this.binaryClassifier.loadFromFile(); } catch (Exception e) {
		 * LOGGER.error("Could not load a binary classifier from File."); }
		 */
	}

	private void loadDefaultFineGrainedClassifier() {
		this.fineGrainedClassifier = new FineGrainedClassifier(5);
	}

	/**
	 * Determines for a list of strings whether each string is relevant decision
	 * knowledge or not. The classifier needs a list of strings not just one string.
	 *
	 * @param stringsToBeClassified
	 *            list of strings to be checked for relevance.
	 * @return list of boolean values in the same order as the input strings. Each
	 *         value indicates whether a string is relevant (true) or not (false).
	 */
	private boolean makeBinaryPrediction(String stringToBeClassified) {
		double[][] features = preprocess(stringToBeClassified);
		// Make predictions for each nGram; then determine maximum probability of all
		// added together.
		int predictionResult = 0;
		for (double[] feature : features) {
			int currentPredictionResult = binaryClassifier.predict(feature);
			if (currentPredictionResult > predictionResult) {
				predictionResult = currentPredictionResult;
			}
		}
		return predictionResult > 0;
	}

	/**
	 * @param stringsToBeClassified
	 * @return
	 * @see this.makeBinaryDecision
	 */
	public boolean[] makeBinaryPredictions(List<String> stringsToBeClassified) {
		boolean[] binaryPredictionResults = new boolean[stringsToBeClassified.size()];
		for (int i = 0; i < stringsToBeClassified.size(); i++) {
			binaryPredictionResults[i] = makeBinaryPrediction(stringsToBeClassified.get(i));
		}
		return binaryPredictionResults;
	}

	/**
	 * Trains a binary classifier using features and labels given by the parameters
	 * of the method.
	 *
	 * @param features
	 *            features of the instances
	 * @param labels
	 *            labels of the instances
	 */
	public void trainBinaryClassifier(PreprocessedData data) {
		this.binaryClassifier.train(data.preprocessedSentences, data.updatedLabels);
	}

	public KnowledgeType makeFineGrainedPrediction(String stringToBeClassified) {
		double[][] features = preprocess(stringToBeClassified);
		int predictionResult = 0;
		// Make predictions for each nGram; then determine maximum probability of all
		// added together.
		for (double[] feature : features) {
			int currentPredictionResult = fineGrainedClassifier.predict(feature);
			// TODO Calculate prediction that occurred most often
			predictionResult = currentPredictionResult;
		}
		KnowledgeType predictedKnowledgeType = FineGrainedClassifier.mapIndexToKnowledgeType(predictionResult);
		return predictedKnowledgeType;
	}

	/**
	 * @param stringsToBeClassified
	 * @return
	 * @see this.makeBinaryDecision
	 */
	public List<KnowledgeType> makeFineGrainedPredictions(List<String> stringsToBeClassified) {
		try {
			List<KnowledgeType> fineGrainedPredictionResults = new ArrayList<>();
			for (String string : stringsToBeClassified) {
				fineGrainedPredictionResults.add(makeFineGrainedPrediction(string));
			}
			return fineGrainedPredictionResults;
		} catch (Exception e) {
			LOGGER.error("Fine grained classification failed. Message: " + e.getMessage());
		}
		return null;
	}

	/**
	 * @param features
	 * @param labels
	 * @see this.trainBinaryClassifier
	 */
	public void trainFineGrainedClassifier(PreprocessedData data) {
		this.fineGrainedClassifier.train(data.preprocessedSentences, data.updatedLabels);
	}

	/**
	 * Preprocesses a sentence in such a way, that the classifiers can use them for
	 * training or prediction.
	 *
	 * @param stringsToBePreprocessed
	 *            sentence
	 * @return preprocessed sentence in a numerical representation.
	 */
	public double[][] preprocess(String stringsToBePreprocessed) {
		return Preprocessor.getInstance().preprocess(stringsToBePreprocessed);
	}

	public BinaryClassifier getBinaryClassifier() {
		return this.binaryClassifier;
	}

	public FineGrainedClassifier getFineGrainedClassifier() {
		return this.fineGrainedClassifier;
	}

	/**
	 * @return whether or not the classifier is currently training.
	 */
	public boolean isTraining() {
		return this.getFineGrainedClassifier().isCurrentlyTraining()
				|| this.getBinaryClassifier().isCurrentlyTraining();
	}

	/**
	 * @return whether or not the classifier was trained.
	 */
	public boolean isTrained() {
		return getBinaryClassifier().isModelTrained() && getFineGrainedClassifier().isModelTrained();
	}
}
