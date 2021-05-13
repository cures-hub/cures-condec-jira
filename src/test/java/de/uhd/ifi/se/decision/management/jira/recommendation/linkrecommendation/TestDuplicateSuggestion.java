package de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.project.Project;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.suggestions.DuplicateSuggestion;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraProjects;

public class TestDuplicateSuggestion extends TestSetUp {

	private List<Issue> testIssues;

	@Before
	public void setUp() {
		TestSetUp.init();
		Project project = JiraProjects.getTestProject();
		testIssues = JiraIssues.createJiraIssues(project);
	}

	@Test
	public void testDuplicateFragment() {
		Issue i1 = testIssues.get(0);
		Issue i2 = testIssues.get(1);
		String field = "description";
		String text = "Hello world!";
		int start = 0;
		int length = 5;
		DuplicateSuggestion duplicateSuggestion = new DuplicateSuggestion(new KnowledgeElement(i1),
				new KnowledgeElement(i2), text, start, length, field);

		assertEquals(i1.getId().longValue(), duplicateSuggestion.getSource().getId());
		assertEquals(i2.getId().longValue(), duplicateSuggestion.getTarget().getId());
		assertEquals(field, duplicateSuggestion.getField());
		assertEquals(text, duplicateSuggestion.getPreprocessedSummary());
		assertEquals(start, duplicateSuggestion.getStartDuplicate());
		assertEquals(length, duplicateSuggestion.getLength());

	}
}
