package de.uhd.ifi.se.decision.management.jira.consistency.contextinformation;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.link.LinkCollection;
import de.uhd.ifi.se.decision.management.jira.consistency.suggestions.LinkSuggestion;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
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
	private KnowledgeElement element;
	private List<ContextInformationProvider> cips;
	private Map<String,LinkSuggestion> linkSuggestions;


	public ContextInformation(Issue element) {
		this(new KnowledgeElement(element));
	}

	public ContextInformation(KnowledgeElement element) {
		this.element = element;
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

	public Collection<KnowledgeElement> getLinkedIssues() {
		Collection<KnowledgeElement> linkedKnowledgeElements = new ArrayList<>();
		LinkCollection linkCollection = ComponentAccessor.getIssueLinkManager().getLinkCollectionOverrideSecurity(this.element.getJiraIssue());
		if (linkCollection != null) {
			for (Issue i : linkCollection.getAllIssues()){
				linkedKnowledgeElements.add(new KnowledgeElement(i));
			}
		}
		return linkedKnowledgeElements;
	}

	public Collection<KnowledgeElement> getDiscardedSuggestionIssues() {
		return ConsistencyPersistenceHelper
			.getDiscardedLinkSuggestions(this.element.getJiraIssue())
			.stream()
			.map(KnowledgeElement::new)
			.collect(Collectors.toList());
	}

	public Collection<LinkSuggestion> getLinkSuggestions()  {
		//Add all issues of project to projectIssues set
		Set<KnowledgeElement> projectIssues = null;
		try {
			projectIssues = new HashSet<>(this.getAllIssuesForProject(this.element.getJiraIssue().getProjectId()));
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}

		this.assessRelation(element, new ArrayList<>(projectIssues));
		//calculate context score

		//get filtered issues
		Set<KnowledgeElement> filteredIssues = this.filterIssues(projectIssues);

		//retain scores of filtered issues
		return this.linkSuggestions.values()
			.stream()
			// issue was not filtered out
			.filter(linkSuggestion -> filteredIssues.contains(linkSuggestion.getTargetElement()))
			// the probability is higher or equal to the minimum probability set by the admin for the project
			.filter(linkSuggestion -> linkSuggestion.getTotalScore() >= ConfigPersistenceManager.getMinLinkSuggestionScore(this.element.getProject().getProjectKey()))
			.collect(Collectors.toCollection(ArrayList::new));
	}

	private Collection<LinkSuggestion> getCalculatedLinkSuggestions() {
		return this.linkSuggestions.values();
	}

	private Set<KnowledgeElement> filterIssues(Set<KnowledgeElement> projectIssues) {
		//Create union of all issues to be filtered out.
		Set<KnowledgeElement> filteredIssues = new HashSet<>(projectIssues);
		Set<KnowledgeElement> filterOutIssues = new HashSet<>(this.getLinkedIssues());
		filterOutIssues.addAll(this.getDiscardedSuggestionIssues());
		filterOutIssues.add(this.element);

		//Calculate difference between all issues of project and the issues that need to be filtered out.
		filteredIssues.removeAll(filterOutIssues);

		return filteredIssues;
	}

	@Override
	public void assessRelation(KnowledgeElement baseElement, List<KnowledgeElement> knowledgeElements) {
		// init the link suggestions
		this.linkSuggestions = new HashMap<>();
		for (KnowledgeElement otherIssue : knowledgeElements) {
			linkSuggestions.put(otherIssue.getKey(), new LinkSuggestion(this.element, otherIssue));
		}

		this.cips.forEach((cip) -> {
			cip.assessRelation(this.element, new ArrayList<>(knowledgeElements));;

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
					LinkSuggestion linkSuggestion = this.linkSuggestions.get(score.getTargetElement().getKey());
					linkSuggestion.addToScore(score.getTotalScore() / finalMaxOfIndividualScoresForCurrentCip, cip.getName());//sumOfIndividualScoresForCurrentCip);
				});

		});
	}

	public Collection<KnowledgeElement> getAllIssuesForProject(Long projectId) throws GenericEntityException {
		Collection<KnowledgeElement> issuesOfProject = new ArrayList<>();
		Collection<Long> issueIds = ComponentAccessor.getIssueManager().getIssueIdsForProject(projectId);

		for (Long issueId : issueIds) {
			issuesOfProject.add(new KnowledgeElement(ComponentAccessor.getIssueManager().getIssueObject(issueId)));
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
