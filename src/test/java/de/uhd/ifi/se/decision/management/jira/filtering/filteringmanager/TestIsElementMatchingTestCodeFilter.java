package de.uhd.ifi.se.decision.management.jira.filtering.filteringmanager;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.filtering.FilteringManager;
import de.uhd.ifi.se.decision.management.jira.model.git.ChangedFile;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;

public class TestIsElementMatchingTestCodeFilter extends TestSetUp {

	private FilteringManager filteringManager;

	@Before
	public void setUp() {
		init();
		FilterSettings settings = new FilterSettings("TEST", "");
		filteringManager = new FilteringManager(settings);
	}

	@Test
	public void testTestCodeExcludedFilterAndElementIsNoCodeButNoTestCode() {
		assertTrue(filteringManager.isElementMatchingIsTestCodeFilter(KnowledgeElements.getTestKnowledgeElement()));
	}

	@Test
	public void testTestCodeExcludedFilterAndElementIsCodeButNoTestCode() {
		assertTrue(filteringManager.isElementMatchingIsTestCodeFilter(KnowledgeElements.getCodeFile()));
	}

	@Test
	public void testTestCodeExcludedFilterAndElementIsTestCode() {
		filteringManager.getFilterSettings().setTestCodeShown(false);

		ChangedFile testClass = new ChangedFile();
		testClass.setSummary("TestCodeFile.java");
		assertFalse(filteringManager.isElementMatchingIsTestCodeFilter(testClass));
		assertFalse(filteringManager.isElementMatchingFilterSettings(testClass));
	}

	@Test
	public void testTestCodeIncludedAndElementIsTestCode() {
		filteringManager.getFilterSettings().setTestCodeShown(true);
		ChangedFile testClass = new ChangedFile();
		testClass.setSummary("TestCodeFile.java");
		assertTrue(filteringManager.isElementMatchingIsTestCodeFilter(testClass));
		assertTrue(filteringManager.isElementMatchingFilterSettings(testClass));
	}
}