package de.uhd.ifi.se.decision.management.jira.persistence.activeobjectstrategy;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.MockApplicationUser;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
public class TestInsertDecisionKnowledgeElement extends ActiveObjectStrategyTestSetUp {

	@Ignore
	@Test(expected = NullPointerException.class)
	public void testElementNullUserNull() {
		aoStrategy.insertDecisionKnowledgeElement(null, null);
	}

	@Ignore
	@Test(expected = NullPointerException.class)
	public void testElementEmptyUserNull() {
		DecisionKnowledgeElement element = new DecisionKnowledgeElementImpl();
		aoStrategy.insertDecisionKnowledgeElement(element, null);
	}

	// TODO Fixing the Test Problems (Closed Connection)
	@Ignore
	public void testRepresFilledUserNoFails() {
		DecisionKnowledgeElementImpl dec = new DecisionKnowledgeElementImpl();
		dec.setProject("TEST");
		dec.setType(KnowledgeType.SOLUTION);
		ApplicationUser user = new MockApplicationUser("NoFails");
		assertNotNull(aoStrategy.insertDecisionKnowledgeElement(dec, user));
	}

	// TODO Fixing the Test Problems (Closed Connection)
	@Ignore
	public void testRepresFilledUserWithFails() {
		DecisionKnowledgeElementImpl dec = new DecisionKnowledgeElementImpl();
		dec.setProject("TEST");
		dec.setType(KnowledgeType.SOLUTION);
		ApplicationUser user = new MockApplicationUser("WithFails");
		assertNull(aoStrategy.insertDecisionKnowledgeElement(dec, user));
	}
}
