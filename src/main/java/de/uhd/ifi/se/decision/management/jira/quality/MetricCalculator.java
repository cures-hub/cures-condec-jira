package de.uhd.ifi.se.decision.management.jira.quality;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import de.uhd.ifi.se.decision.management.jira.extraction.GitClient;
import de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol.GitCodeClassExtractor;
import de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol.GitDecXtract;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.GenericLinkManager;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssueTextPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.view.ChartCreator;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetricCalculator {

	private String projectKey;
	private ApplicationUser user;
	private List<Issue> jiraIssues;
	private KnowledgeGraph graph;
	private List<KnowledgeElement> decisionKnowledgeCodeElements;
	private List<KnowledgeElement> decisionKnowledgeCommitElements;
	private final String dataStringSeparator = " ";
	private String issueTypeId;
	private GitClient gitClient;
	private Map<String, Map<String, List<KnowledgeElement>>> extractedIssueRelatedElements;

	protected static final Logger LOGGER = LoggerFactory.getLogger(ChartCreator.class);

	public MetricCalculator(Long projectId, ApplicationUser user, String issueTypeId) {
		this.projectKey = ComponentAccessor.getProjectManager().getProjectObj(projectId).getKey();
		this.user = user;
		this.graph = KnowledgeGraph.getOrCreate(projectKey);
		this.jiraIssues = getJiraIssuesForProject(projectId, user);
		if (ConfigPersistenceManager.isKnowledgeExtractedFromGit(projectKey)) {
			this.gitClient = new GitClient(projectKey);
			Map<String, List<KnowledgeElement>> elementMap = getDecisionKnowledgeElementsFromCode(projectKey);
			if (elementMap != null) {
				this.decisionKnowledgeCodeElements = elementMap.get("Code");
				this.decisionKnowledgeCommitElements = elementMap.get("Commit");
			} else {
				this.decisionKnowledgeCodeElements = null;
				this.decisionKnowledgeCommitElements = null;
			}
			this.extractedIssueRelatedElements = getDecisionKnowledgeElementsFromCodeRelatedToIssue();
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
			jiraIssues = searchResults.getResults();
		} catch (SearchException e) {
			LOGGER.error("Getting JIRA issues for project failed. Message: " + e.getMessage());
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
		for (String repoUri : gitClient.getRemoteUris()) {
			Ref defaultBranch = gitClient.getDefaultBranch(repoUri);
			List<KnowledgeElement> gatheredCommitElements = new ArrayList<>();
			List<KnowledgeElement> gatheredCodeElements = new ArrayList<>();
			List<RevCommit> defaultfeatureCommits = gitClient.getDefaultBranchCommits(repoUri);
			if (defaultfeatureCommits == null || defaultfeatureCommits.size() == 0) {
				return resultMap;
			} else {
				for (RevCommit commit : defaultfeatureCommits) {
					gatheredCommitElements.addAll(gitExtract.getElementsFromMessage(commit));
				}
				allGatheredCommitElements.addAll(gatheredCommitElements);
				RevCommit baseCommit = defaultfeatureCommits.get(defaultfeatureCommits.size() - 2);
				RevCommit lastFeatureBranchCommit = defaultfeatureCommits.get(0);
				gatheredCodeElements
						.addAll(gitExtract.getElementsFromCode(baseCommit, lastFeatureBranchCommit, defaultBranch));
				allGatheredCodeElements.addAll(gatheredCodeElements);
			}
		}
		resultMap.put("Commit", allGatheredCommitElements);
		resultMap.put("Code", allGatheredCodeElements);
		return resultMap;
	}

	private Map<String, Map<String, List<KnowledgeElement>>> getDecisionKnowledgeElementsFromCodeRelatedToIssue() {
		LOGGER.info("RequirementsDashboard getDecisionKnowledgeElementsFromCodeRelatedToIssue 6");
		// Extracts Decision Knowledge from Code Comments AND Commits related to the
		// given Issue
		Map<String, Map<String, List<KnowledgeElement>>> finalMap = new HashMap<String, Map<String, List<KnowledgeElement>>>();
		GitDecXtract gitExtract = new GitDecXtract(projectKey);
		GitClient gitClient = new GitClient(projectKey);
		for (Issue issue : jiraIssues) {
			Map<String, List<KnowledgeElement>> resultMap = new HashMap<String, List<KnowledgeElement>>();
			List<KnowledgeElement> allGatheredCommitElements = new ArrayList<>();
			List<KnowledgeElement> allGatheredCodeElements = new ArrayList<>();

			String filter = issue.getKey().toUpperCase() + "\\.|" + issue.getKey().toUpperCase() + "$|"
					+ issue.getKey().toUpperCase() + "\\-";
			List<Ref> branches = gitClient.getAllRemoteBranches();
			Pattern filterPattern = Pattern.compile(filter, Pattern.CASE_INSENSITIVE);
			if (branches.isEmpty()) {
				return null;
			}
			for (Ref branch : branches) {
				String branchName = branch.getName();
				Matcher branchMatcher = filterPattern.matcher(branchName);
				if (branchMatcher.find()) {
					List<KnowledgeElement> gatheredCommitElements = new ArrayList<>();
					List<KnowledgeElement> gatheredCodeElements = new ArrayList<>();
					List<RevCommit> featureCommits = gitClient.getFeatureBranchCommits(branch);
					if (featureCommits == null || featureCommits.size() == 0) {
						break;
					} else {
						for (RevCommit commit : featureCommits) {
							gatheredCommitElements.addAll(gitExtract.getElementsFromMessage(commit));
						}
						allGatheredCommitElements.addAll(gatheredCommitElements);
						RevCommit baseCommit = featureCommits.get(featureCommits.size() - 1);
						RevCommit lastFeatureBranchCommit = featureCommits.get(0);
						gatheredCodeElements
								.addAll(gitExtract.getElementsFromCode(baseCommit, lastFeatureBranchCommit, branch));
						allGatheredCodeElements.addAll(gatheredCodeElements);
					}
				}
			}
			resultMap.put("Commit", allGatheredCommitElements);
			resultMap.put("Code", allGatheredCodeElements);
			finalMap.put(issue.getKey(), resultMap);
		}
		gitClient.closeAll();
		gitExtract.close();
		return finalMap;
	}

	public Map<String, Integer> numberOfCommentsPerIssue() {
		LOGGER.info("RequirementsDashboard numberOfCommentsPerIssue <1");
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
		LOGGER.info("RequirementsDashboard getNumberOfDecisionKnowledgeElementsForJiraIssues 3 2");

		Map<String, Integer> numberOfSentencesPerIssue = new HashMap<String, Integer>();
		for (Issue jiraIssue : jiraIssues) {
			int numberOfElements = 0;
			List<KnowledgeElement> elements = KnowledgePersistenceManager.getOrCreate(projectKey)
					.getJiraIssueTextManager().getElementsInJiraIssue(jiraIssue.getId());
			if (jiraIssue.getIssueType().getName().equals(type.toString())) {
				numberOfElements++;
			}
			for (KnowledgeElement element : elements) {
				if (element.getType().equals(type)) {
					numberOfElements++;
				}
			}
			if (linkDistance >= 1 && extractedIssueRelatedElements != null
					&& extractedIssueRelatedElements.get(jiraIssue.getKey()) != null
					&& extractedIssueRelatedElements.get(jiraIssue.getKey()).get("Commit") != null) {
				for (KnowledgeElement element : extractedIssueRelatedElements.get(jiraIssue.getKey())
						.get("Commit")) {
					if (element.getType().equals(type)) {
						numberOfElements++;
					}
				}
			}
			if (linkDistance >= 2 && extractedIssueRelatedElements != null
					&& extractedIssueRelatedElements.get(jiraIssue.getKey()) != null
					&& extractedIssueRelatedElements.get(jiraIssue.getKey()).get("Code") != null) {
				for (KnowledgeElement element : extractedIssueRelatedElements.get(jiraIssue.getKey()).get("Code")) {
					if (element.getType().equals(type)) {
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
		if (ConfigPersistenceManager.isKnowledgeExtractedFromGit(projectKey)) {
			GitCodeClassExtractor extract = new GitCodeClassExtractor(projectKey);
			summaryMap.put("Code Classes", extract.getNumberOfCodeClasses());
			extract.close();
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
		numberOfRelevantSentences.put("Relevant Comment", isRelevant);
		numberOfRelevantSentences.put("Irrelevant Comment", isIrrelevant);
		return numberOfRelevantSentences;
	}

	public Map<String, String> getLinksToIssueTypeMap(KnowledgeType knowledgeType, int linkDistance) {
		LOGGER.info("RequirementsDashboard getLinksToIssueTypeMap 1 3");
		Map<String, String> result = new LinkedHashMap<String, String>();
		String type = JiraIssueTypeGenerator.getJiraIssueTypeName(issueTypeId);
		String withLink = "";
		String withoutLink = "";
		for (Issue jiraIssue : jiraIssues) {
			if (jiraIssue.getIssueTypeId() != null && jiraIssue.getIssueTypeId().equals(issueTypeId)) {
				int numberOfElements = 0;
				List<KnowledgeElement> elements = KnowledgePersistenceManager.getOrCreate(projectKey)
						.getJiraIssueTextManager().getElementsInJiraIssue(jiraIssue.getId());
				for (KnowledgeElement element : elements) {
					if (element.getType().equals(knowledgeType)) {
						numberOfElements++;
					}
				}
				if (linkDistance >= 1 && extractedIssueRelatedElements != null
						&& extractedIssueRelatedElements.get(jiraIssue.getKey()) != null
						&& extractedIssueRelatedElements.get(jiraIssue.getKey()).get("Commit") != null) {
					for (KnowledgeElement element : extractedIssueRelatedElements.get(jiraIssue.getKey())
							.get("Commit")) {
						if (element.getType().equals(knowledgeType)) {
							numberOfElements++;
						}
					}
				}
				if (linkDistance >= 2 && extractedIssueRelatedElements != null
						&& extractedIssueRelatedElements.get(jiraIssue.getKey()) != null
						&& extractedIssueRelatedElements.get(jiraIssue.getKey()).get("Code") != null) {
					for (KnowledgeElement element : extractedIssueRelatedElements.get(jiraIssue.getKey()).get("Code")) {
						if (element.getType().equals(knowledgeType)) {
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

	private boolean checkEqualIssueTypeIssue(IssueType issueType2) {
		if (issueType2 == null) {
			return false;
		}

		String jiraIssueTypeName = JiraIssueTypeGenerator.getJiraIssueTypeName(issueTypeId);
		return issueType2.getName().equalsIgnoreCase(jiraIssueTypeName);
	}
}
