package de.uhd.ifi.se.decision.management.jira.config;

import org.junit.Before;
import org.junit.BeforeClass;
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

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.mocks.MockAvatarManager;
import de.uhd.ifi.se.decision.management.jira.mocks.MockIssueLinkManager;
import de.uhd.ifi.se.decision.management.jira.mocks.MockIssueLinkTypeManager;
import de.uhd.ifi.se.decision.management.jira.mocks.MockIssueManager;
import de.uhd.ifi.se.decision.management.jira.mocks.MockIssueService;
import de.uhd.ifi.se.decision.management.jira.mocks.MockIssueTypeManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssueTypes;

public class TestPluginInitializer {

	private static PluginInitializer pluginInitializer;

	@BeforeClass
	public static void setUpBeforeClass() {
		TestSetUp.init();
	}

	@Before
	public void setUp() {
		pluginInitializer = new PluginInitializer();
	}

	@Test
	public void testExecutionAfterProp() throws CreateException {
		pluginInitializer.afterPropertiesSet();
	}

	@Test
	public void testExecutionAfterPropNoInit() {
		ProjectManager projectManager = new MockProjectManager();
		IssueTypeManager issueTypeManager = new MockIssueTypeManager();
		IssueManager issueManager = new MockIssueManager();
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

	public static void addAllIssueTypesToConstantsManager(ConstantsManager constantsManager) throws CreateException {

		// Adding all Issue Types
		constantsManager.insertIssueType("Decision", (long) 20, "Test", "Test", (long) 12290);
		constantsManager.insertIssueType("Alternative", (long) 20, "Test", "Test", (long) 12290);
		constantsManager.insertIssueType("Argument", (long) 20, "Test", "Test", (long) 12290);
		constantsManager.insertIssueType("Assessment", (long) 20, "Test", "Test", (long) 12290);
		constantsManager.insertIssueType("Assumption", (long) 20, "Test", "Test", (long) 12290);
		constantsManager.insertIssueType("Claim", (long) 20, "Test", "Test", (long) 12290);
		constantsManager.insertIssueType("Constraint", (long) 20, "Test", "Test", (long) 12290);
		constantsManager.insertIssueType("Context", (long) 20, "Test", "Test", (long) 12290);
		constantsManager.insertIssueType("Goal", (long) 20, "Test", "Test", (long) 12290);
		constantsManager.insertIssueType("Implication", (long) 20, "Test", "Test", (long) 12290);
		constantsManager.insertIssueType("Issue", (long) 20, "Test", "Test", (long) 12290);
		constantsManager.insertIssueType("Problem", (long) 20, "Test", "Test", (long) 12290);
		constantsManager.insertIssueType("Solution", (long) 20, "Test", "Test", (long) 12290);
	}

	@Test
	public void testExecutionAfterPropInit() throws Exception {
		ProjectManager projectManager = new MockProjectManager();
		IssueManager issueManager = new MockIssueManager();
		ConstantsManager constManager = new MockConstantsManager();
		IssueService issueService = new MockIssueService();
		IssueTypeManager issueTypeManager = new MockIssueTypeManager();

		new MockComponentWorker().init().addMock(IssueManager.class, issueManager)
				.addMock(IssueTypeManager.class, issueTypeManager)
				.addMock(IssueLinkManager.class, new MockIssueLinkManager())
				.addMock(IssueLinkTypeManager.class, new MockIssueLinkTypeManager(false))
				.addMock(IssueService.class, issueService).addMock(ProjectManager.class, projectManager)
				.addMock(ConstantsManager.class, constManager);

		addAllIssueTypesToConstantsManager(constManager);
		((MockIssueTypeManager) issueTypeManager).addIssueType(constManager.getAllIssueTypeObjects());

		pluginInitializer.afterPropertiesSet();
	}

	@Test
	public void testAddIssueTypeToSchemeIssueTypeNullProjectKeyNull() {
		PluginInitializer.addIssueTypeToScheme(null, null);
	}

	@Test
	public void testAddIssueTypeToSchemeIssueTypeNullProjectKeyFilled() {
		PluginInitializer.addIssueTypeToScheme(null, "TEST");
	}

	@Test
	public void testAddIssueTypeToSchemeIssueTypeFilledProjectKeyNull() {
		PluginInitializer.addIssueTypeToScheme(JiraIssueTypes.getTestTypes().get(0), null);
	}

	@Ignore
	@Test
	public void testAddIssueTypeToSchemeIssueTypeFilledProjectKeyFilled() {
		PluginInitializer.addIssueTypeToScheme(JiraIssueTypes.getTestTypes().get(0), "TEST");
	}

	@Test
	public void testRemoveIssueTypeToSchemeIssueTypeNullProjectKeyNull() {
		PluginInitializer.removeIssueTypeFromScheme(null, null);
	}

	@Test
	public void testRemoveIssueTypeToSchemeIssueTypeNullProjectKeyFilled() {
		PluginInitializer.removeIssueTypeFromScheme(null, "TEST");
	}

	@Test
	public void testRemoveIssueTypeToSchemeIssueTypeFilledProjectKeyNull() {
		PluginInitializer.removeIssueTypeFromScheme("Decision", null);
	}

	@Test
	public void testRemoveIssueTypeToSchemeIssueTypeFilledProjectKeyFilled() {
		PluginInitializer.removeIssueTypeFromScheme("Decision", "TEST");
	}

	@Test
	public void testRemoveLinkTypeFromSchemeLinkTypeNullProjectKeyNull() {
		PluginInitializer.removeLinkTypeFromScheme(null, null);
	}

	@Test
	public void testRemoveLinkTypeFromSchemeLinkTypeNullProjectKeyFilled() {
		PluginInitializer.removeLinkTypeFromScheme(null, "TEST");
	}

	@Test
	public void testRemoveLinkTypeFromSchemeLinkTypeFilledProjectKeyNull() {
		PluginInitializer.removeLinkTypeFromScheme("Decision", null);
	}

	@Test
	public void testRemoveLinkTypeFromSchemeLinkTypeFilledProjectKeyFilled() {
		PluginInitializer.removeLinkTypeFromScheme("Decision", "TEST");
	}

	@Test
	public void testAddLinkTypeToSchemeLinkTypeNullProjectKeyNull() {
		PluginInitializer.addLinkTypeToScheme(null, null);
	}

	@Test
	public void testAddLinkTypeToSchemeLinkTypeNullProjectKeyFilled() {
		PluginInitializer.addLinkTypeToScheme(null, "TEST");
	}

	@Test
	public void testAddLinkTypeToSchemeLinkTypeFilledProjectKeyNull() {
		PluginInitializer.addLinkTypeToScheme("Decision", null);
	}

	@Test
	public void testAddLinkTypeToSchemeLinkTypeFilledProjectKeyFilled() {
		PluginInitializer.addLinkTypeToScheme("Decision", "TEST");
	}

}