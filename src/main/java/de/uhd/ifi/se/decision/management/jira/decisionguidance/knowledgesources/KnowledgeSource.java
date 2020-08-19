package de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;

import java.util.List;

public interface KnowledgeSource {

	List<KnowledgeElement> getResults(String inputs);

	String getName();

	void setName(String name);

	boolean isActivated();

	void setActivated(boolean activated);
}
