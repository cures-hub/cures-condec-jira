package de.uhd.ifi.se.decision.management.jira.view;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.impl.JiraIssueTextPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import net.java.ao.test.jdbc.NonTransactional;



public class TestRequirementsDashboardItem {
	
	private RequirementsDashboardItem dashboardItem;
	private Map<String, Object> params;
	
	@Before
	public void setUp() {
		TestSetUp.init();
		MockProjectManager projectManager = new MockProjectManager();
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
		
		IssueType issueType = new MockIssueType(10100, "WI");
		PluginInitializer.addIssueTypeToScheme(issueType.getName(), project.getKey());

	}
	
	@Test
	@NonTransactional
	public void testGetContextMapShowProject() {
		params.put("showProject", "showProject");
		Map<String, Object> ctxResult = this.dashboardItem.getContextMap(params);
		assertTrue(ctxResult.containsKey("showDiv"));
	}
	
	@Test
	@NonTransactional
	public void testGetContextMapShowIssueType() {
		params.clear();
		params.put("showIssueType", "TEST");
		Map<String, Object> ctxResult = this.dashboardItem.getContextMap(params);
		assertTrue(ctxResult.containsKey("showDiv"));
		assertTrue(ctxResult.containsKey("issueTypeNamesMap"));
		Map<String, Object> issueTypeNamesMap = (Map<String, Object>) ctxResult.get("issueTypeNamesMap");
		assertFalse(issueTypeNamesMap.isEmpty());
	}
	
	@Test(expected = Exception.class)
	@NonTransactional
	public void testCreationWithObjects() {
		PartOfJiraIssueText partOfJiraIssueText = JiraIssues
				.getSentencesForCommentText("More Comment with some text").get(0);
		partOfJiraIssueText.setType(KnowledgeType.ALTERNATIVE);
		new JiraIssueTextPersistenceManager("").updateDecisionKnowledgeElement(partOfJiraIssueText, null);

		assertNotNull(this.dashboardItem.createValues("TEST","10100"));
	}
}
