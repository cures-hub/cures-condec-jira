package de.uhd.ifi.se.decision.management.jira.extraction.persistence;

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
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.comments.CommentManager;
import com.atlassian.jira.issue.comments.MutableComment;
import com.atlassian.jira.issue.link.IssueLinkManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.sal.api.transaction.TransactionCallback;
import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.extraction.DecXtractEventListener;
import de.uhd.ifi.se.decision.management.jira.extraction.model.Comment;
import de.uhd.ifi.se.decision.management.jira.extraction.model.Sentence;
import de.uhd.ifi.se.decision.management.jira.extraction.model.impl.CommentImpl;
import de.uhd.ifi.se.decision.management.jira.extraction.model.impl.SentenceImpl;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.LinkImpl;
import de.uhd.ifi.se.decision.management.jira.persistence.GenericLinkManager;
import de.uhd.ifi.se.decision.management.jira.persistence.JiraIssuePersistenceManager;
import net.java.ao.Query;

public class ActiveObjectsManager {

	public static ActiveObjects ActiveObjects;

	private static final Logger LOGGER = LoggerFactory.getLogger(ActiveObjectsManager.class);

	public static void init() {
		if (ActiveObjects == null) {
			ActiveObjects = ComponentGetter.getActiveObjects();
		}
	}

	public static long addNewSentenceintoAo(Comment comment, long issueId, int index) {
		return addNewSentenceintoAo(comment.getJiraCommentId(), comment.getEndSubstringCount().get(index),
				comment.getStartSubstringCount().get(index), comment.getAuthorId(), issueId, comment.getProjectKey());
	}

	public static long addNewSentenceintoAo(long commentId, int endSubStringCount, int startSubstringCount, long userId,
			long issueId, String projectKey) {
		init();

		DecisionKnowledgeInCommentEntity existingElement = getElementFromAO(commentId, endSubStringCount,
				startSubstringCount, userId, projectKey);
		if (existingElement != null) {
			checkIfSentenceHasAValidLink(existingElement.getId(), issueId);
			return existingElement.getId();
		}

		DecisionKnowledgeInCommentEntity newElement = ActiveObjects
				.executeInTransaction(new TransactionCallback<DecisionKnowledgeInCommentEntity>() {
					@Override
					public DecisionKnowledgeInCommentEntity doInTransaction() {
						DecisionKnowledgeInCommentEntity todo = ActiveObjects
								.create(DecisionKnowledgeInCommentEntity.class); // (2)
						todo.setCommentId(commentId);
						todo.setEndSubstringCount(endSubStringCount);
						todo.setStartSubstringCount(startSubstringCount);
						todo.setUserId(userId);
						todo.setTagged(false);
						todo.setRelevant(false);
						todo.setTaggedFineGrained(false);
						todo.setTaggedManually(false);
						todo.setProjectKey(projectKey);
						todo.setIssueId(issueId);
						todo.setKnowledgeTypeString("");
						todo.setArgument("");
						todo.save();
						LOGGER.debug("\naddNewSentenceintoAo:\nInsert Sentence " + todo.getId()
								+ " into AO from comment " + todo.getCommentId());
						return todo;
					}
				});
		return newElement.getId();
	}

	private static void checkIfSentenceHasAValidLink(long sentenceId, long issueId) {
		if (!isSentenceLinked(sentenceId)) {
			Link link = new LinkImpl("i" + issueId, "s" + sentenceId);
			GenericLinkManager.insertLinkWithoutTransaction(link);
		}
	}

	private static boolean isSentenceLinked(long sentenceId) {
		List<Link> links = GenericLinkManager.getLinksForElement("s" + sentenceId);
		if (links == null || links.size() == 0) {
			return false;
		} else {
			return true;
		}
	}

	public static void createSmartLinkForSentence(Sentence sentence) {
		if (sentence == null || isSentenceLinked(sentence.getId())) {
			return;
		}
		boolean smartLinkCreated = false;
		if (sentence.getType().equals(KnowledgeType.ARGUMENT)) {
			DecisionKnowledgeElement lastElement = compareForLaterElement(
					searchForLast(sentence, KnowledgeType.ALTERNATIVE),
					searchForLast(sentence, KnowledgeType.DECISION));
			smartLinkCreated = checkLastElementAndCreateLink(lastElement, sentence);
		} else if (sentence.getType().equals(KnowledgeType.DECISION)
				|| sentence.getType().equals(KnowledgeType.ALTERNATIVE)) {
			DecisionKnowledgeElement lastElement = searchForLast(sentence, KnowledgeType.ISSUE);
			smartLinkCreated = checkLastElementAndCreateLink(lastElement, sentence);
		}
		if (!smartLinkCreated) {
			checkIfSentenceHasAValidLink(sentence.getId(), sentence.getIssueId());
		}
	}

	private static boolean checkLastElementAndCreateLink(DecisionKnowledgeElement lastElement, Sentence sentence) {
		if (lastElement != null) {
			GenericLinkManager.insertLink(new LinkImpl(lastElement, sentence), null);
			return true;
		}
		return false;
	}

	private static DecisionKnowledgeElement compareForLaterElement(DecisionKnowledgeElement first,
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

	private static DecisionKnowledgeElement searchForLast(Sentence sentence, KnowledgeType typeToSearch) {
		DecisionKnowledgeInCommentEntity[] sententenceList = ActiveObjects.find(DecisionKnowledgeInCommentEntity.class,
				Query.select().where("ISSUE_ID = ?", sentence.getIssueId()).order("ID DESC"));

		for (DecisionKnowledgeInCommentEntity aoElement : sententenceList) {
			if (aoElement.getKnowledgeTypeString().equals(typeToSearch.toString())) {
				return new SentenceImpl(aoElement);
			}
		}
		return null;
	}

	public static void updateSentenceElement(Sentence sentence) {
		init();
		ActiveObjects.executeInTransaction(new TransactionCallback<DecisionKnowledgeInCommentEntity>() {
			@Override
			public DecisionKnowledgeInCommentEntity doInTransaction() {
				for (DecisionKnowledgeInCommentEntity databaseEntry : ActiveObjects.find(
						DecisionKnowledgeInCommentEntity.class,
						Query.select().where("PROJECT_KEY = ?", sentence.getProjectKey()))) {
					if (databaseEntry.getId() == sentence.getId()) {
						databaseEntry.setArgument(sentence.getArgument());
						databaseEntry.setEndSubstringCount(sentence.getEndSubstringCount());
						databaseEntry.setRelevant(sentence.isRelevant());
						databaseEntry.setTagged(sentence.isTagged());
						databaseEntry.setTaggedFineGrained(sentence.isTaggedFineGrained());
						databaseEntry.setTaggedManually(sentence.isTaggedManually());
						databaseEntry.setKnowledgeTypeString(sentence.getKnowledgeTypeString());
						databaseEntry.setStartSubstringCount(sentence.getStartSubstringCount());
						databaseEntry.save();
					}
				}
				return null;
			}
		});
	}

	public static DecisionKnowledgeInCommentEntity getElementFromAO(long commentId, int endSubtringCount,
			int startSubstringCount, long userId, String projectKey) {
		init();
		DecisionKnowledgeInCommentEntity element = ActiveObjects
				.executeInTransaction(new TransactionCallback<DecisionKnowledgeInCommentEntity>() {
					@Override
					public DecisionKnowledgeInCommentEntity doInTransaction() {
						for (DecisionKnowledgeInCommentEntity databaseEntry : ActiveObjects.find(
								DecisionKnowledgeInCommentEntity.class,
								Query.select().where(
										"PROJECT_KEY = ? AND COMMENT_ID = ? AND END_SUBSTRING_COUNT = ? AND START_SUBSTRING_COUNT = ?",
										projectKey, commentId, endSubtringCount, startSubstringCount))) {
							return databaseEntry;
						}
						return null;
					}
				});
		return element;

	}

	public static DecisionKnowledgeElement getElementFromAO(long aoId) {
		init();
		for (DecisionKnowledgeInCommentEntity databaseEntry : ActiveObjects.find(DecisionKnowledgeInCommentEntity.class,
				Query.select().where("ID = ?", aoId))) {
			return new SentenceImpl(databaseEntry);

		}
		return null;
	}

	public static void setSentenceKnowledgeType(Sentence sentence) {
		init();
		ActiveObjects.executeInTransaction(new TransactionCallback<DecisionKnowledgeInCommentEntity>() {
			@Override
			public DecisionKnowledgeInCommentEntity doInTransaction() {

				for (DecisionKnowledgeInCommentEntity databaseEntry : ActiveObjects
						.find(DecisionKnowledgeInCommentEntity.class)) {
					if (databaseEntry.getId() == sentence.getId()) {
						databaseEntry.setKnowledgeTypeString(sentence.getKnowledgeTypeString());
						int additionalLength = addTagsToCommentWhenAutoClassified(databaseEntry);
						databaseEntry.setTaggedFineGrained(true);
						databaseEntry.setArgument(sentence.getArgument());
						databaseEntry.setEndSubstringCount(databaseEntry.getEndSubstringCount() + additionalLength);
						updateSentenceLengthForOtherSentencesInSameComment(sentence.getCommentId(),
								sentence.getStartSubstringCount(), additionalLength, sentence.getId());
						databaseEntry.save();
						return databaseEntry;
					}
				}
				return null;
			}
		});

	}

	protected static int addTagsToCommentWhenAutoClassified(DecisionKnowledgeInCommentEntity sentence) {
		CommentManager cm = ComponentAccessor.getCommentManager();
		MutableComment mc = (MutableComment) cm.getMutableComment(sentence.getCommentId());
		String newBody = mc.getBody().substring(sentence.getStartSubstringCount(), sentence.getEndSubstringCount());

		newBody = "{" + sentence.getKnowledgeTypeString() + "}" + newBody + "{" + sentence.getKnowledgeTypeString()
				+ "}";
		int lengthDiff = (sentence.getKnowledgeTypeString().length() + 2) * 2;

		DecXtractEventListener.editCommentLock = true;
		mc.setBody(mc.getBody().substring(0, sentence.getStartSubstringCount()) + newBody
				+ mc.getBody().substring(sentence.getEndSubstringCount()));
		cm.update(mc, true);
		DecXtractEventListener.editCommentLock = false;
		return lengthDiff;
	}

	public static Boolean updateKnowledgeTypeOfSentence(long id, KnowledgeType knowledgeType, String argument) {
		init();
		return ActiveObjects.executeInTransaction(new TransactionCallback<Boolean>() {
			@Override
			public Boolean doInTransaction() {
				for (DecisionKnowledgeInCommentEntity sentenceEntity : ActiveObjects
						.find(DecisionKnowledgeInCommentEntity.class)) {
					if (sentenceEntity.getId() == id) {
						// Knowledgetype is an Argument

						String oldKnowledgeType = sentenceEntity.getKnowledgeTypeString();
						if (knowledgeType.equals(KnowledgeType.OTHER) || knowledgeType.equals(KnowledgeType.ARGUMENT)) {
							sentenceEntity.setKnowledgeTypeString(argument);
							sentenceEntity.setArgument(argument);
						} else {
							sentenceEntity.setKnowledgeTypeString(knowledgeType.toString());
						}
						sentenceEntity.setRelevant(true);
						sentenceEntity.setTaggedFineGrained(true);
						if (!sentenceEntity.getKnowledgeTypeString().equals("Pro")
								&& !sentenceEntity.getKnowledgeTypeString().equals("Con")) {
							sentenceEntity.setArgument("");
							if (knowledgeType.equals(KnowledgeType.OTHER)) {
								sentenceEntity.setRelevant(false);
							}
						}
						if (sentenceEntity.isTaggedManually()) {
							int oldTextLength = getTextLengthOfAoElement(sentenceEntity);
							int newTextLength = updateTagsInComment(sentenceEntity, knowledgeType, argument,
									oldKnowledgeType);
							sentenceEntity
									.setEndSubstringCount(sentenceEntity.getStartSubstringCount() + newTextLength);
							ActiveObjectsManager.updateSentenceLengthForOtherSentencesInSameComment(
									sentenceEntity.getCommentId(), sentenceEntity.getStartSubstringCount(),
									newTextLength - oldTextLength, sentenceEntity.getId());
							sentenceEntity.save();
						} else {
							sentenceEntity.setTaggedManually(true);
							int newLength = addTagsToCommentWhenAutoClassified(sentenceEntity);
							sentenceEntity.setEndSubstringCount(sentenceEntity.getEndSubstringCount() + newLength);
							updateSentenceLengthForOtherSentencesInSameComment(sentenceEntity.getCommentId(),
									sentenceEntity.getStartSubstringCount(), newLength, sentenceEntity.getId());
							sentenceEntity.save();
						}
						sentenceEntity.save();
						return true;
					}
				}
				return false;
			}
		});

	}

	private static int getTextLengthOfAoElement(DecisionKnowledgeInCommentEntity sentence) {
		return sentence.getEndSubstringCount() - sentence.getStartSubstringCount();
	}

	private static int updateTagsInComment(DecisionKnowledgeInCommentEntity sentenceEntity, KnowledgeType knowledgeType,
			String argument, String oldKnowledgeType) {
		CommentManager cm = ComponentAccessor.getCommentManager();
		MutableComment mc = (MutableComment) cm.getMutableComment(sentenceEntity.getCommentId());
		String oldBody = mc.getBody();

		String newBody = oldBody.substring(sentenceEntity.getStartSubstringCount(),
				sentenceEntity.getEndSubstringCount());
		if (knowledgeType.toString().equalsIgnoreCase("other")
				|| knowledgeType.toString().equalsIgnoreCase("argument")) {
			newBody = newBody.replaceAll("(?i)" + oldKnowledgeType + "}", argument + "}");
		} else {
			newBody = newBody.replaceAll("(?i)" + oldKnowledgeType + "}", knowledgeType.toString() + "}");
		}
		// build body with first text and changed text
		int newEndSubstringCount = newBody.length();
		newBody = oldBody.substring(0, sentenceEntity.getStartSubstringCount()) + newBody;
		// If Changed sentence is in the middle of a sentence
		if (oldBody.length() > sentenceEntity.getEndSubstringCount()) {
			newBody = newBody + oldBody.substring(sentenceEntity.getEndSubstringCount());
		}

		mc.setBody(newBody);
		cm.update(mc, true);
		return newEndSubstringCount;
	}

	public static boolean setIsRelevantIntoAo(long activeObjectId, boolean isRelevant) {
		init();
		DecisionKnowledgeInCommentEntity databaseEntry = ActiveObjects
				.executeInTransaction(new TransactionCallback<DecisionKnowledgeInCommentEntity>() {
					@Override
					public DecisionKnowledgeInCommentEntity doInTransaction() {
						for (DecisionKnowledgeInCommentEntity databaseEntry : ActiveObjects
								.find(DecisionKnowledgeInCommentEntity.class)) {
							if (databaseEntry.getId() == activeObjectId) {
								databaseEntry.setRelevant(isRelevant);
								// If relevant is true or false, it's tagged, so set it on true
								databaseEntry.setTagged(true);
								databaseEntry.save();
								return databaseEntry;
							}
						}
						return null;
					}
				});
		if (databaseEntry == null) {
			return false;
		}
		return true;
	}

	public static boolean setSentenceIrrelevant(long id, boolean isTaggedManually) {
		init();
		return ActiveObjects.executeInTransaction(new TransactionCallback<Boolean>() {
			@Override
			public Boolean doInTransaction() {
				for (DecisionKnowledgeInCommentEntity sentenceEntity : ActiveObjects
						.find(DecisionKnowledgeInCommentEntity.class)) {
					if (sentenceEntity.getId() == id) {
						ActiveObjectsManager.stripTagsOutOfComment(sentenceEntity);
						GenericLinkManager.deleteLinksForElementWithoutTransaction("s" + id);

						ActiveObjectsManager.createLinksForNonLinkedElementsForIssue(sentenceEntity.getIssueId());
						sentenceEntity.setRelevant(false);
						sentenceEntity.setTaggedManually(isTaggedManually);
						sentenceEntity.setKnowledgeTypeString(KnowledgeType.OTHER.toString());
						sentenceEntity.setArgument("");
						sentenceEntity.save();
						return true;
					}
				}
				return false;
			}
		});
	}

	protected static void stripTagsOutOfComment(DecisionKnowledgeInCommentEntity sentence) {
		if (sentence.getKnowledgeTypeString() == null || sentence.getKnowledgeTypeString().equalsIgnoreCase("Other")) {
			return;
		}
		CommentManager cm = ComponentAccessor.getCommentManager();
		MutableComment mc = (MutableComment) cm.getMutableComment(sentence.getCommentId());
		String newBody = mc.getBody().substring(sentence.getStartSubstringCount(), sentence.getEndSubstringCount());
		int oldlength = newBody.length();
		int oldEnd = sentence.getEndSubstringCount();
		newBody = newBody.replaceAll("\\{.*?\\}", "");

		sentence.setEndSubstringCount(
				sentence.getEndSubstringCount() - (2 * (sentence.getKnowledgeTypeString().length() + 2)));
		sentence.save();

		int lengthDiff = newBody.length() - oldlength;
		DecXtractEventListener.editCommentLock = true;
		String first = mc.getBody().substring(0, sentence.getStartSubstringCount());
		String second = newBody;
		String third = mc.getBody().substring(oldEnd);
		mc.setBody(first + second + third);
		cm.update(mc, true);
		DecXtractEventListener.editCommentLock = false;
		updateSentenceLengthForOtherSentencesInSameComment(sentence.getCommentId(), sentence.getStartSubstringCount(),
				lengthDiff, sentence.getId());
		;
	}

	public static boolean updateSentenceBodyWhenCommentChanged(long commentId, long aoId, String description) {
		init();
		return ActiveObjects.executeInTransaction(new TransactionCallback<Boolean>() {
			@Override
			public Boolean doInTransaction() {
				int lengthDifference = 0;
				int oldStart = 0;
				for (DecisionKnowledgeInCommentEntity sentenceEntity : ActiveObjects
						.find(DecisionKnowledgeInCommentEntity.class, "ID = ?", aoId)) {
					int oldLength = sentenceEntity.getEndSubstringCount() - sentenceEntity.getStartSubstringCount();
					lengthDifference = (oldLength - description.length()) * -1;
					sentenceEntity.setEndSubstringCount(sentenceEntity.getEndSubstringCount() + lengthDifference);
					sentenceEntity.save();
					oldStart = sentenceEntity.getStartSubstringCount();
				}
				updateSentenceLengthForOtherSentencesInSameComment(commentId, oldStart, lengthDifference, aoId);
				return true;
			}
		});
	}

	private static void updateSentenceLengthForOtherSentencesInSameComment(long commentId, int oldStart,
			int lengthDifference, long aoId) {
		for (DecisionKnowledgeInCommentEntity otherSentenceInComment : ActiveObjects
				.find(DecisionKnowledgeInCommentEntity.class, "COMMENT_ID = ?", commentId)) {
			if (otherSentenceInComment.getStartSubstringCount() > oldStart && otherSentenceInComment.getId() != aoId
					&& otherSentenceInComment.getCommentId() == commentId) {
				otherSentenceInComment
						.setStartSubstringCount(otherSentenceInComment.getStartSubstringCount() + lengthDifference);
				otherSentenceInComment
						.setEndSubstringCount(otherSentenceInComment.getEndSubstringCount() + lengthDifference);
				otherSentenceInComment.save();
			}
		}
	}

	public static List<DecisionKnowledgeElement> getElementsForIssue(long issueId, String projectKey) {
		init();
		List<DecisionKnowledgeElement> elements = new ArrayList<>();
		for (DecisionKnowledgeInCommentEntity databaseEntry : ActiveObjects.find(DecisionKnowledgeInCommentEntity.class,
				Query.select().where("PROJECT_KEY = ? AND ISSUE_ID = ?", projectKey, issueId))) {
			elements.add(new SentenceImpl(databaseEntry));
		}
		return elements;
	}

	/**
	 * Works more efficient than "getElementsForIssue" for Sentence ID searching in
	 * Macros
	 * 
	 * @param issueId
	 * @param projectKey
	 * @param type
	 * @return A list of all fitting Sentence objects
	 */
	public static List<DecisionKnowledgeElement> getElementsForIssueWithType(long issueId, String projectKey,
			String type) {
		init();
		List<DecisionKnowledgeElement> elements = new ArrayList<>();
		for (DecisionKnowledgeInCommentEntity databaseEntry : ActiveObjects.find(DecisionKnowledgeInCommentEntity.class,
				Query.select().where("PROJECT_KEY = ? AND ISSUE_ID = ? AND KNOWLEDGE_TYPE_STRING = ?", projectKey,
						issueId, type))) {
			elements.add(new SentenceImpl(databaseEntry));
		}
		return elements;
	}

	/**
	 * Deletes all sentences in ao tables for this project and all links to and from
	 * sentences. Currently not used. Useful for developing and system testing.
	 * 
	 * @param projectKey
	 *            the project to clear
	 */
	@Deprecated
	public static void clearSentenceDatabaseForProject(String projectKey) {
		init();
		ActiveObjects.executeInTransaction(new TransactionCallback<DecisionKnowledgeInCommentEntity>() {
			@Override
			public DecisionKnowledgeInCommentEntity doInTransaction() {
				for (DecisionKnowledgeInCommentEntity databaseEntry : ActiveObjects.find(
						DecisionKnowledgeInCommentEntity.class, Query.select().where("PROJECT_KEY = ?", projectKey))) {
					GenericLinkManager.deleteLinksForElement("s" + databaseEntry.getId());
					DecisionKnowledgeInCommentEntity.deleteElement(databaseEntry);
				}
				return null;
			}
		});
	}

	public static void cleanSentenceDatabaseForProject(String projectKey) {
		init();
		for (DecisionKnowledgeInCommentEntity databaseEntry : ActiveObjects.find(DecisionKnowledgeInCommentEntity.class,
				Query.select().where("PROJECT_KEY = ?", projectKey))) {
			Sentence sentence = null;
			boolean deleteFlag = false;
			try {
				sentence = new SentenceImpl(databaseEntry);
				ComponentAccessor.getCommentManager().getCommentById(sentence.getCommentId());
				if (sentence.getEndSubstringCount() == 0 && sentence.getStartSubstringCount() == 0) {
					deleteFlag = true;
				}
			} catch (Exception e) {
				deleteFlag = true;
			}
			if (deleteFlag) {
				DecisionKnowledgeInCommentEntity.deleteElement(databaseEntry);
				GenericLinkManager.deleteLinksForElementWithoutTransaction("s" + databaseEntry.getId());
				// GenericLinkManager.deleteLinksForElement("s" + databaseEntry.getId());
			}
		}

	}

	public static void setDefaultValuesToExistingElements() {
		init();
		ActiveObjects.executeInTransaction(new TransactionCallback<DecisionKnowledgeInCommentEntity>() {
			@Override
			public DecisionKnowledgeInCommentEntity doInTransaction() {
				for (DecisionKnowledgeInCommentEntity databaseEntry : ActiveObjects
						.find(DecisionKnowledgeInCommentEntity.class)) {
					if (databaseEntry.isRelevant() == null) {
						databaseEntry.setRelevant(false);
					}
					if (databaseEntry.isTagged() == null) {
						databaseEntry.setTagged(false);
					}
					if (databaseEntry.isTaggedFineGrained() == null) {
						databaseEntry.setTaggedFineGrained(false);
					}
					if (databaseEntry.getArgument() == null) {
						databaseEntry.setArgument("");
					}
					if (databaseEntry.getKnowledgeTypeString() == null) {
						databaseEntry.setKnowledgeTypeString("");
					}
					databaseEntry.save();
				}
				return null;
			}
		});
	}

	public static void createLinksForNonLinkedElementsForProject(String projectKey) {
		init();
		for (DecisionKnowledgeInCommentEntity databaseEntry : ActiveObjects.find(DecisionKnowledgeInCommentEntity.class,
				Query.select().where("PROJECT_KEY = ?", projectKey))) {
			checkIfSentenceHasAValidLink(databaseEntry.getId(), databaseEntry.getIssueId());
		}
	}

	public static void createLinksForNonLinkedElementsForIssue(long issueId) {
		init();
		for (DecisionKnowledgeInCommentEntity databaseEntry : ActiveObjects.find(DecisionKnowledgeInCommentEntity.class,
				Query.select().where("ISSUE_ID = ?", issueId))) {
			checkIfSentenceHasAValidLink(databaseEntry.getId(), databaseEntry.getIssueId());
		}
	}

	public static List<DecisionKnowledgeElement> getAllElementsFromAoByType(String projectKey,
			KnowledgeType rootElementType) {
		init();
		List<DecisionKnowledgeInCommentEntity> listOfDbEntries = new ArrayList<>();
		ActiveObjects.executeInTransaction(new TransactionCallback<DecisionKnowledgeInCommentEntity>() {
			@Override
			public DecisionKnowledgeInCommentEntity doInTransaction() {
				for (DecisionKnowledgeInCommentEntity databaseEntry : ActiveObjects.find(
						DecisionKnowledgeInCommentEntity.class, Query.select().where("PROJECT_KEY = ?", projectKey))) {
					if (databaseEntry.getKnowledgeTypeString() != null
							&& (databaseEntry.getKnowledgeTypeString().equals(rootElementType.toString())
									|| (databaseEntry.getKnowledgeTypeString().length() == 3 // its either Pro or con
											&& rootElementType.equals(KnowledgeType.ARGUMENT)))) {
						try {
							listOfDbEntries.add(databaseEntry);
						} catch (NullPointerException e) {
							continue;
						}
					}
				}
				return null;
			}
		});
		List<DecisionKnowledgeElement> listOfDKE = new ArrayList<>();
		for (DecisionKnowledgeInCommentEntity dke : listOfDbEntries) {
			listOfDKE.add(new SentenceImpl(dke));
		}
		return listOfDKE;
	}

	public static Issue createJIRAIssueFromSentenceObject(long aoId, ApplicationUser user) {

		Sentence element = (Sentence) ActiveObjectsManager.getElementFromAO(aoId);

		JiraIssuePersistenceManager s = new JiraIssuePersistenceManager(element.getProjectKey());
		DecisionKnowledgeElement decElement = s.insertDecisionKnowledgeElement(element, user);

		MutableIssue issue = ComponentAccessor.getIssueService().getIssue(user, decElement.getId()).getIssue();

		IssueLinkManager issueLinkManager = ComponentAccessor.getIssueLinkManager();
		long linkTypeId = JiraIssuePersistenceManager.getLinkTypeId("contain");

		try {
			issueLinkManager.createIssueLink(element.getIssueId(), issue.getId(), linkTypeId, (long) 0, user);
		} catch (CreateException e) {
			return null;
		}

		// delete sentence in comment
		int length = removeSentenceFromComment(element) * -1; // -1 because we decrease the total number of letters
		updateSentenceLengthForOtherSentencesInSameComment(element.getCommentId(), element.getStartSubstringCount(),
				length, aoId);

		// delete ao sentence entry
		deleteSentenceObject(aoId);

		ActiveObjectsManager.createLinksForNonLinkedElementsForIssue(element.getIssueId());

		return issue;
	}

	private static int removeSentenceFromComment(Sentence element) {
		CommentManager cm = ComponentAccessor.getCommentManager();
		MutableComment mc = (MutableComment) cm.getMutableComment(element.getCommentId());
		String newBody = mc.getBody();
		newBody = newBody.substring(0, element.getStartSubstringCount())
				+ newBody.substring(element.getEndSubstringCount());

		DecXtractEventListener.editCommentLock = true;
		mc.setBody(newBody);
		cm.update(mc, true);
		DecXtractEventListener.editCommentLock = false;
		return element.getEndSubstringCount() - element.getStartSubstringCount();
	}

	/**
	 * Proper way to delete sentences from ao. Also deletes their links
	 * 
	 * @param id
	 * @return
	 */
	public static boolean deleteSentenceObject(long id) {
		init();
		boolean isDeleted = false;
		for (DecisionKnowledgeInCommentEntity databaseEntry : ActiveObjects.find(DecisionKnowledgeInCommentEntity.class,
				Query.select().where("ID = ?", id))) {
			GenericLinkManager.deleteLinksForElement("s" + id);
			isDeleted = DecisionKnowledgeInCommentEntity.deleteElement(databaseEntry);
		}
		return isDeleted;
	}

	public static void deleteCommentsSentences(com.atlassian.jira.issue.comments.Comment comment) {
		init();
		DecisionKnowledgeInCommentEntity[] commentSentences = ActiveObjects.find(DecisionKnowledgeInCommentEntity.class,
				Query.select().where("ISSUE_ID = ? AND COMMENT_ID = ?", comment.getIssue().getId(), comment.getId()));
		for (DecisionKnowledgeInCommentEntity entity : commentSentences) {
			deleteSentenceObject(entity.getId());
		}
	}

	public static int countCommentsForIssue(long issueId) {
		init();
		DecisionKnowledgeInCommentEntity[] commentSentences = ActiveObjects.find(DecisionKnowledgeInCommentEntity.class,
				Query.select().where("ISSUE_ID = ?", issueId));
		Set<Long> treeSet = new TreeSet<>();

		for (DecisionKnowledgeInCommentEntity sentence : commentSentences) {
			treeSet.add(sentence.getCommentId());
		}

		return treeSet.size();
	}

	public static DecisionKnowledgeElement addNewCommentToJIRAIssue(DecisionKnowledgeElement decisionKnowledgeElement,
			String argument, ApplicationUser user) {
		long issueId = getIssueId(decisionKnowledgeElement);
		MutableIssue issue = ComponentAccessor.getIssueManager().getIssueObject(issueId);
		if (issue != null) {
			String macro = getMacro(decisionKnowledgeElement, argument);
			String text = macro + decisionKnowledgeElement.getSummary() + "\n"
					+ decisionKnowledgeElement.getDescription() + macro;
			com.atlassian.jira.issue.comments.Comment comment = ComponentAccessor.getCommentManager().create(issue,
					user, text, false);
			Comment com = new CommentImpl(comment, true);
			for (Sentence sentence : com.getSentences()) {
				GenericLinkManager.deleteLinksForElement("s" + sentence.getId());
				String parentDocLoc = DocumentationLocation.getIdentifier(decisionKnowledgeElement);
				Link link = new LinkImpl(parentDocLoc + decisionKnowledgeElement.getId(), "s" + sentence.getId());
				GenericLinkManager.insertLinkWithoutTransaction(link);
				checkIfSentenceHasAValidLink(sentence.getId(), decisionKnowledgeElement.getId());
			}
			return com.getSentences().get(0);
		} else {
			return null;
		}
	}

	private static long getIssueId(DecisionKnowledgeElement decisionKnowledgeElement) {
		long issueId = decisionKnowledgeElement.getId();
		if (decisionKnowledgeElement.getDocumentationLocation().equals(DocumentationLocation.JIRAISSUECOMMENT)) {
			Sentence element = (Sentence) ActiveObjectsManager.getElementFromAO(decisionKnowledgeElement.getId());
			issueId = element.getIssueId();
		}
		return issueId;
	}

	private static String getMacro(DecisionKnowledgeElement decisionKnowledgeElement, String argument) {
		String macro = "{" + decisionKnowledgeElement.getType().toString() + "}";
		if (argument != null && !argument.equals("")) {
			if (argument.equalsIgnoreCase("Pro-argument")) {
				macro = "{pro}";
			} else if (argument.equalsIgnoreCase("Con-argument")) {
				macro = "{con}";
			}
		}
		return macro;
	}

	public static long getIdOfSentenceForMacro(String body, Long issueId, String typeString, String projectKey) {
		init();
		List<DecisionKnowledgeElement> sentences = ActiveObjectsManager.getElementsForIssueWithType(issueId, projectKey,typeString);
		for(DecisionKnowledgeElement sentence: sentences) {
			if(sentence.getDescription().trim().equals(body.trim().replaceAll("<[^>]*>", ""))) {
				return sentence.getId();
			}
		}
		LOGGER.debug("Nothing found for: "+body.replace("<br/>", "").trim() );
		return 0;
	}

}
