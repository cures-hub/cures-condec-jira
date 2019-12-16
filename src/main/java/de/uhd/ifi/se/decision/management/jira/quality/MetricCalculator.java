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
import de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol.GitDecXtract;
import de.uhd.ifi.se.decision.management.jira.filtering.JiraSearchServiceHelper;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.impl.JiraIssueTextPersistenceManager;

//TODO: Allow User to choose Branch
//TODO: Allow User to choose which Elements make up Requirements
//TODO: Number of Code-Classes
//TODO: Which Metrics react to linkDistanceFilter
//TODO: Put getLinkDistanceIssueMap into an extractor class
public class MetricCalculator {

    private String projectKey;
    private ApplicationUser user;
    private List<Issue> jiraIssues;
    private JiraIssueTextPersistenceManager persistenceManager;
    private List<DecisionKnowledgeElement> decisionKnowledgeCodeElements;

    protected static final Logger LOGGER = LoggerFactory.getLogger(ChartCreator.class);

    public MetricCalculator(Long projectId, ApplicationUser user) {
	this.projectKey = ComponentAccessor.getProjectManager().getProjectObj(projectId).getKey();
	this.user = user;
	this.persistenceManager = KnowledgePersistenceManager.getOrCreate(projectKey).getJiraIssueTextManager();
	this.jiraIssues = getJiraIssuesForProject(projectId);
	this.decisionKnowledgeCodeElements = getDecisionKnowledgeElementsFromCode(projectKey);
    }

    public Map<Integer, List<Issue>> getLinkDistanceIssueMap(Integer linkDistance, Issue jiraIssue) {
	Map<Integer, List<Issue>> linkDistanceMap = new HashMap<Integer, List<Issue>>();
	IssueLinkManager issueLinkManager = ComponentAccessor.getIssueLinkManager();
	List<Issue> linkissues = new ArrayList<Issue>();
	linkissues.add(jiraIssue);
	linkDistanceMap.put(0, linkissues);
	List<Issue> inMap = new ArrayList<Issue>();
	inMap.add(jiraIssue);
	for (int i = 1; i <= linkDistance; i++) {
	    linkissues = new ArrayList<Issue>();
	    for (Issue issue : linkDistanceMap.get(i - 1)) {
		Collection<Issue> issueColl = issueLinkManager.getLinkCollection(issue, user).getAllIssues();
		linkissues.addAll(issueColl);
		linkissues.removeAll(inMap);
	    }
	    List<Issue> linkissueswithoutduplicate = new ArrayList<>(new HashSet<>(linkissues)); // Remove Duplicates
	    linkDistanceMap.put(i, linkissueswithoutduplicate);
	    inMap.addAll(linkissueswithoutduplicate);
	}
	return linkDistanceMap;
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

    private List<DecisionKnowledgeElement> getDecisionKnowledgeElementsFromCode(String projectKey) {
	GitDecXtract gitExtract = new GitDecXtract(projectKey);
	List<DecisionKnowledgeElement> elements = gitExtract.getElements("master");
	return elements;
    }

    public Map<String, Integer> numberOfCommentsPerIssue() {
	Map<String, Integer> numberMap = new HashMap<String, Integer>();
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

    public Map<String, Integer> getNumberOfDecisionKnowledgeElementsForJiraIssues(KnowledgeType type,
	    Integer linkDistance) {
	if (type == null) {
	    return new HashMap<String, Integer>();
	}
	Map<String, Integer> numberOfSentencesPerIssue = new HashMap<String, Integer>();
	for (Issue jiraIssue : jiraIssues) {
	    int numberOfElements = 0;
	    Map<Integer, List<Issue>> linkDistanceMap = getLinkDistanceIssueMap(linkDistance, jiraIssue);
	    for (int i = 0; i <= linkDistance; i++) {
		for (Issue issue : linkDistanceMap.get(i)) {
		    List<DecisionKnowledgeElement> elements = persistenceManager.getElementsInJiraIssue(issue.getId());
		    if (issue.getIssueType().getName().equals(type.toString())) {
			numberOfElements++;
		    }
		    // Extract Code Decisions
		    elements.addAll(decisionKnowledgeCodeElements);
		    for (DecisionKnowledgeElement element : elements) {
			if (element.getType().equals(type)) {
			    numberOfElements++;
			}
		    }

		}
	    }
	    numberOfSentencesPerIssue.put(jiraIssue.getKey(), numberOfElements);
	}
	return numberOfSentencesPerIssue;
    }

    public Map<String, Integer> getDistributionOfKnowledgeTypes() {
	Map<String, Integer> distributionOfKnowledgeTypes = new HashMap<String, Integer>();
	for (KnowledgeType type : KnowledgeType.getDefaultTypes()) {
	    int numberOfElements = persistenceManager.getDecisionKnowledgeElements(type).size();
	    for (DecisionKnowledgeElement element : decisionKnowledgeCodeElements) {
		if (element.getType().equals(type)) {
		    numberOfElements++;
		}
	    }
	    for (Issue issue : jiraIssues) {
		if (issue.getIssueType().getName().equals(type.toString())) {
		    numberOfElements++;
		}
	    }
	    distributionOfKnowledgeTypes.put(type.toString(), numberOfElements);
	}
	return distributionOfKnowledgeTypes;
    }

    public Map<String, Integer> getReqAndClassSummary() {
	Map<String, Integer> summaryMap = new HashMap<String, Integer>();
	int numberOfRequirements = 0;
	for (Issue issue : jiraIssues) { // Temporary Solution until Settings are available
	    if (issue.getIssueType().toString().equals("System Function")
		    || issue.getIssueType().toString().equals("Nonfunctional Requirement")
		    || issue.getIssueType().toString().equals("Persona")
		    || issue.getIssueType().toString().equals("Usertask")
		    || issue.getIssueType().toString().equals("Subtask")
		    || issue.getIssueType().toString().equals("Workspace")) {
		numberOfRequirements++;
	    }
	}
	summaryMap.put("Requirements", numberOfRequirements);

	return summaryMap;
    }

}
