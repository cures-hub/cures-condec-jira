package de.uhd.ifi.se.decision.management.jira.view;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.config.ConstantsManager;
import com.atlassian.jira.config.IssueTypeManager;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.comments.CommentManager;
import com.atlassian.jira.issue.fields.config.manager.IssueTypeSchemeManager;
import com.atlassian.jira.issue.link.IssueLinkManager;
import com.atlassian.jira.mock.MockConstantsManager;
import com.atlassian.jira.mock.MockProjectManager;
import com.atlassian.jira.mock.component.MockComponentWorker;
import com.atlassian.jira.mock.security.MockAuthenticationContext;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.security.JiraAuthenticationContext;
import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.extraction.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.mocks.MockCommentManager;
import de.uhd.ifi.se.decision.management.jira.mocks.MockIssueLinkManager;
import de.uhd.ifi.se.decision.management.jira.mocks.MockIssueManager;
import de.uhd.ifi.se.decision.management.jira.mocks.MockIssueTypeManager;
import de.uhd.ifi.se.decision.management.jira.mocks.MockIssueTypeSchemeManager;
import de.uhd.ifi.se.decision.management.jira.mocks.MockSearchService;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssueTextPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.test.jdbc.NonTransactional;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues.addElementToDataBase;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TestRequirementsDashboardItem extends TestSetUp {

	private RequirementsDashboardItem dashboardItem;
	private Map<String, Object> params;
	@Mock
	HttpServletRequest req;

	@Before
	public void setUp() {
		TestSetUpGit.setUpBeforeClass();
		init();
		this.dashboardItem = new RequirementsDashboardItem();
		addElementToDataBase(17, "Issue");
		addElementToDataBase(18, "Decision");
		addElementToDataBase(19, "Argument");
		new MockComponentWorker().init().addMock(IssueLinkManager.class, new MockIssueLinkManager())
				.addMock(CommentManager.class, new MockCommentManager())
				.addMock(ProjectManager.class, new MockProjectManager())
				.addMock(ConstantsManager.class, new MockConstantsManager())
				.addMock(IssueTypeManager.class, new MockIssueTypeManager())
				.addMock(IssueManager.class, new MockIssueManager())
				.addMock(IssueTypeSchemeManager.class, new MockIssueTypeSchemeManager())
				.addMock(JiraAuthenticationContext.class, new MockAuthenticationContext(JiraUsers.SYS_ADMIN.createApplicationUser()))
				.addMock(SearchService.class, new MockSearchService());

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
		assertNotNull(this.dashboardItem.createValues("TEST", "10100", 2, false,
				KnowledgeType.toStringList(),
				KnowledgeStatus.toStringList(),
				null));
	}

}
