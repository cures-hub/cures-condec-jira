package de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.contextinformation;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.mock.issue.MockIssue;
import com.atlassian.jira.user.MockApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.LinkRecommendationConfiguration;
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

		LinkRecommendationConfiguration linkSuggestionConfiguration = ConfigPersistenceManager
				.getLinkSuggestionConfiguration("TEST");
		linkSuggestionConfiguration.setMinProbability(0);
		ConfigPersistenceManager.saveLinkSuggestionConfiguration("TEST", linkSuggestionConfiguration);

		contextInformationProvider = new UserContextInformationProvider();
	}

	@Test
	public void testSameUser() {
		MockIssue i2 = (MockIssue) testIssues.get(1);
		i2.setCreatorId(JiraUsers.SYS_ADMIN.createApplicationUser().getKey());
		i2.setAssignee(new MockApplicationUser("TESTUSER"));
		contextInformationProvider.assessRelation(element, new KnowledgeElement(i2));
		assertEquals(1., contextInformationProvider.getLinkSuggestions().get(0).getScore().getValue(), 0);
	}

	@Test
	public void testDifferentUser() {
		MockIssue i2 = (MockIssue) testIssues.get(1);
		i2.setAssignee(new MockApplicationUser("NOT_TESTUSER"));
		contextInformationProvider.assessRelation(element, new KnowledgeElement(i2));
		assertEquals(1., contextInformationProvider.getLinkSuggestions().get(0).getScore().getValue(), 0);
		contextInformationProvider = new UserContextInformationProvider();

		i2.setAssignee(new MockApplicationUser("TESTUSER"));
		i2.setCreatorId(JiraUsers.BLACK_HEAD.createApplicationUser().getKey());
		contextInformationProvider.assessRelation(element, new KnowledgeElement(i2));
		assertEquals(1., contextInformationProvider.getLinkSuggestions().get(0).getScore().getValue(), 0);
		contextInformationProvider = new UserContextInformationProvider();

		i2.setAssignee(new MockApplicationUser("NOT_TESTUSER"));
		contextInformationProvider.assessRelation(element, new KnowledgeElement(i2));
		assertEquals(1., contextInformationProvider.getLinkSuggestions().get(0).getScore().getValue(), 0);
		contextInformationProvider = new UserContextInformationProvider();

		i2.setAssignee(null);
		i2.setCreatorId(null);
		i2.setReporter(null);
		contextInformationProvider.assessRelation(element, new KnowledgeElement(i2));
		assertEquals(1., contextInformationProvider.getLinkSuggestions().get(0).getScore().getValue(), 0);

		contextInformationProvider.assessRelation(new KnowledgeElement(), new KnowledgeElement(i2));
		assertEquals(1., contextInformationProvider.getLinkSuggestions().get(0).getScore().getValue(), 0);
	}
}
