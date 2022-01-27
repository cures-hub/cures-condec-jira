package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.calculation;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.KnowledgeElementWithImpact;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;
import de.uhd.ifi.se.decision.management.jira.view.matrix.MatrixNode;
import de.uhd.ifi.se.decision.management.jira.view.treeviewer.TreeViewerNode;
import de.uhd.ifi.se.decision.management.jira.view.vis.VisNode;

public class TestColorizer extends TestSetUp {

    private FilterSettings settings;
    private List<KnowledgeElementWithImpact> impactedElements;

    @Before
	public void setUp() {
        init();
        settings = new FilterSettings("TEST", "");
        settings.setSelectedElement("TEST-1");
        impactedElements = new ArrayList<>();
	}

    @Test
    public void testColorizeTreeRootNode() {
        KnowledgeElementWithImpact element = new KnowledgeElementWithImpact(settings.getSelectedElement());
		impactedElements.add(element);

        TreeViewerNode node = new TreeViewerNode(settings.getSelectedElement(), settings);
        TreeViewerNode childNode = new TreeViewerNode(KnowledgeElements.getTestKnowledgeElements().get(1), settings);
        List<TreeViewerNode> children = new ArrayList<>();
        children.add(childNode);
        node.setChildren(children);
        node = Colorizer.colorizeTreeNode(impactedElements, node, settings);

        assertEquals("background-color:#FFFFFF", node.getLiAttr().get("style"));
    }

    @Test
    public void testColorizeTreeContextNode() {
        KnowledgeElementWithImpact element = new KnowledgeElementWithImpact(KnowledgeElements.getTestKnowledgeElements().get(4));
        element.setImpactExplanation("context");
        impactedElements.add(element);

        TreeViewerNode node = new TreeViewerNode(element, settings);
        node = Colorizer.colorizeTreeNode(impactedElements, node, settings);

        assertEquals("background-color:#3399CC", node.getLiAttr().get("style"));
    }

    @Test
    public void testColorizeTreeNode() {
        KnowledgeElementWithImpact element = new KnowledgeElementWithImpact(KnowledgeElements.getTestKnowledgeElements().get(4));
        element.setImpactValue(0.5);
        impactedElements.add(element);

        TreeViewerNode node = new TreeViewerNode(element, settings);
        node = Colorizer.colorizeTreeNode(impactedElements, node, settings);

        assertEquals("background-color:#7f7f00", node.getLiAttr().get("style"));
    }

    @Test
    public void testColorizeVisRootNode() {
        KnowledgeElementWithImpact element = new KnowledgeElementWithImpact(settings.getSelectedElement());

		VisNode node = new VisNode(settings.getSelectedElement(), settings);
        node = Colorizer.colorizeVisNode(element, node, settings);

        assertEquals("#FFFFFF", node.getColor());
        assertEquals("black", node.getFont().get("color"));
    }

    @Test
    public void testColorizeVisContextNode() {
        KnowledgeElementWithImpact element = new KnowledgeElementWithImpact(KnowledgeElements.getTestKnowledgeElements().get(4));
        element.setImpactExplanation("context");

		VisNode node = new VisNode(element, settings);
        node = Colorizer.colorizeVisNode(element, node, settings);

        assertEquals("#3399CC", node.getColor());
        assertEquals("black", node.getFont().get("color"));
    }

    @Test
    public void testColorizeVisNode() {
        KnowledgeElementWithImpact element = new KnowledgeElementWithImpact(KnowledgeElements.getTestKnowledgeElements().get(4));
        element.setImpactValue(0.5);

		VisNode node = new VisNode(element, settings);
        node = Colorizer.colorizeVisNode(element, node, settings);

        assertEquals("#7f7f00", node.getColor());
        assertEquals("black", node.getFont().get("color"));
    }

    @Test
    public void testColorizeMatrixRootNode() {
        KnowledgeElementWithImpact element = new KnowledgeElementWithImpact(settings.getSelectedElement());

		MatrixNode node = new MatrixNode(settings.getSelectedElement());
        node = Colorizer.colorizeMatrixNode(element, node, settings);

        assertEquals("#FFFFFF", node.getChangeImpactColor());
    }

    @Test
    public void testColorizeMatrixContextNode() {
        KnowledgeElementWithImpact element = new KnowledgeElementWithImpact(KnowledgeElements.getTestKnowledgeElements().get(4));
        element.setImpactExplanation("context");

		MatrixNode node = new MatrixNode(element);
        node = Colorizer.colorizeMatrixNode(element, node, settings);

        assertEquals("#3399CC", node.getChangeImpactColor());
    }

    @Test
    public void testColorizeMatrixNode() {
        KnowledgeElementWithImpact element = new KnowledgeElementWithImpact(KnowledgeElements.getTestKnowledgeElements().get(4));
        element.setImpactValue(0.5);

		MatrixNode node = new MatrixNode(element);
        node = Colorizer.colorizeMatrixNode(element, node, settings);

        assertEquals("#7f7f00", node.getChangeImpactColor());
    }

    @Test
    public void testColorForImpact() {
        double impactValue = 0.45;
        String color = Colorizer.colorForImpact(impactValue);
        assertEquals("#728c00", color);

        impactValue = 0.99;
        color = Colorizer.colorForImpact(impactValue);
        assertEquals("#fc0200", color);
    }
}
