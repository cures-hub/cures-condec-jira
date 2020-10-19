package de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.calculationmethods.CalculationMethod;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.calculationmethods.CalculationMethodType;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.calculationmethods.factory.CalculationMethodFactory;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.calculationmethods.factory.CalculationMethodFactoryProvider;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.Recommendation;

import java.util.ArrayList;
import java.util.List;

public abstract class KnowledgeSource<T extends CalculationMethod> {

	protected CalculationMethodType calculationMethodType;
	protected T calculationMethod;
	protected KnowledgeSourceType knowledgeSourceType;

	protected List<Recommendation> recommendations;
	protected String projectKey;
	protected boolean isActivated;
	protected String name;

	public List<Recommendation> getResults(String inputs) {
		if (this.isActivated) {
			this.getCalculationMethod();
			return this.calculationMethod.getResults(inputs);
		}
		return new ArrayList<>();
	}

	public List<Recommendation> getResults(KnowledgeElement knowledgeElement) {
		if (this.isActivated) {
			this.getCalculationMethod();
			if (this.calculationMethod != null) return this.calculationMethod.getResults(knowledgeElement);
		}
		return new ArrayList<>();
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isActivated() {
		return isActivated;
	}

	public void setActivated(boolean activated) {
		isActivated = activated;
	}

	public T getCalculationMethod() {
		CalculationMethodFactory<T> calculationMethodFactory = CalculationMethodFactoryProvider.getFactory(this.knowledgeSourceType);
		if (calculationMethodFactory != null)
			this.calculationMethod = calculationMethodFactory.getCalculationMethod(this.calculationMethodType, this.projectKey, this.name);
		return this.calculationMethod;
	}

	public void setCalculationMethodTypeType(CalculationMethodType calculationMethodType) {
		this.calculationMethodType = calculationMethodType;
	}
}
