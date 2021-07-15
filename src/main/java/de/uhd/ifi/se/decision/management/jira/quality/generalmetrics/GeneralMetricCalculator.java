package de.uhd.ifi.se.decision.management.jira.quality.generalmetrics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.filtering.FilteringManager;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Origin;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;

public class GeneralMetricCalculator {

	@JsonIgnore
	private FilterSettings filterSettings;
	@JsonIgnore
	private List<Issue> jiraIssues;
	@JsonIgnore
	private KnowledgeGraph graph;
	@JsonIgnore
	private CommentMetricCalculator commentMetricCalculator;

	private Map<String, Integer> numberOfCommentsPerIssue;
	private Map<String, Integer> distributionOfKnowledgeTypes;
	private Map<String, Integer> reqAndClassSummary;
	private Map<String, String> elementsFromDifferentOrigins;
	private Map<String, Integer> numberOfRelevantComments;
	private Map<String, Integer> numberOfCommits;
	private Map<String, String> definitionOfDoneCheckResults;

	@JsonIgnore
	protected static final Logger LOGGER = LoggerFactory.getLogger(GeneralMetricCalculator.class);

	public GeneralMetricCalculator(ApplicationUser user, FilterSettings filterSettings) {
		FilteringManager filteringManager = new FilteringManager(filterSettings);
		this.graph = filteringManager.getFilteredGraph();
		this.filterSettings = filterSettings;
		this.jiraIssues = KnowledgePersistenceManager.getOrCreate(filterSettings.getProjectKey()).getJiraIssueManager()
				.getAllJiraIssuesForProject();
		this.commentMetricCalculator = new CommentMetricCalculator(jiraIssues);

		this.numberOfCommentsPerIssue = calculateNumberOfCommentsPerIssue();
		this.distributionOfKnowledgeTypes = calculateDistributionOfKnowledgeTypes();
		this.reqAndClassSummary = calculateReqAndClassSummary();
		this.elementsFromDifferentOrigins = calculateElementsFromDifferentOrigins();
		this.numberOfRelevantComments = calculateNumberOfRelevantComments();
		this.numberOfCommits = calculateNumberOfCommits();
		this.definitionOfDoneCheckResults = calculateDefinitionOfDoneCheckResults();
	}

	private Map<String, Integer> calculateNumberOfCommentsPerIssue() {
		return commentMetricCalculator.getNumberOfCommentsPerIssue();
	}

	private Map<String, Integer> calculateDistributionOfKnowledgeTypes() {
		LOGGER.info("GeneralMetricsCalculator getDistributionOfKnowledgeTypes");
		Map<String, Integer> distributionMap = new HashMap<String, Integer>();
		for (KnowledgeType type : KnowledgeType.getDefaultTypes()) {
			List<KnowledgeElement> elements = graph.getElements(type);
			distributionMap.put(type.toString(), elements.size());
		}
		return distributionMap;
	}

	private Map<String, Integer> calculateReqAndClassSummary() {
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

	private Map<String, String> calculateElementsFromDifferentOrigins() {
		LOGGER.info("GeneralMetricCalculator getElementsFromDifferentOrigins");
		Map<String, String> originMap = new HashMap<>();

		String elementsInJiraIssues = "";
		String elementsInJiraIssueText = "";
		String elementsInCommitMessages = "";
		String elementsInCodeComments = "";
		Set<KnowledgeElement> elements = graph.vertexSet();
		for (KnowledgeElement element : elements) {
			if (element.getType() == KnowledgeType.CODE || element.getType() == KnowledgeType.OTHER) {
				continue;
			}
			if (element.getDocumentationLocation() == DocumentationLocation.JIRAISSUE) {
				elementsInJiraIssues += element.getKey() + " ";
				continue;
			}
			if (element.getDocumentationLocation() == DocumentationLocation.JIRAISSUETEXT) {
				if (element.getOrigin() == Origin.COMMIT) {
					elementsInCommitMessages += element.getKey() + " ";
				} else {
					elementsInJiraIssueText += element.getKey() + " ";
				}
			}
			if (element.getDocumentationLocation() == DocumentationLocation.CODE) {
				elementsInCodeComments += element.getKey() + " ";
			}
		}
		originMap.put("Jira Issue Description or Comment", elementsInJiraIssueText.trim());
		originMap.put("Entire Jira Issue", elementsInJiraIssues.trim());
		originMap.put("Commit Message", elementsInCommitMessages.trim());
		originMap.put("Code Comment", elementsInCodeComments.trim());

		return originMap;
	}

	private Map<String, Integer> calculateNumberOfRelevantComments() {
		return commentMetricCalculator.getNumberOfRelevantComments();
	}

	private Map<String, Integer> calculateNumberOfCommits() {
		if (!ConfigPersistenceManager.getGitConfiguration(filterSettings.getProjectKey()).isActivated()) {
			return new HashMap<>();
		}
		return commentMetricCalculator.getNumberOfCommitsPerIssue();
	}

	private Map<String, String> calculateDefinitionOfDoneCheckResults() {
		LOGGER.info("GeneralMetricCalculator calculateDefinitionOfDoneCheckResults");
		Map<String, String> resultMap = new HashMap<>();

		String elementsWithDoDCheckSuccess = "";
		String elementsWithDoDCheckFail = "";
		Set<KnowledgeElement> elements = graph.vertexSet();
		for (KnowledgeElement element : elements) {
			if (element.failsDefinitionOfDone(filterSettings)) {
				elementsWithDoDCheckFail += element.getKey() + " ";
			} else {
				elementsWithDoDCheckSuccess += element.getKey() + " ";
			}
		}
		resultMap.put("Definition of Done Fulfilled", elementsWithDoDCheckSuccess.trim());
		resultMap.put("Definition of Done Failed", elementsWithDoDCheckFail.trim());

		return resultMap;
	}

	@JsonProperty("numberOfCommentsPerIssue")
	public Map<String, Integer> getNumberOfCommentsPerIssue() {
		return numberOfCommentsPerIssue;
	}

	@JsonProperty("distributionOfKnowledgeTypes")
	public Map<String, Integer> getDistributionOfKnowledgeTypes() {
		return distributionOfKnowledgeTypes;
	}

	@JsonProperty("reqAndClassSummary")
	public Map<String, Integer> getReqAndClassSummary() {
		return reqAndClassSummary;
	}

	@JsonProperty("elementsFromDifferentOrigins")
	public Map<String, String> getElementsFromDifferentOrigins() {
		return elementsFromDifferentOrigins;
	}

	@JsonProperty("numberOfRelevantComments")
	public Map<String, Integer> getNumberOfRelevantComments() {
		return numberOfRelevantComments;
	}

	@JsonProperty("numberOfCommits")
	public Map<String, Integer> getNumberOfCommits() {
		return numberOfCommits;
	}

	@JsonProperty("definitionOfDoneCheckResults")
	public Map<String, String> getDefinitionOfDoneCheckResults() {
		return definitionOfDoneCheckResults;
	}
}
