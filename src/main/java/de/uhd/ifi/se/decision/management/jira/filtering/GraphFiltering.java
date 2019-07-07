package de.uhd.ifi.se.decision.management.jira.filtering;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.bc.issue.search.SearchService;
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
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.JiraIssueTextPersistenceManager;

public class GraphFiltering {
	static final Logger LOGGER = LoggerFactory.getLogger(GraphFiltering.class);

	FilterSettings filterSettings;

	ApplicationUser user;
	List<Clause> resultingClauses;
	String resultingQuery;
	JiraQueryHandler queryHandler;

	public JiraQueryHandler getQueryHandler() {
		return queryHandler;
	}

	public GraphFiltering(FilterSettings filterSettings, ApplicationUser user, boolean mergeFilterQueryWithProjectKey) {
		this.filterSettings = filterSettings;
		this.user = user;
		this.queryHandler = new JiraQueryHandler(user, filterSettings.getProjectKey(), filterSettings.getSearchString());
	}

	public List<Issue> produceResultsWithAdditionalFilters(String resultingQuery) {
		JqlClauseBuilder clauseBuilder = JqlQueryBuilder.newClauseBuilder();
		clauseBuilder.project(filterSettings.getProjectKey());
		boolean first = true;
		if (this.resultingClauses != null) {
			for (Clause clause : this.resultingClauses) {
				if (!matchesCreatedOrIssueType(clause)) {
					JqlClauseBuilder newQueryBuilder = JqlQueryBuilder.newClauseBuilder(clauseBuilder.buildQuery());
					if (first) {
						newQueryBuilder.and();
						first = false;
					}
					newQueryBuilder.addClause(clause);
					clauseBuilder = newQueryBuilder;
				}
			}
		} else {
			if (!matchesCreatedOrIssueType(resultingQuery) && resultingQuery != null) {
				clauseBuilder.addCondition(resultingQuery);
			}
		}
		clauseBuilder = addIssueTypes(clauseBuilder);
		clauseBuilder = addTimeFilter(clauseBuilder);
		return processQueryResult(clauseBuilder);
	}

	// New issue type filter function
	private JqlClauseBuilder addIssueTypes(JqlClauseBuilder queryBuilder) {
		JqlClauseBuilder clauseBuilder = JqlQueryBuilder.newClauseBuilder(queryBuilder.buildQuery());
		clauseBuilder.and();
		String[] types = new String[filterSettings.getIssueTypes().size()];
		for (int i = 0; i < filterSettings.getIssueTypes().size(); i++) {
			types[i] = filterSettings.getIssueTypes().get(i).toString();
		}
		clauseBuilder.issueType(types);
		return clauseBuilder;
	}

	// New time filter function
	private JqlClauseBuilder addTimeFilter(JqlClauseBuilder queryBuilder) {
		if (filterSettings.getCreatedEarliest() >= 0) {
			JqlClauseBuilder newQueryBuilder = JqlQueryBuilder.newClauseBuilder(queryBuilder.buildQuery());
			newQueryBuilder.and();
			newQueryBuilder.addDateCondition("created", Operator.GREATER_THAN_EQUALS,
					new Date(filterSettings.getCreatedEarliest()));
			return newQueryBuilder;
		}
		if (filterSettings.getCreatedLatest() >= 0) {
			JqlClauseBuilder newQueryBuilder = JqlQueryBuilder.newClauseBuilder(queryBuilder.buildQuery());
			newQueryBuilder.and();
			newQueryBuilder.addDateCondition("created", Operator.LESS_THAN_EQUALS,
					new Date(filterSettings.getCreatedLatest()));
			return newQueryBuilder;
		}
		return queryBuilder;
	}

	// New process of the final result
	private List<Issue> processQueryResult(JqlClauseBuilder queryBuilder) {
		List<Issue> resultingIssues = new ArrayList<>();
		Query finalQuery = queryBuilder.buildQuery();
		final SearchService.ParseResult parseResult = queryHandler.getSearchService().parseQuery(user,
				queryHandler.getSearchService().getJqlString(finalQuery));
		if (parseResult.isValid()) {
			List<Clause> clauses = parseResult.getQuery().getWhereClause().getClauses();

			if (!clauses.isEmpty()) {
				this.resultingClauses = parseResult.getQuery().getWhereClause().getClauses();
				queryHandler.findDatesInQuery(clauses);
				queryHandler.getNamesOfJiraIssueTypesInQuery(clauses);
			} else {
				this.resultingQuery = queryHandler.getSearchService().getGeneratedJqlString(queryBuilder.buildQuery());
				queryHandler.findDatesInQuery(this.resultingQuery);
				queryHandler.getNamesOfJiraIssueTypesInQuery(this.resultingQuery);
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
		return resultingIssues;
	}

	private boolean matchesCreatedOrIssueType(String resultingQuery) {
		if (queryHandler.isQueryContainsIssueTypes() && resultingQuery.contains("issuetype")) {
			return true;
		}
		if (queryHandler.isQueryContainsCreationDate()) {
			// TODO
			// if (queryHandler.getFilterSettings().getCreatedEarliest() >= 0 &&
			// resultingQuery.contains("created")
			// && resultingQuery.contains(">=")) {
			// return true;
			// }
			// if (queryHandler.getFilterSettings().getCreatedLatest() >= 0 &&
			// resultingQuery.contains("created")
			// && resultingQuery.contains("<=")) {
			// return true;
			// }
		}
		return false;
	}

	private boolean matchesCreatedOrIssueType(Clause clause) {
		if (queryHandler.isQueryContainsIssueTypes() && clause.getName().equals("issuetype")) {
			return true;
		}
		if (queryHandler.isQueryContainsCreationDate()) {
			// TODO
			// if (queryHandler.getFilterSettings().getCreatedEarliest() >= 0 &&
			// clause.getName().equals("created")
			// && clause.toString().contains(">=")) {
			// return true;
			// }
			// if (queryHandler.getFilterSettings().getCreatedLatest() >= 0 &&
			// clause.getName().equals("created")
			// && clause.toString().contains("<=")) {
			// return true;
			// }
		}
		return false;
	}

	public List<DecisionKnowledgeElement> getAllElementsMatchingQuery() {
		// List<DecisionKnowledgeElement> results = new ArrayList<>(getQueryResults());
		List<DecisionKnowledgeElement> results = new ArrayList<>();
		List<Issue> jiraIssuesForProject = JiraSearchServiceHelper.getAllJiraIssuesForProject(user,
				filterSettings.getProjectKey());
		if (jiraIssuesForProject == null) {
			return results;
		}
		for (Issue currentIssue : jiraIssuesForProject) {
			List<DecisionKnowledgeElement> elements = JiraIssueTextPersistenceManager
					.getElementsForIssue(currentIssue.getId(), filterSettings.getProjectKey());
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
			// TODO
			// if (queryHandler.getFilterSettings().getCreatedEarliest() > 0
			// && (element).getCreated().getTime() <
			// queryHandler.getFilterSettings().getCreatedEarliest()) {
			// return false;
			// }
			// if (queryHandler.getFilterSettings().getCreatedLatest() > 0
			// && (element).getCreated().getTime() >
			// queryHandler.getFilterSettings().getCreatedLatest()) {
			// return false;
			// }
		}
		if (queryHandler.isQueryContainsIssueTypes()) {
			// if (element.getType().equals(KnowledgeType.PRO) ||
			// element.getType().equals(KnowledgeType.CON)) {
			// if
			// (!queryHandler.getFilterSettings().getIssueTypes().contains(KnowledgeType.ARGUMENT))
			// {
			// return false;
			// }
			// } else if
			// (!queryHandler.getFilterSettings().getIssueTypes().contains(element)) {
			// return false;
			// }
			// TODO

		}

		return true;
	}

	public FilterSettings getFilterData() {
		return this.filterSettings;
	}

	public void setFilterData(FilterSettings filterData) {
		this.filterSettings = filterData;
	}

	public ApplicationUser getUser() {
		return user;
	}

	public void setUser(ApplicationUser user) {
		this.user = user;
	}

	// TODO
	public List<DecisionKnowledgeElement> getQueryResults() {
		return new ArrayList<>();
	}
}