package de.uhd.ifi.se.decision.management.jira.consistency.contextinformation;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.link.LinkCollection;
import de.uhd.ifi.se.decision.management.jira.consistency.suggestions.LinkSuggestion;
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

public class ContextInformation implements ContextInformationProvider {
	private Issue issue;
	private List<ContextInformationProvider> cips;
	private Map<String,LinkSuggestion> linkSuggestions;


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

	public Collection<LinkSuggestion> getLinkSuggestions()  {
		//Add all issues of project to projectIssues set
		Set<Issue> projectIssues = null;
		try {
			projectIssues = new HashSet<>(this.getAllIssuesForProject(this.issue.getProjectId()));
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}

		this.assessRelation(issue, new ArrayList<>(projectIssues));
		//calculate context score

		//get filtered issues
		Set<Issue> filteredIssues = this.filterIssues(projectIssues);

		//retain scores of filtered issues
		return this.linkSuggestions.values()
			.stream()
			// issue was not filtered out
			.filter(linkSuggestion -> filteredIssues.contains(linkSuggestion.getTargetIssue()))
			// the probability is higher or equal to the minimum probability set by the admin for the project
			.filter(linkSuggestion -> linkSuggestion.getTotalScore() >= ConfigPersistenceManager.getMinLinkSuggestionScore(this.issue.getProjectObject().getKey()))
			.collect(Collectors.toCollection(ArrayList::new));
	}

	private Collection<LinkSuggestion> getCalculatedLinkSuggestions() {
		return this.linkSuggestions.values();
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

	@Override
	public void assessRelation(Issue baseIssue, List<Issue> issuesToTest) {
		// init the link suggestions
		this.linkSuggestions = new HashMap<>();
		for (Issue otherIssue : issuesToTest) {
			linkSuggestions.put(otherIssue.getKey(), new LinkSuggestion(this.issue, otherIssue));
		}

		this.cips.forEach((cip) -> {
			cip.assessRelation(this.issue, new ArrayList<>(issuesToTest));;

			Double maxOfIndividualScoresForCurrentCip = cip.getLinkSuggestions()
				.stream()
				.mapToDouble(LinkSuggestion::getTotalScore)
				.max().orElse(1.0);

			if (maxOfIndividualScoresForCurrentCip == 0) {
				maxOfIndividualScoresForCurrentCip = 1.;
			}

			Double finalMaxOfIndividualScoresForCurrentCip = maxOfIndividualScoresForCurrentCip;
			// Divide each score by the max value to scale it to [0,1]
			cip.getLinkSuggestions()
				.forEach(score -> {
					LinkSuggestion linkSuggestion = this.linkSuggestions.get(score.getTargetIssue().getKey());
					linkSuggestion.addToScore(score.getTotalScore() / finalMaxOfIndividualScoresForCurrentCip, cip.getName());//sumOfIndividualScoresForCurrentCip);
				});

		});
	}

	public Collection<Issue> getAllIssuesForProject(Long projectId) throws GenericEntityException {
		Collection<Issue> issuesOfProject = new ArrayList<>();
		Collection<Long> issueIds = ComponentAccessor.getIssueManager().getIssueIdsForProject(projectId);

		for (Long issueId : issueIds) {
			issuesOfProject.add(ComponentAccessor.getIssueManager().getIssueObject(issueId));
		}
		return issuesOfProject;
	}

	@Override
	public String getId() {
		return "BaseCalculation";
	}

	@Override
	public String getName() {
		return "BaseCalculation";
	}



}
