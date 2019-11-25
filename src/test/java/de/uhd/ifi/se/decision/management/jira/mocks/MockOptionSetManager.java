package de.uhd.ifi.se.decision.management.jira.mocks;

import java.util.ArrayList;
import java.util.Collection;

import com.atlassian.jira.issue.fields.config.FieldConfig;
import com.atlassian.jira.issue.fields.option.Option;
import com.atlassian.jira.issue.fields.option.OptionSet;
import com.atlassian.jira.issue.fields.option.OptionSetManager;

public class MockOptionSetManager implements OptionSetManager {

	@Override
	public OptionSet addOptionToOptionSet(FieldConfig arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OptionSet createOptionSet(FieldConfig arg0, Collection<String> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OptionSet getOptionsForConfig(FieldConfig arg0) {
		return new OptionSet() {

			@Override
			public Collection<Option> getOptions() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Collection<String> getOptionIds() {
				return new ArrayList<String>();
			}

			@Override
			public void addOption(String arg0, String arg1) {
				// TODO Auto-generated method stub
			}
		};
	}

	@Override
	public OptionSet removeOptionFromOptionSet(FieldConfig arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeOptionSet(FieldConfig arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public OptionSet updateOptionSet(FieldConfig arg0, Collection<String> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

}
