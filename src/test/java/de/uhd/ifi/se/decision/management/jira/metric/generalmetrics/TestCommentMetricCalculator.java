package de.uhd.ifi.se.decision.management.jira.metric.generalmetrics;

import static de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues.getTestJiraIssues;
import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.metric.CommentMetricCalculator;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import net.java.ao.test.jdbc.NonTransactional;

public class TestCommentMetricCalculator extends TestSetUp {

	protected CommentMetricCalculator commentMetricCalculator;

	@Before
	public void setUp() {
		init();
		commentMetricCalculator = new CommentMetricCalculator(getTestJiraIssues());
	}

	@Test
	@NonTransactional
	public void testNumberOfCommentsPerIssue() {
		Map<Integer, List<KnowledgeElement>> map = commentMetricCalculator.getNumberOfCommentsPerJiraIssueMap();
		assertEquals(1, map.size());
	}

	@Test
	@NonTransactional
	public void testGetNumberOfRelevantComments() {
		JiraIssues.getSentencesForCommentText("{decision} We will use MySQL! {decision}");
		assertEquals(1, new CommentMetricCalculator(getTestJiraIssues()).getNumberOfRelevantComments());
	}

	@Test
	@NonTransactional
	public void testGetNumberOfIrrelevantComments() {
		JiraIssues.getSentencesForCommentText("I am an irrelevant comment.");
		assertEquals(1, new CommentMetricCalculator(getTestJiraIssues()).getNumberOfIrrelevantComments());
	}

	@Test
	@NonTransactional
	public void testGetNumberOfCommitsPerJiraIssue() {
		JiraIssues.getSentencesForCommentText("Hash: 123");
		assertEquals(2, new CommentMetricCalculator(getTestJiraIssues()).getNumberOfCommitsPerJiraIssueMap().size());
	}

}
