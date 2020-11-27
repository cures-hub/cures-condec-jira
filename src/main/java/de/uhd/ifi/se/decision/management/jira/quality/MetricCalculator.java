package de.uhd.ifi.se.decision.management.jira.quality;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jgit.revwalk.RevCommit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.extraction.GitClient;
import de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol.GitDecXtract;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssuePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.quality.commentmetrics.CommentMetricCalculator;

public class MetricCalculator {

	private List<Issue> jiraIssues;
	private KnowledgeGraph graph;
	private List<KnowledgeElement> decisionKnowledgeCodeElements;
	private List<KnowledgeElement> decisionKnowledgeCommitElements;
	private Map<String, List<KnowledgeElement>> extractedIssueRelatedElements;
	private FilterSettings filterSettings;
	private CommentMetricCalculator commentMetricCalculator;

	protected static final Logger LOGGER = LoggerFactory.getLogger(MetricCalculator.class);

	public MetricCalculator(ApplicationUser user, FilterSettings filterSettings) {
		this.filterSettings = filterSettings;
		this.graph = KnowledgeGraph.getOrCreate(filterSettings.getProjectKey());
		this.jiraIssues = JiraIssuePersistenceManager.getAllJiraIssuesForProject(user, filterSettings.getProjectKey());
		if (ConfigPersistenceManager.isKnowledgeExtractedFromGit(filterSettings.getProjectKey())) {
			extractedIssueRelatedElements = new HashMap<>();
			Map<String, List<KnowledgeElement>> elementMap = getDecisionKnowledgeElementsFromCode(
					filterSettings.getProjectKey());
			if (elementMap != null) {
				this.decisionKnowledgeCodeElements = elementMap.get("Code");
				this.decisionKnowledgeCommitElements = elementMap.get("Commit");
			} else {
				this.decisionKnowledgeCodeElements = null;
				this.decisionKnowledgeCommitElements = null;
			}
		}
		this.commentMetricCalculator = new CommentMetricCalculator(jiraIssues);
	}

	private Map<String, List<KnowledgeElement>> getDecisionKnowledgeElementsFromCode(String projectKey) {
		LOGGER.info("RequirementsDashboard getDecisionKnowledgeElementsFromCode 7");
		// Extracts Decision Knowledge from Code Comments AND Commits
		GitDecXtract gitExtract = new GitDecXtract(projectKey);

		Map<String, List<KnowledgeElement>> resultMap = new HashMap<String, List<KnowledgeElement>>();
		List<KnowledgeElement> allGatheredCommitElements = new ArrayList<>();
		List<KnowledgeElement> allGatheredCodeElements = new ArrayList<>();
		String filter = "(" + projectKey + "-)\\d+";
		Pattern filterPattern = Pattern.compile(filter, Pattern.CASE_INSENSITIVE);

		List<KnowledgeElement> gatheredCommitElements = new ArrayList<>();
		List<RevCommit> defaultfeatureCommits = GitClient.getOrCreate(filterSettings.getProjectKey())
				.getDefaultBranchCommits();
		if (defaultfeatureCommits == null || defaultfeatureCommits.size() == 0) {
			return resultMap;
		} else {
			for (RevCommit commit : defaultfeatureCommits) {
				List<KnowledgeElement> extractedCommitElements = gitExtract.getElementsFromMessage(commit);
				gatheredCommitElements.addAll(extractedCommitElements);
				if (extractedCommitElements != null && extractedCommitElements.size() > 0) {
					Matcher matcher = filterPattern.matcher(commit.getFullMessage());
					if (matcher.find()) {
						this.extractedIssueRelatedElements.put(matcher.group(), extractedCommitElements);
					}
				}
			}
			allGatheredCommitElements.addAll(gatheredCommitElements);
			RevCommit baseCommit = defaultfeatureCommits.get(defaultfeatureCommits.size() - 2);
			RevCommit lastFeatureBranchCommit = defaultfeatureCommits.get(0);
			// TODO default branch
			List<KnowledgeElement> extractedCodeElements = gitExtract.getElementsFromCode(baseCommit,
					lastFeatureBranchCommit,
					GitClient.getOrCreate(filterSettings.getProjectKey()).getBranches().get(0));
			allGatheredCommitElements.addAll(extractedCodeElements);
		}

		resultMap.put("Commit", allGatheredCommitElements);
		resultMap.put("Code", allGatheredCodeElements);
		return resultMap;

	}

	public Map<String, Integer> numberOfCommentsPerIssue() {
		return commentMetricCalculator.getNumberOfCommentsPerIssue();
	}

	public Map<String, Integer> getDistributionOfKnowledgeTypes() {
		LOGGER.info("RequirementsDashboard getDistributionOfKnowledgeTypes <1");
		Map<String, Integer> distributionOfKnowledgeTypes = new HashMap<String, Integer>();
		for (KnowledgeType type : KnowledgeType.getDefaultTypes()) {
			List<KnowledgeElement> elements = graph.getElements(type);
			distributionOfKnowledgeTypes.put(type.toString(), elements.size());
		}
		return distributionOfKnowledgeTypes;
	}

	public Map<String, Integer> getReqAndClassSummary() {
		LOGGER.info("RequirementsDashboard getReqAndClassSummary 3");
		Map<String, Integer> summaryMap = new HashMap<String, Integer>();
		int numberOfRequirements = 0;
		for (Issue issue : jiraIssues) {
			// Temporary Solution until Settings are available
			if (issue.getIssueType().getName().equals("System Function")
					|| issue.getIssueType().getName().equals("Nonfunctional Requirement")
					|| issue.getIssueType().getName().equals("Persona")
					|| issue.getIssueType().getName().equals("Usertask")
					|| issue.getIssueType().getName().equals("Subtask")
					|| issue.getIssueType().getName().equals("User Role")
					|| issue.getIssueType().getName().equals("Quality Requirement")
					|| issue.getIssueType().getName().equals("Story") || issue.getIssueType().getName().equals("Epic")
					|| issue.getIssueType().getName().equals("Workspace")) {
				numberOfRequirements++;
			}
		}
		summaryMap.put("Requirements", numberOfRequirements);
		summaryMap.put("Code Classes", graph.getElements(KnowledgeType.CODE).size());
		return summaryMap;
	}

	public Map<String, Integer> getKnowledgeSourceCount() {
		LOGGER.info("RequirementsDashboard getKnowledgeSourceCount <1");
		Map<String, Integer> sourceMap = new HashMap<String, Integer>();
		if (decisionKnowledgeCodeElements != null) {
			sourceMap.put("Code", decisionKnowledgeCodeElements.size());
		} else {
			sourceMap.put("Code", 0);
		}
		if (decisionKnowledgeCommitElements != null) {
			sourceMap.put("Commit", decisionKnowledgeCommitElements.size());
		} else {
			sourceMap.put("Commit", 0);
		}
		int numberIssues = 0;
		int numberIssueContent = 0;
		List<KnowledgeElement> elements = new ArrayList<KnowledgeElement>();
		for (KnowledgeType type : KnowledgeType.getDefaultTypes()) {
			elements.addAll(graph.getElements(type));
		}
		for (KnowledgeElement element : elements) {
			if (element.getDocumentationLocation() == DocumentationLocation.JIRAISSUE) {
				numberIssues++;
			} else if (element.getDocumentationLocation() == DocumentationLocation.JIRAISSUETEXT) {
				numberIssueContent++;
			}
		}
		sourceMap.put("Issue Content", numberIssueContent);
		sourceMap.put("Jira Issues", numberIssues);
		return sourceMap;
	}

	public Map<String, Integer> getNumberOfRelevantComments() {
		return commentMetricCalculator.getNumberOfRelevantComments();
	}

	public void setJiraIssues(List<Issue> issues) {
		jiraIssues = new ArrayList<>();
		for (Issue issue : issues) {
			jiraIssues.add(issue);
		}
	}
}
