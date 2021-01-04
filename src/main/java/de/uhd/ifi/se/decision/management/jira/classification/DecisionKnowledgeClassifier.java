package de.uhd.ifi.se.decision.management.jira.classification;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.gzipfilter.org.apache.commons.lang.ArrayUtils;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.util.JiraHome;

import de.uhd.ifi.se.decision.management.jira.classification.implementation.BinaryClassifier;
import de.uhd.ifi.se.decision.management.jira.classification.implementation.FineGrainedClassifier;
import de.uhd.ifi.se.decision.management.jira.classification.preprocessing.PreprocessedData;
import de.uhd.ifi.se.decision.management.jira.classification.preprocessing.Preprocessor;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

/**
 * Tries to identify decision knowledge in natural language texts using a binary
 * and fine grained supervised classifier.
 */
public class DecisionKnowledgeClassifier {

	private BinaryClassifier binaryClassifier;
	private FineGrainedClassifier fineGrainedClassifier;

	/**
	 * @issue What is the best place to store the supervised text classifier related
	 *        data?
	 * @decision Clone git repo to JIRAHome/data/condec-plugin/classifier!
	 */
	public static final String DEFAULT_DIR = ComponentAccessor.getComponentOfType(JiraHome.class).getDataDirectory()
			.getAbsolutePath() + File.separator + "condec-plugin" + File.separator + "classifier" + File.separator;

	protected static final Logger LOGGER = LoggerFactory.getLogger(DecisionKnowledgeClassifier.class);

	private static DecisionKnowledgeClassifier instance;

	private DecisionKnowledgeClassifier() {
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
		double[] predictionResult = new double[this.binaryClassifier.getNumClasses()];
		// Make predictions for each nGram; then determine maximum probability of all
		// added together.
		for (double[] feature : features) {
			double[] currentPredictionResult = binaryClassifier.predictProbabilities(feature);
			if (this.max(currentPredictionResult) > this.max(predictionResult)) {
				predictionResult = currentPredictionResult;
			}
		}
		return BinaryClassifier.isRelevant(predictionResult);
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

	public void makeFineGrainedPrediction(String stringToBeClassified, List<KnowledgeType> fineGrainedPredictionResults,
			int i) throws Exception {
		double[][] features = preprocess(stringToBeClassified);
		double[] predictionResult = new double[this.fineGrainedClassifier.getNumClasses()];
		// Make predictions for each nGram; then determine maximum probability of all
		// added together.
		// ExecutorService taskExecutor = Executors.newFixedThreadPool(features.size());
		for (double[] feature : features) {
			double[] currentPredictionResult = fineGrainedClassifier.predictProbabilities(feature);
			if (this.max(currentPredictionResult) > this.max(predictionResult)) {
				predictionResult = currentPredictionResult;
			}
			/*
			 * IntStream.range(0, predictionResult.length) .forEach(j -> predictionResult[j]
			 * = predictionResult[j] + currentPredictionResult[j]);
			 * 
			 */
		}
		KnowledgeType predictedKnowledgeType = FineGrainedClassifier.getType(predictionResult);
		fineGrainedPredictionResults.set(i, predictedKnowledgeType);

	}

	private Double max(double[] array) {
		return Collections.max(Arrays.asList(ArrayUtils.toObject(array)));
	}

	/**
	 * @param stringsToBeClassified
	 * @return
	 * @see this.makeBinaryDecision
	 */
	public List<KnowledgeType> makeFineGrainedPredictions(List<String> stringsToBeClassified) {
		List<KnowledgeType> fineGrainedPredictionResults = Arrays
				.asList(new KnowledgeType[stringsToBeClassified.size()]);
		ExecutorService taskExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

		try {

			// Classify string instances
			for (int i = 0; i < stringsToBeClassified.size(); i++) {

				final int finalI = i;
				taskExecutor.execute(() -> {
					try {
						makeFineGrainedPrediction(stringsToBeClassified.get(finalI), fineGrainedPredictionResults,
								finalI);
					} catch (Exception e) {
						LOGGER.error(e.getMessage());
					}
				});
			}
			taskExecutor.shutdown();
			taskExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (Exception e) {
			LOGGER.error("Fine grained classification failed. Message: " + e.getMessage());
			return null;
		}

		return fineGrainedPredictionResults;
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
