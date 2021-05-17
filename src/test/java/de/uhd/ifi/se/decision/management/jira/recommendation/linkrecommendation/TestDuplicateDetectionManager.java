package de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.project.Project;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.duplicatedetection.DuplicateDetectionManager;
import de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.duplicatedetection.DuplicateTextDetector;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssueTypes;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraProjects;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;

public class TestDuplicateDetectionManager extends TestSetUp {
	private Project project;
	private ApplicationUser user;
	private DuplicateDetectionManager detectionManager;

	@Before
	public void setUp() {
		TestSetUp.init();
		project = JiraProjects.TEST.createJiraProject(1);// JiraProjects.getTestProject();
		user = JiraUsers.SYS_ADMIN.getApplicationUser();
		detectionManager = new DuplicateDetectionManager(KnowledgeElements.getTestKnowledgeElement(),
				new DuplicateTextDetector(3));
	}

	@Test
	public void testDuplicateDetectionGetter() {
		assertEquals(KnowledgeElements.getTestKnowledgeElement(), detectionManager.getKnowledgeElement());
	}

	@Test
	public void testFindAllDuplicatesWithDuplicatesExisting() {
		List<KnowledgeElement> testElements = generateDuplicates("This text should be detected as a duplicate.");
		testElements.addAll(generateDuplicates("This text should be detected as a duplicate which is even longer."));
		DuplicateDetectionManager detectionManager = new DuplicateDetectionManager(testElements.get(0),
				new DuplicateTextDetector(3));
		assertEquals(3, detectionManager.findAllDuplicates(testElements).size());
	}

	private List<KnowledgeElement> generateDuplicates(String text) {
		List<KnowledgeElement> issues = new ArrayList<>();
		issues.add(new KnowledgeElement(
				JiraIssues.createJiraIssue(99, JiraIssueTypes.getTestTypes().get(0), project, text, user)));
		issues.add(new KnowledgeElement(
				JiraIssues.createJiraIssue(999, JiraIssueTypes.getTestTypes().get(0), project, text, user)));

		return issues;
	}

	@Test
	public void testFindAllDuplicatesWithNoDuplicatesExisting() {
		assertEquals(0, detectionManager.findAllDuplicates(KnowledgeElements.getTestKnowledgeElements()).size());
	}
}
