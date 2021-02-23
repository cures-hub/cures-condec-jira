package de.uhd.ifi.se.decision.management.jira.classification;

import org.codehaus.jackson.annotate.JsonProperty;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;

/**
 * Contains the configuration details for the automatic text classification for
 * one Jira project (see @link {@link DecisionKnowledgeProject}.
 */
public class TextClassificationConfiguration {

	private boolean isActivated;
	private String selectedGroundTruthFile;
	private String lastEvaluationResults;
	public String trainedBinaryClassifier;
	public String trainedFineGrainedClassifier;

	public TextClassificationConfiguration() {
		this.isActivated = false;
		this.selectedGroundTruthFile = "defaultTrainingData.csv";
		this.lastEvaluationResults = "";
	}

	public boolean isActivated() {
		return isActivated;
	}

	@JsonProperty("isActivated")
	public void setActivated(boolean isActivated) {
		this.isActivated = isActivated;
	}

	public String getSelectedGroundTruthFile() {
		return selectedGroundTruthFile;
	}

	@JsonProperty("selectedGroundTruthFile")
	public void setSelectedGroundTruthFile(String selectedGroundTruthFile) {
		this.selectedGroundTruthFile = selectedGroundTruthFile;
	}

	public String getLastEvaluationResults() {
		return lastEvaluationResults;
	}

	@JsonProperty("lastEvaluationResults")
	public void setLastEvaluationResults(String lastEvaluationResults) {
		this.lastEvaluationResults = lastEvaluationResults;
	}

}
