package de.uhd.ifi.se.decision.management.jira.config;

import com.atlassian.jira.avatar.AvatarManager;
import com.atlassian.jira.exception.CreateException;
import de.uhd.ifi.se.decision.management.jira.mocks.*;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.config.ConstantsManager;
import com.atlassian.jira.config.IssueTypeManager;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.link.IssueLinkManager;
import com.atlassian.jira.issue.link.IssueLinkTypeManager;
import com.atlassian.jira.mock.MockConstantsManager;
import com.atlassian.jira.mock.MockProjectManager;
import com.atlassian.jira.mock.component.MockComponentWorker;
import com.atlassian.jira.project.ProjectManager;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;

public class TestPluginInitializer extends TestSetUp {

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

		((MockIssueTypeManager)issueTypeManager).addIssueType(constManager.getAllIssueTypeObjects());

		pluginInitializer.afterPropertiesSet();
	}

	@Test
	public void testAddIssueTypeToSchemeIssueTypeNullProjectKeyNull() {
		initialization();
		pluginInitializer.addIssueTypeToScheme(null,null);
	}

	@Test
	public void testAddIssueTypeToSchemeIssueTypeNullProjectKeyFilled() {
		initialization();
		pluginInitializer.addIssueTypeToScheme(null,"TEST");
	}

	@Test
	public void testAddIssueTypeToSchemeIssueTypeFilledProjectKeyNull() {
		initialization();
		pluginInitializer.addIssueTypeToScheme("Decision",null);
	}

	@Test
	public void testAddIssueTypeToSchemeIssueTypeFilledProjectKeyFilled(){
		initialization();
		pluginInitializer.addIssueTypeToScheme("Decision","TEST");
	}

	@Test
	public void testRemoveIssueTypeToSchemeIssueTypeNullProjectKeyNull() {
		initialization();
		pluginInitializer.removeIssueTypeFromScheme(null,null);
	}

	@Test
	public void testRemoveIssueTypeToSchemeIssueTypeNullProjectKeyFilled() {
		initialization();
		pluginInitializer.removeIssueTypeFromScheme(null,"TEST");
	}

	@Test
	public void testRemoveIssueTypeToSchemeIssueTypeFilledProjectKeyNull(){
		initialization();
		pluginInitializer.removeIssueTypeFromScheme("Decision",null);
	}

	@Test
	public void testRemoveIssueTypeToSchemeIssueTypeFilledProjectKeyFilled(){
		initialization();
		pluginInitializer.removeIssueTypeFromScheme("Decision","TEST");
	}
}