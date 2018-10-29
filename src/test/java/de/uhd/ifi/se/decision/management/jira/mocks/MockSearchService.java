package de.uhd.ifi.se.decision.management.jira.mocks;

import java.util.ArrayList;
import java.util.List;

import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.search.SearchContext;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchResults;
import com.atlassian.jira.jql.context.QueryContext;
import com.atlassian.jira.mock.issue.MockIssue;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.util.MessageSet;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.query.Query;

public class MockSearchService implements SearchService  {

	@Override
	public boolean doesQueryFitFilterForm(ApplicationUser arg0, Query arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getGeneratedJqlString(Query arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getIssueSearchPath(ApplicationUser arg0, IssueSearchParameters arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getJqlString(Query arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public QueryContext getQueryContext(ApplicationUser arg0, Query arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getQueryString(ApplicationUser arg0, Query arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SearchContext getSearchContext(ApplicationUser arg0, Query arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public QueryContext getSimpleQueryContext(ApplicationUser arg0, Query arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ParseResult parseQuery(ApplicationUser arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Query sanitiseSearchQuery(ApplicationUser arg0, Query arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public SearchResults search(ApplicationUser arg0, Query arg1, PagerFilter arg2) throws SearchException {
		List<Issue> issueList = new ArrayList<Issue>();
		MockIssue i = new MockIssue(1337);
		i.setKey("Test-1337");
		issueList.add(i);
		return new SearchResults(issueList, arg2);
	}

	@Override
	public long searchCount(ApplicationUser arg0, Query arg1) throws SearchException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long searchCountOverrideSecurity(ApplicationUser arg0, Query arg1) throws SearchException {
		// TODO Auto-generated method stub
		return 0;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public SearchResults searchOverrideSecurity(ApplicationUser arg0, Query arg1, PagerFilter arg2)
			throws SearchException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MessageSet validateQuery(ApplicationUser arg0, Query arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MessageSet validateQuery(ApplicationUser arg0, Query arg1, Long arg2) {
		// TODO Auto-generated method stub
		return null;
	}

}
