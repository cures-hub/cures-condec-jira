package de.uhd.ifi.se.decision.management.jira.persistence.singlelocations;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.git.ChangedFile;
import de.uhd.ifi.se.decision.management.jira.persistence.GenericLinkManager;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.CodeClassInDatabase;
import net.java.ao.Query;

/**
 * Extends the abstract class
 * {@link AbstractPersistenceManagerForSingleLocation}. Responsible for storing
 * and retrieving code files related to Jira issues (work items or
 * requirements).
 *
 * @see AbstractPersistenceManagerForSingleLocation
 * @see CodeClassInDatabase
 * 
 * @issue How should this class be named?
 * @alternative Call it CodeClassPersistenceManager!
 * @con In Java, files often contain one class, but there can also be inner
 *      classes, which would not be detected.
 * @con In some languages such as JavaScript, there might not be classes but
 *      files.
 * @alternative Call it CodeFilePersistenceManager!
 * @pro Currently, the file name is stored in database and linked to the Jira
 *      issue.
 * 
 * @issue Is it really necessary to store the code files in the database?
 * @decision We store code files in the database to establish links to them.
 * @pro When storing code files they get a unique id that is used for linking.
 *      With this id, links can also be changed by the user.
 */
public class CodeClassPersistenceManager extends AbstractPersistenceManagerForSingleLocation {
	private static final Logger LOGGER = LoggerFactory.getLogger(JiraIssueTextPersistenceManager.class);
	private static final ActiveObjects ACTIVE_OBJECTS = ComponentGetter.getActiveObjects();

	public CodeClassPersistenceManager(String projectKey) {
		this.projectKey = projectKey;
		this.documentationLocation = DocumentationLocation.CODE;
	}

	@Override
	public boolean deleteKnowledgeElement(KnowledgeElement element, ApplicationUser user) {
		if (element == null) {
			return false;
		}
		KnowledgeElement fileToBeDeleted = getKnowledgeElementByName(((ChangedFile) element).getOldName());
		return fileToBeDeleted == null ? false : deleteKnowledgeElement(fileToBeDeleted.getId(), user);
	}

	@Override
	public boolean deleteKnowledgeElement(long id, ApplicationUser user) {
		if (id <= 0) {
			LOGGER.error(
					"Element cannot be deleted since it does not exist (id is less than zero) or the user is null.");
			return false;
		}
		boolean isDeleted = false;
		for (CodeClassInDatabase databaseEntry : ACTIVE_OBJECTS.find(CodeClassInDatabase.class,
				Query.select().where("ID = ?", id))) {
			GenericLinkManager.deleteLinksForElement(id, documentationLocation);
			KnowledgeGraph.getInstance(projectKey).removeVertex(new ChangedFile(databaseEntry));
			isDeleted = CodeClassInDatabase.deleteElement(databaseEntry);
		}
		return isDeleted;
	}

	/**
	 * Deletes all code files ({@link ChangedFile}s) in database.
	 * 
	 * @return true if all files were deleted.
	 */
	public boolean deleteKnowledgeElements() {
		if (projectKey == null || projectKey.isBlank()) {
			LOGGER.error("Elements cannot be deleted since the project key is invalid.");
			return false;
		}
		boolean isDeleted = false;
		for (CodeClassInDatabase databaseEntry : ACTIVE_OBJECTS.find(CodeClassInDatabase.class,
				Query.select().where("PROJECT_KEY = ?", projectKey))) {
			GenericLinkManager.deleteLinksForElement(databaseEntry.getId(), DocumentationLocation.CODE);
			KnowledgeGraph.getInstance(projectKey).removeVertex(new ChangedFile(databaseEntry));
			isDeleted = CodeClassInDatabase.deleteElement(databaseEntry);
		}
		return isDeleted;
	}

	@Override
	public KnowledgeElement getKnowledgeElement(long id) {
		KnowledgeElement element = null;
		for (CodeClassInDatabase databaseEntry : ACTIVE_OBJECTS.find(CodeClassInDatabase.class,
				Query.select().where("ID = ?", id))) {
			element = new ChangedFile(databaseEntry);
		}
		return element;
	}

	@Override
	public KnowledgeElement getKnowledgeElement(String key) {
		long id = ChangedFile.parseIdFromKey(key);
		return getKnowledgeElement(id);
	}

	public KnowledgeElement getKnowledgeElement(KnowledgeElement element) {
		if (element == null || element.getId() <= 0) {
			return null;
		}
		return getKnowledgeElement(element.getId());
	}

	public KnowledgeElement getKnowledgeElementByName(String name) {
		KnowledgeElement element = null;
		for (CodeClassInDatabase databaseEntry : ACTIVE_OBJECTS.find(CodeClassInDatabase.class,
				Query.select().where("PROJECT_KEY = ? AND FILE_NAME = ?", projectKey, name))) {
			element = new ChangedFile(databaseEntry);
		}
		return element;
	}

	@Override
	public List<KnowledgeElement> getKnowledgeElements() {
		List<KnowledgeElement> knowledgeElements = new ArrayList<>();
		for (CodeClassInDatabase databaseEntry : ACTIVE_OBJECTS.find(CodeClassInDatabase.class,
				Query.select().where("PROJECT_KEY = ?", projectKey))) {
			knowledgeElements.add(new ChangedFile(databaseEntry));
		}
		return knowledgeElements;
	}

	@Override
	public List<Link> getInwardLinks(KnowledgeElement element) {
		return GenericLinkManager.getInwardLinks(element);
	}

	@Override
	public List<Link> getOutwardLinks(KnowledgeElement element) {
		return GenericLinkManager.getOutwardLinks(element);
	}

	@Override
	public KnowledgeElement insertKnowledgeElement(KnowledgeElement knowledgeElement, ApplicationUser user) {
		if (knowledgeElement.getDocumentationLocation() != documentationLocation) {
			return null;
		}
		ChangedFile changedFile = (ChangedFile) knowledgeElement;
		ChangedFile existingElement = (ChangedFile) getKnowledgeElementByName(changedFile.getName());
		if (existingElement != null) {
			updateKnowledgeElement(knowledgeElement, user);
			existingElement = (ChangedFile) getKnowledgeElementByName(changedFile.getName());
			existingElement.setCommits(changedFile.getCommits());
			createLinksToJiraIssues(existingElement, user);
			return existingElement;
		}
		CodeClassInDatabase databaseEntry = ACTIVE_OBJECTS.create(CodeClassInDatabase.class);
		setParameters(changedFile, databaseEntry);
		databaseEntry.save();
		ChangedFile newElement = new ChangedFile(databaseEntry);
		newElement.setCommits(changedFile.getCommits());
		createLinksToJiraIssues(newElement, user);

		return newElement;
	}

	private void createLinksToJiraIssues(ChangedFile newElement, ApplicationUser user) {
		for (String key : newElement.getJiraIssueKeys()) {
			Issue jiraIssue = JiraIssuePersistenceManager.getJiraIssue(key);
			KnowledgeElement parentElement = new KnowledgeElement(jiraIssue);
			Link link = new Link(newElement, parentElement);
			KnowledgePersistenceManager.getOrCreate(projectKey).insertLink(link, user);
		}
	}

	private static void setParameters(KnowledgeElement element, CodeClassInDatabase databaseEntry) {
		databaseEntry.setProjectKey(element.getProject().getProjectKey());
		databaseEntry.setFileName(element.getSummary());
		databaseEntry.setLineCount(element.getLineCount());
	}

	@Override
	public ApplicationUser getCreator(KnowledgeElement element) {
		// currently not implemented
		return null;
	}

	@Override
	public boolean updateKnowledgeElement(KnowledgeElement newElement, ApplicationUser user) {
		if (newElement == null || newElement.getProject() == null) {
			return false;
		}
		KnowledgeElement fileToBeUpdated = getKnowledgeElementByName(((ChangedFile) newElement).getOldName());
		if (fileToBeUpdated == null) {
			fileToBeUpdated = getKnowledgeElementByName(((ChangedFile) newElement).getName());
			if (fileToBeUpdated == null) {
				return false;
			}
		}
		newElement.setId(fileToBeUpdated.getId());
		CodeClassInDatabase entry = findDatabaseEntry(newElement);
		if (entry == null) {
			return false;
		}
		setParameters(newElement, entry);
		entry.save();
		createLinksToJiraIssues((ChangedFile) newElement, user);
		return true;
	}

	public CodeClassInDatabase findDatabaseEntry(KnowledgeElement element) {
		Long id = element.getId();
		CodeClassInDatabase entry = null;
		for (CodeClassInDatabase databaseEntry : ACTIVE_OBJECTS.find(CodeClassInDatabase.class,
				Query.select().where("ID = ?", id))) {
			entry = databaseEntry;
		}
		return entry;
	}
}
