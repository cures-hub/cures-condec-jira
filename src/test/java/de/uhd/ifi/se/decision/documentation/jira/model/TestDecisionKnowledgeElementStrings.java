package de.uhd.ifi.se.decision.documentation.jira.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import com.atlassian.activeobjects.test.TestActiveObjects;
import de.uhd.ifi.se.decision.documentation.jira.ComponentGetter;
import de.uhd.ifi.se.decision.documentation.jira.TestSetUp;
import de.uhd.ifi.se.decision.documentation.jira.mocks.MockDefaultUserManager;
import de.uhd.ifi.se.decision.documentation.jira.mocks.MockTransactionTemplate;
import net.java.ao.EntityManager;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.documentation.jira.model.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.documentation.jira.model.KnowledgeType;
import org.junit.runner.RunWith;

/**
 *
 * @description Test Class for Simple Geter and Setter Tests
 *
 */
@RunWith(ActiveObjectsJUnitRunner.class)
public class TestDecisionKnowledgeElementStrings extends TestSetUp{
	protected EntityManager entityManager;

	private Long id;
	private String summary;
	private String description;
	private KnowledgeType type;
	private String projectKey;
	private String key;
	private DecisionKnowledgeElementImpl repre;

	@Before
	public void setUp() {
		initialization();
		this.id = (long) 100;
		this.summary = "Test";
		this.description = "Test";
		this.type = KnowledgeType.SOLUTION;
		this.projectKey = "Test";
		this.key = "Test";

		this.repre = new DecisionKnowledgeElementImpl(id, summary, description, type, projectKey, key);

		new ComponentGetter().init(new TestActiveObjects(entityManager), new MockTransactionTemplate(), new MockDefaultUserManager());

	}

	@Test
	public void testGetTypeAsString(){
		assertEquals("Solution", repre.getTypeAsString());
	}

	@Test
	public void testgetId() {
		assertEquals(this.id, this.repre.getId(), 0.0);
	}

	@Test
	public void testGetName() {
		assertEquals(this.summary, this.repre.getSummary());
	}

	@Test
	public void testGetDescription() {
		assertEquals(this.description, this.repre.getDescription());
	}

	@Test
	public void testGetType() {
		assertEquals(this.type, this.repre.getType());
	}

	@Test
	public void testGetPKey() {
		assertEquals(this.projectKey, this.repre.getProjectKey());
	}

	@Test
	public void testSetId() {
		this.repre.setId(this.id + 1);
		assertEquals(this.id + 1, this.repre.getId(), 0.0);
	}

	@Test
	public void testSetName() {
		this.repre.setSummary(this.summary + "New");
		assertEquals(this.summary + "New", this.repre.getSummary());
	}

	@Test
	public void testSetDescription() {
		this.repre.setDescription(this.description + "New");
		assertEquals(this.description + "New", this.repre.getDescription());
	}

	@Test
	public void testSetType() {
		this.repre.setType(KnowledgeType.ALTERNATIVE);
		assertEquals(KnowledgeType.ALTERNATIVE, this.repre.getType());
	}

	@Test
	public void tstSetPKey() {
		this.repre.setProjectKey(this.projectKey + "New");
		assertEquals(this.projectKey + "New", this.repre.getProjectKey());
	}

	@Test
	public void testGetKeyKeyNull(){
		DecisionKnowledgeElementImpl impl = new DecisionKnowledgeElementImpl();
		impl.setProjectKey("TEST");
		impl.setId((long) 10);
		assertEquals("TEST-10", impl.getKey());
	}

	@Test
	public void testEqualsFalse(){
		assertFalse(this.repre.equals(null));
	}

	@Test
	public void testEqualsNotTheObject(){
		DecisionKnowledgeElement element = new DecisionKnowledgeElementImpl();
		element.setId((long)123);
		assertFalse(this.repre.equals(element));
	}

	@Test
	public void testGetOutwardLinks(){
		repre.getOutwardLinks();
	}

	@Test
	public void testGetInwardLinks(){
		repre.getInwardLinks();
	}
}
