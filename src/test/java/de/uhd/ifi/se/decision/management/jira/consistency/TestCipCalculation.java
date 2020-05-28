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
import static org.junit.Assert.assertNull;

public class TestCipCalculation extends TestSetUp {

	private static Issue baseIssue;

	@BeforeClass
	public static void setUp() {
		TestSetUp.init();
		Project project = JiraProjects.getTestProject();
		List<MutableIssue> testIssues = JiraIssues.createJiraIssues(project);
		TestCipCalculation.baseIssue = testIssues.get(0);
	}

	@Test
	public void testCIP() {
		ContextInformation contextInformation = new ContextInformation(TestCipCalculation.baseIssue);
		try {
			Collection<LinkSuggestion> linkSuggestions = contextInformation.getLinkSuggestions();
			List<LinkSuggestion> sortedLinkSuggestions = linkSuggestions
				.stream()
				.sorted((LinkSuggestion::compareTo))
				.collect(Collectors.toList());
			LinkSuggestion identicalIssueSuggestion = sortedLinkSuggestions.get(sortedLinkSuggestions.size() - 1);
			assertEquals("The baseIssue should be the most similar to itself.", baseIssue.getKey(),
				identicalIssueSuggestion.getTargetIssue().getKey());

			assertEquals("The baseIssue should be set correctly.", baseIssue.getKey(),
				identicalIssueSuggestion.getBaseIssue().getKey());

		} catch (NullPointerException | GenericEntityException e) {
			System.err.println("ERROR:");
			e.printStackTrace();
			assertNull(e);
		}
	}
}
