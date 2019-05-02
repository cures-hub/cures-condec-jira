package de.uhd.ifi.se.decision.management.jira;

import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Locale;

import com.atlassian.jira.avatar.AvatarManager;
import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.ConstantsManager;
import com.atlassian.jira.config.IssueTypeManager;
import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.jira.config.util.JiraHome;
import com.atlassian.jira.exception.CreateException;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.comments.CommentManager;
import com.atlassian.jira.issue.fields.config.FieldConfigScheme;
import com.atlassian.jira.issue.fields.config.manager.IssueTypeSchemeManager;
import com.atlassian.jira.issue.fields.option.OptionSetManager;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.issue.issuetype.MockIssueType;
import com.atlassian.jira.issue.link.IssueLinkManager;
import com.atlassian.jira.issue.link.IssueLinkTypeManager;
import com.atlassian.jira.mock.MockApplicationProperties;
import com.atlassian.jira.mock.MockConstantsManager;
import com.atlassian.jira.mock.MockProjectManager;
import com.atlassian.jira.mock.component.MockComponentWorker;
import com.atlassian.jira.mock.issue.MockIssue;
import com.atlassian.jira.project.MockProject;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.security.roles.ProjectRoleManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.MockApplicationUser;
import com.atlassian.jira.user.util.MockUserManager;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.jira.util.VelocityParamFactory;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.velocity.VelocityManager;

import de.uhd.ifi.se.decision.management.jira.mocks.MockAvatarManager;
import de.uhd.ifi.se.decision.management.jira.mocks.MockCommentManager;
import de.uhd.ifi.se.decision.management.jira.mocks.MockIssueLinkManager;
import de.uhd.ifi.se.decision.management.jira.mocks.MockIssueLinkTypeManager;
import de.uhd.ifi.se.decision.management.jira.mocks.MockIssueManagerSelfImpl;
import de.uhd.ifi.se.decision.management.jira.mocks.MockIssueService;
import de.uhd.ifi.se.decision.management.jira.mocks.MockIssueTypeManager;
import de.uhd.ifi.se.decision.management.jira.mocks.MockJiraHomeForTesting;
import de.uhd.ifi.se.decision.management.jira.mocks.MockPluginSettingsFactory;
import de.uhd.ifi.se.decision.management.jira.mocks.MockProjectRoleManager;
import de.uhd.ifi.se.decision.management.jira.mocks.MockVelocityManager;
import de.uhd.ifi.se.decision.management.jira.mocks.MockVelocityParamFactory;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.DecisionKnowledgeElementInDatabase;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.LinkInDatabase;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.PartOfJiraIssueTextInDatabase;
import net.java.ao.EntityManager;
import net.java.ao.test.jdbc.DatabaseUpdater;

public class TestSetUpWithIssues {
	private ProjectManager projectManager;
	private IssueManager issueManager;
	private ConstantsManager constantsManager;
	protected MockIssue issue;

	public void initialization() {
		projectManager = new MockProjectManager();
		issueManager = new MockIssueManagerSelfImpl();
		constantsManager = new MockConstantsManager();
		// With the Value we could add some kinde of real support of the classifier
		// tests
		MockApplicationProperties mockApplicationProperties = new MockApplicationProperties();
		mockApplicationProperties.setString(APKeys.JIRA_BASEURL, "null");
		IssueTypeManager issueTypeManager = new MockIssueTypeManager();
		try {
			((MockIssueTypeManager) issueTypeManager).addingAllIssueTypes();
		} catch (CreateException e) {
			e.printStackTrace();
		}
		IssueService issueService = new MockIssueService();

		UserManager userManager = new MockUserManager();
		ApplicationUser user = new MockApplicationUser("NoFails");
		ApplicationUser user2 = new MockApplicationUser("WithFails");
		ApplicationUser user3 = new MockApplicationUser("NoSysAdmin");
		ApplicationUser user4 = new MockApplicationUser("SysAdmin");
		((MockUserManager) userManager).addUser(user);
		((MockUserManager) userManager).addUser(user2);
		((MockUserManager) userManager).addUser(user3);
		((MockUserManager) userManager).addUser(user4);

		new MockComponentWorker().init().addMock(IssueManager.class, issueManager)
				.addMock(IssueLinkManager.class, new MockIssueLinkManager())
				.addMock(IssueLinkTypeManager.class, new MockIssueLinkTypeManager())
				.addMock(IssueService.class, issueService).addMock(ProjectManager.class, projectManager)
				.addMock(UserManager.class, userManager).addMock(ConstantsManager.class, constantsManager)
				.addMock(ProjectRoleManager.class, new MockProjectRoleManager())
				.addMock(VelocityManager.class, new MockVelocityManager())
				.addMock(VelocityParamFactory.class, new MockVelocityParamFactory())
				.addMock(AvatarManager.class, new MockAvatarManager()).addMock(IssueTypeManager.class, issueTypeManager)
				.addMock(IssueTypeSchemeManager.class, mock(IssueTypeSchemeManager.class))
				.addMock(FieldConfigScheme.class, mock(FieldConfigScheme.class))
				.addMock(PluginSettingsFactory.class, new MockPluginSettingsFactory())
				.addMock(OptionSetManager.class, mock(OptionSetManager.class))
				.addMock(CommentManager.class, new MockCommentManager())
				.addMock(ApplicationProperties.class, mockApplicationProperties)
				.addMock(JiraHome.class, new MockJiraHomeForTesting())
				.addMock(SearchService.class, mock(SearchService.class));

		creatingProjectIssueStructure();
	}

	private void creatingProjectIssueStructure() {
		Project project = new MockProject(1, "TEST");
		((MockProject) project).setKey("TEST");
		((MockProjectManager) projectManager).addProject(project);

		ArrayList<KnowledgeType> types = new ArrayList<>();

		types.add(KnowledgeType.OTHER);
		types.add(KnowledgeType.DECISION);
		types.add(KnowledgeType.QUESTION);
		types.add(KnowledgeType.ISSUE);
		types.add(KnowledgeType.GOAL);
		types.add(KnowledgeType.SOLUTION);
		types.add(KnowledgeType.ALTERNATIVE);
		types.add(KnowledgeType.CLAIM);
		types.add(KnowledgeType.CONTEXT);
		types.add(KnowledgeType.ALTERNATIVE);
		types.add(KnowledgeType.CONSTRAINT);
		types.add(KnowledgeType.IMPLICATION);
		types.add(KnowledgeType.ASSESSMENT);
		types.add(KnowledgeType.ARGUMENT);
		types.add(KnowledgeType.PROBLEM);

		for (int i = 2; i < types.size() + 2; i++) {
			if (types.get(i - 2).toString().equals("Problem")) {
				MutableIssue issue = new MockIssue(30, "TEST-" + 30);
				((MockIssue) issue).setProjectId(project.getId());
				issue.setProjectObject(project);
				IssueType issueType = new MockIssueType(i, types.get(i - 2).toString().toLowerCase(Locale.ENGLISH));
				((MockConstantsManager) constantsManager).addIssueType(issueType);
				issue.setIssueType(issueType);
				issue.setSummary("Test");
				((MockIssueManagerSelfImpl) issueManager).addIssue(issue);
			} else {
				MutableIssue issue = new MockIssue(i, "TEST-" + i);
				((MockIssue) issue).setProjectId(project.getId());
				issue.setProjectObject(project);
				IssueType issueType = new MockIssueType(i, types.get(i - 2).toString().toLowerCase(Locale.ENGLISH));
				((MockConstantsManager) constantsManager).addIssueType(issueType);
				issue.setIssueType(issueType);
				issue.setSummary("Test");
				((MockIssueManagerSelfImpl) issueManager).addIssue(issue);
				if (i > types.size() - 4) {
					issue.setParentId((long) 3);
				}
			}
		}
		MutableIssue issue = new MockIssue(50, "TEST-50");
		((MockIssue) issue).setProjectId(project.getId());
		issue.setProjectObject(project);
		IssueType issueType = new MockIssueType(50, "Class");
		((MockConstantsManager) constantsManager).addIssueType(issueType);
		issue.setIssueType(issueType);
		issue.setSummary("Test");
		issue.setDescription("Test");
		((MockIssueManagerSelfImpl) issueManager).addIssue(issue);
		issue.setParentId((long) 3);

		Project condecProject = new MockProject(3, "CONDEC");
		((MockProject) condecProject).setKey("CONDEC");
		((MockProjectManager) projectManager).addProject(condecProject);
		MutableIssue cuuIssue = new MockIssue(1234, "CONDEC-1234");
		cuuIssue.setIssueType(constantsManager.getIssueType("2"));
		cuuIssue.setSummary("Test Send");
		cuuIssue.setProjectObject(condecProject);
		((MockIssueManagerSelfImpl) issueManager).addIssue(cuuIssue);
	}

	public static final class AoSentenceTestDatabaseUpdater implements DatabaseUpdater {
		@SuppressWarnings("unchecked")
		@Override
		public void update(EntityManager entityManager) throws Exception {
			entityManager.migrate(DecisionKnowledgeElementInDatabase.class);
			entityManager.migrate(PartOfJiraIssueTextInDatabase.class);
			entityManager.migrate(LinkInDatabase.class);
		}
	}

	protected MockIssue createGlobalIssue() {
		Project project = ComponentAccessor.getProjectManager().getProjectByCurrentKey("TEST");
		issue = new MockIssue(30, "TEST-" + 30);
		((MockIssue) issue).setProjectId(project.getId());
		issue.setProjectObject(project);
		IssueType issueType = new MockIssueType(1, KnowledgeType.DECISION.toString().toLowerCase(Locale.ENGLISH));
		issue.setIssueType(issueType);
		issue.setSummary("Test");
		return issue;
	}

	public static MockIssue createIssue() {
		Project project = ComponentAccessor.getProjectManager().getProjectByCurrentKey("TEST");
		MockIssue issue = new MockIssue(30, "TEST-" + 30);
		((MockIssue) issue).setProjectId(project.getId());
		issue.setProjectObject(project);
		IssueType issueType = new MockIssueType(1, KnowledgeType.DECISION.toString().toLowerCase(Locale.ENGLISH));
		issue.setIssueType(issueType);
		issue.setSummary("Test");
		return issue;
	}
}