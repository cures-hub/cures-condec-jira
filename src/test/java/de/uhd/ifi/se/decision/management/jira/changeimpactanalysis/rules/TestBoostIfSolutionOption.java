package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.rules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.ChangePropagationRuleType;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;

public class TestBoostIfSolutionOption extends TestSetUp {

	@Before
	public void setUp() {
		init();
	}

	@Test
	public void testDescription() {
		assertEquals("Boost when element is a solution option",
				ChangePropagationRuleType.BOOST_IF_SOLUTION_OPTION.getDescription());
	}

	@Test
	public void testExplanation() {
		assertTrue(ChangePropagationRuleType.BOOST_IF_SOLUTION_OPTION
			.getExplanation().contains("in the knowledge graph is a solution option"));
	}

    @Test
	public void testPropagation() {
		KnowledgeElement currentElement = KnowledgeElements.getOtherWorkItem();
		KnowledgeElement rootElement = KnowledgeElements.getProArgument();
		FilterSettings filterSettings = new FilterSettings("TEST", "");
        filterSettings.setSelectedElementObject(rootElement);
		
		assertNotNull(ChangePropagationRuleType.BOOST_IF_SOLUTION_OPTION.getFunction()
				.isChangePropagated(filterSettings, currentElement, null));
	}
}