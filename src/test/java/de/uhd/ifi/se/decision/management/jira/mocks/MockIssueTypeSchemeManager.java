package de.uhd.ifi.se.decision.management.jira.mocks;

import java.util.Collection;
import java.util.List;

import org.ofbiz.core.entity.GenericValue;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.fields.config.FieldConfig;
import com.atlassian.jira.issue.fields.config.FieldConfigScheme;
import com.atlassian.jira.issue.fields.config.manager.IssueTypeSchemeManager;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.project.Project;

import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssueTypes;

public class MockIssueTypeSchemeManager implements IssueTypeSchemeManager {

	@Override
	public void addOptionToDefault(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public FieldConfigScheme create(String arg0, String arg1, List<String> arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteScheme(FieldConfigScheme arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public Collection<FieldConfigScheme> getAllRelatedSchemes(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<FieldConfigScheme> getAllSchemes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FieldConfigScheme getConfigScheme(GenericValue arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FieldConfigScheme getConfigScheme(Project arg0) {
		FieldConfigScheme configScheme = new MockFieldConfigScheme();
		((MockFieldConfigScheme) configScheme).setId((long) 1);
		((MockFieldConfigScheme) configScheme).setName("TestScheme");
		return configScheme;
	}

	@Override
	public IssueType getDefaultIssueType(Project arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FieldConfigScheme getDefaultIssueTypeScheme() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getDefaultIssueTypeSchemeId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public IssueType getDefaultValue(Issue arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IssueType getDefaultValue(FieldConfig arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IssueType getDefaultValue(GenericValue arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<IssueType> getIssueTypesForDefaultScheme() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<IssueType> getIssueTypesForProject(GenericValue arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<IssueType> getIssueTypesForProject(Project project) {
		return JiraIssueTypes.getTestTypes();
	}

	@Override
	public Collection<IssueType> getIssueTypesForScheme(FieldConfigScheme arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<IssueType> getNonSubTaskIssueTypesForProject(Project arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<IssueType> getSubTaskIssueTypesForProject(Project arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isDefaultIssueTypeScheme(FieldConfigScheme arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeOptionFromAllSchemes(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDefaultValue(FieldConfig arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public FieldConfigScheme setProjectAssociationsForIssueTypeScheme(FieldConfigScheme arg0,
			Collection<Project> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FieldConfigScheme update(FieldConfigScheme arg0, Collection<String> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

}
