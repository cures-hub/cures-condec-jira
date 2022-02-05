package de.uhd.ifi.se.decision.management.jira.model;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlElement;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm.SingleSourcePaths;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.alg.shortestpath.TreeSingleSourcePathsImpl;
import org.jgrapht.alg.util.Pair;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.changehistory.ChangeHistory;
import com.atlassian.jira.issue.link.IssueLink;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.persistence.DecisionGroupPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.DecisionGroupInDatabase;
import de.uhd.ifi.se.decision.management.jira.quality.DefinitionOfDone;
import de.uhd.ifi.se.decision.management.jira.quality.DefinitionOfDoneChecker;

/**
 * Models knowledge elements, e.g., decision knowledge elements, requirements,
 * work items, or code classes. These elements are nodes of the knowledge graph
 * and connected by links/edges/relationships.
 *
 * @see KnowledgeGraph
 * @see Link
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class KnowledgeElement {

	protected long id;
	protected DecisionKnowledgeProject project;
	private String summary;
	private String description;
	protected KnowledgeType type;
	private String key;
	protected TreeMap<Date, String> updateDateAndAuthor;
	protected DocumentationLocation documentationLocation;
	protected Origin origin;
	protected KnowledgeStatus status;

	public KnowledgeElement() {
		this.description = "";
		this.summary = "";
		this.type = KnowledgeType.OTHER;
		// the origin is the same as the documentation location per default
		this.origin = Origin.DOCUMENTATION_LOCATION;
		this.updateDateAndAuthor = new TreeMap<Date, String>();
	}

	public KnowledgeElement(long id, String summary, String description, KnowledgeType type, String projectKey,
			String key, DocumentationLocation documentationLocation, KnowledgeStatus status) {
		this();
		this.id = id;
		this.summary = summary;
		this.description = description;
		this.type = type;
		this.project = new DecisionKnowledgeProject(projectKey);
		this.key = key;
		this.documentationLocation = documentationLocation;
		this.status = status;
	}

	public KnowledgeElement(long id, String projectKey, String documentationLocation) {
		this.id = id;
		this.project = new DecisionKnowledgeProject(projectKey);
		this.documentationLocation = DocumentationLocation
				.getDocumentationLocationFromIdentifier(documentationLocation);
	}

	public KnowledgeElement(long id, String summary, String description, String type, String projectKey, String key,
			DocumentationLocation documentationLocation, String status) {
		this(id, summary, description, KnowledgeType.getKnowledgeType(type), projectKey, key, documentationLocation,
				KnowledgeStatus.getKnowledgeStatus(status));
	}

	public KnowledgeElement(Issue issue) {
		this();
		if (issue == null) {
			return;
		}
		this.id = issue.getId();
		this.summary = issue.getSummary();
		this.description = issue.getDescription();
		if (issue.getIssueType() != null) {
			this.type = KnowledgeType.getKnowledgeType(issue.getIssueType().getName());
		}
		if (issue.getProjectObject() != null) {
			this.project = new DecisionKnowledgeProject(issue.getProjectObject().getKey());
		}
		this.key = issue.getKey();
		this.documentationLocation = DocumentationLocation.JIRAISSUE;
		if (issue.getCreated() != null) {
			updateDateAndAuthor.put(issue.getCreated(), "");
		}
		List<ChangeHistory> changeHistory = ComponentAccessor.getChangeHistoryManager().getChangeHistories(issue);
		changeHistory.forEach(changeItem -> {
			updateDateAndAuthor.put(changeItem.getTimePerformed(), changeItem.getAuthorDisplayName());
		});

		if (issue.getStatus() != null) {
			this.status = KnowledgeStatus.getKnowledgeStatus(issue.getStatus().getName());
		}
	}

	/**
	 * @return id of the knowledge element. This id is the internal database id.
	 *         When using Jira issues to persist knowledge, this id is different to
	 *         the project internal id that is part of the key.
	 */
	@XmlElement
	public long getId() {
		return id;
	}

	/**
	 * @param id
	 *            of the knowledge element. This id is the internal database id.
	 *            When using Jira issues to persist knowledge, this id is different
	 *            to the project internal id that is part of the key.
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return summary of the knowledge element. The summary is a short description
	 *         of the element.
	 */
	@XmlElement
	public String getSummary() {
		return summary;
	}

	/**
	 * @param summary
	 *            of the knowledge element. The summary is a short description of
	 *            the element.
	 */
	public void setSummary(String summary) {
		this.summary = summary;
	}

	/**
	 * @return description of the knowledge element. The description provides
	 *         details about the element. When using Jira issues to persist
	 *         knowledge, it can include images and other fancy stuff.
	 */
	@XmlElement
	public String getDescription() {
		return description;
	}

	/**
	 * @return concatenated String of the summary and the description.
	 */
	public String getText() {
		return summary + " " + description;
	}

	/**
	 * @param description
	 *            of the knowledge element. The description provides details about
	 *            the element. When using Jira issues to persist knowledge, it can
	 *            include images and other fancy stuff.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @see KnowledgeType
	 * @return type of the knowledge element. For example, types are decision,
	 *         alternative, issue, and argument.
	 */
	public KnowledgeType getType() {
		return type;
	}

	/**
	 * @see KnowledgeType
	 * @return type of the knowledge element as a String. For example, types are
	 *         decision, alternative, issue, and argument. This methods returns the
	 *         type of Jira issues that are no knowledge elements.
	 */
	@XmlElement(name = "type")
	public String getTypeAsString() {
		if (getType() == KnowledgeType.OTHER && getDocumentationLocation() == DocumentationLocation.JIRAISSUE) {
			IssueManager issueManager = ComponentAccessor.getIssueManager();
			Issue issue = issueManager.getIssueByCurrentKey(getKey());
			return issue != null ? issue.getIssueType().getName() : "";
		}
		return getType().toString();
	}

	/**
	 * @see KnowledgeType
	 * @param type
	 *            of the knowledge element. For example, types are decision,
	 *            alternative, issue, and argument.
	 */
	public void setType(KnowledgeType type) {
		// If the user tries to change a decision to an alternative, the knowledge type
		// cannot be changed (i.e. it stays a decision), but the status of the decision
		// is changed to "rejected".
		if (type == KnowledgeType.ALTERNATIVE && this.type == KnowledgeType.DECISION) {
			this.type = KnowledgeType.DECISION;
			this.status = KnowledgeStatus.REJECTED;
			return;
		}
		// For example, if an alternative is picked as the decision, the status changes
		// from "idea" to "decided".
		if (this.type != type) {
			this.status = KnowledgeStatus.getDefaultStatus(type);
		}
		this.type = type == null ? KnowledgeType.OTHER : type;
	}

	/**
	 * @see KnowledgeType
	 * @param typeAsString
	 *            of the knowledge element. For example, types are decision,
	 *            alternative, issue, and argument.
	 */
	@JsonProperty("type")
	public void setType(String typeAsString) {
		KnowledgeType type = KnowledgeType.getKnowledgeType(typeAsString);
		setType(type);
	}

	/**
	 * @issue How do we access the decision groups/level for a knowledge element?
	 * @decision We query the database table to access the decision groups/level for
	 *           a knowledge element!
	 * @con It seems not to be very efficient to query the database all the time.
	 * @pro We don't have to propagate changes (e.g. renamed groups) to the
	 *      knowledge graph object.
	 * @alternative We could make decision groups/levels a "real" attribute of the
	 *              KnowledgeElement class and only query it from database when the
	 *              element is added to knowledge graph.
	 * @pro Might be more efficient.
	 * @con We would have to propagate changes (e.g. renamed groups) to the
	 *      knowledge graph object.
	 *
	 * @return decision groups (e.g. process, UI, non-existence, ...) and the
	 *         decision level (high level, medium level, or realization level)
	 *         assigned to this knowledge element.
	 * @see DecisionGroupPersistenceManager
	 * @see DecisionGroupInDatabase
	 */
	@XmlElement(name = "groups")
	public List<String> getDecisionGroups() {
		return DecisionGroupPersistenceManager.getGroupsForElement(this);
	}

	/**
	 * @see DecisionKnowledgeProject
	 * @return project that the knowledge element belongs to. The project is a Jira
	 *         project that is extended with settings for this plug-in, for example,
	 *         whether the plug-in is activated for the project.
	 */
	public DecisionKnowledgeProject getProject() {
		return project;
	}

	/**
	 * @see DecisionKnowledgeProject
	 * @param project
	 *            that the knowledge element belongs to. The project is a Jira
	 *            project that is extended with settings for this plug-in, for
	 *            example, whether the plug-in is activated for the project.
	 */
	public void setProject(DecisionKnowledgeProject project) {
		this.project = project;
	}

	/**
	 * @see DecisionKnowledgeProject
	 * @param projectKey
	 *            key of Jira project that the knowledge element belongs to via its
	 *            key. The project is a Jira project that is extended with settings
	 *            for this plug-in, for example, whether the plug-in is activated
	 *            for the project.
	 */
	@JsonProperty("projectKey")
	public void setProject(String projectKey) {
		project = new DecisionKnowledgeProject(projectKey);
	}

	/**
	 * @return key of the knowledge element. The key is composed of
	 *         projectKey-project internal id.
	 */
	@XmlElement
	public String getKey() {
		if (key == null && project != null) {
			return project.getProjectKey() + "-" + id;
		}
		return key;
	}

	/**
	 * @param key
	 *            of the knowledge element. The key is composed of
	 *            projectKey-project internal id.
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @see DocumentationLocation
	 * @return documentation location of the knowledge element. For example,
	 *         knowledge can be documented in commit messages or in the comments to
	 *         a Jira issue.
	 */
	public DocumentationLocation getDocumentationLocation() {
		return documentationLocation;
	}

	/**
	 * @see DocumentationLocation
	 * @param documentationLocation
	 *            of the knowledge element. For example, knowledge can be documented
	 *            in commit messages or in the comments to a Jira issue.
	 */
	public void setDocumentationLocation(DocumentationLocation documentationLocation) {
		this.documentationLocation = documentationLocation;
	}

	/**
	 * @see DocumentationLocation
	 * @return documentation location of the knowledge element as a String. For
	 *         example, knowledge can be documented in commit messages or in the
	 *         comments to a Jira issue.
	 */
	@XmlElement(name = "documentationLocation")
	public String getDocumentationLocationAsString() {
		if (documentationLocation != null) {
			return documentationLocation.getIdentifier();
		}
		return "";
	}

	/**
	 * @see DocumentationLocation
	 * @param documentationLocation
	 *            of the knowledge element. For example, knowledge can be documented
	 *            in commit messages or in the comments and the description of a
	 *            Jira issue.
	 */
	@JsonProperty("documentationLocation")
	public void setDocumentationLocation(String documentationLocation) {
		if (documentationLocation == null || documentationLocation.isBlank()) {
			// TODO Add here persistence strategy chosen in project
			this.documentationLocation = DocumentationLocation.JIRAISSUE;
		}
		this.documentationLocation = DocumentationLocation
				.getDocumentationLocationFromIdentifier(documentationLocation);
	}

	/**
	 * @return {@link Origin} that indicates the source of a knowledge element. The
	 *         origin might be different from the current
	 *         {@link DocumentationLocation}.
	 */
	public Origin getOrigin() {
		return origin;
	}

	/**
	 * @return an URL of the knowledge element as String.
	 */
	@XmlElement
	public String getUrl() {
		String key = getKey();
		String jiraBaseUrl = ComponentAccessor.getApplicationProperties().getString(APKeys.JIRA_BASEURL);
		// TODO Simplify recognition of decision knowledge documented in Jira issue
		// comments/description and code comments, work with inheritance
		if (getDocumentationLocation() == DocumentationLocation.CODE) {
			String urlOfKnowledgePage = jiraBaseUrl + "/projects/" + getProject().getProjectKey()
					+ "?selectedItem=decision-knowledge-page";
			return urlOfKnowledgePage + "&type=" + getTypeAsString();
		}
		if (getDocumentationLocation() == DocumentationLocation.JIRAISSUETEXT) {
			key = key.split(":")[0];
		}
		return jiraBaseUrl + "/browse/" + key;
	}

	/**
	 * @return creation date of the knowledge element.
	 */
	@XmlElement
	public Date getCreationDate() {
		if (!updateDateAndAuthor.isEmpty()) {
			return updateDateAndAuthor.firstKey();
		}
		return new Date();
	}

	/**
	 * @param creationDate
	 *            of creation of the knowledge element.
	 */
	public void setCreationDate(Date creationDate) {
		if (!updateDateAndAuthor.isEmpty() && creationDate != null) {
			String creator = updateDateAndAuthor.firstEntry().getValue();

			// Check whether the new creation date is actually older than the
			// updates stored inside the TreeMap
			TreeMap<Date, String> clonedMap = new TreeMap<Date, String>();
			clonedMap.putAll(updateDateAndAuthor);
			clonedMap.remove(clonedMap.firstKey());
			clonedMap.put(creationDate, creator);
			if (clonedMap.firstKey() == creationDate) {
				updateDateAndAuthor = clonedMap;
			}
		}
	}

	/**
	 * @return sorted map of all update dates and their corresponding authors of the
	 *         knowledge element.
	 */
	public TreeMap<Date, String> getUpdateDateAndAuthor() {
		return updateDateAndAuthor;
	}

	/**
	 * @param updateDateAndAuthor
	 *            map containing the update dates of the knowledge element and their
	 *            corresponding authors.
	 */
	public void setUpdateDateAndAuthor(TreeMap<Date, String> updateDateAndAuthor) {
		this.updateDateAndAuthor = updateDateAndAuthor;
	}

	/**
	 * @return true if the element exists in database.
	 */
	public boolean existsInDatabase() {
		KnowledgeElement elementInDatabase = KnowledgePersistenceManager.getInstance("").getKnowledgeElement(id,
				documentationLocation);
		return elementInDatabase != null && elementInDatabase.getId() > 0;
	}

	/**
	 * @return Jira issue that the knowledge element or irrelevant text is part of.
	 */
	// TODO Improve this method. Code classes are not handled currently.
	public Issue getJiraIssue() {
		if (documentationLocation == DocumentationLocation.JIRAISSUE) {
			return ComponentAccessor.getIssueManager().getIssueObject(id);
		}
		if (documentationLocation == DocumentationLocation.JIRAISSUETEXT && this instanceof PartOfJiraIssueText) {
			return ((PartOfJiraIssueText) this).getJiraIssue();
		}
		// TODO Add ChangedFile
		return null;
	}

	@Override
	public String toString() {
		return getDescription();
	}

	/**
	 * @return creator of an element as an {@link ApplicationUser} object.
	 */
	public ApplicationUser getCreator() {
		KnowledgePersistenceManager persistenceManager = KnowledgePersistenceManager
				.getInstance(project.getProjectKey());
		if (getDocumentationLocation() == DocumentationLocation.JIRAISSUE) {
			return persistenceManager.getJiraIssueManager().getCreator(this);
		}
		if (getDocumentationLocation() == DocumentationLocation.JIRAISSUETEXT) {
			return persistenceManager.getJiraIssueTextManager().getCreator(this);
		}
		return null;
	}

	/**
	 * @return name of the creator of an element as a String.
	 */
	@XmlElement(name = "creator")
	public String getCreatorName() {
		if (!updateDateAndAuthor.isEmpty()) {
			return updateDateAndAuthor.firstEntry().getValue();
		}
		ApplicationUser user = getCreator();
		return user != null ? user.getDisplayName() : "";
	}

	/**
	 * @return date of last update of the knowledge element.
	 */
	@XmlElement
	public Date getLatestUpdatingDate() {
		if (updateDateAndAuthor.isEmpty()) {
			return getCreationDate();
		}
		return updateDateAndAuthor.lastKey();
	}

	/**
	 * @return name of the person who did latest changes to the element as a String.
	 */
	@XmlElement(name = "latestAuthor")
	public String getLatestAuthorName() {
		if (!updateDateAndAuthor.isEmpty()) {
			return updateDateAndAuthor.lastEntry().getValue();
		}
		return getCreatorName();
	}

	/**
	 * @param knowledgeType
	 *            the {@link KnowledgeType} of the element.
	 * @return true if this knowledge element is linked to another knowledge element
	 *         of the given {@link KnowledgeType}.
	 */
	public boolean hasNeighborOfType(KnowledgeType knowledgeType) {
		return !getNeighborsOfType(knowledgeType).isEmpty();
	}

	/**
	 * @param knowledgeType
	 *            the {@link KnowledgeType} of the element.
	 * @return set of other knowledge elements that are linked to this knowledge
	 *         element of the given {@link KnowledgeType}.
	 */
	public Set<KnowledgeElement> getNeighborsOfType(KnowledgeType knowledgeType) {
		KnowledgeGraph graph = KnowledgeGraph.getInstance(project);
		if (graph == null) {
			return new HashSet<>();
		}
		Set<KnowledgeElement> neighbors = Graphs.neighborSetOf(graph, this);
		return neighbors.stream().filter(element -> element.getType() == knowledgeType).collect(Collectors.toSet());
	}

	/**
	 * @return all links (=edges) of this element in the {@link KnowledgeGraph} as a
	 *         set of {@link Link} objects (edge/relationship), does contain Jira
	 *         {@link IssueLink}s and ConDec links (e.g. links between code classes
	 *         and Jira issues).
	 */
	public Set<Link> getLinks() {
		if (project == null) {
			return new HashSet<>();
		}
		return KnowledgeGraph.getInstance(project).edgesOf(this);
	}

	/**
	 * @issue How can we get all knowledge elements within a certain distance/number
	 *        of hops from one element?
	 * @decision Use the ShortestPathAlgorithm of the jGraphT library to find all
	 *           knowledge elements within a certain distance/number of hops from
	 *           one element.
	 * @pro Might be faster than the former implementation using the recursion
	 *      (better performance).
	 * @alternative We used to have our own implementation to get all knowledge
	 *              elements within a certain distance using recursion.
	 * 
	 * @param maxDistance
	 *            maximal link distance that the {@link KnowledgeGraph} is travered
	 *            to search for the other {@link KnowledgeElement}s starting from
	 *            this element.
	 * @return set of all {@link KnowledgeElement}s reachable from this element
	 *         within the maximal link distance. Uses the
	 *         {@link DijkstraShortestPath} algorithm. Assumes that the graph is
	 *         undirected.
	 */
	public Set<KnowledgeElement> getLinkedElements(int maxDistance) {
		return getDistanceAndPredecessorMapForLinkedElements(maxDistance).keySet();
	}

	/**
	 * @param maxDistance
	 *            maximal link distance that the {@link KnowledgeGraph} is travered
	 *            to search for the other {@link KnowledgeElement}s starting from
	 *            this element.
	 * @return map of other reachable knowledge elements and links within a certain
	 *         distance from this element.
	 */
	private Map<KnowledgeElement, Pair<Double, Link>> getDistanceAndPredecessorMapForLinkedElements(int maxDistance) {
		TreeSingleSourcePathsImpl<KnowledgeElement, Link> paths = (TreeSingleSourcePathsImpl<KnowledgeElement, Link>) getAllPaths(
				maxDistance);
		return paths.getDistanceAndPredecessorMap();
	}

	/**
	 * @param maxDistance
	 *            maximal link distance that the {@link KnowledgeGraph} is travered
	 *            to search for the other {@link KnowledgeElement}s starting from
	 *            this element.
	 * @return all paths from this element to other {@link KnowledgeElement}s in the
	 *         {@link KnowledgeGraph} (not filtered) within the maximal link
	 *         distance as a {@link SingleSourcePaths} object. Assumes that the
	 *         graph is undirected.
	 */
	public SingleSourcePaths<KnowledgeElement, Link> getAllPaths(int maxDistance) {
		ShortestPathAlgorithm<KnowledgeElement, Link> pathAlgorithm = getShortestPathAlgorithm(maxDistance);
		return pathAlgorithm.getPaths(this);
	}

	/**
	 * @issue How can we get the link distance/number of hops from one element to
	 *        another element in the knowledge graph?
	 * @decision Use the ShortestPathAlgorithm of the jGraphT library to get the
	 *           link distance/number of hops from one element to another element.
	 * @pro Might be faster than our own implementation (better performance).
	 * @alternative We used to have our own implementation to get the link
	 *              distance/number of hops from one element to another element.
	 * 
	 * @param otherElement
	 *            another element in the {@link KnowledgeGraph}
	 * @param maxDistance
	 *            maximal link distance that the {@link KnowledgeGraph} is travered
	 *            to search for the other {@link KnowledgeElement} starting from
	 *            this element.
	 * @return length of the shortest path between this knowledge element to another
	 *         element in the {@link KnowledgeGraph} within the maximal link
	 *         distance. Uses the {@link DijkstraShortestPath} algorithm. Assumes
	 *         that the graph is undirected.
	 */
	public int getLinkDistance(KnowledgeElement otherElement, int maxDistance) {
		ShortestPathAlgorithm<KnowledgeElement, Link> pathAlgorithm = getShortestPathAlgorithm(maxDistance);
		GraphPath<KnowledgeElement, Link> graphPath = pathAlgorithm.getPath(this, otherElement);
		if (graphPath != null) {
			return graphPath.getLength();
		}
		return -1;
	}

	private ShortestPathAlgorithm<KnowledgeElement, Link> getShortestPathAlgorithm(int maxLinkDistance) {
		KnowledgeGraph graph = KnowledgeGraph.getInstance(project);
		graph.addVertex(this);
		return graph.getShortestPathAlgorithm(maxLinkDistance);
	}

	/**
	 * @param otherElement
	 *            object of {@link KnowledgeElement}.
	 * @return {@link Link} object if there exists a link (= edge or relationship)
	 *         between this knowledge element and the other knowledge element in the
	 *         {@link KnowledgeGraph}. Returns null if no edge/link/relationship can
	 *         be found.
	 */
	public Link getLink(KnowledgeElement otherElement) {
		if (this.equals(otherElement)) {
			return null;
		}
		for (Link link : this.getLinks()) {
			if (link.getOppositeElement(this).equals(otherElement)) {
				return link;
			}
		}
		return null;
	}

	/**
	 * @param targetElement
	 *            object of {@link KnowledgeElement}.
	 * @return {@link Link} object if there exists an outgoing link (= edge or
	 *         relationship) between this knowledge element and the potential target
	 *         knowledge element in the {@link KnowledgeGraph}. Returns null if no
	 *         outgoing edge/link/relationshio can be found.
	 */
	public Link getOutgoingLink(KnowledgeElement targetElement) {
		if (this.equals(targetElement)) {
			return null;
		}
		for (Link link : this.getLinks()) {
			if (link.getTarget().equals(targetElement)) {
				return link;
			}
		}
		return null;
	}

	/**
	 * Determines whether an element is linked to at least one other knowledge
	 * element in the {@link KnowledgeGraph}.
	 *
	 * @return id of first {@link Link} that is found. Returns 0 if the element is
	 *         not linked.
	 */
	public long isLinked() {
		Set<Link> links = getLinks();
		if (links.isEmpty()) {
			return 0;
		}
		return links.iterator().next().getId();
	}

	/**
	 * @see KnowledgeStatus
	 * @return status of the knowledge element. For example, the status for issues
	 *         can be solved or unsolved.
	 */
	public KnowledgeStatus getStatus() {
		if (status == null || status == KnowledgeStatus.UNDEFINED) {
			return KnowledgeStatus.getDefaultStatus(getType());
		}
		return status;
	}

	/**
	 * @see KnowledgeStatus
	 * @param status
	 *            of the knowledge element. For example, the status for issues can
	 *            be solved or unsolved.
	 */
	public void setStatus(KnowledgeStatus status) {
		this.status = status;
	}

	/**
	 * @see KnowledgeStatus
	 * @param status
	 *            of the knowledge element. For example, the status for issues can
	 *            be solved or unsolved.
	 */
	@JsonProperty("status")
	public void setStatus(String status) {
		this.status = KnowledgeStatus.getKnowledgeStatus(status);
	}

	/**
	 * @see KnowledgeStatus
	 * @return status of the knowledge element. For example, the status for issues
	 *         can be solved or unsolved.
	 */
	@XmlElement(name = "status")
	public String getStatusAsString() {
		return getStatus().toString();
	}

	/**
	 * @return true if the element is correctly linked according to the
	 *         {@link DefinitionOfDone}. For example, an argument needs to be linked
	 *         to at least one solution option (decision or alternative) in the
	 *         {@link KnowledgeGraph}. Otherwise, it is incomplete, i.e., its
	 *         documentation needs to be improved.
	 */
	public boolean failsDefinitionOfDone(FilterSettings filterSettings) {
		return !DefinitionOfDoneChecker.checkDefinitionOfDone(this, filterSettings);
	}

	@Override
	public boolean equals(Object object) {
		if (object == null) {
			return false;
		}
		if (object == this) {
			return true;
		}
		if (!(object instanceof KnowledgeElement)) {
			return false;
		}
		KnowledgeElement element = (KnowledgeElement) object;
		return id == element.getId() && getDocumentationLocation() == element.getDocumentationLocation();
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, getDocumentationLocation());
	}

	/**
	 * @return linked solution options (alternatives, decisions, solutions, claims).
	 *         Assumes that this knowledge element is a decision problem
	 *         (=issue/question).
	 */
	public List<SolutionOption> getLinkedSolutionOptions() {
		List<SolutionOption> solutionOptions = getLinks().stream()
				.filter(link -> link.getOppositeElement(this).getType().getSuperType() == KnowledgeType.SOLUTION)
				.map(link -> new SolutionOption(link.getOppositeElement(this))).collect(Collectors.toList());
		return solutionOptions;
	}

	/**
	 * @return linked decision problems (issues, goals, questions) to this knowledge
	 *         element.
	 */
	public List<KnowledgeElement> getLinkedDecisionProblems() {
		List<KnowledgeElement> decisionProblems = getLinks().stream()
				.filter(link -> link.getOppositeElement(this).getType().getSuperType() == KnowledgeType.PROBLEM)
				.map(link -> link.getOppositeElement(this)).collect(Collectors.toList());
		return decisionProblems;
	}

	/**
	 * @param knowledgeTypes
	 *            one ore more {@link KnowledgeType}s.
	 * @return true if the element has one of the given {@link KnowledgeType}s.
	 */
	public boolean hasKnowledgeType(KnowledgeType... knowledgeTypes) {
		for (KnowledgeType knowledgeType : knowledgeTypes) {
			if (this.getType() == knowledgeType)
				return true;
		}
		return false;
	}
}
