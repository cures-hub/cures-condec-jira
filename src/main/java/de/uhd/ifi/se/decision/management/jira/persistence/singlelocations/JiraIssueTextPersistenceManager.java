package de.uhd.ifi.se.decision.management.jira.persistence.singlelocations;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.jira.issue.comments.MutableComment;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.classification.JiraIssueTextParser;
import de.uhd.ifi.se.decision.management.jira.classification.TextClassifier;
import de.uhd.ifi.se.decision.management.jira.eventlistener.implementation.JiraIssueTextExtractionEventListener;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.AutomaticLinkCreator;
import de.uhd.ifi.se.decision.management.jira.persistence.GenericLinkManager;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.PartOfJiraIssueTextInDatabase;
import net.java.ao.Query;

/**
 * Extends the abstract class
 * {@link AbstractPersistenceManagerForSingleLocation}. Uses Jira issue comments
 * or the description to store decision knowledge. A sentence in a comment or
 * the description of a Jira issue is called {@link PartOfJiraIssueText}.
 *
 * @see AbstractPersistenceManagerForSingleLocation
 */
public class JiraIssueTextPersistenceManager extends AbstractPersistenceManagerForSingleLocation {
	private static final Logger LOGGER = LoggerFactory.getLogger(JiraIssueTextPersistenceManager.class);
	private static final ActiveObjects ACTIVE_OBJECTS = ComponentGetter.getActiveObjects();

	public JiraIssueTextPersistenceManager(String projectKey) {
		this.projectKey = projectKey;
		this.documentationLocation = DocumentationLocation.JIRAISSUETEXT;
	}

	@Override
	public boolean deleteKnowledgeElement(long id, ApplicationUser user) {
		if (id <= 0 || user == null) {
			LOGGER.error(
					"Element cannot be deleted since it does not exist in database (id is less than zero) or the user is null.");
			return false;
		}
		boolean isDeleted = false;
		for (PartOfJiraIssueTextInDatabase databaseEntry : ACTIVE_OBJECTS.find(PartOfJiraIssueTextInDatabase.class,
				Query.select().where("ID = ?", id))) {
			GenericLinkManager.deleteLinksForElement(id, DocumentationLocation.JIRAISSUETEXT);
			KnowledgeGraph.getInstance(projectKey).removeVertex(new PartOfJiraIssueText(databaseEntry));
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

	/**
	 * Deletes all decision knowledge elements and their links documented in the
	 * description and all comments of a Jira issue. Does not delete the text or
	 * change the description itself.
	 *
	 * @param jiraIssue
	 *            that the decision knowledge elements are documented in.
	 * @return true if deletion was successfull.
	 */
	public boolean deleteElementsInJiraIssue(Issue jiraIssue) {
		if (jiraIssue == null) {
			return false;
		}
		List<Comment> comments = ComponentAccessor.getCommentManager().getComments(jiraIssue);
		comments.forEach(comment -> deleteElementsInComment(comment));
		return deleteElementsInDescription(jiraIssue);
	}

	/**
	 * Deletes all decision knowledge elements and their links documented in the
	 * description and all comments of a Jira project. Does not delete the text or
	 * change the description itself.
	 *
	 * @return true if deletion was successfull.
	 */
	public boolean deleteElementsOfProject() {
		PartOfJiraIssueTextInDatabase[] databaseEntries = ACTIVE_OBJECTS.find(PartOfJiraIssueTextInDatabase.class,
				Query.select().where("PROJECT_KEY = ?", projectKey));
		ACTIVE_OBJECTS.delete(databaseEntries);
		KnowledgeGraph.instances.remove(projectKey);
		return GenericLinkManager.deleteInvalidLinks();
	}

	private boolean deletePartsOfText(long jiraIssueId, long commentId) {
		boolean isDeleted = false;
		PartOfJiraIssueTextInDatabase[] databaseEntries = ACTIVE_OBJECTS.find(PartOfJiraIssueTextInDatabase.class,
				Query.select().where("PROJECT_KEY = ? AND JIRA_ISSUE_ID = ? AND COMMENT_ID = ?", projectKey,
						jiraIssueId, commentId));
		for (PartOfJiraIssueTextInDatabase databaseEntry : databaseEntries) {
			KnowledgeGraph.getInstance(projectKey).removeVertex(new PartOfJiraIssueText(databaseEntry));
			GenericLinkManager.deleteLinksForElement(databaseEntry.getId(), DocumentationLocation.JIRAISSUETEXT);
			isDeleted = PartOfJiraIssueTextInDatabase.deleteElement(databaseEntry);
		}
		return isDeleted;
	}

	@Override
	public KnowledgeElement getKnowledgeElement(long id) {
		PartOfJiraIssueText sentence = null;
		for (PartOfJiraIssueTextInDatabase databaseEntry : ACTIVE_OBJECTS.find(PartOfJiraIssueTextInDatabase.class,
				Query.select().where("ID = ?", id))) {
			sentence = new PartOfJiraIssueText(databaseEntry);
		}
		return sentence;
	}

	@Override
	public KnowledgeElement getKnowledgeElement(String key) {
		if (key == null) {
			return null;
		}
		long id = Long.parseLong(key.split(":")[1]);
		return getKnowledgeElement(id);
	}

	@Override
	public List<KnowledgeElement> getKnowledgeElements() {
		List<KnowledgeElement> knowledgeElements = new ArrayList<>();
		for (PartOfJiraIssueTextInDatabase databaseEntry : ACTIVE_OBJECTS.find(PartOfJiraIssueTextInDatabase.class,
				Query.select().where("PROJECT_KEY = ?", projectKey))) {
			knowledgeElements.add(new PartOfJiraIssueText(databaseEntry));
		}
		return knowledgeElements;
	}

	/**
	 * @param jiraIssueId
	 *            id of the Jira issue that the decision knowledge elements are
	 *            documented in.
	 * @return list of all decision knowledge elements documented in the description
	 *         or comments of a Jira issue. Does also return irrelevant
	 *         sentences/parts of text.
	 */
	public List<KnowledgeElement> getElementsInJiraIssue(long jiraIssueId) {
		List<KnowledgeElement> elements = new ArrayList<>();
		for (PartOfJiraIssueTextInDatabase databaseEntry : ACTIVE_OBJECTS.find(PartOfJiraIssueTextInDatabase.class,
				Query.select().where("PROJECT_KEY = ? AND JIRA_ISSUE_ID = ?", projectKey, jiraIssueId))) {
			elements.add(new PartOfJiraIssueText(databaseEntry));
		}
		return elements;
	}

	/**
	 * @param jiraIssueId
	 *            id of the Jira issue that the decision knowledge elements and
	 *            irrelevant textual parts are documented in.
	 * @return list of all decision knowledge elements documented in the description
	 *         or comments of a Jira issue that are NOT validated by a user (i.e.
	 *         not manually approved after automatic text classification). Does also
	 *         return non-validated irrelevant sentences/parts of text.
	 */
	public List<KnowledgeElement> getNonValidatedElementsInJiraIssue(long jiraIssueId) {
		List<KnowledgeElement> nonValidatedElements = new ArrayList<KnowledgeElement>();
		for (KnowledgeElement element : getElementsInJiraIssue(jiraIssueId)) {
			PartOfJiraIssueText jiraIssueTextPart = (PartOfJiraIssueText) element;
			if (!jiraIssueTextPart.isValidated() && !jiraIssueTextPart.isTranscribedCommitReference()
					&& !jiraIssueTextPart.isCodeChangeExplanation()) {
				nonValidatedElements.add(jiraIssueTextPart);
			}
		}
		return nonValidatedElements;
	}

	/**
	 * @param commentId
	 *            id of the comment that the decision knowledge element(s) is/are
	 *            documented in.
	 * @return list of all decision knowledge elements documented in the a certain
	 *         comment of a Jira issue. Does also return irrelevant parts of text.
	 */
	public List<PartOfJiraIssueText> getElementsInComment(long commentId) {
		List<PartOfJiraIssueText> elements = new ArrayList<>();
		for (PartOfJiraIssueTextInDatabase databaseEntry : ACTIVE_OBJECTS.find(PartOfJiraIssueTextInDatabase.class,
				Query.select().where("PROJECT_KEY = ? AND COMMENT_ID = ?", projectKey, commentId)
						.order("START_POSITION ASC"))) {
			elements.add(new PartOfJiraIssueText(databaseEntry));
		}
		return elements;
	}

	/**
	 * @param jiraIssueId
	 *            id of the Jira issue that the decision knowledge elements are
	 *            documented in.
	 * @return list of all decision knowledge elements and parts of irrelevant text
	 *         documented in the description of a Jira issue. The comment id of such
	 *         elements is zero.
	 */
	public List<PartOfJiraIssueText> getElementsInDescription(long jiraIssueId) {
		List<PartOfJiraIssueText> elements = new ArrayList<>();
		for (PartOfJiraIssueTextInDatabase databaseEntry : ACTIVE_OBJECTS.find(PartOfJiraIssueTextInDatabase.class,
				Query.select()
						.where("PROJECT_KEY = ? AND JIRA_ISSUE_ID = ? AND COMMENT_ID = 0", projectKey, jiraIssueId)
						.order("START_POSITION ASC"))) {
			elements.add(new PartOfJiraIssueText(databaseEntry));
		}
		return elements;
	}

	/**
	 * @param summary
	 *            of the decision knowledge element.
	 * @param jiraIssueId
	 *            id of the Jira issue that the decision knowledge element is
	 *            documented in.
	 * @param type
	 *            {@link KnowledgeType} of the element.
	 * @return decision knowledge element documented in the description or comments
	 *         of a Jira issue with the summary and type.
	 */
	public KnowledgeElement getElementBySummaryAndTypeInJiraIssue(String summary, long jiraIssueId,
			KnowledgeType type) {
		if (summary == null || jiraIssueId <= 0 || type == null || summary.isBlank()) {
			return null;
		}
		List<KnowledgeElement> sentences = getElementsWithTypeInJiraIssue(jiraIssueId, type);
		for (KnowledgeElement sentence : sentences) {
			if (sentence.getSummary().trim().equalsIgnoreCase(summary)) {
				return sentence;
			}
		}
		LOGGER.debug("Nothing found for: " + summary);
		return null;
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
	public List<KnowledgeElement> getElementsWithTypeInJiraIssue(long jiraIssueId, KnowledgeType type) {
		List<KnowledgeElement> elements = getElementsInJiraIssue(jiraIssueId);
		elements.removeIf(e -> (e.getType() != type));
		return elements;
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
	public KnowledgeElement insertKnowledgeElement(KnowledgeElement element, ApplicationUser user,
			KnowledgeElement parentElement) {
		if (element == null || user == null) {
			return null;
		}
		if (parentElement == null || parentElement.getJiraIssue() == null) {
			return insertKnowledgeElement(element, user);
		}
		if (parentElement.getDocumentationLocation() == DocumentationLocation.JIRAISSUETEXT) {
			PartOfJiraIssueText parentSentence = (PartOfJiraIssueText) getKnowledgeElement(parentElement);
			MutableComment existingComment = parentSentence.getComment();
			if (existingComment != null) {
				StringBuffer stringBuffer = new StringBuffer(existingComment.getBody());
				stringBuffer.insert(parentSentence.getEndPosition(), "\n" + getTextWithTags(element));
				existingComment.setBody(stringBuffer.toString());
				ComponentAccessor.getCommentManager().update(existingComment, true);
			} else {
				StringBuffer stringBuffer = new StringBuffer(parentSentence.getJiraIssueDescription());
				stringBuffer.insert(parentSentence.getEndPosition(), "\n" + getTextWithTags(element));
				JiraIssuePersistenceManager.updateDescription(parentSentence.getJiraIssue(), stringBuffer.toString(),
						user);
			}
		} else {
			createCommentInJiraIssue(element, parentElement.getJiraIssue(), user);
		}

		// ensure that database id is set correctly
		KnowledgeElement elementWithCorrectId = getElementBySummaryAndTypeInJiraIssue(element.getSummary(),
				parentElement.getJiraIssue().getId(), element.getType());

		return elementWithCorrectId != null ? elementWithCorrectId : element;
	}

	private Comment createCommentInJiraIssue(KnowledgeElement element, Issue jiraIssue, ApplicationUser user) {
		String text = getTextWithTags(element);
		return ComponentAccessor.getCommentManager().create(jiraIssue, user, text, true);
	}

	private String getTextWithTags(KnowledgeElement element) {
		String tag = element.getType().getTag();
		return tag + element.getSummary() + " " + element.getDescription() + tag;
	}

	@Override
	public KnowledgeElement insertKnowledgeElement(KnowledgeElement element, ApplicationUser user) {
		KnowledgeElement existingElement = getKnowledgeElement(element);
		if (existingElement != null) {
			return existingElement;
		}

		PartOfJiraIssueTextInDatabase databaseEntry = ACTIVE_OBJECTS.create(PartOfJiraIssueTextInDatabase.class);

		setParameters((PartOfJiraIssueText) element, databaseEntry);
		databaseEntry.save();

		PartOfJiraIssueText sentence = new PartOfJiraIssueText(databaseEntry);
		if (sentence.getId() > 0 && sentence.isRelevant()) {
			KnowledgeGraph.getInstance(projectKey).addVertex(sentence);
		}
		return sentence;
	}

	public KnowledgeElement getKnowledgeElement(KnowledgeElement element) {
		if (element == null) {
			return null;
		}
		if (element.getId() > 0) {
			return getKnowledgeElement(element.getId());
		}

		PartOfJiraIssueText sentence = (PartOfJiraIssueText) element;
		PartOfJiraIssueText sentenceInDatabase = null;
		for (PartOfJiraIssueTextInDatabase databaseEntry : ACTIVE_OBJECTS.find(PartOfJiraIssueTextInDatabase.class,
				Query.select().where(
						"PROJECT_KEY = ? AND JIRA_ISSUE_ID = ? AND COMMENT_ID = ? AND END_POSITION = ? AND START_POSITION = ?",
						sentence.getProject().getProjectKey(), sentence.getJiraIssue().getId(), sentence.getCommentId(),
						sentence.getEndPosition(), sentence.getStartPosition()))) {
			sentenceInDatabase = new PartOfJiraIssueText(databaseEntry);
		}
		return sentenceInDatabase;
	}

	private void setParameters(PartOfJiraIssueText element, PartOfJiraIssueTextInDatabase databaseEntry) {
		databaseEntry.setProjectKey(element.getProject().getProjectKey());
		databaseEntry.setCommentId(element.getCommentId());
		databaseEntry.setType(element.getTypeAsString());
		databaseEntry.setRelevant(element.isRelevant());
		databaseEntry.setValidated(element.isValidated());
		if (element.isValidated()) {
			TextClassifier.getInstance(projectKey).update(element);
		}
		databaseEntry.setStartPosition(element.getStartPosition());
		databaseEntry.setEndPosition(element.getEndPosition());
		databaseEntry.setJiraIssueId(element.getJiraIssue().getId());
		databaseEntry.setStatus(element.getStatusAsString());
	}

	@Override
	public boolean updateKnowledgeElement(KnowledgeElement element, ApplicationUser user) {
		if (element == null || element.getProject() == null
				|| element.getDocumentationLocation() != documentationLocation) {
			return false;
		}

		// get corresponding element from database
		PartOfJiraIssueText sentence = (PartOfJiraIssueText) getKnowledgeElement(element);
		if (sentence == null) {
			return false;
		}
		// summary and description are the same for PartOfJiraIssueText objects
		sentence.setDescription(element.getDescription());
		sentence.setStatus(element.getStatus());
		sentence.setType(element.getType());
		sentence.setRelevant(element.getType() != KnowledgeType.OTHER);
		return updateElementInTextAndDatabase(sentence, user);
	}

	/**
	 * Updates the Jira issue description or comment and also the database entries
	 * for a {@link PartOfJiraIssueText} and all the other parts/sentences of the
	 * Jira issue description or comment.
	 * 
	 * @param newElement
	 *            {@link PartOfJiraIssueText} after the update.
	 * @param sentence
	 *            {@link PartOfJiraIssueText} before the update, i.e. in the old
	 *            state.
	 * @param user
	 *            authenticated Jira {@link ApplicationUser}.
	 * @return true if updating the Jira issue description or comment and also the
	 *         database entries was successful.
	 */
	public boolean updateElementInTextAndDatabase(PartOfJiraIssueText sentence, ApplicationUser user) {
		String tag = sentence.getType().getTag();
		String changedPartOfText = tag + sentence.getDescription() + tag;

		String text = sentence.getTextOfEntireDescriptionOrComment();
		String firstPartOfText = text.substring(0, sentence.getStartPosition());
		String lastPartOfText = text.substring(sentence.getEndPosition());

		String newBody = firstPartOfText + changedPartOfText + lastPartOfText;

		JiraIssueTextExtractionEventListener.editLock = true;
		MutableComment mutableComment = sentence.getComment();
		if (mutableComment == null) {
			JiraIssuePersistenceManager.updateDescription(sentence.getJiraIssue(), newBody, user);
		} else {
			mutableComment.setBody(newBody);
			mutableComment.setUpdated(new Date());
			mutableComment.setUpdateAuthor(user);
			ComponentAccessor.getCommentManager().update(mutableComment, true);
		}
		JiraIssueTextExtractionEventListener.editLock = false;

		int lengthDifference = changedPartOfText.length() - sentence.getLength();
		updateSentenceLengthForOtherSentencesInSameComment(sentence, lengthDifference);

		sentence.setEndPosition(sentence.getStartPosition() + changedPartOfText.length());
		return updateInDatabase(sentence);
	}

	public boolean updateInDatabase(PartOfJiraIssueText sentence) {
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
				sentence.getJiraIssue().getId())) {
			if (otherSentenceInComment.getStartPosition() > sentence.getStartPosition()
					&& otherSentenceInComment.getId() != sentence.getId()) {
				otherSentenceInComment.setStartPosition(otherSentenceInComment.getStartPosition() + lengthDifference);
				otherSentenceInComment.setEndPosition(otherSentenceInComment.getEndPosition() + lengthDifference);
				otherSentenceInComment.save();
			}
		}
	}

	@Override
	public ApplicationUser getCreator(KnowledgeElement element) {
		PartOfJiraIssueText sentence = (PartOfJiraIssueText) getKnowledgeElement(element.getId());
		if (sentence == null) {
			LOGGER.error("Element could not be found.");
			return null;
		}
		return sentence.getCreator();
	}

	public boolean createLinksForNonLinkedElements(Issue jiraIssue) {
		return createLinksForNonLinkedElements(getElementsInJiraIssue(jiraIssue.getId()));
	}

	public boolean createLinksForNonLinkedElements() {
		return createLinksForNonLinkedElements(getKnowledgeElements());
	}

	public boolean createLinksForNonLinkedElements(List<KnowledgeElement> elements) {
		boolean areElementsLinked = true;
		for (KnowledgeElement element : elements) {
			areElementsLinked = areElementsLinked && ensureThatElementIsLinked(element);
		}
		return areElementsLinked;
	}

	public boolean ensureThatElementIsLinked(KnowledgeElement element) {
		if (element.isLinked() > 0) {
			return true;
		}
		KnowledgePersistenceManager persistenceManager = KnowledgePersistenceManager.getInstance(projectKey);
		KnowledgeElement parentElement = persistenceManager.getJiraIssueManager()
				.getKnowledgeElement(element.getJiraIssue().getId());
		long linkId = persistenceManager.insertLink(parentElement, element, null);
		return linkId > 0;
	}

	/**
	 * Deletes elements in database that are broken (are neither stored in
	 * description nor in a comment or have zero length).
	 *
	 * @param user
	 *            Jira {@link ApplicationUser}.
	 * @return true if any element was deleted.
	 */
	public boolean deleteInvalidElements(ApplicationUser user) {
		boolean isAnyElementDeleted = false;
		for (KnowledgeElement element : getKnowledgeElements()) {
			if (!((PartOfJiraIssueText) element).isValid()) {
				deleteKnowledgeElement(element, user);
				isAnyElementDeleted = true;
			}
		}
		return isAnyElementDeleted;
	}

	/**
	 * Updates the decision knowledge elements and parts of irrelevant text within
	 * the description of a Jira issue.
	 * 
	 * @param elementId
	 *            of the sentence that should be converted into an entire Jira
	 *            issue.
	 * @param user
	 *            Jira {@link ApplicationUser}.
	 * @return newly created Jira issue with summary and type of the former
	 *         sentence.
	 * 
	 * @issue Should the sentence be deleted in the description or comment after a
	 *        Jira issue was created with the same type and summary?
	 * @decision The sentence is not deleted in the description or comment after a
	 *           Jira issue was created with the same type and summary!
	 * @pro The origin of the Jira issue is kept.
	 * @pro Easier to implement.
	 */
	public Issue createJiraIssueFromSentenceObject(long elementId, ApplicationUser user) {
		if (elementId <= 0 || user == null) {
			LOGGER.error("Parameter are Invalid");
			return null;
		}

		PartOfJiraIssueText sentence = (PartOfJiraIssueText) getKnowledgeElement(elementId);

		// delete sentence in database and knowledge graph (not in description/comment)
		KnowledgePersistenceManager.getInstance(projectKey).deleteKnowledgeElement(sentence, user);

		// create Jira issue and link it
		JiraIssuePersistenceManager jiraIssuePersistenceManager = KnowledgePersistenceManager.getInstance(projectKey)
				.getJiraIssueManager();
		KnowledgeElement jiraIssue = jiraIssuePersistenceManager.insertKnowledgeElement(sentence, user);
		Link link = Link.instantiateDirectedLink(new KnowledgeElement(sentence.getJiraIssue()), jiraIssue);
		jiraIssuePersistenceManager.insertLink(link, user);

		return ComponentAccessor.getIssueService().getIssue(user, jiraIssue.getId()).getIssue();
	}

	/**
	 * Updates the decision knowledge elements and parts of irrelevant text within
	 * the description and all comments of a Jira issue. Splits the
	 * description/comment into parts (substrings) and inserts these parts into the
	 * database table. Does not update the description/comment itself since that was
	 * already done by the user.
	 * 
	 * @param jiraIssue
	 *            Jira issue with decision knowledge elements in its description and
	 *            comments.
	 * @param isUpdatedEvenIfSameSize
	 *            if true the parts of text in the database are updated even if the
	 *            same amount of was parsed as before (e.g. useful after changing
	 *            the summary of an element).
	 */
	public void updateElementsOfJiraIssueInDatabase(Issue jiraIssue, boolean isUpdatedEvenIfSameSize) {
		updateElementsOfDescriptionInDatabase(jiraIssue, isUpdatedEvenIfSameSize);
		List<Comment> comments = ComponentAccessor.getCommentManager().getComments(jiraIssue);
		for (Comment comment : comments) {
			updateElementsOfCommentInDatabase(comment, isUpdatedEvenIfSameSize);
		}
	}

	/**
	 * Updates the decision knowledge elements and parts of irrelevant text within a
	 * comment of a Jira issue. Splits the comment into parts (substrings) and
	 * inserts these parts into the database table. Does not update the description
	 * itself since that was already done by the user.
	 * 
	 * @param comment
	 *            of a Jira issue with decision knowledge elements.
	 * @param isUpdatedEvenIfSameSize
	 *            if true the parts of text in the database are updated even if the
	 *            same amount of was parsed as before (e.g. useful after changing
	 *            the summary of an element).
	 * @return list of identified knowledge elements.
	 * 
	 * @issue Elements used to be deleted and new ones were created afterwards. How
	 *        to enable a "real" update?
	 * @decision Overwrite parts of Jira issue text in AO database if they exist!
	 * @con If a new knowledge element is inserted at the beginning of the text, the
	 *      links in the knowledge graph might be wrong.
	 */
	public List<PartOfJiraIssueText> updateElementsOfCommentInDatabase(Comment comment,
			boolean isUpdatedEvenIfSameSize) {
		List<PartOfJiraIssueText> partsOfComment = new JiraIssueTextParser(projectKey)
				.getPartsOfText(comment.getBody());
		for (PartOfJiraIssueText sentence : partsOfComment) {
			sentence.setComment(comment);
		}
		List<PartOfJiraIssueText> elementsInDatabase = getElementsInComment(comment.getId());

		if (partsOfComment.size() != elementsInDatabase.size()) {
			deleteElementsInComment(comment);
			return insertKnowledgeElements(partsOfComment);
		}
		if (isUpdatedEvenIfSameSize) {
			return updateKnowledgeElements(partsOfComment, elementsInDatabase);
		}
		return elementsInDatabase;
	}

	/**
	 * Updates the decision knowledge elements and parts of irrelevant text within
	 * the description of a Jira issue. Splits the description into parts
	 * (substrings) and inserts these parts into the database table. Does not update
	 * the description itself since that was already done by the user.
	 * 
	 * @param jiraIssue
	 *            Jira issue with decision knowledge elements in its description.
	 * @param isUpdatedEvenIfSameSize
	 *            if true the parts of text in the database are updated even if the
	 *            same amount of text was parsed as before (e.g. useful after
	 *            changing the summary of an element).
	 * @return list of identified knowledge elements.
	 */
	public List<PartOfJiraIssueText> updateElementsOfDescriptionInDatabase(Issue jiraIssue,
			boolean isUpdatedEvenIfSameSize) {
		LOGGER.debug(jiraIssue.getDescription());
		List<PartOfJiraIssueText> partsOfDescription = new JiraIssueTextParser(projectKey)
				.getPartsOfText(jiraIssue.getDescription());
		for (PartOfJiraIssueText sentence : partsOfDescription) {
			sentence.setJiraIssue(jiraIssue);
		}
		List<PartOfJiraIssueText> elementsInDatabase = getElementsInDescription(jiraIssue.getId());

		if (elementsInDatabase.size() != partsOfDescription.size()) {
			deleteElementsInDescription(jiraIssue);
			return insertKnowledgeElements(partsOfDescription);
		}
		if (isUpdatedEvenIfSameSize) {
			return updateKnowledgeElements(partsOfDescription, elementsInDatabase);
		}
		return elementsInDatabase;
	}

	/**
	 * Inserts all {@link PartOfJiraIssueText}s into database.
	 * 
	 * @param partsOfText
	 *            sentences, i.e. {@link PartOfJiraIssueText}s to be inserted into
	 *            database.
	 * @return list of {@link PartOfJiraIssueText}s with database id.
	 */
	private List<PartOfJiraIssueText> insertKnowledgeElements(List<PartOfJiraIssueText> partsOfText) {
		List<PartOfJiraIssueText> elementsInDatabase = new ArrayList<>();
		for (PartOfJiraIssueText sentence : partsOfText) {
			sentence = (PartOfJiraIssueText) insertKnowledgeElement(sentence, null);
			elementsInDatabase.add(sentence);
			AutomaticLinkCreator.createSmartLinkForSentenceIfRelevant(sentence);
		}
		return elementsInDatabase;
	}

	/**
	 * Updates all {@link PartOfJiraIssueText}s in database after changes in the
	 * Jira issue description or a comment.
	 * 
	 * @param newPartsOfText
	 *            sentences, i.e. {@link PartOfJiraIssueText}s extracted from the
	 *            Jira issue description or a comment.
	 * @param elementsInDatabase
	 *            existing {@link PartOfJiraIssueText}s in the database that should
	 *            be updated.
	 * @return updated list of {@link PartOfJiraIssueText}s with database ids.
	 */
	private List<PartOfJiraIssueText> updateKnowledgeElements(List<PartOfJiraIssueText> newPartsOfText,
			List<PartOfJiraIssueText> elementsInDatabase) {
		for (int i = 0; i < newPartsOfText.size(); i++) {
			PartOfJiraIssueText sentence = newPartsOfText.get(i);
			sentence.setId(elementsInDatabase.get(i).getId());
			sentence.setStatus(elementsInDatabase.get(i).getStatus());
			updateInDatabase(sentence);
			elementsInDatabase.set(i, sentence);
			KnowledgeGraph.getInstance(projectKey).updateElement(sentence);
			AutomaticLinkCreator.createSmartLinkForSentenceIfRelevant(sentence);
		}
		return elementsInDatabase;
	}
}
