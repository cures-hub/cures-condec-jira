package de.uhd.ifi.se.decision.management.jira.persistence.singlelocations;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.user.ApplicationUser;
import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.extraction.GitClient;
import de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol.GitCodeClassExtractor;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.persistence.DecisionGroupManager;
import de.uhd.ifi.se.decision.management.jira.persistence.GenericLinkManager;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.CodeClassInDatabase;
import net.java.ao.Query;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
		String id = key.split("-")[1];
		return getKnowledgeElement(Long.parseLong(id));
	}

	public KnowledgeElement getKnowledgeElement(KnowledgeElement element) {
		if (element == null || element.getId() <= 0) {
			return null;
		}
		return this.getKnowledgeElement(element.getId());
	}

	public KnowledgeElement getKnowledgeElementByNameAndIssueKeys(String name, String issueKeys) {
		KnowledgeElement element = null;
		String issueKeysWithRemove = issueKeys;
		if (issueKeys.contains("-")) {
			issueKeysWithRemove = removeProjectKey(issueKeys, issueKeys.split("-")[0]);
		}
		for (CodeClassInDatabase databaseEntry : ACTIVE_OBJECTS.find(CodeClassInDatabase.class,
				Query.select().where("FILE_NAME = ? AND JIRA_ISSUE_KEYS = ?", name, issueKeysWithRemove))) {
			element = new KnowledgeElement(databaseEntry);
		}
		return element;
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

	@Override
	public List<KnowledgeElement> getKnowledgeElements() {
		List<KnowledgeElement> decisionKnowledgeElements = new ArrayList<KnowledgeElement>();
		for (CodeClassInDatabase databaseEntry : ACTIVE_OBJECTS.find(CodeClassInDatabase.class,
				Query.select().where("PROJECT_KEY = ?", projectKey))) {
			decisionKnowledgeElements.add(new KnowledgeElement(databaseEntry));
		}
		return decisionKnowledgeElements;
	}

	public List<KnowledgeElement> getKnowledgeElementsMatchingName(String fileName) {
		List<KnowledgeElement> decisionKnowledgeElements = new ArrayList<KnowledgeElement>();
		for (CodeClassInDatabase databaseEntry : ACTIVE_OBJECTS.find(CodeClassInDatabase.class,
				Query.select().where("PROJECT_KEY = ? AND FILE_NAME = ?", projectKey, fileName))) {
			decisionKnowledgeElements.add(new KnowledgeElement(databaseEntry));
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
			e.printStackTrace();
		}
		KnowledgeElement newElement = new KnowledgeElement(databaseEntry);
		if (newElement.getId() > 0) {
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
		}
		return newElement;
	}

	private String removeProjectKey(String oldString, String projectKey) {
		String newString = "";
		for (String key : oldString.split(";")) {
			String keyWithRemove = key.replace(projectKey + "-", "");
			newString = newString + keyWithRemove + ";";
		}
		return newString;
	}

	private KnowledgeElement checkIfElementExistsInDatabase(KnowledgeElement element) {
		KnowledgeElement existingElement = new KnowledgeElement();
		if (element.getId() > 0) {
			existingElement = getKnowledgeElement(element);
		} else {
			existingElement = getKnowledgeElementByNameAndIssueKeys(element.getSummary(),
					removeProjectKey(element.getDescription(), element.getProject().getProjectKey()));
		}
		if (existingElement != null) {
			return existingElement;
		}
		return null;
	}

	private static void setParameters(KnowledgeElement element, CodeClassInDatabase databaseEntry) {
		databaseEntry.setProjectKey(element.getProject().getProjectKey());
		databaseEntry.setType(element.getTypeAsString());
		String issuekeys = "";
		for (String key : element.getDescription().split(";")) {
			if (key.contains("-")) {
				issuekeys = issuekeys + key.split("-")[1] + ";";
			}
		}
		if (issuekeys.length() > 255) {
			issuekeys = issuekeys.substring(0, 255);
			while (issuekeys.charAt(issuekeys.length() - 1) != ';') {
				issuekeys = issuekeys.substring(0, issuekeys.length() - 2);
			}
		}
		databaseEntry.setJiraIssueKeys(issuekeys);
		databaseEntry.setFileName(element.getSummary());
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

	public void maintainCodeClassKnowledgeElements(String repoUri, ObjectId oldHead, ObjectId newhead) {
		List<KnowledgeElement> existingElements = getKnowledgeElements();
		if (existingElements == null || existingElements.size() == 0) {
			extractAllCodeClasses(null);
		} else {
			GitCodeClassExtractor ccExtractor = new GitCodeClassExtractor(projectKey);
			GitClient gitClient = new GitClient(projectKey);
			ObjectReader reader = gitClient.getRepository(repoUri).newObjectReader();
			CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
			try {
				oldTreeIter.reset(reader, oldHead);
				CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
				newTreeIter.reset(reader, newhead);
				String gitPath = ccExtractor.getGitClient().getDirectory(repoUri).getAbsolutePath();
				gitPath = gitPath.substring(0, gitPath.length() - 5);
				for (DiffEntry diff : gitClient.getGit(repoUri).diff().setNewTree(newTreeIter).setOldTree(oldTreeIter).call()) {
					if (diff.getChangeType().equals(DiffEntry.ChangeType.DELETE) && diff.getOldPath().contains(".java")) {
						diffDelete(null, ccExtractor, gitPath, diff);
					} else if (diff.getNewPath().contains(".java")) {
						File file = new File(gitPath, diff.getNewPath());
						if (diff.getChangeType().equals(DiffEntry.ChangeType.RENAME)
								|| diff.getChangeType().equals(DiffEntry.ChangeType.MODIFY)) {
							diffModify(null, ccExtractor, gitPath, diff, getKnowledgeElementByNameAndIssueKeys(file.getName(), getIssueListAsString(ccExtractor.getIssuesKeysForFile(file))));
						} else if (diff.getChangeType().equals(DiffEntry.ChangeType.ADD)) {
							diffAdd(null, ccExtractor, gitPath, diff);
						}
					}
				}
			} catch (IOException | GitAPIException e) {
				e.printStackTrace();
				gitClient.closeAll();
				ccExtractor.close();
			}
			gitClient.closeAll();
			ccExtractor.close();
		}
	}

	private void diffAdd(ApplicationUser user, GitCodeClassExtractor ccExtractor, String gitPath, DiffEntry diff) {
		File newFile = new File(gitPath, diff.getNewPath());
		insertKnowledgeElement(ccExtractor.createKnowledgeElementFromFile(newFile, ccExtractor.getIssuesKeysForFile(newFile)), user);
	}

	private void diffModify(ApplicationUser user, GitCodeClassExtractor ccExtractor, String gitPath, DiffEntry diff, KnowledgeElement element) {
		deleteKnowledgeElement(element, user);
		diffAdd(user, ccExtractor, gitPath, diff);
	}

	private void diffDelete(ApplicationUser user, GitCodeClassExtractor ccExtractor, String gitPath, DiffEntry diff) {
		File file = new File(gitPath, diff.getOldPath());
		List<KnowledgeElement> elements = getKnowledgeElementsMatchingName(file.getName());
		for (KnowledgeElement element : elements) {
			if (ccExtractor.getIssuesKeysForFile(file) == null) {
				deleteKnowledgeElement(element, user);
			}
		}
	}

	private void extractAllCodeClasses(ApplicationUser user) {
		GitCodeClassExtractor ccExtractor = new GitCodeClassExtractor(projectKey);
		List<File> codeClasses = ccExtractor.getCodeClassListFull();
		for (File file : codeClasses) {
			List<String> issueKeys = ccExtractor.getIssuesKeysForFile(file);
			if (issueKeys != null && issueKeys.size() > 0) {
				insertKnowledgeElement(ccExtractor.createKnowledgeElementFromFile(file, issueKeys), user);
			}
		}
		ccExtractor.close();
	}

}
