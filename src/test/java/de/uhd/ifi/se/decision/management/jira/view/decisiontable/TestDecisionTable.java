package de.uhd.ifi.se.decision.management.jira.view.decisiontable;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.Argument;
import de.uhd.ifi.se.decision.management.jira.model.SolutionOption;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;

public class TestDecisionTable extends TestSetUp {

	@Before
	public void setUp() {
		init();
	}

	@Test
	public void testDecisionTableForOneDecisionProblem() {
		DecisionTable decisionTable = new DecisionTable(KnowledgeElements.getSolvedDecisionProblem());
		assertEquals(2, decisionTable.getAlternatives().size());
		assertEquals(1, decisionTable.getCriteria().size());

		SolutionOption alternative = decisionTable.getAlternatives().get(0);
		assertEquals(1, alternative.getArguments().size());

		Argument argument = alternative.getArguments().get(0);
		assertEquals(1, argument.getCriteria().size());
		assertEquals("NFR: Usability", argument.getCriteria().get(0).getSummary());
	}

	@Test
	public void testGetCriteriaQuery() {
		assertEquals("project=CONDEC and type = \"Non functional requirement\"",
				DecisionTable.getCriteriaQuery("TEST"));
	}

	@Test
	public void testGetAllCriteria() {
		DecisionTable decisionTable = new DecisionTable("TEST");
		assertFalse(decisionTable.getAllDecisionTableCriteriaForProject(JiraUsers.SYS_ADMIN.getApplicationUser())
				.isEmpty());
	}
}
