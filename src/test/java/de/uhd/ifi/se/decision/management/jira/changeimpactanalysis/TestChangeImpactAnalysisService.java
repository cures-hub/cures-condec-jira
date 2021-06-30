package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import de.uhd.ifi.se.decision.management.jira.view.matrix.Matrix;
import de.uhd.ifi.se.decision.management.jira.view.treeviewer.TreeViewer;
import de.uhd.ifi.se.decision.management.jira.view.vis.VisGraph;
import net.java.ao.test.jdbc.NonTransactional;

public class TestChangeImpactAnalysisService extends TestSetUp {

	protected static KnowledgeElement element;
	protected KnowledgeGraph graph;

	@Before
	public void setUp() {
		init();
		graph = new KnowledgeGraph("TEST");
		element = JiraIssues.addElementToDataBase();
	}

	@Test
	@NonTransactional
	public void testCalculateGraphImpact() {
		FilterSettings settings = new FilterSettings("TEST", "");
		settings.setSelectedElement("TEST-1");
		Arrays.stream(KnowledgeType.values()).map(KnowledgeType::toString).forEach((string) -> {
		});

		VisGraph graph = ChangeImpactAnalysisService.calculateGraphImpact(settings);
		assertEquals(10, graph.getNodes().size());
		assertEquals(16, graph.getEdges().size());
	}

	@Test
	@NonTransactional
	public void testCalculateMatrixImpact() {
		FilterSettings settings = new FilterSettings("TEST", "");
		settings.setSelectedElement("TEST-1");
		Matrix matrix = ChangeImpactAnalysisService.calculateMatrixImpact(settings);
		assertEquals(10, matrix.getHeaderElementsWithHighlighting().size());
	}

	@Test
	@NonTransactional
	public void testCalculateTreeImpact() {
		FilterSettings settings = new FilterSettings("TEST", "");
		settings.setSelectedElement("TEST-1");
		TreeViewer tree = ChangeImpactAnalysisService.calculateTreeImpact(settings);
		assertEquals(1, tree.getNodes().size());
		int childSize = tree.getNodes().stream().findFirst().orElseThrow().getChildren().size();
		assertEquals(5, childSize);
	}
}
