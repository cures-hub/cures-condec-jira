package de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.calculationmethods.factory;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.calculationmethods.CalculationMethod;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.calculationmethods.CalculationMethodType;

public abstract class CalculationMethodFactory<T extends CalculationMethod> {

	public abstract T getCalculationMethod(CalculationMethodType calculationMethodType, String projectKey, String knowledgeSourceName);
}
