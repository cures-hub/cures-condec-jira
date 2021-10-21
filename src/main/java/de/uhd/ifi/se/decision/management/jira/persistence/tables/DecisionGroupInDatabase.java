package de.uhd.ifi.se.decision.management.jira.persistence.tables;

import java.sql.SQLException;

import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import net.java.ao.RawEntity;
import net.java.ao.schema.AutoIncrement;
import net.java.ao.schema.PrimaryKey;
import net.java.ao.schema.Table;

/**
 * Interface for groups of decisions and solution options (alternatives).
 * Determines which table columns are used for object relational mapping to the
 * database.
 * 
 * @see KnowledgeElement
 */
@Table("CondecDecGroup")
public interface DecisionGroupInDatabase extends RawEntity<Long> {

	@AutoIncrement
	@PrimaryKey("ID")
	long getId();

	void setId(long id);

	/**
	 * @return name of the group/level, e.g. "high level" or "UI".
	 */
	String getGroup();

	/**
	 * @param group
	 *            name of the group/level, e.g. "high level" or "UI".
	 */
	void setGroup(String group);

	/**
	 * @return id of the {@link KnowledgeElement} that this group is assigned to.
	 */
	long getSourceId();

	/**
	 * @param id
	 *            of the {@link KnowledgeElement} that this group is assigned to.
	 */
	void setSourceId(long id);

	/**
	 * @return {@link DocumentationLocation#getIdentifier()} of the
	 *         {@link KnowledgeElement} that this group is assigned to.
	 */
	String getSourceDocumentationLocation();

	/**
	 * @param documentationLocation
	 *            {@link DocumentationLocation#getIdentifier()} of the
	 *            {@link KnowledgeElement} that this group is assigned to.
	 */
	void setSourceDocumentationLocation(String documentationLocation);

	String getProjectKey();

	void setProjectKey(String projectKey);

	/**
	 * Deletes the {@link DecisionGroupInDatabase} object, i.e., removes it from
	 * database.
	 *
	 * @param groupAssignmentToDelete
	 *            {@link DecisionGroupInDatabase} object.
	 * @return true if deletion was successful, false otherwise.
	 */
	static boolean deleteGroup(DecisionGroupInDatabase groupAssignmentToDelete) {
		try {
			groupAssignmentToDelete.getEntityManager().delete(groupAssignmentToDelete);
			return true;
		} catch (SQLException e) {
			return false;
		}
	}
}
