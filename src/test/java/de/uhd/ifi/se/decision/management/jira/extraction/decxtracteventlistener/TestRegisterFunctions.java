package de.uhd.ifi.se.decision.management.jira.extraction.decxtracteventlistener;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;


import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
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
