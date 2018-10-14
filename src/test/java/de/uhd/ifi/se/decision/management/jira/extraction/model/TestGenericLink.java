package de.uhd.ifi.se.decision.management.jira.extraction.model;

import static org.junit.Assert.*;

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
import de.uhd.ifi.se.decision.management.jira.extraction.model.impl.GenericLinkImpl;
import de.uhd.ifi.se.decision.management.jira.extraction.persistence.LinkBetweenDifferentEntitiesEntity;
import de.uhd.ifi.se.decision.management.jira.mocks.MockDefaultUserManager;
import de.uhd.ifi.se.decision.management.jira.mocks.MockIssueManagerSelfImpl;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
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
				new MockDefaultUserManager());

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
		ApplicationUser currentUser = ComponentAccessor.getUserManager().getUser("NoFails");
		// Get access to the Jira comment and component manager
		CommentManager commentManager = ComponentAccessor.getCommentManager();
		// Get the last comment entered in on the issue to a String
		commentManager.create(issue, currentUser, comment, true);
	}

	@Test
	@NonTransactional
	public void testSecondConstructor() {
		GenericLinkImpl link = new GenericLinkImpl("i1337","i1338");
		assertTrue(link.getIdOfDestinationElement().equals("i1337"));
		assertTrue(link.getIdOfSourceElement().equals("i1338"));
	}
	
	@Test
	@NonTransactional
	public void testThirdConstructor() {
		GenericLinkImpl link = new GenericLinkImpl("i1337","i1338","contain");
		assertTrue(link.getIdOfDestinationElement().equals("i1337"));
		assertTrue(link.getIdOfSourceElement().equals("i1338"));
		assertTrue(link.getType().equals("contain"));
		
	}
	
	
	@Test
	@NonTransactional
	public void testSimpleLink() {

		GenericLinkImpl link = new GenericLinkImpl();
		link.setIdOfDestinationElement("i" + issue.getId());
		CommentImpl c = new CommentImpl(ComponentAccessor.getCommentManager().getLastComment(issue));
		Sentence s = c.getSentences().get(0);
		link.setIdOfSourceElement("s" + s.getId());

		GenericLinkManager.insertGenericLink(link, null);

		assertNotNull(link.getOpposite("s" + s.getId()));
		assertNotNull(link.getOpposite("i" + issue.getId()));
	}

	@Test
	@NonTransactional
	public void testSimpleLinkFlipped() {

		GenericLinkImpl link = new GenericLinkImpl();
		link.setIdOfSourceElement("i" + issue.getId());
		CommentImpl c = new CommentImpl(ComponentAccessor.getCommentManager().getLastComment(issue));
		Sentence s = c.getSentences().get(0);
		link.setIdOfDestinationElement("s" + s.getId());

		GenericLinkManager.insertGenericLink(link, null);

		assertNotNull(link.getOpposite("s" + s.getId()));
		assertNotNull(link.getOpposite("i" + issue.getId()));
	}

	@Test
	@NonTransactional
	public void testLinkSentenceSentence() {

		GenericLinkImpl link = new GenericLinkImpl();

		CommentImpl c = new CommentImpl(ComponentAccessor.getCommentManager().getLastComment(issue));
		Sentence s = c.getSentences().get(0);
		Sentence s1 = c.getSentences().get(1);

		link.setIdOfSourceElement("s" + s1.getId());
		link.setIdOfDestinationElement("s" + s.getId());

		GenericLinkManager.insertGenericLink(link, null);

		assertNotNull(link.getOpposite("s" + s.getId()));
		assertNotNull(link.getOpposite("s" + s1.getId()));
	}

	@Test
	@NonTransactional
	public void testLinkIssueIssue() {

		GenericLinkImpl link = new GenericLinkImpl();

		link.setIdOfSourceElement("i" + issue.getId());
		link.setIdOfDestinationElement("i" + issue.getId());
		GenericLinkManager.insertGenericLink(link, null);

		assertNotNull(link.getOpposite("i" + issue.getId()));
		assertNotNull(link.getOpposite("i" + issue.getId()));
	}

	@Test
	@NonTransactional
	public void testLinkGetBothElements() {

		GenericLinkImpl link = new GenericLinkImpl();

		link.setIdOfSourceElement("i" + issue.getId());
		link.setIdOfDestinationElement("i" + issue.getId());
		GenericLinkManager.insertGenericLink(link, null);

		assertTrue(link.getBothElements().size() == 2);
		assertNotNull(link.getBothElements().get(0));
		assertNotNull(link.getBothElements().get(1));
	}
	
	

	@Test
	@NonTransactional
	public void testIsValidWithValidLink() {

		GenericLinkImpl link = new GenericLinkImpl();

		link.setIdOfSourceElement("i" + issue.getId());
		link.setIdOfDestinationElement("i" + issue.getId());
		GenericLinkManager.insertGenericLink(link, null);

		assertTrue(link.isValid());
	}
	
	@Test
	@NonTransactional
	public void testIsValidWithInValidLink() {

		GenericLinkImpl link = new GenericLinkImpl();

		link.setIdOfSourceElement("i" + 1233);
		link.setIdOfDestinationElement("i" + 13423);
		GenericLinkManager.insertGenericLink(link, null);

		assertFalse(link.isValid());
	}
	
	@Test
	@NonTransactional
	public void testToStringToBeatCodeCoverage() {

		GenericLinkImpl link = new GenericLinkImpl();

		link.setIdOfSourceElement("i" + 1233);
		link.setIdOfDestinationElement("i" + 13423);
		
		assertTrue(link.toString().equals("i1233 to i13423"));
	}

	

}