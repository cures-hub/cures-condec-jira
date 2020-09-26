package de.uhd.ifi.se.decision.management.jira.quality;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.eclipse.jgit.revwalk.RevCommit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.jira.issue.link.IssueLinkManager;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchResults;
import com.atlassian.jira.jql.builder.JqlClauseBuilder;
import com.atlassian.jira.jql.builder.JqlQueryBuilder;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.query.Query;

import de.uhd.ifi.se.decision.management.jira.config.JiraIssueTypeGenerator;
import de.uhd.ifi.se.decision.management.jira.extraction.GitClient;
import de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol.GitDecXtract;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.DecisionGroupManager;
import de.uhd.ifi.se.decision.management.jira.persistence.GenericLinkManager;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.CodeClassPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssueTextPersistenceManager;

public class MetricCalculator {

	private String projectKey = "TEST";
	private ApplicationUser user;
	private List<Issue> jiraIssues;
	private KnowledgeGraph graph;
	private List<KnowledgeElement> decisionKnowledgeCodeElements;
	private List<KnowledgeElement> decisionKnowledgeCommitElements;
	private final String dataStringSeparator = " ";
	private String issueTypeId;
	private GitClient gitClient;
	private Map<String, List<KnowledgeElement>> extractedIssueRelatedElements;
	private boolean ignoreGit;
	private List<String> knowledgeTypes;
	private List<String> knowledgeStatus;
	private List<String> decisionGroups;

	protected static final Logger LOGGER = LoggerFactory.getLogger(MetricCalculator.class);

	public MetricCalculator(Long projectId, ApplicationUser user, String issueTypeId, boolean ignoreGit,
			List<String> knowledgeTypes, List<String> knowledgeStatus, List<String> decisionGroups) {
		if (ComponentAccessor.getProjectManager().getProjectObj(projectId) != null) {
			this.projectKey = ComponentAccessor.getProjectManager().getProjectObj(projectId).getKey();
		}
		this.user = user;
		this.graph = KnowledgeGraph.getOrCreate(projectKey);
		this.jiraIssues = getJiraIssuesForProject(projectId, user);
		this.ignoreGit = ignoreGit;
		this.knowledgeTypes = knowledgeTypes;
		this.knowledgeStatus = knowledgeStatus;
		this.decisionGroups = decisionGroups;
		if (ConfigPersistenceManager.isKnowledgeExtractedFromGit(projectKey) && !ignoreGit) {
			this.gitClient = GitClient.getOrCreate(projectKey);
			extractedIssueRelatedElements = new HashMap<>();
			Map<String, List<KnowledgeElement>> elementMap = getDecisionKnowledgeElementsFromCode(projectKey);
			if (elementMap != null) {
				this.decisionKnowledgeCodeElements = elementMap.get("Code");
				this.decisionKnowledgeCommitElements = elementMap.get("Commit");
			} else {
				this.decisionKnowledgeCodeElements = null;
				this.decisionKnowledgeCommitElements = null;
			}
		}
		this.issueTypeId = issueTypeId;
	}

	public static List<Issue> getJiraIssuesForProject(long projectId, ApplicationUser user) {
		List<Issue> jiraIssues = new ArrayList<Issue>();
		JqlClauseBuilder jqlClauseBuilder = JqlQueryBuilder.newClauseBuilder();
		Query query = jqlClauseBuilder.project(projectId).buildQuery();
		SearchResults<Issue> searchResults = null;
		SearchService searchService = ComponentAccessor.getComponentOfType(SearchService.class);
		try {
			searchResults = searchService.search(user, query, PagerFilter.getUnlimitedFilter());
			jiraIssues = searchResults.getResults();
		} catch (SearchException e) {
			LOGGER.error("Getting Jira issues for project failed. Message: " + e.getMessage());
		}
		return jiraIssues;
	}

	private Map<String, List<KnowledgeElement>> getDecisionKnowledgeElementsFromCode(String projectKey) {
		LOGGER.info("RequirementsDashboard getDecisionKnowledgeElementsFromCode 7");
		// Extracts Decision Knowledge from Code Comments AND Commits
		GitDecXtract gitExtract = new GitDecXtract(projectKey);

		Map<String, List<KnowledgeElement>> resultMap = new HashMap<String, List<KnowledgeElement>>();
		List<KnowledgeElement> allGatheredCommitElements = new ArrayList<>();
		List<KnowledgeElement> allGatheredCodeElements = new ArrayList<>();
		String filter = "(" + projectKey + "-)\\d+";
		Pattern filterPattern = Pattern.compile(filter, Pattern.CASE_INSENSITIVE);

		List<KnowledgeElement> gatheredCommitElements = new ArrayList<>();
		List<RevCommit> defaultfeatureCommits = gitClient.getDefaultBranchCommits();
		if (defaultfeatureCommits == null || defaultfeatureCommits.size() == 0) {
			return resultMap;
		} else {
			for (RevCommit commit : defaultfeatureCommits) {
				List<KnowledgeElement> extractedCommitElements = gitExtract.getElementsFromMessage(commit);
				gatheredCommitElements.addAll(extractedCommitElements);
				if (extractedCommitElements != null && extractedCommitElements.size() > 0) {
					Matcher matcher = filterPattern.matcher(commit.getFullMessage());
					if (matcher.find()) {
						this.extractedIssueRelatedElements.put(matcher.group(), extractedCommitElements);
					}
				}
			}
			for (KnowledgeElement element : gatheredCommitElements) {
				if (knowledgeTypes.contains(element.getTypeAsString())
						&& knowledgeStatus.contains(element.getStatusAsString()) && groupsMatch(element)) {
					allGatheredCommitElements.add(element);
				}
			}
			RevCommit baseCommit = defaultfeatureCommits.get(defaultfeatureCommits.size() - 2);
			RevCommit lastFeatureBranchCommit = defaultfeatureCommits.get(0);
			// TODO default branch
			List<KnowledgeElement> extractedCodeElements = gitExtract.getElementsFromCode(baseCommit,
					lastFeatureBranchCommit, gitClient.getAllRemoteBranches().get(0));
			for (KnowledgeElement element : extractedCodeElements) {
				if (knowledgeTypes.contains(element.getTypeAsString())
						&& knowledgeStatus.contains(element.getStatusAsString()) && groupsMatch(element)) {
					allGatheredCodeElements.add(element);
				}
			}
		}

		resultMap.put("Commit", allGatheredCommitElements);
		resultMap.put("Code", allGatheredCodeElements);
		return resultMap;

	}

	public Map<String, Integer> numberOfCommentsPerIssue() {
		LOGGER.info("RequirementsDashboard numberOfCommentsPerIssue <1");
		Map<String, Integer> numberMap = new HashMap<String, Integer>();
		int numberOfComments;
		for (Issue jiraIssue : jiraIssues) {
			try {
				numberOfComments = ComponentAccessor.getCommentManager().getComments(jiraIssue).size();
			} catch (NullPointerException e) {
				LOGGER.error("Getting number of comments for Jira issues failed. Message: " + e.getMessage());
				numberOfComments = 0;
			}
			numberMap.put(jiraIssue.getKey(), numberOfComments);
		}
		return numberMap;
	}

	public Map<String, Integer> getNumberOfDecisionKnowledgeElementsForJiraIssues(KnowledgeType type,
			Integer linkDistance) {
		LOGGER.info("RequirementsDashboard getNumberOfDecisionKnowledgeElementsForJiraIssues 3 2");

		Map<String, Integer> numberOfSentencesPerIssue = new HashMap<String, Integer>();
		for (Issue jiraIssue : jiraIssues) {
			int numberOfElements = 0;
			List<KnowledgeElement> elements = KnowledgePersistenceManager.getOrCreate(projectKey)
					.getJiraIssueTextManager().getElementsInJiraIssue(jiraIssue.getId());
			if (jiraIssue.getIssueType().getName().equals(type.toString())
					&& knowledgeStatus.contains(new KnowledgeElement(jiraIssue).getStatusAsString())
					&& groupsMatch(new KnowledgeElement(jiraIssue))) {
				numberOfElements++;
			}
			for (KnowledgeElement element : elements) {
				if (element.getType().equals(type) && knowledgeStatus.contains(element.getStatusAsString())
						&& groupsMatch(element)) {
					numberOfElements++;
				}
			}
			if (linkDistance >= 1 && extractedIssueRelatedElements != null
					&& extractedIssueRelatedElements.get(jiraIssue.getKey()) != null) {
				for (KnowledgeElement element : extractedIssueRelatedElements.get(jiraIssue.getKey())) {
					if (element.getType().equals(type) && knowledgeStatus.contains(element.getStatusAsString())
							&& groupsMatch(element)) {
						numberOfElements++;
					}
				}
			}
			numberOfSentencesPerIssue.put(jiraIssue.getKey(), numberOfElements);
		}
		return numberOfSentencesPerIssue;
	}

	public Map<String, Integer> getDistributionOfKnowledgeTypes() {
		LOGGER.info("RequirementsDashboard getDistributionOfKnowledgeTypes <1");
		Map<String, Integer> distributionOfKnowledgeTypes = new HashMap<String, Integer>();
		for (KnowledgeType type : KnowledgeType.getDefaultTypes()) {
			int numberOfElements = 0;
			List<KnowledgeElement> elements = graph.getElements(type);
			for (KnowledgeElement element : elements) {
				if (knowledgeStatus.contains(element.getStatusAsString()) && groupsMatch(element)) {
					numberOfElements++;
				}
			}
			distributionOfKnowledgeTypes.put(type.toString(), numberOfElements);
		}
		return distributionOfKnowledgeTypes;
	}

	public Map<String, Integer> getReqAndClassSummary() {
		LOGGER.info("RequirementsDashboard getReqAndClassSummary 3");
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
		if (ConfigPersistenceManager.isKnowledgeExtractedFromGit(projectKey) && !ignoreGit) {
			CodeClassPersistenceManager ccpManager = new CodeClassPersistenceManager(projectKey);
			summaryMap.put("Code Classes", ccpManager.getKnowledgeElements().size());
		} else {
			summaryMap.put("Code Classes", 0);
		}

		return summaryMap;
	}

	public Map<String, Integer> getKnowledgeSourceCount() {
		LOGGER.info("RequirementsDashboard getKnowledgeSourceCount <1");
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
			if (element.getDocumentationLocation() == DocumentationLocation.JIRAISSUE
					&& knowledgeTypes.contains(element.getTypeAsString())
					&& knowledgeStatus.contains(element.getStatusAsString()) && groupsMatch(element)) {
				numberIssues++;
			} else if (element.getDocumentationLocation() == DocumentationLocation.JIRAISSUETEXT
					&& knowledgeTypes.contains(element.getTypeAsString())
					&& knowledgeStatus.contains(element.getStatusAsString()) && groupsMatch(element)) {
				numberIssueContent++;
			}
		}
		sourceMap.put("Issue Content", numberIssueContent);
		sourceMap.put("Jira Issues", numberIssues);
		return sourceMap;
	}

	public Map<String, String> getDecKnowlElementsOfATypeGroupedByHavingElementsOfOtherType(KnowledgeType linkFrom,
			KnowledgeType linkTo) {
		LOGGER.info("RequirementsDashboard getDecKnowlElementsOfATypeGroupedByHavingElementsOfOtherType 3 4 4 5 4 4 4");
		String[] data = new String[2];
		Arrays.fill(data, "");

		List<KnowledgeElement> listOfIssues = graph.getElements(linkFrom);

		for (KnowledgeElement issue : listOfIssues) {
			List<Link> links = GenericLinkManager.getLinksForElement(issue.getId(),
					DocumentationLocation.JIRAISSUETEXT);
			boolean hastOtherElementLinked = false;
			for (Link link : links) {
				if (link != null && link.getTarget() != null && link.getSource() != null && link.isValid()
						&& link.getOppositeElement(issue.getId()) instanceof PartOfJiraIssueText
						&& link.getOppositeElement(issue.getId()).getType().equals(linkTo)
						&& knowledgeStatus.contains(issue.getStatusAsString())
						&& knowledgeStatus.contains(link.getOppositeElement(issue).getStatusAsString())
						&& groupsMatch(issue) && groupsMatch(link.getOppositeElement(issue))) {
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
			if (issue.getIssueType().getName().equals(linkFrom.toString())
					&& issueLinkManager.getLinkCollection(issue, user) != null) {
				Collection<Issue> issueColl = issueLinkManager.getLinkCollection(issue, user).getAllIssues();
				boolean hasDecision = false;
				for (Issue linkedIssue : issueColl) {
					if (!hasDecision && linkedIssue.getIssueType().getName().equals(linkTo.toString())
							&& knowledgeStatus.contains(new KnowledgeElement(linkedIssue).getStatusAsString())
							&& groupsMatch(new KnowledgeElement(linkedIssue))) {
						hasDecision = true;
						data[0] += issue.getKey() + dataStringSeparator;
					}
				}
				if (!hasDecision) {
					data[1] += issue.getKey() + dataStringSeparator;
				}
			}
		}
		Map<String, String> havingLinkMap = new LinkedHashMap<String, String>();
		havingLinkMap.put(linkFrom.toString() + " has " + linkTo.toString(), data[0].trim());
		havingLinkMap.put(linkFrom.toString() + " has no " + linkTo.toString(), data[1].trim());
		return havingLinkMap;
	}

	public Map<String, Integer> getNumberOfRelevantComments() {
		LOGGER.info("RequirementsDashboard getNumberOfRelevantComments 3");
		Map<String, Integer> numberOfRelevantSentences = new LinkedHashMap<String, Integer>();
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
							&& currentElement.getType() == KnowledgeType.OTHER
							&& knowledgeTypes.contains(currentElement.getTypeAsString())
							&& knowledgeStatus.contains(currentElement.getStatusAsString())
							&& groupsMatch(currentElement)) {
						relevant = true;
						isRelevant++;
					}
				}
				if (!relevant) {
					isIrrelevant++;
				}
			}
		}
		numberOfRelevantSentences.put("Relevant Comment", isRelevant);
		numberOfRelevantSentences.put("Irrelevant Comment", isIrrelevant);
		return numberOfRelevantSentences;
	}

	public Map<String, String> getLinksToIssueTypeMap(KnowledgeType knowledgeType, int linkDistance) {
		LOGGER.info("RequirementsDashboard getLinksToIssueTypeMap 1 3");
		if (knowledgeType == null) {
			return null;
		}
		Map<String, String> result = new LinkedHashMap<String, String>();
		String withLink = "";
		String withoutLink = "";
		for (Issue jiraIssue : jiraIssues) {
			if (jiraIssue.getIssueTypeId() != null && jiraIssue.getIssueTypeId().equals(issueTypeId)) {
				int numberOfElements = 0;
				List<KnowledgeElement> elements = KnowledgePersistenceManager.getOrCreate(projectKey)
						.getJiraIssueTextManager().getElementsInJiraIssue(jiraIssue.getId());
				for (KnowledgeElement element : elements) {
					if (element.getType().equals(knowledgeType) && knowledgeStatus.contains(element.getStatusAsString())
							&& groupsMatch(element)) {
						numberOfElements++;
					}
				}
				if (linkDistance >= 1 && extractedIssueRelatedElements != null
						&& extractedIssueRelatedElements.get(jiraIssue.getKey()) != null) {
					for (KnowledgeElement element : extractedIssueRelatedElements.get(jiraIssue.getKey())) {
						if (element.getType().equals(knowledgeType)
								&& knowledgeStatus.contains(element.getStatusAsString()) && groupsMatch(element)) {
							numberOfElements++;
						}
					}
				}
				if (numberOfElements > 0) {
					withLink += jiraIssue.getKey() + dataStringSeparator;
				} else {
					withoutLink += jiraIssue.getKey() + dataStringSeparator;
				}
			}
		}
		String jiraIssueTypeName = JiraIssueTypeGenerator.getJiraIssueTypeName(issueTypeId);
		result.put("Links from " + jiraIssueTypeName + " to " + knowledgeType.toString(), withLink);
		result.put("No links from " + jiraIssueTypeName + " to " + knowledgeType.toString(), withoutLink);
		return result;
	}

	private boolean groupsMatch(KnowledgeElement element) {
		return (decisionGroups == null || DecisionGroupManager.getGroupsForElement(element) != null
				&& CollectionUtils.containsAny(decisionGroups, DecisionGroupManager.getGroupsForElement(element)));
	}

	public void setJiraIssues(List<MutableIssue> issues) {
		jiraIssues = new ArrayList<>();
		for (MutableIssue issue : issues) {
			jiraIssues.add(issue);
		}
	}

	// TODO: Improve test dataset to no longer require this method
	public void addElementFromGit(KnowledgeElement element, Issue issue) {
		decisionKnowledgeCodeElements = new ArrayList<>();
		decisionKnowledgeCodeElements.add(element);
		decisionKnowledgeCommitElements = new ArrayList<>();
		decisionKnowledgeCommitElements.add(element);
		List<KnowledgeElement> elements = new ArrayList<KnowledgeElement>();
		elements.add(element);
		extractedIssueRelatedElements = new HashMap<>();
		extractedIssueRelatedElements.put(issue.getKey(), elements);
	}
}
