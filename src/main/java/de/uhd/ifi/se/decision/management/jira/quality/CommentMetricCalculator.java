package de.uhd.ifi.se.decision.management.jira.quality;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchResults;
import com.atlassian.jira.jql.builder.JqlClauseBuilder;
import com.atlassian.jira.jql.builder.JqlQueryBuilder;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.query.Query;

import de.uhd.ifi.se.decision.management.jira.filtering.JiraSearchServiceHelper;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.impl.JiraIssueTextPersistenceManager;

public class CommentMetricCalculator {

	private String projectKey;
	private ApplicationUser user;
	private List<Issue> jiraIssues;

	protected static final Logger LOGGER = LoggerFactory.getLogger(CommentMetricCalculator.class);

	public CommentMetricCalculator(long projectId, ApplicationUser user) {
		this.projectKey = ComponentAccessor.getProjectManager().getProjectObj(projectId).getKey();
		this.user = user;
		this.jiraIssues = getJiraIssuesForProject(projectId);
	}

	private List<Issue> getJiraIssuesForProject(long projectId) {
		List<Issue> jiraIssues = new ArrayList<Issue>();
		JqlClauseBuilder jqlClauseBuilder = JqlQueryBuilder.newClauseBuilder();
		Query query = jqlClauseBuilder.project(projectId).buildQuery();
		SearchResults<Issue> searchResults = null;
		SearchService searchService = ComponentAccessor.getComponentOfType(SearchService.class);
		try {
			searchResults = searchService.search(user, query, PagerFilter.getUnlimitedFilter());
			jiraIssues = JiraSearchServiceHelper.getJiraIssues(searchResults);
		} catch (SearchException e) {
			LOGGER.error("Getting JIRA issues for project failed. Message: " + e.getMessage());
		}
		return jiraIssues;
	}

	public Map<String, Integer> getNumberOfCommentsForJiraIssues() {
		Map<String, Integer> numberOfCommentsForJiraIssues = new HashMap<String, Integer>();
		int numberOfComments;
		for (Issue jiraIssue : jiraIssues) {
			try {
				numberOfComments = ComponentAccessor.getCommentManager().getComments(jiraIssue).size();
			} catch (NullPointerException e) {
				LOGGER.error("Getting number of comments for JIRA issues failed. Message: " + e.getMessage());
				numberOfComments = 0;
			}
			numberOfCommentsForJiraIssues.put(jiraIssue.getKey(), numberOfComments);
		}
		return numberOfCommentsForJiraIssues;
	}

	public Map<String, Integer> getNumberOfRelevantSentences() {
		Map<String, Integer> numberOfRelevantSentences = new HashMap<String, Integer>();
		int isRelevant = 0;
		int isIrrelevant = 0;

		JiraIssueTextPersistenceManager persistenceManager = KnowledgePersistenceManager.getOrCreate(projectKey)
				.getJiraIssueTextManager();
		for (Issue jiraIssue : jiraIssues) {
			List<KnowledgeElement> elements = persistenceManager.getElementsInJiraIssue(jiraIssue.getId());
			for (KnowledgeElement currentElement : elements) {
				if (currentElement instanceof PartOfJiraIssueText
						&& ((PartOfJiraIssueText) currentElement).isRelevant()) {
					isRelevant++;
				} else if (currentElement instanceof PartOfJiraIssueText
						&& !((PartOfJiraIssueText) currentElement).isRelevant()) {
					isIrrelevant++;
				}
			}
		}
		numberOfRelevantSentences.put("Relevant Sentences", isRelevant);
		numberOfRelevantSentences.put("Irrelevant Sentences", isIrrelevant);

		return numberOfRelevantSentences;
	}

}
