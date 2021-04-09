package de.uhd.ifi.se.decision.management.jira.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlElement;

import org.codehaus.jackson.annotate.JsonProperty;
import org.jgrapht.Graphs;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.link.IssueLink;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.persistence.DecisionGroupManager;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.quality.completeness.CompletenessHandler;
import de.uhd.ifi.se.decision.management.jira.quality.completeness.DefinitionOfDone;

/**
 * Models knowledge elements, e.g., decision knowledge elements, requirements,
 * work items, or code classes. These elements are nodes of the knowledge graph
 * and connected by links/edges/relationships.
 *
 * @see KnowledgeGraph
 * @see Link
 */
public class KnowledgeElement {

	protected long id;
	protected DecisionKnowledgeProject project;
	private String summary;
	private String description;
	protected KnowledgeType type;
	private String key;
	private Date creationDate;
	private Date updatingDate;
	protected DocumentationLocation documentationLocation;
	protected Origin origin;

	/**
	 * @issue Where shall we store the line count of a code file knowledge element?
	 * @alternative In the ChangedFile class!
	 * @pro Only files have a line count, not other knowledge elements.
	 * @con The line count needs to be handled by the CodeClassPersistenceManager,
	 *      which uses KnowledgeElement instead of ChangedFile in many cases.
	 * @con The CodeCompletenessCheck class (using the lineCount) implements the
	 *      CompletenessCheck interface, which works with KnowledgeElements, not
	 *      ChangedFiles.
	 * @con Converting a KnowledgeElement into a ChangedFile performs very badly.
	 * @decision In the KnowledgeElement class!
	 * @con Not all knowledge elements have a line count.
	 * @pro Many functions using the lineCount already work with KnowledgeElements,
	 *      not ChangedFiles.
	 */
	private int lineCount;

	protected KnowledgeStatus status;

	public KnowledgeElement() {
		this.description = "";
		this.summary = "";
		this.type = KnowledgeType.OTHER;
		// the origin is the same as the documentation location per default
		this.origin = Origin.DOCUMENTATION_LOCATION;
	}

	public KnowledgeElement(long id, String summary, String description, KnowledgeType type, String projectKey,
			String key, DocumentationLocation documentationLocation, KnowledgeStatus status) {
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
			String documentationLocation, String status) {
		this(id, summary, description, KnowledgeType.getKnowledgeType(type), projectKey, key,
				DocumentationLocation.getDocumentationLocationFromIdentifier(documentationLocation),
				KnowledgeStatus.getKnowledgeStatus(status));
	}

	public KnowledgeElement(long id, String summary, String description, String type, String projectKey, String key,
			DocumentationLocation documentationLocation, String status) {
		this(id, summary, description, KnowledgeType.getKnowledgeType(type), projectKey, key, documentationLocation,
				KnowledgeStatus.getKnowledgeStatus(status));
	}

	public KnowledgeElement(Issue issue) {
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
		this.creationDate = issue.getCreated();
		this.updatingDate = issue.getUpdated();
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
		if (type != null) {
			return type;
		}
		return KnowledgeType.OTHER;
	}

	/**
	 * @see KnowledgeType
	 * @return type of the knowledge element as a String. For example, types are
	 *         decision, alternative, issue, and argument. This methods returns the
	 *         type of Jira issues that are no knowledge elements.
	 */
	@XmlElement(name = "type")
	public String getTypeAsString() {
		if (this.getType() == KnowledgeType.OTHER
				&& this.getDocumentationLocation() == DocumentationLocation.JIRAISSUE) {
			IssueManager issueManager = ComponentAccessor.getIssueManager();
			Issue issue = issueManager.getIssueByCurrentKey(this.getKey());
			return issue != null ? issue.getIssueType().getName() : "";
		}
		return this.getType().toString();
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
		this.setType(type);
	}

	/**
	 * TODO Address issue
	 *
	 * @issue Currently, groups are a derived attribute of this class. How efficient
	 *        is it to query the database via the DecisionGroupsManager? Would it be
	 *        more efficient to have a "real" groups attribute in this class?
	 *
	 * @return List<String> of groups assigned to this knowledge element.
	 */
	@XmlElement(name = "groups")
	public List<String> getDecisionGroups() {
		return DecisionGroupManager.getGroupsForElement(this);
	}

	/**
	 * Add a list of groups assigned to this decision
	 *
	 * @param decisionGroup
	 *            of groups
	 */
	public void addDecisionGroups(List<String> decisionGroup) {
		for (String group : decisionGroup) {
			this.addDecisionGroup(group);
		}
	}

	/**
	 * Add a group to the list of groups
	 *
	 * @param group
	 *            to add as string
	 */
	public void addDecisionGroup(String group) {
		DecisionGroupManager.insertGroup(group, this);
	}

	/**
	 * Remove a group from the list of groups
	 *
	 * @param group
	 *            to remove as string
	 */
	public void removeDecisionGroup(String group) {
		DecisionGroupManager.deleteGroupAssignment(group, this);
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
		this.project = new DecisionKnowledgeProject(projectKey);
	}

	/**
	 * @return key of the knowledge element. The key is composed of
	 *         projectKey-project internal id.
	 */
	@XmlElement
	public String getKey() {
		if (this.key == null && this.project != null) {
			return this.project.getProjectKey() + "-" + this.id;
		}
		return this.key;
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
		return this.documentationLocation;
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
			return this.documentationLocation.getIdentifier();
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
		String key = this.getKey();
		// TODO Recognize code classes
		// TODO Simplify recognition of decision knowledge documented in Jira issue
		// comments/description
		if (this.getDocumentationLocation() == DocumentationLocation.JIRAISSUETEXT) {
			key = key.split(":")[0];
		}
		ApplicationProperties applicationProperties = ComponentAccessor.getApplicationProperties();
		return applicationProperties.getString(APKeys.JIRA_BASEURL) + "/browse/" + key;
	}

	/**
	 * @return creation date of the knowledge element.
	 */
	@XmlElement
	public Date getCreationDate() {
		if (creationDate == null) {
			return new Date();
		}
		return creationDate;
	}

	/**
	 * @param creationDate
	 *            of creation of the knowledge element.
	 */
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	/**
	 * @return date of last update of the knowledge element.
	 */
	@XmlElement
	public Date getUpdatingDate() {
		if (updatingDate == null) {
			return getCreationDate();
		}
		return updatingDate;
	}

	/**
	 * @param updatingDate
	 *            date of last update of the knowledge element.
	 */
	public void setUpdatingDate(Date updatingDate) {
		this.updatingDate = updatingDate;
	}

	/**
	 * @return true if the element exists in database.
	 */
	public boolean existsInDatabase() {
		KnowledgeElement elementInDatabase = KnowledgePersistenceManager.getOrCreate("").getKnowledgeElement(id,
				documentationLocation);
		return elementInDatabase != null && elementInDatabase.getId() > 0;
	}

	/**
	 * @issue Should code classes be assigned to all Jira issues that they were
	 *        committed to?
	 * 
	 * @return Jira issue that the knowledge element or irrelevant text is part of.
	 */
	// TODO Improve this method. Code classes are not handled currently.
	public Issue getJiraIssue() {
		if (documentationLocation == DocumentationLocation.JIRAISSUE) {
			return ComponentAccessor.getIssueManager().getIssueObject(id);
		}
		if (documentationLocation == DocumentationLocation.JIRAISSUETEXT) {
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
				.getOrCreate(project.getProjectKey());
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
		ApplicationUser user = getCreator();
		return user != null ? user.getDisplayName() : "";
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
		KnowledgeGraph graph = KnowledgeGraph.getOrCreate(project);
		Set<KnowledgeElement> neighbors = Graphs.neighborSetOf(graph, this);
		return neighbors.stream().filter(element -> element.getType() == knowledgeType).collect(Collectors.toSet());
	}

	/**
	 * @return all links (=edges) of this element in the {@link KnowledgeGraph} as a
	 *         set of {@link Link} objects, does contain Jira {@link IssueLink}s and
	 *         generic links (e.g. links between code classes and Jira issues).
	 */
	public Set<Link> getLinks() {
		if (project == null) {
			return new HashSet<>();
		}
		return KnowledgeGraph.getOrCreate(project).edgesOf(this);
	}

	public Set<KnowledgeElement> getLinkedElements(int currentDistance) {
		Set<KnowledgeElement> elements = new HashSet<>();
		Set<Link> traversedLinks = new HashSet<>();
		elements.add(this);

		if (currentDistance == 0) {
			return elements;
		}
		for (Link link : this.getLinks()) {
			if (!traversedLinks.add(link)) {
				continue;
			}
			KnowledgeElement oppositeElement = link.getOppositeElement(this);
			if (oppositeElement == null) {
				continue;
			}
			elements.addAll(oppositeElement.getLinkedElements(currentDistance - 1));
		}
		return elements;
	}

	public int getLinkDistance(KnowledgeElement other, int maxLinkDistance) {
		Set<KnowledgeElement> linkedElements = new HashSet<KnowledgeElement>();
		for (int distance = 0; distance <= maxLinkDistance; distance++) {
			Set<KnowledgeElement> newLinkedElements = getLinkedElements(distance);
			newLinkedElements.removeAll(linkedElements);
			for (KnowledgeElement element : newLinkedElements) {
				if (other.equals(element)) {
					return distance;
				}
			}
			linkedElements.addAll(newLinkedElements);
		}
		return -1;
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

	public int getLineCount() {
		return this.lineCount;
	}

	public void setLineCount(int lineCount) {
		this.lineCount = lineCount;
	}

	/**
	 * @return true if the element is correctly linked according to the
	 *         {@link DefinitionOfDone}. For example, an argument needs to be linked
	 *         to at least one solution option (decision or alternative) in the
	 *         {@link KnowledgeGraph}. Otherwise, it is incomplete, i.e., its
	 *         documentation needs to be improved.
	 */
	public boolean isIncomplete() {
		return !CompletenessHandler.checkForCompleteness(this);
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
		return this.id == element.getId() && this.getDocumentationLocation() == element.getDocumentationLocation();
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
	public List<KnowledgeElement> getLinkedSolutionOptions() {
		List<KnowledgeElement> solutionOptions = getLinks().stream()
				.filter(link -> link.getOppositeElement(this).getType().getSuperType() == KnowledgeType.SOLUTION)
				.map(link -> link.getOppositeElement(this)).collect(Collectors.toList());
		return solutionOptions;
	}

	/**
	 * @return linked arguments (pros, cons). Assumes that this knowledge element is
	 *         a solution option (=alternative/decision/solution/claim).
	 */
	public List<Argument> getArguments() {
		List<Argument> arguments = new ArrayList<>();
		for (KnowledgeElement element : getLinkedElements(1)) {
			if (element.getType() == KnowledgeType.PRO || element.getType() == KnowledgeType.CON
					|| element.getType() == KnowledgeType.ARGUMENT) {
				arguments.add(new Argument(element));
			}
		}
		return arguments;
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
