package de.uhd.ifi.se.decision.management.jira.view.dashboard;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.issue.issuetype.IssueType;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import net.java.ao.test.jdbc.NonTransactional;

public class TestRequirementsDashboardItem extends TestSetUp {

	private RequirementsDashboardItem dashboardItem;

	@Before
	public void setUp() {
		init();
		dashboardItem = new RequirementsDashboardItem();
	}

	@Test
	@NonTransactional
	public void testGetContextMap() {
		Map<String, Object> ctxResult = dashboardItem.getContextMap(new HashMap<String, Object>());
		assertTrue(ctxResult.containsKey("projectKey"));
		assertTrue(ctxResult.containsKey("jiraIssueTypes"));
		@SuppressWarnings("unchecked")
		Collection<IssueType> jiraIssueTypes = (Collection<IssueType>) ctxResult.get("jiraIssueTypes");
		assertFalse(jiraIssueTypes.isEmpty());
	}
}
