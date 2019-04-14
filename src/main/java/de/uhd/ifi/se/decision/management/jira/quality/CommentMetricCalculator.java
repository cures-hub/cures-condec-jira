package de.uhd.ifi.se.decision.management.jira.quality;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import de.uhd.ifi.se.decision.management.jira.config.JiraIssueTypeGenerator;
import de.uhd.ifi.se.decision.management.jira.extraction.GitClient;
import de.uhd.ifi.se.decision.management.jira.extraction.impl.GitClientImpl;
import de.uhd.ifi.se.decision.management.jira.filtering.JiraSearchServiceHelper;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.Graph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.model.impl.GraphImpl;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.GenericLinkManager;
import de.uhd.ifi.se.decision.management.jira.persistence.JiraIssueTextPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.view.treant.Node;

public class CommentMetricCalculator {

	private String projectKey;
	private ApplicationUser user;
	private JiraIssueTextPersistenceManager persistenceManager;
	private String jiraIssueTypeId;
	private List<Issue> jiraIssues;
	private int absolutDepth;
	private GitClient gitClient;

	public CommentMetricCalculator(long projectId, ApplicationUser user, String jiraIssueTypeId) {
		this.projectKey = ComponentAccessor.getProjectManager().getProjectObj(projectId).getKey();
		this.user = user;
		this.persistenceManager = new JiraIssueTextPersistenceManager(projectKey);
		this.jiraIssueTypeId = jiraIssueTypeId;
		this.jiraIssues = getJiraIssuesForProject(projectId);
		this.gitClient = new GitClientImpl(projectKey);
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
				// Jira issue does not exist
				numberOfComments = 0;
			}
			numberOfCommentsForJiraIssues.put(jiraIssue.getKey(), numberOfComments);
		}
		return numberOfCommentsForJiraIssues;
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

	public Map<String, Integer> getNumberOfRelevantSentences() {
		Map<String, Integer> numberOfRelevantSentences = new HashMap<String, Integer>();
		int isRelevant = 0;
		int isIrrelevant = 0;

		for (Issue jiraIssue : jiraIssues) {
			List<DecisionKnowledgeElement> elements = JiraIssueTextPersistenceManager
					.getElementsForIssue(jiraIssue.getId(), projectKey);
			for (DecisionKnowledgeElement currentElement : elements) {
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

	public Map<String, Integer> getNumberOfCommitsForJiraIssues() {
		Map<String, Integer> resultMap = new HashMap<String, Integer>();
		for (Issue jiraIssue : jiraIssues) {
			int numberOfCommits = gitClient.getNumberOfCommits(jiraIssue.getKey());
			resultMap.put(jiraIssue.getKey(), numberOfCommits);
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

	public Map<String, Integer> getNumberOfLinksToOtherElement(KnowledgeType linkFrom, KnowledgeType linkTo) {
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

	public String issuesWithNoExistingLinksToDecisionKnowledge(KnowledgeType linkFrom) {
		if (linkFrom == null) {
			return "";
		}
		String listOfElementsWithoutLink = " ";
		List<DecisionKnowledgeElement> listOfIssues = this.persistenceManager.getDecisionKnowledgeElements(linkFrom);

		for (DecisionKnowledgeElement issue : listOfIssues) {
			if (issue instanceof PartOfJiraIssueText
					&& !listOfElementsWithoutLink.contains(((PartOfJiraIssueText) issue).getKey().split(":")[0])) {
				listOfElementsWithoutLink += ((PartOfJiraIssueText) issue).getKey().split(":")[0] + " ";
			}
		}
		return listOfElementsWithoutLink;
	}

	public Map<String, Integer> getAlternativeArguments() {
		int alternativesHaveArgument = 0;
		int alternativesHaveNoArgument = 0;

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
				alternativesHaveArgument++;
			} else {
				alternativesHaveNoArgument++;

			}
		}
		Map<String, Integer> dkeCount = new HashMap<String, Integer>();
		dkeCount.put("Alternative with Argument", alternativesHaveArgument);
		dkeCount.put("Alternative without Argument", alternativesHaveNoArgument);

		return dkeCount;
	}

	public List<Integer> getLinkDistance(KnowledgeType type) {
		if (type == null) {
			return new ArrayList<Integer>();
		}
		List<Integer> linkDistances = new ArrayList<Integer>();

		List<DecisionKnowledgeElement> listOfIssues = persistenceManager.getDecisionKnowledgeElements(type);

		for (DecisionKnowledgeElement currentAlternative : listOfIssues) {
			int depth = graphRecursionBot(currentAlternative);
			linkDistances.add(depth);
		}

		return linkDistances;
	}

	public Object getLinksToIssueTypeMap(KnowledgeType knowledgeType) {
		if (knowledgeType == null) {
			return null;
		}
		Map<String, Integer> result = new HashMap<String, Integer>();
		int withLink = 0;
		int withoutLink = 0;
		for (Issue issue : jiraIssues) {
			boolean linkExisting = false;
			if (!checkEqualIssueTypeIssue(issue.getIssueType())) {
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
				withLink++;
			} else {
				withoutLink++;
			}
		}

		String jiraIssueTypeName = JiraIssueTypeGenerator.getJiraIssueTypeName(jiraIssueTypeId);

		result.put("Links from " + jiraIssueTypeName + " to " + knowledgeType.toString(), withLink);
		result.put("No links from " + jiraIssueTypeName + " to " + knowledgeType.toString(), withoutLink);
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
