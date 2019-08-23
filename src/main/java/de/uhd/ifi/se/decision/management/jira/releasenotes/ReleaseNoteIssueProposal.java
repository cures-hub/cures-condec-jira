package de.uhd.ifi.se.decision.management.jira.releasenotes;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.releasenotes.impl.ReleaseNoteIssueProposalImpl;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

import java.util.EnumMap;

/**
 * Interface Release Note Issue Proposal
 * It saves the decision knowledge element, the final rating and the task criteria prioritisation metrics.
 */
@JsonDeserialize(as = ReleaseNoteIssueProposalImpl.class)
public interface ReleaseNoteIssueProposal {

	/**
	 * Get the DecisionKnowledgeElement.
	 *
	 * @return DecisionKnowledgeElement of the ReleaseNoteIssueProposal.
	 */
	DecisionKnowledgeElement getDecisionKnowledgeElement();

	/**
	 * Set the DecisionKnowledgeElement.
	 *
	 * @param decisionKnowledgeElement of the ReleaseNoteIssueProposal.
	 */
	void setDecisionKnowledgeElement(DecisionKnowledgeElement decisionKnowledgeElement);

	/**
	 * Get rating of the ReleaseNoteIssueProposal.
	 *
	 * @return ratings of ReleaseNoteIssueProposal.
	 */
	double getRating();

	/**
	 * Set rating of the ReleaseNoteIssueProposal.
	 *
	 * @param rating of ReleaseNoteIssueProposal.
	 */
	void setRating(double rating);

	/**
	 * Get criteria prioritisation of the ReleaseNoteIssueProposal.
	 *
	 * @return taskCriteriaPrioritisation of ReleaseNoteIssueProposal.
	 */
	EnumMap<TaskCriteriaPrioritisation, Integer> getTaskCriteriaPrioritisation();

	/**
	 * Set criteria prioritisation of ReleaseNoteIssueProposal.
	 *
	 * @param taskCriteriaPrioritisation of ReleaseNoteIssueProposal.
	 */
	void setTaskCriteriaPrioritisation(EnumMap<TaskCriteriaPrioritisation, Integer> taskCriteriaPrioritisation);


}