package de.uhd.ifi.se.decision.management.jira.mocks;

import javax.annotation.Nonnull;

import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.search.SearchContext;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchResults;
import com.atlassian.jira.jql.context.QueryContext;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.util.MessageSet;
import com.atlassian.jira.util.MessageSetImpl;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.query.Query;
import com.atlassian.query.QueryImpl;
import com.atlassian.query.clause.TerminalClauseImpl;

public class MockSearchService implements SearchService {
	@Override
	public SearchResults<Issue> search(ApplicationUser applicationUser, Query query, PagerFilter pagerFilter)
			throws SearchException {
		return null;
	}

	@Override
	public SearchResults<Issue> searchOverrideSecurity(ApplicationUser applicationUser, Query query,
			PagerFilter pagerFilter) throws SearchException {
		return null;
	}

	@Override
	public long searchCount(ApplicationUser applicationUser, Query query) throws SearchException {
		return 0;
	}

	@Override
	public long searchCountOverrideSecurity(ApplicationUser applicationUser, Query query) throws SearchException {
		return 0;
	}

	@Override
	public String getQueryString(ApplicationUser applicationUser, Query query) {
		return null;
	}

	@Nonnull
	@Override
	public String getIssueSearchPath(ApplicationUser applicationUser,
			@Nonnull IssueSearchParameters issueSearchParameters) {
		return null;
	}

	@Override
	public ParseResult parseQuery(ApplicationUser applicationUser, String s) {
		ParseResult result = new ParseResult(new QueryImpl(new TerminalClauseImpl("Test", (long) 21323), s),
				new MessageSetImpl());
		return result;
	}

	@Nonnull
	@Override
	public MessageSet validateQuery(ApplicationUser applicationUser, @Nonnull Query query) {
		return null;
	}

	@Nonnull
	@Override
	public MessageSet validateQuery(ApplicationUser applicationUser, @Nonnull Query query, Long aLong) {
		return null;
	}

	@Override
	public boolean doesQueryFitFilterForm(ApplicationUser applicationUser, Query query) {
		return false;
	}

	@Override
	public QueryContext getQueryContext(ApplicationUser applicationUser, Query query) {
		return null;
	}

	@Override
	public QueryContext getSimpleQueryContext(ApplicationUser applicationUser, Query query) {
		return null;
	}

	@Override
	public SearchContext getSearchContext(ApplicationUser applicationUser, Query query) {
		return null;
	}

	@Override
	public String getJqlString(Query query) {
		return null;
	}

	@Override
	public String getGeneratedJqlString(Query query) {
		return "Test";
	}

	@Override
	public Query sanitiseSearchQuery(ApplicationUser applicationUser, Query query) {
		return null;
	}
}
