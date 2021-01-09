package de.uhd.ifi.se.decision.management.jira.classification;

import static java.util.stream.Collectors.toList;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.gzipfilter.org.apache.commons.lang.ArrayUtils;

import de.uhd.ifi.se.decision.management.jira.classification.preprocessing.Preprocessor;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.PartOfJiraIssueText;
import smile.validation.metric.ClassificationMetric;
import smile.validation.metric.FScore;

/**
 * Class responsible to train the supervised text classifier. For this purpose,
 * the project admin needs to create and select a training data file.
 */
public class ClassifierTrainer implements EvaluableClassifier {
	protected static final Logger LOGGER = LoggerFactory.getLogger(ClassifierTrainer.class);

	protected TrainingData trainingData;
	protected String projectKey;

	public ClassifierTrainer(String projectKey) {
		new File(DecisionKnowledgeClassifier.CLASSIFIER_DIRECTORY).mkdirs();
		this.projectKey = projectKey;
		trainingData = new TrainingData();
	}

	public ClassifierTrainer(String projectKey, String fileName) {
		new File(DecisionKnowledgeClassifier.CLASSIFIER_DIRECTORY).mkdirs();
		this.projectKey = projectKey;
		if (fileName == null || fileName.isEmpty()) {
			return;
		}
		trainingData = new TrainingData(fileName);
	}

	/**
	 * Trains the Classifier with the Data from the Database that was set and
	 * validated from the user. Creates a new model Files that can be used to
	 * classify the comments and description of a Jira issue and Git-commit
	 * messages.
	 */
	// is called after setting training-file
	public boolean train() {
		boolean isTrained = true;
		try {
			LOGGER.debug("Binary classifier training started.");
			DecisionKnowledgeClassifier.getInstance().getBinaryClassifier().train(trainingData);
			LOGGER.debug("Fine-grained classifier training started.");
			DecisionKnowledgeClassifier.getInstance().getFineGrainedClassifier().train(trainingData);
		} catch (Exception e) {
			LOGGER.error("The classifier could not be trained:" + e.getMessage());
			isTrained = false;
		}
		return isTrained;
	}

	public boolean update(PartOfJiraIssueText sentence) {
		DecisionKnowledgeClassifier classifier = DecisionKnowledgeClassifier.getInstance();
		try {
			double[][] features = Preprocessor.getInstance().preprocess(sentence.getSummary());
			// classifier needs numerical value
			Integer labelIsRelevant = sentence.isRelevant() ? 1 : 0;

			for (double[] feature : features) {
				classifier.getBinaryClassifier().update(feature, labelIsRelevant);
				if (sentence.isRelevant()) {
					classifier.getFineGrainedClassifier().train(feature, sentence.getType());
				}
			}
		} catch (Exception e) {
			LOGGER.error("Could not update classifier: " + e.getMessage());
		}
		return true;
	}

	public TrainingData getTrainingData() {
		return trainingData;
	}

	/**
	 * Reads training data from a file to train the classifier.
	 *
	 * @param file
	 *            file to train the classifier.
	 */
	public void setTrainingFile(File file) {
		trainingData = new TrainingData(file);
	}

	/**
	 * @return CSV file for the current project that can be used to train the
	 *         classifier. It is saved on the server in the Jira home directory in
	 *         the data/condec-plugin/classifier folder. Returns null if the file
	 *         could not be saved.
	 */
	public File saveTrainingFile() {
		TrainingData trainingData = new TrainingData(getKnowledgeElementsValidForTraining());
		return trainingData.saveToFile(projectKey);
	}

	public List<KnowledgeElement> getKnowledgeElementsValidForTraining() {
		Set<KnowledgeElement> allElementsInGraph = KnowledgeGraph.getOrCreate(projectKey).vertexSet();
		List<KnowledgeElement> knowledgeElements = new ArrayList<>();
		for (KnowledgeElement element : allElementsInGraph) {
			if (!element.getType().canBeDocumentedInJiraIssueText() && element.getType() != KnowledgeType.OTHER) {
				continue;
			}
			if (element instanceof PartOfJiraIssueText && !((PartOfJiraIssueText) element).isValidated()) {
				continue;
			}
			knowledgeElements.add(element);
		}
		return knowledgeElements;
	}

	private List<String> extractStringsFromDke(List<KnowledgeElement> sentences) {
		List<String> extractedStringsFromPoji = new ArrayList<String>();
		for (KnowledgeElement sentence : sentences) {
			extractedStringsFromPoji.add(sentence.getSummary());
		}
		return extractedStringsFromPoji;
	}

	@Override
	public Map<String, Double> evaluateClassifier() {
		// create and initialize default measurements list
		List<ClassificationMetric> defaultMeasurements = new ArrayList<>();
		defaultMeasurements.add(new FScore());

		List<KnowledgeElement> elements = getKnowledgeElementsValidForTraining();
		return evaluateClassifier(defaultMeasurements, elements);
	}

	@Override
	public Map<String, Double> evaluateClassifier(List<ClassificationMetric> measurements,
			List<KnowledgeElement> partOfJiraIssueTexts) {
		LOGGER.debug("Started evaluation!");
		Map<String, Double> resultsMap = new HashMap<>();
		List<KnowledgeElement> relevantPartOfJiraIssueTexts = partOfJiraIssueTexts.stream()
				.filter(x -> !x.getType().equals(KnowledgeType.OTHER)).collect(toList());

		// format data
		List<String> sentences = this.extractStringsFromDke(partOfJiraIssueTexts);
		List<String> relevantSentences = this.extractStringsFromDke(relevantPartOfJiraIssueTexts);
		// extract true values
		Integer[] binaryTruths = partOfJiraIssueTexts.stream()
				// when type equals other then it is irrelevant
				.map(x -> x.getType().equals(KnowledgeType.OTHER) ? 0 : 1).collect(toList())
				.toArray(new Integer[partOfJiraIssueTexts.size()]);

		Integer[] fineGrainedTruths = relevantPartOfJiraIssueTexts.stream()
				.map(x -> FineGrainedClassifier.mapKnowledgeTypeToIndex(x.getType())).collect(toList())
				.toArray(new Integer[relevantPartOfJiraIssueTexts.size()]);

		// predict classes
		long start = System.currentTimeMillis();

		boolean[] binaryPredictionsList = DecisionKnowledgeClassifier.getInstance().getBinaryClassifier()
				.predict(sentences);
		Integer[] binaryPredictions = new Integer[sentences.size()];
		for (int i = 0; i < binaryPredictionsList.length; i++) {
			binaryPredictions[i] = binaryPredictionsList[i] ? 1 : 0;
		}

		// LOGGER.info(("Time for binary prediction on " + sentences.size() + "
		// sentences took " + (end-start) + " ms.");

		Integer[] fineGrainedPredictions = DecisionKnowledgeClassifier.getInstance().getFineGrainedClassifier()
				.predict(relevantSentences).stream().map(x -> FineGrainedClassifier.mapKnowledgeTypeToIndex(x))
				.collect(toList()).toArray(new Integer[relevantSentences.size()]);
		long end = System.currentTimeMillis();

		LOGGER.info("Time for prediction on " + sentences.size() + " sentences took " + (end - start) + " ms.");

		// calculate measurements for each ClassificationMeasure in measurements
		for (ClassificationMetric measurement : measurements) {
			LOGGER.debug("Evaluating: " + measurement.getClass().getSimpleName());
			String binaryKey = measurement.getClass().getSimpleName() + "_binary";
			Double binaryMeasurement = measurement.score(ArrayUtils.toPrimitive(binaryTruths),
					ArrayUtils.toPrimitive(binaryPredictions));
			resultsMap.put(binaryKey, binaryMeasurement);

			for (int classLabel : IntStream
					.range(0, DecisionKnowledgeClassifier.getInstance().getFineGrainedClassifier().getNumClasses())
					.toArray()) {
				String fineGrainedKey = measurement.getClass().getSimpleName() + "_fineGrained_"
						+ FineGrainedClassifier.mapIndexToKnowledgeType(classLabel);

				Integer[] currentFineGrainedTruths = mapFineGrainedToBinaryResults(fineGrainedTruths, classLabel);
				Integer[] currentFineGrainedPredictions = mapFineGrainedToBinaryResults(fineGrainedPredictions,
						classLabel);

				Double fineGrainedMeasurement = measurement.score(ArrayUtils.toPrimitive(currentFineGrainedTruths),
						ArrayUtils.toPrimitive(currentFineGrainedPredictions));
				resultsMap.put(fineGrainedKey, fineGrainedMeasurement);
			}

		}
		// return results
		LOGGER.debug("Finished evaluation!");

		return resultsMap;
	}

	private Integer[] mapFineGrainedToBinaryResults(Integer[] array, Integer currentElement) {
		return Arrays.stream(array).map(x -> x.equals(currentElement) ? 1 : 0).toArray(Integer[]::new);

	}
}