package de.uhd.ifi.se.decision.management.jira.view.matrix;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TestMatrix extends TestSetUp {
	private Matrix matrix;

	@Before
	public void setUp() {
		init();
		FilterSettings filterSettings = new FilterSettings("TEST", "");
		matrix = new Matrix(filterSettings);
	}

	@Test
	public void testGetHeaderElements() {
		assertEquals(JiraIssues.getTestJiraIssueCount(), this.matrix.getHeaderElements().size());
	}

	@Test
	public void testGetLinks() {
		assertNull(matrix.getMatrixOfLinks()[0][0]);
	}

	@Test
	public void testGetLinkTypesWithColors() {
		Map<String, String> linkTypesWithColors = matrix.getLinkTypesWithColor();
		assertEquals(2, linkTypesWithColors.size());
		assertEquals("#80c9ff", linkTypesWithColors.get("relate"));
	}

	@Test
	public void testMatrixWithColorMap() {
		Map<Long, String> colormap = new HashMap<>();
		colormap.put(1L, "#ffffff");

		Set<KnowledgeElement> elements = new HashSet<>();
		elements.add( new KnowledgeElement());

		Matrix matrix = new Matrix(elements, colormap);

		assertEquals(matrix.getColorMap().size(), 1);
		assertEquals(matrix.getColorMap().get(1L), "#ffffff");

		colormap = new HashMap<>();
		colormap.put(1L, "#000000");

		matrix.setColorMap(colormap);

		assertEquals(matrix.getColorMap().size(), 1);
		assertEquals(matrix.getColorMap().get(1L), "#000000");

	}

}