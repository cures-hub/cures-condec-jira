package de.uhd.ifi.se.decision.management.jira.model.knowledgegraph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.git.model.CodeComment;
import de.uhd.ifi.se.decision.management.jira.git.parser.RationaleFromCodeCommentParser;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;
import net.java.ao.test.jdbc.NonTransactional;

public class TestAddElementsNotInDatabase extends TestSetUp {

	private KnowledgeGraph graph;

	@Before
	public void setUp() {
		init();
		graph = KnowledgeGraph.getInstance("TEST");
	}

	@Test
	@NonTransactional
	public void testTwoDecisionProblemsWithManyDecisionKnowledgeElements() {
		CodeComment codeComment = new CodeComment("", 0, 1);
		codeComment.setCommentContent("/**" + //
				"* @issue How to present related knowledge and change impact to developers?\n" + //
				"* @alternative Present related knowledge and change impact as a list of\n" + //
				"*              proposals.\n" + //
				"* @con Would mislead developers. Developers associate content assist with\n" + //
				"*      auto-completion and proposals for bug-fixes.\n" + //
				"* @decision Present related knowledge and change impact in dedicated views!\n" + //
				"*\n" + //
				"* @issue How to trigger decision exploration and change impact analysis?\n" + //
				"* @alternative Content assist invocation triggers decision exploration view and\n" + //
				"*              change impact analysis view\n" + //
				"* @con Would mislead developers. Developers associate content assist with\n" + //
				"*      auto-completion and proposals for bug-fixes.\n" + //
				"* @decision Use menu items in context menu to trigger decision exploration and\n" + //
				"*           change impact analysis!\n" + //
				"*/");
		List<KnowledgeElement> elementsFound = new RationaleFromCodeCommentParser()
				.getRationaleElementsFromCodeComment(codeComment);
		assertEquals(8, elementsFound.size());
		assertEquals("Use menu items in context menu to trigger decision exploration and change impact analysis!",
				elementsFound.get(7).getSummary());
		elementsFound.forEach(element -> element.setProject("TEST"));

		graph.addElementsNotInDatabase(KnowledgeElements.getTestKnowledgeElement(), elementsFound);

		KnowledgeElement firstIssue = graph
				.getElementsNotInDatabaseBySummary("How to present related knowledge and change impact to developers?");
		KnowledgeElement firstDecision = graph
				.getElementsNotInDatabaseBySummary("Present related knowledge and change impact in dedicated views!");
		assertTrue(graph.containsEdge(firstDecision, firstIssue));

		KnowledgeElement secondIssue = graph
				.getElementsNotInDatabaseBySummary("How to trigger decision exploration and change impact analysis?");
		KnowledgeElement secondAlternative = graph.getElementsNotInDatabaseBySummary(
				"Content assist invocation triggers decision exploration view and change impact analysis view");
		assertTrue(graph.containsEdge(secondAlternative, secondIssue));
		KnowledgeElement secondDecision = graph.getElementsNotInDatabaseBySummary(
				"Use menu items in context menu to trigger decision exploration and change impact analysis!");
		assertNotNull(secondDecision);
		assertTrue(graph.containsEdge(secondDecision, secondIssue));
	}
}
