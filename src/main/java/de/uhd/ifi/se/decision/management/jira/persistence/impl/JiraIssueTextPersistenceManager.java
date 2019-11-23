package de.uhd.ifi.se.decision.management.jira.persistence.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.exception.CreateException;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.jira.issue.comments.MutableComment;
import com.atlassian.jira.issue.link.IssueLinkManager;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.classification.implementation.OnlineClassificationTrainerImpl;
import de.uhd.ifi.se.decision.management.jira.eventlistener.JiraIssueTextExtractionEventListener;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.LinkType;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfText;
import de.uhd.ifi.se.decision.management.jira.model.text.impl.PartOfJiraIssueTextImpl;
import de.uhd.ifi.se.decision.management.jira.model.text.impl.TextSplitterImpl;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.PartOfJiraIssueTextInDatabase;
import de.uhd.ifi.se.decision.management.jira.view.macros.AbstractKnowledgeClassificationMacro;
import net.java.ao.Query;

/**
 * Extends the abstract class
 * {@link AbstractPersistenceManagerForSingleLocation}. Uses Jira issue comments
 * or the description to store decision knowledge.
 *
 * @see AbstractPersistenceManagerForSingleLocation
 */
public class JiraIssueTextPersistenceManager extends AbstractPersistenceManagerForSingleLocation {
	private static final Logger LOGGER = LoggerFactory.getLogger(JiraIssueTextPersistenceManager.class);
	private static final ActiveObjects ACTIVE_OBJECTS = ComponentGetter.getActiveObjects();
	private static OnlineClassificationTrainerImpl classificationTrainer = new OnlineClassificationTrainerImpl();

	public JiraIssueTextPersistenceManager(String projectKey) {
		this.projectKey = projectKey;
		this.documentationLocation = DocumentationLocation.JIRAISSUETEXT;
	}

	@Override
	public boolean deleteDecisionKnowledgeElement(long id, ApplicationUser user) {
		if (id <= 0 || user == null) {
			LOGGER.error(
					"Element cannot be deleted since it does not exist (id is less than zero) or the user is null.");
			return false;
		}
		boolean isDeleted = false;
		for (PartOfJiraIssueTextInDatabase databaseEntry : ACTIVE_OBJECTS.find(PartOfJiraIssueTextInDatabase.class,
				Query.select().where("ID = ?", id))) {
			StatusPersistenceManager.deleteStatus(new PartOfJiraIssueTextImpl(databaseEntry));
			GenericLinkManager.deleteLinksForElement(id, DocumentationLocation.JIRAISSUETEXT);
			KnowledgeGraph.getOrCreate(projectKey).removeVertex(new PartOfJiraIssueTextImpl(databaseEntry));
			isDeleted = PartOfJiraIssueTextInDatabase.deleteElement(databaseEntry);
		}
		return isDeleted;
	}

	/**
	 * Deletes all decision knowledge elements and their links documented in a
	 * certain comment of a Jira issue in database. Does not delete or change the
	 * comment itself.
	 *
	 * @param comment
	 *            of the Jira issue that the decision knowledge elements are
	 *            documented in.
	 * @return true if deletion was successfull.
	 */
	public boolean deleteElementsInComment(Comment comment) {
		if (comment == null) {
			LOGGER.error("Decision knowledge elements in comment cannot be deleted since the comment is null.");
			return false;
		}
		return deletePartsOfText(comment.getIssue().getId(), comment.getId());
	}

	/**
	 * Deletes all decision knowledge elements and their links documented in the
	 * description of a Jira issue. Does not delete the text or change the
	 * description itself.
	 *
	 * @param jiraIssue
	 *            that the decision knowledge elements are documented in.
	 * @return true if deletion was successfull.
	 */
	public boolean deleteElementsInDescription(Issue jiraIssue) {
		if (jiraIssue == null) {
			LOGGER.error("Decision knowledge elements in description cannot be deleted since the Jira issue is null.");
			return false;
		}
		return deletePartsOfText(jiraIssue.getId(), 0);
	}

	private boolean deletePartsOfText(long jiraIssueId, long commentId) {
		boolean isDeleted = false;
		PartOfJiraIssueTextInDatabase[] databaseEntries = ACTIVE_OBJECTS.find(PartOfJiraIssueTextInDatabase.class,
				Query.select().where("PROJECT_KEY = ? AND JIRA_ISSUE_ID = ? AND COMMENT_ID = ?", projectKey,
						jiraIssueId, commentId));
		for (PartOfJiraIssueTextInDatabase databaseEntry : databaseEntries) {
			GenericLinkManager.deleteLinksForElement(databaseEntry.getId(), DocumentationLocation.JIRAISSUETEXT);
			isDeleted = PartOfJiraIssueTextInDatabase.deleteElement(databaseEntry);
		}
		return isDeleted;
	}

	@Override
	public DecisionKnowledgeElement getDecisionKnowledgeElement(long id) {
		PartOfJiraIssueText sentence = null;
		for (PartOfJiraIssueTextInDatabase databaseEntry : ACTIVE_OBJECTS.find(PartOfJiraIssueTextInDatabase.class,
				Query.select().where("ID = ?", id))) {
			sentence = new PartOfJiraIssueTextImpl(databaseEntry);
		}
		return sentence;
	}

	@Override
	public DecisionKnowledgeElement getDecisionKnowledgeElement(String key) {
		if (key == null) {
			return null;
		}
		long id = Long.parseLong(key.split(":")[1]);
		return getDecisionKnowledgeElement(id);
	}

	@Override
	public List<DecisionKnowledgeElement> getDecisionKnowledgeElements() {
		List<DecisionKnowledgeElement> decisionKnowledgeElements = new ArrayList<DecisionKnowledgeElement>();
		for (PartOfJiraIssueTextInDatabase databaseEntry : ACTIVE_OBJECTS.find(PartOfJiraIssueTextInDatabase.class,
				Query.select().where("PROJECT_KEY = ?", projectKey))) {
			decisionKnowledgeElements.add(new PartOfJiraIssueTextImpl(databaseEntry));
		}
		return decisionKnowledgeElements;
	}

	/**
	 * Returns all decision knowledge elements documented in the description or
	 * comments of a Jira issue.
	 *
	 * @param jiraIssueId
	 *            id of the Jira issue that the decision knowledge elements are
	 *            documented in.
	 * @return list of all decision knowledge elements documented in the description
	 *         or comments of a Jira issue.
	 */
	public List<DecisionKnowledgeElement> getElementsInJiraIssue(long jiraIssueId) {
		List<DecisionKnowledgeElement> elements = new ArrayList<DecisionKnowledgeElement>();
		for (PartOfJiraIssueTextInDatabase databaseEntry : ACTIVE_OBJECTS.find(PartOfJiraIssueTextInDatabase.class,
				Query.select().where("PROJECT_KEY = ? AND JIRA_ISSUE_ID = ?", projectKey, jiraIssueId))) {
			elements.add(new PartOfJiraIssueTextImpl(databaseEntry));
		}
		return elements;
	}

	/**
	 * Returns all decision knowledge elements documented in a certain comment of a
	 * Jira issue.
	 *
	 * @param commentId
	 *            id of the comment that the decision knowledge element(s) is/are
	 *            documented in.
	 * @return list of all decision knowledge elements documented in the a certain
	 *         comment of a Jira issue.
	 */
	public List<DecisionKnowledgeElement> getElementsInComment(long commentId) {
		List<DecisionKnowledgeElement> elements = new ArrayList<DecisionKnowledgeElement>();
		for (PartOfJiraIssueTextInDatabase databaseEntry : ACTIVE_OBJECTS.find(PartOfJiraIssueTextInDatabase.class,
				Query.select().where("PROJECT_KEY = ? AND COMMENT_ID = ?", projectKey, commentId))) {
			elements.add(new PartOfJiraIssueTextImpl(databaseEntry));
		}
		return elements;
	}

	/**
	 * Returns all decision knowledge elements documented in the description of a
	 * Jira issue. The comment id of such elements is zero.
	 *
	 * @param jiraIssueId
	 *            id of the Jira issue that the decision knowledge elements are
	 *            documented in.
	 * @return list of all decision knowledge elements documented in the description
	 *         of a Jira issue.
	 */
	public List<DecisionKnowledgeElement> getElementsInDescription(long jiraIssueId) {
		List<DecisionKnowledgeElement> elements = getElementsInJiraIssue(jiraIssueId);
		elements.removeIf(e -> (((PartOfJiraIssueText) e).getCommentId() != 0));
		return elements;
	}

	/**
	 * Returns the id of the decision knowledge element documented in the
	 * description or comments of a Jira issue with the summary and type.
	 * 
	 * @param summary
	 *            of the decision knowledge element.
	 * @param jiraIssueId
	 *            id of the Jira issue that the decision knowledge element is
	 *            documented in.
	 * @param type
	 *            {@link KnowledgeType} of the element.
	 * @return id of the decision knowledge element documented in the description or
	 *         comments of a Jira issue with the summary and type.
	 */
	public long getIdOfElement(String summary, long jiraIssueId, KnowledgeType type) {
		if (summary == null || jiraIssueId <= 0 || type == null || summary.isBlank()) {
			return 0;
		}
		List<DecisionKnowledgeElement> sentences = getElementsWithTypeInJiraIssue(jiraIssueId, type);
		for (DecisionKnowledgeElement sentence : sentences) {
			if (sentence.getSummary().trim().equalsIgnoreCase(summary)) {
				return sentence.getId();
			}
		}
		LOGGER.debug("Nothing found for: " + summary);
		return 0;
	}

	/**
	 * Returns all decision knowledge elements with a certain type documented in the
	 * description or comments of a Jira issue.
	 *
	 * @param jiraIssueId
	 *            id of the Jira issue that the decision knowledge elements are
	 *            documented in.
	 * @param type
	 *            {@link KnowledgeType} of the element.
	 * @return list of all decision knowledge elements with a certain type
	 *         documented in the description or comments of a Jira issue.
	 */
	public List<DecisionKnowledgeElement> getElementsWithTypeInJiraIssue(long jiraIssueId, KnowledgeType type) {
		List<DecisionKnowledgeElement> elements = getElementsInJiraIssue(jiraIssueId);
		elements.removeIf(e -> (e.getType() != type));
		return elements;
	}

	@Override
	public List<Link> getInwardLinks(DecisionKnowledgeElement element) {
		return GenericLinkManager.getInwardLinks(element);
	}

	@Override
	public List<Link> getOutwardLinks(DecisionKnowledgeElement element) {
		return GenericLinkManager.getOutwardLinks(element);
	}

	@Override
	public DecisionKnowledgeElement insertDecisionKnowledgeElement(DecisionKnowledgeElement element,
			ApplicationUser user, DecisionKnowledgeElement parentElement) {
		if (element == null || user == null) {
			return null;
		}
		if (parentElement == null) {
			return insertDecisionKnowledgeElement(element, user);
		}
		Issue jiraIssue = parentElement.getJiraIssue();
		if (jiraIssue == null) {
			return null;
		}
		Comment comment = createCommentInJiraIssue(element, jiraIssue, user);
		return insertDecisionKnowledgeElement(new PartOfJiraIssueTextImpl(comment), user);
	}

	/**
	 * Returns the Jira issue that decision knowledge element is documented in
	 * (either in a comment or the description of this Jira issue).
	 * 
	 * @param id
	 *            of the decision knowledge element.
	 * @return Jira issue as an {@link Issue} object.
	 */
	public Issue getJiraIssue(long id) {
		PartOfJiraIssueText sentence = (PartOfJiraIssueTextImpl) this.getDecisionKnowledgeElement(id);
		if (sentence == null) {
			return null;
		}
		return sentence.getJiraIssue();
	}

	private Comment createCommentInJiraIssue(DecisionKnowledgeElement element, Issue jiraIssue, ApplicationUser user) {
		String tag = AbstractKnowledgeClassificationMacro.getTag(element.getTypeAsString());
		String text = tag + element.getSummary() + "\n" + element.getDescription() + tag;
		return ComponentAccessor.getCommentManager().create(jiraIssue, user, text, false);
	}

	@Override
	public DecisionKnowledgeElement insertDecisionKnowledgeElement(DecisionKnowledgeElement element,
			ApplicationUser user) {
		DecisionKnowledgeElement existingElement = checkIfElementExistsInDatabase(element);
		if (existingElement != null) {
			return existingElement;
		}

		PartOfJiraIssueTextInDatabase databaseEntry = ACTIVE_OBJECTS.create(PartOfJiraIssueTextInDatabase.class);

		setParameters((PartOfJiraIssueText) element, databaseEntry);
		databaseEntry.save();

		PartOfJiraIssueText sentence = new PartOfJiraIssueTextImpl(databaseEntry);
		if (sentence.getId() > 0 && sentence.isRelevant()) {
			KnowledgePersistenceManager.insertStatus(sentence);
			KnowledgeGraph.getOrCreate(projectKey).addVertex(sentence);
		}
		return sentence;
	}

	private DecisionKnowledgeElement checkIfElementExistsInDatabase(DecisionKnowledgeElement element) {
		if (element.getDocumentationLocation() != DocumentationLocation.JIRAISSUETEXT) {
			return null;
		}
		DecisionKnowledgeElement existingElement = getDecisionKnowledgeElement(element);
		if (existingElement != null) {
			ensureThatElementIsLinked(existingElement);
			return existingElement;
		}
		return null;
	}

	public DecisionKnowledgeElement getDecisionKnowledgeElement(DecisionKnowledgeElement element) {
		if (element == null) {
			return null;
		}
		if (element.getId() > 0) {
			return this.getDecisionKnowledgeElement(element.getId());
		}

		PartOfJiraIssueText sentence = (PartOfJiraIssueText) element;
		PartOfJiraIssueText sentenceInDatabase = null;
		for (PartOfJiraIssueTextInDatabase databaseEntry : ACTIVE_OBJECTS.find(PartOfJiraIssueTextInDatabase.class,
				Query.select().where("PROJECT_KEY = ? AND COMMENT_ID = ? AND END_POSITION = ? AND START_POSITION = ?",
						sentence.getProject().getProjectKey(), sentence.getCommentId(), sentence.getEndPosition(),
						sentence.getStartPosition()))) {
			sentenceInDatabase = new PartOfJiraIssueTextImpl(databaseEntry);
		}
		return sentenceInDatabase;
	}

	private static void setParameters(PartOfJiraIssueText element, PartOfJiraIssueTextInDatabase databaseEntry) {
		databaseEntry.setProjectKey(element.getProject().getProjectKey());
		databaseEntry.setCommentId(element.getCommentId());
		databaseEntry.setType(element.getTypeAsString());
		databaseEntry.setRelevant(element.isRelevant());
		databaseEntry.setValidated(element.isValidated());
		// TODO Is there a better place for this method?
		if (element.isValidated()) {
			classificationTrainer.update(element);
		}
		databaseEntry.setStartPosition(element.getStartPosition());
		databaseEntry.setEndPosition(element.getEndPosition());
		databaseEntry.setJiraIssueId(element.getJiraIssueId());
	}

	@Override
	public boolean updateDecisionKnowledgeElement(DecisionKnowledgeElement element, ApplicationUser user) {
		if (element == null) {
			return false;
		}
		PartOfJiraIssueText sentence = new PartOfJiraIssueTextImpl(element);
		sentence.setValidated(true);
		return updatePartOfJiraIssueText(sentence, user);
	}

	@Override
	public boolean updateDecisionKnowledgeElementWithoutStatusChange(DecisionKnowledgeElement element,
			ApplicationUser user) {
		if (element == null) {
			return false;
		}
		PartOfJiraIssueText partOfJiraIssueText = new PartOfJiraIssueTextImpl(element);
		partOfJiraIssueText.setValidated(true);
		PartOfJiraIssueText partOfJiraIssueTextInDatabase = (PartOfJiraIssueText) getDecisionKnowledgeElement(
				element.getId());
		if (partOfJiraIssueTextInDatabase == null) {
			return false;
		}
		return updateElementInDatabase(partOfJiraIssueText, partOfJiraIssueTextInDatabase, user);
	}

	@Override
	public ApplicationUser getCreator(DecisionKnowledgeElement element) {
		PartOfJiraIssueText sentence = (PartOfJiraIssueText) getDecisionKnowledgeElement(element.getId());
		if (sentence == null) {
			LOGGER.error("Element could not be found.");
			return null;
		}
		return sentence.getCreator();
	}

	public static boolean updatePartOfJiraIssueText(PartOfJiraIssueText element, ApplicationUser user) {
		if (element == null || element.getProject() == null) {
			return false;
		}

		JiraIssueTextPersistenceManager persistenceManager = KnowledgePersistenceManager
				.getOrCreate(element.getProject().getProjectKey()).getJiraIssueTextManager();

		// Get corresponding element from database
		PartOfJiraIssueText sentence = (PartOfJiraIssueText) persistenceManager
				.getDecisionKnowledgeElement(element.getId());
		if (sentence == null) {
			return false;
		}
		// only the knowledge type has changed
		if (element.getSummary() == null) {
			element.setSummary(sentence.getSummary());
			element.setDescription(sentence.getDescription());
		}
		if (sentence.getType().equals(KnowledgeType.DECISION) && element.getType().equals(KnowledgeType.ALTERNATIVE)) {
			StatusPersistenceManager.setStatusForElement(sentence, KnowledgeStatus.REJECTED);
		}
		if (sentence.getType().equals(KnowledgeType.ALTERNATIVE) && element.getType().equals(KnowledgeType.DECISION)) {
			StatusPersistenceManager.deleteStatus(element);
		}
		return updateElementInDatabase(element, sentence, user);
	}

	private static boolean updateElementInDatabase(PartOfJiraIssueText element, PartOfJiraIssueText sentence,
			ApplicationUser user) {
		String tag = AbstractKnowledgeClassificationMacro.getTag(element.getType());
		String changedPartOfText = tag + element.getDescription() + tag;

		String text = "";
		MutableComment mutableComment = sentence.getComment();
		if (mutableComment == null) {
			text = sentence.getJiraIssueDescription();
		} else {
			text = mutableComment.getBody();
		}

		String firstPartOfText = text.substring(0, sentence.getStartPosition());
		String lastPartOfText = text.substring(sentence.getEndPosition());

		String newBody = firstPartOfText + changedPartOfText + lastPartOfText;

		JiraIssueTextExtractionEventListener.editCommentLock = true;
		if (mutableComment == null) {
			MutableIssue jiraIssue = (MutableIssue) sentence.getJiraIssue();
			jiraIssue.setDescription(newBody);
			JiraIssuePersistenceManager.updateJiraIssue(jiraIssue, user);
		} else {
			mutableComment.setBody(newBody);
			ComponentAccessor.getCommentManager().update(mutableComment, true);
		}
		JiraIssueTextExtractionEventListener.editCommentLock = false;

		int lengthDifference = changedPartOfText.length() - sentence.getLength();
		updateSentenceLengthForOtherSentencesInSameComment(sentence, lengthDifference);

		sentence.setEndPosition(sentence.getStartPosition() + changedPartOfText.length());
		sentence.setType(element.getType());
		sentence.setValidated(element.isValidated());
		sentence.setRelevant(element.getType() != KnowledgeType.OTHER);
		// sentence.setCommentId(element.getCommentId());

		return updateInDatabase(sentence);
	}

	public static boolean updateInDatabase(PartOfJiraIssueText sentence) {
		boolean isUpdated = false;
		for (PartOfJiraIssueTextInDatabase databaseEntry : ACTIVE_OBJECTS.find(PartOfJiraIssueTextInDatabase.class)) {
			if (databaseEntry.getId() == sentence.getId()) {
				setParameters(sentence, databaseEntry);
				databaseEntry.save();
				isUpdated = true;
			}
		}
		return isUpdated;
	}

	public static void updateSentenceLengthForOtherSentencesInSameComment(PartOfJiraIssueText sentence,
			int lengthDifference) {
		for (PartOfJiraIssueTextInDatabase otherSentenceInComment : ACTIVE_OBJECTS.find(
				PartOfJiraIssueTextInDatabase.class, "COMMENT_ID = ? AND JIRA_ISSUE_ID = ?", sentence.getCommentId(),
				sentence.getJiraIssueId())) {
			if (otherSentenceInComment.getStartPosition() > sentence.getStartPosition()
					&& otherSentenceInComment.getId() != sentence.getId()) {
				otherSentenceInComment.setStartPosition(otherSentenceInComment.getStartPosition() + lengthDifference);
				otherSentenceInComment.setEndPosition(otherSentenceInComment.getEndPosition() + lengthDifference);
				otherSentenceInComment.save();
			}
		}
	}

	public static boolean createSmartLinkForElement(PartOfJiraIssueText sentence) {
		if (sentence == null) {
			return false;
		}
		if (isElementLinked(sentence)) {
			return true;
		}
		boolean isLinkCreated = false;
		DecisionKnowledgeElement lastElement = AutomaticLinkCreator.getPotentialParentElement(sentence);
		isLinkCreated = createLink(lastElement, sentence);
		return isLinkCreated;
	}

	public boolean createLinksForNonLinkedElements(long jiraIssueId) {
		return createLinksForNonLinkedElements(getElementsInJiraIssue(jiraIssueId));
	}

	public boolean createLinksForNonLinkedElements() {
		return createLinksForNonLinkedElements(getDecisionKnowledgeElements());
	}

	public boolean createLinksForNonLinkedElements(List<DecisionKnowledgeElement> elements) {
		boolean areElementsLinked = true;
		for (DecisionKnowledgeElement element : elements) {
			areElementsLinked = areElementsLinked && ensureThatElementIsLinked(element);
		}
		return areElementsLinked;
	}

	public boolean ensureThatElementIsLinked(DecisionKnowledgeElement element) {
		if (isElementLinked(element)) {
			return true;
		}
		DecisionKnowledgeElement parentElement = new DecisionKnowledgeElementImpl(
				((PartOfJiraIssueText) element).getJiraIssue());
		Link link = Link.instantiateDirectedLink(parentElement, element);
		long linkId = KnowledgePersistenceManager.getOrCreate(projectKey).insertLink(link, null);
		return linkId > 0;
	}

	public static boolean createLink(DecisionKnowledgeElement lastElement, PartOfJiraIssueText sentence) {
		if (lastElement == null || !sentence.isRelevant()) {
			return false;
		}
		Link link = Link.instantiateDirectedLink(lastElement, sentence);
		long linkId = KnowledgePersistenceManager.getOrCreate(lastElement.getProject().getProjectKey()).insertLink(link,
				null);
		return linkId > 0;
	}

	/**
	 * Determines whether an element is linked to at least one other decision
	 * knowledge element.
	 *
	 * @param id
	 *            id of a decision knowledge element in database. The id is
	 *            different to the key.
	 * @param documentationLocation
	 *            of the element
	 * @return list of linked elements.
	 * @see DecisionKnowledgeElement
	 */
	public static boolean isElementLinked(DecisionKnowledgeElement element) {
		List<Link> links = GenericLinkManager.getLinksForElement(element);
		return links != null && links.size() > 0;
	}

	public static void cleanSentenceDatabase(String projectKey) {
		for (PartOfJiraIssueTextInDatabase databaseEntry : ACTIVE_OBJECTS.find(PartOfJiraIssueTextInDatabase.class,
				Query.select().where("PROJECT_KEY = ?", projectKey))) {
			if (!isExistent(databaseEntry)) {
				PartOfJiraIssueTextInDatabase.deleteElement(databaseEntry);
				GenericLinkManager.deleteLinksForElement(databaseEntry.getId(), DocumentationLocation.JIRAISSUETEXT);
				DecisionKnowledgeElement element = new PartOfJiraIssueTextImpl(databaseEntry);
				KnowledgeGraph.getOrCreate(element.getProject().getProjectKey()).removeVertex(element);
			}
		}
	}

	public static boolean isExistent(PartOfJiraIssueTextInDatabase databaseEntry) {
		PartOfJiraIssueText sentence = new PartOfJiraIssueTextImpl(databaseEntry);
		return isExistent(sentence);
	}

	public static boolean isExistent(PartOfJiraIssueText sentence) {
		if (sentence == null) {
			return false;
		}
		if (sentence.getCommentId() <= 0) {
			// documented in Jira issue description
			return true;
		}
		if (sentence.getComment() == null) {
			return false;
		}
		return !(sentence.getEndPosition() == 0 && sentence.getStartPosition() == 0);
	}

	/**
	 * Migration function on button "Validate Sentence Database" Adds Link types to
	 * "empty" links. Can be deleted in a future release
	 */
	public static boolean migrateArgumentTypesInLinks(String projectKey) {
		if (projectKey == null || projectKey.equals("")) {
			return false;
		}
		PartOfJiraIssueTextInDatabase[] sentencesInProject = ACTIVE_OBJECTS.find(PartOfJiraIssueTextInDatabase.class,
				Query.select().where("PROJECT_KEY = ?", projectKey));
		for (PartOfJiraIssueTextInDatabase databaseEntry : sentencesInProject) {
			if (databaseEntry.getType().length() == 3) {// Equals Argument
				List<Link> links = GenericLinkManager.getLinksForElement(databaseEntry.getId(),
						DocumentationLocation.JIRAISSUETEXT);
				for (Link link : links) {
					if (link.getType().equalsIgnoreCase("contain")) {
						KnowledgePersistenceManager.getOrCreate(projectKey).deleteLink(link, null);
						link.setType(LinkType.getLinkTypeForKnowledgeType(databaseEntry.getType()).toString());
						KnowledgePersistenceManager.getOrCreate(projectKey).insertLink(link, null);
					}
				}
			}
		}
		return true;
	}

	public Issue createJIRAIssueFromSentenceObject(long aoId, ApplicationUser user) {
		if (aoId <= 0 || user == null) {
			LOGGER.error("Parameter are Invalid");
			return null;
		}

		PartOfJiraIssueText element = (PartOfJiraIssueText) this.getDecisionKnowledgeElement(aoId);

		JiraIssuePersistenceManager persistenceManager = KnowledgePersistenceManager.getOrCreate(this.projectKey)
				.getJiraIssueManager();
		DecisionKnowledgeElement decElement = persistenceManager.insertDecisionKnowledgeElement(element, user);

		MutableIssue issue = ComponentAccessor.getIssueService().getIssue(user, decElement.getId()).getIssue();

		IssueLinkManager issueLinkManager = ComponentAccessor.getIssueLinkManager();
		long linkTypeId = JiraIssuePersistenceManager.getLinkTypeId("contain");

		try {
			issueLinkManager.createIssueLink(element.getJiraIssueId(), issue.getId(), linkTypeId, (long) 0, user);
		} catch (CreateException e) {
			LOGGER.error("Creating JIRA issue from part of text failed. Message: " + e.getMessage());
			return null;
		}

		// delete sentence in comment
		int length = JiraIssueTextPersistenceManager.removeSentenceFromComment(element) * -1; // -1 because we
		// decrease the total number of letters
		updateSentenceLengthForOtherSentencesInSameComment(element, length);

		// delete ao sentence entry
		KnowledgePersistenceManager.getOrCreate(projectKey).deleteDecisionKnowledgeElement(element, null);

		createLinksForNonLinkedElements(element.getJiraIssueId());

		return issue;
	}

	public static List<DecisionKnowledgeElement> updateComment(Comment comment) {
		String projectKey = comment.getIssue().getProjectObject().getKey();
		List<PartOfText> partsOfText = new TextSplitterImpl().getPartsOfText(comment.getBody(), projectKey);

		JiraIssueTextPersistenceManager persistenceManager = KnowledgePersistenceManager.getOrCreate(projectKey)
				.getJiraIssueTextManager();

		List<DecisionKnowledgeElement> knowledgeElementsInText = persistenceManager
				.getElementsInComment(comment.getId());

		// @issue Elements used to be deleted and new ones were created afterwards.
		// How to enable a "real" update?
		// @decision Overwrite parts of Jira issue text in AO database if they exist!
		// @con If a new knowledge element is inserted at the beginning of the text, the
		// links in the knowledge graph might be wrong.
		int numberOfTextPartsInComment = knowledgeElementsInText.size();

		// Update AO entries
		for (int i = 0; i < partsOfText.size(); i++) {
			PartOfJiraIssueText sentence = new PartOfJiraIssueTextImpl(partsOfText.get(i), comment);
			if (i < numberOfTextPartsInComment) {
				sentence.setId(knowledgeElementsInText.get(i).getId());
				updateInDatabase(sentence);
			} else {
				sentence = (PartOfJiraIssueText) persistenceManager.insertDecisionKnowledgeElement(sentence, null);
			}
			createSmartLinkForElement(sentence);
			knowledgeElementsInText.set(i, sentence);
		}
		return knowledgeElementsInText;
	}

	public static List<DecisionKnowledgeElement> updateDescription(Issue jiraIssue) {
		String projectKey = jiraIssue.getProjectObject().getKey();
		List<PartOfText> partsOfText = new TextSplitterImpl().getPartsOfText(jiraIssue.getDescription(), projectKey);

		JiraIssueTextPersistenceManager persistenceManager = KnowledgePersistenceManager.getOrCreate(projectKey)
				.getJiraIssueTextManager();

		List<DecisionKnowledgeElement> parts = persistenceManager.getElementsInDescription(jiraIssue.getId());
		int numberOfTextParts = parts.size();

		for (int i = 0; i < partsOfText.size(); i++) {
			PartOfJiraIssueText sentence = new PartOfJiraIssueTextImpl(partsOfText.get(i), jiraIssue);
			if (i < numberOfTextParts) {
				// Update AO entry
				sentence.setId(parts.get(i).getId());
				updateInDatabase(sentence);
				parts.set(i, sentence);
			} else {
				// Create new AO entry
				sentence = (PartOfJiraIssueText) persistenceManager.insertDecisionKnowledgeElement(sentence, null);
				parts.add(sentence);
			}
			sentence = (PartOfJiraIssueText) persistenceManager.getDecisionKnowledgeElement(sentence);
			createSmartLinkForElement(sentence);
			KnowledgeGraph.getOrCreate(projectKey).updateNode(sentence);
		}
		return parts;
	}

	private static int removeSentenceFromComment(PartOfJiraIssueText element) {
		MutableComment mutableComment = element.getComment();
		String newBody = mutableComment.getBody();
		newBody = newBody.substring(0, element.getStartPosition()) + newBody.substring(element.getEndPosition());

		JiraIssueTextExtractionEventListener.editCommentLock = true;
		mutableComment.setBody(newBody);
		ComponentAccessor.getCommentManager().update(mutableComment, true);
		JiraIssueTextExtractionEventListener.editCommentLock = false;
		return element.getEndPosition() - element.getStartPosition();
	}

	public List<DecisionKnowledgeElement> getUserValidatedPartsOfText(String projectKey) {
		List<DecisionKnowledgeElement> validatedPartsOfText = new ArrayList<DecisionKnowledgeElement>();
		if (projectKey == null || projectKey.isEmpty()) {
			return validatedPartsOfText;
		}
		PartOfJiraIssueTextInDatabase[] databaseEntries = ACTIVE_OBJECTS.find(PartOfJiraIssueTextInDatabase.class,
				Query.select().where("PROJECT_KEY = ? and VALIDATED = ?", projectKey, true));

		for (PartOfJiraIssueTextInDatabase databaseEntry : databaseEntries) {
			PartOfJiraIssueText validatedPartOfText = new PartOfJiraIssueTextImpl(databaseEntry);
			validatedPartsOfText.add(validatedPartOfText);
		}
		return validatedPartsOfText;
	}

	public List<DecisionKnowledgeElement> getUnvalidatedPartsOfText(String projectKey) {
		List<DecisionKnowledgeElement> unvalidatedPartsOfText = new ArrayList<DecisionKnowledgeElement>();
		if (projectKey == null || projectKey.isEmpty()) {
			return unvalidatedPartsOfText;
		}
		PartOfJiraIssueTextInDatabase[] databaseEntries = ACTIVE_OBJECTS.find(PartOfJiraIssueTextInDatabase.class,
				Query.select().where("PROJECT_KEY = ? and VALIDATED = ?", projectKey, false));

		for (PartOfJiraIssueTextInDatabase databaseEntry : databaseEntries) {
			PartOfJiraIssueText validatedPartOfText = new PartOfJiraIssueTextImpl(databaseEntry);
			unvalidatedPartsOfText.add(validatedPartOfText);
		}
		return unvalidatedPartsOfText;
	}

	/**
	 * Split a text into parts (substrings).
	 *
	 * @param comment
	 *            JIRA issue comment.
	 * @return list of sentence objects.
	 * @see PartOfText
	 */
	public static List<PartOfJiraIssueText> getPartsOfComment(Comment comment) {
		String projectKey = comment.getIssue().getProjectObject().getKey();
		List<PartOfText> partsOfText = new TextSplitterImpl().getPartsOfText(comment.getBody(), projectKey);

		List<PartOfJiraIssueText> parts = new ArrayList<PartOfJiraIssueText>();

		JiraIssueTextPersistenceManager persistenceManager = KnowledgePersistenceManager.getOrCreate(projectKey)
				.getJiraIssueTextManager();
		// Create AO entries
		for (PartOfText partOfText : partsOfText) {
			PartOfJiraIssueText sentence = new PartOfJiraIssueTextImpl(partOfText, comment);
			sentence = (PartOfJiraIssueText) persistenceManager.insertDecisionKnowledgeElement(sentence, null);
			createSmartLinkForElement(sentence);
			parts.add(sentence);
		}
		return parts;
	}

}
