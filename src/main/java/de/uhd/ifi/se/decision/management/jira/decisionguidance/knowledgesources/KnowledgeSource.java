package de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.calculationmethods.CalculationMethod;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.calculationmethods.CalculationMethodType;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.calculationmethods.factory.CaclulationMethodFactory;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.calculationmethods.factory.CalculationMethodFactoryProvider;
import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.Recommendation;

import java.util.List;

public abstract class KnowledgeSource<T extends CalculationMethod> {

	protected CalculationMethodType calculationMethodType;
	protected T knowledgeSourceAlgorithm;
	protected KnowledgeSourceType knowledgeSourceType;

	protected List<Recommendation> recommendations;
	protected String projectKey;
	protected boolean isActivated;
	protected String name;

	public abstract List<Recommendation> getResults(String inputs);


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

	public T getKnowledgeSourceAlgorithm() {
		CaclulationMethodFactory<T> caclulationMethodFactory = CalculationMethodFactoryProvider.getFactory(this.knowledgeSourceType);
		this.knowledgeSourceAlgorithm = caclulationMethodFactory.getAlgorithm(this.calculationMethodType);
		return this.knowledgeSourceAlgorithm;
	}

	public void setKnowledgeSourceAlgorithmType(CalculationMethodType calculationMethodType) {
		this.calculationMethodType = calculationMethodType;
	}
}
