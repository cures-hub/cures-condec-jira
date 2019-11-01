package de.uhd.ifi.se.decision.management.jira.persistence.knowledgepersistencemanager.singlelocations.jiraissuetextpersistencemanager;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.extraction.TestTextSplitter;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.impl.JiraIssueTextPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.test.jdbc.NonTransactional;

public class TestGetDecisionKnowledgeElements extends TestSetUp {

	protected static JiraIssueTextPersistenceManager manager;
	protected static ApplicationUser user;
	protected static DecisionKnowledgeElement element;

	@Before
	public void setUp() {
		init();
		manager = new JiraIssueTextPersistenceManager("TEST");
		user = JiraUsers.SYS_ADMIN.getApplicationUser();
		element = JiraIssues.addElementToDataBase();
	}

	@Test
	@NonTransactional
	public void testGetAllElementsFromAoByType() {
		List<PartOfJiraIssueText> sentences = TestTextSplitter.getSentencesForCommentText(
				"some sentence in front. {issue} testobject {issue} some sentence in the back.");
		assertEquals(KnowledgeType.ISSUE, sentences.get(1).getType());

		List<DecisionKnowledgeElement> listWithObjects = new JiraIssueTextPersistenceManager("TEST")
				.getDecisionKnowledgeElements(KnowledgeType.ISSUE);
		assertEquals(1, listWithObjects.size());
	}

	@Test
	@NonTransactional
	public void testGetAllElementsFromAoByTypeAlternative() {
		List<PartOfJiraIssueText> sentences = TestTextSplitter.getSentencesForCommentText(
				"some sentence in front. {alternative} testobject {alternative} some sentence in the back.");
		assertEquals(KnowledgeType.ALTERNATIVE, sentences.get(1).getType());

		List<DecisionKnowledgeElement> listWithObjects = new JiraIssueTextPersistenceManager("TEST")
				.getDecisionKnowledgeElements(KnowledgeType.ALTERNATIVE);
		assertEquals(1, listWithObjects.size());
	}

	@Test
	@NonTransactional
	public void testGetAllElementsFromAoByArgumentType() {
		List<PartOfJiraIssueText> sentences = TestTextSplitter.getSentencesForCommentText(
				"some sentence in front. {con} testobject {con} some sentence in the back.");
		assertEquals(KnowledgeType.OTHER, sentences.get(0).getType());
		assertEquals(KnowledgeType.CON, sentences.get(1).getType());
		assertEquals(KnowledgeType.OTHER, sentences.get(2).getType());
		assertEquals(3, sentences.size());

		List<DecisionKnowledgeElement> listWithObjects = new JiraIssueTextPersistenceManager("TEST")
				.getDecisionKnowledgeElements(KnowledgeType.CON);
		// TODO Why 2 not 1?
		assertEquals(2, listWithObjects.size());
	}

	@Test
	@NonTransactional
	public void testGetAllElementsFromAoByEmptyType() {
		List<PartOfJiraIssueText> sentences = TestTextSplitter.getSentencesForCommentText(
				"some sentence in front.  {pro} testobject {pro} some sentence in the back.");
		assertEquals(KnowledgeType.OTHER, sentences.get(2).getType());

		List<DecisionKnowledgeElement> listWithObjects = new JiraIssueTextPersistenceManager("TEST")
				.getDecisionKnowledgeElements(KnowledgeType.OTHER);
		assertEquals(2, listWithObjects.size());
	}
}
