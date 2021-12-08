package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.calculation;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.KnowledgeElementWithImpact;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.view.treeviewer.TreeViewer;

public class TestColorizer extends TestSetUp {
    
    @Before
	public void setUp() {
        init();
	}

    @Test
    public void testColorizeTreeNode() {
        FilterSettings settings = new FilterSettings("TEST", "");
        settings.setSelectedElement("TEST-1");
        TreeViewer tree = new TreeViewer(settings);
        List<KnowledgeElementWithImpact> impactedElements = new ArrayList<>();
        KnowledgeElementWithImpact rootElement = new KnowledgeElementWithImpact(settings.getSelectedElement());
		impactedElements.add(rootElement);

        tree.getNodes().forEach(node -> {
			Colorizer.colorizeTreeNode(impactedElements, node, settings);
            if (node.getElement() == rootElement) {
                assertEquals("1.00", node.getLiAttr().get("cia_parentImpact"));
                assertEquals("1.00", node.getLiAttr().get("cia_linkTypeWeight"));
                assertEquals("1.00", node.getLiAttr().get("cia_ruleBasedValue"));
                assertEquals("1.00", node.getLiAttr().get("cia_impactFactor"));
                assertEquals("", node.getLiAttr().get("cia_propagationRuleSummary"));
                assertEquals("", node.getLiAttr().get("cia_valueExplanation"));
            }
            assertEquals("background-color:white", node.getLiAttr().get("style"));
        });
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
