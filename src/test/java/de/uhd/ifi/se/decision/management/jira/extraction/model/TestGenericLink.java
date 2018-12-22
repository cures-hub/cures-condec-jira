package de.uhd.ifi.se.decision.management.jira.extraction.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.comments.CommentManager;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.issue.issuetype.MockIssueType;
import com.atlassian.jira.mock.issue.MockIssue;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.extraction.model.impl.CommentImpl;
import de.uhd.ifi.se.decision.management.jira.mocks.MockIssueManagerSelfImpl;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.management.jira.mocks.MockUserManager;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
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

	private MutableIssue issue;

	@Before
	public void setUp() {
		initialization();
		TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate(),
				new MockUserManager());

		createLocalIssue();
		addCommentsToIssue("this is a testSentence. This a second one. And a third one");

	}

	private void createLocalIssue() {
		Project project = ComponentAccessor.getProjectManager().getProjectByCurrentKey("TEST");
		issue = new MockIssue(2, "TEST-" + 2);
		((MockIssue) issue).setProjectId(project.getId());
		issue.setProjectObject(project);
		IssueType issueType = new MockIssueType(1, KnowledgeType.DECISION.toString().toLowerCase(Locale.ENGLISH));
		issue.setIssueType(issueType);
		issue.setSummary("Test");
		((MockIssueManagerSelfImpl) ComponentAccessor.getIssueManager()).addIssue(issue);
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
		CommentImpl c = new CommentImpl(ComponentAccessor.getCommentManager().getLastComment(issue), true);
		Sentence s = c.getSentences().get(0);
		Link link = new LinkImpl("s" + s.getId(), "i" + issue.getId());
		assertTrue(link.getIdOfDestinationElementWithPrefix().equals("i" + issue.getId()));
		assertTrue(link.getIdOfSourceElementWithPrefix().equals("s" + s.getId()));
	}

	@Test
	@NonTransactional
	public void testThirdConstructor() {
		CommentImpl c = new CommentImpl(ComponentAccessor.getCommentManager().getLastComment(issue), true);
		Sentence s = c.getSentences().get(0);
		Link link = new LinkImpl("i" + issue.getId(), "s" + s.getId(), "contain");
		assertTrue(link.getIdOfSourceElementWithPrefix().equals("i" + issue.getId()));
		assertTrue(link.getIdOfDestinationElementWithPrefix().equals("s" + s.getId()));
		assertTrue(link.getType().equals("contain"));
	}

	@Test
	@NonTransactional
	public void testSimpleLink() {

		Link link = new LinkImpl();
		link.setDestinationElement("i" + issue.getId());
		CommentImpl c = new CommentImpl(ComponentAccessor.getCommentManager().getLastComment(issue), true);
		Sentence s = c.getSentences().get(0);
		link.setSourceElement("s" + s.getId());

		GenericLinkManager.insertLink(link, null);

		assertNotNull(link.getOppositeElement("s" + s.getId()));
		assertNotNull(link.getOppositeElement("i" + issue.getId()));
	}

	@Test
	@NonTransactional
	public void testSimpleLinkFlipped() {

		Link link = new LinkImpl();
		link.setSourceElement("i" + issue.getId());
		CommentImpl c = new CommentImpl(ComponentAccessor.getCommentManager().getLastComment(issue), true);
		Sentence s = c.getSentences().get(0);
		link.setDestinationElement("s" + s.getId());

		GenericLinkManager.insertLink(link, null);

		assertNotNull(link.getOppositeElement("s" + s.getId()));
		assertNotNull(link.getOppositeElement("i" + issue.getId()));
	}

	@Test
	@NonTransactional
	public void testLinkSentenceSentence() {

		Link link = new LinkImpl();

		CommentImpl c = new CommentImpl(ComponentAccessor.getCommentManager().getLastComment(issue), true);
		Sentence s = c.getSentences().get(0);
		Sentence s1 = c.getSentences().get(1);

		link.setSourceElement("s" + s1.getId());
		link.setDestinationElement("s" + s.getId());

		GenericLinkManager.insertLink(link, null);

		assertNotNull(link.getOppositeElement("s" + s.getId()));
		assertNotNull(link.getOppositeElement("s" + s1.getId()));
	}

	@Test
	@NonTransactional
	public void testLinkIssueIssue() {

		Link link = new LinkImpl();

		link.setSourceElement("i" + issue.getId());
		link.setDestinationElement("i" + issue.getId());
		GenericLinkManager.insertLink(link, null);

		assertNotNull(link.getOppositeElement("i" + issue.getId()));
		assertNotNull(link.getOppositeElement("i" + issue.getId()));
	}

	@Test
	@NonTransactional
	public void testLinkGetBothElements() {

		Link link = new LinkImpl();

		link.setSourceElement("i" + issue.getId());
		link.setDestinationElement("i" + issue.getId());
		GenericLinkManager.insertLink(link, null);

		assertTrue(link.getBothElements().size() == 2);
		assertNotNull(link.getBothElements().get(0));
		assertNotNull(link.getBothElements().get(1));
	}

	@Test
	@NonTransactional
	public void testIsValidWithValidLink() {

		Link link = new LinkImpl();

		link.setSourceElement("i" + issue.getId());
		link.setDestinationElement("i" + issue.getId());
		GenericLinkManager.insertLink(link, null);

		assertTrue(link.isValid());
	}

	@Test
	@NonTransactional
	public void testIsIssueLinkWithValidLink() {

		CommentImpl c = new CommentImpl(ComponentAccessor.getCommentManager().getLastComment(issue), true);
		Sentence s = c.getSentences().get(0);
		Link link = new LinkImpl("s" + s.getId(), "i" + issue.getId());
		assertTrue(link.getIdOfDestinationElementWithPrefix().equals("i" + issue.getId()));
		assertTrue(link.getIdOfSourceElementWithPrefix().equals("s" + s.getId()));

		GenericLinkManager.insertLink(link, null);

		assertTrue(link.isValid());
		assertFalse(link.isIssueLink());
	}

	@Test
	@NonTransactional
	public void testToStringToBeatCodeCoverage() {

		CommentImpl c = new CommentImpl(ComponentAccessor.getCommentManager().getLastComment(issue), true);
		Sentence s = c.getSentences().get(0);
		Link link = new LinkImpl("s" + s.getId(), "i" + issue.getId());
		assertTrue(link.getIdOfDestinationElementWithPrefix().equals("i" + issue.getId()));
		assertTrue(link.getIdOfSourceElementWithPrefix().equals("s" + s.getId()));

		assertTrue(link.toString().equals("s1 to i2"));
	}

}