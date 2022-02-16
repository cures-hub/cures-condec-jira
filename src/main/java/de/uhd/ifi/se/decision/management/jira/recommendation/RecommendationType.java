package de.uhd.ifi.se.decision.management.jira.recommendation;

import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.KnowledgeSource;

/**
 * Type of a {@link Recommendation}, e.g. solution option from an
 * {@link RecommendationType#EXTERNAL} {@link KnowledgeSource}, a new
 * {@link RecommendationType#LINK} or potential
 * {@link RecommendationType#DUPLICATE} within the project.
 */
public enum RecommendationType {
	DUPLICATE, LINK, EXTERNAL
}