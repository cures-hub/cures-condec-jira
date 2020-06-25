package de.uhd.ifi.se.decision.management.jira.consistency;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.link.LinkCollection;
import de.uhd.ifi.se.decision.management.jira.consistency.implementation.TextualSimilarityCIP;
import de.uhd.ifi.se.decision.management.jira.consistency.implementation.TimeCIP;
import de.uhd.ifi.se.decision.management.jira.consistency.implementation.TracingCIP;
import de.uhd.ifi.se.decision.management.jira.consistency.implementation.UserCIP;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.ConsistencyPersistenceHelper;
import org.ofbiz.core.entity.GenericEntityException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ContextInformation {
	private Issue issue;
	private List<ContextInformationProvider> cips;


	public ContextInformation(Issue issue) {
		this.issue = issue;
		// Add context information providers
		this.cips = new ArrayList<>();
		this.cips.add(new TextualSimilarityCIP());
		this.cips.add(new TracingCIP());
		this.cips.add(new TimeCIP());
		this.cips.add(new UserCIP());

	}

	public ContextInformation(String issueKey) {
		this(ComponentAccessor.getIssueManager().getIssueByCurrentKey(issueKey));
	}

	public Collection<Issue> getLinkedIssues() {
		Collection<Issue> linkedIssues = new ArrayList<>();
		LinkCollection linkCollection = ComponentAccessor.getIssueLinkManager().getLinkCollectionOverrideSecurity(this.issue);
		if (linkCollection != null) {
			linkedIssues = linkCollection.getAllIssues();
		}
		return linkedIssues;
	}

	public Collection<Issue> getDiscardedSuggestionIssues() {
		return ConsistencyPersistenceHelper.getDiscardedSuggestions(this.issue);
	}

	public Collection<LinkSuggestion> getLinkSuggestions() throws GenericEntityException {
		//Add all issues of project to projectIssues set
		Set<Issue> projectIssues = new HashSet<>(this.getAllIssuesForProject(this.issue.getProjectId()));

		//calculate context score
		Collection<LinkSuggestion> linkSuggestionScores = this.calculateNewCriScores(projectIssues);

		//get filtered issues
		Set<Issue> filteredIssues = this.filterIssues(projectIssues);

		//retain scores of filtered issues
		return linkSuggestionScores
			.stream()
			// issue was not filtered out
			.filter(linkSuggestion -> filteredIssues.contains(linkSuggestion.getTargetIssue()))
			// the probability is higher or equal to the minimum probability set by the admin for the project
			.filter(linkSuggestion -> linkSuggestion.getTotalScore() >= ConfigPersistenceManager.getMinLinkSuggestionScore(this.issue.getProjectObject().getKey()))
			.collect(Collectors.toCollection(ArrayList::new));
	}

	private Set<Issue> filterIssues(Set<Issue> projectIssues) {
		//Create union of all issues to be filtered out.
		Set<Issue> filteredIssues = new HashSet<>(projectIssues);
		Set<Issue> filterOutIssues = new HashSet<>(this.getLinkedIssues());
		filterOutIssues.addAll(this.getDiscardedSuggestionIssues());
		filterOutIssues.add(this.issue);

		//Calculate difference between all issues of project and the issues that need to be filtered out.
		filteredIssues.removeAll(filterOutIssues);

		return filteredIssues;
	}

	private Collection<LinkSuggestion> calculateNewCriScores(Set<Issue> projectIssues) {
		// init the link suggestions
		Map<String, LinkSuggestion> linkSuggestions = new HashMap<>();
		for (Issue otherIssue : projectIssues) {
			linkSuggestions.put(otherIssue.getKey(), new LinkSuggestion(this.issue, otherIssue));
		}

		for (ContextInformationProvider cip : this.cips) {
			Map<String, Double> individualScores = new HashMap<>();
			for (Issue otherIssue : projectIssues) {
				individualScores.put(otherIssue.getKey(), cip.assessRelation(this.issue, otherIssue));
			}
			/*
			Double sumOfIndividualScoresForCurrentCip = individualScores.values()
				.stream()
				.mapToDouble(Double::doubleValue)
				.sum();
			 */
			Double maxOfIndividualScoresForCurrentCip = individualScores.values()
				.stream()
				.mapToDouble(Double::doubleValue)
				.max().orElse(1.0);

			if (maxOfIndividualScoresForCurrentCip == 0) {
				maxOfIndividualScoresForCurrentCip = 1.;
			}
			Double finalMaxOfIndividualScoresForCurrentCip = maxOfIndividualScoresForCurrentCip;


			// for this purpose it might be better to divide by max value.
			individualScores.entrySet()
				.stream()
				.forEach(score -> {
					LinkSuggestion linkSuggestion = linkSuggestions.get(score.getKey());
					linkSuggestion.addToScore(score.getValue() / finalMaxOfIndividualScoresForCurrentCip, cip.getName());//sumOfIndividualScoresForCurrentCip);
				});
		}

		return linkSuggestions.values();
	}

	public Collection<Issue> getAllIssuesForProject(Long projectId) throws GenericEntityException {
		Collection<Issue> issuesOfProject = new ArrayList<>();
		Collection<Long> issueIds = ComponentAccessor.getIssueManager().getIssueIdsForProject(projectId);

		for (Long issueId : issueIds) {
			issuesOfProject.add(ComponentAccessor.getIssueManager().getIssueObject(issueId));
		}
		return issuesOfProject;
	}
}
