package de.uhd.ifi.se.decision.management.jira.extraction.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.comments.CommentManager;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.extraction.model.impl.CommentImpl;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.management.jira.mocks.MockUserManager;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.LinkImpl;
import de.uhd.ifi.se.decision.management.jira.persistence.GenericLinkManager;
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

		createLocalIssue();
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
		Comment comment = new CommentImpl(ComponentAccessor.getCommentManager().getLastComment(issue), true);
		DecisionKnowledgeElement element = new DecisionKnowledgeElementImpl(issue);
		Sentence sentence = comment.getSentences().get(0);
		Link link = new LinkImpl(sentence, element);
		assertTrue(link.getDestinationElement().getId() == issue.getId());
		assertTrue(link.getSourceElement().getId() == sentence.getId());

		assertTrue(link.getDestinationElement().getDocumentationLocation() == DocumentationLocation.JIRAISSUE);
		assertTrue(link.getSourceElement().getDocumentationLocation() == DocumentationLocation.JIRAISSUECOMMENT);
	}

	@Test
	@NonTransactional
	public void testThirdConstructor() {
		Comment comment = new CommentImpl(ComponentAccessor.getCommentManager().getLastComment(issue), true);
		DecisionKnowledgeElement element = new DecisionKnowledgeElementImpl(issue);
		Sentence sentence = comment.getSentences().get(0);
		Link link = new LinkImpl(sentence, element, "contain");
		assertTrue(link.getDestinationElement().getId() == issue.getId());
		assertTrue(link.getSourceElement().getId() == sentence.getId());

		assertTrue(link.getDestinationElement().getDocumentationLocation() == DocumentationLocation.JIRAISSUE);
		assertTrue(link.getSourceElement().getDocumentationLocation() == DocumentationLocation.JIRAISSUECOMMENT);
		assertTrue(link.getType().equals("contain"));
	}

	@Test
	@NonTransactional
	public void testSimpleLink() {
		Link link = new LinkImpl();
		DecisionKnowledgeElement element = new DecisionKnowledgeElementImpl(issue);
		link.setDestinationElement(element);
		Comment comment = new CommentImpl(ComponentAccessor.getCommentManager().getLastComment(issue), true);
		Sentence sentence = comment.getSentences().get(0);
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
		Comment comment = new CommentImpl(ComponentAccessor.getCommentManager().getLastComment(issue), true);
		Sentence sentence = comment.getSentences().get(0);
		link.setDestinationElement(sentence);

		GenericLinkManager.insertLink(link, null);

		assertNotNull(link.getOppositeElement(sentence.getId()));
		assertNotNull(link.getOppositeElement(issue.getId()));
	}

	@Test
	@NonTransactional
	public void testLinkSentenceSentence() {

		Link link = new LinkImpl();

		Comment comment = new CommentImpl(ComponentAccessor.getCommentManager().getLastComment(issue), true);
		Sentence s = comment.getSentences().get(0);
		Sentence s1 = comment.getSentences().get(1);

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
		Comment comment = new CommentImpl(ComponentAccessor.getCommentManager().getLastComment(issue), true);
		Sentence sentence = comment.getSentences().get(0);
		DecisionKnowledgeElement element = new DecisionKnowledgeElementImpl(issue);
		Link link = new LinkImpl(sentence, element);
		assertTrue(link.getDestinationElement().getId() == issue.getId());
		assertTrue(link.getSourceElement().getId() == sentence.getId());

		assertTrue(link.getDestinationElement().getDocumentationLocation() == DocumentationLocation.JIRAISSUE);
		assertTrue(link.getSourceElement().getDocumentationLocation() == DocumentationLocation.JIRAISSUECOMMENT);

		GenericLinkManager.insertLink(link, null);

		assertTrue(link.isValid());
		assertFalse(link.isIssueLink());
	}

	@Test
	@NonTransactional
	public void testToStringToBeatCodeCoverage() {
		Comment comment = new CommentImpl(ComponentAccessor.getCommentManager().getLastComment(issue), true);
		Sentence sentence = comment.getSentences().get(0);
		DecisionKnowledgeElement element = new DecisionKnowledgeElementImpl(issue);
		Link link = new LinkImpl(sentence, element);

		assertEquals("s1 to i30", link.toString());
	}

}