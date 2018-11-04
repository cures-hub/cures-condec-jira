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
import com.atlassian.jira.plugin.report.impl.AbstractReport;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.util.ParameterUtils;
import com.atlassian.jira.web.action.ProjectActionSupport;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.plugin.spring.scanner.annotation.imports.JiraImport;

import de.uhd.ifi.se.decision.management.jira.extraction.model.Sentence;
import de.uhd.ifi.se.decision.management.jira.extraction.model.impl.SentenceImpl;
import de.uhd.ifi.se.decision.management.jira.extraction.persistence.ActiveObjectsManager;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElementImpl;
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

	private String jiraIssueTypeToLinkTo;

	private String issuesWithNoExistingLinksToDecisionKnowledge;

	public static org.json.JSONObject restResponse;
	
	//Need these constructurs, instead bean exception
	public DecisionKnowledgeReport(ProjectManager projectManager) {
		this.projectManager = projectManager;
	}
	
	//Need these constructurs, instead bean exception
	public DecisionKnowledgeReport(ProjectManager projectManager, String rootType) {
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

		// get Number of comments per Issue
		velocityParams.put("numCommentsPerIssueMap", getNumberOfCommentsPerIssueMap(action.getLoggedInUser()));

		// get Number of Decisions per Issue
		velocityParams.put("numDecisionsPerIssueMap",
				getNumberOfSentencePerIssueMap(action.getLoggedInUser(), KnowledgeType.DECISION));

		// get Number of Issues per Issue
		velocityParams.put("numIssuesPerIssueMap",
				getNumberOfSentencePerIssueMap(action.getLoggedInUser(), KnowledgeType.ISSUE));

		// get Number of relevant Sentences per Issue
		Map<String, Integer> numRelevantSentences = getNumberOfRelevantSentences(action.getLoggedInUser());
		velocityParams.put("numRelevantSentences", numRelevantSentences);
		velocityParams.put("map", Map.class); // TODO: what was this for? It is not used in vm.

		// get Number of commits per Issue
		velocityParams.put("numCommitsPerIssueMap", getNumberOfCommitsPerIssueMap(action.getLoggedInUser()));

		// Get associated Knowledge Types in Sentences per Issue
		Map<String, Integer> numKnowledgeTypesPerIssue = getDecKnowElementsPerIssue();
		velocityParams.put("numKnowledgeTypesPerIssue", numKnowledgeTypesPerIssue);

		// Get types of decisions and alternatives linkes to Issue (e.g. has decision
		// but no alternative)
		velocityParams.put("numLinksToIssue", getLinkToOtherElement(KnowledgeType.ISSUE, KnowledgeType.DECISION));
		velocityParams.put("issuesWithoutDecisionLinks", this.issuesWithNoExistingLinksToDecisionKnowledge);
		velocityParams.put("numLinksToDecision", getLinkToOtherElement(KnowledgeType.DECISION, KnowledgeType.ISSUE));
		velocityParams.put("decisionsWithoutIssueLinks", this.issuesWithNoExistingLinksToDecisionKnowledge);

		// Get Number of Alternatives With Arguments
		velocityParams.put("numAlternativeWoArgument", getAlternativeArguments());
		velocityParams.put("issuesWithAltWoArg", this.issuesWithNoExistingLinksToDecisionKnowledge);

		// Get Link Distance
		velocityParams.put("numLinkDistanceAlternative", getLinkDistance(KnowledgeType.ALTERNATIVE));
		velocityParams.put("numLinkDistanceIssue", getLinkDistance(KnowledgeType.ISSUE));
		velocityParams.put("numLinkDistanceDecision", getLinkDistance(KnowledgeType.DECISION));

		velocityParams.put("numLinksToIssueTypeIssue",
				getLinksToIssueTypeMap(KnowledgeType.ISSUE, action.getLoggedInUser()));
		velocityParams.put("jiraIssuesWithoutLinksToIssue", this.issuesWithNoExistingLinksToDecisionKnowledge);

		velocityParams.put("numLinksToIssueTypeDecision",
				getLinksToIssueTypeMap(KnowledgeType.DECISION, action.getLoggedInUser()));
		velocityParams.put("jiraIssuesWithoutLinksToDecision", this.issuesWithNoExistingLinksToDecisionKnowledge);

		velocityParams.put("issueType", getPropperStringForBugAndTasksFromIssueType());

		return velocityParams;
	}

	private Object getLinksToIssueTypeMap(KnowledgeType knowledgeType, ApplicationUser applicationUser) {
		Map<String, Integer> result = new HashMap<>();
		String noLinkExistingList = "";
		int withLink = 0;
		int withoutLink = 0;
		SearchResults issues = getIssuesForThisProject(applicationUser);
		for (Issue issue : issues.getIssues()) {
			boolean linkExisting = false;
			if (checkEqualIssueTypeIssue(issue.getIssueType())) {
				for (Link link : GenericLinkManager.getLinksForElement("i" + issue.getId())) {
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
				noLinkExistingList += issue.getKey() + " ";
			}
		}
		result.put("Links from " + getPropperStringForBugAndTasksFromIssueType() + " " + knowledgeType.toString(),
				withLink);
		result.put("No links from " + getPropperStringForBugAndTasksFromIssueType() + " " + knowledgeType.toString(),
				withoutLink);
		this.issuesWithNoExistingLinksToDecisionKnowledge = noLinkExistingList;
		return result;
	}

	private String getPropperStringForBugAndTasksFromIssueType() {
		if (this.jiraIssueTypeToLinkTo.equalsIgnoreCase("Wi")) {
			return "Work Item";
		} else if (this.jiraIssueTypeToLinkTo.equals("B")) {
			return "Bug";
		}
		return "Unknown Element";
	}

	private boolean checkEqualIssueTypeIssue(IssueType issueType2) {
		if (issueType2 == null) {
			return false;
		}
		if (this.jiraIssueTypeToLinkTo.equals("WI")
				&& (issueType2.getName().equalsIgnoreCase("User Task") || issueType2.getName().equalsIgnoreCase("Aufgabe"))) {
			return true;
		}
		return (this.jiraIssueTypeToLinkTo.equals("B")
				&& (issueType2.getName().equalsIgnoreCase("Bug") || issueType2.getName().equalsIgnoreCase("Fehler")));

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

	private Map<String, Integer> getNumberOfSentencePerIssueMap(ApplicationUser loggedInUser, KnowledgeType type) {
		Map<String, Integer> result = new HashMap<String, Integer>();

		SearchResults projectIssues = getIssuesForThisProject(loggedInUser);
		if (projectIssues == null || projectIssues.getIssues().size() == 0) {
			return result;
		}
		String projectKey = ComponentAccessor.getProjectManager().getProjectObj(this.projectId).getKey();
		for (Issue currentIssue : projectIssues.getIssues()) {
			int count = 0;
			List<DecisionKnowledgeElement> elements = ActiveObjectsManager.getElementsForIssue(currentIssue.getId(),
					projectKey);
			for (DecisionKnowledgeElement dke : elements) {
				if ((new SentenceImpl(dke.getId())).getType().equals(type)) {
					count++;
				}
			}
			result.put(currentIssue.getKey(), count);
		}
		return result;
	}

	private List<Integer> getLinkDistance(KnowledgeType type) {
		List<Integer> linkDistances = new ArrayList<>();

		List<DecisionKnowledgeElement> listOfIssues = ActiveObjectsManager
				.getAllElementsFromAoByType(projectManager.getProjectObj(this.projectId).getKey(), type);

		for (DecisionKnowledgeElement currentAlternative : listOfIssues) {
			int depth = graphRecursionBot(currentAlternative);
			linkDistances.add(depth);
		}

		return linkDistances;
	}

	private Map<String, Integer> getAlternativeArguments() {
		int alternativesHaveArgument = 0;
		int alternativesHaveNoArgument = 0;
		String listOfElementsWithoutArgument = "";

		List<DecisionKnowledgeElement> listOfIssues = ActiveObjectsManager.getAllElementsFromAoByType(
				projectManager.getProjectObj(this.projectId).getKey(), KnowledgeType.ALTERNATIVE);

		for (DecisionKnowledgeElement currentAlternative : listOfIssues) {
			List<Link> links = GenericLinkManager.getLinksForElement("s" + currentAlternative.getId());
			boolean hasArgument = false;
			for (Link link : links) {
				DecisionKnowledgeElement dke = link.getOppositeElement("s" + currentAlternative.getId());
				if (dke instanceof Sentence && dke.getType().equals(KnowledgeType.ARGUMENT)) {
					hasArgument = true;
				}
			}
			if (hasArgument) {
				alternativesHaveArgument++;
			} else {
				alternativesHaveNoArgument++;
				if (currentAlternative instanceof Sentence && !listOfElementsWithoutArgument
						.contains(((Sentence) currentAlternative).getKey().split(":")[0])) {
					listOfElementsWithoutArgument += ((Sentence) currentAlternative).getKey().split(":")[0] + " ";
				}
			}
		}
		this.issuesWithNoExistingLinksToDecisionKnowledge = listOfElementsWithoutArgument;
		Map<String, Integer> dkeCount = new HashMap<String, Integer>();
		dkeCount.put("Alternative with Argument", alternativesHaveArgument);
		dkeCount.put("Alternative without Argument", alternativesHaveNoArgument);

		return dkeCount;
	}

	private Map<String, Integer> getLinkToOtherElement(KnowledgeType linkFrom, KnowledgeType linkTo1) {
		Integer[] statistics = new Integer[4];
		Arrays.fill(statistics, 0);
		String listOfElementsWithoutLink = " ";
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
				if (issue instanceof Sentence
						&& !listOfElementsWithoutLink.contains(((Sentence) issue).getKey().split(":")[0])) {
					listOfElementsWithoutLink += ((Sentence) issue).getKey().split(":")[0] + " ";
				}
			}
		}

		// Hashmaps as counter suck
		Map<String, Integer> dkeCount = new HashMap<String, Integer>();
		dkeCount.put("Has " + linkTo1.toString(), statistics[0]);
		dkeCount.put("Has no " + linkTo1.toString(), statistics[1]);
		this.issuesWithNoExistingLinksToDecisionKnowledge = listOfElementsWithoutLink;
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

	private Map<String, Integer> getNumberOfCommitsPerIssueMap(ApplicationUser loggedInUser) {
		Map<String, Integer> resultMap = new HashMap<String, Integer>();

		SearchResults issues = getIssuesForThisProject(loggedInUser);
		for (Issue issue : issues.getIssues()) {
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

	private Map<String, Integer> getNumberOfCommentsPerIssueMap(ApplicationUser user) {
		Map<String, Integer> result = new HashMap<String, Integer>();
		SearchResults searchResults = getIssuesForThisProject(user);
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
		this.jiraIssueTypeToLinkTo = ParameterUtils.getStringParam(params, "rootType");
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
			Node newChildNode = createNodeStructure(childAndLink.getKey(), childAndLink.getValue(), depth,
					currentDepth + 1, graph);
			if (this.absolutDepth < currentDepth) {
				this.absolutDepth = currentDepth;
			}
			nodes.add(newChildNode);
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