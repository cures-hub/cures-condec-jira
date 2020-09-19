package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.jgrapht.Graphs;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;
import net.java.ao.test.jdbc.NonTransactional;

public class TestAlternativeCompletenessCheck extends TestSetUp {

	private List<KnowledgeElement> elements;
	private KnowledgeElement alternative;
	private ApplicationUser user;
	private AlternativeCompletenessCheck alternativeCompletenessCheck;

	@Before
	public void setUp() {
		init();
		user = JiraUsers.SYS_ADMIN.getApplicationUser();
		elements = KnowledgeElements.getTestKnowledgeElements();
		alternative = elements.get(5);
		alternativeCompletenessCheck = new AlternativeCompletenessCheck();
	}

	@Test
	@NonTransactional
	public void testIsLinkedToIssue() {
		assertEquals(KnowledgeType.ALTERNATIVE, alternative.getType());
		assertEquals(3, alternative.getId());
		KnowledgeElement issue = elements.get(3);
		assertEquals(KnowledgeType.ISSUE, issue.getType());
		assertEquals(2, issue.getId());
		assertNotNull(alternative.getLink(issue));
		assertTrue(alternativeCompletenessCheck.execute(alternative));
	}

	@Test
	@NonTransactional
	public void testIsNotLinkedToIssue() {
		assertEquals(KnowledgeType.ALTERNATIVE, alternative.getType());
		assertEquals(3, alternative.getId());
		KnowledgeElement issue = elements.get(3);
		assertEquals(KnowledgeType.ISSUE, issue.getType());
		assertEquals(2, issue.getId());
		Link linkToIssue = alternative.getLink(issue);
		KnowledgePersistenceManager.getOrCreate("TEST").deleteLink(linkToIssue, user);
		linkToIssue = alternative.getLink(issue);
		assertNull(linkToIssue);
		KnowledgeGraph graph = KnowledgeGraph.getOrCreate(alternative.getProject());
		assertEquals(3, Graphs.neighborSetOf(graph, alternative).size());
		assertFalse(alternativeCompletenessCheck.execute(alternative));
	}

	@Test
	@NonTransactional
	public void testIsLinkedToArgument() {
		// set criteria "alternative has to be linked to argument" in definition of done
		DefinitionOfDone definitionOfDone = new DefinitionOfDone();
		definitionOfDone.setAlternativeLinkedToArgument(true);
		ConfigPersistenceManager.setDefinitionOfDone("TEST", definitionOfDone);
		assertTrue(alternativeCompletenessCheck.execute(alternative));

		// unlink argument from alternative
		KnowledgeElement argument = elements.get(8);
		KnowledgeGraph.getOrCreate("TEST").removeEdge(argument, alternative);
		assertNull(alternative.getLink(argument));
		assertFalse(alternativeCompletenessCheck.execute(alternative));
	}

	@Test
	@NonTransactional
	public void testIsLinkedToProArgument() {
		// set criteria "alternative has to be linked to argument" in definition of done
		DefinitionOfDone definitionOfDone = new DefinitionOfDone();
		definitionOfDone.setAlternativeLinkedToArgument(true);
		ConfigPersistenceManager.setDefinitionOfDone("TEST", definitionOfDone);

		// link alternative to a pro-argument
		KnowledgeElement proArgument = JiraIssues.addElementToDataBase(342, "pro");
		KnowledgePersistenceManager.getOrCreate("TEST").insertLink(alternative, proArgument, user);
		assertNotNull(alternative.getLink(proArgument));
		assertTrue(alternativeCompletenessCheck.execute(alternative));
	}

	@Test
	@NonTransactional
	public void testIsLinkedToConArgument() {
		// set criteria "alternative has to be linked to argument" in definition of done
		DefinitionOfDone definitionOfDone = new DefinitionOfDone();
		definitionOfDone.setAlternativeLinkedToArgument(true);
		ConfigPersistenceManager.setDefinitionOfDone("TEST", definitionOfDone);

		// link alternative to a con-argument
		KnowledgeElement conArgument = JiraIssues.addElementToDataBase(344, "con");
		KnowledgePersistenceManager.getOrCreate("TEST").insertLink(alternative, conArgument, user);
		assertNotNull(alternative.getLink(conArgument));
		assertTrue(alternativeCompletenessCheck.execute(alternative));
	}

	@After
	public void tearDown() {
		// restore default
		ConfigPersistenceManager.setDefinitionOfDone("TEST", new DefinitionOfDone());
	}
}
