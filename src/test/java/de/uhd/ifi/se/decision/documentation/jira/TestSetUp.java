package de.uhd.ifi.se.decision.documentation.jira;

import java.util.ArrayList;

import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.config.ConstantsManager;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.issue.issuetype.MockIssueType;
import com.atlassian.jira.issue.link.IssueLinkManager;
import com.atlassian.jira.issue.link.IssueLinkTypeManager;
import com.atlassian.jira.mock.MockConstantsManager;
import com.atlassian.jira.mock.MockProjectManager;
import com.atlassian.jira.mock.component.MockComponentWorker;
import com.atlassian.jira.mock.issue.MockIssue;
import com.atlassian.jira.project.MockProject;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.MockApplicationUser;
import com.atlassian.jira.user.util.MockUserManager;
import com.atlassian.jira.user.util.UserManager;

import de.uhd.ifi.se.decision.documentation.jira.mocks.MockIssueLinkManager;
import de.uhd.ifi.se.decision.documentation.jira.mocks.MockIssueLinkTypeManager;
import de.uhd.ifi.se.decision.documentation.jira.mocks.MockIssueManager;
import de.uhd.ifi.se.decision.documentation.jira.mocks.MockIssueService;
import de.uhd.ifi.se.decision.documentation.jira.model.KnowledgeType;

public class TestSetUp {
	private ProjectManager projectManager;
	private IssueManager issueManager;
	private ConstantsManager constantsManager;
	private UserManager userManager;

	public void initialization() {
		projectManager = new MockProjectManager();
		issueManager = new MockIssueManager();
		constantsManager = new MockConstantsManager();
		IssueService issueService = new MockIssueService();

		userManager = new MockUserManager();
		ApplicationUser user = new MockApplicationUser("NoFails");
		ApplicationUser user2 = new MockApplicationUser("WithFails");
		((MockUserManager) userManager).addUser(user);
		((MockUserManager) userManager).addUser(user2);

		creatingProjectIssueStructure();

		new MockComponentWorker().init().addMock(IssueManager.class, issueManager)
				.addMock(IssueLinkManager.class, new MockIssueLinkManager())
				.addMock(IssueLinkTypeManager.class, new MockIssueLinkTypeManager()).addMock(IssueService.class, issueService)
				.addMock(ProjectManager.class, projectManager).addMock(UserManager.class, userManager)
				.addMock(ConstantsManager.class, constantsManager);

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
			if (types.get(i - 2).equals("Problem")) {
				MutableIssue issue = new MockIssue(30, "TEST-" + 30);
				((MockIssue) issue).setProjectId(project.getId());
				((MockIssue) issue).setProjectObject(project);
				IssueType issueType = new MockIssueType(i, types.get(i - 2).toString().toLowerCase());
				((MockConstantsManager) constantsManager).addIssueType(issueType);
				((MockIssue) issue).setIssueType(issueType);
				((MockIssue) issue).setSummary("Test");
				((MockIssueManager) issueManager).addIssue(issue);
			} else {
				MutableIssue issue = new MockIssue(i, "TEST-" + i);
				((MockIssue) issue).setProjectId(project.getId());
				((MockIssue) issue).setProjectObject(project);
				IssueType issueType = new MockIssueType(i, types.get(i - 2).toString().toLowerCase());
				((MockConstantsManager) constantsManager).addIssueType(issueType);
				((MockIssue) issue).setIssueType(issueType);
				((MockIssue) issue).setSummary("Test");
				((MockIssueManager) issueManager).addIssue(issue);
				if (i > types.size() - 4) {
					((MockIssue) issue).setParentId((long) 3);
				}
			}
		}
		MutableIssue issue = new MockIssue(50, "TEST-50");
		((MockIssue) issue).setProjectId(project.getId());
		((MockIssue) issue).setProjectObject(project);
		IssueType issueType = new MockIssueType(50, "Class");
		((MockConstantsManager) constantsManager).addIssueType(issueType);
		((MockIssue) issue).setIssueType(issueType);
		((MockIssue) issue).setSummary("Test");
		((MockIssueManager) issueManager).addIssue(issue);
		((MockIssue) issue).setParentId((long) 3);
	}

}
