package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;

public interface CompletenessCheck {

	public boolean execute(KnowledgeElement knowledgeElement);

}
