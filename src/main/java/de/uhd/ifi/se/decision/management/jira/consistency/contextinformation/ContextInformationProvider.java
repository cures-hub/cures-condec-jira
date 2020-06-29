package de.uhd.ifi.se.decision.management.jira.consistency.contextinformation;

import com.atlassian.jira.issue.Issue;

/**
 * Interface for different context information provider.
 * Context utility functions are realized by so called context information providers.
 * The currently available context information providers, each representing a different
 * kind of context information used for calculating the architectural knowledge context.
 *
 * @author Philipp de Sombre
 *
 * @implSpec  C. Miesbauer and R. Weinreich,
 * "Capturing and Maintaining Architectural Knowledge Using Context Information",
 * 2012 Joint Working IEEE/IFIP Conference on Software Architecture and European Conference on Software Architecture
 */
public interface ContextInformationProvider {

	/**
	 *
	 * @return id of the context information provider
	 */
	String getId();

	/**
	 *
	 * @return name of the context information provider
	 */
	String getName();

	/**
	 * Calculates the relationship between the issues i1 and i2. Higher values indicate a higher similarity.
	 *
	 * @param i1
	 * @param i2
	 * @return value of relationship
	 */
	double assessRelation(Issue i1, Issue i2);

}
