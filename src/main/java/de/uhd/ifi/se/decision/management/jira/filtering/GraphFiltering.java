package de.uhd.ifi.se.decision.management.jira.filtering;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.bc.issue.search.SearchService.ParseResult;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchResults;
import com.atlassian.jira.jql.builder.JqlClauseBuilder;
import com.atlassian.jira.jql.builder.JqlQueryBuilder;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.query.Query;
import com.atlassian.query.clause.Clause;
import com.atlassian.query.operator.Operator;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.JiraIssueTextPersistenceManager;

public class GraphFiltering {
	static final Logger LOGGER = LoggerFactory.getLogger(GraphFiltering.class);

	private FilterSettings filterData;

	private ApplicationUser user;
	private List<DecisionKnowledgeElement> queryResults;
	private List<Clause> resultingClauses;
	private String resultingQuery;
	private QueryHandler queryHandler;

	public QueryHandler getQueryHandler() {
		return queryHandler;
	}

	public GraphFiltering(FilterSettings filterData, ApplicationUser user, boolean mergeFilterQueryWithProjectKey) {
		this.filterData = filterData;
		this.user = user;
		this.queryResults = new ArrayList<DecisionKnowledgeElement>();
		this.queryHandler = new QueryHandler(user, filterData.getProjectKey(), mergeFilterQueryWithProjectKey);
	}

	public List<Issue> getJiraIssuesFromQuery(String query) {
		ParseResult parseResult = queryHandler.processParseResult(filterData.getSearchString());
		if (!parseResult.isValid()) {
			LOGGER.error(parseResult.getErrors().toString());
			return new ArrayList<Issue>();
		}

		List<Issue> jiraIssues = new ArrayList<Issue>();
		List<Clause> clauses = parseResult.getQuery().getWhereClause().getClauses();

		if (!clauses.isEmpty()) {
			this.resultingClauses = clauses;
			queryHandler.findDatesInQuery(clauses);
			queryHandler.findIssueTypesInQuery(clauses);
		} else {
			String finalQuery = queryHandler.getFinalQuery();
			this.resultingQuery = finalQuery;
			queryHandler.findDatesInQuery(finalQuery);
			queryHandler.findIssueTypesInQuery(finalQuery);
		}
		try {
			SearchResults<Issue> results = queryHandler.getSearchService().search(this.user, parseResult.getQuery(),
					PagerFilter.getUnlimitedFilter());
			jiraIssues = JiraSearchServiceHelper.getJiraIssues(results);
		} catch (SearchException e) {
			LOGGER.error("Produce results from query failed. Message: " + e.getMessage());
			e.printStackTrace();
		}

		for (Issue issue : jiraIssues) {
			queryResults.add(new DecisionKnowledgeElementImpl(issue));
		}
		return jiraIssues;
	}

	public void produceResultsWithAdditionalFilters() {
		queryResults.clear();
		JqlClauseBuilder queryBuilder = JqlQueryBuilder.newClauseBuilder();
		queryBuilder.project(filterData.getProjectKey());
		boolean first = true;
		if (this.resultingClauses != null) {
			for (Clause clause : this.resultingClauses) {
				if (!matchesCreatedOrIssueType(clause)) {
					JqlClauseBuilder newQueryBuilder = JqlQueryBuilder.newClauseBuilder(queryBuilder.buildQuery());
					if (first) {
						newQueryBuilder.and();
						first = false;
					}
					newQueryBuilder.addClause(clause);
					queryBuilder = newQueryBuilder;
				}
			}
		} else {
			if (!matchesCreatedOrIssueType(resultingQuery) && resultingQuery != null) {
				queryBuilder.addCondition(resultingQuery);
			}
		}
		queryBuilder = addIssueTypes(queryBuilder);
		queryBuilder = addTimeFilter(queryBuilder);
		processQueryResult(queryBuilder);

	}

	// New issue type filter function
	private JqlClauseBuilder addIssueTypes(JqlClauseBuilder queryBuilder) {
		JqlClauseBuilder newQueryBuilder = JqlQueryBuilder.newClauseBuilder(queryBuilder.buildQuery());
		newQueryBuilder.and();
		String[] types = new String[filterData.getIssueTypes().size()];
		for (int i = 0; i < filterData.getIssueTypes().size(); i++) {
			types[i] = filterData.getIssueTypes().get(i).toString();
		}
		newQueryBuilder.issueType(types);
		return newQueryBuilder;
	}

	// New time filter function
	private JqlClauseBuilder addTimeFilter(JqlClauseBuilder queryBuilder) {
		if (filterData.getCreatedEarliest() >= 0) {
			JqlClauseBuilder newQueryBuilder = JqlQueryBuilder.newClauseBuilder(queryBuilder.buildQuery());
			newQueryBuilder.and();
			newQueryBuilder.addDateCondition("created", Operator.GREATER_THAN_EQUALS,
					new Date(filterData.getCreatedEarliest()));
			return newQueryBuilder;
		}
		if (filterData.getCreatedLatest() >= 0) {
			JqlClauseBuilder newQueryBuilder = JqlQueryBuilder.newClauseBuilder(queryBuilder.buildQuery());
			newQueryBuilder.and();
			newQueryBuilder.addDateCondition("created", Operator.LESS_THAN_EQUALS,
					new Date(filterData.getCreatedLatest()));
			return newQueryBuilder;
		}
		return queryBuilder;
	}

	// New process of the final result
	private void processQueryResult(JqlClauseBuilder queryBuilder) {
		List<Issue> resultingIssues = new ArrayList<>();
		Query finalQuery = queryBuilder.buildQuery();
		final SearchService.ParseResult parseResult = queryHandler.getSearchService().parseQuery(user,
				queryHandler.getSearchService().getJqlString(finalQuery));
		if (parseResult.isValid()) {
			List<Clause> clauses = parseResult.getQuery().getWhereClause().getClauses();

			if (!clauses.isEmpty()) {
				this.resultingClauses = parseResult.getQuery().getWhereClause().getClauses();
				queryHandler.findDatesInQuery(clauses);
				queryHandler.findIssueTypesInQuery(clauses);
			} else {
				this.resultingQuery = queryHandler.getSearchService().getGeneratedJqlString(queryBuilder.buildQuery());
				queryHandler.findDatesInQuery(this.resultingQuery);
				queryHandler.findIssueTypesInQuery(this.resultingQuery);
			}
			try {
				final SearchResults<Issue> results = queryHandler.getSearchService().search(this.user,
						parseResult.getQuery(), PagerFilter.getUnlimitedFilter());
				resultingIssues = JiraSearchServiceHelper.getJiraIssues(results);

			} catch (SearchException e) {
				LOGGER.error("Errors massage in processQueryResult. Message: " + e.getMessage());
			}
		} else {
			LOGGER.error(parseResult.getErrors().toString());
		}
		if (resultingIssues != null) {
			for (Issue issue : resultingIssues) {
				queryResults.add(new DecisionKnowledgeElementImpl(issue));
			}
		}
	}

	private boolean matchesCreatedOrIssueType(String resultingQuery) {
		if (queryHandler.isQueryContainsIssueTypes() && resultingQuery.contains("issuetype")) {
			return true;
		}
		if (queryHandler.isQueryContainsCreationDate()) {
			if (queryHandler.getFilterSettings().getCreatedEarliest() >= 0 && resultingQuery.contains("created")
					&& resultingQuery.contains(">=")) {
				return true;
			}
			if (queryHandler.getFilterSettings().getCreatedLatest() >= 0 && resultingQuery.contains("created")
					&& resultingQuery.contains("<=")) {
				return true;
			}
		}
		return false;
	}

	private boolean matchesCreatedOrIssueType(Clause clause) {
		if (queryHandler.isQueryContainsIssueTypes() && clause.getName().equals("issuetype")) {
			return true;
		}
		if (queryHandler.isQueryContainsCreationDate()) {
			if (queryHandler.getFilterSettings().getCreatedEarliest() >= 0 && clause.getName().equals("created")
					&& clause.toString().contains(">=")) {
				return true;
			}
			if (queryHandler.getFilterSettings().getCreatedLatest() >= 0 && clause.getName().equals("created")
					&& clause.toString().contains("<=")) {
				return true;
			}
		}
		return false;
	}

	public List<DecisionKnowledgeElement> getAllElementsMatchingQuery() {
		List<DecisionKnowledgeElement> results = new ArrayList<>(this.getQueryResults());
		List<Issue> jiraIssuesForProject = JiraSearchServiceHelper.getAllJiraIssuesForProject(user, filterData.getProjectKey());
		if (jiraIssuesForProject == null) {
			return results;
		}
		for (Issue currentIssue : jiraIssuesForProject) {
			List<DecisionKnowledgeElement> elements = JiraIssueTextPersistenceManager
					.getElementsForIssue(currentIssue.getId(), filterData.getProjectKey());
			for (DecisionKnowledgeElement currentElement : elements) {
				if (!results.contains(currentElement) && currentElement instanceof PartOfJiraIssueText
						&& checkIfJiraTextMatchesFilter(currentElement)) {
					results.add(currentElement);
				}
			}
		}

		return results;
	}

	private boolean checkIfJiraTextMatchesFilter(DecisionKnowledgeElement element) {
		if (queryHandler.isQueryContainsCreationDate()) {
			if (queryHandler.getFilterSettings().getCreatedEarliest() > 0
					&& (element).getCreated().getTime() < queryHandler.getFilterSettings().getCreatedEarliest()) {
				return false;
			}
			if (queryHandler.getFilterSettings().getCreatedLatest() > 0
					&& (element).getCreated().getTime() > queryHandler.getFilterSettings().getCreatedLatest()) {
				return false;
			}
		}
		if (queryHandler.isQueryContainsIssueTypes()) {
			if (element.getType().equals(KnowledgeType.PRO) || element.getType().equals(KnowledgeType.CON)) {
				if (!queryHandler.getFilterSettings().getIssueTypes().contains(KnowledgeType.ARGUMENT)) {
					return false;
				}
			} else if (!queryHandler.getFilterSettings().getIssueTypes().contains(element)) {
				return false;
			}
		}

		return true;
	}

	public FilterSettings getFilterData() {
		return this.filterData;
	}

	public void setFilterData(FilterSettings filterData) {
		this.filterData = filterData;
	}

	public ApplicationUser getUser() {
		return user;
	}

	public void setUser(ApplicationUser user) {
		this.user = user;
	}

	public List<DecisionKnowledgeElement> getQueryResults() {
		return queryResults;
	}
}