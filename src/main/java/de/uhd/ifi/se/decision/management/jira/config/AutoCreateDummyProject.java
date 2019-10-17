package de.uhd.ifi.se.decision.management.jira.config;

import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.bc.project.ProjectCreationData;
import com.atlassian.jira.bc.project.ProjectService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.IssueTypeManager;
import com.atlassian.jira.issue.IssueInputParameters;
import com.atlassian.jira.issue.IssueInputParametersImpl;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.project.AssigneeTypes;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.user.ApplicationUser;

import java.util.Collection;

public class AutoCreateDummyProject {
	private static final int MAX_ISSUES = 550;
	private ApplicationUser user = ComponentAccessor.getUserManager().getUserByName("admin");

	private void createDummyIssues(Project prj) {
		IssueService issueService = ComponentAccessor.getComponent(IssueService.class);

		IssueTypeManager issueTypeManager = ComponentAccessor.getComponent(IssueTypeManager.class);
		String issueTypeId = "";
		if (issueTypeManager.getIssueType("Issue")!=null) {
			issueTypeId = issueTypeManager.getIssueType("Issue").getId();
		}
		else {
			Collection<IssueType> issueTypes = issueTypeManager.getIssueTypes();
			for (IssueType issueType : issueTypes) {
				String typeName = issueType.getName();
				String typeId = issueType.getId();
				if (typeName.contains("Issue")) {
					issueTypeId = typeId;
				}
			}
		}

		for (int i=1;i<=MAX_ISSUES;i++) {
			IssueInputParameters issueInput = new IssueInputParametersImpl();
			issueInput.setProjectId(prj.getId());
			issueInput.setSummary("bulk issue" + String.valueOf(i));
			issueInput.setIssueTypeId(issueTypeId);
			issueInput.setReporterId(user.getKey());

			try {
				IssueService.CreateValidationResult validIssue = issueService.validateCreate(user, issueInput);
				issueService.create(user, validIssue);
			}
			catch (Exception ex) {
				System.err.println(ex.getMessage());
				ex.printStackTrace();
			}
		}
	}

	private Project createDummyProject() {
		String name = "ConDec Developer";
		String key  = "CONDEC";
		String description = "A new blank project.";
		ProjectService projectService = ComponentAccessor.getComponent(ProjectService.class);

		ProjectCreationData.Builder pcdBuilder = new ProjectCreationData.Builder();
		pcdBuilder.withKey(key);
		pcdBuilder.withName(name);
		pcdBuilder.withDescription(description);
		pcdBuilder.withLead(user);
		pcdBuilder.withType("business");
		pcdBuilder.withAssigneeType(AssigneeTypes.UNASSIGNED);
		pcdBuilder.withUrl("http://localhost");
		ProjectCreationData pcd = pcdBuilder.build();
		
		Project prj = null;
		try {
			ProjectService.CreateProjectValidationResult projectResult = projectService.validateCreateProject(user, pcd);
			prj = projectService.createProject(projectResult);
		}
		catch (Exception ex) {
			System.err.println(ex.getMessage());
			ex.printStackTrace();
		}
		return prj;
	}
}
