package de.uhd.ifi.se.decision.management.jira.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Type of links between decision knowledge elements
 */
public enum LinkType {
	SUPPORT("Supports", "supports", "is supported by", "contain_style", ""), //
	ATTACK("Attacks", "attacks", "is attacked by", "contain_style", ""), //
	FORBID("Forbids", "forbids", "is forbidden by", "contain-style", "#ff0000"), //
	CONSTRAINT("Constraints", "constraints", "is constrained by", "contain-style", "#0066b3"), //
	ENABLE("Enables", "enables", "is enabled by", "contain-style", "#80ff80"), //
	COMPRISE("Comprises", "comprises", "is comprised by", "contain-style", "#BA55D3"), SUBSUME("Subsumes", "subsumes",
			"is subsumed by", "contain-style", "#00cc00"), //
	RELATE("Relates", "relates to", "is relates to", "contain-style", "#80c9ff"), //
	OVERRIDE("Overrides", "overrides", "is overridden by", "contain-style", "#FFFF00"), //
	REPLACE("Replaces", "replaces", "is replaced by", "contain-style", "#ff8000");

	private String name;
	private String outwardLink;
	private String inwardLink;
	private String style;
	private String color;

	// TODO why does the constructur get the outwardLink and inwardLink? Please
	// remove or add JavaDoc. Is the constructor public or private?
	LinkType(String name, String outwardLink, String inwardLink, String style, String color) {
		this.name = name;
		this.outwardLink = outwardLink;
		this.inwardLink = inwardLink;
		this.style = style;
		this.color = color;
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

	public String getColor() {
		return color;
	}

	/**
	 * Convert the link type to a String with lower case letters, e.g., relate,
	 * support, and attack.
	 *
	 * @return knowledge type as a String starting with a capital letter.
	 */
	@Override
	public String toString() {
		return this.name().toLowerCase(Locale.ENGLISH);
	}

	/**
	 * Convert a link type name to a link type.
	 *
	 * @param name
	 * @return link type.
	 */
	public static LinkType getLinkType(String name) {
		if (name == null) {
			return LinkType.getDefaultLinkType();
		}
		for (LinkType linkType : LinkType.values()) {
			if (linkType.getName().toLowerCase(Locale.ENGLISH).matches(name.toLowerCase(Locale.ENGLISH))) {
				return linkType;
			}
		}
		return LinkType.getDefaultLinkType();
	}

	public static String getLinkTypeColor(LinkType linkType) {
		return linkType.getColor();
	}

	public static String getLinkTypeColor(String linkTypeName) {
		LinkType linkType = getLinkType(linkTypeName);
		return linkType.getColor();
	}

	/**
	 * Get the link type that is associated to a certain knowledge type, e.g.,
	 * support for pro-arguments and attack for con-arguments. The default link type
	 * is relate.
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
			return LinkType.getDefaultLinkType();
		}
	}

	public static LinkType getDefaultLinkType() {
		return LinkType.RELATE;
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
			linkTypes.add(linkType.getName());
		}
		return linkTypes;
	}
}
