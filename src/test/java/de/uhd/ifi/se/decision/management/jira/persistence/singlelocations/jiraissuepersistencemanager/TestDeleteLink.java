package de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.jiraissuepersistencemanager;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssuePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import de.uhd.ifi.se.decision.management.jira.testdata.Links;

public class TestDeleteLink extends TestSetUp {

	private JiraIssuePersistenceManager persistenceManager;
	private ApplicationUser user;
	private Link link;

	@Before
	public void setUp() {
		init();
		persistenceManager = KnowledgePersistenceManager.getOrCreate("TEST").getJiraIssueManager();
		user = JiraUsers.SYS_ADMIN.getApplicationUser();
		link = Links.getTestLink();
	}

	@Test
	public void testLinkNullUserNull() {
		assertFalse(persistenceManager.deleteLink(null, null));
	}

	@Test
	public void testLinkNullUserFilled() {
		assertFalse(persistenceManager.deleteLink(null, user));
	}

	@Test
	public void testLinkFilledUserNull() {
		assertFalse(persistenceManager.deleteLink(link, null));
	}

	@Test
	public void testLinkFilledUserFilled() {
		assertTrue(persistenceManager.deleteLink(link, user));
	}

	@Test
	public void testLinkFilledUserHasNoPermissions() {
		ApplicationUser user = JiraUsers.BLACK_HEAD.getApplicationUser();
		assertFalse(persistenceManager.deleteLink(link, user));
	}
}
