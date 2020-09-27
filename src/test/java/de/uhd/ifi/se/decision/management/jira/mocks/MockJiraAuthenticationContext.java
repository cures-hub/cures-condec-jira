package de.uhd.ifi.se.decision.management.jira.mocks;

import java.util.Locale;

import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.util.I18nHelper;
import com.atlassian.jira.web.util.OutlookDate;

import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

@SuppressWarnings("deprecation")
public class MockJiraAuthenticationContext implements JiraAuthenticationContext {

	@Override
	public void clearLoggedInUser() {
		// TODO Auto-generated method stub

	}

	@Override
	public I18nHelper getI18nBean() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public I18nHelper getI18nHelper() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Locale getLocale() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ApplicationUser getLoggedInUser() {
		return getUser();
	}

	@Override
	public OutlookDate getOutlookDate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getText(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ApplicationUser getUser() {
		return JiraUsers.SYS_ADMIN.getApplicationUser();
	}

	@Override
	public boolean isLoggedInUser() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setLoggedInUser(ApplicationUser arg0) {
		// TODO Auto-generated method stub

	}

}
