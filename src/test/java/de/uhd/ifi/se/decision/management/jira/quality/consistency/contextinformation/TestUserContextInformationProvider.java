package de.uhd.ifi.se.decision.management.jira.quality.consistency.contextinformation;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.mock.issue.MockIssue;
import com.atlassian.jira.user.MockApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.quality.consistency.LinkSuggestionConfiguration;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraProjects;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestUserContextInformationProvider extends TestSetUp {

	private List<Issue> testIssues;
	private ContextInformationProvider contextInformationProvider;
	private KnowledgeElement element;

	@Before
	public void setUp() {
		TestSetUp.init();
		testIssues = JiraIssues.createJiraIssues(JiraProjects.getTestProject());

		MockIssue i1 = (MockIssue) testIssues.get(0);
		i1.setCreatorId(JiraUsers.SYS_ADMIN.createApplicationUser().getKey());
		i1.setAssignee(new MockApplicationUser("TESTUSER"));
		element = new KnowledgeElement(i1);

		LinkSuggestionConfiguration linkSuggestionConfiguration = ConfigPersistenceManager
				.getLinkSuggestionConfiguration("TEST");
		linkSuggestionConfiguration.setMinProbability(0);
		ConfigPersistenceManager.saveLinkSuggestionConfiguration("TEST", linkSuggestionConfiguration);

		contextInformationProvider = new UserContextInformationProvider();
	}

	@Test
	public void testId() {
		assertEquals("UserCIP_equalCreatorOrEqualAssignee", contextInformationProvider.getId());
	}

	@Test
	public void testSameUser() {
		MockIssue i2 = (MockIssue) testIssues.get(1);
		i2.setCreatorId(JiraUsers.SYS_ADMIN.createApplicationUser().getKey());
		i2.setAssignee(new MockApplicationUser("TESTUSER"));
		List<KnowledgeElement> testIssueList = Collections.singletonList(new KnowledgeElement(i2));
		contextInformationProvider.assessRelation(element, testIssueList);
		assertEquals(1., contextInformationProvider.getLinkSuggestions().get(0).getScore().getTotal(), 0);
	}

	@Test
	public void testDifferentUser() {
		MockIssue i2 = (MockIssue) testIssues.get(1);
		List<KnowledgeElement> testIssueList = Collections.singletonList(new KnowledgeElement(i2));
		i2.setAssignee(new MockApplicationUser("NOT_TESTUSER"));
		contextInformationProvider.assessRelation(element, testIssueList);
		assertEquals(1., contextInformationProvider.getLinkSuggestions().get(0).getScore().getTotal(), 0);
		contextInformationProvider = new UserContextInformationProvider();

		i2.setAssignee(new MockApplicationUser("TESTUSER"));
		i2.setCreatorId(JiraUsers.BLACK_HEAD.createApplicationUser().getKey());
		contextInformationProvider.assessRelation(element, testIssueList);
		assertEquals(1., contextInformationProvider.getLinkSuggestions().get(0).getScore().getTotal(), 0);
		contextInformationProvider = new UserContextInformationProvider();

		i2.setAssignee(new MockApplicationUser("NOT_TESTUSER"));
		contextInformationProvider.assessRelation(element, testIssueList);
		assertEquals(1., contextInformationProvider.getLinkSuggestions().get(0).getScore().getTotal(), 0);
		contextInformationProvider = new UserContextInformationProvider();

		i2.setAssignee(null);
		i2.setCreatorId(null);
		i2.setReporter(null);
		testIssueList = Collections.singletonList(new KnowledgeElement(i2));
		contextInformationProvider.assessRelation(element, testIssueList);
		assertEquals(1., contextInformationProvider.getLinkSuggestions().get(0).getScore().getTotal(), 0);

		contextInformationProvider.assessRelation(new KnowledgeElement(), testIssueList);
		assertEquals(1., contextInformationProvider.getLinkSuggestions().get(0).getScore().getTotal(), 0);
	}
}
