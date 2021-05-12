package de.uhd.ifi.se.decision.management.jira.quality.consistency.contextinformation;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.issue.Issue;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.quality.consistency.LinkSuggestionConfiguration;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;

public class TestTracingContextInformationProvider extends TestSetUp {

	@Before
	public void setUp() {
		init();
		LinkSuggestionConfiguration linkSuggestionConfiguration = ConfigPersistenceManager
				.getLinkSuggestionConfiguration("TEST");
		linkSuggestionConfiguration.setMinProbability(0);
		ConfigPersistenceManager.saveLinkSuggestionConfiguration("TEST", linkSuggestionConfiguration);
	}

	@Test
	public void testTracingCIP() {
		ContextInformationProvider tracingCIP = new TracingContextInformationProvider();
		assertEquals("TracingCIP_BFS", tracingCIP.getId());

		List<Issue> testIssues = JiraIssues.getTestJiraIssues();

		Issue i0 = testIssues.get(0);
		Issue i1 = testIssues.get(1);
		Issue i2 = testIssues.get(2);

		KnowledgeElement e0 = new KnowledgeElement(i0);

		List<KnowledgeElement> testIssueList = Collections.singletonList(new KnowledgeElement(i1));
		tracingCIP.assessRelation(e0, testIssueList);
		assertEquals(0.5, tracingCIP.getLinkSuggestions().get(0).getScore().getTotal(), 0);
		// Score is 1/4
		tracingCIP = new TracingContextInformationProvider();

		testIssueList = Collections.singletonList(new KnowledgeElement(i2));
		tracingCIP.assessRelation(e0, testIssueList);
		assertEquals(0.25, tracingCIP.getLinkSuggestions().get(0).getScore().getTotal(), 0.1);
	}
}
