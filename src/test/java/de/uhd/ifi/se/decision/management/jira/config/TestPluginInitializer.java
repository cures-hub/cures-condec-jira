package de.uhd.ifi.se.decision.management.jira.config;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.atlassian.jira.avatar.AvatarManager;
import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.config.ConstantsManager;
import com.atlassian.jira.config.IssueTypeManager;
import com.atlassian.jira.exception.CreateException;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.link.IssueLinkManager;
import com.atlassian.jira.issue.link.IssueLinkTypeManager;
import com.atlassian.jira.mock.MockConstantsManager;
import com.atlassian.jira.mock.MockProjectManager;
import com.atlassian.jira.mock.component.MockComponentWorker;
import com.atlassian.jira.project.ProjectManager;

import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.mocks.MockAvatarManager;
import de.uhd.ifi.se.decision.management.jira.mocks.MockIssueLinkManager;
import de.uhd.ifi.se.decision.management.jira.mocks.MockIssueLinkTypeManager;
import de.uhd.ifi.se.decision.management.jira.mocks.MockIssueManagerSelfImpl;
import de.uhd.ifi.se.decision.management.jira.mocks.MockIssueService;
import de.uhd.ifi.se.decision.management.jira.mocks.MockIssueTypeManager;

public class TestPluginInitializer extends TestSetUpWithIssues {

	private PluginInitializer pluginInitializer;

	@Before
	public void setUp() {
		pluginInitializer = new PluginInitializer();
	}

	@Test
	public void testExecutionAfterProp() throws CreateException {
		initialization();
		pluginInitializer.afterPropertiesSet();
	}

	@Test
	public void testExecutionAfterPropNoInit() {
		ProjectManager projectManager = new MockProjectManager();
		IssueTypeManager issueTypeManager = new MockIssueTypeManager();
		IssueManager issueManager = new MockIssueManagerSelfImpl();
		ConstantsManager constManager = new MockConstantsManager();
		IssueService issueService = new MockIssueService();

		new MockComponentWorker().init().addMock(IssueManager.class, issueManager)
				.addMock(IssueTypeManager.class, issueTypeManager)
				.addMock(IssueLinkManager.class, new MockIssueLinkManager())
				.addMock(IssueLinkTypeManager.class, new MockIssueLinkTypeManager(true))
				.addMock(IssueService.class, issueService).addMock(ProjectManager.class, projectManager)
				.addMock(ConstantsManager.class, constManager).addMock(AvatarManager.class, new MockAvatarManager());
		pluginInitializer.afterPropertiesSet();
	}

	@Test
	public void testExecutionAfterPropInit() throws Exception {
		ProjectManager projectManager = new MockProjectManager();
		IssueManager issueManager = new MockIssueManagerSelfImpl();
		ConstantsManager constManager = new MockConstantsManager();
		IssueService issueService = new MockIssueService();
		IssueTypeManager issueTypeManager = new MockIssueTypeManager();

		new MockComponentWorker().init().addMock(IssueManager.class, issueManager)
				.addMock(IssueTypeManager.class, issueTypeManager)
				.addMock(IssueLinkManager.class, new MockIssueLinkManager())
				.addMock(IssueLinkTypeManager.class, new MockIssueLinkTypeManager(false))
				.addMock(IssueService.class, issueService).addMock(ProjectManager.class, projectManager)
				.addMock(ConstantsManager.class, constManager);

		// Adding all Issue Types
		constManager.insertIssueType("Decision", (long) 20, "Test", "Test", (long) 12290);
		constManager.insertIssueType("Alternative", (long) 20, "Test", "Test", (long) 12290);
		constManager.insertIssueType("Argument", (long) 20, "Test", "Test", (long) 12290);
		constManager.insertIssueType("Assessment", (long) 20, "Test", "Test", (long) 12290);
		constManager.insertIssueType("Assumption", (long) 20, "Test", "Test", (long) 12290);
		constManager.insertIssueType("Claim", (long) 20, "Test", "Test", (long) 12290);
		constManager.insertIssueType("Constraint", (long) 20, "Test", "Test", (long) 12290);
		constManager.insertIssueType("Context", (long) 20, "Test", "Test", (long) 12290);
		constManager.insertIssueType("Goal", (long) 20, "Test", "Test", (long) 12290);
		constManager.insertIssueType("Implication", (long) 20, "Test", "Test", (long) 12290);
		constManager.insertIssueType("Issue", (long) 20, "Test", "Test", (long) 12290);
		constManager.insertIssueType("Problem", (long) 20, "Test", "Test", (long) 12290);
		constManager.insertIssueType("Solution", (long) 20, "Test", "Test", (long) 12290);

		((MockIssueTypeManager) issueTypeManager).addIssueType(constManager.getAllIssueTypeObjects());

		pluginInitializer.afterPropertiesSet();
	}

	@Test
	public void testAddIssueTypeToSchemeIssueTypeNullProjectKeyNull() {
		initialization();
		PluginInitializer.addIssueTypeToScheme(null, null);
	}

	@Test
	public void testAddIssueTypeToSchemeIssueTypeNullProjectKeyFilled() {
		initialization();
		PluginInitializer.addIssueTypeToScheme(null, "TEST");
	}

	@Test
	public void testAddIssueTypeToSchemeIssueTypeFilledProjectKeyNull() {
		initialization();
		PluginInitializer.addIssueTypeToScheme("Decision", null);
	}

	@Test
	@Ignore
	public void testAddIssueTypeToSchemeIssueTypeFilledProjectKeyFilled() {
		initialization();
		PluginInitializer.addIssueTypeToScheme("Decision", "TEST");
	}

	@Test
	public void testRemoveIssueTypeToSchemeIssueTypeNullProjectKeyNull() {
		initialization();
		PluginInitializer.removeIssueTypeFromScheme(null, null);
	}

	@Test
	public void testRemoveIssueTypeToSchemeIssueTypeNullProjectKeyFilled() {
		initialization();
		PluginInitializer.removeIssueTypeFromScheme(null, "TEST");
	}

	@Test
	public void testRemoveIssueTypeToSchemeIssueTypeFilledProjectKeyNull() {
		initialization();
		PluginInitializer.removeIssueTypeFromScheme("Decision", null);
	}

	@Test
	public void testRemoveIssueTypeToSchemeIssueTypeFilledProjectKeyFilled() {
		initialization();
		PluginInitializer.removeIssueTypeFromScheme("Decision", "TEST");
	}
}