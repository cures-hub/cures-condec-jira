package de.uhd.ifi.se.decision.management.jira.filtering;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.search.SearchRequest;
import com.atlassian.jira.issue.search.SearchRequestManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.query.clause.Clause;

public class QueryHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(QueryHandler.class);

	private String query;
	private boolean queryIsJQL;
	private boolean queryIsFilter;
	private String projectKey;
	private Boolean mergeFilterQueryWithProjectKey;
	private SearchService searchService;
	private ApplicationUser user;
	private long startDate;
	private long endDate;
	private boolean queryContainsCreationDate;
	private boolean queryContainsIssueTypes;
	private String finalQuery;
	private List<String> issueTypesInQuery;

	public QueryHandler(ApplicationUser user, String projectKey, Boolean mergeFilterQueryWithProjectKey) {
		this.projectKey = projectKey;
		this.mergeFilterQueryWithProjectKey = mergeFilterQueryWithProjectKey;
		this.searchService = ComponentAccessor.getComponentOfType(SearchService.class);
		this.user = user;
		this.startDate = -1;
		this.endDate = -1;
		this.queryContainsCreationDate = false;
		this.queryContainsIssueTypes = false;
		this.issueTypesInQuery = new ArrayList<>();
		this.finalQuery = "";
	}

	public SearchService.ParseResult processParsResult() {
		String filteredQuery = cropQuery();
		if (queryIsFilter) {
			long filterId = 0;
			boolean filterIsNumberCoded = false;
			try {
				filterId = Long.parseLong(filteredQuery, 10);
				filterIsNumberCoded = true;
			} catch (NumberFormatException n) {
				LOGGER.error("Produce results from query failed. Message: " + n.getMessage());
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
		return searchService.parseQuery(this.user, finalQuery);
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
		case "recentlyviewfinalQueryed": // Viewed recently
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

	public void findDatesInQuery(List<Clause> clauses) {
		for (Clause clause : clauses) {
			if (clause.getName().equals("created")) {
				this.queryContainsCreationDate = true;
				long todaysDate = new Date().getTime();
				String time = clause.toString().substring(13, clause.toString().length() - 2);
				if (clause.toString().contains(" <= ")) {
					this.endDate = findEndTime(todaysDate, time);
				} else if (clause.toString().contains(" >= ")) {
					this.startDate = findStartTime(todaysDate, time);
				}
			}
		}
	}

	public void findDatesInQuery(String query) {
		if (query.contains("created")) {
			this.queryContainsCreationDate = true;
			long todaysDate = new Date().getTime();
			String time = query.substring(11, query.length());
			if (query.contains(" <= ")) {
				this.endDate = findEndTime(todaysDate, time);
			} else if (query.contains(" >= ")) {
				this.startDate = findStartTime(todaysDate, time);
			}
		}

	}

	public void findIssueTypesInQuery(List<Clause> clauses) {
		for (Clause clause : clauses) {
			if (clause.getName().equals("issuetype")) {
				this.queryContainsIssueTypes = true;
				String issuetypes = clause.toString().substring(14, clause.toString().length() - 2);
				if (clause.toString().contains("=")) {
					this.issueTypesInQuery.add(issuetypes.trim());
				} else {
					String issueTypesCleared = issuetypes.replaceAll("[()]", "").replaceAll("\"", "");
					String[] split = issueTypesCleared.split(",");
					for (String issueType : split) {
						this.issueTypesInQuery.add(issueType.trim());
					}
				}
			}
		}
	}

	public void findIssueTypesInQuery(String query) {
		if (query.contains("issuetype")) {
			this.queryContainsIssueTypes = true;
			if (query.contains("=")) {
				String[] split = query.split("=");
				this.issueTypesInQuery.add(split[1].trim());
			} else {
				String issueTypeSeparated = query.substring(12, query.length());
				String issueTypeCleared = issueTypeSeparated.replaceAll("[()]", "").replaceAll("\"", "");
				String[] split = issueTypeCleared.split(",");
				for (String issueType : split) {
					String cleandIssueType = issueType.replaceAll("[()]", "");
					this.issueTypesInQuery.add(cleandIssueType.trim());
				}
			}
		}
	}

	private long findStartTime(long currentDate, String time) {
		long startTime = 0;
		if (time.matches("(\\d\\d\\d\\d-\\d\\d-\\d\\d)")) {
			startTime = getDateFromYearMonthDateFormat(time);
		} else if (time.matches("(-\\d+(.))")) {
			startTime = getTimeFromNumberAndFactorLetter(currentDate, time);
		}
		return startTime;
	}

	private long findEndTime(long currentDate, String time) {
		long endTime = 0;
		if (time.matches("(\\d\\d\\d\\d-\\d\\d-\\d\\d)")) {
			endTime = getDateFromYearMonthDateFormat(time);
		} else if (time.matches("-\\d+(.)+")) {
			endTime = getTimeFromNumberAndFactorLetter(currentDate, time);
		}
		return endTime;
	}

	private long getDateFromYearMonthDateFormat(String dateAsString) {
		long dateAsLong = -1;
		try {
			DateFormat simple = new SimpleDateFormat("yyyy-MM-dd");
			Date date = simple.parse(dateAsString);
			dateAsLong = date.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return dateAsLong;
	}

	private long getTimeFromNumberAndFactorLetter(long currentDate, String dateAsNumberAndLetter) {
		long factor;
		String clearedTime = dateAsNumberAndLetter.replaceAll("\\s(.)+", "");
		char factorLetter = clearedTime.charAt(clearedTime.length() - 1);
		factor = getTimeFactor(factorLetter);
		long queryTime = currentDate + 1;
		String queryTimeString = dateAsNumberAndLetter.replaceAll("\\D+", "");
		try {
			queryTime = Long.parseLong(queryTimeString);
		} catch (NumberFormatException e) {
			LOGGER.error("No valid time given");
		}
		long result = currentDate - (queryTime * factor);
		return result;
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

	public SearchService getSearchService() {
		return this.searchService;
	}

	public long getStartDate() {
		return startDate;
	}

	public long getEndDate() {
		return endDate;
	}

	public boolean isQueryContainsIssueTypes() {
		return queryContainsIssueTypes;
	}

	public boolean isQueryContainsCreationDate() {
		return queryContainsCreationDate;
	}

	public String getFinalQuery() {
		if (finalQuery.equals("") || finalQuery == null) {
			LOGGER.error("Filter query is null or empty");
			return "";
		}
		return this.finalQuery;
	}

	public List<String> getIssueTypesInQuery() {
		return issueTypesInQuery;
	}
}
