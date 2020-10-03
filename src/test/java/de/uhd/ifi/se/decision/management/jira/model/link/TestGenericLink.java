package de.uhd.ifi.se.decision.management.jira.model.link;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.jira.issue.comments.CommentManager;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.LinkType;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.GenericLinkManager;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssueTextPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.test.jdbc.NonTransactional;

public class TestGenericLink extends TestSetUp {

	private Issue issue;

	@Before
	public void setUp() {
		init();
		issue = ComponentAccessor.getIssueManager().getIssueObject("TEST-30");
		addCommentsToIssue("this is a testSentence. This a second one. And a third one");
	}

	private void addCommentsToIssue(String comment) {
		// Get the current logged in user
		ApplicationUser currentUser = JiraUsers.SYS_ADMIN.getApplicationUser();
		// Get access to the Jira comment and component manager
		CommentManager commentManager = ComponentAccessor.getCommentManager();
		// Get the last comment entered in on the issue to a String
		commentManager.create(issue, currentUser, comment, true);
	}

	@Test
	@NonTransactional
	public void testSecondConstructor() {
		Comment comment = ComponentAccessor.getCommentManager().getLastComment(issue);
		JiraIssueTextPersistenceManager persistenceManager = KnowledgePersistenceManager.getOrCreate("TEST")
				.getJiraIssueTextManager();
		List<PartOfJiraIssueText> sentences = persistenceManager.insertPartsOfComment(comment);
		KnowledgeElement element = new KnowledgeElement(issue);
		PartOfJiraIssueText sentence = sentences.get(0);
		Link link = new Link(sentence, element);
		assertTrue(link.getTarget().getId() == issue.getId());
		assertTrue(link.getSource().getId() == sentence.getId());

		assertTrue(link.getTarget().getDocumentationLocation() == DocumentationLocation.JIRAISSUE);
		assertTrue(link.getSource().getDocumentationLocation() == DocumentationLocation.JIRAISSUETEXT);
	}

	/*
	 * The constructor was removed!
	 */
	@Test
	@Ignore
	@NonTransactional
	public void testThirdConstructor() {
		Comment comment = ComponentAccessor.getCommentManager().getLastComment(issue);
		JiraIssueTextPersistenceManager persistenceManager = KnowledgePersistenceManager.getOrCreate("TEST")
				.getJiraIssueTextManager();
		List<PartOfJiraIssueText> sentences = persistenceManager.insertPartsOfComment(comment);
		KnowledgeElement element = new KnowledgeElement(issue);
		PartOfJiraIssueText sentence = sentences.get(0);
		Link link = new Link(sentence, element, LinkType.getDefaultLinkType());
		assertTrue(link.getTarget().getId() == issue.getId());
		assertTrue(link.getSource().getId() == sentence.getId());

		assertTrue(link.getTarget().getDocumentationLocation() == DocumentationLocation.JIRAISSUE);
		assertTrue(link.getSource().getDocumentationLocation() == DocumentationLocation.JIRAISSUETEXT);
		assertTrue(link.getTypeAsString().equals("contain"));
	}

	@Test
	@NonTransactional
	public void testSimpleLink() {
		KnowledgeElement element = new KnowledgeElement(issue);
		Comment comment = ComponentAccessor.getCommentManager().getLastComment(issue);
		JiraIssueTextPersistenceManager persistenceManager = KnowledgePersistenceManager.getOrCreate("TEST")
				.getJiraIssueTextManager();
		List<PartOfJiraIssueText> sentences = persistenceManager.insertPartsOfComment(comment);
		PartOfJiraIssueText sentence = sentences.get(0);
		Link link = new Link(sentence, element);
		GenericLinkManager.insertLink(link, null);

		assertNotNull(link.getOppositeElement(sentence));
		assertNotNull(link.getOppositeElement(issue.getId()));
	}

	@Test
	@NonTransactional
	public void testSimpleLinkFlipped() {
		KnowledgeElement element = new KnowledgeElement(issue);
		Comment comment = ComponentAccessor.getCommentManager().getLastComment(issue);
		JiraIssueTextPersistenceManager persistenceManager = KnowledgePersistenceManager.getOrCreate("TEST")
				.getJiraIssueTextManager();
		List<PartOfJiraIssueText> sentences = persistenceManager.insertPartsOfComment(comment);
		PartOfJiraIssueText sentence = sentences.get(0);
		Link link = new Link(element, sentence);

		GenericLinkManager.insertLink(link, null);

		assertNotNull(link.getOppositeElement(sentence.getId()));
		assertNotNull(link.getOppositeElement(issue.getId()));
	}

	@Test
	@NonTransactional
	public void testLinkSentenceSentence() {
		Comment comment = ComponentAccessor.getCommentManager().getLastComment(issue);
		JiraIssueTextPersistenceManager persistenceManager = KnowledgePersistenceManager.getOrCreate("TEST")
				.getJiraIssueTextManager();
		List<PartOfJiraIssueText> sentences = persistenceManager.insertPartsOfComment(comment);
		PartOfJiraIssueText s = sentences.get(0);
		PartOfJiraIssueText s1 = sentences.get(1);
		Link link = new Link(s1, s);

		GenericLinkManager.insertLink(link, null);

		assertNotNull(link.getOppositeElement(s.getId()));
		assertNotNull(link.getOppositeElement(s1.getId()));
	}

	@Test
	@NonTransactional
	public void testLinkIssueIssue() {
		KnowledgeElement element = new KnowledgeElement(issue);
		Link link = new Link(element, element);
		GenericLinkManager.insertLink(link, null);

		assertNotNull(link.getOppositeElement(issue.getId()));
		assertNotNull(link.getOppositeElement(issue.getId()));
	}

	@Test
	@NonTransactional
	public void testLinkGetBothElements() {
		KnowledgeElement element = new KnowledgeElement(issue);
		Link link = new Link(element, element);
		GenericLinkManager.insertLink(link, null);

		assertTrue(link.getBothElements().size() == 2);
		assertNotNull(link.getBothElements().get(0));
		assertNotNull(link.getBothElements().get(1));
	}

	@Test
	@NonTransactional
	public void testIsValidWithValidLink() {
		KnowledgeElement element = new KnowledgeElement(issue);
		Link link = new Link(element, element);
		GenericLinkManager.insertLink(link, null);

		assertTrue(link.isValid());
	}

	@Test
	@NonTransactional
	public void testIsIssueLinkWithValidLink() {
		Comment comment = ComponentAccessor.getCommentManager().getLastComment(issue);
		JiraIssueTextPersistenceManager persistenceManager = KnowledgePersistenceManager.getOrCreate("TEST")
				.getJiraIssueTextManager();
		List<PartOfJiraIssueText> sentences = persistenceManager.insertPartsOfComment(comment);
		PartOfJiraIssueText sentence = sentences.get(0);
		KnowledgeElement element = new KnowledgeElement(issue);
		Link link = new Link(sentence, element);
		assertTrue(link.getTarget().getId() == issue.getId());
		assertTrue(link.getSource().getId() == sentence.getId());

		assertTrue(link.getTarget().getDocumentationLocation() == DocumentationLocation.JIRAISSUE);
		assertTrue(link.getSource().getDocumentationLocation() == DocumentationLocation.JIRAISSUETEXT);

		GenericLinkManager.insertLink(link, null);

		assertTrue(link.isValid());
		assertFalse(link.isIssueLink());
	}

	@Test
	@NonTransactional
	public void testToStringToBeatCodeCoverage() {
		Comment comment = ComponentAccessor.getCommentManager().getLastComment(issue);
		JiraIssueTextPersistenceManager persistenceManager = KnowledgePersistenceManager.getOrCreate("TEST")
				.getJiraIssueTextManager();
		List<PartOfJiraIssueText> sentences = persistenceManager.insertPartsOfComment(comment);
		// JiraIssueComment comment = new JiraIssueCommentImpl();
		PartOfJiraIssueText sentence = sentences.get(0);
		KnowledgeElement element = new KnowledgeElement(issue);
		Link link = new Link(sentence, element);

		assertEquals("s1 to i30", link.toString());
	}

}