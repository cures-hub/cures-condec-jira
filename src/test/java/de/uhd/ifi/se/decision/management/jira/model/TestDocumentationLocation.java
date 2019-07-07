package de.uhd.ifi.se.decision.management.jira.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestDocumentationLocation {

	@Test
	public void testGetDocumentationLocationFromIdentifier() {
		for (String location : DocumentationLocation.toList()) {
			assertTrue(location.equals(DocumentationLocation.getDocumentationLocationFromString(location).toString()));
		}
	}

	@Test
	public void testGetIdentifier() {
		for (DocumentationLocation location : DocumentationLocation.values()) {
			String identifier = location.getIdentifier();
			DocumentationLocation newLocation = DocumentationLocation
					.getDocumentationLocationFromIdentifier(identifier);
			if (location == DocumentationLocation.UNKNOWN) {
				continue;
			}
			assertEquals(location, newLocation);
		}
	}
}
