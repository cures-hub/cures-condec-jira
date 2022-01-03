package de.uhd.ifi.se.decision.management.jira.quality.generalmetrics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.issue.Issue;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.filtering.FilteringManager;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Origin;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.quality.completeness.DefinitionOfDoneChecker;

public class GeneralMetricCalculator {

	@JsonIgnore
	private FilterSettings filterSettings;
	@JsonIgnore
	private List<Issue> jiraIssues;
	@JsonIgnore
	private KnowledgeGraph graph;
	@JsonIgnore
	private Set<KnowledgeElement> knowledgeElements;
	@JsonIgnore
	private CommentMetricCalculator commentMetricCalculator;

	private Map<Integer, List<KnowledgeElement>> numberOfCommentsMap;
	private Map<Integer, List<KnowledgeElement>> numberOfCommitsMap;
	private Map<String, List<KnowledgeElement>> distributionOfKnowledgeTypes;
	private Map<String, List<KnowledgeElement>> reqAndClassSummary;
	private Map<String, List<KnowledgeElement>> elementsFromDifferentOrigins;
	private Map<String, Integer> numberOfRelevantComments;
	private Map<String, List<KnowledgeElement>> definitionOfDoneCheckResults;

	@JsonIgnore
	protected static final Logger LOGGER = LoggerFactory.getLogger(GeneralMetricCalculator.class);

	public GeneralMetricCalculator(FilterSettings filterSettings) {
		FilteringManager filteringManager = new FilteringManager(filterSettings);
		this.filterSettings = filterSettings;
		this.graph = filteringManager.getFilteredGraph();
		this.knowledgeElements = graph.vertexSet();
		this.jiraIssues = KnowledgePersistenceManager.getInstance(filterSettings.getProjectKey()).getJiraIssueManager()
				.getAllJiraIssuesForProject();
		this.commentMetricCalculator = new CommentMetricCalculator(jiraIssues);

		this.numberOfCommentsMap = commentMetricCalculator.getNumberOfCommentsPerIssue();
		this.distributionOfKnowledgeTypes = calculateDistributionOfKnowledgeTypes();
		this.reqAndClassSummary = calculateReqAndClassSummary();
		this.elementsFromDifferentOrigins = calculateElementsFromDifferentOrigins();
		this.numberOfRelevantComments = calculateNumberOfRelevantComments();
		this.numberOfCommitsMap = calculateNumberOfCommits();
		this.definitionOfDoneCheckResults = calculateDefinitionOfDoneCheckResults();
	}

	private Map<Integer, List<KnowledgeElement>> calculateNumberOfCommits() {
		if (!ConfigPersistenceManager.getGitConfiguration(filterSettings.getProjectKey()).isActivated()) {
			return new HashMap<>();
		}
		return commentMetricCalculator.getNumberOfCommitsPerIssue();
	}

	private Map<String, List<KnowledgeElement>> calculateDistributionOfKnowledgeTypes() {
		LOGGER.info("GeneralMetricsCalculator getDistributionOfKnowledgeTypes");
		Map<String, List<KnowledgeElement>> distributionMap = new HashMap<>();
		for (KnowledgeType type : KnowledgeType.getDefaultTypes()) {
			for (KnowledgeElement element : graph.getElements(type)) {
				if (!distributionMap.containsKey(type.toString())) {
					distributionMap.put(type.toString(), new ArrayList<>());
				} else {
					distributionMap.get(type.toString()).add(element);
				}
			}
		}
		return distributionMap;
	}

	private Map<String, List<KnowledgeElement>> calculateReqAndClassSummary() {
		LOGGER.info("GeneralMetricsCalculator getReqAndClassSummary");
		Map<String, List<KnowledgeElement>> summaryMap = new HashMap<>();
		List<KnowledgeElement> requirements = new ArrayList<>();
		List<String> requirementsTypes = KnowledgeType.getRequirementsTypes();
		for (Issue issue : jiraIssues) {
			if (requirementsTypes.contains(issue.getIssueType().getName())) {
				KnowledgeElement knowledgeElement = new KnowledgeElement(issue);
				requirements.add(knowledgeElement);
			}
		}
		summaryMap.put("Requirements", requirements);
		summaryMap.put("Code Files", graph.getElements(KnowledgeType.CODE));
		return summaryMap;
	}

	private Map<String, List<KnowledgeElement>> calculateElementsFromDifferentOrigins() {
		LOGGER.info("GeneralMetricCalculator getElementsFromDifferentOrigins");
		Map<String, List<KnowledgeElement>> originMap = new HashMap<>();

		List<KnowledgeElement> elementsInJiraIssues = new ArrayList<>();
		List<KnowledgeElement> elementsInJiraIssueText = new ArrayList<>();
		List<KnowledgeElement> elementsInCommitMessages = new ArrayList<>();
		List<KnowledgeElement> elementsInCodeComments = new ArrayList<>();
		for (KnowledgeElement element : knowledgeElements) {
			if (element.getType() == KnowledgeType.CODE || element.getType() == KnowledgeType.OTHER) {
				continue;
			}
			if (element.getDocumentationLocation() == DocumentationLocation.JIRAISSUE) {
				elementsInJiraIssues.add(element);
				continue;
			}
			if (element.getDocumentationLocation() == DocumentationLocation.JIRAISSUETEXT) {
				if (element.getOrigin() == Origin.COMMIT) {
					elementsInCommitMessages.add(element);
				} else {
					elementsInJiraIssueText.add(element);
				}
			}
			if (element.getDocumentationLocation() == DocumentationLocation.CODE) {
				elementsInCodeComments.add(element);
			}
		}
		originMap.put("Jira Issue Description or Comment", elementsInJiraIssueText);
		originMap.put("Entire Jira Issue", elementsInJiraIssues);
		originMap.put("Commit Message", elementsInCommitMessages);
		originMap.put("Code Comment", elementsInCodeComments);

		return originMap;
	}

	private Map<String, Integer> calculateNumberOfRelevantComments() {
		return commentMetricCalculator.getNumberOfRelevantComments();
	}

	private Map<String, List<KnowledgeElement>> calculateDefinitionOfDoneCheckResults() {
		LOGGER.info("GeneralMetricCalculator calculateDefinitionOfDoneCheckResults");
		Map<String, List<KnowledgeElement>> resultMap = new HashMap<>();

		List<KnowledgeElement> elementsWithDoDCheckSuccess = new ArrayList<>();
		List<KnowledgeElement> elementsWithDoDCheckFail = new ArrayList<>();
		for (KnowledgeElement element : knowledgeElements) {
			if (DefinitionOfDoneChecker.checkDefinitionOfDone(element, filterSettings)) {
				elementsWithDoDCheckSuccess.add(element);
			} else {
				elementsWithDoDCheckFail.add(element);
			}
		}
		resultMap.put("Definition of Done Fulfilled", elementsWithDoDCheckSuccess);
		resultMap.put("Definition of Done Violated", elementsWithDoDCheckFail);

		return resultMap;
	}

	@XmlElement
	public Map<Integer, List<KnowledgeElement>> getNumberOfCommentsMap() {
		return numberOfCommentsMap;
	}

	@XmlElement
	public Map<Integer, List<KnowledgeElement>> getNumberOfCommitsMap() {
		return numberOfCommitsMap;
	}

	@XmlElement
	public Map<String, List<KnowledgeElement>> getDistributionOfKnowledgeTypes() {
		return distributionOfKnowledgeTypes;
	}

	@XmlElement
	public Map<String, List<KnowledgeElement>> getReqAndClassSummary() {
		return reqAndClassSummary;
	}

	@XmlElement
	public Map<String, List<KnowledgeElement>> getElementsFromDifferentOrigins() {
		return elementsFromDifferentOrigins;
	}

	@XmlElement
	public Map<String, Integer> getNumberOfRelevantComments() {
		return numberOfRelevantComments;
	}

	@XmlElement
	public Map<String, List<KnowledgeElement>> getDefinitionOfDoneCheckResults() {
		return definitionOfDoneCheckResults;
	}
}
