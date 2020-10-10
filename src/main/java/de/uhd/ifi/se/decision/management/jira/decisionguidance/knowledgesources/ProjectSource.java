package de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.calculationmethods.CalculationMethodType;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.calculationmethods.projectsource.ProjectCalculationMethod;
import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.Recommendation;

import java.util.List;

public class ProjectSource extends KnowledgeSource<ProjectCalculationMethod> {

	public ProjectSource(String projectKey) {
		this.projectKey = projectKey;
		this.calculationMethodType = CalculationMethodType.SUBSTRING;
		this.knowledgeSourceType = KnowledgeSourceType.PROJECT;
		this.isActivated = false;
	}

	public ProjectSource(String projectKey, String projectSourceName, boolean isActivated) {
		this(projectKey);
		this.name = projectSourceName;
		this.isActivated = isActivated;
	}

	@Override
	public List<Recommendation> getResults(String inputs) {
		this.knowledgeSourceAlgorithm = this.getKnowledgeSourceAlgorithm();
		this.knowledgeSourceAlgorithm.setData(this.projectKey, this.name);
		return this.knowledgeSourceAlgorithm.getResults(inputs);
	}
}
