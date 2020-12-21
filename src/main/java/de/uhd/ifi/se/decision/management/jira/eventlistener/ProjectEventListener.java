package de.uhd.ifi.se.decision.management.jira.eventlistener;

import com.atlassian.jira.event.ProjectDeletedEvent;

public interface ProjectEventListener {

	/**
	 * Event listener method.
	 *
	 * @param projectDeleted
	 *            that triggered the listener
	 */
	void onProjectDeletion(ProjectDeletedEvent projectDeletedEvent);
}
