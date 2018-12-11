package de.uhd.ifi.se.decision.management.jira.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Type of links between decision knowledge elements
 */
public enum LinkType {
	CONTAIN, SUPPORT, ATTACK;

	/**
	 * Convert the link type to a String with lower case letters, e.g., contain,
	 * support, and attack.
	 *
	 * @return knowledge type as a String starting with a capital letter.
	 */
	@Override
	public String toString() {
		return this.name().toLowerCase(Locale.ENGLISH);
	}

	/**
	 * Get the link type that is associated to a certain knowledge type, e.g.,
	 * support for pro-arguments and attack for con-arguments. The default link type
	 * is contain.
	 *
	 * @return link type.
	 */
	public static LinkType getLinkType(KnowledgeType knowledgeTypeOfChildElement) {
		switch (knowledgeTypeOfChildElement) {
		case PRO:
			return LinkType.SUPPORT;
		case CON:
			return LinkType.ATTACK;
		default:
			return LinkType.CONTAIN;
		}
	}

	/**
	 * Convert all link types to a list of String.
	 *
	 * @return list of link types as Strings.
	 */
	public static List<String> toList() {
		List<String> linkTypes = new ArrayList<String>();
		for (LinkType linkType : LinkType.values()) {
			linkTypes.add(linkType.toString());
		}
		return linkTypes;
	}
}
