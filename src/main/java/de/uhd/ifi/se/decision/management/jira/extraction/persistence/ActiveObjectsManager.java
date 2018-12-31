package de.uhd.ifi.se.decision.management.jira.extraction.persistence;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.exception.CreateException;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.comments.MutableComment;
import com.atlassian.jira.issue.link.IssueLinkManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.sal.api.transaction.TransactionCallback;

import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.extraction.DecXtractEventListener;
import de.uhd.ifi.se.decision.management.jira.extraction.model.Sentence;
import de.uhd.ifi.se.decision.management.jira.extraction.model.impl.SentenceImpl;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
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

	public static final Logger LOGGER = LoggerFactory.getLogger(ActiveObjectsManager.class);

	public static void init() {
		if (ActiveObjects == null) {
			ActiveObjects = ComponentGetter.getActiveObjects();
		}
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
			JiraIssueCommentPersistenceManager.checkIfSentenceHasAValidLink(databaseEntry.getId(),
					databaseEntry.getIssueId(), LinkType.getLinkTypeForKnowledgeType(databaseEntry.getType()));
		}
	}

	public static void createLinksForNonLinkedElementsForIssue(long issueId) {
		init();
		for (DecisionKnowledgeInCommentEntity databaseEntry : ActiveObjects.find(DecisionKnowledgeInCommentEntity.class,
				Query.select().where("ISSUE_ID = ?", issueId))) {
			JiraIssueCommentPersistenceManager.checkIfSentenceHasAValidLink(databaseEntry.getId(),
					databaseEntry.getIssueId(), LinkType.getLinkTypeForKnowledgeType(databaseEntry.getType()));
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

	public static void deleteCommentsSentences(com.atlassian.jira.issue.comments.Comment comment) {
		init();
		DecisionKnowledgeInCommentEntity[] commentSentences = ActiveObjects.find(DecisionKnowledgeInCommentEntity.class,
				Query.select().where("ISSUE_ID = ? AND COMMENT_ID = ?", comment.getIssue().getId(), comment.getId()));
		for (DecisionKnowledgeInCommentEntity entity : commentSentences) {
			new JiraIssueCommentPersistenceManager("").deleteDecisionKnowledgeElement(entity.getId(), null);
		}
	}

	public static long getIdOfSentenceForMacro(String body, Long issueId, String typeString, String projectKey) {
		init();
		List<DecisionKnowledgeElement> sentences = JiraIssueCommentPersistenceManager
				.getElementsForIssueWithType(issueId, projectKey, typeString);
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