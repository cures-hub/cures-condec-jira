package de.uhd.ifi.se.decision.management.jira.mocks;

import com.atlassian.jira.database.MockQueryDslAccessor;
import com.atlassian.jira.issue.fields.config.FieldConfig;
import com.atlassian.jira.issue.fields.config.FieldConfigScheme;
import com.atlassian.jira.issue.fields.option.OptionSet;
import com.atlassian.jira.issue.fields.option.OptionSetManager;
import com.atlassian.jira.mock.MockConstantsManager;

import javax.annotation.Nonnull;
import java.util.Collection;

import static org.mockito.Mockito.mock;

public class MockOptionSetManager implements OptionSetManager{

    @Nonnull
    @Override
    public OptionSet getOptionsForConfig(FieldConfig fieldConfig) {
        OptionSet optionSet = mock(OptionSet.class);
        return optionSet;
    }

    @Nonnull
    @Override
    public OptionSet createOptionSet(FieldConfig fieldConfig, Collection<String> collection) {
        return null;
    }

    @Nonnull
    @Override
    public OptionSet updateOptionSet(FieldConfig fieldConfig, Collection<String> collection) {
        return null;
    }

    @Nonnull
    @Override
    public OptionSet addOptionToOptionSet(FieldConfig fieldConfig, String s) {
        return null;
    }

    @Nonnull
    @Override
    public OptionSet removeOptionFromOptionSet(FieldConfig fieldConfig, String s) {
        return null;
    }

    @Override
    public void removeOptionSet(FieldConfig fieldConfig) {

    }
}
