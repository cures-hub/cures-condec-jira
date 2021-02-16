package de.uhd.ifi.se.decision.management.jira.quality.generalmetrics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Origin;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssuePersistenceManager;

public class GeneralMetricCalculator {

	private List<Issue> jiraIssues;
	private KnowledgeGraph graph;
	private String projectKey;
	private CommentMetricCalculator commentMetricCalculator;

	protected static final Logger LOGGER = LoggerFactory.getLogger(GeneralMetricCalculator.class);

	public GeneralMetricCalculator(ApplicationUser user, String projectKey) {
		this.graph = KnowledgeGraph.getOrCreate(projectKey);
		this.projectKey = projectKey;
		this.jiraIssues = JiraIssuePersistenceManager.getAllJiraIssuesForProject(user, projectKey);
		this.commentMetricCalculator = new CommentMetricCalculator(jiraIssues);
	}

	public Map<String, Integer> numberOfCommentsPerIssue() {
		return commentMetricCalculator.getNumberOfCommentsPerIssue();
	}

	public Map<String, Integer> getDistributionOfKnowledgeTypes() {
		LOGGER.info("GeneralMetricsCalculator getDistributionOfKnowledgeTypes");
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
		List<String> requirementsTypes = KnowledgeType.getRequirementsTypes();
		for (Issue issue : jiraIssues) {
			if (requirementsTypes.contains(issue.getIssueType().getName())) {
				numberOfRequirements++;
			}
		}
		summaryMap.put("Requirements", numberOfRequirements);
		summaryMap.put("Code Files", graph.getElements(KnowledgeType.CODE).size());
		return summaryMap;
	}

	public Map<String, Integer> getNumberOfElementsPerDocumentationLocation() {
		LOGGER.info("GeneralMetricCalculator getElementsFromDifferentOrigins");
		Map<String, Integer> originMap = new HashMap<>();

		Integer elementsInJiraIssues = 0;
		Integer elementsInJiraIssueText = 0;
		Integer elementsInCommitMessages = 0;
		Integer elementsInCodeComments = 0;
		Set<KnowledgeElement> elements = graph.vertexSet();
		for (KnowledgeElement element : elements) {
			if (element.getType() == KnowledgeType.CODE || element.getType() == KnowledgeType.OTHER) {
				continue;
			}
			if (element.getDocumentationLocation() == DocumentationLocation.JIRAISSUE) {
				elementsInJiraIssues++;
				continue;
			}
			if (element.getDocumentationLocation() == DocumentationLocation.JIRAISSUETEXT) {
				if (element.getOrigin() == Origin.COMMIT) {
					elementsInCommitMessages++;
				} else {
					elementsInJiraIssueText++;
				}
			}
			if (element.getDocumentationLocation() == DocumentationLocation.CODE) {
				elementsInCodeComments++;
			}
		}
		originMap.put("Jira Issue Description or Comment", elementsInJiraIssueText);
		originMap.put("Entire Jira Issue", elementsInJiraIssues);
		originMap.put("Commit Message", elementsInCommitMessages);
		originMap.put("Code Comment", elementsInCodeComments);
		return originMap;
	}

	public Map<String, Integer> getNumberOfRelevantComments() {
		return commentMetricCalculator.getNumberOfRelevantComments();
	}

	public Map<String, Integer> getNumberOfCommits() {
		if (!ConfigPersistenceManager.isKnowledgeExtractedFromGit(projectKey)) {
			return new HashMap<>();
		}
		return commentMetricCalculator.getNumberOfCommitsPerIssue();
	}

	public void setJiraIssues(List<Issue> issues) {
		jiraIssues = new ArrayList<>();
		for (Issue issue : issues) {
			jiraIssues.add(issue);
		}
	}
}
