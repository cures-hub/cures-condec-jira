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

	private Map<String, Integer> numberOfCommentsPerIssue;
	private Map<String, Integer> numberOfCommits;
	private Map<String, Integer> distributionOfKnowledgeTypes;
	private Map<String, String> reqAndClassSummary;
	private Map<String, String> elementsFromDifferentOrigins;
	private Map<String, Integer> numberOfRelevantComments;
	private Map<String, String> definitionOfDoneCheckResults;

	@JsonIgnore
	protected static final Logger LOGGER = LoggerFactory.getLogger(GeneralMetricCalculator.class);

	public GeneralMetricCalculator(ApplicationUser user, FilterSettings filterSettings) {
		FilteringManager filteringManager = new FilteringManager(filterSettings);
		this.filterSettings = filterSettings;
		this.graph = filteringManager.getFilteredGraph();
		this.knowledgeElements = graph.vertexSet();
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

	private Map<String, Integer> calculateNumberOfCommits() {
		if (!ConfigPersistenceManager.getGitConfiguration(filterSettings.getProjectKey()).isActivated()) {
			return new HashMap<>();
		}
		return commentMetricCalculator.getNumberOfCommitsPerIssue();
	}

	private Map<String, Integer> calculateDistributionOfKnowledgeTypes() {
		LOGGER.info("GeneralMetricsCalculator getDistributionOfKnowledgeTypes");
		Map<String, Integer> distributionMap = new HashMap<>();
		for (KnowledgeType type : KnowledgeType.getDefaultTypes()) {
			List<KnowledgeElement> elements = graph.getElements(type);
			distributionMap.put(type.toString(), elements.size());
		}
		return distributionMap;
	}

	private Map<String, String> calculateReqAndClassSummary() {
		LOGGER.info("RequirementsDashboard getReqAndClassSummary 3");
		Map<String, String> summaryMap = new HashMap<>();
		StringBuilder requirements = new StringBuilder();
		StringBuilder codeFiles = new StringBuilder();
		List<String> requirementsTypes = KnowledgeType.getRequirementsTypes();
		for (Issue issue : jiraIssues) {
			if (requirementsTypes.contains(issue.getIssueType().getName())) {
				KnowledgeElement knowledgeElement = new KnowledgeElement(issue);
				requirements.append(knowledgeElement.getKey()).append(" ");
			}
		}
		for (KnowledgeElement knowledgeElement : graph.getElements(KnowledgeType.CODE)) {
			codeFiles.append(filterSettings.getProjectKey()).append('-').append(knowledgeElement.getDescription()).append(" ");
		}
		summaryMap.put("Requirements", requirements.toString().trim());
		summaryMap.put("Code Files", codeFiles.toString().trim());
		return summaryMap;
	}

	private Map<String, String> calculateElementsFromDifferentOrigins() {
		LOGGER.info("GeneralMetricCalculator getElementsFromDifferentOrigins");
		Map<String, String> originMap = new HashMap<>();

		StringBuilder elementsInJiraIssues = new StringBuilder();
		StringBuilder elementsInJiraIssueText = new StringBuilder();
		StringBuilder elementsInCommitMessages = new StringBuilder();
		StringBuilder elementsInCodeComments = new StringBuilder();
		for (KnowledgeElement element : knowledgeElements) {
			if (element.getType() == KnowledgeType.CODE || element.getType() == KnowledgeType.OTHER) {
				continue;
			}
			if (element.getDocumentationLocation() == DocumentationLocation.JIRAISSUE) {
				elementsInJiraIssues.append(element.getKey()).append(" ");
				continue;
			}
			if (element.getDocumentationLocation() == DocumentationLocation.JIRAISSUETEXT) {
				if (element.getOrigin() == Origin.COMMIT) {
					elementsInCommitMessages.append(element.getKey()).append(" ");
				} else {
					elementsInJiraIssueText.append(element.getKey()).append(" ");
				}
			}
			if (element.getDocumentationLocation() == DocumentationLocation.CODE) {
				elementsInCodeComments.append(element.getKey()).append(" ");
			}
		}
		originMap.put("Jira Issue Description or Comment", elementsInJiraIssueText.toString().trim());
		originMap.put("Entire Jira Issue", elementsInJiraIssues.toString().trim());
		originMap.put("Commit Message", elementsInCommitMessages.toString().trim());
		originMap.put("Code Comment", elementsInCodeComments.toString().trim());

		return originMap;
	}

	private Map<String, Integer> calculateNumberOfRelevantComments() {
		return commentMetricCalculator.getNumberOfRelevantComments();
	}

	private Map<String, String> calculateDefinitionOfDoneCheckResults() {
		LOGGER.info("GeneralMetricCalculator calculateDefinitionOfDoneCheckResults");
		Map<String, String> resultMap = new HashMap<>();

		StringBuilder elementsWithDoDCheckSuccess = new StringBuilder();
		StringBuilder elementsWithDoDCheckFail = new StringBuilder();
		for (KnowledgeElement element : knowledgeElements) {
			if (DefinitionOfDoneChecker.checkDefinitionOfDone(element, filterSettings)) {
				elementsWithDoDCheckSuccess.append(element.getKey()).append(" ");
			} else {
				elementsWithDoDCheckFail.append(element.getKey()).append(" ");
			}
		}
		resultMap.put("Definition of Done Fulfilled", elementsWithDoDCheckSuccess.toString().trim());
		resultMap.put("Definition of Done Failed", elementsWithDoDCheckFail.toString().trim());

		return resultMap;
	}

	@JsonProperty("numberOfCommentsPerIssue")
	public Map<String, Integer> getNumberOfCommentsPerIssue() {
		return numberOfCommentsPerIssue;
	}

	@JsonProperty("numberOfCommits")
	public Map<String, Integer> getNumberOfCommits() {
		return numberOfCommits;
	}

	@JsonProperty("distributionOfKnowledgeTypes")
	public Map<String, Integer> getDistributionOfKnowledgeTypes() {
		return distributionOfKnowledgeTypes;
	}

	@JsonProperty("reqAndClassSummary")
	public Map<String, String> getReqAndClassSummary() {
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

	@JsonProperty("definitionOfDoneCheckResults")
	public Map<String, String> getDefinitionOfDoneCheckResults() {
		return definitionOfDoneCheckResults;
	}
}
