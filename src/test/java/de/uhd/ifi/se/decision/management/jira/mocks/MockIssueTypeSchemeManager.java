package de.uhd.ifi.se.decision.management.jira.mocks;

import com.atlassian.jira.exception.CreateException;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.fields.config.FieldConfig;
import com.atlassian.jira.issue.fields.config.FieldConfigScheme;
import com.atlassian.jira.issue.fields.config.manager.IssueTypeSchemeManager;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.project.Project;
import org.ofbiz.core.entity.GenericValue;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;

import static org.mockito.Mockito.mock;

public class MockIssueTypeSchemeManager implements IssueTypeSchemeManager{
    @Override
    public FieldConfigScheme create(String s, String s1, List<String> list) {
        return null;
    }

    @Override
    public FieldConfigScheme update(FieldConfigScheme fieldConfigScheme, Collection<String> collection) {
        return null;
    }

    @Override
    public FieldConfigScheme getDefaultIssueTypeScheme() {
        return null;
    }

    @Override
    public boolean isDefaultIssueTypeScheme(FieldConfigScheme fieldConfigScheme) {
        return false;
    }

    @Override
    public void addOptionToDefault(String s) {

    }

    @Override
    public Collection<FieldConfigScheme> getAllRelatedSchemes(String s) {
        return null;
    }

    @Override
    public void removeOptionFromAllSchemes(String s) {

    }

    @Override
    public void deleteScheme(FieldConfigScheme fieldConfigScheme) {

    }

    @Override
    public List<FieldConfigScheme> getAllSchemes() {
        return null;
    }

    @Override
    public IssueType getDefaultValue(Issue issue) {
        return null;
    }

    @Override
    public IssueType getDefaultValue(FieldConfig fieldConfig) {
        return null;
    }

    @Override
    public void setDefaultValue(FieldConfig fieldConfig, String s) {

    }

    @Override
    public IssueType getDefaultValue(GenericValue genericValue) {
        return null;
    }

    @Override
    public IssueType getDefaultIssueType(Project project) {
        return null;
    }

    @Override
    public FieldConfigScheme getConfigScheme(GenericValue genericValue) {
        FieldConfigScheme scheme = mock(FieldConfigScheme.class);
        return scheme;
    }

    @Override
    public FieldConfigScheme getConfigScheme(Project project) {
        FieldConfigScheme scheme = mock(FieldConfigScheme.class);
        return scheme;
    }

    @Nonnull
    @Override
    public Collection<IssueType> getIssueTypesForProject(GenericValue genericValue) {
        return null;
    }

    @Nonnull
    @Override
    public Collection<IssueType> getIssueTypesForProject(Project project) {
        MockIssueTypeManager typeManager = new MockIssueTypeManager();
        try {
            typeManager.addingAllIssueTypes();
        } catch (CreateException e) {
            e.printStackTrace();
        }
        Collection<IssueType> types = typeManager.getIssueTypes();
        IssueType removeType = null;
        for(IssueType type: types){
            if(type.getName().equals("Decision")){
                removeType = type;
            }
        }
        if(removeType!=null) {
            types.remove(removeType);
        }
        return types;
    }

    @Nonnull
    @Override
    public Collection<IssueType> getIssueTypesForDefaultScheme() {
        return null;
    }

    @Nonnull
    @Override
    public Collection<IssueType> getSubTaskIssueTypesForProject(@Nonnull Project project) {
        return null;
    }

    @Nonnull
    @Override
    public Collection<IssueType> getNonSubTaskIssueTypesForProject(Project project) {
        return null;
    }
}
