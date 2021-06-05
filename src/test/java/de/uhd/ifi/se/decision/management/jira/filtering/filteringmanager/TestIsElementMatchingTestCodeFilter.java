package de.uhd.ifi.se.decision.management.jira.filtering.filteringmanager;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.filtering.FilteringManager;
import de.uhd.ifi.se.decision.management.jira.testdata.CodeFiles;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;

public class TestIsElementMatchingTestCodeFilter extends TestSetUp {

	private FilteringManager filteringManager;

	@Before
	public void setUp() {
		init();
		FilterSettings settings = new FilterSettings("TEST", "");
		settings.getKnowledgeTypes().add("Code");
		filteringManager = new FilteringManager(settings);
	}

	@Test
	public void testTestCodeExcludedFilterAndElementIsNoCodeButNoTestCode() {
		assertTrue(filteringManager.isElementMatchingIsTestCodeFilter(KnowledgeElements.getTestKnowledgeElement()));
	}

	@Test
	public void testTestCodeExcludedFilterAndElementIsCodeButNoTestCode() {
		assertTrue(filteringManager.isElementMatchingIsTestCodeFilter(CodeFiles.getSmallCodeFileDone()));
	}

	@Test
	public void testTestCodeExcludedFilterAndElementIsTestCode() {
		filteringManager.getFilterSettings().setTestCodeShown(false);
		assertFalse(filteringManager.isElementMatchingIsTestCodeFilter(CodeFiles.getTestCodeFileDone()));
		assertFalse(filteringManager.isElementMatchingFilterSettings(CodeFiles.getTestCodeFileDone()));
	}

	@Test
	public void testTestCodeIncludedAndElementIsTestCode() {
		filteringManager.getFilterSettings().setTestCodeShown(true);
		assertTrue(filteringManager.isElementMatchingIsTestCodeFilter(CodeFiles.getTestCodeFileDone()));
		assertTrue(filteringManager.isElementMatchingFilterSettings(CodeFiles.getTestCodeFileDone()));
	}
}