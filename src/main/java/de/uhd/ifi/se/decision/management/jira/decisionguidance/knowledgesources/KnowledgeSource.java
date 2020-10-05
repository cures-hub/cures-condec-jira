package de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.algorithms.KnowledgeSourceAlgorithm;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.algorithms.KnowledgeSourceAlgorithmType;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.algorithms.factory.KnowledgeSourceAlgorithmFactory;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.algorithms.factory.KnowledgeSourceAlgorithmFactoryProvider;
import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.Recommendation;

import java.util.List;

public abstract class KnowledgeSource<T extends KnowledgeSourceAlgorithm> {

	protected KnowledgeSourceAlgorithmType knowledgeSourceAlgorithmType;
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
		KnowledgeSourceAlgorithmFactory<T> knowledgeSourceAlgorithmFactory = KnowledgeSourceAlgorithmFactoryProvider.getFactory(this.knowledgeSourceType);
		this.knowledgeSourceAlgorithm = knowledgeSourceAlgorithmFactory.getAlgorithm(this.knowledgeSourceAlgorithmType);
		return this.knowledgeSourceAlgorithm;
	}

	public void setKnowledgeSourceAlgorithmType(KnowledgeSourceAlgorithmType knowledgeSourceAlgorithmType) {
		this.knowledgeSourceAlgorithmType = knowledgeSourceAlgorithmType;
	}
}
