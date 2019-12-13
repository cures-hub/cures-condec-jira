package de.uhd.ifi.se.decision.management.jira.quality;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.link.IssueLinkManager;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchResults;
import com.atlassian.jira.jql.builder.JqlClauseBuilder;
import com.atlassian.jira.jql.builder.JqlQueryBuilder;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.query.Query;

import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.extraction.GitClient;
import de.uhd.ifi.se.decision.management.jira.filtering.JiraSearchServiceHelper;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.impl.JiraIssueTextPersistenceManager;

public class MetricCalculator {
	
	private String projectKey;
	private ApplicationUser user;
	private List<Issue> jiraIssues;
	
	protected static final Logger LOGGER = LoggerFactory.getLogger(MetricCalculator.class);
	
	public MetricCalculator(Long projectId, ApplicationUser user){
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
	
    public Map<String,Object> addChart(String chartId, Map<String,Object> parameters, String chartName, Map<String, Integer> metricData) {
    	Map<String, String> chartNamesAndPurpose = new HashMap<String, String>();
		Map<String, Object> chartNamesAndData = new HashMap<String, Object>();
		chartNamesAndPurpose.put(chartId, "\\"+chartName);
		chartNamesAndData.put(chartId, metricData);
		parameters.put("chartNamesAndPurpose", chartNamesAndPurpose);
		parameters.put("chartNamesAndData", chartNamesAndData);
    	return parameters;
    }
    
    public Map<String, Integer> numberOfCommentsPerIssue(){
    	Map<String, Integer> numberMap = new HashMap<String,Integer>();
    	int numberOfComments;
    	for (Issue jiraIssue : jiraIssues) {
			try {
				numberOfComments = ComponentAccessor.getCommentManager().getComments(jiraIssue).size();
			} catch (NullPointerException e) {
				LOGGER.error("Getting number of comments for JIRA issues failed. Message: " + e.getMessage());
				numberOfComments = 0;
			}
			numberMap.put(jiraIssue.getKey(), numberOfComments);
		}
    	return numberMap;
    }
    
    public Map<String, Integer> numberOfCommitsPerIssue() {
		Map<String, Integer> resultMap = new HashMap<String, Integer>();
		try {
			GitClient gitClient = ComponentGetter.getGitClient(projectKey);
			if (gitClient != null) {
				for (Issue jiraIssue : jiraIssues) {
					int numberOfCommits = gitClient.getNumberOfCommits(jiraIssue);
					resultMap.put(jiraIssue.getKey(), numberOfCommits);
				}
			}
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
		}
		return resultMap;
	}
    //TODO: Extract getLinkDistanceIssues(linkDistance) 
    public Map<String, Integer> getNumberOfDecisionKnowledgeElementsForJiraIssues(KnowledgeType type, Integer linkDistance) {
		if (type == null) {
			return new HashMap<String, Integer>();
		}
		JiraIssueTextPersistenceManager persistenceManager = KnowledgePersistenceManager.getOrCreate(projectKey)
				.getJiraIssueTextManager();

		Map<String, Integer> numberOfSentencesPerIssue = new HashMap<String, Integer>();
		IssueLinkManager issueLinkManager = ComponentAccessor.getIssueLinkManager();
		for (Issue jiraIssue : jiraIssues) {
			int numberOfElements = 0;
			Map<Integer,List<Issue>> linkDistanceMap= new HashMap<Integer,List<Issue>>();
			List<Issue> linkissues = new ArrayList<Issue>();
			linkissues.add(jiraIssue);
			linkDistanceMap.put(0,linkissues);
			List<Issue> inMap = new ArrayList<Issue>();
			inMap.add(jiraIssue);
			for(int i=1;i <= linkDistance;i++) {
				linkissues.clear();
				for(Issue issue: linkDistanceMap.get(i-1)) {
					Collection<Issue> issueColl = issueLinkManager.getLinkCollection(issue, user).getAllIssues();
					linkissues.addAll(issueColl);
					linkissues.removeAll(inMap);
				}
				linkissues = new ArrayList<>(new HashSet<>(linkissues)); //Remove Duplicates
				linkDistanceMap.put(i, linkissues);
				inMap.addAll(linkissues);
				for(Issue issue :linkDistanceMap.get(i)) {
					List<DecisionKnowledgeElement> elements = persistenceManager.getElementsInJiraIssue(issue.getId());
					for (DecisionKnowledgeElement element : elements) {
						if (element.getType().equals(type)) {
							numberOfElements++;
						}
					}	
					if(i<linkDistance) {
						//Commit decisions
					}
				}
			}
			List<DecisionKnowledgeElement> elements = persistenceManager.getElementsInJiraIssue(jiraIssue.getId());
			for (DecisionKnowledgeElement element : elements) {
				if (element.getType().equals(type)) {
					numberOfElements++;
				}
			}	
			//Commit decisions
			numberOfSentencesPerIssue.put(jiraIssue.getKey(), numberOfElements);
		}
		return numberOfSentencesPerIssue;
	}
    
}

