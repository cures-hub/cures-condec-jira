package de.uhd.ifi.se.decision.management.jira.metric;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.issue.Issue;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.filtering.FilteringManager;
import de.uhd.ifi.se.decision.management.jira.git.GitClient;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Origin;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.quality.DefinitionOfDone;
import de.uhd.ifi.se.decision.management.jira.quality.DefinitionOfDoneChecker;

/**
 * Calculates the following metrics on the {@link KnowledgeGraph} data structure
 * after it was filtered with the given {@link FilterSettings}:
 * <ul>
 * <li>Number of comments per Jira issue, see
 * {@link CharacterizedJiraIssue}</li>
 * <li>Number of commits per Jira issue, see {@link CharacterizedJiraIssue} and
 * {@link GitClient}</li>
 * <li>Number of code files and requirements in the project</li>
 * <li>Number of rationale elements per
 * {@link Origin}/{@link DocumentationLocation}</li>
 * <li>Number of comments with and without decision knowledge</li>
 * <li>Number of decision knowledge elements per decision knowledge type</li>
 * <li>Number of knowledge elements fulfilling and violating the
 * {@link DefinitionOfDone}</li>
 * </ul>
 * 
 * @issue How to model the results of the metric calculation?
 * @decision We use maps that have categories as keys and the elements that fall
 *           into the category as values to model the results of the metric
 *           calculation!
 * @pro Easy and similar representation for all metrics, similar treatment of
 *      metrics in UI.
 * @con Maps are not very "speaking". It is not clear what the categories are.
 * @alternative We could use custom classes to represent the metrics.
 * @con Needs individual treatment of metrics in the UI.
 */
public class GeneralMetricCalculator {

	private FilterSettings filterSettings;
	private List<Issue> jiraIssues;
	private KnowledgeGraph graph;
	private Set<KnowledgeElement> knowledgeElements;
	private CommentMetricCalculator commentMetricCalculator;

	protected static final Logger LOGGER = LoggerFactory.getLogger(GeneralMetricCalculator.class);

	public GeneralMetricCalculator(FilterSettings filterSettings) {
		FilteringManager filteringManager = new FilteringManager(filterSettings);
		this.filterSettings = filterSettings;
		this.graph = filteringManager.getFilteredGraph();
		this.knowledgeElements = graph.vertexSet();
		this.jiraIssues = KnowledgePersistenceManager.getInstance(filterSettings.getProjectKey()).getJiraIssueManager()
				.getAllJiraIssuesForProject();
		this.commentMetricCalculator = new CommentMetricCalculator(jiraIssues);
	}

	/**
	 * @return map with number of comments as keys and elements (Jira issues) that
	 *         have the respective number of comments as map values.
	 */
	@XmlElement
	public Map<Integer, List<KnowledgeElement>> getNumberOfCommentsMap() {
		return commentMetricCalculator.getNumberOfCommentsPerJiraIssueMap();
	}

	/**
	 * @return map with number of commits as keys and elements (Jira issues) that
	 *         have the respective number of commits linked as map values.
	 */
	@XmlElement
	public Map<Integer, List<KnowledgeElement>> getNumberOfCommitsMap() {
		if (!ConfigPersistenceManager.getGitConfiguration(filterSettings.getProjectKey()).isActivated()) {
			return new HashMap<>();
		}
		return commentMetricCalculator.getNumberOfCommitsPerJiraIssueMap();
	}

	/**
	 * @return map with decision knowledge types (e.g. issue, decision, alternative,
	 *         pro, con) as keys and respective decision knowledge elements as map
	 *         values.
	 */
	@XmlElement
	public Map<String, List<KnowledgeElement>> getNumberOfDecisionKnowledgeElements() {
		Map<String, List<KnowledgeElement>> distributionMap = new HashMap<>();
		for (KnowledgeElement element : knowledgeElements) {
			if (!element.getType().isDecisionKnowledge()) {
				continue;
			}
			String decisionKnowledgeTypeName = element.getType().toString();
			if (!distributionMap.containsKey(decisionKnowledgeTypeName)) {
				distributionMap.put(decisionKnowledgeTypeName, new ArrayList<>());
			} else {
				distributionMap.get(decisionKnowledgeTypeName).add(element);
			}
		}
		return distributionMap;
	}

	/**
	 * @return map with two keys "Requirements" and "Code Files" and respective
	 *         knowledge elements as map values.
	 */
	@XmlElement
	public Map<String, List<KnowledgeElement>> getRequirementsAndCodeFiles() {
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

	/**
	 * @return map with different {@link Origin}s as keys and respective knowledge
	 *         elements that are captured in the origin as map values.
	 */
	@XmlElement
	public Map<String, List<KnowledgeElement>> getElementsFromDifferentOrigins() {
		Map<String, List<KnowledgeElement>> originMap = new HashMap<>();

		List<KnowledgeElement> elementsInJiraIssues = new ArrayList<>();
		List<KnowledgeElement> elementsInJiraIssueText = new ArrayList<>();
		List<KnowledgeElement> elementsInCommitMessages = new ArrayList<>();
		List<KnowledgeElement> elementsInCodeComments = new ArrayList<>();
		for (KnowledgeElement element : knowledgeElements) {
			if (!element.getType().isDecisionKnowledge()) {
				continue;
			}
			if (element.getDocumentationLocation() == DocumentationLocation.JIRAISSUE) {
				elementsInJiraIssues.add(element);
			} else if (element.getDocumentationLocation() == DocumentationLocation.JIRAISSUETEXT) {
				if (element.getOrigin() == Origin.COMMIT) {
					elementsInCommitMessages.add(element);
				} else {
					elementsInJiraIssueText.add(element);
				}
			} else {
				// if (element.getDocumentationLocation() == DocumentationLocation.CODE)
				elementsInCodeComments.add(element);
			}
		}
		originMap.put("Jira Issue Description or Comment", elementsInJiraIssueText);
		originMap.put("Entire Jira Issue", elementsInJiraIssues);
		originMap.put("Commit Message", elementsInCommitMessages);
		originMap.put("Code Comment", elementsInCodeComments);

		return originMap;

	}

	/**
	 * @return map with two keys "Relevant Comments" and "Irrelevant Comments" and
	 *         the respective numbers as map values.
	 * @see CommentMetricCalculator#getNumberOfRelevantComments()
	 * @see CommentMetricCalculator#getNumberOfIrrelevantComments()
	 */
	@XmlElement
	public Map<String, Integer> getNumberOfRelevantAndIrrelevantComments() {
		Map<String, Integer> commentRelevanceMap = new LinkedHashMap<>();
		commentRelevanceMap.put("Relevant Comments", commentMetricCalculator.getNumberOfRelevantComments());
		commentRelevanceMap.put("Irrelevant Comments", commentMetricCalculator.getNumberOfIrrelevantComments());
		return commentRelevanceMap;
	}

	@XmlElement
	public Map<String, List<KnowledgeElement>> getDefinitionOfDoneCheckResults() {
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
}
