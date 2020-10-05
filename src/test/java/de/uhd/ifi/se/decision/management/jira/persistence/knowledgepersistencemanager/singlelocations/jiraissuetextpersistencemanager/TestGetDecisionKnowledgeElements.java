package de.uhd.ifi.se.decision.management.jira.persistence.knowledgepersistencemanager.singlelocations.jiraissuetextpersistencemanager;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssueTextPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.test.jdbc.NonTransactional;

public class TestGetDecisionKnowledgeElements extends TestSetUp {

	protected static JiraIssueTextPersistenceManager manager;
	protected static ApplicationUser user;
	protected static KnowledgeElement element;

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
		List<PartOfJiraIssueText> sentences = JiraIssues.getSentencesForCommentText(
				"some sentence in front. {issue} testobject {issue} some sentence in the back.");
		assertEquals(KnowledgeType.ISSUE, sentences.get(1).getType());

		List<KnowledgeElement> listWithObjects = new JiraIssueTextPersistenceManager("TEST")
				.getKnowledgeElements();
		// TODO Why 4, not 3?
		assertEquals(4, listWithObjects.size());
	}

	@Test
	@NonTransactional
	public void testGetAllElementsFromAoByTypeAlternative() {
		List<PartOfJiraIssueText> sentences = JiraIssues.getSentencesForCommentText(
				"some sentence in front. {alternative} testobject {alternative} some sentence in the back.");
		assertEquals(KnowledgeType.ALTERNATIVE, sentences.get(1).getType());

		List<KnowledgeElement> listWithObjects = new JiraIssueTextPersistenceManager("TEST")
				.getKnowledgeElements();
		assertEquals(4, listWithObjects.size());
	}

	@Test
	@NonTransactional
	public void testGetAllElementsFromAoByArgumentType() {
		List<PartOfJiraIssueText> sentences = JiraIssues.getSentencesForCommentText(
				"some sentence in front. {con} testobject {con} some sentence in the back.");
		assertEquals(KnowledgeType.OTHER, sentences.get(0).getType());
		assertEquals(KnowledgeType.CON, sentences.get(1).getType());
		assertEquals(KnowledgeType.OTHER, sentences.get(2).getType());
		assertEquals(3, sentences.size());

		List<KnowledgeElement> listWithObjects = new JiraIssueTextPersistenceManager("TEST")
				.getKnowledgeElements();
		assertEquals(4, listWithObjects.size());
	}

	@Test
	@NonTransactional
	public void testGetAllElementsFromAoByEmptyType() {
		List<PartOfJiraIssueText> sentences = JiraIssues.getSentencesForCommentText(
				"some sentence in front.  {pro} testobject {pro} some sentence in the back.");
		assertEquals(KnowledgeType.OTHER, sentences.get(2).getType());

		List<KnowledgeElement> listWithObjects = new JiraIssueTextPersistenceManager("TEST")
				.getKnowledgeElements();
		assertEquals(4, listWithObjects.size());
	}
}
