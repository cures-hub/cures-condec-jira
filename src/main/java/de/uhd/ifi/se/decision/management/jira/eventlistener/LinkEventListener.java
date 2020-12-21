package de.uhd.ifi.se.decision.management.jira.eventlistener;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;

public interface LinkEventListener {

	/**
	 * Event listener method.
	 *
	 * @param element
	 */
	void onLinkEvent(KnowledgeElement element);
}
