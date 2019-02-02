package de.uhd.ifi.se.decision.management.jira.extraction.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;

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
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.Graph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.Sentence;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.model.impl.GraphImpl;
import de.uhd.ifi.se.decision.management.jira.oauth.OAuthManager;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.GenericLinkManager;
import de.uhd.ifi.se.decision.management.jira.persistence.JiraIssueCommentPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.view.treant.Node;

public class CommentMetricCalculator {

	private long projectId;
	private String projectKey;
	private ApplicationUser user;
	private JiraIssueCommentPersistenceManager persistenceManager;
	private String jiraIssueTypeToLinkTo;
	private final SearchResults searchResults;

	public static org.json.JSONObject restResponse;

	private SearchService searchService;

	public CommentMetricCalculator(long projectId, ApplicationUser user, String jiraIssueTypeToLinkTo) {
		this.projectId = projectId;
		this.projectKey = ComponentAccessor.getProjectManager().getProjectObj(projectId).getKey();
		this.user = user;
		this.persistenceManager = new JiraIssueCommentPersistenceManager(projectKey);
		this.searchService = ComponentAccessor.getComponentOfType(SearchService.class);
		this.jiraIssueTypeToLinkTo = jiraIssueTypeToLinkTo;
		this.searchResults = getIssuesForThisProject();
	}

	private SearchResults getIssuesForThisProject() {
		JqlClauseBuilder jqlClauseBuilder = JqlQueryBuilder.newClauseBuilder();
		Query query = jqlClauseBuilder.project(this.projectId).buildQuery();
		SearchResults searchResult = new SearchResults(null, 0, 0, 0);
		try {
			searchResult = searchService.search(user, query, PagerFilter.getUnlimitedFilter());
		} catch (SearchException e) {
		}
		return searchResult;
	}

	public Map<String, Integer> getNumberOfCommentsPerIssueMap() {
		Map<String, Integer> result = new HashMap<String, Integer>();
		if (searchResults == null || searchResults.getIssues().size() == 0) {
			return result;
		}
		for (Issue issue : searchResults.getIssues()) {
			int size = 0;
			try {
				size = ComponentAccessor.getCommentManager().getComments(issue).size();
				result.put(issue.getKey(), size);
			} catch (NullPointerException e) {// Issue does not exist
			}
		}
		return result;
	}

	public Map<String, Integer> getNumberOfSentencePerIssueMap(KnowledgeType type) {
		Map<String, Integer> result = new HashMap<String, Integer>();
		if (searchResults == null || searchResults.getIssues().size() == 0) {
			return result;
		}
		for (Issue currentIssue : searchResults.getIssues()) {
			int count = 0;
			List<DecisionKnowledgeElement> elements = JiraIssueCommentPersistenceManager
					.getElementsForIssue(currentIssue.getId(), projectKey);
			for (DecisionKnowledgeElement dke : elements) {
				if (dke.getType().equals(type)) {
					count++;
				}
			}
			result.put(currentIssue.getKey(), count);
		}
		return result;
	}

	public Map<String, Integer> getNumberOfRelevantSentences() {
		Map<String, Integer> result = new HashMap<String, Integer>();
		int isRelevant = 0;
		int isNotRelevant = 0;
		if (searchResults == null || searchResults.getIssues().size() == 0) {
			return result;
		}

		for (Issue currentIssue : searchResults.getIssues()) {
			List<DecisionKnowledgeElement> elements = JiraIssueCommentPersistenceManager
					.getElementsForIssue(currentIssue.getId(), projectKey);
			for (DecisionKnowledgeElement currentElement : elements) {
				if (currentElement instanceof Sentence && ((Sentence) currentElement).isRelevant()) {
					isRelevant++;
				} else if (currentElement instanceof Sentence && !((Sentence) currentElement).isRelevant()) {
					isNotRelevant++;
				}
			}
		}
		result.put("Relevant Sentences", isRelevant);
		result.put("Irrelevant Sentences", isNotRelevant);

		return result;
	}

	public Map<String, Integer> getNumberOfCommitsPerIssueMap() {
		Map<String, Integer> resultMap = new HashMap<String, Integer>();
		for (Issue issue : searchResults.getIssues()) {
			requestNumberOfGitCommits(issue.getKey());
			if (restResponse != null) {
				try {
					JSONArray result = (JSONArray) restResponse.get("commits");
					resultMap.put(issue.getKey(), result.length());
				} catch (Exception e) {
					resultMap.put(issue.getKey(), 0);
				}
			}
		}

		return resultMap;
	}

	public Map<String, Integer> getDecKnowElementsPerIssue() {
		Map<String, Integer> dkeCount = new HashMap<String, Integer>();

		for (KnowledgeType type : KnowledgeType.getDefaultTypes()) {
			dkeCount.put(type.toString(), this.persistenceManager.getDecisionKnowledgeElements(type).size());
		}
		return dkeCount;
	}

	public Map<String, Integer> getLinkToOtherElement(KnowledgeType linkFrom, KnowledgeType linkTo1) {
		Integer[] statistics = new Integer[4];
		Arrays.fill(statistics, 0);

		List<DecisionKnowledgeElement> listOfIssues = this.persistenceManager.getDecisionKnowledgeElements(linkFrom);

		for (DecisionKnowledgeElement issue : listOfIssues) {
			List<Link> links = GenericLinkManager.getLinksForElement(issue.getId(),
					DocumentationLocation.JIRAISSUECOMMENT);
			boolean hastOtherElementLinked = false;

			for (Link link : links) {
				if (link.isValid()) {
					DecisionKnowledgeElement dke = link.getOppositeElement(issue.getId());
					if (dke instanceof Sentence && dke.getType().equals(linkTo1)) { // alt
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
		dkeCount.put("Has " + linkTo1.toString(), statistics[0]);
		dkeCount.put("Has no " + linkTo1.toString(), statistics[1]);

		return dkeCount;
	}

	public String issuesWithNoExistingLinksToDecisionKnowledge(KnowledgeType linkFrom) {
		String listOfElementsWithoutLink = " ";
		List<DecisionKnowledgeElement> listOfIssues = this.persistenceManager.getDecisionKnowledgeElements(linkFrom);

		for (DecisionKnowledgeElement issue : listOfIssues) {
			boolean hastOtherElementLinked = false;

			if (!hastOtherElementLinked) {
				if (issue instanceof Sentence
						&& !listOfElementsWithoutLink.contains(((Sentence) issue).getKey().split(":")[0])) {
					listOfElementsWithoutLink += ((Sentence) issue).getKey().split(":")[0] + " ";
				}
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
					DocumentationLocation.JIRAISSUECOMMENT);
			boolean hasArgument = false;
			for (Link link : links) {
				if (link.isValid()) {
					DecisionKnowledgeElement dke = link.getOppositeElement(currentAlternative.getId());
					if (dke instanceof Sentence && dke.getType().equals(KnowledgeType.ARGUMENT)) {
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
		List<Integer> linkDistances = new ArrayList<Integer>();

		List<DecisionKnowledgeElement> listOfIssues = this.persistenceManager.getDecisionKnowledgeElements(type);

		for (DecisionKnowledgeElement currentAlternative : listOfIssues) {
			int depth = graphRecursionBot(currentAlternative);
			linkDistances.add(depth);
		}

		return linkDistances;
	}

	public Object getLinksToIssueTypeMap(KnowledgeType knowledgeType) {
		Map<String, Integer> result = new HashMap<>();
		int withLink = 0;
		int withoutLink = 0;
		for (Issue issue : searchResults.getIssues()) {
			boolean linkExisting = false;
			if (checkEqualIssueTypeIssue(issue.getIssueType())) {
				for (Link link : GenericLinkManager.getLinksForElement(issue.getId(),
						DocumentationLocation.JIRAISSUE)) {
					if (link.isValid()) {
						DecisionKnowledgeElement dke = link.getOppositeElement(new DecisionKnowledgeElementImpl(issue));
						if (dke.getType().equals(knowledgeType)) {
							linkExisting = true;
						}
					}
				}
			}
			if (linkExisting) {
				withLink++;
			} else {
				withoutLink++;
			}
		}
		result.put("Links from " + getPropperStringForBugAndTasksFromIssueType() + " " + knowledgeType.toString(),
				withLink);
		result.put("No links from " + getPropperStringForBugAndTasksFromIssueType() + " " + knowledgeType.toString(),
				withoutLink);
		return result;
	}

	public String getPropperStringForBugAndTasksFromIssueType() {
		System.out.println(JiraIssueTypeGenerator.getJiraIssueTypeName(jiraIssueTypeToLinkTo));
		return JiraIssueTypeGenerator.getJiraIssueTypeName(jiraIssueTypeToLinkTo);
	}

	private void requestNumberOfGitCommits(String issueKey) {
		if (issueKey == null) {
			return;
		}
		try {
			OAuthManager ar = new OAuthManager();
			String baseUrl = ConfigPersistenceManager.getOauthJiraHome();
			if (!baseUrl.endsWith("/")) {
				baseUrl = baseUrl + "/";
			}
			ar.startRequest(baseUrl + "rest/gitplugin/1.0/issues/" + issueKey + "/commits");
		} catch (Exception e) {

		}
	}

	private int graphRecursionBot(DecisionKnowledgeElement dke) {
		int absolutDepth = 0;
		Graph graph = new GraphImpl(projectKey, dke.getKey());
		this.createNodeStructure(dke, null, 100, 1, absolutDepth, graph);
		return absolutDepth;
	}

	private Node createNodeStructure(DecisionKnowledgeElement element, Link link, int depth, int absolutDepth,
			int currentDepth, Graph graph) {
		if (element == null || element.getProject() == null || element.getType() == KnowledgeType.OTHER) {
			return new Node();
		}
		Map<DecisionKnowledgeElement, Link> childrenAndLinks = graph.getLinkedElementsAndLinks(element);
		Node node;
		if (link != null) {
			node = new Node(element, link, false, false);
		} else {
			node = new Node(element, false, false);
		}
		List<Node> nodes = new ArrayList<Node>();
		for (Map.Entry<DecisionKnowledgeElement, Link> childAndLink : childrenAndLinks.entrySet()) {
			Node newChildNode = createNodeStructure(childAndLink.getKey(), childAndLink.getValue(), depth,
					currentDepth + 1, absolutDepth, graph);
			if (absolutDepth < currentDepth) {
				absolutDepth = currentDepth;
			}
			nodes.add(newChildNode);
		}
		node.setChildren(nodes);
		return node;
	}

	private boolean checkEqualIssueTypeIssue(IssueType issueType2) {
		if (issueType2 == null) {
			return false;
		}

		String jiraIssueTypeName = JiraIssueTypeGenerator.getJiraIssueTypeName(jiraIssueTypeToLinkTo);
		return issueType2.getName().equalsIgnoreCase(jiraIssueTypeName);
	}

}
