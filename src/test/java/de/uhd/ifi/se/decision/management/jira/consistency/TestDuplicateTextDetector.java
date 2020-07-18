package de.uhd.ifi.se.decision.management.jira.consistency;

import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.project.Project;
import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.consistency.duplicatedetection.BasicDuplicateTextDetector;
import de.uhd.ifi.se.decision.management.jira.consistency.duplicatedetection.DuplicateDetectionStrategy;
import de.uhd.ifi.se.decision.management.jira.consistency.suggestions.DuplicateSuggestion;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraProjects;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestDuplicateTextDetector extends TestSetUp {
	private static MutableIssue baseIssue;
	private static MutableIssue otherIssue;
	private static DuplicateDetectionStrategy basicDuplicateTextDetector;

	@BeforeClass
	public static void setUp() {
		TestSetUp.init();
		Project project = JiraProjects.getTestProject();
		List<MutableIssue> testIssues = JiraIssues.createJiraIssues(project);
		baseIssue = testIssues.get(0);
		otherIssue = testIssues.get(1);
	}

	@Test
	public void testBasicDuplicateTextDetectorWithNoDuplicates() {
		try {
			basicDuplicateTextDetector = new BasicDuplicateTextDetector(3);
			List<DuplicateSuggestion> foundDuplicates = basicDuplicateTextDetector.detectDuplicates(new KnowledgeElement(baseIssue), new KnowledgeElement(otherIssue));
			assertTrue("No duplicates should have been found", foundDuplicates.isEmpty());
		} catch (Exception e) {
			assertNull("No exception should be thrown.", e);
		}
	}

	@Test
	public void testBasicDuplicateTextDetectorWithBaseIssueBeingNull() {

		basicDuplicateTextDetector = new BasicDuplicateTextDetector(3);
		assertThrows(NullPointerException.class, () -> basicDuplicateTextDetector.detectDuplicates(null, new KnowledgeElement(otherIssue)));

	}

	@Test
	public void testBasicDuplicateTextDetectorWithOtherIssueBeingNull() {

		basicDuplicateTextDetector = new BasicDuplicateTextDetector(3);
		assertThrows(NullPointerException.class, () -> basicDuplicateTextDetector.detectDuplicates(new KnowledgeElement(baseIssue), null));

	}

	@Test
	public void testBasicDuplicateTextDetectorWithDuplicates() {
		try {
			basicDuplicateTextDetector = new BasicDuplicateTextDetector(3);
			List<DuplicateSuggestion> foundDuplicates = basicDuplicateTextDetector.detectDuplicates(new KnowledgeElement(baseIssue), new KnowledgeElement(baseIssue));
			assertFalse("One duplicate should have been found", foundDuplicates.isEmpty());
		} catch (Exception e) {
			assertNull("No exception should be thrown.", e);
		}
	}
}
