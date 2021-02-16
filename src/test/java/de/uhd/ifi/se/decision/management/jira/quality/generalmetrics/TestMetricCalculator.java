package de.uhd.ifi.se.decision.management.jira.quality.generalmetrics;

import static de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues.addComment;
import static de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues.addElementToDataBase;
import static de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues.getTestJiraIssues;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.extraction.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.test.jdbc.NonTransactional;

public class TestMetricCalculator extends TestSetUpGit {

	protected GeneralMetricCalculator calculator;

	@Override
	@Before
	public void setUp() {
		init();
		ApplicationUser user = JiraUsers.SYS_ADMIN.getApplicationUser();
		calculator = new GeneralMetricCalculator(user, "TEST");
		calculator.setJiraIssues(getTestJiraIssues());
	}

	@Test
	@NonTransactional
	public void testGetDistributionOfKnowledgeTypes() {
		assertEquals(4, calculator.getDistributionOfKnowledgeTypes().size());
	}

	@Test
	@NonTransactional
	public void testGetReqAndClassSummary() {
		assertEquals(2, calculator.getReqAndClassSummary().size());
	}

	@Test
	@NonTransactional
	public void testGetKnowledgeSourceCount() {
		addElementToDataBase(24, KnowledgeType.DECISION);
		addComment(getTestJiraIssues().get(7));
		calculator.setJiraIssues(getTestJiraIssues());
		assertEquals(4, calculator.getElementsFromDifferentOrigins().size());
	}

}
