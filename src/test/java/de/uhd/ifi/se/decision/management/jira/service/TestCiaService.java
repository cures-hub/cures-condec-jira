package de.uhd.ifi.se.decision.management.jira.service;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import de.uhd.ifi.se.decision.management.jira.view.matrix.Matrix;
import de.uhd.ifi.se.decision.management.jira.view.treeviewer.TreeViewer;
import de.uhd.ifi.se.decision.management.jira.view.vis.VisGraph;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.Assert.assertEquals;

public class TestCiaService extends TestSetUp {

	protected static KnowledgeElement element;
	protected KnowledgeGraph graph;

	@Before
	public void setUp() {
		init();
		graph = new KnowledgeGraph("TEST");
		element = JiraIssues.addElementToDataBase();
	}

	@Test
	public void testCalculateGraphImpact() {
		FilterSettings settings = new FilterSettings("TEST", "");
		settings.setSelectedElement("TEST-1");
		Arrays.stream(KnowledgeType.values()).map(KnowledgeType::toString).forEach(
			System.out::println
		);

		VisGraph graph = CiaService.calculateGraphImpact(settings);
		assertEquals(10,graph.getNodes().size());
		assertEquals(16, graph.getEdges().size());
	}

	@Test
	public void testCalculateMatrixImpact() {
		FilterSettings settings = new FilterSettings("TEST", "");
		settings.setSelectedElement("TEST-1");
		Matrix matrix = CiaService.calculateMatrixImpact(settings);
		assertEquals(10,matrix.getHeaderElements().size());
	}

	@Test
	public void testCalculateTreeImpact() {
		FilterSettings settings = new FilterSettings("TEST", "");
		settings.setSelectedElement("TEST-1");
		TreeViewer tree = CiaService.calculateTreeImpact(settings);
		assertEquals(1,tree.getNodes().size());
		int childSize = tree.getNodes().stream().findFirst().orElseThrow().getChildren().size();
		assertEquals(5, childSize);
	}

	@Test
	public void testSimplifyGraph() {
		FilterSettings settings = new FilterSettings("TEST", "");
		KnowledgeGraph graph = KnowledgeGraph.getInstance("TEST");
		assertEquals(graph.vertexSet().size(), 20);
		assertEquals(graph.edgeSet().size(), 23);
		KnowledgeGraph subGraph = CiaService.simplifiedGraph(graph, settings, new AtomicLong(-1));
		assertEquals(subGraph.vertexSet().size(), 15);
		assertEquals(subGraph.edgeSet().size(), 28);
	}

}
