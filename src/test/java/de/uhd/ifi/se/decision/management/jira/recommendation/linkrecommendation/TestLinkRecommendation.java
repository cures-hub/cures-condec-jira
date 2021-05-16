package de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.issue.Issue;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;

public class TestLinkRecommendation extends TestSetUp {

	private static List<Issue> testIssues;

	@Before
	public void setUp() {
		init();
		testIssues = JiraIssues.getTestJiraIssues();
	}

	@Test
	public void testLinkSuggestion() {
		LinkRecommendation linkSuggestion1 = new LinkRecommendation(new KnowledgeElement(testIssues.get(0)),
				new KnowledgeElement(testIssues.get(1)));

		linkSuggestion1.addToScore(0.5, "test");
		assertEquals(-1, linkSuggestion1.compareTo(null));

		LinkRecommendation linkSuggestion2 = new LinkRecommendation(new KnowledgeElement(testIssues.get(0)),
				new KnowledgeElement(testIssues.get(1)));
		linkSuggestion2.addToScore(0.5, "test");
		assertEquals(-1, linkSuggestion1.compareTo(linkSuggestion2));

		linkSuggestion2.addToScore(0.5, "test1");
		assertEquals(-1, linkSuggestion1.compareTo(linkSuggestion2));

		linkSuggestion1.addToScore(1., "test1");
		assertEquals(1, linkSuggestion1.compareTo(linkSuggestion2));
	}
}
