package de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources;

import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.Recommendation;

import java.util.List;

public interface InputMethod<T, E extends KnowledgeSource> {

	List<Recommendation> getResults(T input);

	void setData(E knowledgeSource);

}
