package de.uhd.ifi.se.decision.management.jira.consistency;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.project.Project;
import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.consistency.implementation.DuplicateFragment;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraProjects;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestDuplicateFragment extends TestSetUp {

	List<MutableIssue> testIssues;

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
		DuplicateFragment duplicateFragment = new DuplicateFragment(i1, i2, text, start, length, field);

		assertEquals(i1, duplicateFragment.getI1());
		assertEquals(i2, duplicateFragment.getI2());
		assertEquals(field, duplicateFragment.getField());
		assertEquals(text, duplicateFragment.getPreprocessedSummary());
		assertEquals(start, duplicateFragment.getStartDuplicate());
		assertEquals(length, duplicateFragment.getLength());


	}
}
