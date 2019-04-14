package de.uhd.ifi.se.decision.management.jira.filtering;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchRequest;
import com.atlassian.jira.issue.search.SearchRequestManager;
import com.atlassian.jira.issue.search.SearchResults;
import com.atlassian.jira.jql.builder.JqlClauseBuilder;
import com.atlassian.jira.jql.builder.JqlQueryBuilder;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.query.clause.Clause;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.JiraIssueTextPersistenceManager;

public class GraphFiltering {
	private static final Logger LOGGER = LoggerFactory.getLogger(GraphFiltering.class);

	private String query;
	private String projectKey;
	private boolean queryIsJQL;
	private boolean queryIsFilter;
	private boolean queryContainsCreationDate;
	private ApplicationUser user;
	private List<DecisionKnowledgeElement> queryResults;
	private long startDate;
	private long endDate;
	private SearchService searchService;
	private Boolean mergeFilterQueryWithProjectKey;

	public GraphFiltering(String projectKey, String query, ApplicationUser user,
			Boolean mergeFilterQueryWithProjectKey) {
		this.query = query;
		this.projectKey = projectKey;
		this.user = user;
		this.queryResults = new ArrayList<>();
		this.queryIsFilter = false;
		this.queryIsJQL = false;
		this.queryContainsCreationDate = false;
		this.startDate = -1;
		this.endDate = -1;
		this.mergeFilterQueryWithProjectKey = mergeFilterQueryWithProjectKey;
	}

	private String cropQuery() {
		String croppedQuery = "";
		String[] split = this.query.split("§");
		if (split.length > 1) {
			this.query = "?" + split[1];
		}
		if (this.query.indexOf("jql") == 1) {
			this.queryIsJQL = true;
		} else if (this.query.indexOf("filter") == 1) {
			this.queryIsFilter = true;
		}
		if (this.queryIsJQL) {
			croppedQuery = this.query.substring(5, this.query.length());
		} else if (this.queryIsFilter) {
			croppedQuery = this.query.substring(8, this.query.length());
		}
		return croppedQuery;
	}

	private String queryFromFilterId(long id) {
		String returnQuery;
		if (id <= 0) {
			switch ((int) id) {
			case 0: // Any other than the preset Filters
				returnQuery = "type != null";
				break;
			case -1: // My open issues
				returnQuery = "assignee = currentUser() AND resolution = Unresolved";
				break;
			case -2: // Reported by me
				returnQuery = "reporter = currentUser()";
				break;
			case -3: // Viewed recently
				returnQuery = "issuekey IN issueHistory()";
				break;
			case -4: // All issues
				returnQuery = "type != null";
				break;
			case -5: // Open issues
				returnQuery = "resolution = Unresolved";
				break;
			case -6: // Created recently
				returnQuery = "created >= -1w";
				break;
			case -7: // Resolved recently
				returnQuery = "resolutiondate >= -1w";
				break;
			case -8: // Updated recently
				returnQuery = "updated >= -1w";
				break;
			case -9: // Done issues
				returnQuery = "statusCategory = Done";
				break;
			default:
				returnQuery = "type != null";
				break;
			}
			returnQuery = "Project = " + projectKey + " AND " + returnQuery;
		} else {
			SearchRequestManager srm = ComponentAccessor.getComponentOfType(SearchRequestManager.class);
			SearchRequest filter = srm.getSharedEntity(id);
			returnQuery = filter.getQuery().getQueryString();
		}
		return returnQuery;
	}

	private String queryFromFilterString(String filterQuery) {
		String returnQuery;
		switch (filterQuery) {
		case "myopenissues": // My open issues
			returnQuery = "assignee = currentUser() AND resolution = Unresolved";
			break;
		case "reportedbyme": // Reported by me
			returnQuery = "reporter = currentUser()";
			break;
		case "recentlyviewed": // Viewed recently
			returnQuery = "issuekey IN issueHistory()";
			break;
		case "allissues": // All issues
			returnQuery = "type != null";
			break;
		case "allopenissues": // Open issues
			returnQuery = "resolution = Unresolved";
			break;
		case "addedrecently": // Created recently
			returnQuery = "created >= -1w";
			break;
		case "updatedrecently": // Updated recently
			returnQuery = "updated >= -1w";
			break;
		case "resolvedrecently": // Resolved recently
			returnQuery = "resolutiondate >= -1w";
			break;
		case "doneissues": // Done issues
			returnQuery = "statusCategory = Done";
			break;
		default:
			returnQuery = "type != null";
			break;
		}
		return returnQuery;
	}

	private void findDatesInQuery(List<Clause> clauses) {
		for (Clause clause : clauses) {
			if (clause.getName().equals("created")) {
				this.queryContainsCreationDate = true;
				long todaysDate = new Date().getTime();
				String time = clause.toString().substring(13, clause.toString().length() - 2);
				if (clause.toString().contains(" <= ")) {
					this.endDate = findEndtime(todaysDate, time);
				} else if (clause.toString().contains(" >= ")) {
					this.startDate = findStarttime(todaysDate, time);
				}
			}
		}
	}

	private void findDatesInQuery(String query) {
		if (query.contains("created")) {
			this.queryContainsCreationDate = true;
			long todaysDate = new Date().getTime();
			String time = query.substring(11, query.length());
			if (query.contains(" <= ")) {
				this.endDate = findEndtime(todaysDate, time);
			} else if (query.contains(" >= ")) {
				this.startDate = findStarttime(todaysDate, time);
			}
		}

	}

	private long findStarttime(long currentDate, String time) {
		long startTime = 0;
		if (time.matches("(\\d\\d\\d\\d-\\d\\d-\\d\\d)")) {
			String[] split = time.split("-");
			try {
				int year = Integer.parseInt(split[0]);
				int month = Integer.parseInt(split[1]);
				int day = Integer.parseInt(split[2]);
				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.YEAR, year - 1900);
				calendar.set(Calendar.MONTH, month - 1);
				calendar.set(Calendar.DAY_OF_MONTH, day);
				Date queryStartDate = calendar.getTime();
				startTime = queryStartDate.getTime();
			} catch (NumberFormatException e) {
				LOGGER.error("The Date is not in Format yyyy-mm-dd");
			}
		} else if (time.matches("(-\\d+(.))")) {
			long factor = 0;
			String clearedTime = time.replaceAll("\\s(.)+", "");
			char factorLetter = clearedTime.charAt(clearedTime.length() - 1);
			factor = getTimeFactor(factorLetter);
			long queryTime = currentDate + 1;
			String queryTimeString = time.replaceAll("\\D+", "");
			try {
				queryTime = Long.parseLong(queryTimeString);
			} catch (NumberFormatException e) {
				LOGGER.error("No valid time given");
			}
			startTime = currentDate - (queryTime * factor);
		}
		return startTime;
	}

	private long findEndtime(long currentDate, String time) {
		long endTime = 0;
		if (time.matches("(\\d\\d\\d\\d-\\d\\d-\\d\\d)")) {
			String[] split = time.split("-");
			try {
				int year = Integer.parseInt(split[0]);
				int month = Integer.parseInt(split[1]);
				int day = Integer.parseInt(split[2]);
				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.YEAR, year - 1900);
				calendar.set(Calendar.MONTH, month - 1);
				calendar.set(Calendar.DAY_OF_MONTH, day + 1);
				Date queryEndDate = calendar.getTime();
				endTime = queryEndDate.getTime();
			} catch (NumberFormatException e) {
				LOGGER.error("The Date is not in Format yyyy-mm-dd");
			}
		} else if (time.matches("-\\d+(.)+")) {
			long factor;
			String clearedTime = time.replaceAll("\\s(.)+", "");
			char factorLetter = clearedTime.charAt(clearedTime.length() - 1);
			factor = getTimeFactor(factorLetter);
			long queryTime = currentDate + 1;
			String queryTimeString = time.replaceAll("\\D+", "");
			try {
				queryTime = Long.parseLong(queryTimeString);
			} catch (NumberFormatException e) {
				LOGGER.error("No valid time given");
			}
			endTime = currentDate - (queryTime * factor);
		}
		return endTime;
	}

	private long getTimeFactor(char factorAsALetter) {
		long factor;
		switch (factorAsALetter) {
		case 'm':
			factor = 60000;
			break;
		case 'h':
			factor = 3600000;
			break;
		case 'd':
			factor = 86400000;
			break;
		case 'w':
			factor = 604800000;
			break;
		default:
			factor = 1;
			break;
		}
		return factor;
	}

	public void produceResultsFromQuery() {
		String filteredQuery = cropQuery();
		String finalQuery = "";
		List<Issue> resultingIssues = new ArrayList<>();
		if (queryIsFilter) {

			long filterId = 0;
			boolean filterIsNumberCoded = false;
			try {
				filterId = Long.parseLong(filteredQuery, 10);
				filterIsNumberCoded = true;
			} catch (NumberFormatException n) {
				// n.printStackTrace();
			}
			if (filterIsNumberCoded) {
				finalQuery = queryFromFilterId(filterId);
			} else {
				finalQuery = queryFromFilterString(filteredQuery);
			}
		} else if (queryIsJQL) {
			finalQuery = cropQuery();
		} else if (!queryIsJQL && !queryIsFilter) {
			finalQuery = "type = null";
		}
		if (this.mergeFilterQueryWithProjectKey) {
			finalQuery = "(" + finalQuery + ")AND( PROJECT=" + this.projectKey + ")";
		}
		final SearchService.ParseResult parseResult = getSearchService().parseQuery(this.user, finalQuery);

		if (parseResult.isValid())

		{
			List<Clause> clauses = parseResult.getQuery().getWhereClause().getClauses();
			if (!clauses.isEmpty()) {
				findDatesInQuery(clauses);
			} else {
				findDatesInQuery(finalQuery);
			}
			try {
				final SearchResults<Issue> results = getSearchService().search(this.user, parseResult.getQuery(),
						PagerFilter.getUnlimitedFilter());
				resultingIssues = JiraSearchServiceHelper.getJiraIssues(results);

			} catch (SearchException e) {
				e.printStackTrace();
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

	public List<DecisionKnowledgeElement> getAllElementsMatchingQuery() {
		List<DecisionKnowledgeElement> results = new ArrayList<>();
		results.addAll(this.getQueryResults());
		boolean isFilteredByTime = this.isQueryContainsCreationDate();
		long startTime = this.getStartDate();
		long endTime = this.getEndDate();
		SearchResults<Issue> projectIssues = getIssuesForThisProject(user);
		if (projectIssues != null) {
			for (Issue currentIssue : JiraSearchServiceHelper.getJiraIssues(projectIssues)) {
				List<DecisionKnowledgeElement> elements = JiraIssueTextPersistenceManager
						.getElementsForIssue(currentIssue.getId(), projectKey);
				for (DecisionKnowledgeElement currentElement : elements) {
					if (!results.contains(currentElement)) {
						if (isFilteredByTime) {
							if (startTime <= 0) {
								if (currentElement instanceof PartOfJiraIssueText
										&& ((PartOfJiraIssueText) currentElement).getCreated().getTime() < endTime) {
									results.add(currentElement);
								}
							} else if (endTime <= 0) {
								if (currentElement instanceof PartOfJiraIssueText
										&& ((PartOfJiraIssueText) currentElement).getCreated().getTime() > startTime) {
									results.add(currentElement);
								}
							} else {
								if (currentElement instanceof PartOfJiraIssueText
										&& (((PartOfJiraIssueText) currentElement).getCreated().getTime() < endTime)
										&& (((PartOfJiraIssueText) currentElement).getCreated()
												.getTime() > startTime)) {
									results.add(currentElement);
								}
							}
						} else {
							if (currentElement instanceof PartOfJiraIssueText) {
								results.add(currentElement);
							}
						}
					}
				}
			}
		}
		return results;
	}

	private SearchResults<Issue> getIssuesForThisProject(ApplicationUser user) {
		JqlClauseBuilder jqlClauseBuilder = JqlQueryBuilder.newClauseBuilder();
		com.atlassian.query.Query query = jqlClauseBuilder.project(projectKey).buildQuery();
		SearchResults<Issue> searchResult;
		try {
			searchResult = getSearchService().search(user, query, PagerFilter.getUnlimitedFilter());
		} catch (SearchException e) {
			return null;
		}
		return searchResult;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
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

	public boolean isQueryContainsCreationDate() {
		return queryContainsCreationDate;
	}

	public long getStartDate() {
		return startDate;
	}

	public long getEndDate() {
		return endDate;
	}

	public SearchService getSearchService() {
		if (this.searchService == null) {
			return searchService = ComponentAccessor.getComponentOfType(SearchService.class);
		}
		return this.searchService;
	}

	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}
}