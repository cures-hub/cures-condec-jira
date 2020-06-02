package de.uhd.ifi.se.decision.management.jira.consistency;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.project.Project;
import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraProjects;
import org.junit.BeforeClass;
import org.junit.Test;
import org.ofbiz.core.entity.GenericEntityException;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class TestCipCalculation extends TestSetUp {

	private static List<MutableIssue> testIssues;

	@BeforeClass
	public static void setUp() {
		TestSetUp.init();
		Project project = JiraProjects.getTestProject();
		TestCipCalculation.testIssues = JiraIssues.createJiraIssues(project);
	}

	@Test
	public void testCIP() {
		Issue baseIssue = TestCipCalculation.testIssues.get(0);
		ContextInformation contextInformation = new ContextInformation(baseIssue);
		try {
			Collection<LinkSuggestion> linkSuggestions = contextInformation.getLinkSuggestions();
			List<LinkSuggestion> sortedLinkSuggestions = linkSuggestions
				.stream()
				.sorted((LinkSuggestion::compareTo))
				.collect(Collectors.toList());
			LinkSuggestion identicalIssueSuggestion = sortedLinkSuggestions.get(sortedLinkSuggestions.size() - 1);
			assertEquals("The baseIssue should be the most similar to itself.", baseIssue.getKey(),
				identicalIssueSuggestion.getTargetIssue().getKey());
			assertNotNull(identicalIssueSuggestion.getScore().getScores());

			assertEquals("The baseIssue should be set correctly.", baseIssue.getKey(),
				identicalIssueSuggestion.getBaseIssue().getKey());

		} catch (NullPointerException | GenericEntityException e) {
			System.err.println("ERROR:");
			e.printStackTrace();
			assertNull(e);
		}
	}


	@Test
	public void testLinkSuggestion() {
		LinkSuggestion linkSuggestion1 = new LinkSuggestion(testIssues.get(0), testIssues.get(1));
		linkSuggestion1.addToScore(0.5, "test");
		assertEquals(-1, linkSuggestion1.compareTo(null));


		LinkSuggestion linkSuggestion2 = new LinkSuggestion(testIssues.get(0), testIssues.get(1));
		linkSuggestion2.addToScore(0.5, "test");
		assertEquals(-1, linkSuggestion1.compareTo(linkSuggestion2));

		linkSuggestion2.addToScore(0.5, "test1");
		assertEquals(-1, linkSuggestion1.compareTo(linkSuggestion2));

		linkSuggestion1.addToScore(1., "test1");
		assertEquals(1, linkSuggestion1.compareTo(linkSuggestion2));
	}
}
