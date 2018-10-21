package de.uhd.ifi.se.decision.management.jira.extraction.model;

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
import de.uhd.ifi.se.decision.management.jira.extraction.model.util.HTMLCodeGeneratorForSentences;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.management.jira.mocks.MockUserManager;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import net.java.ao.EntityManager;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.jdbc.NonTransactional;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(TestComment.AoSentenceTestDatabaseUpdater.class)
public class TestHTMLCodeGenerator extends TestSetUpWithIssues {

	private EntityManager entityManager;

	private MutableIssue issue;

	@Before
	public void setUp() {
		initialization();
		TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate(),
				new MockUserManager());

		createLocalIssue();

	}

	private void createLocalIssue() {
		Project project = ComponentAccessor.getProjectManager().getProjectByCurrentKey("TEST");
		issue = new MockIssue(30, "TEST-" + 30);
		((MockIssue) issue).setProjectId(project.getId());
		issue.setProjectObject(project);
		IssueType issueType = new MockIssueType(1, KnowledgeType.DECISION.toString().toLowerCase(Locale.ENGLISH));
		issue.setIssueType(issueType);
		issue.setSummary("Test");
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
	public void testSimpleOutput() {
		HTMLCodeGeneratorForSentences html = new HTMLCodeGeneratorForSentences();
		addCommentsToIssue("This is a testsentence");
		CommentImpl c = new CommentImpl(ComponentAccessor.getCommentManager().getLastComment(issue));
		assertTrue(html.getCodedElement(c.getSentences().get(0)).length() > 0);
	}

	@Test
	@NonTransactional
	public void testRelevantOutput() {
		HTMLCodeGeneratorForSentences html = new HTMLCodeGeneratorForSentences();
		addCommentsToIssue("+1 I like this idea");
		CommentImpl c = new CommentImpl(ComponentAccessor.getCommentManager().getLastComment(issue));
		Sentence s = c.getSentences().get(0);
		s.setRelevant(true);
		assertTrue(html.getCodedElement(s).length() > 0);
	}

	@Test
	@NonTransactional
	public void testCodeTextOutput() {
		HTMLCodeGeneratorForSentences html = new HTMLCodeGeneratorForSentences();
		addCommentsToIssue("{code:java} int i = 0; {code}");
		CommentImpl c = new CommentImpl(ComponentAccessor.getCommentManager().getLastComment(issue));
		Sentence s = c.getSentences().get(0);
		s.setRelevant(false);
		assertTrue(html.getCodedElement(s).length() > 0);
	}

	@Test
	@NonTransactional
	public void testQuoteTextOutput() {
		HTMLCodeGeneratorForSentences html = new HTMLCodeGeneratorForSentences();
		addCommentsToIssue("{quote} int i = 0; {quote}");
		CommentImpl c = new CommentImpl(ComponentAccessor.getCommentManager().getLastComment(issue));
		Sentence s = c.getSentences().get(0);
		s.setRelevant(false);
		assertTrue(html.getCodedElement(s).length() > 0);
	}

	@Test
	@NonTransactional
	public void testHandCodedTextOutput() {
		HTMLCodeGeneratorForSentences html = new HTMLCodeGeneratorForSentences();
		addCommentsToIssue("[issue] this is a isue[/issue]");
		CommentImpl c = new CommentImpl(ComponentAccessor.getCommentManager().getLastComment(issue));
		Sentence s = c.getSentences().get(0);
		assertTrue(html.getCodedElement(s).length() > 0);
	}

	@Test
	@NonTransactional
	public void testRelevantOutputWithKnowledgeType() {
		HTMLCodeGeneratorForSentences html = new HTMLCodeGeneratorForSentences();
		addCommentsToIssue("+1 I like this idea");
		CommentImpl c = new CommentImpl(ComponentAccessor.getCommentManager().getLastComment(issue));
		Sentence s = c.getSentences().get(0);
		s.setRelevant(true);
		s.setType(KnowledgeType.ISSUE);
		assertTrue(html.getCodedElement(s).length() > 0);
	}

	@Test
	@NonTransactional
	public void testRelevantOutputWithKnowledgeTypeArgument() {
		HTMLCodeGeneratorForSentences html = new HTMLCodeGeneratorForSentences();
		addCommentsToIssue("+1 I like this idea");
		CommentImpl c = new CommentImpl(ComponentAccessor.getCommentManager().getLastComment(issue));
		Sentence s = c.getSentences().get(0);
		s.setRelevant(true);
		s.setType(KnowledgeType.ARGUMENT);
		s.setArgument("pro");
		assertTrue(html.getCodedElement(s).length() > 0);
	}

}
