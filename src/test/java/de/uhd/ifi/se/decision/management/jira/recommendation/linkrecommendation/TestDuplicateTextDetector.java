package de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.project.Project;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.duplicatedetection.DuplicateTextDetector;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraProjects;

public class TestDuplicateTextDetector extends TestSetUp {
	private static Issue baseIssue;
	private static Issue otherIssue;
	private static DuplicateTextDetector basicDuplicateTextDetector;

	@BeforeClass
	public static void setUp() {
		TestSetUp.init();
		Project project = JiraProjects.getTestProject();
		List<Issue> testIssues = JiraIssues.createJiraIssues(project);
		baseIssue = testIssues.get(0);
		otherIssue = testIssues.get(1);
	}

	@Test
	public void testBasicDuplicateTextDetectorWithNoDuplicates() {
		basicDuplicateTextDetector = new DuplicateTextDetector(3);
		List<DuplicateRecommendation> foundDuplicates = basicDuplicateTextDetector
				.detectDuplicates(new KnowledgeElement(baseIssue), new KnowledgeElement(otherIssue));
		assertTrue("No duplicates should have been found", foundDuplicates.isEmpty());
	}

	@Test
	public void testBasicDuplicateTextDetectorWithBaseIssueBeingNull() {

		basicDuplicateTextDetector = new DuplicateTextDetector(3);
		assertThrows(NullPointerException.class,
				() -> basicDuplicateTextDetector.detectDuplicates(null, new KnowledgeElement(otherIssue)));

	}

	@Test
	public void testBasicDuplicateTextDetectorWithOtherIssueBeingNull() {
		basicDuplicateTextDetector = new DuplicateTextDetector(3);
		assertThrows(NullPointerException.class,
				() -> basicDuplicateTextDetector.detectDuplicates(new KnowledgeElement(baseIssue), null));

	}

	@Test
	public void testBasicDuplicateTextDetectorWithDuplicates() {
		basicDuplicateTextDetector = new DuplicateTextDetector(3);
		List<DuplicateRecommendation> foundDuplicates = basicDuplicateTextDetector
				.detectDuplicates(new KnowledgeElement(baseIssue), new KnowledgeElement(baseIssue));
		assertFalse("One duplicate should have been found", foundDuplicates.isEmpty());
	}
}
