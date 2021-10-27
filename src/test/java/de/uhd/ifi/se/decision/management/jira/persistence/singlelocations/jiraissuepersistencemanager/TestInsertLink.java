package de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.jiraissuepersistencemanager;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssuePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import de.uhd.ifi.se.decision.management.jira.testdata.Links;

public class TestInsertLink extends TestSetUp {

	private static JiraIssuePersistenceManager persistenceManager;
	private static ApplicationUser user;
	private static Link link;

	@Before
	public void setUp() {
		init();
		persistenceManager = KnowledgePersistenceManager.getInstance("TEST").getJiraIssueManager();
		user = JiraUsers.SYS_ADMIN.getApplicationUser();
		link = Links.getTestLink();
	}

	@Test
	public void testLinkNullUserNull() {
		assertEquals(0, persistenceManager.insertLink(null, null));
	}

	@Test
	public void testLinkFilledUserNull() {
		assertEquals(0, persistenceManager.insertLink(link, null));
	}

	@Test(expected = NullPointerException.class)
	public void testLinkNullUserFilled() {
		persistenceManager.insertLink(null, user);
	}

	@Test
	public void testLinkFilledUserFilled() {
		assertEquals(1, persistenceManager.insertLink(link, user));
	}

	@Test
	public void testLinkInvalidUserFilled() {
		link.getSource().setId(-1);
		assertEquals(0, persistenceManager.insertLink(link, user));
	}

	@Test
	public void testLinkFilledUserHasNoPermissions() {
		ApplicationUser user = JiraUsers.BLACK_HEAD.getApplicationUser();
		assertEquals(0, persistenceManager.insertLink(link, user));
	}
}
