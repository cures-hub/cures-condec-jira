package de.uhd.ifi.se.decision.management.jira.model;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import de.uhd.ifi.se.decision.management.jira.config.JiraSchemeManager;

/**
 * Models the types of links between knowledge elements.
 * 
 * Link types among decisions are modeled according to Kruchten's taxonomy.
 * 
 * The duplicate relationship was added to be able to model this type of
 * inconsistency.
 * 
 * Other Jira issue link types such as the "jira_subtask_link" type are not
 * explicitly included in this enum (only as OTHER).
 */
public enum LinkType {
	SUPPORT("Supports", "supports", "is supported by", "#00994C"), // pro-arguments to solution options
	ATTACK("Attacks", "attacks", "is attacked by", "#c0392b"), // con-arguments to solution options
	FORBID("Forbids", "forbids", "is forbidden by", "#c0392b"), // among decisions
	CONSTRAINT("Constraints", "constraints", "is constrained by", "#0066b3"), // among decisions
	ENABLE("Enables", "enables", "is enabled by", "#80ff80"), // among decisions
	COMPRISE("Comprises", "comprises", "is comprised by", "#BA55D3"), // among decisions
	SUBSUME("Subsumes", "subsumes", "is subsumed by", "#00cc00"), // among decisions
	RELATE("Relates", "relates to", "relates to", "#80c9ff"), // among decisions
	OVERRIDE("Overrides", "overrides", "is overridden by", "#FFFF00"), // among decisions
	REPLACE("Replaces", "replaces", "is replaced by", "#ff8000"), // among decisions
	DUPLICATE("Duplicate", "duplicates", "is duplicated by", "#c0392b"), // among duplicated elements
	TRANSITIVE("Transitive", "transitively links to", "transitively links to", "#15ceb6"), // special filtering
	OTHER("", "other", "other", ""); // other Jira issue links, e.g. "jira_subtask_link"

	private String name;
	private String outwardName;
	private String inwardName;
	private String color;

	private LinkType(String name, String outwardName, String inwardName, String color) {
		this.name = name;
		this.outwardName = outwardName;
		this.inwardName = inwardName;
		this.color = color;
	}

	public String getName() {
		return name;
	}

	public String getOutwardName() {
		return outwardName;
	}

	public String getInwardName() {
		return inwardName;
	}

	/**
	 * @return style needed to create a Jira issue link type.
	 * @see JiraSchemeManager#createLinkType(String)
	 */
	public String getStyle() {
		return "contain-style";
	}

	public String getColor() {
		return color;
	}

	public static Set<LinkType> getDefaultTypes() {
		return EnumSet.of(SUPPORT, ATTACK);
	}

	/**
	 * @return link type as a String in lowercase, e.g., relate, support, and
	 *         attack.
	 */
	@Override
	public String toString() {
		return name().toLowerCase(Locale.ENGLISH);
	}

	/**
	 * @param name
	 *            of the link type as a String.
	 * @return link type as a {@link LinkType} object.
	 */
	public static LinkType getLinkType(String name) {
		if (name == null || name.isBlank()) {
			return LinkType.getDefaultLinkType();
		}
		for (LinkType linkType : LinkType.values()) {
			if (linkType.getName().toLowerCase(Locale.ENGLISH).startsWith(name.toLowerCase(Locale.ENGLISH))) {
				return linkType;
			}
		}
		return OTHER;
	}

	public static String getLinkTypeColor(String linkTypeName) {
		LinkType linkType = getLinkType(linkTypeName);
		if (linkType == OTHER) {
			return String.format("#%x", StringUtils.capitalize(linkTypeName).hashCode() & 0x0FFFFFF);
		}
		return linkType.getColor();
	}

	/**
	 * @param knowledgeTypeOfChildElement
	 *            knowledge type of the child element.
	 * @return link type that is associated to a certain knowledge type, e.g.,
	 *         support for pro-arguments and attack for con-arguments. The default
	 *         link type is relate.
	 */
	public static LinkType getLinkTypeForKnowledgeType(KnowledgeType knowledgeTypeOfChildElement) {
		switch (knowledgeTypeOfChildElement) {
		case PRO:
			return SUPPORT;
		case CON:
			return ATTACK;
		default:
			return getDefaultLinkType();
		}
	}

	public static LinkType getDefaultLinkType() {
		return RELATE;
	}

	public static boolean linkTypesAreEqual(KnowledgeType formerKnowledgeType, KnowledgeType knowledgeType) {
		boolean bothKnowledgeTypesAreArguments = formerKnowledgeType.replaceProAndConWithArgument() == knowledgeType
				.replaceProAndConWithArgument();
		LinkType formerLinkType = getLinkTypeForKnowledgeType(formerKnowledgeType);
		LinkType linkType = LinkType.getLinkTypeForKnowledgeType(knowledgeType);
		return bothKnowledgeTypesAreArguments && formerLinkType.equals(linkType);
	}

	/**
	 * @return names of link types as a set of Strings.
	 */
	public static Set<String> toStringSet() {
		Set<String> linkTypes = new HashSet<String>();
		for (LinkType linkType : LinkType.values()) {
			linkTypes.add(linkType.getName());
		}
		return linkTypes;
	}
}
