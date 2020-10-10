package de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.calculationmethods.factory;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.calculationmethods.CalculationMethod;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.calculationmethods.CalculationMethodType;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.calculationmethods.projectsource.ProjectCalculationMethodSubstring;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.calculationmethods.projectsource.ProjectCalculationMethodTokenize;

public class ProjectCaclulationMethodFactory extends CaclulationMethodFactory {

	private String projectKey;
	private String knowledgeSourceName;

	public ProjectCaclulationMethodFactory() {

	}

	public ProjectCaclulationMethodFactory(String projectKey, String knowledgeSourceName) {
		this.projectKey = projectKey;
		this.knowledgeSourceName = knowledgeSourceName;
	}

	public CalculationMethod getAlgorithm(CalculationMethodType calculationMethodType) {
		if (calculationMethodType == null)
			return new ProjectCalculationMethodSubstring();
		switch (calculationMethodType) {
			case TOKENIZED:
				return new ProjectCalculationMethodTokenize();
			default:
				return new ProjectCalculationMethodSubstring();
		}
	}
}
