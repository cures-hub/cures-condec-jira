package de.uhd.ifi.se.decision.management.jira.persistence.jiraissuetextpersistencemanager;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.jira.issue.comments.CommentManager;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.extraction.TestTextSplitter;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.management.jira.mocks.MockUserManager;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.model.text.impl.PartOfJiraIssueTextImpl;
import de.uhd.ifi.se.decision.management.jira.persistence.JiraIssueTextPersistenceManager;
import net.java.ao.EntityManager;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.jdbc.NonTransactional;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(TestSetUpWithIssues.AoSentenceTestDatabaseUpdater.class)
public class TestJiraIssueCommentPersistenceManagerSetUp extends TestSetUpWithIssues {

	private EntityManager entityManager;

	protected JiraIssueTextPersistenceManager manager;
	protected ApplicationUser user;
	protected PartOfJiraIssueText element;
	protected Comment comment1;
	protected DecisionKnowledgeElement decisionKnowledgeElement;

	@Before
	public void setUp() {
		initialization();
		TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate(),
				new MockUserManager());
		createGlobalIssue();
		manager = new JiraIssueTextPersistenceManager("TEST");
		user = ComponentAccessor.getUserManager().getUserByName("NoFails");
		addElementToDataBase();
		addDecisionKnowledgeElement();
	}

	protected void addElementToDataBase() {
		element = new PartOfJiraIssueTextImpl();
		element.setProject("TEST");
		element.setJiraIssueId(12);
		element.setId(1);
		element.setKey("TEST-12231");
		element.setType("Argument");
		element.setProject("TEST");
		element.setDescription("Old");
		element.setDocumentationLocation(DocumentationLocation.JIRAISSUETEXT);
		JiraIssueTextPersistenceManager.insertDecisionKnowledgeElement(element, user);
	}

	private void addDecisionKnowledgeElement() {
		decisionKnowledgeElement = new DecisionKnowledgeElementImpl();
		decisionKnowledgeElement.setProject("TEST");
		decisionKnowledgeElement.setId(1232);
		decisionKnowledgeElement.setKey("TEST-1232");
		decisionKnowledgeElement.setType("DECISION");
		decisionKnowledgeElement.setProject("TEST");
		decisionKnowledgeElement.setDescription("Old");
	}

	protected void addCommentsToIssue(String comment) {

		ComponentAccessor.getCommentManager().deleteCommentsForIssue(issue);
		// Get the current logged in user
		ApplicationUser currentUser = ComponentAccessor.getUserManager().getUserByName("NoFails");
		// Get access to the Jira comment and component manager
		CommentManager commentManager = ComponentAccessor.getCommentManager();
		// Get the last comment entered in on the issue to a String
		comment1 = commentManager.create(issue, currentUser, comment, true);
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
