package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.calculation;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.KnowledgeElementWithImpact;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.view.matrix.MatrixNode;
import de.uhd.ifi.se.decision.management.jira.view.treeviewer.TreeViewerNode;
import de.uhd.ifi.se.decision.management.jira.view.vis.VisNode;

public class TestColorizer extends TestSetUp {

    @Before
	public void setUp() {
        init();
	}

    @Test
    public void testColorizeTreeNode() {
        FilterSettings settings = new FilterSettings("TEST", "");
        settings.setSelectedElement("TEST-1");

        TreeViewerNode node = new TreeViewerNode(settings.getSelectedElement(), settings);
        List<KnowledgeElementWithImpact> impactedElements = new ArrayList<>();
        KnowledgeElementWithImpact element = new KnowledgeElementWithImpact(settings.getSelectedElement());
		impactedElements.add(element);
        node = Colorizer.colorizeTreeNode(impactedElements, node, settings);

        assertEquals("background-color:white", node.getLiAttr().get("style"));
    }

    @Test
    public void testColorizeVisNode() {
        FilterSettings settings = new FilterSettings("TEST", "");
        settings.setSelectedElement("TEST-1");

		VisNode node = new VisNode(settings.getSelectedElement(), settings);
        KnowledgeElementWithImpact element = new KnowledgeElementWithImpact(settings.getSelectedElement());
        node = Colorizer.colorizeVisNode(element, node, settings);

        assertEquals("white", node.getColor());
        assertEquals("black", node.getFont().get("color"));
    }

    @Test
    public void testColorizeMatrixNode() {
        FilterSettings settings = new FilterSettings("TEST", "");
        settings.setSelectedElement("TEST-1");

		MatrixNode node = new MatrixNode(settings.getSelectedElement());
        KnowledgeElementWithImpact element = new KnowledgeElementWithImpact(settings.getSelectedElement());
        node = Colorizer.colorizeMatrixNode(element, node, settings);

        assertEquals("#FFFFFF", node.getChangeImpactColor());
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
