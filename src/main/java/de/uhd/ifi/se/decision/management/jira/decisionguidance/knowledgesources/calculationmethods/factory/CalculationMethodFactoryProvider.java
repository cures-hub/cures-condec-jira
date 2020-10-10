package de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.calculationmethods.factory;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.KnowledgeSourceType;

public class CalculationMethodFactoryProvider {

	public static CaclulationMethodFactory getFactory(KnowledgeSourceType knowledgeSourceType) {
		switch (knowledgeSourceType) {
			default:
				return new ProjectCaclulationMethodFactory();
		}
	}
}
