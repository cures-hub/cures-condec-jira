package de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.jiraissuepersistencemanager;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.LinkType;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssuePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestInsertLink extends TestSetUp {

	private static JiraIssuePersistenceManager persistenceManager;
	private static ApplicationUser user;
	private static Link link;

	@Before
	public void setUp() {
		init();
		persistenceManager = KnowledgePersistenceManager.getOrCreate("TEST").getJiraIssueManager();
		user = JiraUsers.SYS_ADMIN.getApplicationUser();
		link = new Link(1, 4, DocumentationLocation.JIRAISSUE, DocumentationLocation.JIRAISSUE);
		link.setType(LinkType.RELATE);
	}

	@Test
	public void testLinkNullUserNull() {
		assertEquals(0, persistenceManager.insertLink(null, null));
	}

	@Test
	public void testLinkFilledUserNull() {
		Link link = new Link(1, 2, DocumentationLocation.JIRAISSUE, DocumentationLocation.JIRAISSUE);
		link.setType("contains");
		link.setProject("TEST");
		assertEquals(0, persistenceManager.insertLink(link, null));
	}

	@Test(expected = NullPointerException.class)
	public void testLinkNullUserFilled() {
		persistenceManager.insertLink(null, user);
	}

	@Test
	public void testLinkFilledUserFilled() {
		Link link = new Link(1, 2, DocumentationLocation.JIRAISSUE, DocumentationLocation.JIRAISSUE);
		link.setType("contains");
		link.setProject("TEST");
		assertEquals(1, persistenceManager.insertLink(link, user));
	}

	@Test
	public void testLinkFilledUserHasNoPermissions() {
		Link link = new Link(1, 2, DocumentationLocation.JIRAISSUE, DocumentationLocation.JIRAISSUE);
		link.setType("contains");
		link.setProject("TEST");
		ApplicationUser user = JiraUsers.BLACK_HEAD.getApplicationUser();
		assertNotNull(user);
		assertEquals(0, persistenceManager.insertLink(link, user));
	}
}
