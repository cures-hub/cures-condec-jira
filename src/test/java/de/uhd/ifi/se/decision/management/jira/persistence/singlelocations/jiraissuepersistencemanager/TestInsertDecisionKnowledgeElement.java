package de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.jiraissuepersistencemanager;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.LinkType;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssuePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestInsertDecisionKnowledgeElement extends TestSetUp {

	private JiraIssuePersistenceManager persistenceManager;
	private ApplicationUser user;
	private Link link;

	@Before
	public void setUp() {
		init();
		persistenceManager = KnowledgePersistenceManager.getOrCreate("TEST").getJiraIssueManager();
		user = JiraUsers.SYS_ADMIN.getApplicationUser();
		link = new Link(1, 4, DocumentationLocation.JIRAISSUE, DocumentationLocation.JIRAISSUE);
		link.setType(LinkType.RELATE);
	}

	@Test(expected = NullPointerException.class)
	public void testElementNullUserNull() {
		persistenceManager.insertKnowledgeElement(null, null);
	}

	@Test(expected = NullPointerException.class)
	public void testElementEmptyUserNull() {
		KnowledgeElement element = new KnowledgeElement();
		persistenceManager.insertKnowledgeElement(element, null);
	}

	@Test(expected = NullPointerException.class)
	public void testElementEmptyUserExistent() {
		KnowledgeElement element = new KnowledgeElement();
		assertNotNull(persistenceManager.insertKnowledgeElement(element, user));
	}

	@Test
	public void testElementExistentUserExistent() {
		KnowledgeElement element = new KnowledgeElement();
		element.setProject("TEST");
		element.setType(KnowledgeType.SOLUTION);
		assertNotNull(persistenceManager.insertKnowledgeElement(element, user));
	}

	@Test
	public void testElementExistentUserNotAuthorized() {
		KnowledgeElement element = new KnowledgeElement();
		element.setProject("TEST");
		element.setType(KnowledgeType.SOLUTION);
		assertNull(persistenceManager.insertKnowledgeElement(element, JiraUsers.BLACK_HEAD.getApplicationUser()));
	}
}
