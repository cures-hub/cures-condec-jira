package de.uhd.ifi.se.decision.documentation.jira.persistence;

import de.uhd.ifi.se.decision.documentation.jira.TestSetUp;
import org.junit.Before;
import org.junit.Test;

public class TestStrategyProvider extends TestSetUp {
	private StrategyProvider provider;

	@Before
	public void setUp() {
		initialization();
		provider = new StrategyProvider();
	}

	@Test(expected = java.lang.IllegalArgumentException.class)
	public void testProjectKeyNull() {
		provider.getStrategy(null);
	}

	@Test
	public void testProjectKeyNotExist() {
		provider.getStrategy("TESTNOT");
	}

	@Test
	public void testProjectKeyExists() {
		provider.getStrategy("TEST");
	}
}