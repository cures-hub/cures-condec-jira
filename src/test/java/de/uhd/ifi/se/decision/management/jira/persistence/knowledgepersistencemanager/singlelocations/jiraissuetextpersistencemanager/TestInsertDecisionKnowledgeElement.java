package de.uhd.ifi.se.decision.management.jira.persistence.knowledgepersistencemanager.singlelocations.jiraissuetextpersistencemanager;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.component.ComponentAccessor;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssueTextPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.test.jdbc.NonTransactional;

public class TestInsertDecisionKnowledgeElement extends TestSetUp {

	protected JiraIssueTextPersistenceManager manager;
	protected PartOfJiraIssueText element;
	protected KnowledgeElement decisionKnowledgeElement;

	@Before
	public void setUp() {
		init();
		manager = new JiraIssueTextPersistenceManager("TEST");
		decisionKnowledgeElement = new KnowledgeElement(
				ComponentAccessor.getIssueManager().getIssueByCurrentKey("TEST-3"));
		element = JiraIssues.addElementToDataBase();
	}

	@Test
	@NonTransactional
	public void testElementInsertedTwice() {
		List<PartOfJiraIssueText> comment = JiraIssues.getSentencesForCommentText("first Comment");
		long id = manager.insertKnowledgeElement(comment.get(0), null).getId();
		long id2 = manager.insertKnowledgeElement(comment.get(0), null).getId();
		assertNotNull(new JiraIssueTextPersistenceManager("").getKnowledgeElement(id));
		assertTrue(id == id2);
	}

	@Test
	@NonTransactional
	public void testElementNullUserNullParentNull() {
		assertNull(manager.insertKnowledgeElement(null, null, null));
	}

	@Test
	@NonTransactional
	public void testElementFilledUserNullParentNull() {
		assertNull(manager.insertKnowledgeElement(decisionKnowledgeElement, null, null));
	}

	@Test
	@NonTransactional
	public void testElementNullUserFilledParentNull() {
		assertNull(manager.insertKnowledgeElement(null, null, null));
	}

	@Test
	@NonTransactional
	public void testElementFilledUserFilledParentNull() {
		assertNull(manager.insertKnowledgeElement(decisionKnowledgeElement, null, null));
	}

	@Test
	@NonTransactional
	public void testElementNullUserNullParentFilled() {
		assertNull(manager.insertKnowledgeElement(null, null, null));
	}

	@Test
	@NonTransactional
	public void testElementFilledUserNullParentFilled() {
		assertNull(manager.insertKnowledgeElement(decisionKnowledgeElement, null, null));
	}

	@Test
	@NonTransactional
	public void testElementFilledUserFilledParentFilled() {
		assertNotNull(manager.insertKnowledgeElement(element, JiraUsers.SYS_ADMIN.getApplicationUser(),
				decisionKnowledgeElement));
	}

}
