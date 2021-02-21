package de.uhd.ifi.se.decision.management.jira.classification;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;

/**
 * Contains the configuration details for the automatic text classification for
 * one Jira project (see @link {@link DecisionKnowledgeProject}.
 */
public class TextClassificationConfiguration {

	private boolean isActivated;
	public String selectedGroundTruthFile;
	public String lastEvaluationResults;
	public String trainedBinaryClassifier;
	public String trainedFineGrainedClassifier;

	public TextClassificationConfiguration() {
		this.setActivated(false);
		this.selectedGroundTruthFile = "defaultTrainingData.csv";

	}

	public boolean isActivated() {
		return isActivated;
	}

	public void setActivated(boolean isActivated) {
		this.isActivated = isActivated;
	}


}
