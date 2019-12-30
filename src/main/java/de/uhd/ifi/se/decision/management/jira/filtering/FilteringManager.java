package de.uhd.ifi.se.decision.management.jira.filtering;

import java.util.List;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.LinkType;

/**
 * Interface for accessing the filtered knowledge graphs. The filter criteria
 * are specified in the {@link FilterSettings} class.
 * 
 * @see FilterSettings
 * @see KnowledgeGraph
 */
public interface FilteringManager {

	List<DecisionKnowledgeElement> getAllElementsMatchingQuery();

	List<DecisionKnowledgeElement> getAllElementsMatchingFilterSettings();

	/**
	 * Checks if the element matches the specified filter criteria in the
	 * {@link FilterSetting}s object.
	 * 
	 * @param element
	 *            {@link DecisionKnowledgeElement} object.
	 * @return true if the element matches the specified filter criteria.
	 */
	boolean isElementMatchingFilterSettings(DecisionKnowledgeElement element);

	/**
	 * Checks if the element's description, summary, or key contains the given
	 * substring in the {@link FilterSetting}s.
	 * 
	 * @param element
	 *            {@link DecisionKnowledgeElement} object.
	 * @return true if the element's description, summary, or key contains the given
	 *         substring.
	 */
	boolean isElementMatchingSubStringFilter(DecisionKnowledgeElement element);

	/**
	 * Checks if the element's description, summary, or key contains the given
	 * substring in the {@link FilterSetting}s.
	 * 
	 * @param element
	 *            {@link DecisionKnowledgeElement} object.
	 * @return true if the element's description, summary, or key contains the given
	 *         substring.
	 */
	boolean isElementMatchingJiraQueryFilter(DecisionKnowledgeElement element);

	/**
	 * Checks if the element's type equals one of the given {@link KnowledgeType}s
	 * in the {@link FilterSetting}s.
	 * 
	 * @param element
	 *            {@link DecisionKnowledgeElement} object.
	 * @return true if the element's type equals one of the given
	 *         {@link KnowledgeType}s.
	 */
	boolean isElementMatchingKnowledgeTypeFilter(DecisionKnowledgeElement element);

	/**
	 * Checks if one of the element's outgoing and ingoing edges/links has a link
	 * type that equals one of the given {@link LinkType}s in the
	 * {@link FilterSetting}s.
	 * 
	 * @param element
	 *            {@link DecisionKnowledgeElement} object.
	 * @return true if one of the element's outgoing and ingoing edges/links has a
	 *         link type that equals one of the given {@link LinkType}s.
	 */
	boolean isElementMatchingLinkTypeFilter(DecisionKnowledgeElement element);

	/**
	 * Checks if the element is created in the given time frame in the
	 * {@link FilterSetting}s. See {@link DecisionKnowledgeElement#getCreated()}.
	 * 
	 * @param element
	 *            {@link DecisionKnowledgeElement} object.
	 * @return true if the element is created in the given time frame.
	 */
	boolean isElementMatchingTimeFilter(DecisionKnowledgeElement element);

	/**
	 * Checks if the element's status equals one of the given
	 * {@link KnowledgeStatus} in the {@link FilterSetting}s.
	 * 
	 * @param element
	 *            {@link DecisionKnowledgeElement} object.
	 * @return true if the element's status equals one of the given
	 *         {@link KnowledgeStatus}.
	 */
	boolean isElementMatchingStatusFilter(DecisionKnowledgeElement element);

	/**
	 * Checks if the element is documented in one of the given
	 * {@link DocumentationLocation}s in the {@link FilterSetting}s.
	 * 
	 * @param element
	 *            {@link DecisionKnowledgeElement} object.
	 * @return true if the element is documented in one of the given
	 *         {@link DocumentationLocation}s.
	 */
	boolean isElementMatchingDocumentationLocationFilter(DecisionKnowledgeElement element);

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