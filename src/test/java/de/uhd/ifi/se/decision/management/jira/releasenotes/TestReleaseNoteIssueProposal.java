package de.uhd.ifi.se.decision.management.jira.releasenotes;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.releasenotes.impl.ReleaseNoteIssueProposalImpl;
import org.junit.Before;
import org.junit.Test;

import java.util.EnumMap;

import static org.junit.Assert.assertEquals;

public class TestReleaseNoteIssueProposal {
	private long idOfDKElement;
	private ReleaseNoteIssueProposal proposal;
	private double rating;

	@Before
	public void setUp() {
		idOfDKElement = 14;
		DecisionKnowledgeElement dkElement = new DecisionKnowledgeElementImpl();
		dkElement.setId(idOfDKElement);
		proposal = new ReleaseNoteIssueProposalImpl(dkElement, 3);
		rating = 54.21;
		proposal.setRating(rating);
	}

	@Test
	public void testGetDecisionKnowledgeElement() {
		assertEquals(idOfDKElement, proposal.getDecisionKnowledgeElement().getId());
	}

	@Test
	public void testSetDecisionKnowledgeElement() {
		DecisionKnowledgeElement dkElement2 = new DecisionKnowledgeElementImpl();
		dkElement2.setId(15);
		proposal.setDecisionKnowledgeElement(dkElement2);
		assertEquals(15, proposal.getDecisionKnowledgeElement().getId());
	}

	@Test
	public void testGetRating() {
		assertEquals(rating, proposal.getRating(), 0.0);
	}

	@Test
	public void testSetRating() {
		double rating2 = 32.653;
		proposal.setRating(rating2);
		assertEquals(rating2, proposal.getRating(), 0.0);
	}

	@Test
	public void testGetTaskCriteriaPrioritisation() {
		assertEquals(8, proposal.getTaskCriteriaPrioritisation().size());

	}

	@Test
	public void testSetTaskCriteriaPrioritisation() {
		EnumMap<TaskCriteriaPrioritisation, Integer> taskCriteriaPrioritisation = TaskCriteriaPrioritisation.toIntegerEnumMap();
		taskCriteriaPrioritisation.put(TaskCriteriaPrioritisation.DAYS_COMPLETION, 135);
		proposal.setTaskCriteriaPrioritisation(taskCriteriaPrioritisation);
		assertEquals(135, proposal.getTaskCriteriaPrioritisation().get(TaskCriteriaPrioritisation.DAYS_COMPLETION), 0.0);
	}
}