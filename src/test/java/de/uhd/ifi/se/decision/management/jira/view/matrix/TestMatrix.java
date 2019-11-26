package de.uhd.ifi.se.decision.management.jira.view.matrix;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestMatrix extends TestSetUp {
	private Matrix matrix;

	@Before
	public void setUp() {
		init();
		List<DecisionKnowledgeElement> decisions = new ArrayList<>();

		DecisionKnowledgeElement element_1 = new DecisionKnowledgeElementImpl((long) 1, "TESTfwf", "", "Decision", "TEST", "Test-1", "i");
		decisions.add(element_1);

		DecisionKnowledgeElement element_2 = new DecisionKnowledgeElementImpl((long) 2, "TESTfwfw", "", "Decision", "TEST", "Test-1", "i");
		decisions.add(element_2);

		matrix = new Matrix("Test", decisions);
	}


	@Test
	public void testGetHeaderElements() {
		assertEquals(2, this.matrix.getHeaderElements().size());
	}

	@Test
	public void testGetColoredRows() {
		assertTrue(this.matrix.getColoredRows("Test").get(1).contains("LightGray"));
		assertTrue(this.matrix.getColoredRows("Test").get(1).contains("White"));
	}



}