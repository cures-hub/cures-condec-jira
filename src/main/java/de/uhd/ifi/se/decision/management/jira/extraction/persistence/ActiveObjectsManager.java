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
import de.uhd.ifi.se.decision.management.jira.extraction.model.impl.SentenceImpl;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.LinkType;
import de.uhd.ifi.se.decision.management.jira.persistence.GenericLinkManager;
import de.uhd.ifi.se.decision.management.jira.persistence.JiraIssueCommentPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.JiraIssuePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.DecisionKnowledgeInCommentEntity;
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
				comment.getStartSubstringCount().get(index), issueId, comment.getProjectKey());
	}

	public static long addNewSentenceintoAo(long commentId, int endSubStringCount, int startSubstringCount,
			long issueId, String projectKey) {
		init();

		DecisionKnowledgeInCommentEntity existingElement = getElementFromAO(commentId, endSubStringCount,
				startSubstringCount, projectKey);
		if (existingElement != null) {
			checkIfSentenceHasAValidLink(existingElement.getId(), issueId,
					LinkType.getLinkTypeForKnowledgeType(existingElement.getType()));
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
						todo.setTagged(false);
						todo.setRelevant(false);
						todo.setProjectKey(projectKey);
						todo.setIssueId(issueId);
						todo.setType("");
						todo.save();
						LOGGER.debug("\naddNewSentenceintoAo:\nInsert Sentence " + todo.getId()
								+ " into AO from comment " + todo.getCommentId());
						return todo;
					}
				});
		return newElement.getId();
	}

	private static void checkIfSentenceHasAValidLink(long sentenceId, long issueId, LinkType linkType) {
		if (!isSentenceLinked(sentenceId)) {
			DecisionKnowledgeElement parentElement = new DecisionKnowledgeElementImpl();
			parentElement.setId(issueId);
			parentElement.setDocumentationLocation("i");

			DecisionKnowledgeElement childElement = new DecisionKnowledgeElementImpl();
			childElement.setId(sentenceId);
			childElement.setDocumentationLocation("s");

			Link link = Link.instantiateDirectedLink(parentElement, childElement, linkType);
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
		KnowledgeType knowledgeType = sentence.getType();
		if (knowledgeType == KnowledgeType.ARGUMENT || knowledgeType == KnowledgeType.PRO
				|| knowledgeType == KnowledgeType.CON) {
			DecisionKnowledgeElement lastElement = compareForLaterElement(
					searchForLast(sentence, KnowledgeType.ALTERNATIVE),
					searchForLast(sentence, KnowledgeType.DECISION));
			smartLinkCreated = checkLastElementAndCreateLink(lastElement, sentence);
		} else if (knowledgeType == KnowledgeType.DECISION || knowledgeType == KnowledgeType.ALTERNATIVE) {
			DecisionKnowledgeElement lastElement = searchForLast(sentence, KnowledgeType.ISSUE);
			smartLinkCreated = checkLastElementAndCreateLink(lastElement, sentence);
		}
		if (!smartLinkCreated) {
			checkIfSentenceHasAValidLink(sentence.getId(), sentence.getIssueId(),
					LinkType.getLinkTypeForKnowledgeType(sentence.getTypeAsString()));
		}
	}

	private static boolean checkLastElementAndCreateLink(DecisionKnowledgeElement lastElement, Sentence sentence) {
		if (lastElement != null) {
			Link link = Link.instantiateDirectedLink(lastElement, sentence);
			GenericLinkManager.insertLink(link, null);
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
			if (aoElement.getType().equals(typeToSearch.toString())) {
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
						Query.select().where("PROJECT_KEY = ?", sentence.getProject().getProjectKey()))) {
					if (databaseEntry.getId() == sentence.getId()) {
						databaseEntry.setEndSubstringCount(sentence.getEndSubstringCount());
						databaseEntry.setRelevant(sentence.isRelevant());
						databaseEntry.setTagged(sentence.isTagged());
						databaseEntry.setType(sentence.getTypeAsString());
						databaseEntry.setStartSubstringCount(sentence.getStartSubstringCount());
						databaseEntry.save();
					}
				}
				return null;
			}
		});
	}

	public static DecisionKnowledgeInCommentEntity getElementFromAO(long commentId, int endSubtringCount,
			int startSubstringCount, String projectKey) {
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

	public static void setSentenceKnowledgeType(Sentence sentence) {
		init();
		ActiveObjects.executeInTransaction(new TransactionCallback<DecisionKnowledgeInCommentEntity>() {
			@Override
			public DecisionKnowledgeInCommentEntity doInTransaction() {

				for (DecisionKnowledgeInCommentEntity databaseEntry : ActiveObjects
						.find(DecisionKnowledgeInCommentEntity.class)) {
					if (databaseEntry.getId() == sentence.getId()) {
						databaseEntry.setType(sentence.getTypeAsString());
						int additionalLength = JiraIssueCommentPersistenceManager
								.addTagsToCommentWhenAutoClassified(databaseEntry);
						// TODO used to be setTaggedFinegrained
						databaseEntry.setTagged(true);
						databaseEntry.setEndSubstringCount(databaseEntry.getEndSubstringCount() + additionalLength);
						JiraIssueCommentPersistenceManager.updateSentenceLengthForOtherSentencesInSameComment(sentence,
								additionalLength);
						databaseEntry.save();
						return databaseEntry;
					}
				}
				return null;
			}
		});
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

	public static boolean setSentenceIrrelevant(long id, boolean isTagged) {
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
						sentenceEntity.setTagged(isTagged);
						sentenceEntity.setType(KnowledgeType.OTHER.toString());
						sentenceEntity.save();
						return true;
					}
				}
				return false;
			}
		});
	}

	protected static void stripTagsOutOfComment(DecisionKnowledgeInCommentEntity sentenceEntity) {
		if (sentenceEntity.getType() == null || sentenceEntity.getType().equalsIgnoreCase("Other")) {
			return;
		}
		CommentManager commentManager = ComponentAccessor.getCommentManager();
		MutableComment mutableComment = (MutableComment) commentManager.getMutableComment(sentenceEntity.getCommentId());
		String newBody = "";
		try {
			newBody = mutableComment.getBody().substring(sentenceEntity.getStartSubstringCount(),
					sentenceEntity.getEndSubstringCount());
		} catch (StringIndexOutOfBoundsException e) {
			return;
		}
		int oldlength = newBody.length();
		int oldEnd = sentenceEntity.getEndSubstringCount();
		newBody = newBody.replaceAll("\\{.*?\\}", "");

		sentenceEntity.setEndSubstringCount(sentenceEntity.getEndSubstringCount() - (2 * (sentenceEntity.getType().length() + 2)));
		sentenceEntity.save();

		int lengthDiff = newBody.length() - oldlength;
		DecXtractEventListener.editCommentLock = true;
		String first = mutableComment.getBody().substring(0, sentenceEntity.getStartSubstringCount());
		String second = newBody;
		String third = mutableComment.getBody().substring(oldEnd);
		mutableComment.setBody(first + second + third);
		commentManager.update(mutableComment, true);
		DecXtractEventListener.editCommentLock = false;
		
		JiraIssueCommentPersistenceManager.updateSentenceLengthForOtherSentencesInSameComment(new SentenceImpl(sentenceEntity), lengthDiff);
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
		List<DecisionKnowledgeElement> elements = new ArrayList<DecisionKnowledgeElement>();
		for (DecisionKnowledgeInCommentEntity databaseEntry : ActiveObjects.find(DecisionKnowledgeInCommentEntity.class,
				Query.select().where("PROJECT_KEY = ? AND ISSUE_ID = ? AND TYPE = ?", projectKey, issueId, type))) {
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

	public static void createLinksForNonLinkedElementsForProject(String projectKey) {
		init();
		for (DecisionKnowledgeInCommentEntity databaseEntry : ActiveObjects.find(DecisionKnowledgeInCommentEntity.class,
				Query.select().where("PROJECT_KEY = ?", projectKey))) {
			checkIfSentenceHasAValidLink(databaseEntry.getId(), databaseEntry.getIssueId(),
					LinkType.getLinkTypeForKnowledgeType(databaseEntry.getType()));
		}
	}

	public static void createLinksForNonLinkedElementsForIssue(long issueId) {
		init();
		for (DecisionKnowledgeInCommentEntity databaseEntry : ActiveObjects.find(DecisionKnowledgeInCommentEntity.class,
				Query.select().where("ISSUE_ID = ?", issueId))) {
			checkIfSentenceHasAValidLink(databaseEntry.getId(), databaseEntry.getIssueId(),
					LinkType.getLinkTypeForKnowledgeType(databaseEntry.getType()));
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
					if (databaseEntry.getType() != null && (databaseEntry.getType().equals(rootElementType.toString())
							|| (databaseEntry.getType().length() == 3 // its either Pro or con
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

		Sentence element = (Sentence) new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(aoId);

		JiraIssuePersistenceManager s = new JiraIssuePersistenceManager(element.getProject().getProjectKey());
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
		JiraIssueCommentPersistenceManager.updateSentenceLengthForOtherSentencesInSameComment(element, length);

		// delete ao sentence entry
		new JiraIssueCommentPersistenceManager("").deleteDecisionKnowledgeElement(aoId, null);

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

	public static void deleteCommentsSentences(com.atlassian.jira.issue.comments.Comment comment) {
		init();
		DecisionKnowledgeInCommentEntity[] commentSentences = ActiveObjects.find(DecisionKnowledgeInCommentEntity.class,
				Query.select().where("ISSUE_ID = ? AND COMMENT_ID = ?", comment.getIssue().getId(), comment.getId()));
		for (DecisionKnowledgeInCommentEntity entity : commentSentences) {
			new JiraIssueCommentPersistenceManager("").deleteDecisionKnowledgeElement(entity.getId(), null);
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

	public static long getIdOfSentenceForMacro(String body, Long issueId, String typeString, String projectKey) {
		init();
		List<DecisionKnowledgeElement> sentences = ActiveObjectsManager.getElementsForIssueWithType(issueId, projectKey,
				typeString);
		for (DecisionKnowledgeElement sentence : sentences) {
			if (sentence.getDescription().trim().equals(body.trim().replaceAll("<[^>]*>", ""))) {
				return sentence.getId();
			}
		}
		LOGGER.debug("Nothing found for: " + body.replace("<br/>", "").trim());
		return 0;
	}

	/**
	 * Migration function on button "Validate Sentence Database" Adds Link types to
	 * "empty" links. Can be deleted in a future release
	 * 
	 * @param projectKey
	 */
	public static void migrateArgumentTypesInLinks(String projectKey) {
		init();
		DecisionKnowledgeInCommentEntity[] sentencesInProject = ActiveObjects
				.find(DecisionKnowledgeInCommentEntity.class, Query.select().where("PROJECT_KEY = ?", projectKey));
		for (DecisionKnowledgeInCommentEntity dbEntry : sentencesInProject) {
			if (dbEntry.getType().length() == 3) {// Equals Argument
				List<Link> links = GenericLinkManager.getLinksForElement("s" + dbEntry.getId());
				for (Link link : links) {
					if (link.getType() == null || link.getType() == "" || link.getType().equalsIgnoreCase("contain")) {
						GenericLinkManager.deleteLink(link);
						link.setType(LinkType.getLinkTypeForKnowledgeType(dbEntry.getType()).toString());
						GenericLinkManager.insertLinkWithoutTransaction(link);
					}
				}
			}
		}
	}

}