package de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.calculationmethods.factory;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.calculationmethods.CalculationMethod;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.calculationmethods.CalculationMethodType;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.calculationmethods.projectsource.ProjectCalculationMethodSubstring;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.calculationmethods.projectsource.ProjectCalculationMethodTokenize;

public class ProjectCalculationMethodFactory extends CalculationMethodFactory {

	private String projectKey;
	private String knowledgeSourceName;

	public ProjectCalculationMethodFactory() {

	}

	public ProjectCalculationMethodFactory(String projectKey, String knowledgeSourceName) {
		this.projectKey = projectKey;
		this.knowledgeSourceName = knowledgeSourceName;
	}

	public CalculationMethod getCalculationMethod(CalculationMethodType calculationMethodType, String projectKey, String knowledgeSourceName) {
		if (calculationMethodType == null)
			return new ProjectCalculationMethodSubstring(projectKey, knowledgeSourceName);
		switch (calculationMethodType) {
			case TOKENIZED:
				return new ProjectCalculationMethodTokenize(projectKey, knowledgeSourceName);
			default:
				return new ProjectCalculationMethodSubstring(projectKey, knowledgeSourceName);
		}
	}
}
