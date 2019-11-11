package de.uhd.ifi.se.decision.management.jira.releasenotes;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestTargetGroup {

	@Test
	public void testToString() {
		assertEquals("developer", TargetGroup.DEVELOPER.toString());
	}

	@Test
	public void testGetTargetGroup() {
		assertEquals(TargetGroup.DEVELOPER, TargetGroup.getTargetGroup("developer"));
		assertEquals(TargetGroup.DEVELOPER, TargetGroup.getTargetGroup(null));
	}
}