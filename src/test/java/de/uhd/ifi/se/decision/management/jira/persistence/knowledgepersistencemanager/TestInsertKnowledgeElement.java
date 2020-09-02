package de.uhd.ifi.se.decision.management.jira.persistence.knowledgepersistencemanager;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;
import net.java.ao.test.jdbc.NonTransactional;

public class TestInsertKnowledgeElement extends TestSetUp {

	public ApplicationUser user;
	public KnowledgePersistenceManager knowledgePersistenceManager;

	@Before
	public void setUp() {
		init();
		user = JiraUsers.SYS_ADMIN.getApplicationUser();
		knowledgePersistenceManager = KnowledgePersistenceManager.getOrCreate("TEST");
	}

	@Test
	@NonTransactional
	public void testElementDocumentationLocationNullUserValid() {
		KnowledgeElement element = new KnowledgeElement();
		element.setProject("TEST");
		assertNull(knowledgePersistenceManager.insertKnowledgeElement(element, user));
	}

	@Test
	@NonTransactional
	public void testElementValidUserValid() {
		KnowledgeElement parentElement = KnowledgeElements.getTestKnowledgeElement();
		KnowledgeElement element = JiraIssues.getSentencesForCommentText("Test comments").get(0);
		element.setProject("TEST");
		element.setDocumentationLocation("s");
		element = knowledgePersistenceManager.insertKnowledgeElement(element, user, parentElement);
		assertTrue(element.getId() > 0);
	}
}