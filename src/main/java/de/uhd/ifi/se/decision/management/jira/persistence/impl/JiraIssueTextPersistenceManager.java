package de.uhd.ifi.se.decision.management.jira.persistence.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.exception.CreateException;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
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
import de.uhd.ifi.se.decision.management.jira.model.impl.LinkImpl;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfText;
import de.uhd.ifi.se.decision.management.jira.model.text.impl.PartOfJiraIssueTextImpl;
import de.uhd.ifi.se.decision.management.jira.model.text.impl.TextSplitterImpl;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.LinkInDatabase;
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

	public static boolean deletePartsOfComment(Comment comment) {
		if (comment == null) {
			LOGGER.error("Sentences in comment cannot be deleted since the comment is null.");
			return false;
		}
		return deletePartsOfText(comment.getIssue().getId(), comment.getId());
	}

	public static boolean deletePartsOfDescription(Issue jiraIssue) {
		if (jiraIssue == null) {
			LOGGER.error("Sentences in comment cannot be deleted since the JIRA issue is null.");
			return false;
		}
		return deletePartsOfText(jiraIssue.getId(), 0);
	}

	private static boolean deletePartsOfText(long jiraIssueId, long commentId) {
		boolean isDeleted = false;
		PartOfJiraIssueTextInDatabase[] commentSentences = ACTIVE_OBJECTS.find(PartOfJiraIssueTextInDatabase.class,
				Query.select().where("JIRA_ISSUE_ID = ? AND COMMENT_ID = ?", jiraIssueId, commentId));
		for (PartOfJiraIssueTextInDatabase databaseEntry : commentSentences) {
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

	public static DecisionKnowledgeElement searchForLast(PartOfJiraIssueText sentence, KnowledgeType typeToSearch) {
		PartOfJiraIssueText lastSentence = null;
		PartOfJiraIssueTextInDatabase[] databaseEntries = ACTIVE_OBJECTS.find(PartOfJiraIssueTextInDatabase.class,
				Query.select().where("JIRA_ISSUE_ID = ?", sentence.getJiraIssueId()).order("ID DESC"));

		for (PartOfJiraIssueTextInDatabase databaseEntry : databaseEntries) {
			if (databaseEntry.getType().equals(typeToSearch.toString())) {
				lastSentence = new PartOfJiraIssueTextImpl(databaseEntry);
				break;
			}
		}
		return lastSentence;
	}

	@Override
	public DecisionKnowledgeElement getDecisionKnowledgeElement(String key) {
		long commentId = Long.parseLong(key.split(":")[1]);
		return getDecisionKnowledgeElement(commentId);
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

	public static List<DecisionKnowledgeElement> getElementsForIssue(long issueId, String projectKey) {
		List<DecisionKnowledgeElement> elements = new ArrayList<DecisionKnowledgeElement>();
		for (PartOfJiraIssueTextInDatabase databaseEntry : ACTIVE_OBJECTS.find(PartOfJiraIssueTextInDatabase.class,
				Query.select().where("PROJECT_KEY = ? AND JIRA_ISSUE_ID = ?", projectKey, issueId))) {
			elements.add(new PartOfJiraIssueTextImpl(databaseEntry));
		}
		return elements;
	}

	public static List<DecisionKnowledgeElement> getElementsForComment(long commentId) {
		List<DecisionKnowledgeElement> elements = new ArrayList<DecisionKnowledgeElement>();
		for (PartOfJiraIssueTextInDatabase databaseEntry : ACTIVE_OBJECTS.find(PartOfJiraIssueTextInDatabase.class,
				Query.select().where("COMMENT_ID = ?", commentId))) {
			elements.add(new PartOfJiraIssueTextImpl(databaseEntry));
		}
		return elements;
	}

	public static List<DecisionKnowledgeElement> getElementsForDescription(long jiraIssueId) {
		List<DecisionKnowledgeElement> elements = new ArrayList<DecisionKnowledgeElement>();
		for (PartOfJiraIssueTextInDatabase databaseEntry : ACTIVE_OBJECTS.find(PartOfJiraIssueTextInDatabase.class,
				Query.select().where("COMMENT_ID = 0 AND JIRA_ISSUE_ID = ?", jiraIssueId))) {
			elements.add(new PartOfJiraIssueTextImpl(databaseEntry));
		}
		return elements;
	}

	/**
	 * Works more efficient than "getElementsForIssue" for Sentence ID searching in
	 * Macros
	 *
	 * @param jiraIssueId
	 * @param projectKey
	 * @param type
	 * @return A list of all fitting Sentence objects
	 */
	public static List<DecisionKnowledgeElement> getElementsForJiraIssueWithType(long jiraIssueId, String projectKey,
			String type) {

		List<DecisionKnowledgeElement> elements = new ArrayList<DecisionKnowledgeElement>();
		if (jiraIssueId <= 0 || projectKey == null || type == null) {
			LOGGER.error("Id, ProjectKey, Type are Invalid");
			return elements;
		}
		for (PartOfJiraIssueTextInDatabase databaseEntry : ACTIVE_OBJECTS.find(PartOfJiraIssueTextInDatabase.class,
				Query.select().where("PROJECT_KEY = ? AND JIRA_ISSUE_ID = ? AND TYPE = ?", projectKey, jiraIssueId,
						type))) {
			elements.add(new PartOfJiraIssueTextImpl(databaseEntry));
		}
		return elements;
	}

	public static long getIdOfSentenceForMacro(String body, long issueId, String typeString, String projectKey) {
		if (body == null || issueId <= 0 || typeString == null || projectKey == null) {
			return 0;
		}
		List<DecisionKnowledgeElement> sentences = getElementsForJiraIssueWithType(issueId, projectKey, typeString);
		for (DecisionKnowledgeElement sentence : sentences) {
			if (sentence.getDescription().trim().equals(body.trim().replaceAll("<[^>]*>", ""))) {
				return sentence.getId();
			}
		}
		LOGGER.debug("Nothing found for: " + body.replace("<br/>", "").trim());
		return 0;
	}

	@Override
	public List<DecisionKnowledgeElement> getElementsLinkedWithInwardLinks(DecisionKnowledgeElement element) {
		List<Link> inwardLink = getInwardLinks(element);
		List<DecisionKnowledgeElement> inwardElement = new ArrayList<>();
		for (Link link : inwardLink) {
			inwardElement.add(link.getSource());
		}
		return inwardElement;
	}

	@Override
	public List<DecisionKnowledgeElement> getElementsLinkedWithOutwardLinks(DecisionKnowledgeElement element) {
		List<Link> outwardLink = getOutwardLinks(element);
		List<DecisionKnowledgeElement> outwardElements = new ArrayList<>();
		for (Link link : outwardLink) {
			outwardElements.add(link.getTarget());
		}
		return outwardElements;
	}

	@Override
	public List<Link> getInwardLinks(DecisionKnowledgeElement element) {
		List<Link> inwardLinks = new ArrayList<Link>();
		LinkInDatabase[] links = ACTIVE_OBJECTS.find(LinkInDatabase.class,
				Query.select().where("DESTINATION_ID = ? AND DEST_DOCUMENTATION_LOCATION = ?", element.getId(),
						element.getDocumentationLocation().getIdentifier()));
		for (LinkInDatabase link : links) {
			Link inwardLink = new LinkImpl(link);
			inwardLink.setDestinationElement(element);
			AbstractPersistenceManagerForSingleLocation sourcePersistenceManager = KnowledgePersistenceManager
					.getOrCreate(projectKey).getPersistenceManager(link.getSourceDocumentationLocation());
			inwardLink.setSourceElement(sourcePersistenceManager.getDecisionKnowledgeElement(link.getSourceId()));
			inwardLinks.add(inwardLink);
		}
		return inwardLinks;
	}

	@Override
	public List<Link> getOutwardLinks(DecisionKnowledgeElement element) {
		List<Link> outwardLinks = new ArrayList<Link>();
		LinkInDatabase[] links = ACTIVE_OBJECTS.find(LinkInDatabase.class,
				Query.select().where("SOURCE_ID = ? AND SOURCE_DOCUMENTATION_LOCATION = ?", element.getId(),
						element.getDocumentationLocation().getIdentifier()));
		for (LinkInDatabase link : links) {
			Link outwardLink = new LinkImpl(link);
			outwardLink.setSourceElement(element);
			AbstractPersistenceManagerForSingleLocation destinationPersistenceManager = KnowledgePersistenceManager
					.getOrCreate(projectKey).getPersistenceManager(link.getDestDocumentationLocation());
			outwardLink.setDestinationElement(
					destinationPersistenceManager.getDecisionKnowledgeElement(link.getDestinationId()));
			outwardLinks.add(outwardLink);
		}
		return outwardLinks;
	}

	@Override
	public DecisionKnowledgeElement insertDecisionKnowledgeElement(DecisionKnowledgeElement element,
			ApplicationUser user, DecisionKnowledgeElement parentElement) {
		if (element == null || user == null || parentElement == null) {
			return null;
		}
		Issue jiraIssue = getJiraIssue(parentElement);
		if (jiraIssue == null) {
			return null;
		}
		createCommentInJiraIssue(element, jiraIssue, user);
		return insertDecisionKnowledgeElement(element, user);
	}

	private Issue getJiraIssue(DecisionKnowledgeElement element) {
		if (element == null) {
			return null;
		}
		long jiraIssueId;
		if (element.getDocumentationLocation() == DocumentationLocation.JIRAISSUETEXT) {
			PartOfJiraIssueText sentence = (PartOfJiraIssueText) this.getDecisionKnowledgeElement(element.getId());
			jiraIssueId = sentence.getJiraIssueId();
		} else {
			jiraIssueId = element.getId();
		}
		return ComponentAccessor.getIssueManager().getIssueObject(jiraIssueId);
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
		DecisionKnowledgeElement existingElement = getDecisionKnowledgeElement((PartOfJiraIssueText) element);
		if (existingElement != null) {
			DecisionKnowledgeElement parentElement = new DecisionKnowledgeElementImpl(
					((PartOfJiraIssueText) element).getJiraIssueId(), projectKey, "i");
			JiraIssueTextPersistenceManager.checkIfSentenceHasAValidLink(existingElement, parentElement,
					LinkType.getLinkTypeForKnowledgeType(existingElement.getType()));
			return existingElement;
		}
		return null;
	}

	public DecisionKnowledgeElement getDecisionKnowledgeElement(PartOfJiraIssueText sentence) {
		if (sentence == null) {
			return null;
		}
		if (sentence.getId() > 0) {
			return this.getDecisionKnowledgeElement(sentence.getId());
		}

		PartOfJiraIssueText sentenceInDatabase = null;
		for (PartOfJiraIssueTextInDatabase databaseEntry : ACTIVE_OBJECTS.find(PartOfJiraIssueTextInDatabase.class,
				Query.select().where("PROJECT_KEY = ? AND COMMENT_ID = ? AND END_POSITION = ? AND START_POSITION = ?",
						sentence.getProject().getProjectKey(), sentence.getCommentId(), sentence.getEndPosition(),
						sentence.getStartPosition()))) {
			sentenceInDatabase = new PartOfJiraIssueTextImpl(databaseEntry);
		}
		return sentenceInDatabase;
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
			createSmartLinkForSentence(sentence);
			parts.add(sentence);
		}
		return parts;
	}

	private static void setParameters(PartOfJiraIssueText element, PartOfJiraIssueTextInDatabase databaseEntry) {
		databaseEntry.setProjectKey(element.getProject().getProjectKey());
		databaseEntry.setCommentId(element.getCommentId());
		databaseEntry.setType(element.getTypeAsString());
		databaseEntry.setRelevant(element.isRelevant());
		databaseEntry.setValidated(element.isValidated());
		if (element.isValidated()) {
			try {
				classificationTrainer.update(element);
			} catch (Exception e) {
				// TODO Replace System.err.println with LOGGER.error
				System.err.println("Could not update Classifier.");
				e.printStackTrace();
			}
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
		return updatePartOfJiraIssueText(createPartOfJiraIssueText(element), user);
	}

	private static PartOfJiraIssueText createPartOfJiraIssueText(DecisionKnowledgeElement element) {
		PartOfJiraIssueText sentence = new PartOfJiraIssueTextImpl();
		sentence.setId(element.getId());
		sentence.setType(element.getType());
		sentence.setSummary(element.getSummary());
		sentence.setDescription(element.getDescription());
		sentence.setProject(element.getProject());
		sentence.setValidated(true);
		return sentence;
	}

	@Override
	public boolean updateDecisionKnowledgeElementWithoutStatusChange(DecisionKnowledgeElement element,
			ApplicationUser user) {
		if (element == null) {
			return false;
		}
		PartOfJiraIssueText partOfJiraIssueText = createPartOfJiraIssueText(element);
		PartOfJiraIssueText partOfJiraIssueTextInDatabase = (PartOfJiraIssueText) getDecisionKnowledgeElement(
				element.getId());
		if (partOfJiraIssueTextInDatabase == null) {
			return false;
		}
		return updateElementInDatabase(partOfJiraIssueText, partOfJiraIssueTextInDatabase, user);
	}

	@Override
	public ApplicationUser getCreator(DecisionKnowledgeElement element) {
		if (element.getKey().contains(":")) {
			long commentId = Long.parseLong(element.getKey().split(":")[1]);
			PartOfJiraIssueText issueText = (PartOfJiraIssueText) getDecisionKnowledgeElement(commentId);
			Comment comment = issueText.getComment();
			if (comment == null) {
				Issue issue = ((PartOfJiraIssueText) element).getJiraIssue();
				return issue.getReporter();
			}
			return comment.getAuthorApplicationUser();
		}
		LOGGER.error("Element is not a Sentence");
		return element.getProject().getPersistenceStrategy().getCreator(element);
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

		boolean isUpdated = updateInDatabase(sentence);
		// if (sentence.isRelevant()) {
		// KnowledgeGraph.getOrCreate(sentence.getProject().getProjectKey()).updateNode(sentence);
		// }
		return isUpdated;
	}

	public static boolean updateInDatabase(PartOfJiraIssueText sentence) {
		boolean isUpdated = false;
		for (PartOfJiraIssueTextInDatabase databaseEntry : ACTIVE_OBJECTS.find(PartOfJiraIssueTextInDatabase.class)) {
			if (databaseEntry.getId() == sentence.getId()) {
				setParameters(sentence, databaseEntry);
				databaseEntry.save();
				isUpdated = true;
				// KnowledgeGraph.getOrCreate(sentence.getProject().getProjectKey()).addVertex(sentence);
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

	public static boolean createSmartLinkForSentence(PartOfJiraIssueText sentence) {
		if (sentence == null) {
			return false;
		}
		if (AbstractPersistenceManagerForSingleLocation.isElementLinked(sentence)) {
			return true;
		}
		boolean isLinkCreated = false;
		KnowledgeType knowledgeType = sentence.getType();

		if (knowledgeType == KnowledgeType.ARGUMENT || knowledgeType == KnowledgeType.PRO
				|| knowledgeType == KnowledgeType.CON) {
			DecisionKnowledgeElement lastElement = getMostRecentElement(
					searchForLast(sentence, KnowledgeType.ALTERNATIVE),
					searchForLast(sentence, KnowledgeType.DECISION));
			isLinkCreated = createLink(lastElement, sentence);
		}

		if (knowledgeType == KnowledgeType.DECISION || knowledgeType == KnowledgeType.ALTERNATIVE) {
			DecisionKnowledgeElement lastElement = searchForLast(sentence, KnowledgeType.ISSUE);
			isLinkCreated = createLink(lastElement, sentence);
		}

		if (!isLinkCreated && sentence.isRelevant()) {
			String projectKey = sentence.getProject().getProjectKey();
			JiraIssuePersistenceManager persistenceManager = KnowledgePersistenceManager.getOrCreate(projectKey)
					.getJiraIssueManager();
			DecisionKnowledgeElement parentElement = persistenceManager
					.getDecisionKnowledgeElement(sentence.getJiraIssueId());
			isLinkCreated = createLink(parentElement, sentence);
		}
		return isLinkCreated;
	}

	public static DecisionKnowledgeElement getMostRecentElement(DecisionKnowledgeElement first,
			DecisionKnowledgeElement second) {
		if (first == null) {
			return second;
		}
		if (second == null) {
			return first;
		}
		if (first.getId() > second.getId()) {
			return first;
		}
		return second;
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

	public static void createLinksForNonLinkedElementsForIssue(long issueId) {
		for (PartOfJiraIssueTextInDatabase databaseEntry : ACTIVE_OBJECTS.find(PartOfJiraIssueTextInDatabase.class,
				Query.select().where("JIRA_ISSUE_ID = ?", issueId))) {
			DecisionKnowledgeElement parentElement = new DecisionKnowledgeElementImpl(databaseEntry.getJiraIssueId(),
					databaseEntry.getProjectKey(), "i");
			checkIfSentenceHasAValidLink(new PartOfJiraIssueTextImpl(databaseEntry), parentElement,
					LinkType.getLinkTypeForKnowledgeType(databaseEntry.getType()));
		}
	}

	public static boolean createLinksForNonLinkedElementsForProject(String projectKey) {
		if (projectKey == null || projectKey.equals("")) {
			return false;
		}
		for (PartOfJiraIssueTextInDatabase databaseEntry : ACTIVE_OBJECTS.find(PartOfJiraIssueTextInDatabase.class,
				Query.select().where("PROJECT_KEY = ?", projectKey))) {
			DecisionKnowledgeElement parentElement = new DecisionKnowledgeElementImpl(databaseEntry.getJiraIssueId(),
					projectKey, "i");

			checkIfSentenceHasAValidLink(new PartOfJiraIssueTextImpl(databaseEntry), parentElement,
					LinkType.getLinkTypeForKnowledgeType(databaseEntry.getType()));
		}
		return true;
	}

	public static boolean checkIfSentenceHasAValidLink(DecisionKnowledgeElement childElement,
			DecisionKnowledgeElement parentElement, LinkType linkType) {
		if (AbstractPersistenceManagerForSingleLocation.isElementLinked(childElement)) {
			return true;
		}
		String projectKey = parentElement.getProject().getProjectKey();
		Link link = Link.instantiateDirectedLink(parentElement, childElement, linkType);
		long linkId = KnowledgePersistenceManager.getOrCreate(projectKey).insertLink(link, null);
		return linkId > 0;
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
		if (sentence.getCommentId() == -1) {
			// Part of JIRA issue description
			return true;
		}
		Comment comment = ComponentAccessor.getCommentManager().getCommentById(sentence.getCommentId());
		if (comment == null) {
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

		createLinksForNonLinkedElementsForIssue(element.getJiraIssueId());

		return issue;
	}

	public static List<DecisionKnowledgeElement> updateComment(Comment comment) {
		String projectKey = comment.getIssue().getProjectObject().getKey();
		List<PartOfText> partsOfText = new TextSplitterImpl().getPartsOfText(comment.getBody(), projectKey);

		List<DecisionKnowledgeElement> knowledgeElementsInText = getElementsForComment(comment.getId());

		// @issue Currently elements are deleted and new ones are created afterwards.
		// How to enable a "real" update?
		// @decision Overwrite parts of JIRA issue text in AO database if they exist!
		// @con If a new knowledge element is inserted at the beginning of the text, the
		// links in the knowledge graph might be wrong.
		int numberOfTextPartsInComment = knowledgeElementsInText.size();

		JiraIssueTextPersistenceManager persistenceManager = KnowledgePersistenceManager.getOrCreate(projectKey)
				.getJiraIssueTextManager();

		// Update AO entries
		for (int i = 0; i < partsOfText.size(); i++) {
			PartOfJiraIssueText sentence = new PartOfJiraIssueTextImpl(partsOfText.get(i), comment);
			if (i < numberOfTextPartsInComment) {
				sentence.setId(knowledgeElementsInText.get(i).getId());
				updateInDatabase(sentence);
			} else {
				sentence = (PartOfJiraIssueText) persistenceManager.insertDecisionKnowledgeElement(sentence, null);
			}
			createSmartLinkForSentence(sentence);
			knowledgeElementsInText.set(i, sentence);
		}
		return knowledgeElementsInText;
	}

	public static List<DecisionKnowledgeElement> updateDescription(Issue jiraIssue) {
		String projectKey = jiraIssue.getProjectObject().getKey();
		List<PartOfText> partsOfText = new TextSplitterImpl().getPartsOfText(jiraIssue.getDescription(), projectKey);

		List<DecisionKnowledgeElement> parts = getElementsForDescription(jiraIssue.getId());
		int numberOfTextParts = parts.size();

		JiraIssueTextPersistenceManager persistenceManager = KnowledgePersistenceManager.getOrCreate(projectKey)
				.getJiraIssueTextManager();

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
			createSmartLinkForSentence(sentence);
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

	public static Issue getJiraIssue(long id) {
		Issue jiraIssue = null;
		IssueManager issueManager = ComponentAccessor.getIssueManager();

		for (PartOfJiraIssueTextInDatabase databaseEntry : ACTIVE_OBJECTS.find(PartOfJiraIssueTextInDatabase.class,
				Query.select().where("ID = ?", id))) {
			jiraIssue = issueManager.getIssueObject(databaseEntry.getJiraIssueId());
		}
		return jiraIssue;
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

}
