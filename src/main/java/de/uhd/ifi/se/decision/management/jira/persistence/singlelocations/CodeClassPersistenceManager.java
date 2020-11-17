package de.uhd.ifi.se.decision.management.jira.persistence.singlelocations;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.diff.DiffEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol.GitCodeClassExtractor;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.git.ChangedFile;
import de.uhd.ifi.se.decision.management.jira.model.git.Diff;
import de.uhd.ifi.se.decision.management.jira.persistence.DecisionGroupManager;
import de.uhd.ifi.se.decision.management.jira.persistence.GenericLinkManager;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.CodeClassInDatabase;
import net.java.ao.Query;

/**
 * Extends the abstract class
 * {@link AbstractPersistenceManagerForSingleLocation}. Responsible for storing
 * and retrieving code classes related to Jira issues (work items).
 *
 * @see AbstractPersistenceManagerForSingleLocation
 * @see CodeClassInDatabase
 * 
 * @issue Is it really necessary to store the code classes in the database?
 * @decision We store code classes in the database to establish links to them.
 * @pro When storing code classes they get a unique id that is used for linking.
 *      With this id, links can also be changed by the user.
 */
public class CodeClassPersistenceManager extends AbstractPersistenceManagerForSingleLocation {
	private static final Logger LOGGER = LoggerFactory.getLogger(JiraIssueTextPersistenceManager.class);
	private static final ActiveObjects ACTIVE_OBJECTS = ComponentGetter.getActiveObjects();

	public CodeClassPersistenceManager(String projectKey) {
		this.projectKey = projectKey;
		this.documentationLocation = DocumentationLocation.COMMIT;
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
			GenericLinkManager.deleteLinksForElement(id, DocumentationLocation.COMMIT);
			KnowledgeGraph.getOrCreate(projectKey).removeVertex(new KnowledgeElement(databaseEntry));
			isDeleted = CodeClassInDatabase.deleteElement(databaseEntry);
		}
		return isDeleted;
	}

	public boolean deleteKnowledgeElements() {
		if (projectKey == null || projectKey.isBlank()) {
			LOGGER.error("Elements cannot be deleted since the project key is invalid.");
			return false;
		}
		boolean isDeleted = false;
		for (CodeClassInDatabase databaseEntry : ACTIVE_OBJECTS.find(CodeClassInDatabase.class,
				Query.select().where("PROJECT_KEY = ?", projectKey))) {
			GenericLinkManager.deleteLinksForElement(databaseEntry.getId(), DocumentationLocation.COMMIT);
			KnowledgeGraph.getOrCreate(projectKey).removeVertex(new KnowledgeElement(databaseEntry));
			isDeleted = CodeClassInDatabase.deleteElement(databaseEntry);
		}
		return isDeleted;
	}

	@Override
	public KnowledgeElement getKnowledgeElement(long id) {
		KnowledgeElement element = null;
		for (CodeClassInDatabase databaseEntry : ACTIVE_OBJECTS.find(CodeClassInDatabase.class,
				Query.select().where("ID = ?", id))) {
			element = new KnowledgeElement(databaseEntry);
		}
		return element;
	}

	@Override
	public KnowledgeElement getKnowledgeElement(String key) {
		String[] split = key.split("-");
		String id = "0";
		if (split.length > 1) {
			id = key.split("-")[1];
		}
		return getKnowledgeElement(Long.parseLong(id));
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
				Query.select().where("FILE_NAME = ?", name))) {
			element = new KnowledgeElement(databaseEntry);
		}
		return element;
	}

	@Override
	public List<KnowledgeElement> getKnowledgeElements() {
		List<KnowledgeElement> knowledgeElements = new ArrayList<KnowledgeElement>();
		for (CodeClassInDatabase databaseEntry : ACTIVE_OBJECTS.find(CodeClassInDatabase.class,
				Query.select().where("PROJECT_KEY = ?", projectKey))) {
			knowledgeElements.add(new KnowledgeElement(databaseEntry));
		}
		return knowledgeElements;
	}

	public List<KnowledgeElement> getKnowledgeElementsMatchingName(String fileName) {
		List<KnowledgeElement> knowledgeElements = new ArrayList<KnowledgeElement>();
		for (CodeClassInDatabase databaseEntry : ACTIVE_OBJECTS.find(CodeClassInDatabase.class,
				Query.select().where("PROJECT_KEY = ? AND FILE_NAME = ?", projectKey, fileName))) {
			knowledgeElements.add(new KnowledgeElement(databaseEntry));
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

	/**
	 * @param changedFile
	 *            {@link ChangedFile} in the git repository, e.g. a Java class.
	 * @param jiraIssueKeys
	 *            all keys of the Jira issues that the file was changed in.
	 * @param user
	 *            authenticated Jira {@link ApplicationUser}.
	 * @return {@link KnowledgeElement} that is now filled with an internal database
	 *         id and key. Returns null if insertion failed. Establishes links to
	 *         all Jira issues.
	 */
	public KnowledgeElement insertKnowledgeElement(ChangedFile changedFile, ApplicationUser user) {
		changedFile.setProject(projectKey);

		KnowledgeElement element = null;

		for (String key : changedFile.getJiraIssueKeys()) {
			Issue jiraIssue = JiraIssuePersistenceManager.getJiraIssue(key);
			KnowledgeElement parentElement = new KnowledgeElement(jiraIssue);
			element = insertKnowledgeElement(changedFile, user, parentElement);
		}

		return element;
	}

	@Override
	// TODO Refactor, decrease complexity
	public KnowledgeElement insertKnowledgeElement(KnowledgeElement element, ApplicationUser user) {
		KnowledgeElement existingElement = checkIfElementExistsInDatabase(element);
		if (existingElement != null) {
			return existingElement;
		}
		CodeClassInDatabase databaseEntry = ACTIVE_OBJECTS.create(CodeClassInDatabase.class);
		setParameters(element, databaseEntry);
		try {
			databaseEntry.save();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		KnowledgeElement newElement = new KnowledgeElement(databaseEntry);
		if (newElement.getId() <= 0) {
			return newElement;
		}
		KnowledgeGraph.getOrCreate(projectKey).addVertex(newElement);
		AbstractPersistenceManagerForSingleLocation persistenceManager = KnowledgePersistenceManager
				.getOrCreate(projectKey).getJiraIssueManager();
		List<String> groupsToAssign = new ArrayList<String>();
		for (String key : element.getDescription().split(";")) {
			if (!key.isBlank()) {
				KnowledgeElement issueElement = persistenceManager.getKnowledgeElement(key);
				if (issueElement != null) {
					if (issueElement.getDecisionGroups() != null) {
						groupsToAssign.addAll(issueElement.getDecisionGroups());
					}
					Link link = new Link(newElement, issueElement);
					long databaseId = GenericLinkManager.insertLink(link, null);
					if (databaseId > 0) {
						link.setId(databaseId);
						KnowledgeGraph.getOrCreate(projectKey).addEdge(link);
					}
				}
			}
		}
		for (String group : groupsToAssign) {
			DecisionGroupManager.insertGroup(group, newElement);
		}

		return newElement;
	}

	private KnowledgeElement checkIfElementExistsInDatabase(KnowledgeElement element) {
		KnowledgeElement existingElement = new KnowledgeElement();
		if (element.getId() > 0) {
			existingElement = getKnowledgeElement(element);
		} else {
			existingElement = getKnowledgeElementByName(element.getSummary());
		}
		if (existingElement != null) {
			return existingElement;
		}
		return null;
	}

	private static void setParameters(KnowledgeElement element, CodeClassInDatabase databaseEntry) {
		databaseEntry.setProjectKey(element.getProject().getProjectKey());
		String issueKeys = "";
		for (String key : element.getDescription().split(";")) {
			if (key.contains("-")) {
				issueKeys = issueKeys + key.split("-")[1] + ";";
			}
		}
		if (issueKeys.length() > 255) {
			issueKeys = issueKeys.substring(0, 255);
			while (issueKeys.charAt(issueKeys.length() - 1) != ';') {
				issueKeys = issueKeys.substring(0, issueKeys.length() - 2);
			}
		}
		databaseEntry.setFileName(element.getSummary());
	}

	@Override
	public ApplicationUser getCreator(KnowledgeElement element) {
		return element.getCreator();
	}

	@Override
	public boolean updateKnowledgeElement(KnowledgeElement newElement, ApplicationUser user) {
		if (newElement == null || newElement.getProject() == null) {
			return false;
		}
		CodeClassInDatabase entry = getEntryForKnowledgeElement(newElement);
		if (entry == null) {
			return false;
		}
		setParameters(newElement, entry);
		entry.save();
		return true;
	}

	public CodeClassInDatabase getEntryForKnowledgeElement(KnowledgeElement element) {
		Long id = element.getId();
		CodeClassInDatabase entry = null;
		for (CodeClassInDatabase databaseEntry : ACTIVE_OBJECTS.find(CodeClassInDatabase.class,
				Query.select().where("ID = ?", id))) {
			entry = databaseEntry;
		}
		return entry;
	}

	public void maintainCodeClassKnowledgeElements(Diff diff) {
		if (diff == null || diff.getChangedFiles().isEmpty()) {
			return;
		}

		GitCodeClassExtractor ccExtractor = new GitCodeClassExtractor(projectKey);
		for (ChangedFile changedFile : diff.getChangedFiles()) {
			updateCodeClassInDatabase(ccExtractor, changedFile);
		}
	}

	private void updateCodeClassInDatabase(GitCodeClassExtractor ccExtractor, ChangedFile changedFile) {
		if (!changedFile.isJavaClass()) {
			return;
		}
		DiffEntry diffEntry = changedFile.getDiffEntry();
		switch (diffEntry.getChangeType()) {
		case ADD:
			diffAdd(null, ccExtractor, changedFile);
			break;
		case DELETE:
			diffDelete(null, ccExtractor, changedFile);
			break;
		case MODIFY:
			diffModify(null, ccExtractor, changedFile);
		case RENAME:
			// same as modify, thus, no break after modify to fall through
			break;
		default:
			break;
		}
	}

	private void diffAdd(ApplicationUser user, GitCodeClassExtractor ccExtractor, ChangedFile changedFile) {
		insertKnowledgeElement(changedFile, user);
	}

	private void diffModify(ApplicationUser user, GitCodeClassExtractor ccExtractor, ChangedFile changedFile) {
		KnowledgeElement element = getKnowledgeElementByName(changedFile.getName());
		deleteKnowledgeElement(element, user);
		diffAdd(user, ccExtractor, changedFile);
	}

	private void diffDelete(ApplicationUser user, GitCodeClassExtractor ccExtractor, ChangedFile changedFile) {
		List<KnowledgeElement> elements = getKnowledgeElementsMatchingName(changedFile.getOldName());
		for (KnowledgeElement element : elements) {
			// if (ccExtractor.getJiraIssueKeysForFile(changedFile) == null) {
			// deleteKnowledgeElement(element, user);
			// }
		}
	}
}
