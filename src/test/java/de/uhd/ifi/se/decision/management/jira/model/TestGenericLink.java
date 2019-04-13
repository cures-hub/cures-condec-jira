package de.uhd.ifi.se.decision.management.jira.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.management.jira.mocks.MockUserManager;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.model.impl.LinkImpl;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.GenericLinkManager;
import de.uhd.ifi.se.decision.management.jira.persistence.JiraIssueTextPersistenceManager;
import net.java.ao.EntityManager;
import net.java.ao.test.jdbc.NonTransactional;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
@net.java.ao.test.jdbc.Data(TestSetUpWithIssues.AoSentenceTestDatabaseUpdater.class)
public class TestGenericLink extends TestSetUpWithIssues {

	private EntityManager entityManager;

	@Before
	public void setUp() {
		initialization();
		TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate(),
				new MockUserManager());

		createGlobalIssue();
		addCommentsToIssue("this is a testSentence. This a second one. And a third one");
	}

	private void addCommentsToIssue(String comment) {
		// Get the current logged in user
		ApplicationUser currentUser = ComponentAccessor.getUserManager().getUserByName("NoFails");
		// Get access to the Jira comment and component manager
		CommentManager commentManager = ComponentAccessor.getCommentManager();
		// Get the last comment entered in on the issue to a String
		commentManager.create(issue, currentUser, comment, true);
	}

	@Test
	@NonTransactional
	public void testSecondConstructor() {
		Comment comment = ComponentAccessor.getCommentManager().getLastComment(issue);
		List<PartOfJiraIssueText> sentences = JiraIssueTextPersistenceManager.getPartsOfComment(comment);
		DecisionKnowledgeElement element = new DecisionKnowledgeElementImpl(issue);
		PartOfJiraIssueText sentence = sentences.get(0);
		Link link = new LinkImpl(sentence, element);
		assertTrue(link.getDestinationElement().getId() == issue.getId());
		assertTrue(link.getSourceElement().getId() == sentence.getId());

		assertTrue(link.getDestinationElement().getDocumentationLocation() == DocumentationLocation.JIRAISSUE);
		assertTrue(link.getSourceElement().getDocumentationLocation() == DocumentationLocation.JIRAISSUETEXT);
	}

	@Test
	@NonTransactional
	public void testThirdConstructor() {
		Comment comment = ComponentAccessor.getCommentManager().getLastComment(issue);
		List<PartOfJiraIssueText> sentences = JiraIssueTextPersistenceManager.getPartsOfComment(comment);
		DecisionKnowledgeElement element = new DecisionKnowledgeElementImpl(issue);
		PartOfJiraIssueText sentence = sentences.get(0);
		Link link = new LinkImpl(sentence, element, "contain");
		assertTrue(link.getDestinationElement().getId() == issue.getId());
		assertTrue(link.getSourceElement().getId() == sentence.getId());

		assertTrue(link.getDestinationElement().getDocumentationLocation() == DocumentationLocation.JIRAISSUE);
		assertTrue(link.getSourceElement().getDocumentationLocation() == DocumentationLocation.JIRAISSUETEXT);
		assertTrue(link.getType().equals("contain"));
	}

	@Test
	@NonTransactional
	public void testSimpleLink() {
		Link link = new LinkImpl();
		DecisionKnowledgeElement element = new DecisionKnowledgeElementImpl(issue);
		link.setDestinationElement(element);
		Comment comment = ComponentAccessor.getCommentManager().getLastComment(issue);
		List<PartOfJiraIssueText> sentences = JiraIssueTextPersistenceManager.getPartsOfComment(comment);
		PartOfJiraIssueText sentence = sentences.get(0);
		link.setSourceElement(sentence);

		GenericLinkManager.insertLink(link, null);

		assertNotNull(link.getOppositeElement(sentence));
		assertNotNull(link.getOppositeElement(issue.getId()));
	}

	@Test
	@NonTransactional
	public void testSimpleLinkFlipped() {
		Link link = new LinkImpl();
		DecisionKnowledgeElement element = new DecisionKnowledgeElementImpl(issue);
		link.setSourceElement(element);
		Comment comment = ComponentAccessor.getCommentManager().getLastComment(issue);
		List<PartOfJiraIssueText> sentences = JiraIssueTextPersistenceManager.getPartsOfComment(comment);
		PartOfJiraIssueText sentence = sentences.get(0);
		link.setDestinationElement(sentence);

		GenericLinkManager.insertLink(link, null);

		assertNotNull(link.getOppositeElement(sentence.getId()));
		assertNotNull(link.getOppositeElement(issue.getId()));
	}

	@Test
	@NonTransactional
	public void testLinkSentenceSentence() {

		Link link = new LinkImpl();

		Comment comment = ComponentAccessor.getCommentManager().getLastComment(issue);
		List<PartOfJiraIssueText> sentences = JiraIssueTextPersistenceManager.getPartsOfComment(comment);
		PartOfJiraIssueText s = sentences.get(0);
		PartOfJiraIssueText s1 = sentences.get(1);

		link.setSourceElement(s1);
		link.setDestinationElement(s);

		GenericLinkManager.insertLink(link, null);

		assertNotNull(link.getOppositeElement(s.getId()));
		assertNotNull(link.getOppositeElement(s1.getId()));
	}

	@Test
	@NonTransactional
	public void testLinkIssueIssue() {

		Link link = new LinkImpl();

		DecisionKnowledgeElement element = new DecisionKnowledgeElementImpl(issue);
		link.setSourceElement(element);
		link.setDestinationElement(element);
		GenericLinkManager.insertLink(link, null);

		assertNotNull(link.getOppositeElement(issue.getId()));
		assertNotNull(link.getOppositeElement(issue.getId()));
	}

	@Test
	@NonTransactional
	public void testLinkGetBothElements() {
		Link link = new LinkImpl();
		DecisionKnowledgeElement element = new DecisionKnowledgeElementImpl(issue);
		link.setSourceElement(element);
		link.setDestinationElement(element);
		GenericLinkManager.insertLink(link, null);

		assertTrue(link.getBothElements().size() == 2);
		assertNotNull(link.getBothElements().get(0));
		assertNotNull(link.getBothElements().get(1));
	}

	@Test
	@NonTransactional
	public void testIsValidWithValidLink() {
		Link link = new LinkImpl();
		DecisionKnowledgeElement element = new DecisionKnowledgeElementImpl(issue);
		link.setSourceElement(element);
		link.setDestinationElement(element);
		GenericLinkManager.insertLink(link, null);

		assertTrue(link.isValid());
	}

	@Test
	@NonTransactional
	public void testIsIssueLinkWithValidLink() {
		Comment comment = ComponentAccessor.getCommentManager().getLastComment(issue);
		List<PartOfJiraIssueText> sentences = JiraIssueTextPersistenceManager.getPartsOfComment(comment);
		PartOfJiraIssueText sentence = sentences.get(0);
		DecisionKnowledgeElement element = new DecisionKnowledgeElementImpl(issue);
		Link link = new LinkImpl(sentence, element);
		assertTrue(link.getDestinationElement().getId() == issue.getId());
		assertTrue(link.getSourceElement().getId() == sentence.getId());

		assertTrue(link.getDestinationElement().getDocumentationLocation() == DocumentationLocation.JIRAISSUE);
		assertTrue(link.getSourceElement().getDocumentationLocation() == DocumentationLocation.JIRAISSUETEXT);

		GenericLinkManager.insertLink(link, null);

		assertTrue(link.isValid());
		assertFalse(link.isIssueLink());
	}

	@Test
	@NonTransactional
	public void testToStringToBeatCodeCoverage() {
		Comment comment = ComponentAccessor.getCommentManager().getLastComment(issue);
		List<PartOfJiraIssueText> sentences = JiraIssueTextPersistenceManager.getPartsOfComment(comment);
		// JiraIssueComment comment = new JiraIssueCommentImpl();
		PartOfJiraIssueText sentence = sentences.get(0);
		DecisionKnowledgeElement element = new DecisionKnowledgeElementImpl(issue);
		Link link = new LinkImpl(sentence, element);

		assertEquals("s1 to i30", link.toString());
	}

}