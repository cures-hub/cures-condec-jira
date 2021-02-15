package de.uhd.ifi.se.decision.management.jira.classification;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.gzipfilter.org.apache.commons.lang.ArrayUtils;

import de.uhd.ifi.se.decision.management.jira.classification.preprocessing.PreprocessedData;
import de.uhd.ifi.se.decision.management.jira.classification.preprocessing.Preprocessor;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.PartOfJiraIssueText;
import smile.classification.Classifier;
import smile.validation.ClassificationValidations;
import smile.validation.CrossValidation;
import smile.validation.metric.Accuracy;
import smile.validation.metric.ClassificationMetric;
import smile.validation.metric.FScore;
import smile.validation.metric.Precision;
import smile.validation.metric.Sensitivity;

/**
 * Class responsible to train the supervised text classifier. For this purpose,
 * the project admin needs to create and select a training data file.
 */
public class ClassifierTrainer {
	protected static final Logger LOGGER = LoggerFactory.getLogger(ClassifierTrainer.class);

	protected TrainingData trainingData;
	protected String projectKey;

	public ClassifierTrainer(String projectKey) {
		new File(TextClassifier.CLASSIFIER_DIRECTORY).mkdirs();
		this.projectKey = projectKey;
		trainingData = new TrainingData();
	}

	public ClassifierTrainer(String projectKey, String fileName) {
		new File(TextClassifier.CLASSIFIER_DIRECTORY).mkdirs();
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
			TextClassifier.getInstance().getBinaryClassifier().train(trainingData);
			LOGGER.debug("Fine-grained classifier training started.");
			TextClassifier.getInstance().getFineGrainedClassifier().train(trainingData);
		} catch (Exception e) {
			LOGGER.error("The classifier could not be trained:" + e.getMessage());
			isTrained = false;
		}
		return isTrained;
	}

	public boolean update(PartOfJiraIssueText sentence) {
		TextClassifier classifier = TextClassifier.getInstance();
		try {
			double[][] features = Preprocessor.getInstance().preprocess(sentence.getSummary());
			// classifier needs numerical value
			Integer labelIsRelevant = sentence.isRelevant() ? 1 : 0;

			for (double[] feature : features) {
				classifier.getBinaryClassifier().update(feature, labelIsRelevant);
				if (sentence.isRelevant()) {
					classifier.getFineGrainedClassifier().update(feature, sentence.getType());
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
			if (element.getDocumentationLocation() == DocumentationLocation.JIRAISSUE
					&& element.getType() == KnowledgeType.OTHER) {
				continue;
			}
			// if (element instanceof PartOfJiraIssueText && !((PartOfJiraIssueText)
			// element).isValidated()) {
			// continue;
			// }
			if (element.getSummary().startsWith("In class ") && element.getSummary().contains("the following methods")
					|| element.getSummary().contains("Commit Hash:")) {
				// Code change or commit comment
				continue;
			}
			knowledgeElements.add(element);
		}
		return knowledgeElements;
	}

	public Map<String, Double> evaluateClassifier() {
		List<ClassificationMetric> metrics = new ArrayList<>();
		metrics.add(new FScore());
		metrics.add(new Precision());
		metrics.add(new Sensitivity());
		metrics.add(new Accuracy());

		LOGGER.debug("Started evaluation!");
		Map<String, Double> resultsMap = new LinkedHashMap<>();
		resultsMap.putAll(evaluateBinaryClassifier(3));
		resultsMap.putAll(evaluateFineGrainedClassifier(3));
		LOGGER.debug("Finished evaluation!");
		return resultsMap;
	}

	public Map<String, Double> evaluateBinaryClassifier(int k) {
		Map<String, Double> resultsMap = new LinkedHashMap<>();
		PreprocessedData preprocessedData = new PreprocessedData(trainingData, false);
		ClassificationValidations<Classifier<double[]>> validations = CrossValidation.classification(k,
				preprocessedData.preprocessedSentences, preprocessedData.updatedLabels,
				TextClassifier.getInstance().getBinaryClassifier()::train);

		resultsMap.put("Binary Precision", validations.avg.precision);
		resultsMap.put("Binary Recall", validations.avg.sensitivity);
		resultsMap.put("Binary F1", validations.avg.f1);
		resultsMap.put("Binary Accuracy", validations.avg.accuracy);
		resultsMap.put("Binary Number of Errors", (double) validations.avg.error);
		return resultsMap;
	}

	public Map<String, Double> evaluateFineGrainedClassifier(int k) {
		Map<String, Double> resultsMap = new LinkedHashMap<>();
		PreprocessedData preprocessedData = new PreprocessedData(trainingData, true);
		ClassificationValidations<Classifier<double[]>> validations = CrossValidation.classification(k,
				preprocessedData.preprocessedSentences, preprocessedData.updatedLabels,
				TextClassifier.getInstance().getFineGrainedClassifier()::train);
		resultsMap.put("Fine-grained Accuracy Overall", validations.avg.accuracy);
		resultsMap.put("Fine-grained Number of Errors Overall", (double) validations.avg.error);

		int[] truthsPrimitive = new int[0];
		int[] predictionsPrimitive = new int[0];
		for (int i = 0; i < k; i++) {
			truthsPrimitive = PreprocessedData.concatenate(truthsPrimitive, validations.rounds.get(0).truth);
			predictionsPrimitive = PreprocessedData.concatenate(predictionsPrimitive,
					validations.rounds.get(0).prediction);
		}

		for (int classLabel = 0; classLabel < TextClassifier.getInstance().getFineGrainedClassifier()
				.getNumClasses(); classLabel++) {
			KnowledgeType type = FineGrainedClassifier.mapIndexToKnowledgeType(classLabel);
			Integer[] truths = mapFineGrainedToBinaryResults(ArrayUtils.toObject(truthsPrimitive), classLabel);
			Integer[] predictions = mapFineGrainedToBinaryResults(ArrayUtils.toObject(predictionsPrimitive),
					classLabel);

			Double fineGrainedPrecisions = Precision.of(ArrayUtils.toPrimitive(truths),
					ArrayUtils.toPrimitive(predictions));
			resultsMap.put("Fine-grained Precision" + type.toString(), fineGrainedPrecisions);
			Double fineGrainedRecall = Sensitivity.of(ArrayUtils.toPrimitive(truths),
					ArrayUtils.toPrimitive(predictions));
			resultsMap.put("Fine-grained Recall" + type.toString(), fineGrainedRecall);
			resultsMap.put("Fine-grained F1" + type.toString(),
					2 * (fineGrainedRecall * fineGrainedPrecisions) / (fineGrainedRecall + fineGrainedPrecisions));
		}
		return resultsMap;
	}

	private Integer[] mapFineGrainedToBinaryResults(Integer[] array, Integer currentElement) {
		return Arrays.stream(array).map(x -> x.equals(currentElement) ? 1 : 0).toArray(Integer[]::new);
	}
}