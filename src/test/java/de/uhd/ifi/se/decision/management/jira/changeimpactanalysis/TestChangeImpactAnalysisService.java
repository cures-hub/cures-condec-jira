package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import de.uhd.ifi.se.decision.management.jira.view.matrix.Matrix;
import de.uhd.ifi.se.decision.management.jira.view.treeviewer.TreeViewer;
import de.uhd.ifi.se.decision.management.jira.view.vis.VisGraph;
import net.java.ao.test.jdbc.NonTransactional;

public class TestChangeImpactAnalysisService extends TestSetUp {

	protected static KnowledgeElement element;
	protected KnowledgeGraph graph;
	private FilterSettings settings;

	@Before
	public void setUp() {
		init();
		@SuppressWarnings("unused")
        ChangeImpactAnalysisService service = new ChangeImpactAnalysisService();
		graph = KnowledgeGraph.getInstance("TEST");
		element = JiraIssues.addElementToDataBase();
		settings = new FilterSettings("TEST", "");
		settings.setSelectedElement("TEST-1");
	}

	@Test
	@NonTransactional
	public void testCalculateGraphImpact() {
		VisGraph graph = ChangeImpactAnalysisService.calculateGraphImpact(settings);
		assertTrue(graph.getNodes().size() > 0);
	}

	@Test
	@NonTransactional
	public void testCalculateMatrixImpact() {
		Matrix matrix = ChangeImpactAnalysisService.calculateMatrixImpact(settings);
		assertTrue(matrix.getHeaderElementsWithHighlighting().size() > 0);
	}

	@Test
	@NonTransactional
	public void testCalculateTreeImpact() {
		TreeViewer tree = ChangeImpactAnalysisService.calculateTreeImpact(settings);
		assertTrue(tree.getNodes().size() > 0);
	}

	@Test
	public void testCalculateImpactedKnowledgeElements() {
		List<KnowledgeElementWithImpact> impactedElements = ChangeImpactAnalysisService.calculateImpactedKnowledgeElements(settings);
		assertTrue(impactedElements.size() > 0);
		assertEquals(settings.getSelectedElement(), impactedElements.get(0));
	}
}
