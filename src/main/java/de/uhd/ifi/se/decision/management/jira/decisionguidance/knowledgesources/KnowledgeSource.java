package de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources;

import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.Recommendation;

public interface KnowledgeSource {

	Recommendation getResults(String inputs);

	String getName();

	void setName(String name);

	boolean isActivated();

	void setActivated(boolean activated);
}
