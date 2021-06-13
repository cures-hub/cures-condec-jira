package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.jgrapht.Graphs;
import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;
import net.java.ao.test.jdbc.NonTransactional;

public class TestArgumentCompletenessCheck extends TestSetUp {

	private KnowledgeElement proArgument;
	private ApplicationUser user;
	private ArgumentCompletenessCheck argumentCompletenessCheck;

	@Before
	public void setUp() {
		init();
		user = JiraUsers.SYS_ADMIN.getApplicationUser();
		argumentCompletenessCheck = new ArgumentCompletenessCheck();
		proArgument = KnowledgeElements.getProArgument();
	}

	@Test
	@NonTransactional
	public void testIsLinkedToDecision() {
		assertEquals(KnowledgeType.ARGUMENT, proArgument.getType().replaceProAndConWithArgument());
		assertEquals(5, proArgument.getId());
		KnowledgeElement decision = KnowledgeElements.getDecision();
		assertEquals(KnowledgeType.DECISION, decision.getType());
		assertEquals(4, decision.getId());
		assertNotNull(proArgument.getLink(decision));
		assertTrue(argumentCompletenessCheck.execute(proArgument));
	}

	@Test
	@NonTransactional
	public void testIsLinkedToAlternative() {
		KnowledgeElement alternative = JiraIssues.addElementToDataBase(321, KnowledgeType.ALTERNATIVE);
		assertEquals(KnowledgeType.ARGUMENT, proArgument.getType().replaceProAndConWithArgument());
		KnowledgeElement proArgument = JiraIssues.addElementToDataBase(322, KnowledgeType.PRO);
		assertEquals(KnowledgeType.ALTERNATIVE, alternative.getType());
		KnowledgePersistenceManager.getOrCreate("TEST").insertLink(proArgument, alternative, user);
		assertNotNull(proArgument.getLink(alternative));
		assertTrue(argumentCompletenessCheck.execute(proArgument));
	}

	@Test
	@NonTransactional
	public void testIsNeitherLinkedToDecisionNorToAlternative() {
		assertEquals(KnowledgeType.ARGUMENT, proArgument.getType());
		assertEquals(5, proArgument.getId());
		KnowledgeElement decision = KnowledgeElements.getDecision();
		assertEquals(KnowledgeType.DECISION, decision.getType());
		assertEquals(4, decision.getId());

		Link linkToDecision = proArgument.getLink(decision);
		assertNotNull(linkToDecision);

		KnowledgeGraph.getInstance("TEST").removeEdge(linkToDecision);
		linkToDecision = proArgument.getLink(decision);
		assertNull(linkToDecision);

		KnowledgeGraph graph = KnowledgeGraph.getInstance(proArgument.getProject());
		assertFalse(graph.containsEdge(linkToDecision));
		assertEquals(2, Graphs.neighborSetOf(graph, proArgument).size());
		assertFalse(argumentCompletenessCheck.execute(proArgument));
	}

	@Test
	@NonTransactional
	public void testIsCompleteAccordingToSettings() {
		assertTrue(argumentCompletenessCheck.isCompleteAccordingToSettings());
	}

	@Test
	@NonTransactional
	public void testGetFailedCriteriaNeighbourDecision() {
		assertEquals(KnowledgeType.ARGUMENT, proArgument.getType().replaceProAndConWithArgument());
		assertEquals(5, proArgument.getId());
		KnowledgeElement decision = KnowledgeElements.getDecision();
		assertEquals(KnowledgeType.DECISION, decision.getType());
		assertEquals(4, decision.getId());
		assertNotNull(proArgument.getLink(decision));
		assertFalse(argumentCompletenessCheck.getFailedCriteria(proArgument).isEmpty());
	}

	@Test
	@NonTransactional
	public void testGetFailedCriteriaNeighbourAlternative() {
		KnowledgeElement alternative = JiraIssues.addElementToDataBase(321, KnowledgeType.ALTERNATIVE);
		assertEquals(KnowledgeType.ARGUMENT, proArgument.getType().replaceProAndConWithArgument());
		KnowledgeElement proArgument = JiraIssues.addElementToDataBase(322, KnowledgeType.PRO);
		assertEquals(KnowledgeType.ALTERNATIVE, alternative.getType());
		KnowledgePersistenceManager.getOrCreate("TEST").insertLink(proArgument, alternative, user);
		assertNotNull(proArgument.getLink(alternative));
		assertFalse(argumentCompletenessCheck.getFailedCriteria(proArgument).isEmpty());
	}

	@Test
	@NonTransactional
	public void testGetFailedCriteriaNeighbourNoDecisionOrAlternative() {
		assertEquals(KnowledgeType.ARGUMENT, proArgument.getType());
		assertEquals(5, proArgument.getId());
		KnowledgeElement decision = KnowledgeElements.getDecision();
		assertEquals(KnowledgeType.DECISION, decision.getType());
		assertEquals(4, decision.getId());

		Link linkToDecision = proArgument.getLink(decision);
		assertNotNull(linkToDecision);

		KnowledgeGraph.getInstance("TEST").removeEdge(linkToDecision);
		linkToDecision = proArgument.getLink(decision);
		assertNull(linkToDecision);

		KnowledgeGraph graph = KnowledgeGraph.getInstance(proArgument.getProject());
		assertFalse(graph.containsEdge(linkToDecision));
		assertEquals(2, Graphs.neighborSetOf(graph, proArgument).size());
		assertTrue(argumentCompletenessCheck.getFailedCriteria(proArgument).isEmpty());
	}
}