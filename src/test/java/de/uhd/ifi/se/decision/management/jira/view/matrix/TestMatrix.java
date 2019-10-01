package de.uhd.ifi.se.decision.management.jira.view.matrix;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class TestMatrix extends TestSetUp {
	private Matrix matrix;
	private Map<Long, String> matrixHeaderRow;
	private Map<Long, List<String>> matrixData;

	@Before
	public void setUp() {
		init();
		matrixHeaderRow = new TreeMap<>();
		matrixData = new TreeMap<>();
		List<DecisionKnowledgeElement> decisions = new ArrayList<>();

		DecisionKnowledgeElement element = new DecisionKnowledgeElementImpl();
		element.setId(1);
		element.setKey("Test-1");
		element.setType("Decision");
		element.setProject("TEST");
		element.setSummary("TESTfwf");
		element.setDocumentationLocation("i");
		decisions.add(element);

		matrix = new Matrix("Test", decisions);

	}

	@Test
	public void testGetMatrixHeaderRow() {
		assertTrue(this.matrix.getMatrixHeaderRow().containsKey((long) 1));
		assertTrue(this.matrix.getMatrixHeaderRow().containsValue("TESTfwf"));
	}

	@Test
	public void testGetMatrixData() {
		assertTrue(this.matrix.getMatrixData().containsKey((long) 1));
		assertEquals(1, this.matrix.getMatrixData().size());
	}

	@Test
	public void testGetMatrixDataEntries() {
		assertTrue(this.matrix.getMatrixData().get((long) 1).contains("LightGray"));
	}

}
