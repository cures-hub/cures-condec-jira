package de.uhd.ifi.se.decision.management.jira.view;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.mock.MockProjectManager;
import com.atlassian.jira.project.MockProject;
import com.atlassian.jira.project.Project;
import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.config.SettingsOfAllProjects;
import net.java.ao.test.jdbc.NonTransactional;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TestFeatureBranchQualityDashboardItem extends TestSetUp {

	private FeatureBranchQualityDashboardItem dashboardItem;
	private List<Project> projects;
	private Map<String, Object> params;
	private MockProjectManager projectManager;

	@Before
	public void setUp() {
		TestSetUp.init();
		projectManager = new MockProjectManager();
		this.dashboardItem = new FeatureBranchQualityDashboardItem();

		params = new HashMap<String, Object>();
		projects = ComponentAccessor.getProjectManager().getProjects();

		// add two projects
		Project project = new MockProject(1, "TEST");
		((MockProject) project).setKey("TEST");
		((MockProjectManager) projectManager).addProject(project);
		project = new MockProject(1, "SETS");
		((MockProject) project).setKey("SETS");
		((MockProjectManager) projectManager).addProject(project);

	}

	@Test
	@NonTransactional
	public void testGetContextMapNull() {
		Map<String, Object> ctxResult = this.dashboardItem.getContextMap(params);
	}

}
