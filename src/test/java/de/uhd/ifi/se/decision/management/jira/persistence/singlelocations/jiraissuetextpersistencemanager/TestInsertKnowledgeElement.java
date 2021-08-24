package de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.jiraissuetextpersistencemanager;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssueTextPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;
import net.java.ao.test.jdbc.NonTransactional;

public class TestInsertKnowledgeElement extends TestSetUp {

	protected JiraIssueTextPersistenceManager manager;
	protected PartOfJiraIssueText element;
	protected KnowledgeElement unsolvedIssue;

	@Before
	public void setUp() {
		init();
		manager = new JiraIssueTextPersistenceManager("TEST");
		unsolvedIssue = KnowledgeElements.getUnsolvedDecisionProblem();
		element = JiraIssues.getSentencesForCommentText("{alternative}We could...{alternative}").get(0);
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
		assertNull(manager.insertKnowledgeElement(unsolvedIssue, null, null));
	}

	@Test
	@NonTransactional
	public void testElementNullUserFilledParentNull() {
		assertNull(manager.insertKnowledgeElement(null, JiraUsers.SYS_ADMIN.getApplicationUser(), null));
	}

	@Test
	@NonTransactional
	public void testElementFilledUserFilledParentNull() {
		assertNotNull(manager.insertKnowledgeElement(element, JiraUsers.SYS_ADMIN.getApplicationUser(), null));
	}

	@Test
	@NonTransactional
	public void testElementNullUserNullParentJiraIssue() {
		assertNull(manager.insertKnowledgeElement(null, null, unsolvedIssue));
	}

	@Test
	@NonTransactional
	public void testElementFilledUserNullParentJiraIssue() {
		assertNull(manager.insertKnowledgeElement(element, null, unsolvedIssue));
	}

	@Test
	@NonTransactional
	public void testElementFilledUserFilledParentJiraIssue() {
		assertNotNull(manager.insertKnowledgeElement(element, JiraUsers.SYS_ADMIN.getApplicationUser(), unsolvedIssue));
	}

	@Test
	@NonTransactional
	public void testElementFilledUserFilledParentDocumentedInComment() {
		KnowledgeElement parent = JiraIssues.getSentencesForCommentText("{issue}How to?{issue}").get(0);
		assertNotNull(manager.insertKnowledgeElement(element, JiraUsers.SYS_ADMIN.getApplicationUser(), parent));
	}

	@Test
	@NonTransactional
	public void testElementFilledUserFilledParentDocumentedInDescription() {
		PartOfJiraIssueText parent = JiraIssues.getSentencesForCommentText("{issue}How to?{issue}").get(0);
		parent.setCommentId(0);
		manager.updateInDatabase(parent);
		assertNotNull(manager.insertKnowledgeElement(element, JiraUsers.SYS_ADMIN.getApplicationUser(), parent));
	}

}
