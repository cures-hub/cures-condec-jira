package de.uhd.ifi.se.decision.management.jira.persistence;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.user.ApplicationUser;
import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.extraction.GitClient;
import de.uhd.ifi.se.decision.management.jira.extraction.impl.GitClientImpl;
import de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol.GitCodeClassExtractor;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.impl.KnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.model.impl.LinkImpl;
import de.uhd.ifi.se.decision.management.jira.persistence.impl.AbstractPersistenceManagerForSingleLocation;
import de.uhd.ifi.se.decision.management.jira.persistence.impl.GenericLinkManager;
import de.uhd.ifi.se.decision.management.jira.persistence.impl.JiraIssueTextPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.CodeClassElementInDatabase;
import net.java.ao.Query;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CodeClassKnowledgeElementPersistenceManager extends AbstractPersistenceManagerForSingleLocation {
	private static final Logger LOGGER = LoggerFactory.getLogger(JiraIssueTextPersistenceManager.class);
	private static final ActiveObjects ACTIVE_OBJECTS = ComponentGetter.getActiveObjects();

	public CodeClassKnowledgeElementPersistenceManager(String projectKey) {
		this.projectKey = projectKey;
		this.documentationLocation = DocumentationLocation.COMMIT;
	}

	@Override
	public boolean deleteDecisionKnowledgeElement(long id, ApplicationUser user) {
		if (id <= 0 || user == null) {
			LOGGER.error(
					"Element cannot be deleted since it does not exist (id is less than zero) or the user is null.");
			return false;
		}
		boolean isDeleted = false;
		for (CodeClassElementInDatabase databaseEntry : ACTIVE_OBJECTS.find(CodeClassElementInDatabase.class,
				Query.select().where("ID = ?", id))) {
			GenericLinkManager.deleteLinksForElement(id, DocumentationLocation.COMMIT);
			KnowledgeGraph.getOrCreate(projectKey).removeVertex(new KnowledgeElementImpl(databaseEntry));
			isDeleted = CodeClassElementInDatabase.deleteElement(databaseEntry);
		}
		return isDeleted;
	}

	@Override
	public KnowledgeElement getDecisionKnowledgeElement(long id) {
		KnowledgeElement element = null;
		for (CodeClassElementInDatabase databaseEntry : ACTIVE_OBJECTS.find(CodeClassElementInDatabase.class,
				Query.select().where("ID = ?", id))) {
			element = new KnowledgeElementImpl(databaseEntry);
		}
		return element;
	}

	@Override
	public KnowledgeElement getDecisionKnowledgeElement(String key) {
		String id = key.split("-")[1];
		return getDecisionKnowledgeElement(id);
	}

	public KnowledgeElement getDecisionKnowledgeElement(KnowledgeElement element) {
		if (element == null || element.getId() <= 0) {
			return null;
		}
		return this.getDecisionKnowledgeElement(element.getId());
	}

	public KnowledgeElement getDecisionKnowledgeElementByNameAndIssueKeys(String name, String issueKeys) {
		KnowledgeElement element = null;
		for (CodeClassElementInDatabase databaseEntry : ACTIVE_OBJECTS.find(CodeClassElementInDatabase.class,
				Query.select().where("FILE_NAME = ? AND JIRA_ISSUE_KEYS = ?", name, issueKeys))) {
			element = new KnowledgeElementImpl(databaseEntry);
		}
		return element;
	}

	public CodeClassElementInDatabase getEntryForKnowledgeElement(KnowledgeElement element) {
		Long id = element.getId();
		for (CodeClassElementInDatabase databaseEntry : ACTIVE_OBJECTS.find(CodeClassElementInDatabase.class,
				Query.select().where("ID = ?", id))) {
			return databaseEntry;
		}
		return null;
	}

	@Override
	public List<KnowledgeElement> getDecisionKnowledgeElements() {
		List<KnowledgeElement> decisionKnowledgeElements = new ArrayList<KnowledgeElement>();
		for (CodeClassElementInDatabase databaseEntry : ACTIVE_OBJECTS.find(CodeClassElementInDatabase.class,
				Query.select().where("PROJECT_KEY = ?", projectKey))) {
			decisionKnowledgeElements.add(new KnowledgeElementImpl(databaseEntry));
		}
		return decisionKnowledgeElements;
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
	public KnowledgeElement insertDecisionKnowledgeElement(KnowledgeElement element, ApplicationUser user) {
		KnowledgeElement existingElement = checkIfElementExistsInDatabase(element);
		if (existingElement != null) {
			return existingElement;
		}
		CodeClassElementInDatabase databaseEntry = ACTIVE_OBJECTS.create(CodeClassElementInDatabase.class);

		setParameters(element, databaseEntry);
		databaseEntry.save();

		KnowledgeElement newElement = new KnowledgeElementImpl(databaseEntry);
		if (newElement.getId() > 0) {
			KnowledgeGraph.getOrCreate(projectKey).addVertex(newElement);
			AbstractPersistenceManagerForSingleLocation persistenceManager = KnowledgePersistenceManager
					.getOrCreate(projectKey).getJiraIssueManager();
			for (String key : element.getDescription().split(";")) {
				KnowledgeElement issueElement = persistenceManager.getDecisionKnowledgeElement(key);
				Link link = new LinkImpl(element, issueElement);
				GenericLinkManager.insertLink(link, user);
			}
		}
		return newElement;
	}

	private KnowledgeElement checkIfElementExistsInDatabase(KnowledgeElement element) {
		KnowledgeElement existingElement = getDecisionKnowledgeElement(element);
		if (existingElement != null) {
			return existingElement;
		}
		return null;
	}

	private static void setParameters(KnowledgeElement element, CodeClassElementInDatabase databaseEntry) {
		databaseEntry.setProjectKey(element.getProject().getProjectKey());
		databaseEntry.setType(element.getTypeAsString());
		String issueKeys = "";
		for (Link link : element.getLinks()) {
			String issueKey = link.getTarget().getKey();
			issueKeys = issueKeys + issueKey + ";";
		}
		databaseEntry.setJiraIssueKeys(issueKeys);
		databaseEntry.setFileName(element.getSummary());
	}

	public List<String> getIssueKeysAsList(KnowledgeElement element) {
		List<String> issueKeys = new ArrayList<String>();
		for (Link link : element.getLinks()) {
			issueKeys.add(link.getTarget().getKey());
		}
		return issueKeys;
	}

	public String getIssueListAsString(List<String> list) {
		String keys = "";
		for (String key : list) {
			keys = keys + key + ";";
		}
		return keys;
	}

	@Override
	public ApplicationUser getCreator(KnowledgeElement element) {
		return null;
	}

	public boolean updateDecisionKnowledgeElement(KnowledgeElement newElement, ApplicationUser user) {
		if (newElement == null || newElement.getProject() == null) {
			return false;
		}
		CodeClassElementInDatabase entry = getEntryForKnowledgeElement(newElement);
		if (entry == null) {
			return false;
		}
		setParameters(newElement, entry);
		entry.save();
		return true;
	}

	public List<KnowledgeElement> getKnowledgeElementsForJiraIssueKey(String issueKey) {
		List<KnowledgeElement> decisionKnowledgeElements = getDecisionKnowledgeElements();
		List<KnowledgeElement> relevantElements = new ArrayList<>();
		for (KnowledgeElement element : decisionKnowledgeElements) {
			if (element.getDescription().contains(issueKey)) {
				relevantElements.add(element);
			}
		}
		return relevantElements;
	}

	public void maintainCodeClassKnowledgeElements(String repoUri, ObjectId oldHead, ObjectId newhead) {
		ApplicationUser user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();
		List<KnowledgeElement> existingElements = getDecisionKnowledgeElements();
		GitCodeClassExtractor ccExtractor = new GitCodeClassExtractor(projectKey);
		List<File> codeClasses = ccExtractor.getCodeClassListFull();
		if (existingElements == null || existingElements.size() == 0) {
			for (File file : codeClasses) {
				// Extract relevant Issue Keys
				List<String> issueKeys = ccExtractor.getIssuesKeysForFile(file);
				KnowledgeElement newElement = ccExtractor.createKnowledgeElementFromFile(file, issueKeys);
				insertDecisionKnowledgeElement(newElement, user);
			}
			ccExtractor.close();
		} else {
			GitClient gitClient = new GitClientImpl(projectKey);
			ObjectReader reader = gitClient.getRepository(repoUri).newObjectReader();
			CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
			try {
				oldTreeIter.reset(reader, oldHead);
				CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
				newTreeIter.reset(reader, newhead);
				List<DiffEntry> diffs = gitClient.getGit(repoUri).diff()
						.setNewTree(newTreeIter)
						.setOldTree(oldTreeIter)
						.call();
				for (DiffEntry diff : diffs) {
					File file = new File(diff.getOldPath());
					List<String> issueKeys = ccExtractor.getIssuesKeysForFile(file);
					String keys = getIssueListAsString(issueKeys);
					KnowledgeElement element = getDecisionKnowledgeElementByNameAndIssueKeys(file.getName(), keys);
					if (diff.getChangeType().equals(DiffEntry.ChangeType.DELETE)) {
						deleteDecisionKnowledgeElement(element, user);
					} else if (diff.getChangeType().equals(DiffEntry.ChangeType.RENAME) || diff.getChangeType().equals(DiffEntry.ChangeType.MODIFY)) {
						deleteDecisionKnowledgeElement(element, user);
						File newFile = new File(diff.getNewPath());
						insertDecisionKnowledgeElement(ccExtractor.createKnowledgeElementFromFile(newFile,
								ccExtractor.getIssuesKeysForFile(newFile)), user);
					} else if (diff.getChangeType().equals(DiffEntry.ChangeType.ADD)) {
						File newFile = new File(diff.getNewPath());
						insertDecisionKnowledgeElement(ccExtractor.createKnowledgeElementFromFile(newFile,
								ccExtractor.getIssuesKeysForFile(newFile)), user);
					}
				}
			} catch (IOException | GitAPIException e) {
				e.printStackTrace();
			} finally {
				gitClient.closeAll();
			}
		}
	}
	//TODO: Keep persistent when pull
	//TODO: Refresh when issue deleted

}
