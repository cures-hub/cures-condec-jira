package de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.calculationmethods.factory;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.KnowledgeSourceType;

public class CalculationMethodFactoryProvider {

	public static CalculationMethodFactory getFactory(KnowledgeSourceType knowledgeSourceType) {
		if (knowledgeSourceType == null) return null;
		switch (knowledgeSourceType) {
			default:
				return new ProjectCalculationMethodFactory();
		}
	}
}
