package de.uhd.ifi.se.decision.management.jira.quality;

import static de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues.addComment;
import static de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues.getTestJiraIssues;
import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import net.java.ao.test.jdbc.NonTransactional;

public class TestCommentMetricCalculator extends TestSetUp {

	protected CommentMetricCalculator commentMetricCalculator;

	@Before
	public void setUp() {
		init();
		commentMetricCalculator = new CommentMetricCalculator(getTestJiraIssues(), "TEST");
	}

	@Test
	@NonTransactional
	public void testNumberOfCommentsPerIssue() {
		Map<String, Integer> map = commentMetricCalculator.numberOfCommentsPerIssue();
		assertEquals(10, map.size());
	}

	@Test
	@NonTransactional
	public void testGetNumberOfRelevantComments() {
		addComment(getTestJiraIssues().get(7));
		assertEquals(2, commentMetricCalculator.getNumberOfRelevantComments().size());
	}

}
