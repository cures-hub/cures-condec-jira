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
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchResults;
import com.atlassian.jira.jql.builder.JqlClauseBuilder;
import com.atlassian.jira.jql.builder.JqlQueryBuilder;
import com.atlassian.jira.plugin.report.impl.AbstractReport;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.util.ParameterUtils;
import com.atlassian.jira.web.action.ProjectActionSupport;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.plugin.spring.scanner.annotation.imports.JiraImport;

import de.uhd.ifi.se.decision.management.jira.extraction.model.Sentence;
import de.uhd.ifi.se.decision.management.jira.extraction.persistence.ActiveObjectsManager;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.Graph;
import de.uhd.ifi.se.decision.management.jira.model.GraphImpl;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.oauth.OAuthManager;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistence;
import de.uhd.ifi.se.decision.management.jira.persistence.GenericLinkManager;
import de.uhd.ifi.se.decision.management.jira.view.treant.Node;

public class DecisionKnowledgeReport extends AbstractReport {

	@JiraImport
	private final ProjectManager projectManager;

	private Long projectId;

	private SearchService searchService;

	private int absolutDepth;

	public static org.json.JSONObject restResponse;

	public DecisionKnowledgeReport(ProjectManager projectManager) {
		this.projectManager = projectManager;
	}

	@SuppressWarnings("rawtypes")
	public String generateReportHtml(ProjectActionSupport action, Map params) throws Exception {
		Map<String, Object> velocityParams = createValues(action);
		return descriptor.getHtml("view", velocityParams);
	}

	public Map<String, Object> createValues(ProjectActionSupport action) {
		Map<String, Object> velocityParams = new HashMap<>();
		velocityParams.put("projectName", action.getProjectManager().getProjectObj(this.projectId).getName());

		List<Integer> numCommentsPerIssue = getNumberOfCommentsPerIssue(action.getLoggedInUser());
		velocityParams.put("numCommentsPerIssue", numCommentsPerIssue);

		// get Number of Sentence per Issue
		List<Integer> numSentencePerIssue = getNumberOfSentencePerIssue(action.getLoggedInUser());
		velocityParams.put("numSentencePerIssue", numSentencePerIssue);

		// get Number of relevant Sentences per Issue
		Map<String, Integer> numRelevantSentences = getNumberOfRelevantSentences(action.getLoggedInUser());
		velocityParams.put("numRelevantSentences", numRelevantSentences);
		velocityParams.put("map", Map.class); // TODO: what was this for? It is not used in vm.

		// get Number of commits per Issue
		List<Integer> numCommitsPerIssue = getNumberOfCommitsPerIssue(action.getLoggedInUser());
		velocityParams.put("numCommitsPerIssue", numCommitsPerIssue);

		// Get associated Knowledge Types in Sentences per Issue
		Map<String, Integer> numKnowledgeTypesPerIssue = getDecKnowElementsPerIssue();
		velocityParams.put("numKnowledgeTypesPerIssue", numKnowledgeTypesPerIssue);

		// Get types of decisions and alternatives linkes to Issue (e.g. has decision
		// but no alternative)
		velocityParams.put("numLinksToIssue", getLinkToOtherElement(KnowledgeType.ISSUE, KnowledgeType.DECISION));
		velocityParams.put("numLinksToDecision", getLinkToOtherElement(KnowledgeType.DECISION, KnowledgeType.ISSUE));

		// Get Number of Alternatives With Arguments
		Map<String, Integer> numAlternativeWoArgument = getAlternativeArguments();
		velocityParams.put("numAlternativeWoArgument", numAlternativeWoArgument);

		// Get Link Distance
		velocityParams.put("numLinkDistanceAlternative", getLinkDistance(KnowledgeType.ALTERNATIVE));
		velocityParams.put("numLinkDistanceIssue", getLinkDistance(KnowledgeType.ISSUE));
		velocityParams.put("numLinkDistanceDecision", getLinkDistance(KnowledgeType.DECISION));

		return velocityParams;
	}

	private Map<String, Integer> getNumberOfRelevantSentences(ApplicationUser loggedInUser) {
		Map<String, Integer> result = new HashMap<>();
		int isRelevant = 0;
		int isNotRelevant = 0;
		SearchResults projectIssues = getIssuesForThisProject(loggedInUser);
		if (projectIssues == null || projectIssues.getIssues().size() == 0) {
			return result;
		}

		String projectKey = ComponentAccessor.getProjectManager().getProjectObj(this.projectId).getKey();
		for (Issue currentIssue : projectIssues.getIssues()) {
			List<DecisionKnowledgeElement> elements = ActiveObjectsManager.getElementsForIssue(currentIssue.getId(),
					projectKey);
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

	private List<Integer> getNumberOfSentencePerIssue(ApplicationUser loggedInUser) {
		List<Integer> result = new ArrayList<>();

		SearchResults projectIssues = getIssuesForThisProject(loggedInUser);
		if (projectIssues == null || projectIssues.getIssues().size() == 0) {
			return result;
		}
		String projectKey = ComponentAccessor.getProjectManager().getProjectObj(this.projectId).getKey();
		for (Issue currentIssue : projectIssues.getIssues()) {
			List<DecisionKnowledgeElement> elements = ActiveObjectsManager.getElementsForIssue(currentIssue.getId(),
					projectKey);
			if (elements.size() > 0) {
				result.add(elements.size());
			}
		}
		return result;
	}

	private List<Integer> getLinkDistance(KnowledgeType type) {
		List<Integer> linkDistances = new ArrayList<>();

		List<DecisionKnowledgeElement> listOfIssues = ActiveObjectsManager
				.getAllElementsFromAoByType(projectManager.getProjectObj(this.projectId).getKey(), type);

		for (DecisionKnowledgeElement currentAlternative : listOfIssues) {
			// Treant treant = new Treant(currentAlternative.getProject().getProjectKey(),
			// currentAlternative.getKey(),
			// 100);
			int depth = graphRecursionBot(currentAlternative);
			linkDistances.add(depth);
		}

		return linkDistances;
	}

	private Map<String, Integer> getAlternativeArguments() {
		int alternativesHaveArgument = 0;
		int alternativesHaveNoArgument = 0;

		List<DecisionKnowledgeElement> listOfIssues = ActiveObjectsManager.getAllElementsFromAoByType(
				projectManager.getProjectObj(this.projectId).getKey(), KnowledgeType.ALTERNATIVE);

		for (DecisionKnowledgeElement currentAlternative : listOfIssues) {
			List<Link> links = GenericLinkManager.getLinksForElement("s" + currentAlternative.getId());
			boolean hasArgument = false;
			for (Link link : links) {
				DecisionKnowledgeElement dke = link.getOppositeElement("s" + currentAlternative.getId());
				if (dke instanceof Sentence && ((Sentence) dke).getArgument().equalsIgnoreCase("Pro")) {
					hasArgument = true;
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

	private Map<String, Integer> getLinkToOtherElement(KnowledgeType linkFrom, KnowledgeType linkTo1) {
		Integer[] statistics = new Integer[4];
		Arrays.fill(statistics, 0);
		List<DecisionKnowledgeElement> listOfIssues = ActiveObjectsManager
				.getAllElementsFromAoByType(projectManager.getProjectObj(this.projectId).getKey(), linkFrom);

		for (DecisionKnowledgeElement issue : listOfIssues) {
			List<Link> links = GenericLinkManager.getLinksForElement("s" + issue.getId());
			boolean hastOtherElementLinked = false;

			for (Link link : links) {
				DecisionKnowledgeElement dke = link.getOppositeElement("s" + issue.getId());
				if (dke instanceof Sentence && dke.getType().equals(linkTo1)) { // alt
					hastOtherElementLinked = true;
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

	private Map<String, Integer> getDecKnowElementsPerIssue() {
		Map<String, Integer> dkeCount = new HashMap<String, Integer>();

		for (KnowledgeType type : KnowledgeType.getDefaulTypes()) {
			String projectKey = projectManager.getProjectObj(this.projectId).getKey();
			dkeCount.put(type.toString(), ActiveObjectsManager.getAllElementsFromAoByType(projectKey, type).size());
		}
		return dkeCount;
	}

	private List<Integer> getNumberOfCommitsPerIssue(ApplicationUser loggedInUser) {
		List<Integer> commentList = new ArrayList<>();

		SearchResults issues = getIssuesForThisProject(loggedInUser);
		for (Issue issue : issues.getIssues()) {
			requestNumberOfGitCommits(issue.getKey());
			if (restResponse != null) {
				try {
					JSONArray result = (JSONArray) restResponse.get("commits");
					commentList.add(result.length());
				} catch (Exception e) {
					commentList.add(0);
				}
			}
		}

		return commentList;
	}

	private List<Integer> getNumberOfCommentsPerIssue(ApplicationUser user) {
		List<Integer> commentList = new ArrayList<>();
		SearchResults searchResults = getIssuesForThisProject(user);
		if (searchResults == null || searchResults.getIssues().size() == 0) {
			return commentList;
		}
		for (Issue issue : searchResults.getIssues()) {
			int size = 0;
			try {
				size = ComponentAccessor.getCommentManager().getComments(issue).size();
			} catch (NullPointerException e) {// Issue does not exist
				commentList.add(size);
			}
			commentList.add(size);
		}
		return commentList;
	}

	private SearchResults getIssuesForThisProject(ApplicationUser user) {
		JqlClauseBuilder jqlClauseBuilder = JqlQueryBuilder.newClauseBuilder();
		com.atlassian.query.Query query = jqlClauseBuilder.project(this.projectId).buildQuery();
		SearchResults searchResult;
		try {
			searchResult = getSearchService().search(user, query, PagerFilter.getUnlimitedFilter());
		} catch (SearchException e) {
			return null;
		}
		return searchResult;
	}

	public SearchService getSearchService() {
		if (this.searchService == null) {
			return searchService = ComponentAccessor.getComponentOfType(SearchService.class);
		}
		return this.searchService;
	}

	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}

	/**
	 * Seems to be uncalled, but is called by atlassian during execution to transfer
	 * velocity variables.
	 */
	@SuppressWarnings("rawtypes")
	public void validate(ProjectActionSupport action, Map params) {
		this.projectId = ParameterUtils.getLongParam(params, "selectedProjectId");
	}

	private int graphRecursionBot(DecisionKnowledgeElement dke) {
		this.absolutDepth = 0;
		Graph graph = new GraphImpl(projectManager.getProjectObj(this.projectId).getKey(), dke.getKey());
		this.createNodeStructure(dke, null, 100, 1, graph);
		return absolutDepth;
	}

	private Node createNodeStructure(DecisionKnowledgeElement element, Link link, int depth, int currentDepth,
			Graph graph) {
		if (element == null || element.getProject().getProjectKey() == null
				|| element.getType() == KnowledgeType.OTHER) {
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
			if ((childAndLink.getKey() instanceof Sentence && ((Sentence) childAndLink.getKey()).isRelevant())
					|| (childAndLink.getKey() instanceof DecisionKnowledgeElement)) {
				Node newChildNode = createNodeStructure(childAndLink.getKey(), childAndLink.getValue(), depth,
						currentDepth + 1, graph);
				if (this.absolutDepth < currentDepth) {
					this.absolutDepth = currentDepth;
				}
				nodes.add(newChildNode);
			}
		}
		node.setChildren(nodes);
		return node;
	}

	private void requestNumberOfGitCommits(String issueKey) {
		if (issueKey == null) {
			return;
		}
		try {
			OAuthManager ar = new OAuthManager();
			String baseUrl = ConfigPersistence.getOauthJiraHome();
			if (!baseUrl.endsWith("/")) {
				baseUrl = baseUrl + "/";
			}
			ar.startRequest(baseUrl + "rest/gitplugin/1.0/issues/" + issueKey + "/commits");
		} catch (Exception e) {

		}
	}

}