package de.uhd.ifi.se.decision.management.jira.persistence.knowledgepersistencemanager.genericlinkmanager;

import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.persistence.impl.GenericLinkManager;
import net.java.ao.test.jdbc.NonTransactional;

public class TestDeleteInvalidLinks extends TestSetUp {

	@Before
	public void setUp() {
		init();
	}

	@Test
	@NonTransactional
	public void testLinkFilled() {
		assertFalse(GenericLinkManager.deleteInvalidLinks());
	}
}
