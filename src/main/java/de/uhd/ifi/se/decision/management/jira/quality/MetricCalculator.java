package de.uhd.ifi.se.decision.management.jira.quality;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.issue.link.IssueLinkManager;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchResults;
import com.atlassian.jira.jql.builder.JqlClauseBuilder;
import com.atlassian.jira.jql.builder.JqlQueryBuilder;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.query.Query;

import de.uhd.ifi.se.decision.management.jira.config.JiraIssueTypeGenerator;
import de.uhd.ifi.se.decision.management.jira.extraction.impl.GitClientImpl;
import de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol.GitCodeClassExtractor;
import de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol.GitDecXtract;
import de.uhd.ifi.se.decision.management.jira.filtering.JiraSearchServiceHelper;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.impl.KnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.impl.GenericLinkManager;
import de.uhd.ifi.se.decision.management.jira.persistence.impl.JiraIssueTextPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.view.ChartCreator;

public class MetricCalculator {

    private String projectKey;
    private ApplicationUser user;
    private List<Issue> jiraIssues;
    private KnowledgeGraph graph;
    private List<KnowledgeElement> decisionKnowledgeCodeElements;
    private List<KnowledgeElement> decisionKnowledgeCommitElements;
    private final String dataStringSeparator = " ";
    private String issueTypeId;
    private GitClientImpl gitClient;

    protected static final Logger LOGGER = LoggerFactory.getLogger(ChartCreator.class);

    public MetricCalculator(Long projectId, ApplicationUser user, String issueTypeId) {
	this.projectKey = ComponentAccessor.getProjectManager().getProjectObj(projectId).getKey();
	this.user = user;
	this.graph = KnowledgeGraph.getOrCreate(projectKey);
	this.jiraIssues = getJiraIssuesForProject(projectId, user);
	this.gitClient = new GitClientImpl(projectKey);
	Map<String, List<KnowledgeElement>> elementMap = getDecisionKnowledgeElementsFromCode(projectKey);
	if (elementMap != null) {
	    this.decisionKnowledgeCodeElements = elementMap.get("Code");
	    this.decisionKnowledgeCommitElements = elementMap.get("Commit");
	} else {
	    this.decisionKnowledgeCodeElements = null;
	    this.decisionKnowledgeCommitElements = null;
	}
	this.issueTypeId = issueTypeId;
    }

    public Map<Integer, List<Issue>> getLinkDistanceIssueMap(Integer linkDistance, Issue jiraIssue) {
	Map<Integer, List<Issue>> linkDistanceMap = new HashMap<Integer, List<Issue>>();
	IssueLinkManager issueLinkManager = ComponentAccessor.getIssueLinkManager();
	List<Issue> linkissues = new ArrayList<Issue>();
	linkissues.add(jiraIssue);
	linkDistanceMap.put(0, linkissues);
	List<Issue> inMap = new ArrayList<Issue>();
	inMap.add(jiraIssue);
	for (int i = 1; i <= linkDistance; i++) {
	    linkissues = new ArrayList<Issue>();
	    for (Issue issue : linkDistanceMap.get(i - 1)) {
		Collection<Issue> issueColl = issueLinkManager.getLinkCollection(issue, user).getAllIssues();
		linkissues.addAll(issueColl);
		linkissues.removeAll(inMap);
	    }
	    List<Issue> linkissueswithoutduplicate = new ArrayList<>(new HashSet<>(linkissues)); // Remove Duplicates
	    linkDistanceMap.put(i, linkissueswithoutduplicate);
	    inMap.addAll(linkissueswithoutduplicate);
	}
	return linkDistanceMap;
    }

    public static List<Issue> getJiraIssuesForProject(long projectId, ApplicationUser user) {
	List<Issue> jiraIssues = new ArrayList<Issue>();
	JqlClauseBuilder jqlClauseBuilder = JqlQueryBuilder.newClauseBuilder();
	Query query = jqlClauseBuilder.project(projectId).buildQuery();
	SearchResults<Issue> searchResults = null;
	SearchService searchService = ComponentAccessor.getComponentOfType(SearchService.class);
	try {
	    searchResults = searchService.search(user, query, PagerFilter.getUnlimitedFilter());
	    jiraIssues = JiraSearchServiceHelper.getJiraIssues(searchResults);
	} catch (SearchException e) {
	    LOGGER.error("Getting JIRA issues for project failed. Message: " + e.getMessage());
	}
	return jiraIssues;
    }

    private Map<String, List<KnowledgeElement>> getDecisionKnowledgeElementsFromCode(String projectKey) {
	// Extracts Decision Knowledge from Code Comments AND Commits
	GitDecXtract gitExtract = new GitDecXtract(projectKey);

	Map<String, List<KnowledgeElement>> resultMap = new HashMap<String, List<KnowledgeElement>>();
	List<KnowledgeElement> allGatheredCommitElements = new ArrayList<>();
	List<KnowledgeElement> allGatheredCodeElements = new ArrayList<>();
	for (String repoUri : gitClient.getRemoteUris()) {
	    List<Ref> branches = gitClient.getRemoteBranches(repoUri);
	    Ref lastBranch = branches.get(0);
	    Ref defaultBranch = gitClient.getDefaultBranch(repoUri);
	    List<KnowledgeElement> gatheredCommitElements = new ArrayList<>();
	    List<KnowledgeElement> gatheredCodeElements = new ArrayList<>();
	    List<RevCommit> defaultfeatureCommits = gitClient.getFeatureBranchCommits(defaultBranch);
	    List<RevCommit> lastfeatureCommits = gitClient.getFeatureBranchCommits(lastBranch);
	    if (defaultfeatureCommits == null || defaultfeatureCommits.size() == 0 || lastfeatureCommits == null
		    || lastfeatureCommits.size() == 0) {
		return resultMap;
	    } else {
		for (RevCommit commit : defaultfeatureCommits) {
		    gatheredCommitElements.addAll(gitExtract.getElementsFromMessage(commit));
		}
		allGatheredCommitElements.addAll(gatheredCommitElements);
		RevCommit baseCommit = lastfeatureCommits.get(0);
		RevCommit lastFeatureBranchCommit = lastfeatureCommits.get(lastfeatureCommits.size() - 1);
		gatheredCodeElements
			.addAll(gitExtract.getElementsFromCode(baseCommit, lastFeatureBranchCommit, lastBranch));
		allGatheredCodeElements.addAll(gatheredCodeElements);
	    }
	}
	resultMap.put("Commit", allGatheredCommitElements);
	resultMap.put("Code", allGatheredCodeElements);
	return resultMap;
    }

    public Map<String, Integer> numberOfCommentsPerIssue() {
	Map<String, Integer> numberMap = new HashMap<String, Integer>();
	int numberOfComments;
	for (Issue jiraIssue : jiraIssues) {
	    try {
		numberOfComments = ComponentAccessor.getCommentManager().getComments(jiraIssue).size();
	    } catch (NullPointerException e) {
		LOGGER.error("Getting number of comments for JIRA issues failed. Message: " + e.getMessage());
		numberOfComments = 0;
	    }
	    numberMap.put(jiraIssue.getKey(), numberOfComments);
	}
	return numberMap;
    }

    public Map<String, Integer> getNumberOfDecisionKnowledgeElementsForJiraIssues(KnowledgeType type,
	    Integer linkDistance) {
	Map<String, Integer> numberOfSentencesPerIssue = new HashMap<String, Integer>();
	for (Issue jiraIssue : jiraIssues) {
	    int numberOfElements = 0;
	    Map<Integer, List<Issue>> linkDistanceMap = getLinkDistanceIssueMap(linkDistance, jiraIssue);
	    for (int i = 0; i <= linkDistance; i++) {
		for (Issue issue : linkDistanceMap.get(i)) {
		    List<KnowledgeElement> elements = KnowledgePersistenceManager.getOrCreate(projectKey)
			    .getJiraIssueTextManager().getElementsInJiraIssue(issue.getId());
		    if (issue.getIssueType().getName().equals(type.toString())) {
			numberOfElements++;
		    }
		    for (KnowledgeElement element : elements) {
			if (issue.getKey().equals("CONDEC-18")) {
			    System.out.println(element.getSummary());
			}
			if (element.getType().equals(type)) {
			    numberOfElements++;
			}
		    }
		    if (i <= linkDistance - 2 && decisionKnowledgeCodeElements != null) {
			for (KnowledgeElement element : decisionKnowledgeCodeElements) {
			    if (element.getType().equals(type)) {
				numberOfElements++;
			    }
			}
		    }
		    if (i > 0 && i <= (linkDistance - 1) && decisionKnowledgeCommitElements != null) {
			for (KnowledgeElement element : decisionKnowledgeCommitElements) {
			    if (element.getType().equals(type)) {
				numberOfElements++;
			    }
			}
		    }
		}
	    }
	    numberOfSentencesPerIssue.put(jiraIssue.getKey(), numberOfElements);
	}
	return numberOfSentencesPerIssue;
    }

    public Map<String, Integer> getDistributionOfKnowledgeTypes() {
	Map<String, Integer> distributionOfKnowledgeTypes = new HashMap<String, Integer>();
	for (KnowledgeType type : KnowledgeType.getDefaultTypes()) {
	    int numberOfElements = graph.getElements(type).size();
	    /*
	     * for (KnowledgeElement element :
	     * (Optional.ofNullable(decisionKnowledgeCodeElements)
	     * .orElse(Collections.emptyList()))) { if (element.getType().equals(type)) {
	     * numberOfElements++; } } for (KnowledgeElement element :
	     * (Optional.ofNullable(decisionKnowledgeCommitElements)
	     * .orElse(Collections.emptyList()))) { if (element.getType().equals(type)) {
	     * numberOfElements++; } }
	     */
	    distributionOfKnowledgeTypes.put(type.toString(), numberOfElements);
	}
	return distributionOfKnowledgeTypes;
    }

    public Map<String, Integer> getReqAndClassSummary() {
	Map<String, Integer> summaryMap = new HashMap<String, Integer>();
	int numberOfRequirements = 0;
	for (Issue issue : jiraIssues) {
	    // Temporary Solution until Settings are available
	    if (issue.getIssueType().getName().equals("System Function")
		    || issue.getIssueType().getName().equals("Nonfunctional Requirement")
		    || issue.getIssueType().getName().equals("Persona")
		    || issue.getIssueType().getName().equals("Usertask")
		    || issue.getIssueType().getName().equals("Subtask")
		    || issue.getIssueType().getName().equals("Workspace")) {
		numberOfRequirements++;
	    }
	}
	summaryMap.put("Requirements", numberOfRequirements);
	GitCodeClassExtractor extract = new GitCodeClassExtractor("CONDEC");
	summaryMap.put("Code Classes", extract.getNumberOfCodeClasses());
	return summaryMap;
    }

    public Map<String, Integer> getKnowledgeSourceCount() {
	Map<String, Integer> sourceMap = new HashMap<String, Integer>();
	if (decisionKnowledgeCodeElements != null) {
	    sourceMap.put("Code", decisionKnowledgeCodeElements.size());
	} else {
	    sourceMap.put("Code", 0);
	}
	if (decisionKnowledgeCommitElements != null) {
	    sourceMap.put("Commit", decisionKnowledgeCommitElements.size());
	} else {
	    sourceMap.put("Commit", 0);
	}
	int numberIssues = 0;
	int numberIssueContent = 0;
	List<KnowledgeElement> elements = new ArrayList<KnowledgeElement>();
	for (KnowledgeType type : KnowledgeType.getDefaultTypes()) {
	    elements.addAll(graph.getElements(type));
	}
	for (KnowledgeElement element : elements) {
	    if (element.getDocumentationLocation().getIdentifier().equals("i")) {
		numberIssues++;
	    } else if (element.getDocumentationLocation().getIdentifier().equals("s")) {
		numberIssueContent++;
	    }
	}
	sourceMap.put("Issue Content", numberIssueContent);
	sourceMap.put("Jira Issues", numberIssues);
	return sourceMap;
    }

    public Map<String, String> getDecKnowlElementsOfATypeGroupedByHavingElementsOfOtherType(KnowledgeType linkFrom,
	    KnowledgeType linkTo) {
	String[] data = new String[2];
	Arrays.fill(data, "");

	List<KnowledgeElement> listOfIssues = graph.getElements(linkFrom);

	for (KnowledgeElement issue : listOfIssues) {
	    List<Link> links = GenericLinkManager.getLinksForElement(issue.getId(),
		    DocumentationLocation.JIRAISSUETEXT);
	    boolean hastOtherElementLinked = false;

	    for (Link link : links) {
		if (link.isValid() && link.getOppositeElement(issue.getId()) instanceof PartOfJiraIssueText
			&& link.getOppositeElement(issue.getId()).getType().equals(linkTo)) {
		    hastOtherElementLinked = true;
		    data[0] += issue.getKey() + dataStringSeparator;
		}
	    }
	    if (!hastOtherElementLinked) {
		data[1] += issue.getKey() + dataStringSeparator;
	    }
	}
	IssueLinkManager issueLinkManager = ComponentAccessor.getIssueLinkManager();
	// Elements from Issues
	for (Issue issue : jiraIssues) {
	    if (issue.getIssueType().getName().equals(linkFrom.toString())) {
		Collection<Issue> issueColl = issueLinkManager.getLinkCollection(issue, user).getAllIssues();
		boolean hasDecision = false;
		for (Issue linkedIssue : issueColl) {
		    if (!hasDecision && linkedIssue.getIssueType().getName().equals(linkTo.toString())) {
			hasDecision = true;
			data[0] += issue.getKey() + dataStringSeparator;
		    }
		}
		if (!hasDecision) {
		    data[1] += issue.getKey() + dataStringSeparator;
		}
	    }
	}
	// TODO: Find a way to include Elements from Commit and Code as they are not
	// linked between Issue and Decision and there is no way to link to them in the
	// chart
	Map<String, String> havingLinkMap = new HashMap<String, String>();
	havingLinkMap.put(linkFrom.toString() + " has " + linkTo.toString(), data[0].trim());
	havingLinkMap.put(linkFrom.toString() + " has no " + linkTo.toString(), data[1].trim());
	return havingLinkMap;
    }

    public Map<String, Integer> getNumberOfRelevantComments() {
	Map<String, Integer> numberOfRelevantSentences = new HashMap<String, Integer>();
	int isRelevant = 0;
	int isIrrelevant = 0;

	JiraIssueTextPersistenceManager persistenceManager = KnowledgePersistenceManager.getOrCreate(projectKey)
		.getJiraIssueTextManager();
	for (Issue jiraIssue : jiraIssues) {
	    List<Comment> comments = ComponentAccessor.getCommentManager().getComments(jiraIssue);
	    List<KnowledgeElement> elements = persistenceManager.getElementsInJiraIssue(jiraIssue.getId());
	    for (Comment comment : comments) {
		boolean relevant = false;
		for (KnowledgeElement currentElement : elements) {
		    if (comment.getBody().contains(currentElement.getDescription())
			    && currentElement.getTypeAsString() != "OTHER") {
			relevant = true;
			isRelevant++;
		    }
		}
		if (!relevant) {
		    isIrrelevant++;
		}
	    }
	    /*
	     * for (DecisionKnowledgeElement currentElement : elements) {
	     * 
	     * if (currentElement instanceof PartOfJiraIssueText && ((PartOfJiraIssueText)
	     * currentElement).isRelevant()) { isRelevant++; } else if (currentElement
	     * instanceof PartOfJiraIssueText && !((PartOfJiraIssueText)
	     * currentElement).isRelevant()) { isIrrelevant++; } }
	     */

	}
	numberOfRelevantSentences.put("Relevant Sentences", isRelevant);
	numberOfRelevantSentences.put("Irrelevant Sentences", isIrrelevant);
	return numberOfRelevantSentences;
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
		// skipped+=issue.getKey()+dataStringSeparator;
		continue;
	    }
	    for (Link link : GenericLinkManager.getLinksForElement(issue.getId(), DocumentationLocation.JIRAISSUE)) {
		if (link.isValid()) {
		    KnowledgeElement dke = link.getOppositeElement(new KnowledgeElementImpl(issue));
		    if (dke.getType().equals(knowledgeType)) {
			linkExisting = true;
		    }
		}
	    }

	    if (linkExisting) {
		withLink += issue.getKey() + dataStringSeparator;
	    } else {
		withoutLink += issue.getKey() + dataStringSeparator;
	    }
	}

	String jiraIssueTypeName = JiraIssueTypeGenerator.getJiraIssueTypeName(issueTypeId);

	result.put("Links from " + jiraIssueTypeName + " to " + knowledgeType.toString(), withLink);
	result.put("No links from " + jiraIssueTypeName + " to " + knowledgeType.toString(), withoutLink);
	// result.put("Skipped issues not of target type " + jiraIssueTypeName,
	// skipped);
	return result;
    }

    private boolean checkEqualIssueTypeIssue(IssueType issueType2) {
	if (issueType2 == null) {
	    return false;
	}

	String jiraIssueTypeName = JiraIssueTypeGenerator.getJiraIssueTypeName(issueTypeId);
	return issueType2.getName().equalsIgnoreCase(jiraIssueTypeName);
    }
}
