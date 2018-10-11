package de.uhd.ifi.se.decision.management.jira.persistence.activeobjectstrategy;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import de.uhd.ifi.se.decision.management.jira.extraction.model.TestComment;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.jdbc.NonTransactional;
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
@Data(ActiveObjectStrategyTestSetUp.AoSentenceTestDatabaseUpdater.class)
public class TestInsertDecisionKnowledgeElement extends ActiveObjectStrategyTestSetUp {

	@Test(expected = NullPointerException.class)
    @NonTransactional
	public void testElementNullUserNull() {
		aoStrategy.insertDecisionKnowledgeElement(null, null);
	}

	@Test(expected = NullPointerException.class)
    @NonTransactional
	public void testElementEmptyUserNull() {
		DecisionKnowledgeElement element = new DecisionKnowledgeElementImpl();
		aoStrategy.insertDecisionKnowledgeElement(element, null);
	}

	@Test
    @NonTransactional
	public void testRepresFilledUserNoFails() {
		DecisionKnowledgeElementImpl dec = new DecisionKnowledgeElementImpl();
		dec.setProject("TEST");
		dec.setType(KnowledgeType.SOLUTION);
		ApplicationUser user = new MockApplicationUser("NoFails");
		assertNotNull(aoStrategy.insertDecisionKnowledgeElement(dec, user));
	}

}
