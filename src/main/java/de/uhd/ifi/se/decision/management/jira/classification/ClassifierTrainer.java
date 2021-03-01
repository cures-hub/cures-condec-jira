package de.uhd.ifi.se.decision.management.jira.classification;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import smile.validation.ClassificationMetrics;

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
			TextClassifier.getInstance(projectKey).getBinaryClassifier().train(trainingData);
			LOGGER.debug("Fine-grained classifier training started.");
			TextClassifier.getInstance(projectKey).getFineGrainedClassifier().train(trainingData);
		} catch (Exception e) {
			LOGGER.error("The classifier could not be trained:" + e.getMessage());
			isTrained = false;
		}
		return isTrained;
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

	/**
	 * Evaluates the binary and fine-grained classifier using common metrics.
	 * 
	 * @param k
	 *            number of folds in k-fold cross-validation.
	 *
	 * @return map of evaluation results
	 */
	public Map<String, ClassificationMetrics> evaluateClassifier(int k) {
		LOGGER.info("Start evaluation of text classifier in project " + projectKey + " on data file "
				+ trainingData.getFileName());
		Map<String, ClassificationMetrics> resultsMap = new LinkedHashMap<>();

		if (k > 1) {
			LOGGER.info("Train and evaluate on the same data using k-fold cross-validation, k is set to: " + k);
			resultsMap.putAll(
					TextClassifier.getInstance(projectKey).getBinaryClassifier().evaluateClassifier(k, trainingData));
			resultsMap.putAll(TextClassifier.getInstance(projectKey).getFineGrainedClassifier().evaluateClassifier(k,
					trainingData));
		} else {
			LOGGER.info(
					"Evaluate the trained classifier on different data than it was trained on (cross-project validation)");
			resultsMap.putAll(
					TextClassifier.getInstance(projectKey).getBinaryClassifier().evaluateClassifier(trainingData));
			resultsMap.putAll(
					TextClassifier.getInstance(projectKey).getFineGrainedClassifier().evaluateClassifier(trainingData));
		}

		LOGGER.info("Finished evaluation: " + resultsMap.toString());
		return resultsMap;
	}
}