package de.uhd.ifi.se.decision.management.jira.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Type of links between decision knowledge elements
 */
public enum LinkType {
	CONTAIN("Contains", "contains", "is contained by", "contain_style"),
	SUPPORT("Supports", "supports", "is supported by", "contain_style"),
	ATTACK("Attacks", "attacks", "is attacked by", "contain_style"),
	CONSTRAINT("Constraint", "constraints", "is constrained by", "contain-style"),
	ENABLE("Enables", "enables", "is enabled by", "contain-style"),
	FORBID("Forbids", "forbids", "is forbidden by", "contain-style"),
	COMPRISE("Comprises", "comprises", "is comprised by", "contain-style"),
	SUBSUME("Subsumes", "subsumes", "is subsumed by", "contain-style"),
	OVERRIDE("Overrides", "overrides", "is overridden by", "contain-style"),
	REPLACE("Replaces", "replaces", "is replaced by", "contain-style"),
	RELATE("Relates", "relates to", "is relates to", "contain-style");

	private String name;
	private String outwardLink;
	private String inwardLink;
	private String style;

	private LinkType(String name, String outwardLink, String inwardLink, String style) {
		this.name = name;
		this.outwardLink = outwardLink;
		this.inwardLink = inwardLink;
		this.style = style;
	}

	public String getName() {
		return name;
	}

	public String getOutwardLink() {
		return outwardLink;
	}

	public String getInwardLink() {
		return inwardLink;
	}

	public String getStyle() {
		return style;
	}

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
	 * Convert a string to a link type.
	 *
	 * @param type
	 *            as a String.
	 * @return link type.
	 */
	public static LinkType getLinkType(String type) {
		if (type == null) {
			return LinkType.CONTAIN;
		}
		switch (type.toLowerCase(Locale.ENGLISH)) {
		case "support":
			return LinkType.SUPPORT;
		case "attack":
			return LinkType.ATTACK;
		default:
			return LinkType.CONTAIN;
		}
	}

	/**
	 * Get the link type that is associated to a certain knowledge type, e.g.,
	 * support for pro-arguments and attack for con-arguments. The default link type
	 * is contain.
	 * 
	 * @param knowledgeTypeOfChildElement
	 *            knowledge type of the child element.
	 * @return link type.
	 */
	public static LinkType getLinkTypeForKnowledgeType(KnowledgeType knowledgeTypeOfChildElement) {
		switch (knowledgeTypeOfChildElement) {
		case PRO:
			return LinkType.SUPPORT;
		case CON:
			return LinkType.ATTACK;
		default:
			return LinkType.CONTAIN;
		}
	}

	public static LinkType getLinkTypeForKnowledgeType(String knowledgeTypeOfChildElement) {
		KnowledgeType type = KnowledgeType.getKnowledgeType(knowledgeTypeOfChildElement);
		return getLinkTypeForKnowledgeType(type);
	}

	public static boolean linkTypesAreEqual(KnowledgeType formerKnowledgeType, KnowledgeType knowledgeType) {
		boolean bothKnowledgeTypesAreArguments = formerKnowledgeType.replaceProAndConWithArgument() == knowledgeType
				.replaceProAndConWithArgument();
		LinkType formerLinkType = getLinkTypeForKnowledgeType(formerKnowledgeType);
		LinkType linkType = LinkType.getLinkTypeForKnowledgeType(knowledgeType);
		return bothKnowledgeTypesAreArguments && formerLinkType.equals(linkType);
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
