package de.uhd.ifi.se.decision.management.jira.persistence.knowledgepersistencemanager;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import de.uhd.ifi.se.decision.management.jira.testdata.Links;

public class TestDeleteLink extends TestSetUp {

	public Link link;
	public ApplicationUser user;
	public KnowledgePersistenceManager knowledgePersistenceManager;

	@Before
	public void setUp() {
		init();
		link = Links.getTestLink();
		user = JiraUsers.SYS_ADMIN.getApplicationUser();
		knowledgePersistenceManager = KnowledgePersistenceManager.getOrCreate("TEST");
	}

	@Test
	public void testLinkValidUserNull() {
		assertFalse(knowledgePersistenceManager.deleteLink(link, null));
	}

	@Test
	public void testLinkValidUserValid() {
		assertTrue(knowledgePersistenceManager.deleteLink(link, user));
	}

	@Test
	public void testLinkNullUserValid() {
		assertFalse(knowledgePersistenceManager.deleteLink(null, user));
	}

	@Test
	public void testLinkWithUnknownDocumentationLocationUserValid() {
		link.setDocumentationLocationOfDestinationElement("");
		assertFalse(knowledgePersistenceManager.deleteLink(link, user));
	}
}