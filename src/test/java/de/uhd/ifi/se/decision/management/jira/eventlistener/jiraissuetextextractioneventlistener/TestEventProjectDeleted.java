package de.uhd.ifi.se.decision.management.jira.eventlistener.jiraissuetextextractioneventlistener;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.atlassian.jira.event.ProjectDeletedEvent;

import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssueTextPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraProjects;
import net.java.ao.test.jdbc.NonTransactional;

public class TestEventProjectDeleted extends TestSetUpEventListener {

	@Test
	@NonTransactional
	public void testDeleteProject() {
		listener.onProjectDeletedEvent(null);

		JiraIssues.addElementToDataBase();
		JiraIssueTextPersistenceManager persistenceManager = KnowledgePersistenceManager.getOrCreate("TEST")
				.getJiraIssueTextManager();
		assertTrue(persistenceManager.getKnowledgeElements().size() > 0);

		ProjectDeletedEvent projectDeletedEvent = new ProjectDeletedEvent(user, JiraProjects.getTestProject());
		listener.onProjectDeletedEvent(projectDeletedEvent);

		assertTrue(persistenceManager.getKnowledgeElements().size() == 0);
	}
}
