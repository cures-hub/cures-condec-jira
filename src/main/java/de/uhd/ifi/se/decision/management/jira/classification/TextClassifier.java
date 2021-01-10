package de.uhd.ifi.se.decision.management.jira.classification;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.classification.preprocessing.Preprocessor;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.PartOfJiraIssueText;

/**
 * Tries to identify decision knowledge in natural language texts using a binary
 * and fine grained supervised classifier.
 */
public class TextClassifier {
	private static final Logger LOGGER = LoggerFactory.getLogger(TextClassifier.class);

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

	public static TextClassifier instance;

	private TextClassifier() {
		FileManager.copyDefaultTrainingDataToClassifierDirectory();
		Preprocessor.copyDefaultPreprocessingDataToFile();
		binaryClassifier = new BinaryClassifier();
		fineGrainedClassifier = new FineGrainedClassifier(5);
	}

	public static TextClassifier getInstance() {
		if (instance == null) {
			instance = new TextClassifier();
		}
		return instance;
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
}
