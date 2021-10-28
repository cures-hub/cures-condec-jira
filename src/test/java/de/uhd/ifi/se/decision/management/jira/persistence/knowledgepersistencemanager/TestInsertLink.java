package de.uhd.ifi.se.decision.management.jira.persistence.knowledgepersistencemanager;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import de.uhd.ifi.se.decision.management.jira.testdata.Links;
import net.java.ao.test.jdbc.NonTransactional;

public class TestInsertLink extends TestSetUp {

	public Link link;
	public ApplicationUser user;
	public KnowledgePersistenceManager knowledgePersistenceManager;

	@Before
	public void setUp() {
		init();
		link = Links.getTestLink();
		user = JiraUsers.SYS_ADMIN.getApplicationUser();
		knowledgePersistenceManager = KnowledgePersistenceManager.getInstance("TEST");
	}

	@Test
	@NonTransactional
	public void testLinkValidUserNull() {
		assertEquals(0, knowledgePersistenceManager.insertLink(link, null));
	}

	@Test
	@NonTransactional
	public void testLinkValidUserValid() {
		assertEquals(1, knowledgePersistenceManager.insertLink(link, user));
	}

	@Test
	public void testLinkNullUserValid() {
		assertEquals(0, knowledgePersistenceManager.insertLink(null, user));
	}

	@Test
	public void testLinkWithUnknownDocumentationLocationUserValid() {
		link.setDocumentationLocationOfDestinationElement("");
		assertEquals(0, knowledgePersistenceManager.insertLink(link, user));
	}

	@Test
	public void testLinkFilledUserNull() {
		Link link = new Link(1, 2, DocumentationLocation.JIRAISSUE, DocumentationLocation.JIRAISSUE);
		link.setType("contains");
		link.setProject("TEST");
		assertEquals(0, knowledgePersistenceManager.insertLink(link, null));
	}

	@Test
	public void testLinkFilledUserFilled() {
		Link link = new Link(1, 2, DocumentationLocation.JIRAISSUE, DocumentationLocation.JIRAISSUE);
		link.setType("contains");
		link.setProject("TEST");
		assertEquals(1, knowledgePersistenceManager.insertLink(link, user));
	}
}