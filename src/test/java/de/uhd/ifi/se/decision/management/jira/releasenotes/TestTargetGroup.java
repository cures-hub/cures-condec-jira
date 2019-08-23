package de.uhd.ifi.se.decision.management.jira.releasenotes;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestTargetGroup {

	@Test
	public void testToString() {
		assertEquals("stakeholder", TargetGroup.STAKEHOLDER.toString());
	}

	@Test
	public void testGetTargetGroup() {
		assertEquals(TargetGroup.STAKEHOLDER, TargetGroup.getTargetGroup("stakeholder"));
		assertEquals(TargetGroup.DEVELOPER, TargetGroup.getTargetGroup(null));

	}
}