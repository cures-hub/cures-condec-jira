package de.uhd.ifi.se.decision.management.jira.classification;

import org.apache.commons.io.FilenameUtils;
import org.codehaus.jackson.annotate.JsonProperty;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.model.PartOfJiraIssueText;

/**
 * Contains the configuration details for the automatic text classification for
 * one Jira project (see {@link DecisionKnowledgeProject}).
 */
public class TextClassificationConfiguration {

	private boolean isActivated;
	private String selectedGroundTruthFileName;
	private String lastEvaluationResults;
	private String trainedClassifierName;
	private boolean isOnlineLearningActivated;

	public TextClassificationConfiguration() {
		selectedGroundTruthFileName = "defaultTrainingData.csv";
		lastEvaluationResults = "";
		trainedClassifierName = "";
	}

	public boolean isActivated() {
		return isActivated;
	}

	@JsonProperty("isActivated")
	public void setActivated(boolean isActivated) {
		this.isActivated = isActivated;
	}

	public String getSelectedGroundTruthFile() {
		return selectedGroundTruthFileName;
	}

	@JsonProperty("selectedGroundTruthFile")
	public void setSelectedGroundTruthFile(String selectedGroundTruthFile) {
		this.selectedGroundTruthFileName = selectedGroundTruthFile;
	}

	public String getLastEvaluationResults() {
		return lastEvaluationResults;
	}

	@JsonProperty("lastEvaluationResults")
	public void setLastEvaluationResults(String lastEvaluationResults) {
		this.lastEvaluationResults = lastEvaluationResults;
	}

	public String getPrefixOfSelectedGroundTruthFileName() {
		return FilenameUtils.getBaseName(selectedGroundTruthFileName);
	}

	public String getSelectedTrainedClassifier() {
		return trainedClassifierName;
	}

	@JsonProperty("selectedTrainedClassifier")
	public void setSelectedTrainedClassifier(String trainedClassifier) {
		this.trainedClassifierName = trainedClassifier;
	}

	/**
	 * @return true if the classifier should be updated whenever a manually approved
	 *         {@link PartOfJiraIssueText} is created or updated.
	 * 
	 * @see TextClassifier#update(PartOfJiraIssueText)
	 */
	public boolean isOnlineLearningActivated() {
		return isOnlineLearningActivated;
	}

	/**
	 * @param isOnlineLearningActivated
	 *            true if the classifier should be updated whenever a manually
	 *            approved {@link PartOfJiraIssueText} is created or updated.
	 * 
	 * @see TextClassifier#update(PartOfJiraIssueText)
	 */
	@JsonProperty("isOnlineLearningActivated")
	public void setOnlineLearningActivated(boolean isOnlineLearningActivated) {
		this.isOnlineLearningActivated = isOnlineLearningActivated;
	}
}
