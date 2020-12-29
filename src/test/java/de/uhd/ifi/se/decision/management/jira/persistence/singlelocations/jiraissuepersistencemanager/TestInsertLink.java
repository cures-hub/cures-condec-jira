package de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.jiraissuepersistencemanager;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestInsertLink extends TestJiraIssuePersistenceManagerSetUp {

	@Test(expected = NullPointerException.class)
	public void testLinkNullUserNull() {
		KnowledgePersistenceManager.getOrCreate("TEST").insertLink(null, null);
	}

	@Test
	public void testLinkFilledUserNull() {
		Link link = new Link(1, 2, DocumentationLocation.JIRAISSUE, DocumentationLocation.JIRAISSUE);
		link.setType("contains");
		link.getTarget().setProject("TEST");
		link.getSource().setProject("TEST");
		assertEquals(0, KnowledgePersistenceManager.getOrCreate("TEST").insertLink(link, null));
	}

	@Test(expected = NullPointerException.class)
	public void testLinkNullUserFilled() {
		ApplicationUser user = JiraUsers.SYS_ADMIN.getApplicationUser();
		KnowledgePersistenceManager.getOrCreate("TEST").insertLink(null, user);
	}

	@Test
	public void testLinkFilledUserFilled() {
		Link link = new Link(1, 2, DocumentationLocation.JIRAISSUE, DocumentationLocation.JIRAISSUE);
		link.setType("contains");
		link.getTarget().setProject("TEST");
		link.getSource().setProject("TEST");
		ApplicationUser user = JiraUsers.SYS_ADMIN.getApplicationUser();
		long linkId = KnowledgePersistenceManager.getOrCreate("TEST").insertLink(link, user);
		KnowledgePersistenceManager.getOrCreate("TEST").deleteLink(link, user);
		assertEquals(1, linkId);
	}

	@Test
	public void testCreateException() {
		Link link = new Link(2, 3, DocumentationLocation.JIRAISSUE, DocumentationLocation.JIRAISSUE);
		link.setType("contains");
		link.getTarget().setProject("TEST");
		link.getSource().setProject("TEST");
		ApplicationUser user = JiraUsers.BLACK_HEAD.getApplicationUser();
		assertEquals(0, KnowledgePersistenceManager.getOrCreate("TEST").insertLink(link, user));
	}

	@Test
	public void testMoreInwardLinks() {
		Link link = new Link(30, 3, DocumentationLocation.JIRAISSUE, DocumentationLocation.JIRAISSUE);
		link.setType("Contains");
		ApplicationUser user = JiraUsers.SYS_ADMIN.getApplicationUser();
		assertEquals(1, KnowledgePersistenceManager.getOrCreate("TEST").insertLink(link, user));
		KnowledgePersistenceManager.getOrCreate("TEST").deleteLink(link, user);
	}

	@Test
	public void testMoreOutwardLinks() {
		Link link = new Link(1, 30, DocumentationLocation.JIRAISSUE, DocumentationLocation.JIRAISSUE);
		link.setType("Contains");
		ApplicationUser user = JiraUsers.SYS_ADMIN.getApplicationUser();
		assertEquals(1, KnowledgePersistenceManager.getOrCreate("TEST").insertLink(link, user));
		KnowledgePersistenceManager.getOrCreate("TEST").deleteLink(link, user);
	}
}
