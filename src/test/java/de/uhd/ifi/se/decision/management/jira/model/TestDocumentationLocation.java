package de.uhd.ifi.se.decision.management.jira.model;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TestDocumentationLocation {

	@Test
	public void testGetDocumentationLocationFromIdentifier(){
		for(String location: DocumentationLocation.toList()){
			assertTrue(location.equals(DocumentationLocation.getDocumentationLocationFromString(location).toString()));
		}
	}

	@Test
	public void testGetIdentifier(){
		for(DocumentationLocation location : DocumentationLocation.values()){
			String identifier = DocumentationLocation.getIdentifier(location);
			DocumentationLocation newLocation = DocumentationLocation.getDocumentationLocationFromIdentifier(identifier);
			assertTrue(location.toString().equals(newLocation.toString()));
		}
	}
}
