package de.uhd.ifi.se.decision.management.jira.extraction.decxtracteventlistener;

import static org.junit.Assert.assertNotNull;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.jira.event.MockEventPublisher;
import de.uhd.ifi.se.decision.management.jira.extraction.DecXtractEventListener;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.atlassian.activeobjects.test.TestActiveObjects;

import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.management.jira.mocks.MockUserManager;
import net.java.ao.EntityManager;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.jdbc.NonTransactional;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(TestSetUpWithIssues.AoSentenceTestDatabaseUpdater.class)
public class TestRegisterFunctions extends TestSetUpEventListener {

	@Test
	@NonTransactional
	public void testCreation() {
		assertNotNull(listener);
	}

	@Test
    public void testAfterPropertiesSet() throws Exception {
	    listener.afterPropertiesSet();
    }

    @Test
    public void testDestroy() throws Exception {
	    listener.destroy();
    }

    @Test
    public void testIssueEventNull(){
	    listener.onIssueEvent(null);
    }

}
