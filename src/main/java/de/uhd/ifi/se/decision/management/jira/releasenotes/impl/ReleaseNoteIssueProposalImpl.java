package de.uhd.ifi.se.decision.management.jira.releasenotes.impl;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.releasenotes.ReleaseNoteIssueProposal;
import de.uhd.ifi.se.decision.management.jira.releasenotes.TaskCriteriaPrioritisation;
import org.codehaus.jackson.annotate.JsonProperty;

import javax.xml.bind.annotation.XmlElement;
import java.util.EnumMap;


/**
 * Model class Release Note Issue Proposal
 * It saves the decision knowledge element, the final rating and the task criteria prioritisation metrics.
 */
public class ReleaseNoteIssueProposalImpl implements ReleaseNoteIssueProposal {

	private DecisionKnowledgeElement decisionKnowledgeElement;

	private EnumMap<TaskCriteriaPrioritisation, Integer> taskCriteriaPrioritisation;
	private double rating;

	/**
	 * Constructer to initialize default values and add count of DK
	 *
	 * @param decisionKnowledgeElement
	 * @param countDecisionKnowledge
	 */
	public ReleaseNoteIssueProposalImpl(DecisionKnowledgeElement decisionKnowledgeElement, int countDecisionKnowledge) {
		this.decisionKnowledgeElement = decisionKnowledgeElement;
		//set default values
		this.taskCriteriaPrioritisation = TaskCriteriaPrioritisation.toIntegerEnumMap();
		this.taskCriteriaPrioritisation.put(TaskCriteriaPrioritisation.COUNT_DECISION_KNOWLEDGE, countDecisionKnowledge);

	}

	/**
	 * @return decisionKnowledgeElement of the ReleaseNoteIssueProposal
	 */
	@Override
	@XmlElement(name = "decisionKnowledgeElement")
	public DecisionKnowledgeElement getDecisionKnowledgeElement() {
		return this.decisionKnowledgeElement;
	}

	/**
	 * @param decisionKnowledgeElement of the ReleaseNoteIssueProposal.
	 */
	@Override
	@JsonProperty("decisionKnowledgeElement")
	public void setDecisionKnowledgeElement(DecisionKnowledgeElement decisionKnowledgeElement) {
		this.decisionKnowledgeElement = decisionKnowledgeElement;
	}

	/**
	 * @return rating of the ReleaseNoteIssueProposal
	 */
	@Override
	@XmlElement(name = "rating")
	public double getRating() {
		return this.rating;
	}

	/**
	 * @param rating of the ReleaseNoteIssueProposal.
	 */
	@Override
	@JsonProperty("rating")
	public void setRating(double rating) {
		this.rating = rating;
	}

	/**
	 * Get criteria Prioritisation of the ReleaseNoteIssueProposal.
	 *
	 * @return taskCriteriaPrioritisation of the ReleaseNoteIssueProposal.
	 */
	@Override
	@XmlElement(name = "taskCriteriaPrioritisation")
	public EnumMap<TaskCriteriaPrioritisation, Integer> getTaskCriteriaPrioritisation() {
		return this.taskCriteriaPrioritisation;
	}

	/**
	 * set criteria Prioritisation of the ReleaseNoteIssueProposal.
	 *
	 * @param taskCriteriaPrioritisation of the ReleaseNoteIssueProposal.
	 */
	@Override
	public void setTaskCriteriaPrioritisation(EnumMap<TaskCriteriaPrioritisation, Integer> taskCriteriaPrioritisation) {
		this.taskCriteriaPrioritisation = taskCriteriaPrioritisation;
	}
}