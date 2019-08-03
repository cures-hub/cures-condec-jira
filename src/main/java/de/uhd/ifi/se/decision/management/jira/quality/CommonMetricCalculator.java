package de.uhd.ifi.se.decision.management.jira.quality;

import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchResults;
import com.atlassian.jira.jql.builder.JqlClauseBuilder;
import com.atlassian.jira.jql.builder.JqlQueryBuilder;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.query.Query;
import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.config.JiraIssueTypeGenerator;
import de.uhd.ifi.se.decision.management.jira.extraction.GitExtractor;
import de.uhd.ifi.se.decision.management.jira.filtering.JiraSearchServiceHelper;
import de.uhd.ifi.se.decision.management.jira.model.*;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.model.impl.GraphImpl;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.GenericLinkManager;
import de.uhd.ifi.se.decision.management.jira.persistence.JiraIssueTextPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.view.treant.Node;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class CommonMetricCalculator {

	private String projectKey;
	private ApplicationUser user;
	private JiraIssueTextPersistenceManager persistenceManager;
	private String jiraIssueTypeId;
	private List<Issue> jiraIssues;
	private int absolutDepth;
	private final String dataStringSeparator = " ";

	private static final Logger LOGGER = LoggerFactory.getLogger(CommonMetricCalculator.class);

	public CommonMetricCalculator(long projectId, ApplicationUser user, String jiraIssueTypeId) {
		this.projectKey = ComponentAccessor.getProjectManager().getProjectObj(projectId).getKey();
		this.user = user;
		this.persistenceManager = new JiraIssueTextPersistenceManager(projectKey);
		this.jiraIssueTypeId = jiraIssueTypeId;
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
		} catch (SearchException ex) {
			LOGGER.error(ex.getMessage());
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
		}
		return jiraIssues;
	}

	public Map<String, Integer> getNumberOfDecisionKnowledgeElementsForJiraIssues(KnowledgeType type) {
		if (type == null) {
			return new HashMap<String, Integer>();
		}
		Map<String, Integer> numberOfSentencesForJiraIssues = new HashMap<String, Integer>();
		for (Issue jiraIssue : jiraIssues) {
			int numberOfElements = 0;
			List<DecisionKnowledgeElement> elements = JiraIssueTextPersistenceManager
					.getElementsForIssue(jiraIssue.getId(), projectKey);
			for (DecisionKnowledgeElement element : elements) {
				if (element.getType().equals(type)) {
					numberOfElements++;
				}
			}
			numberOfSentencesForJiraIssues.put(jiraIssue.getKey(), numberOfElements);
		}
		return numberOfSentencesForJiraIssues;
	}

	public Map<String, Integer> getNumberOfCommitsForJiraIssues() {
		Map<String, Integer> resultMap = new HashMap<String, Integer>();
		try {
			GitExtractor gitExtractor = ComponentGetter.getGitExtractor(projectKey);
			if (gitExtractor!=null) {
				for (Issue jiraIssue : jiraIssues) {
					int numberOfCommits = gitExtractor.GetListOfCommitsForJiraIssue(jiraIssue).size();
					resultMap.put(jiraIssue.getKey(), numberOfCommits);
				}
			}
		}
		catch (Exception ex) {
			LOGGER.error(ex.getMessage());
		}
		return resultMap;
	}

	public Map<String, Integer> getDistributionOfKnowledgeTypes() {
		Map<String, Integer> distributionOfKnowledgeTypes = new HashMap<String, Integer>();
		for (KnowledgeType type : KnowledgeType.getDefaultTypes()) {
			distributionOfKnowledgeTypes.put(type.toString(),
					persistenceManager.getDecisionKnowledgeElements(type).size());
		}
		return distributionOfKnowledgeTypes;
	}

	/* TODO: check why is only tested, never used */
	public Map<String, Integer> _getNumberOfLinksToOtherElement(KnowledgeType linkFrom, KnowledgeType linkTo) {
		if (linkFrom == null || linkTo == null) {
			return new HashMap<String, Integer>();
		}
		Integer[] statistics = new Integer[4];
		Arrays.fill(statistics, 0);

		List<DecisionKnowledgeElement> listOfIssues = this.persistenceManager.getDecisionKnowledgeElements(linkFrom);

		for (DecisionKnowledgeElement issue : listOfIssues) {
			List<Link> links = GenericLinkManager.getLinksForElement(issue.getId(),
					DocumentationLocation.JIRAISSUETEXT);
			boolean hastOtherElementLinked = false;

			for (Link link : links) {
				if (link.isValid()) {
					DecisionKnowledgeElement dke = link.getOppositeElement(issue.getId());
					if (dke instanceof PartOfJiraIssueText && dke.getType().equals(linkTo)) { // alt
						hastOtherElementLinked = true;
					}
				}
			}
			if (hastOtherElementLinked) {
				statistics[0] = statistics[0] + 1;
			} else if (!hastOtherElementLinked) {
				statistics[1] = statistics[1] + 1;
			}
		}

		// Hashmaps as counter suck
		Map<String, Integer> dkeCount = new HashMap<String, Integer>();
		dkeCount.put(linkFrom.toString() + " has " + linkTo.toString(), statistics[0]);
		dkeCount.put(linkFrom.toString() + " has no " + linkTo.toString(), statistics[1]);

		return dkeCount;
	}

	/* TODO: add tests */
	public Map<String, String> getDecKnowlElementsOfATypeGroupedByHavingMoreThanOneElementsOfOtherType(
			KnowledgeType linkFrom, KnowledgeType linkTo) {

		String[] data = new String[2];
		Arrays.fill(data, "");

		List<DecisionKnowledgeElement> listOfIssues = this.persistenceManager.getDecisionKnowledgeElements(linkFrom);

		Map<String, Boolean> observedLinksMoreThanOnce = new HashMap<String, Boolean>();

		for (DecisionKnowledgeElement issue : listOfIssues) {
			String issueKey = issue.getKey();
			List<Link> links = GenericLinkManager.getLinksForElement(issue.getId(),
					DocumentationLocation.JIRAISSUETEXT);
			int numberObservedLinks = 0;

			for (Link link : links) {
				if (link.isValid()) {
					DecisionKnowledgeElement dke = link.getOppositeElement(issue.getId());
					if (dke instanceof PartOfJiraIssueText && dke.getType().equals(linkTo)) { // alt
						numberObservedLinks++;
					}
				}
			}
			if (numberObservedLinks>1) {
				data[0] += issueKey+dataStringSeparator;
			} else {
				data[1] += issueKey+dataStringSeparator;
			}
		}

		Map<String, String> havingManyLinksMap = new HashMap<String, String>();
		havingManyLinksMap.put(linkFrom.toString() + " has more than one " + linkTo.toString(), data[0].trim());
		havingManyLinksMap.put(linkFrom.toString() + " does not have more than one " + linkTo.toString(), data[1].trim());

		return havingManyLinksMap;
	}

	/* TODO: add tests */
	public Map<String, String> getDecKnowlElementsOfATypeGroupedByHavingElementsOfOtherType(
			KnowledgeType linkFrom, KnowledgeType linkTo) {
		if (linkFrom == null || linkTo == null || linkFrom == linkTo) {
			return new HashMap<String, String>();
		}
		String[] data = new String[2];
		Arrays.fill(data, "");

		List<DecisionKnowledgeElement> listOfIssues = this.persistenceManager.getDecisionKnowledgeElements(linkFrom);

		for (DecisionKnowledgeElement issue : listOfIssues) {
			List<Link> links = GenericLinkManager.getLinksForElement(issue.getId(),
					DocumentationLocation.JIRAISSUETEXT);
			boolean hastOtherElementLinked = false;

			for (Link link : links) {
				if (link.isValid()) {
					DecisionKnowledgeElement dke = link.getOppositeElement(issue.getId());
					if (dke instanceof PartOfJiraIssueText && dke.getType().equals(linkTo)) { // alt
						hastOtherElementLinked = true;
					}
				}
			}
			if (hastOtherElementLinked) {
				data[0] += issue.getKey()+dataStringSeparator;
			} else {
				data[1] += issue.getKey()+dataStringSeparator;
			}
		}

		Map<String, String> havingLinkMap = new HashMap<String, String>();
		havingLinkMap.put(linkFrom.toString() + " has " + linkTo.toString(), data[0].trim());
		havingLinkMap.put(linkFrom.toString() + " has no " + linkTo.toString(), data[1].trim());

		return havingLinkMap;
	}

	/* TODO: check why is only tested, never used
	 * using CON and PRO arguments calculations separately */
	public Map<String, String> getAlternativesHavingArguments() {
		String alternativesHaveArgument = "";
		String alternativesHaveNoArgument = "";

		List<DecisionKnowledgeElement> alternatives = this.persistenceManager
				.getDecisionKnowledgeElements(KnowledgeType.ALTERNATIVE);

		for (DecisionKnowledgeElement currentAlternative : alternatives) {
			List<Link> links = GenericLinkManager.getLinksForElement(currentAlternative.getId(),
					DocumentationLocation.JIRAISSUETEXT);
			boolean hasArgument = false;
			for (Link link : links) {
				if (link.isValid()) {
					DecisionKnowledgeElement dke = link.getOppositeElement(currentAlternative.getId());
					if (dke instanceof PartOfJiraIssueText && dke.getType().equals(KnowledgeType.ARGUMENT)) {
						hasArgument = true;
					}
				}
			}
			if (hasArgument) {
				alternativesHaveArgument+=currentAlternative.getKey()+dataStringSeparator;
			} else {
				alternativesHaveNoArgument+=currentAlternative.getKey()+dataStringSeparator;

			}
		}
		Map<String, String> havingArgument = new HashMap<String, String>();
		havingArgument.put("Alternative with Argument", alternativesHaveArgument);
		havingArgument.put("Alternative without Argument", alternativesHaveNoArgument);

		return havingArgument;
	}

	public Map<String, Integer> getLinkDistance(KnowledgeType type) {
		if (type == null) {
			return new HashMap<String, Integer>();
		}
		Map<String, Integer> linkDistances = new HashMap<String, Integer>();

		List<DecisionKnowledgeElement> listOfDecKnowElements = persistenceManager.getDecisionKnowledgeElements(type);
		Integer i = 0;
		for (DecisionKnowledgeElement currentElement : listOfDecKnowElements ) {
			int depth = graphRecursionBot(currentElement);
			i++;
			linkDistances.put(String.valueOf(i)+currentElement.getKey(),depth);
		}

		return linkDistances;
	}

	public Map<String, String> getLinksToIssueTypeMap(KnowledgeType knowledgeType) {
		if (knowledgeType == null) {
			return null;
		}
		Map<String, String> result = new HashMap<String, String>();
		String withLink = "";
		String withoutLink = "";
		for (Issue issue : jiraIssues) {
			boolean linkExisting = false;
			if (!checkEqualIssueTypeIssue(issue.getIssueType())) {
				//skipped+=issue.getKey()+dataStringSeparator;
				continue;
			}
			for (Link link : GenericLinkManager.getLinksForElement(issue.getId(), DocumentationLocation.JIRAISSUE)) {
				if (link.isValid()) {
					DecisionKnowledgeElement dke = link.getOppositeElement(new DecisionKnowledgeElementImpl(issue));
					if (dke.getType().equals(knowledgeType)) {
						linkExisting = true;
					}
				}
			}

			if (linkExisting) {
				withLink+=issue.getKey()+dataStringSeparator;
			} else {
				withoutLink+=issue.getKey()+dataStringSeparator;
			}
		}

		String jiraIssueTypeName = JiraIssueTypeGenerator.getJiraIssueTypeName(jiraIssueTypeId);

		result.put("Links from " + jiraIssueTypeName + " to " + knowledgeType.toString(), withLink);
		result.put("No links from " + jiraIssueTypeName + " to " + knowledgeType.toString(), withoutLink);
		//result.put("Skipped issues not of target type " + jiraIssueTypeName, skipped);
		return result;
	}

	private boolean checkEqualIssueTypeIssue(IssueType issueType2) {
		if (issueType2 == null) {
			return false;
		}

		String jiraIssueTypeName = JiraIssueTypeGenerator.getJiraIssueTypeName(jiraIssueTypeId);
		return issueType2.getName().equalsIgnoreCase(jiraIssueTypeName);
	}

	private int graphRecursionBot(DecisionKnowledgeElement dke) {
		this.absolutDepth = 0;
		Graph graph = new GraphImpl(projectKey, dke.getKey());
		this.createNodeStructure(dke, null, 100, 1, graph);
		return absolutDepth;
	}

	private Node createNodeStructure(DecisionKnowledgeElement element, Link link, int depth, int currentDepth,
			Graph graph) {
		if (element == null || element.getProject() == null || element.getType() == KnowledgeType.OTHER) {
			return new Node();
		}
		Map<DecisionKnowledgeElement, Link> childrenAndLinks = graph.getAdjacentElementsAndLinks(element);
		Node node;
		if (link != null) {
			node = new Node(element, link, false, false);
		} else {
			node = new Node(element, false, false);
		}
		List<Node> nodes = new ArrayList<Node>();
		for (Map.Entry<DecisionKnowledgeElement, Link> childAndLink : childrenAndLinks.entrySet()) {
			Node newChildNode = createNodeStructure(childAndLink.getKey(), childAndLink.getValue(), depth,
					currentDepth + 1, graph);
			if (absolutDepth < currentDepth) {
				absolutDepth = currentDepth;
			}
			nodes.add(newChildNode);
		}
		node.setChildren(nodes);
		return node;
	}

}
