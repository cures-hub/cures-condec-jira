package de.uhd.ifi.se.decision.management.jira.view.matrix;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.impl.KnowledgeElementImpl;

public class TestMatrix extends TestSetUp {
	private Matrix matrix;

	@Before
	public void setUp() {
		init();
		List<KnowledgeElement> decisions = new ArrayList<>();

		KnowledgeElement element_1 = new KnowledgeElementImpl(1, "TESTfwf", "", "Decision", "TEST",
				"Test-1", "i", "");
		decisions.add(element_1);

		KnowledgeElement element_2 = new KnowledgeElementImpl(2, "TESTfwfw", "", "Decision", "TEST",
				"Test-1", "i", "");
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