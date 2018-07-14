package de.uhd.ifi.se.decision.management.jira.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Type of links between decision knowledge elements
 */
public enum LinkType {
	CONTAIN, SUPPORT, ATTACK;

	public String toString() {
		return this.name().toLowerCase(Locale.ENGLISH);
	}

	public static List<String> toList() {
		List<String> linkTypes = new ArrayList<String>();
		for (LinkType linkType : LinkType.values()) {
			linkTypes.add(linkType.toString());
		}
		return linkTypes;
	}
}
