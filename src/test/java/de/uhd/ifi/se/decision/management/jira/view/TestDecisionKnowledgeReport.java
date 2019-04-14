package de.uhd.ifi.se.decision.management.jira.view;

import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.jira.issue.comments.CommentManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.MockApplicationUser;
import com.atlassian.jira.web.action.ProjectActionSupport;

import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.extraction.TestTextSplitter;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.management.jira.mocks.MockUserManager;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.LinkType;
import de.uhd.ifi.se.decision.management.jira.model.impl.LinkImpl;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.AbstractPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.GenericLinkManager;
import de.uhd.ifi.se.decision.management.jira.persistence.JiraIssueTextPersistenceManager;
import net.java.ao.EntityManager;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.jdbc.NonTransactional;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(TestSetUpWithIssues.AoSentenceTestDatabaseUpdater.class)
public class TestDecisionKnowledgeReport extends TestSetUpWithIssues {

	private EntityManager entityManager;
	private DecisionKnowledgeReport report;
	private AbstractPersistenceManager persistenceStrategy;
	private List<PartOfJiraIssueText> sentences;

	@Before
	public void setUp() {
		initialization();
		TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate(),
				new MockUserManager());
		this.report = new DecisionKnowledgeReport();

		ProjectActionSupport pas = new MockProjectActionSupport();
		Map<String, String> params = new HashMap<String, String>();
		params.put("selectedProjectId", "1");
		params.put("issueType", "WI");
		this.report.validate(pas, params);

		persistenceStrategy = AbstractPersistenceManager.getDefaultPersistenceStrategy("TEST");
	}

	private MutableIssue createCommentStructureWithTestIssue(String text) {
		// 1) Check if Tree Element has no Children - Important!
		persistenceStrategy.getDecisionKnowledgeElement((long) 14);

		// 2) Add comment to issue
		MutableIssue issue = ComponentAccessor.getIssueManager().getIssueByCurrentKey("14");
		ComponentAccessor.getCommentManager().deleteCommentsForIssue(issue);
		ApplicationUser currentUser = ComponentAccessor.getUserManager().getUserByName("NoFails");
		CommentManager commentManager = ComponentAccessor.getCommentManager();
		Comment comment1 = commentManager.create(issue, currentUser, text, true);

		// 3) Manipulate Sentence object so it will be shown in the tree viewer
		this.sentences = JiraIssueTextPersistenceManager.getPartsOfComment(comment1);
		return issue;

	}

	// TODO
	@Test
	@NonTransactional
	public void testCreation() {
		assertNotNull(this.report);
		// assertNotNull(this.report.createValues(new MockProjectActionSupport()));
	}

	@Test(expected = Exception.class)
	@NonTransactional
	public void testWithObjects() {
		PartOfJiraIssueText partOfJiraIssueText = TestTextSplitter
				.getSentencesForCommentText("More Comment with some text").get(0);
		partOfJiraIssueText.setType(KnowledgeType.ALTERNATIVE);
		new JiraIssueTextPersistenceManager("").updateDecisionKnowledgeElement(partOfJiraIssueText, null);

		assertNotNull(this.report.createValues(new MockProjectActionSupport()));
	}

	// TODO
	@Test
	@NonTransactional
	public void testWithSingleIssueAndComment() {
		MutableIssue issue = createCommentStructureWithTestIssue("This is a testsentence for test purposes");

		// Map<String, Object> reportResult = this.report.createValues(new
		// MockProjectActionSupport());
		//
		// assertTrue(reportResult.get("numDecisionsPerIssueMap").toString().equals("{Test-1337=0}"));
		// assertTrue(reportResult.get("jiraIssuesWithoutLinksToDecision").toString().contains("Test-1337"));
		// assertTrue(reportResult.get("numLinksToDecision").toString().equals("{Has
		// Issue=0, Has no Issue=0}"));
		// assertTrue(reportResult.get("numCommentsPerIssueMap").toString().equals("{Test-1337=1}"));
		// assertTrue(reportResult.get("jiraIssuesWithoutLinksToIssue").toString().contains("Test-1337"));
		// assertTrue(reportResult.get("numAlternativeWoArgument").toString()
		// .equals("{Alternative without Argument=0, Alternative with Argument=0}"));
		// assertTrue(reportResult.get("numLinksToIssue").toString().equals("{Has no
		// Decision=0, Has Decision=0}"));
		// assertTrue(reportResult.get("numLinksToIssueTypeIssue").toString()
		// .equals("{No links from Work Item Issue=1, Links from Work Item Issue=0}"));
		// assertTrue(reportResult.get("numIssuesPerIssueMap").toString().contains("{Test-1337=0}"));
		// assertTrue(reportResult.get("numLinksToIssue").toString().contains("{Has no
		// Decision=0, Has Decision=0}"));
		// assertTrue(reportResult.get("numLinksToIssueTypeDecision").toString()
		// .equals("{No links from Work Item Decision=1, Links from Work Item
		// Decision=0}"));
		// assertTrue(reportResult.get("numRelevantSentences").toString()
		// .equals("{Relevant Sentences=0, Irrelevant Sentences=0}"));
		// assertTrue(reportResult.get("projectName").toString().equals("TEST"));
		// assertTrue(reportResult.get("numKnowledgeTypesPerIssue").toString()
		// .equals("{Alternative=0, Issue=0, Argument=0, Decision=0}"));
		ComponentAccessor.getCommentManager().deleteCommentsForIssue(issue);
	}

	// TODO
	@Test
	@NonTransactional
	public void testWithLinkedSentences() {
		MutableIssue issue = createCommentStructureWithTestIssue("This is a testsentence for test purposes");
		Link link = new LinkImpl(sentences.get(0), sentences.get(0), LinkType.CONTAIN);
		GenericLinkManager.insertLink(link, null);
		PartOfJiraIssueText sentence = sentences.get(0);
		sentence.setType(KnowledgeType.ISSUE);
		new JiraIssueTextPersistenceManager("").updateDecisionKnowledgeElement(sentence, null);

		// Map<String, Object> reportResult = this.report.createValues(new
		// MockProjectActionSupport());
		// assertTrue(reportResult.get("numKnowledgeTypesPerIssue").toString()
		// .contains("{Alternative=0, Issue=1, Argument=0, Decision=0}"));
		// assertTrue(reportResult.get("numLinkDistanceIssue").toString().equals("[1]"));

		ComponentAccessor.getCommentManager().deleteCommentsForIssue(issue);
	}

	private class MockProjectActionSupport extends ProjectActionSupport {

		private static final long serialVersionUID = -4361508663504224792L;

		@Override
		public ApplicationUser getLoggedInUser() {
			return new MockApplicationUser("NoFails");
		}
	}

}
