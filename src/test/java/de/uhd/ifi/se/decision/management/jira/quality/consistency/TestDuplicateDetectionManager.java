package de.uhd.ifi.se.decision.management.jira.quality.consistency;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.quality.consistency.duplicatedetection.BasicDuplicateTextDetector;
import de.uhd.ifi.se.decision.management.jira.quality.consistency.duplicatedetection.DuplicateDetectionManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssueTypes;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraProjects;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestDuplicateDetectionManager extends TestSetUp {
	private List<Issue> testIssues;
	private Issue issue;
	private Project project;
	private ApplicationUser user;

	@Before
	public void setUp() {
		TestSetUp.init();
		project = JiraProjects.TEST.createJiraProject(1);// JiraProjects.getTestProject();
		testIssues = JiraIssues.createJiraIssues(project);
		issue = testIssues.get(0);
		user = JiraUsers.SYS_ADMIN.getApplicationUser();
	}

	@Test
	public void testDuplicateDetectionGetter() {
		DuplicateDetectionManager detectionManager = new DuplicateDetectionManager(issue,
				new BasicDuplicateTextDetector(3));

		assertEquals("The base issue should be set correctly.", issue.getKey(),
				detectionManager.getKnowledgeElement().getJiraIssue().getKey());

		detectionManager = new DuplicateDetectionManager((Issue) null, new BasicDuplicateTextDetector(3));

		assertNull("The base issue should be set correctly.", detectionManager.getKnowledgeElement().getJiraIssue());

	}

	@Test
	public void testFindAllDuplicatesWithWithValidData() {
		DuplicateDetectionManager detectionManager = new DuplicateDetectionManager(
				transformIssuesToKnowledgeElement(issue), new BasicDuplicateTextDetector(3), 3);

		// No Duplicate Exists
		assertEquals("The base issue should be set correctly.", 0,
				detectionManager.findAllDuplicates(transformIssuesToKnowledgeElements(testIssues)).size());

		List<KnowledgeElement> duplicateIssues = generateDuplicates("This text should be detected as a Duplicate.");
		detectionManager = new DuplicateDetectionManager(duplicateIssues.get(0), new BasicDuplicateTextDetector(3), 3);
		List<KnowledgeElement> list = transformIssuesToKnowledgeElements(testIssues);
		list.addAll(duplicateIssues);
		assertEquals("The duplicate issue should be found.", 1, detectionManager.findAllDuplicates(list).size());

	}

	@Test
	public void testFindAllDuplicatesWithWithNull() {
		DuplicateDetectionManager detectionManager = new DuplicateDetectionManager((Issue) null,
				new BasicDuplicateTextDetector(3));

		// No Duplicate Exists
		assertTrue("No duplicates can be found.",
				detectionManager.findAllDuplicates(transformIssuesToKnowledgeElements(testIssues)).isEmpty());

		detectionManager = new DuplicateDetectionManager(issue, null);

		assertTrue("No duplicates can be found.",
				detectionManager.findAllDuplicates(transformIssuesToKnowledgeElements(testIssues)).isEmpty());

	}

	private List<KnowledgeElement> generateDuplicates(String text) {
		List<KnowledgeElement> issues = new ArrayList<>();
		issues.add(new KnowledgeElement(
				JiraIssues.createJiraIssue(99, JiraIssueTypes.getTestTypes().get(0), project, text, user)));
		issues.add(new KnowledgeElement(
				JiraIssues.createJiraIssue(999, JiraIssueTypes.getTestTypes().get(0), project, text, user)));

		return issues;

	}

	private List<KnowledgeElement> transformIssuesToKnowledgeElements(List<? extends Issue> issues) {
		return issues.stream().map(KnowledgeElement::new).collect(Collectors.toList());
	}

	private KnowledgeElement transformIssuesToKnowledgeElement(Issue issue) {
		return new KnowledgeElement(issue);
	}

}
