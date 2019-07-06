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
import com.atlassian.jira.bc.issue.search.SearchService.ParseResult;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.query.clause.Clause;

import de.uhd.ifi.se.decision.management.jira.model.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.impl.FilterSettingsImpl;

public class QueryHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(QueryHandler.class);

	private FilterSettings filterSettings;
	private boolean queryIsJQL;
	private boolean queryIsPresetJiraFilter;
	private boolean mergeFilterQueryWithProjectKey;
	private SearchService searchService;
	private ApplicationUser user;
	private boolean queryContainsCreationDate;
	private boolean queryContainsIssueTypes;
	private String finalQuery;

	public QueryHandler(ApplicationUser user, String projectKey, Boolean mergeFilterQueryWithProjectKey) {
		this.mergeFilterQueryWithProjectKey = mergeFilterQueryWithProjectKey;
		this.searchService = ComponentAccessor.getComponentOfType(SearchService.class);
		this.user = user;
		this.queryContainsCreationDate = false;
		this.queryContainsIssueTypes = false;
		this.finalQuery = "";
		this.filterSettings = new FilterSettingsImpl(projectKey, "", -1, -1);
	}

	private void jqlStringParser() {
		String jql = this.filterSettings.getSearchString();
		jql = jql.replaceAll("%20", " ");
		jql = jql.replaceAll("%3D", "=");
		jql = jql.replaceAll("%2C", ",");
		this.filterSettings.setSearchString(jql);
	}

	public ParseResult processParseResult(String query) {
		this.filterSettings.setSearchString(query);
		String filteredQuery = cropQuery();
		if (queryIsPresetJiraFilter) {
			long filterId = 0;
			boolean filterIsNumberCoded = false;
			try {
				filterId = Long.parseLong(filteredQuery, 10);
				filterIsNumberCoded = true;
			} catch (NumberFormatException n) {
				LOGGER.error("Produce results from query failed. Message: " + n.getMessage());
			}
			if (filterIsNumberCoded) {
				finalQuery = JiraFilter.getQueryFromFilterId(filterId, this.filterSettings.getProjectKey());
			} else {
				finalQuery = JiraFilter.getQueryFromFilterName(filteredQuery);
			}
		} else if (queryIsJQL) {
			this.filterSettings.setSearchString(cropQuery());
			jqlStringParser();
			return searchService.parseQuery(this.user, this.filterSettings.getSearchString());
		} else {
			finalQuery = "type = null";
		}
		if (this.mergeFilterQueryWithProjectKey) {
			finalQuery = "(" + finalQuery + ")AND( PROJECT=" + this.filterSettings.getProjectKey() + ")";
		}
		return searchService.parseQuery(this.user, finalQuery);
	}

	private String cropQuery() {
		String croppedQuery = "";
		String[] split = this.filterSettings.getSearchString().split("§");
		if (split.length > 1) {
			this.filterSettings.setSearchString("?" + split[1]);
		}
		if (this.filterSettings.getSearchString().indexOf("jql") == 1) {
			this.queryIsJQL = true;
		} else if (this.filterSettings.getSearchString().indexOf("filter") == 1) {
			this.queryIsPresetJiraFilter = true;
		}
		if (this.queryIsJQL) {
			croppedQuery = this.filterSettings.getSearchString().substring(5,
					this.filterSettings.getSearchString().length());
		} else if (this.queryIsPresetJiraFilter) {
			croppedQuery = this.filterSettings.getSearchString().substring(8,
					this.filterSettings.getSearchString().length());
		}
		return croppedQuery;
	}

	public void findDatesInQuery(List<Clause> clauses) {
		for (Clause clause : clauses) {
			if (!clause.getName().equals("created")) {
				continue;
			}
			this.queryContainsCreationDate = true;
			long todaysDate = new Date().getTime();
			String time = clause.toString().substring(13, clause.toString().length() - 2);
			if (clause.toString().contains(" <= ")) {
				this.filterSettings.setCreatedLatest(findEndTime(todaysDate, time));
			} else if (clause.toString().contains(" >= ")) {
				this.filterSettings.setCreatedEarliest(findStartTime(todaysDate, time));
			}
		}
	}

	public void findDatesInQuery(String query) {
		if (!query.contains("created")) {
			return;
		}
		this.queryContainsCreationDate = true;
		long todaysDate = new Date().getTime();
		String time = query.substring(11, query.length());
		if (query.contains(" <= ")) {
			this.filterSettings.setCreatedLatest(findEndTime(todaysDate, time));
		} else if (query.contains(" >= ")) {
			this.filterSettings.setCreatedEarliest(findStartTime(todaysDate, time));
		}
	}

	public void findIssueTypesInQuery(List<Clause> clauses) {
		for (Clause clause : clauses) {
			if (!clause.getName().equals("issuetype")) {
				continue;
			}
			this.queryContainsIssueTypes = true;
			String issuetypes = clause.toString().substring(14, clause.toString().length() - 2);
			List<KnowledgeType> types = new ArrayList<>();
			if (clause.toString().contains("=")) {
				types.add(KnowledgeType.getKnowledgeType(issuetypes.trim()));
			} else {
				String issueTypesCleared = issuetypes.replaceAll("[()]", "").replaceAll("\"", "");
				String[] split = issueTypesCleared.split(",");
				for (String issueType : split) {
					types.add(KnowledgeType.getKnowledgeType(issueType.trim()));
				}
			}
			this.filterSettings.setIssueTypes(types);
		}
	}

	public void findIssueTypesInQuery(String query) {
		if (!query.contains("issuetype")) {
			return;
		}
		this.queryContainsIssueTypes = true;
		List<KnowledgeType> types = new ArrayList<>();
		if (query.contains("=")) {
			String[] split = query.split("=");
			types.add(KnowledgeType.getKnowledgeType(split[1].trim()));
		} else {
			String issueTypeSeparated = query.substring(12, query.length());
			String issueTypeCleared = issueTypeSeparated.replaceAll("[()]", "").replaceAll("\"", "");
			String[] split = issueTypeCleared.split(",");
			for (String issueType : split) {
				String cleandIssueType = issueType.replaceAll("[()]", "");
				types.add(KnowledgeType.getKnowledgeType(cleandIssueType.trim()));
			}
		}
		this.filterSettings.setIssueTypes(types);
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

	public boolean isQueryContainsIssueTypes() {
		return queryContainsIssueTypes;
	}

	public boolean isQueryContainsCreationDate() {
		return queryContainsCreationDate;
	}

	public String getFinalQuery() {
		if (finalQuery == null) {
			LOGGER.error("Filter query is null or empty");
			return "";
		}
		return this.finalQuery;
	}

	public FilterSettings getFilterSettings() {
		return this.filterSettings;
	}
}
