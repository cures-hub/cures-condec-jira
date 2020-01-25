package de.uhd.ifi.se.decision.management.jira.filtering;

import java.util.List;

import org.jgrapht.graph.AsSubgraph;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.LinkType;

/**
 * Interface for accessing the filtered knowledge graph. The filter criteria are
 * specified in the {@link FilterSettings} class.
 * 
 * @see FilterSettings
 * @see KnowledgeGraph
 */
public interface FilteringManager {

	/**
	 * Returns a list of all knowledge elements that match the
	 * {@link FilterSetting}s.
	 * 
	 * @return list of all knowledge elements that match the {@link FilterSetting}s.
	 */
	List<KnowledgeElement> getAllElementsMatchingFilterSettings();

	/**
	 * Returns the subgraph of the {@link KnowledgeGraph} that matches the
	 * {@link FilterSetting}s.
	 * 
	 * @return subgraph of the {@link KnowledgeGraph} that matches the
	 *         {@link FilterSetting}s.
	 */
	AsSubgraph<KnowledgeElement, Link> getSubgraphMatchingFilterSettings();

	/**
	 * Checks if the element matches the specified filter criteria in the
	 * {@link FilterSetting}s object.
	 * 
	 * @param element
	 *            {@link KnowledgeElement} object.
	 * @return true if the element matches the specified filter criteria.
	 */
	boolean isElementMatchingFilterSettings(KnowledgeElement element);

	/**
	 * Checks if the element's description, summary, or key contains the given
	 * substring in the {@link FilterSetting}s.
	 * 
	 * @param element
	 *            {@link KnowledgeElement} object.
	 * @return true if the element's description, summary, or key contains the given
	 *         substring.
	 */
	boolean isElementMatchingSubStringFilter(KnowledgeElement element);

	/**
	 * Checks if the element's type equals one of the given {@link KnowledgeType}s
	 * in the {@link FilterSetting}s.
	 * 
	 * @param element
	 *            {@link KnowledgeElement} object.
	 * @return true if the element's type equals one of the given
	 *         {@link KnowledgeType}s.
	 */
	boolean isElementMatchingKnowledgeTypeFilter(KnowledgeElement element);

	/**
	 * Checks if one of the element's outgoing and ingoing edges/links has a link
	 * type that equals one of the given {@link LinkType}s in the
	 * {@link FilterSetting}s.
	 * 
	 * @param element
	 *            {@link KnowledgeElement} object.
	 * @return true if one of the element's outgoing and ingoing edges/links has a
	 *         link type that equals one of the given {@link LinkType}s.
	 */
	boolean isElementMatchingLinkTypeFilter(KnowledgeElement element);

	/**
	 * Checks if the element is created in the given time frame in the
	 * {@link FilterSetting}s. See {@link KnowledgeElement#getCreated()}.
	 * 
	 * @param element
	 *            {@link KnowledgeElement} object.
	 * @return true if the element is created in the given time frame.
	 */
	boolean isElementMatchingTimeFilter(KnowledgeElement element);

	/**
	 * Checks if the element's status equals one of the given
	 * {@link KnowledgeStatus} in the {@link FilterSetting}s.
	 * 
	 * @param element
	 *            {@link KnowledgeElement} object.
	 * @return true if the element's status equals one of the given
	 *         {@link KnowledgeStatus}.
	 */
	boolean isElementMatchingStatusFilter(KnowledgeElement element);

	/**
	 * Checks if the element is documented in one of the given
	 * {@link DocumentationLocation}s in the {@link FilterSetting}s.
	 * 
	 * @param element
	 *            {@link KnowledgeElement} object.
	 * @return true if the element is documented in one of the given
	 *         {@link DocumentationLocation}s.
	 */
	boolean isElementMatchingDocumentationLocationFilter(KnowledgeElement element);

	/**
	 * Returns the filter settings (=criteria) that the filtering manager uses.
	 * 
	 * @return {@link FilterSettings} object.
	 */
	FilterSettings getFilterSettings();

	/**
	 * Sets the filter settings (=criteria) that the filtering manager uses.
	 * 
	 * @param {@link
	 *            FilterSettings} object.
	 */
	void setFilterSettings(FilterSettings filterSettings);

	/**
	 * Returns the {@link ApplicationUser} associated with the filtering manager.
	 * 
	 * @return {@link ApplicationUser} object.
	 */
	ApplicationUser getUser();

	/**
	 * Sets the {@link ApplicationUser} associated with the filtering manager. The
	 * user needs to have the rights to query the database.
	 * 
	 * @param user
	 *            {@link ApplicationUser} object.
	 */
	void setUser(ApplicationUser user);
}