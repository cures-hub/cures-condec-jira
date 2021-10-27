package de.uhd.ifi.se.decision.management.jira.model.knowledgegraph;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import net.java.ao.test.jdbc.NonTransactional;

public class TestGetElements extends TestSetUp {

	protected static KnowledgeElement element;
	private KnowledgeGraph graph;

	@Before
	public void setUp() {
		init();
		graph = KnowledgeGraph.getInstance("TEST");
		element = JiraIssues.addElementToDataBase();
	}

	@Test
	@NonTransactional
	public void testElementsByType() {
		List<PartOfJiraIssueText> sentences = JiraIssues.getSentencesForCommentText(
				"some sentence in front. {issue} testobject {issue} some sentence in the back.");
		assertEquals(KnowledgeType.ISSUE, sentences.get(1).getType());

		List<KnowledgeElement> listWithObjects = graph.getElements(KnowledgeType.ISSUE);
		assertEquals(3, listWithObjects.size());
	}
}
