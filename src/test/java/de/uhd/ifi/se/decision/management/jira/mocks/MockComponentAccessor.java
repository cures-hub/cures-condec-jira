package de.uhd.ifi.se.decision.management.jira.mocks;

import static org.mockito.Mockito.mock;

import com.atlassian.jira.avatar.AvatarManager;
import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.ConstantsManager;
import com.atlassian.jira.config.IssueTypeManager;
import com.atlassian.jira.config.util.JiraHome;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.changehistory.ChangeHistoryManager;
import com.atlassian.jira.issue.comments.CommentManager;
import com.atlassian.jira.issue.fields.config.FieldConfigScheme;
import com.atlassian.jira.issue.fields.config.manager.IssueTypeSchemeManager;
import com.atlassian.jira.issue.fields.option.OptionSetManager;
import com.atlassian.jira.issue.link.IssueLinkManager;
import com.atlassian.jira.issue.link.IssueLinkTypeManager;
import com.atlassian.jira.mock.MockConstantsManager;
import com.atlassian.jira.mock.MockProjectManager;
import com.atlassian.jira.mock.component.MockComponentWorker;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.security.roles.ProjectRoleManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.MockApplicationUser;
import com.atlassian.jira.user.util.MockUserManager;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.jira.util.VelocityParamFactory;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.velocity.VelocityManager;

public class MockComponentAccessor extends ComponentAccessor {

	public MockComponentAccessor() {
		ProjectManager projectManager = new MockProjectManager();
		IssueManager issueManager = new MockIssueManagerSelfImpl();
		ConstantsManager constantsManager = new MockConstantsManager();
		UserManager userManager = initUserManager();

		new MockComponentWorker().init().addMock(IssueManager.class, issueManager)
				.addMock(IssueLinkManager.class, new MockIssueLinkManager())
				.addMock(IssueLinkTypeManager.class, new MockIssueLinkTypeManager())
				.addMock(IssueService.class, new MockIssueService()).addMock(ProjectManager.class, projectManager)
				.addMock(UserManager.class, userManager).addMock(ConstantsManager.class, constantsManager)
				.addMock(ProjectRoleManager.class, new MockProjectRoleManager())
				.addMock(VelocityManager.class, new MockVelocityManager())
				.addMock(VelocityParamFactory.class, new MockVelocityParamFactory())
				.addMock(AvatarManager.class, new MockAvatarManager())
				.addMock(IssueTypeManager.class, new MockIssueTypeManager())
				.addMock(IssueTypeSchemeManager.class, mock(IssueTypeSchemeManager.class))
				.addMock(FieldConfigScheme.class, mock(FieldConfigScheme.class))
				.addMock(PluginSettingsFactory.class, new MockPluginSettingsFactory())
				.addMock(OptionSetManager.class, mock(OptionSetManager.class))
				.addMock(CommentManager.class, new MockCommentManager())
				.addMock(JiraHome.class, new MockJiraHomeForTesting())
				.addMock(SearchService.class, new MockSearchService())
				.addMock(TransactionTemplate.class, new MockTransactionTemplate())
				.addMock(ChangeHistoryManager.class, mock(ChangeHistoryManager.class));
	}

	public UserManager initUserManager() {
		UserManager userManager = new MockUserManager();
		ApplicationUser user = new MockApplicationUser("SysAdmin");
		((MockUserManager) userManager).addUser(user);
		return userManager;
	}
}
