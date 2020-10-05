package de.uhd.ifi.se.decision.management.jira.view;

import static de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues.addElementToDataBase;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import de.uhd.ifi.se.decision.management.jira.extraction.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssueTextPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.test.jdbc.NonTransactional;

public class TestRequirementsDashboardItem extends TestSetUpGit {

	private RequirementsDashboardItem dashboardItem;
	private Map<String, Object> params;
	@Mock
	HttpServletRequest req;

	@Override
	@Before
	public void setUp() {
		super.setUp();
		this.dashboardItem = new RequirementsDashboardItem();
		addElementToDataBase(17, "Issue");
		addElementToDataBase(18, "Decision");
		addElementToDataBase(19, "Argument");
		params = new HashMap<String, Object>();

	}

	@Test
	@NonTransactional
	public void testGetContextMapShowProject() {
		params.put("showProject", "showProject");

		this.dashboardItem.loggedUser = JiraUsers.SYS_ADMIN.createApplicationUser();
		Map<String, Object> ctxResult = this.dashboardItem.getContextMap(params);
		assertTrue(ctxResult.containsKey("showDiv"));
	}

	@Test
	@NonTransactional
	public void testGetContextMapShowIssueType() {
		params.clear();
		params.put("showIssueType", "TEST");
		this.dashboardItem.loggedUser = JiraUsers.SYS_ADMIN.createApplicationUser();
		Map<String, Object> ctxResult = this.dashboardItem.getContextMap(params);
		assertTrue(ctxResult.containsKey("showDiv"));
		assertTrue(ctxResult.containsKey("issueTypeNamesMap"));
		Map<String, Object> issueTypeNamesMap = (Map<String, Object>) ctxResult.get("issueTypeNamesMap");
		assertFalse(issueTypeNamesMap.isEmpty());
	}

	@Test
	@NonTransactional
	public void testGetContextMapShowContent() {
		params.clear();
		params.put("showContentProjectKey", "TEST");
		params.put("showContentIssueTypeId", "16");

		this.dashboardItem.loggedUser = JiraUsers.SYS_ADMIN.createApplicationUser();
		Map<String, Object> ctxResult = this.dashboardItem.getContextMap(params);
		assertTrue(ctxResult.containsKey("showDiv"));
		assertTrue(ctxResult.containsKey("project"));

	}

	@Test
	@NonTransactional
	public void testGetContextMapShowContentFiltered() {
		params.clear();
		params.put("showContentFilter", "TEST");
		params.put("showContentIssueTypeId", "16");
		this.dashboardItem.loggedUser = JiraUsers.SYS_ADMIN.createApplicationUser();
		Map<String, Object> ctxResult = this.dashboardItem.getContextMap(params);
		assertTrue(ctxResult.containsKey("showDiv"));
		assertTrue(ctxResult.containsKey("project"));

	}

	@Test
	@NonTransactional
	public void testCreationWithObjects() {
		PartOfJiraIssueText partOfJiraIssueText = JiraIssues.getSentencesForCommentText("More Comment with some text")
				.get(0);
		partOfJiraIssueText.setType(KnowledgeType.ALTERNATIVE);
		new JiraIssueTextPersistenceManager("").updateKnowledgeElement(partOfJiraIssueText, null);
		assertNotNull(this.dashboardItem.createValues("TEST", "10100", 2, false, KnowledgeType.toStringList(),
				KnowledgeStatus.toStringList(), null));
	}

}
