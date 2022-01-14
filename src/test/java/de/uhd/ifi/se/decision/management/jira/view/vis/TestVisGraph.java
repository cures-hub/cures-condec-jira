package de.uhd.ifi.se.decision.management.jira.view.vis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.ChangeImpactAnalysisService;
import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.KnowledgeElementWithImpact;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;

public class TestVisGraph extends TestSetUp {
	private VisGraph visGraph;
	private FilterSettings filterSettings;

	@Before
	public void setUp() {
		init();
		filterSettings = new FilterSettings("TEST", "");
		visGraph = new VisGraph(filterSettings);
	}

	@Test
	public void testGetNodes() {
		assertEquals(JiraIssues.getTestJiraIssueCount(), visGraph.getNodes().size());
	}

	@Test
	public void testGetEdges() {
		assertTrue(visGraph.getEdges().size() > 10);
	}

	@Test
	public void testSetNodes() {
		HashSet<VisNode> newNodes = new HashSet<VisNode>();
		visGraph.setNodes(newNodes);
		assertEquals(visGraph.getNodes(), newNodes);
	}

	@Test
	public void testSetEdges() {
		HashSet<VisEdge> newEdges = new HashSet<VisEdge>();
		visGraph.setEdges(newEdges);
		assertEquals(visGraph.getEdges(), newEdges);
	}

	@Test
	public void testGetSelectedVisNodeIdNoElementSelected() {
		assertEquals("", visGraph.getSelectedVisNodeId());
	}

	@Test
	public void testGetSelectedVisNodeIdWithElementSelected() {
		filterSettings = new FilterSettings("TEST", "");
		filterSettings.setSelectedElementObject(KnowledgeElements.getTestKnowledgeElement());
		visGraph = new VisGraph(filterSettings);
		assertEquals("1_i", visGraph.getSelectedVisNodeId());
	}

	@Test
	public void testGetGraph() {
		assertTrue(visGraph.getGraph().vertexSet().size() > 0);
	}

	@Test
	public void testConstructorFilterSettingsNull() {
		assertNotNull(new VisGraph((FilterSettings) null));
	}

	@Test
	public void testConstructorFilterSettingsFilled() {
		assertNotNull(new VisGraph(filterSettings));
	}

	@Test
	public void testConstructorFilterSettingsFilledRootElementExisting() {
		filterSettings.setSelectedElement("TEST-1");
		filterSettings.setLinkDistance(1);
		visGraph = new VisGraph(filterSettings);
		assertTrue(KnowledgeGraph.getInstance("TEST").vertexSet().size() > visGraph.getGraph().vertexSet().size());
		assertTrue(KnowledgeGraph.getInstance("TEST").edgeSet().size() > visGraph.getGraph().edgeSet().size());
	}

	@Test
	public void testConstructorWithCIAElements() {
		filterSettings.setSelectedElement("TEST-1");
		List<KnowledgeElementWithImpact> impactedElements = ChangeImpactAnalysisService.calculateImpactedKnowledgeElements(filterSettings);
		VisGraph visGraph = new VisGraph(filterSettings, impactedElements);
		
		assertTrue(visGraph.getNodes().size() > 0);
	}

	@Test
	public void testConstructorWithCIAElementsSettingsNull() {
		filterSettings.setSelectedElement("TEST-1");
		List<KnowledgeElementWithImpact> impactedElements = ChangeImpactAnalysisService.calculateImpactedKnowledgeElements(filterSettings);
		
		assertEquals(0, new VisGraph(null, impactedElements).getNodes().size());
	}

	@Test
	public void testVisGraphWithHierarchy() {
		filterSettings.setHierarchical(true);
		filterSettings.setSelectedElement("TEST-1");
		visGraph = new VisGraph(filterSettings);
		Set<VisNode> nodes = visGraph.getNodes();
		assertEquals(visGraph.getGraph().vertexSet().size(), nodes.size());
	}
}
