package de.uhd.ifi.se.decision.management.jira.quality.consistency;

import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.mock.issue.MockIssue;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.user.MockApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.GenericLinkManager;
import de.uhd.ifi.se.decision.management.jira.quality.consistency.contextinformation.ContextInformation;
import de.uhd.ifi.se.decision.management.jira.quality.consistency.contextinformation.ContextInformationProvider;
import de.uhd.ifi.se.decision.management.jira.quality.consistency.contextinformation.TracingCIP;
import de.uhd.ifi.se.decision.management.jira.quality.consistency.contextinformation.UserCIP;
import de.uhd.ifi.se.decision.management.jira.quality.consistency.suggestions.LinkSuggestion;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraProjects;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestCipCalculation extends TestSetUp {
	private static final Logger LOGGER = LoggerFactory.getLogger(TestCipCalculation.class);
	private static List<Issue> testIssues;
	private Project project;

	@Before
	public void setUp() {
		TestSetUp.init();
		Project project = JiraProjects.getTestProject();
		TestCipCalculation.testIssues = JiraIssues.createJiraIssues(project);
		this.project = JiraProjects.getTestProject();
		LinkSuggestionConfiguration linkSuggestionConfiguration = ConfigPersistenceManager
				.getLinkSuggestionConfiguration("TEST");
		linkSuggestionConfiguration.setMinProbability(0);
		ConfigPersistenceManager.saveLinkSuggestionConfiguration("TEST", linkSuggestionConfiguration);
	}

	@Test
	public void testCIP() {
		Issue baseIssue = TestCipCalculation.testIssues.get(0);
		ContextInformation contextInformation = new ContextInformation(new KnowledgeElement(baseIssue));
		try {
			GenericLinkManager.deleteLinksForElement(new KnowledgeElement(baseIssue).getId(),
					DocumentationLocation.JIRAISSUE);
			Collection<LinkSuggestion> linkSuggestions = contextInformation.getLinkSuggestions();
			List<LinkSuggestion> sortedLinkSuggestions = linkSuggestions.stream().sorted((LinkSuggestion::compareTo))
					.collect(Collectors.toList());
			LinkSuggestion identicalIssueSuggestion = sortedLinkSuggestions.get(sortedLinkSuggestions.size() - 1);

			// The baseElement should not be most similar to itself, as it is filtered out!
			assertThat(baseIssue.getKey(), not(identicalIssueSuggestion.getTargetElement().getJiraIssue().getKey()));
			assertNotNull(identicalIssueSuggestion.getScore().getScores());

			assertEquals("The baseIssue should be set correctly.", baseIssue.getKey(),
					identicalIssueSuggestion.getBaseIssue().getKey());

		} catch (NullPointerException e) {
			LOGGER.error(e.getMessage());
			assertNull(e);
		}
	}

	@Test
	public void testUserCIP() {
		ContextInformationProvider userCIP = new UserCIP();
		assertEquals("UserCIP_equalCreatorOrEqualAssignee", userCIP.getId());

		MockIssue i1 = (MockIssue) JiraIssues.createJiraIssues(project).get(0);
		KnowledgeElement e1 = new KnowledgeElement(i1);
		i1.setCreatorId(JiraUsers.SYS_ADMIN.createApplicationUser().getKey());
		i1.setAssignee(new MockApplicationUser("TESTUSER"));

		MockIssue i2 = (MockIssue) JiraIssues.createJiraIssues(project).get(1);
		i2.setCreatorId(JiraUsers.SYS_ADMIN.createApplicationUser().getKey());
		i2.setAssignee(new MockApplicationUser("TESTUSER"));
		List<KnowledgeElement> testIssueList = Collections.singletonList(new KnowledgeElement(i2));
		userCIP.assessRelation(e1, testIssueList);
		assertEquals(1., findFirst(userCIP.getLinkSuggestions()).getScore().getTotal(), 0);
		userCIP = new UserCIP();

		i2.setAssignee(new MockApplicationUser("NOT_TESTUSER"));
		userCIP.assessRelation(e1, testIssueList);
		assertEquals(1., findFirst(userCIP.getLinkSuggestions()).getScore().getTotal(), 0);
		userCIP = new UserCIP();

		i2.setAssignee(new MockApplicationUser("TESTUSER"));
		i2.setCreatorId(JiraUsers.BLACK_HEAD.createApplicationUser().getKey());
		userCIP.assessRelation(e1, testIssueList);
		assertEquals(1., findFirst(userCIP.getLinkSuggestions()).getScore().getTotal(), 0);
		userCIP = new UserCIP();

		i2.setAssignee(new MockApplicationUser("NOT_TESTUSER"));
		userCIP.assessRelation(e1, testIssueList);
		assertEquals(1., findFirst(userCIP.getLinkSuggestions()).getScore().getTotal(), 0);
		userCIP = new UserCIP();

		i2.setAssignee(null);
		i2.setCreatorId(null);
		i2.setReporter(null);
		testIssueList = Collections.singletonList(new KnowledgeElement(i2));
		userCIP.assessRelation(e1, testIssueList);
		assertEquals(1., findFirst(userCIP.getLinkSuggestions()).getScore().getTotal(), 0);
		userCIP = new UserCIP();

		i1.setAssignee(null);
		i1.setCreatorId(null);
		userCIP.assessRelation(e1, testIssueList);
		assertEquals(1., findFirst(userCIP.getLinkSuggestions()).getScore().getTotal(), 0);

	}

	private LinkSuggestion findFirst(Collection<LinkSuggestion> collection) {
		return collection.stream().findFirst().get();
	}

	@Test
	public void testTracingCIP() {
		ContextInformationProvider tracingCIP = new TracingCIP();
		assertEquals("TracingCIP_BFS", tracingCIP.getId());

		Issue i0 = testIssues.get(0);
		Issue i1 = testIssues.get(1);
		Issue i2 = testIssues.get(2);

		KnowledgeElement e0 = new KnowledgeElement(i0);

		List<KnowledgeElement> testIssueList = Collections.singletonList(new KnowledgeElement(i1));
		tracingCIP.assessRelation(e0, testIssueList);
		assertEquals(0.5, findFirst(tracingCIP.getLinkSuggestions()).getScore().getTotal(), 0);
		// Score is 1/4
		tracingCIP = new TracingCIP();

		testIssueList = Collections.singletonList(new KnowledgeElement(i2));
		tracingCIP.assessRelation(e0, testIssueList);
		assertEquals(0.25, findFirst(tracingCIP.getLinkSuggestions()).getScore().getTotal(), 0.1);
	}

	@Test
	public void testLinkSuggestion() {
		LinkSuggestion linkSuggestion1 = new LinkSuggestion(new KnowledgeElement(testIssues.get(0)),
				new KnowledgeElement(testIssues.get(1)));

		linkSuggestion1.addToScore(0.5, "test");
		assertEquals(-1, linkSuggestion1.compareTo(null));

		LinkSuggestion linkSuggestion2 = new LinkSuggestion(new KnowledgeElement(testIssues.get(0)),
				new KnowledgeElement(testIssues.get(1)));
		linkSuggestion2.addToScore(0.5, "test");
		assertEquals(-1, linkSuggestion1.compareTo(linkSuggestion2));

		linkSuggestion2.addToScore(0.5, "test1");
		assertEquals(-1, linkSuggestion1.compareTo(linkSuggestion2));

		linkSuggestion1.addToScore(1., "test1");
		assertEquals(1, linkSuggestion1.compareTo(linkSuggestion2));
	}
}
