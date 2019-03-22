package de.uhd.ifi.se.decision.management.jira.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

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
import de.uhd.ifi.se.decision.management.jira.extraction.DecXtractEventListener;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.LinkType;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfText;
import de.uhd.ifi.se.decision.management.jira.model.text.impl.PartOfJiraIssueTextImpl;
import de.uhd.ifi.se.decision.management.jira.model.text.impl.TextSplitterImpl;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.PartOfJiraIssueTextInDatabase;
import de.uhd.ifi.se.decision.management.jira.view.macros.AbstractKnowledgeClassificationMacro;
import net.java.ao.Query;

/**
 * Extends the abstract class AbstractPersistenceManager. Uses JIRA issue
 * comments or the description to store decision knowledge.
 *
 * @see AbstractPersistenceManager
 */
public class JiraIssueCommentPersistenceManager extends AbstractPersistenceManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(JiraIssueCommentPersistenceManager.class);
	private static final ActiveObjects ACTIVE_OBJECTS = ComponentGetter.getActiveObjects();

	public JiraIssueCommentPersistenceManager(String projectKey) {
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
			GenericLinkManager.deleteLinksForElement(id, DocumentationLocation.JIRAISSUETEXT);
			isDeleted = PartOfJiraIssueTextInDatabase.deleteElement(databaseEntry);
		}
		return isDeleted;
	}

	public static boolean deleteAllSentencesOfComments(Comment comment) {
		if (comment == null) {
			LOGGER.error("Sentences in comment cannot be deleted since the comment is null.");
			return false;
		}
		return deleteAllSentences(comment.getIssue().getId(), comment.getId());
	}

	public static boolean deleteAllSentencesOfDescription(Issue jiraIssue) {
		if (jiraIssue == null) {
			LOGGER.error("Sentences in comment cannot be deleted since the JIRA issue is null.");
			return false;
		}
		return deleteAllSentences(jiraIssue.getId(), 0);
	}
	
	private static boolean deleteAllSentences(long jiraIssueId, long commentId) {
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

	public DecisionKnowledgeElement getDecisionKnowledgeElement(PartOfJiraIssueText sentence) {
		if (sentence == null) {
			return null;
		}
		if (sentence.getId() > 0) {
			return this.getDecisionKnowledgeElement(sentence.getId());
		}

		PartOfJiraIssueText sentenceInDatabase = null;
		for (PartOfJiraIssueTextInDatabase databaseEntry : ACTIVE_OBJECTS.find(PartOfJiraIssueTextInDatabase.class,
				Query.select().where(
						"PROJECT_KEY = ? AND COMMENT_ID = ? AND END_SUBSTRING_COUNT = ? AND START_SUBSTRING_COUNT = ?",
						sentence.getProject().getProjectKey(), sentence.getCommentId(), sentence.getEndSubstringCount(),
						sentence.getStartSubstringCount()))) {
			sentenceInDatabase = new PartOfJiraIssueTextImpl(databaseEntry);
		}
		return sentenceInDatabase;
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
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DecisionKnowledgeElement> getElementsLinkedWithOutwardLinks(DecisionKnowledgeElement element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Link> getInwardLinks(DecisionKnowledgeElement element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Link> getOutwardLinks(DecisionKnowledgeElement element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DecisionKnowledgeElement insertDecisionKnowledgeElement(DecisionKnowledgeElement element,
			ApplicationUser user, DecisionKnowledgeElement parentElement) {
		if (element == null || user == null || parentElement == null) {
			return null;
		}
		long issueId;
		if (parentElement.getDocumentationLocation() == DocumentationLocation.JIRAISSUETEXT) {
			PartOfJiraIssueText sentence = (PartOfJiraIssueText) this
					.getDecisionKnowledgeElement(parentElement.getId());
			issueId = sentence.getJiraIssueId();
		} else {
			issueId = parentElement.getId();
		}
		MutableIssue issue = ComponentAccessor.getIssueManager().getIssueObject(issueId);
		if (issue == null) {
			return null;
		}
		String tag = AbstractKnowledgeClassificationMacro.getTag(element.getTypeAsString());
		String text = tag + element.getSummary() + "\n" + element.getDescription() + tag;
		Comment comment = ComponentAccessor.getCommentManager().create(issue, user, text, false);
		List<PartOfJiraIssueText> sentences = JiraIssueCommentPersistenceManager.getPartsOfComment(comment);
		for (PartOfJiraIssueText sentence : sentences) {
			GenericLinkManager.deleteLinksForElement(sentence.getId(), DocumentationLocation.JIRAISSUETEXT);
		}
		return sentences.get(0);
	}

	public static long insertDecisionKnowledgeElement(PartOfJiraIssueText sentence, ApplicationUser user) {
		DecisionKnowledgeElement existingElement = new JiraIssueCommentPersistenceManager("")
				.getDecisionKnowledgeElement(sentence);
		if (existingElement != null) {
			JiraIssueCommentPersistenceManager.checkIfSentenceHasAValidLink(existingElement.getId(),
					sentence.getJiraIssueId(), LinkType.getLinkTypeForKnowledgeType(existingElement.getType()));
			return existingElement.getId();
		}

		PartOfJiraIssueTextInDatabase databaseEntry = ACTIVE_OBJECTS.create(PartOfJiraIssueTextInDatabase.class);
		setParameters(sentence, databaseEntry);
		databaseEntry.save();
		LOGGER.debug("\naddNewSentenceintoAo:\nInsert Sentence " + databaseEntry.getId()
				+ " into database from comment " + databaseEntry.getCommentId());
		return databaseEntry.getId();
	}

	private static void setParameters(PartOfJiraIssueText element, PartOfJiraIssueTextInDatabase databaseEntry) {
		databaseEntry.setProjectKey(element.getProject().getProjectKey());
		databaseEntry.setCommentId(element.getCommentId());
		databaseEntry.setType(element.getTypeAsString());
		databaseEntry.setRelevant(element.isRelevant());
		databaseEntry.setValidated(element.isValidated());
		databaseEntry.setStartSubstringCount(element.getStartSubstringCount());
		databaseEntry.setEndSubstringCount(element.getEndSubstringCount());
		databaseEntry.setJiraIssueId(element.getJiraIssueId());
	}

	@Override
	public boolean updateDecisionKnowledgeElement(DecisionKnowledgeElement element, ApplicationUser user) {
		if (element == null) {
			return false;
		}
		PartOfJiraIssueText sentence = new PartOfJiraIssueTextImpl();
		sentence.setId(element.getId());
		sentence.setType(element.getType());
		sentence.setSummary(element.getSummary());
		sentence.setDescription(element.getDescription());
		sentence.setProject(element.getProject());
		sentence.setValidated(true);

		return this.updateDecisionKnowledgeElement(sentence, user);
	}

	public boolean updateDecisionKnowledgeElement(PartOfJiraIssueText element, ApplicationUser user) {
		if (element == null) {
			return false;
		}
		// Get corresponding element from database
		PartOfJiraIssueText sentence = (PartOfJiraIssueText) this.getDecisionKnowledgeElement(element.getId());
		if (sentence == null) {
			return false;
		}

		// only knowledge type changed
		if (element.getSummary() == null) {
			element.setDescription(sentence.getDescription());
		}

		String tag = AbstractKnowledgeClassificationMacro.getTag(element.getType());
		String changedPartOfComment = tag + element.getDescription() + tag;

		String text = "";
		MutableComment mutableComment = sentence.getComment();
		if (mutableComment == null) {
			text = sentence.getJiraIssueDescription();
		} else {
			text = mutableComment.getBody();
		}

		String firstPartOfComment = text.substring(0, sentence.getStartSubstringCount());
		String lastPartOfComment = text.substring(sentence.getEndSubstringCount());

		DecXtractEventListener.editCommentLock = true;
		if (mutableComment == null) {
			new JiraIssuePersistenceManager(projectKey).updateDecisionKnowledgeElement(sentence.getJiraIssue(), user);
		} else {
			mutableComment.setBody(firstPartOfComment + changedPartOfComment + lastPartOfComment);
			ComponentAccessor.getCommentManager().update(mutableComment, true);
		}
		DecXtractEventListener.editCommentLock = false;

		int lengthDifference = changedPartOfComment.length() - sentence.getLength();
		updateSentenceLengthForOtherSentencesInSameComment(sentence, lengthDifference);

		sentence.setEndSubstringCount(sentence.getStartSubstringCount() + changedPartOfComment.length());
		sentence.setType(element.getType());
		sentence.setValidated(element.isValidated());
		sentence.setRelevant(element.getType() != KnowledgeType.OTHER);

		boolean isUpdated = updateInDatabase(sentence);
		return isUpdated;
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
			if (otherSentenceInComment.getStartSubstringCount() > sentence.getStartSubstringCount()
					&& otherSentenceInComment.getId() != sentence.getId()) {
				otherSentenceInComment
						.setStartSubstringCount(otherSentenceInComment.getStartSubstringCount() + lengthDifference);
				otherSentenceInComment
						.setEndSubstringCount(otherSentenceInComment.getEndSubstringCount() + lengthDifference);
				otherSentenceInComment.save();
			}
		}
	}

	public static DecisionKnowledgeElement compareForLaterElement(DecisionKnowledgeElement first,
			DecisionKnowledgeElement second) {
		if (first == null) {
			return second;
		} else if (second == null) {
			return first;
		} else if (first.getId() > second.getId()) {
			return first;
		} else {
			return second;
		}
	}

	public static boolean checkLastElementAndCreateLink(DecisionKnowledgeElement lastElement,
			PartOfJiraIssueText sentence) {
		if (lastElement == null) {
			return false;
		}
		Link link = Link.instantiateDirectedLink(lastElement, sentence);
		GenericLinkManager.insertLink(link, null);
		return true;
	}

	public static void createSmartLinkForSentence(PartOfJiraIssueText sentence) {
		if (sentence == null || AbstractPersistenceManager.isElementLinked(sentence)) {
			return;
		}
		boolean smartLinkCreated = false;
		KnowledgeType knowledgeType = sentence.getType();
		if (knowledgeType == KnowledgeType.ARGUMENT || knowledgeType == KnowledgeType.PRO
				|| knowledgeType == KnowledgeType.CON) {
			DecisionKnowledgeElement lastElement = JiraIssueCommentPersistenceManager.compareForLaterElement(
					searchForLast(sentence, KnowledgeType.ALTERNATIVE),
					searchForLast(sentence, KnowledgeType.DECISION));
			smartLinkCreated = JiraIssueCommentPersistenceManager.checkLastElementAndCreateLink(lastElement, sentence);
		} else if (knowledgeType == KnowledgeType.DECISION || knowledgeType == KnowledgeType.ALTERNATIVE) {
			DecisionKnowledgeElement lastElement = searchForLast(sentence, KnowledgeType.ISSUE);
			smartLinkCreated = JiraIssueCommentPersistenceManager.checkLastElementAndCreateLink(lastElement, sentence);
		}
		if (!smartLinkCreated) {
			checkIfSentenceHasAValidLink(sentence.getId(), sentence.getJiraIssueId(),
					LinkType.getLinkTypeForKnowledgeType(sentence.getTypeAsString()));
		}
	}

	public static void createLinksForNonLinkedElementsForIssue(long issueId) {
		for (PartOfJiraIssueTextInDatabase databaseEntry : ACTIVE_OBJECTS.find(PartOfJiraIssueTextInDatabase.class,
				Query.select().where("JIRA_ISSUE_ID = ?", issueId))) {
			checkIfSentenceHasAValidLink(databaseEntry.getId(), databaseEntry.getJiraIssueId(),
					LinkType.getLinkTypeForKnowledgeType(databaseEntry.getType()));
		}
	}

	public static void createLinksForNonLinkedElementsForProject(String projectKey) {
		if (projectKey == null || projectKey.equals("")) {
			return;
		}
		for (PartOfJiraIssueTextInDatabase databaseEntry : ACTIVE_OBJECTS.find(PartOfJiraIssueTextInDatabase.class,
				Query.select().where("PROJECT_KEY = ?", projectKey))) {
			checkIfSentenceHasAValidLink(databaseEntry.getId(), databaseEntry.getJiraIssueId(),
					LinkType.getLinkTypeForKnowledgeType(databaseEntry.getType()));
		}
	}

	public static void checkIfSentenceHasAValidLink(long sentenceId, long issueId, LinkType linkType) {
		if (!AbstractPersistenceManager.isElementLinked(sentenceId, DocumentationLocation.JIRAISSUETEXT)) {
			DecisionKnowledgeElement parentElement = new DecisionKnowledgeElementImpl();
			parentElement.setId(issueId);
			parentElement.setDocumentationLocation("i");

			DecisionKnowledgeElement childElement = new DecisionKnowledgeElementImpl();
			childElement.setId(sentenceId);
			childElement.setDocumentationLocation("s");

			Link link = Link.instantiateDirectedLink(parentElement, childElement, linkType);
			GenericLinkManager.insertLink(link, null);
		}
	}

	public static void cleanSentenceDatabase(String projectKey) {
		for (PartOfJiraIssueTextInDatabase databaseEntry : ACTIVE_OBJECTS.find(PartOfJiraIssueTextInDatabase.class,
				Query.select().where("PROJECT_KEY = ?", projectKey))) {
			if (!isExistent(databaseEntry)) {
				PartOfJiraIssueTextInDatabase.deleteElement(databaseEntry);
				GenericLinkManager.deleteLinksForElement(databaseEntry.getId(), DocumentationLocation.JIRAISSUETEXT);
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
		return !(sentence.getEndSubstringCount() == 0 && sentence.getStartSubstringCount() == 0);
	}

	/**
	 * Migration function on button "Validate Sentence Database" Adds Link types to
	 * "empty" links. Can be deleted in a future release
	 * 
	 */
	public static void migrateArgumentTypesInLinks(String projectKey) {
		if (projectKey == null || projectKey.equals("")) {
			return;
		}
		PartOfJiraIssueTextInDatabase[] sentencesInProject = ACTIVE_OBJECTS.find(PartOfJiraIssueTextInDatabase.class,
				Query.select().where("PROJECT_KEY = ?", projectKey));
		for (PartOfJiraIssueTextInDatabase databaseEntry : sentencesInProject) {
			if (databaseEntry.getType().length() == 3) {// Equals Argument
				List<Link> links = GenericLinkManager.getLinksForElement(databaseEntry.getId(),
						DocumentationLocation.JIRAISSUETEXT);
				for (Link link : links) {
					if (link.getType().equalsIgnoreCase("contain")) {
						GenericLinkManager.deleteLink(link);
						link.setType(LinkType.getLinkTypeForKnowledgeType(databaseEntry.getType()).toString());
						GenericLinkManager.insertLink(link, null);
					}
				}
			}
		}
	}

	public Issue createJIRAIssueFromSentenceObject(long aoId, ApplicationUser user) {
		if (aoId <= 0 || user == null) {
			LOGGER.error("Parameter are Invalid");
			return null;
		}

		PartOfJiraIssueText element = (PartOfJiraIssueText) this.getDecisionKnowledgeElement(aoId);

		JiraIssuePersistenceManager persistenceManager = new JiraIssuePersistenceManager(this.projectKey);
		DecisionKnowledgeElement decElement = persistenceManager.insertDecisionKnowledgeElement(element, user);

		MutableIssue issue = ComponentAccessor.getIssueService().getIssue(user, decElement.getId()).getIssue();

		IssueLinkManager issueLinkManager = ComponentAccessor.getIssueLinkManager();
		long linkTypeId = JiraIssuePersistenceManager.getLinkTypeId("contain");

		try {
			issueLinkManager.createIssueLink(element.getJiraIssueId(), issue.getId(), linkTypeId, (long) 0, user);
		} catch (CreateException e) {
			return null;
		}

		// delete sentence in comment
		int length = JiraIssueCommentPersistenceManager.removeSentenceFromComment(element) * -1; // -1 because we
																									// decrease the
																									// total number of
																									// letters
		updateSentenceLengthForOtherSentencesInSameComment(element, length);

		// delete ao sentence entry
		new JiraIssueCommentPersistenceManager("").deleteDecisionKnowledgeElement(aoId, null);

		createLinksForNonLinkedElementsForIssue(element.getJiraIssueId());

		return issue;
	}

	/**
	 * Split a text into parts (substrings).
	 * 
	 * @see PartOfText
	 * @param comment
	 *            JIRA issue comment.
	 * @return list of sentence objects.
	 */
	public static List<PartOfJiraIssueText> getPartsOfComment(Comment comment) {
		String projectKey = comment.getIssue().getProjectObject().getKey();
		List<PartOfText> partsOfText = new TextSplitterImpl().getPartsOfText(comment.getBody(), projectKey);

		List<PartOfJiraIssueText> parts = new ArrayList<PartOfJiraIssueText>();

		// Create AO entries
		for (PartOfText partOfText : partsOfText) {
			PartOfJiraIssueText sentence = new PartOfJiraIssueTextImpl(partOfText, comment);
			long sentenceId = insertDecisionKnowledgeElement(sentence, null);
			sentence = (PartOfJiraIssueText) new JiraIssueCommentPersistenceManager("")
					.getDecisionKnowledgeElement(sentenceId);
			createSmartLinkForSentence(sentence);
			parts.add(sentence);
		}
		return parts;
	}

	public static List<PartOfJiraIssueText> getPartsOfDescription(Issue jiraIssue) {
		String projectKey = jiraIssue.getProjectObject().getKey();
		List<PartOfText> partsOfText = new TextSplitterImpl().getPartsOfText(jiraIssue.getDescription(), projectKey);

		List<PartOfJiraIssueText> parts = new ArrayList<PartOfJiraIssueText>();

		// Create AO entries
		for (PartOfText partOfText : partsOfText) {
			PartOfJiraIssueText sentence = new PartOfJiraIssueTextImpl(partOfText, jiraIssue);
			long sentenceId = insertDecisionKnowledgeElement(sentence, null);
			sentence = (PartOfJiraIssueText) new JiraIssueCommentPersistenceManager("")
					.getDecisionKnowledgeElement(sentenceId);
			createSmartLinkForSentence(sentence);
			parts.add(sentence);
		}
		return parts;
	}

	private static int removeSentenceFromComment(PartOfJiraIssueText element) {
		MutableComment mutableComment = element.getComment();
		String newBody = mutableComment.getBody();
		newBody = newBody.substring(0, element.getStartSubstringCount())
				+ newBody.substring(element.getEndSubstringCount());

		DecXtractEventListener.editCommentLock = true;
		mutableComment.setBody(newBody);
		ComponentAccessor.getCommentManager().update(mutableComment, true);
		DecXtractEventListener.editCommentLock = false;
		return element.getEndSubstringCount() - element.getStartSubstringCount();
	}

	public static String getJiraIssueKey(long id) {
		String jiraIssueKey = null;
		IssueManager issueManager = ComponentAccessor.getIssueManager();

		for (PartOfJiraIssueTextInDatabase databaseEntry : ACTIVE_OBJECTS.find(PartOfJiraIssueTextInDatabase.class,
				Query.select().where("ID = ?", id))) {
			jiraIssueKey = issueManager.getIssueObject(databaseEntry.getJiraIssueId()).getKey();
		}
		return jiraIssueKey;
	}

}
