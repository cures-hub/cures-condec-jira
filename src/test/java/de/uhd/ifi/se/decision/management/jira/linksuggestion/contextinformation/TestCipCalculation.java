package de.uhd.ifi.se.decision.management.jira.linksuggestion.contextinformation;

import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.issue.Issue;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.linksuggestion.LinkSuggestionConfiguration;
import de.uhd.ifi.se.decision.management.jira.linksuggestion.suggestions.LinkSuggestion;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.GenericLinkManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;

public class TestCipCalculation extends TestSetUp {

	private static List<Issue> testIssues;

	@Before
	public void setUp() {
		TestSetUp.init();
		testIssues = JiraIssues.getTestJiraIssues();
		LinkSuggestionConfiguration linkSuggestionConfiguration = ConfigPersistenceManager
				.getLinkSuggestionConfiguration("TEST");
		linkSuggestionConfiguration.setMinProbability(0);
		ConfigPersistenceManager.saveLinkSuggestionConfiguration("TEST", linkSuggestionConfiguration);
	}

	@Test
	public void testCIP() {
		Issue baseIssue = TestCipCalculation.testIssues.get(0);
		ContextInformation contextInformation = new ContextInformation(new KnowledgeElement(baseIssue));
		GenericLinkManager.deleteLinksForElement(new KnowledgeElement(baseIssue).getId(),
				DocumentationLocation.JIRAISSUE);
		Collection<LinkSuggestion> linkSuggestions = contextInformation.getLinkSuggestions();
		List<LinkSuggestion> sortedLinkSuggestions = linkSuggestions.stream().sorted((LinkSuggestion::compareTo))
				.collect(Collectors.toList());
		LinkSuggestion identicalIssueSuggestion = sortedLinkSuggestions.get(sortedLinkSuggestions.size() - 1);

		// The baseElement should not be most similar to itself, as it is filtered out!
		assertThat(baseIssue.getKey(), not(identicalIssueSuggestion.getTarget().getJiraIssue().getKey()));
		assertNotNull(identicalIssueSuggestion.getScore().getSubScores());

		assertEquals("The baseIssue should be set correctly.", baseIssue.getKey(),
				identicalIssueSuggestion.getSource().getKey());

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
