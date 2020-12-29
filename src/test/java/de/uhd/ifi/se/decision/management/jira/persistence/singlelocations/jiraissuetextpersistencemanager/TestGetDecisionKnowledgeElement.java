package de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.jiraissuetextpersistencemanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssueTextPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.test.jdbc.NonTransactional;

public class TestGetDecisionKnowledgeElement extends TestSetUp {

	protected JiraIssueTextPersistenceManager manager;
	protected ApplicationUser user;
	protected KnowledgeElement element;

	@Before
	public void setUp() {
		init();
		manager = new JiraIssueTextPersistenceManager("TEST");
		user = JiraUsers.SYS_ADMIN.getApplicationUser();
		List<PartOfJiraIssueText> comment = JiraIssues.getSentencesForCommentText("first Comment");
		element = manager.insertKnowledgeElement(comment.get(0), null);
	}

	@Test
	@NonTransactional
	public void testElementExistingInDatabase() {
		assertEquals("first Comment", manager.getKnowledgeElement(element.getId()).getSummary());
	}

	@Test
	@NonTransactional
	public void testIdZero() {
		assertNull(manager.getKnowledgeElement(0));
	}

	@Test
	@NonTransactional
	public void testElementExistingInDatabaseButIdZero() {
		// pretend that database id of the element is unknown
		element.setId(0);
		assertEquals("first Comment", manager.getKnowledgeElement(element).getSummary());
	}

	@Test
	@NonTransactional
	public void testElementNull() {
		assertNull(manager.getKnowledgeElement((PartOfJiraIssueText) null));
	}

	@Test
	@NonTransactional
	public void testKeyNull() {
		assertNull(manager.getKnowledgeElement((String) null));
	}

	@Test
	@NonTransactional
	public void testKeyValid() {
		assertEquals("first Comment", manager.getKnowledgeElement("TEST-30:1").getSummary());
	}
}
