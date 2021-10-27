package de.uhd.ifi.se.decision.management.jira.persistence.knowledgepersistencemanager;

import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.AbstractPersistenceManagerForSingleLocation;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import de.uhd.ifi.se.decision.management.jira.testdata.Links;
import net.java.ao.test.jdbc.NonTransactional;

public class TestGetManagerForSingleLocation extends TestSetUp {

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
	public void testDocumentationLocationUnkown() {
		AbstractPersistenceManagerForSingleLocation singleLocationPersistenceManager = knowledgePersistenceManager
				.getManagerForSingleLocation(DocumentationLocation.UNKNOWN);
		assertNull(singleLocationPersistenceManager);
	}

	@Test(expected = IllegalArgumentException.class)
	@NonTransactional
	public void testElementNull() {
		KnowledgePersistenceManager.getManagerForSingleLocation((KnowledgeElement) null);
	}
}