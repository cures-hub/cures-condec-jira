package de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources;

import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.Recommendation;

import java.util.List;

public interface KnowledgeSource {

	List<Recommendation> getResults(String inputs);

	String getName();

	void setName(String name);

	boolean isActivated();

	void setActivated(boolean activated);
}
