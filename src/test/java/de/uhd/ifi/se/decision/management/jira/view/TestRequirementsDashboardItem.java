package de.uhd.ifi.se.decision.management.jira.view;

import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.issue.issuetype.MockIssueType;
import com.atlassian.jira.mock.MockProjectManager;
import com.atlassian.jira.project.MockProject;
import com.atlassian.jira.project.Project;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.config.PluginInitializer;
import net.java.ao.test.jdbc.NonTransactional;



public class TestRequirementsDashboardItem {
	
	private RequirementsDashboardItem dashboardItem;
	private Map<String, Object> params;
	private MockProjectManager projectManager;
	
	@Before
	public void setUp() {
		TestSetUp.init();
		projectManager = new MockProjectManager();
		this.dashboardItem = new RequirementsDashboardItem();

		params = new HashMap<String, Object>();
		ComponentAccessor.getProjectManager().getProjects();

		// add two projects
		Project project = new MockProject(1, "TEST");
		((MockProject) project).setKey("TEST");
		((MockProjectManager) projectManager).addProject(project);
		Project project2 = new MockProject(2, "SETS");
		((MockProject) project2).setKey("SETS");
		((MockProjectManager) projectManager).addProject(project2);
		
		IssueType issueType = new MockIssueType(1, "WI");
		PluginInitializer.addIssueTypeToScheme(issueType.getName(), project.getKey());
		IssueType issueType2 = new MockIssueType(2,"SF");
		PluginInitializer.addIssueTypeToScheme(issueType2.getName(), project.getKey());
	}
	
	@Test
	@NonTransactional
	public void testGetContextMapNull() {
		Map<String, Object> ctxResult = this.dashboardItem.getContextMap(params);
		assertNotNull(ctxResult);
	}
	
}
