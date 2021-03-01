package de.uhd.ifi.se.decision.management.jira.classification;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.classification.preprocessing.Preprocessor;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import smile.validation.ClassificationMetrics;

/**
 * Tries to identify decision knowledge in natural language texts using a binary
 * and fine grained supervised classifier.
 */
public class TextClassifier {
	private static final Logger LOGGER = LoggerFactory.getLogger(TextClassifier.class);

	private BinaryClassifier binaryClassifier;
	private FineGrainedClassifier fineGrainedClassifier;
	private String projectKey;
	private TrainingData trainingData;

	/**
	 * @issue What is the best place to store the supervised text classifier related
	 *        data?
	 * @decision Store the data for the text classifier in
	 *           JiraHome/data/condec-plugin/classifier!
	 * @pro Similar as for the git repositories.
	 */
	public static String CLASSIFIER_DIRECTORY = ComponentGetter.PLUGIN_HOME + "classifier" + File.separator;

	public static Map<String, TextClassifier> instances = new HashMap<>();

	private TextClassifier(String projectKey) {
		LOGGER.info("New text classifier was created");
		new File(CLASSIFIER_DIRECTORY).mkdirs();
		trainingData = new TrainingData();
		FileManager.copyDefaultTrainingDataToClassifierDirectory();
		Preprocessor.copyDefaultPreprocessingDataToFile();
		this.projectKey = projectKey;
		TextClassificationConfiguration config = ConfigPersistenceManager
				.getTextClassificationConfiguration(projectKey);
		String prefix = config.getPrefixOfSelectedGroundTruthFileName();
		binaryClassifier = new BinaryClassifier(prefix);
		fineGrainedClassifier = new FineGrainedClassifier(5, prefix);
	}

	public TextClassifier(String projectKey, String fileName) {
		this(projectKey);
		if (fileName == null || fileName.isEmpty()) {
			return;
		}
		trainingData = new TrainingData(fileName);
	}

	/**
	 * Retrieves an existing {@link TextClassifier} instance or creates a new
	 * instance if there is no instance for the given project key. Uses the multiton
	 * design pattern.
	 * 
	 * @param projectKey
	 *            of the Jira project.
	 * @return either a new or already existing {@link TextClassifier} instance.
	 */
	public static TextClassifier getInstance(String projectKey) {
		if (projectKey == null) {
			throw new IllegalArgumentException("The project key must not be null.");
		}
		if (instances.containsKey(projectKey)) {
			return instances.get(projectKey);
		}
		TextClassifier textClassifier = new TextClassifier(projectKey);
		instances.put(projectKey, textClassifier);
		return instances.get(projectKey);
	}

	/**
	 * @return {@link BinaryClassifier} that predicts whether a sentence (i.e.
	 *         {@link PartOfJiraIssueText}) contains relevant decision knowledge or
	 *         is irrelevant.
	 */
	public BinaryClassifier getBinaryClassifier() {
		return binaryClassifier;
	}

	/**
	 * @return {@link FineGrainedClassifier} that predicts the {@link KnowledgeType}
	 *         of a sentence (i.e. {@link PartOfJiraIssueText}).
	 */
	public FineGrainedClassifier getFineGrainedClassifier() {
		return fineGrainedClassifier;
	}

	/**
	 * @return whether or not the classifier is currently training.
	 */
	public boolean isTraining() {
		return fineGrainedClassifier.isCurrentlyTraining() || binaryClassifier.isCurrentlyTraining();
	}

	/**
	 * @return whether or not the classifier was trained.
	 */
	public boolean isTrained() {
		return binaryClassifier.isTrained() && fineGrainedClassifier.isTrained();
	}

	public void setSelectedTrainedClassifier(String trainedClassifier) {
		binaryClassifier = new BinaryClassifier(trainedClassifier);
		fineGrainedClassifier = new FineGrainedClassifier(5, trainedClassifier);
	}

	/**
	 * Updates the classifier using supervised training data. Used for online
	 * training. That means that manually approved parts of text are directly used
	 * for training.
	 *
	 * @param sentence
	 *            manually approved {@link PartOfJiraIssueText} to train the
	 *            classifier with.
	 */
	public boolean update(PartOfJiraIssueText sentence) {
		if (!ConfigPersistenceManager.getTextClassificationConfiguration(projectKey).isOnlineLearningActivated()) {
			return false;
		}
		try {
			double[][] features = Preprocessor.getInstance().preprocess(sentence.getSummary());
			// classifier needs numerical value
			int labelIsRelevant = sentence.isRelevant() ? 1 : 0;

			for (double[] feature : features) {
				binaryClassifier.update(feature, labelIsRelevant);
				if (sentence.isRelevant()) {
					fineGrainedClassifier.update(feature, sentence.getType());
				}
			}
		} catch (Exception e) {
			LOGGER.error("Could not update classifier: " + e.getMessage());
		}
		return true;
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
			resultsMap.putAll(binaryClassifier.evaluateClassifier(k, trainingData));
			resultsMap.putAll(fineGrainedClassifier.evaluateClassifier(k, trainingData));
		} else {
			LOGGER.info(
					"Evaluate the trained classifier on different data than it was trained on (cross-project validation)");
			resultsMap.putAll(binaryClassifier.evaluateClassifier(trainingData));
			resultsMap.putAll(fineGrainedClassifier.evaluateClassifier(trainingData));
		}

		LOGGER.info("Finished evaluation: " + resultsMap.toString());
		return resultsMap;
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
			binaryClassifier.train(trainingData);
			LOGGER.debug("Fine-grained classifier training started.");
			fineGrainedClassifier.train(trainingData);
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
}
