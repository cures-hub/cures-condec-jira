package de.uhd.ifi.se.decision.management.jira.persistence.knowledgepersistencemanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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

public class TestGetLinkId extends TestSetUp {

	public Link link;
	public ApplicationUser user;
	public KnowledgePersistenceManager knowledgePersistenceManager;

	@Before
	public void setUp() {
		init();
		link = Links.getTestLinks().get(0);
		user = JiraUsers.SYS_ADMIN.getApplicationUser();
		knowledgePersistenceManager = KnowledgePersistenceManager.getInstance("TEST");
	}

	@Test
	@NonTransactional
	public void testJiraIssueLinkValidUserValid() {
		assertEquals(1, knowledgePersistenceManager.insertLink(link, user));
		assertEquals(1, KnowledgePersistenceManager.getLinkId(link));
		assertEquals(1, KnowledgePersistenceManager.getLinkId(link.flip()));
	}

	@Test
	@NonTransactional
	public void testGenericLinkInvalid() {
		assertNotNull(link.getTarget());
		assertEquals(DocumentationLocation.JIRAISSUE, link.getTarget().getDocumentationLocation());
		assertNotNull(link.getSource());
		link.setDocumentationLocationOfDestinationElement("s");
		assertEquals(-1, KnowledgePersistenceManager.getLinkId(link));
		link.setDocumentationLocationOfDestinationElement("i");
	}
}