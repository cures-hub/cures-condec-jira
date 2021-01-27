package de.uhd.ifi.se.decision.management.jira.view.matrix;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;

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

}