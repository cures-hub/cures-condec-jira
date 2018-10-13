package de.uhd.ifi.se.decision.management.jira.extraction.view.reports;

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

import de.uhd.ifi.se.decision.management.jira.extraction.model.GenericLink;
import de.uhd.ifi.se.decision.management.jira.extraction.model.Sentence;
import de.uhd.ifi.se.decision.management.jira.extraction.persistence.ActiveObjectsManager;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.view.treant.Treant;
import org.ofbiz.core.entity.GenericEntityException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DecisionKnowledgeReport extends AbstractReport {

	@JiraImport
	private final ProjectManager projectManager;

	private Long projectId;

	private KnowledgeType rootType;

	public DecisionKnowledgeReport(ProjectManager projectManager) {
		this.projectManager = projectManager;
	}

	public String generateReportHtml(ProjectActionSupport action, Map params) throws Exception {

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
		velocityParams.put("map", Map.class);

		// get Number of commits per Issue TODO:Access commit DB
		List<Integer> numCommitsPerIssue = getNumberOfCommitsPerIssue(action.getLoggedInUser());
		velocityParams.put("numCommitsPerIssue", numCommitsPerIssue);

		// Get associated Knowledge Types in Sentences per Issue
		Map<String, Integer> numKnowledgeTypesPerIssue = getDecKnowElementsPerIssue();
		velocityParams.put("numKnowledgeTypesPerIssue", numKnowledgeTypesPerIssue);

		// Get types of decisions and alternatives linkes to Issue (e.g. has decision
		// but no alternative)
		Map<String, Integer> numLinksToIssue = getAlternativeDecisionPerIssue();
		velocityParams.put("numLinksToIssue", numLinksToIssue);

		// Get Number of Alternatives With Arguments
		Map<String, Integer> numAlternativeWoArgument = getAlternativeArguments();
		velocityParams.put("numAlternativeWoArgument", numAlternativeWoArgument);

		// Get Link Distance
		List<Integer> numLinkDistance = getLinkDistance();
		velocityParams.put("numLinkDistance", numLinkDistance);

		return descriptor.getHtml("view", velocityParams);
	}

	private Map<String, Integer> getNumberOfRelevantSentences(ApplicationUser loggedInUser) {
		Map<String, Integer> result = new HashMap<>();
		int isRelevant = 0;
		int isNotRelevant = 0;
		SearchResults projectIssues = null;
		try {
			projectIssues = getIssuesForThisProject(loggedInUser);
		} catch (SearchException e) {
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

		SearchResults projectIssues = null;
		try {
			projectIssues = getIssuesForThisProject(loggedInUser);
		} catch (SearchException e) {
			return result;
		}
		String projectKey = ComponentAccessor.getProjectManager().getProjectObj(this.projectId).getKey();
		for (Issue currentIssue : projectIssues.getIssues()) {
			List<DecisionKnowledgeElement> elements = ActiveObjectsManager.getElementsForIssue(currentIssue.getId(),
					projectKey);
			result.add(elements.size());
		}
		return result;
	}

	private List<Integer> getLinkDistance() throws GenericEntityException {
		List<Integer> linkDistances = new ArrayList<>();

		List<DecisionKnowledgeElement> listOfIssues = ActiveObjectsManager
				.getAllElementsFromAoByType(projectManager.getProjectObj(this.projectId).getKey(), this.rootType);

		for (DecisionKnowledgeElement currentAlternative : listOfIssues) {
			Treant treant = new Treant(currentAlternative.getProject().getProjectKey(), currentAlternative.getKey(),
					100);
			linkDistances.add(treant.getRealDepth());
		}

		return linkDistances;
	}

	private Map<String, Integer> getAlternativeArguments() {
		int alternativesHaveArgument = 0;
		int alternativesHaveNoArgument = 0;

		List<DecisionKnowledgeElement> listOfIssues = ActiveObjectsManager.getAllElementsFromAoByType(
				projectManager.getProjectObj(this.projectId).getKey(), KnowledgeType.ALTERNATIVE);

		for (DecisionKnowledgeElement currentAlternative : listOfIssues) {
			List<GenericLink> links = ActiveObjectsManager.getGenericLinksForElement("s" + currentAlternative.getId(),
					false);
			boolean hasArgument = false;
			for (GenericLink link : links) {
				DecisionKnowledgeElement dke = link.getOpposite("s" + currentAlternative.getId());
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

	private Map<String, Integer> getAlternativeDecisionPerIssue() throws SearchException {
		Integer[] statistics = new Integer[4];
		Arrays.fill(statistics, 0);
		List<DecisionKnowledgeElement> listOfIssues = ActiveObjectsManager
				.getAllElementsFromAoByType(projectManager.getProjectObj(this.projectId).getKey(), KnowledgeType.ISSUE);

		for (DecisionKnowledgeElement issue : listOfIssues) {
			List<GenericLink> links = ActiveObjectsManager.getGenericLinksForElement("s" + issue.getId(), false);
			boolean hasAlternative = false;
			boolean hasDecision = false;

			for (GenericLink link : links) {
				DecisionKnowledgeElement dke = link.getOpposite("s" + issue.getId());
				if (dke instanceof Sentence && dke.getType().equals(KnowledgeType.ALTERNATIVE)) {
					hasAlternative = true;
				} else if (dke instanceof Sentence && dke.getType().equals(KnowledgeType.DECISION)) {
					hasDecision = true;
				}
			}
			if (hasAlternative && hasDecision) {
				statistics[0] = statistics[0] + 1;
			} else if (hasAlternative && !hasDecision) {
				statistics[1] = statistics[1] + 1;
			} else if (!hasAlternative && hasDecision) {
				statistics[2] = statistics[2] + 1;
			} else if (!hasAlternative && !hasDecision) {
				statistics[3] = statistics[3] + 1;
			}
		}
		// Hashmaps as counter suck
		Map<String, Integer> dkeCount = new HashMap<String, Integer>();
		dkeCount.put("Has Alt and Dec", statistics[0]);
		dkeCount.put("Has Alt but no Dec", statistics[1]);
		dkeCount.put("Has Dec but no Alt", statistics[2]);
		dkeCount.put("Has no Dec and Alt", statistics[3]);

		return dkeCount;
	}

	private Map<String, Integer> getDecKnowElementsPerIssue() throws SearchException {
		Map<String, Integer> dkeCount = new HashMap<String, Integer>();

		for (KnowledgeType type : KnowledgeType.getDefaulTypes()) {
			dkeCount.put(type.toString(), ActiveObjectsManager
					.getAllElementsFromAoByType(projectManager.getProjectObj(this.projectId).getKey(), type).size());
		}
		return dkeCount;
	}

	private List<Integer> getNumberOfCommitsPerIssue(ApplicationUser loggedInUser) throws SearchException {
		JqlClauseBuilder jqlClauseBuilder = JqlQueryBuilder.newClauseBuilder();
		SearchService searchService = ComponentAccessor.getComponentOfType(SearchService.class);

		com.atlassian.query.Query query = jqlClauseBuilder.project(this.projectId).buildQuery();
		SearchResults searchResults = null;

		searchResults = searchService.search(loggedInUser, query, PagerFilter.getUnlimitedFilter());

		List<Integer> commentList = new ArrayList<>();
		for (Issue issue : searchResults.getIssues()) {
			commentList.add(ComponentAccessor.getCommentManager().getComments(issue).size());
		}
		return commentList;
	}

	private List<Integer> getNumberOfCommentsPerIssue(ApplicationUser user) throws SearchException {
		SearchResults searchResults = getIssuesForThisProject(user);
		List<Integer> commentList = new ArrayList<>();
		for (Issue issue : searchResults.getIssues()) {
			commentList.add(ComponentAccessor.getCommentManager().getComments(issue).size());
		}
		return commentList;
	}

	private SearchResults getIssuesForThisProject(ApplicationUser user) throws SearchException {
		// user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();
		JqlClauseBuilder jqlClauseBuilder = JqlQueryBuilder.newClauseBuilder();
		SearchService searchService = ComponentAccessor.getComponentOfType(SearchService.class);
		com.atlassian.query.Query query = jqlClauseBuilder.project(this.projectId).buildQuery();

		return searchService.search(user, query, PagerFilter.getUnlimitedFilter());
	}

	public void validate(ProjectActionSupport action, Map params) {
		this.projectId = ParameterUtils.getLongParam(params, "selectedProjectId");
		this.rootType = KnowledgeType.getKnowledgeType(ParameterUtils.getStringParam(params, "rootType"));
	}
}