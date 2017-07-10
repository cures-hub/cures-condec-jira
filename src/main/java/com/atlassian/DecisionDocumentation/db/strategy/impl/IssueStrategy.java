package com.atlassian.DecisionDocumentation.db.strategy.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.ofbiz.core.entity.GenericEntityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.DecisionDocumentation.db.strategy.Strategy;
import com.atlassian.DecisionDocumentation.rest.model.DecisionRepresentation;
import com.atlassian.DecisionDocumentation.rest.model.LinkRepresentation;
import com.atlassian.DecisionDocumentation.rest.model.SimpleDecisionRepresentation;
import com.atlassian.DecisionDocumentation.util.ComponentGetter;
import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.bc.issue.IssueService.IssueResult;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.ConstantsManager;
import com.atlassian.jira.exception.CreateException;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueInputParameters;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.issue.link.IssueLink;
import com.atlassian.jira.issue.link.IssueLinkManager;
import com.atlassian.jira.issue.link.IssueLinkType;
import com.atlassian.jira.issue.link.IssueLinkTypeManager;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.user.ApplicationUser;

/**
 * 
 * @author Ewald Rode
 * @description
 */
public class IssueStrategy implements Strategy {
	private static final Logger LOGGER = LoggerFactory.getLogger(IssueStrategy.class);

	@Override
	public long createDecisionComponent(DecisionRepresentation dec, ApplicationUser user) {
		IssueInputParameters issueInputParameters = ComponentGetter.getIssueService().newIssueInputParameters();

		issueInputParameters.setSummary(dec.getName());
		issueInputParameters.setDescription(dec.getDescription());
		// TODO change?
		issueInputParameters.setAssigneeId(user.getName());
		issueInputParameters.setReporterId(user.getName());
		Project project = ComponentGetter.getProjectService().getProjectByKey(user, dec.getProjectKey()).getProject();
		issueInputParameters.setProjectId(project.getId());
		String issueTypeId = getIssueTypeId(dec.getType());
		issueInputParameters.setIssueTypeId(issueTypeId);

		IssueService issueService = ComponentGetter.getIssueService();
		IssueService.CreateValidationResult result = issueService.validateCreate(user, issueInputParameters);
		if (result.getErrorCollection().hasAnyErrors()) {
			for (Map.Entry<String, String> entry : result.getErrorCollection().getErrors().entrySet()) {
				LOGGER.error(entry.getKey() + ": " + entry.getValue());
			}
			return 0;
		} else {
			IssueResult issueResult = issueService.create(user, result);
			return issueResult.getIssue().getId();
		}
	}

	@Override
	public void editDecisionComponent(DecisionRepresentation dec, ApplicationUser user) {
		IssueService issueService = ComponentGetter.getIssueService();
		IssueService.IssueResult issueResult = issueService.getIssue(user, dec.getId());
		MutableIssue issue = issueResult.getIssue();
		IssueInputParameters issueInputParameters = issueService.newIssueInputParameters();
		issueInputParameters.setSummary(dec.getName());
		issueInputParameters.setDescription(dec.getDescription());
		IssueService.UpdateValidationResult result = issueService.validateUpdate(user, issue.getId(),
				issueInputParameters);
		if (result.getErrorCollection().hasAnyErrors()) {
			for (Map.Entry<String, String> entry : result.getErrorCollection().getErrors().entrySet()) {
				LOGGER.error(entry.getKey() + ": " + entry.getValue());
			}
		} else {
			issueService.update(user, result);
		}
	}

	//TODO TEST
	@Override 
	public void deleteDecisionComponent(DecisionRepresentation dec, ApplicationUser user) {
		IssueService issueService = ComponentGetter.getIssueService();
		IssueService.IssueResult issue = issueService.getIssue(user, dec.getId());
		if (issue.isValid()) {
			IssueService.DeleteValidationResult result = issueService.validateDelete(user, issue.getIssue().getId());
			if (result.getErrorCollection().hasAnyErrors()) {
				for (Map.Entry<String, String> entry : result.getErrorCollection().getErrors().entrySet()) {
					LOGGER.error(entry.getKey() + ": " + entry.getValue());
				}
			} else {
				issueService.delete(user, result);
			}
		} else {
			LOGGER.error("Issue could not be found.");
		}
	}

	@Override
	public void createLink(LinkRepresentation link, ApplicationUser user) {
		IssueLinkManager issueLinkManager = ComponentAccessor.getIssueLinkManager();
		IssueLinkTypeManager issueLinkTypeManager = ComponentAccessor.getComponent(IssueLinkTypeManager.class);
		Collection<IssueLinkType> issueLinkTypeCollection = issueLinkTypeManager.getIssueLinkTypesByName(link.getLinkType());
		Iterator<IssueLinkType> issueLinkTypeIterator = issueLinkTypeCollection.iterator();
		long typeId = 0;
		while (issueLinkTypeIterator.hasNext()) {
			IssueLinkType issueLinkType = issueLinkTypeIterator.next();
			typeId = issueLinkType.getId();
		}
		long sequence = 0;
		List<IssueLink> inwardIssueLinkList = issueLinkManager.getInwardLinks(link.getIngoingId()); //TODO check
		List<IssueLink> outwardIssueLinkList = issueLinkManager.getOutwardLinks(link.getIngoingId()); //TODO check
		for(IssueLink issueLink : inwardIssueLinkList) {
			if(sequence <= issueLink.getSequence()) {
				sequence = issueLink.getSequence()+1;
			}
		}
		for(IssueLink issueLink : outwardIssueLinkList) {
			if(sequence <= issueLink.getSequence()) {
				sequence = issueLink.getSequence()+1;
			}	
		}
		//TODO set Sequence
		try {
			issueLinkManager.createIssueLink(link.getOutgoingId(), link.getIngoingId(), typeId, sequence, user);//TODO check
		} catch (CreateException e) {
			// TODO Logger issuelink was not created
			e.printStackTrace();
		} finally {
			/**
			 * reset sequences after creation of issuelink
			 */
			outwardIssueLinkList = issueLinkManager.getOutwardLinks(link.getIngoingId());//TODO check
			issueLinkManager.resetSequences(outwardIssueLinkList);
			inwardIssueLinkList = issueLinkManager.getInwardLinks(link.getIngoingId());//TODO check
			issueLinkManager.resetSequences(inwardIssueLinkList);
		}
	}

	//TODO TEST
	@Override
	public void deleteLink(LinkRepresentation link, ApplicationUser user) {
		IssueLinkTypeManager issueLinkTypeManager = ComponentAccessor.getComponent(IssueLinkTypeManager.class);
		Collection<IssueLinkType> issueLinkTypeCollection = issueLinkTypeManager.getIssueLinkTypesByName(link.getLinkType());
		Iterator<IssueLinkType> issueLinkTypeIterator = issueLinkTypeCollection.iterator();
		long typeId = 0;
		while (issueLinkTypeIterator.hasNext()) {
			IssueLinkType issueLinkType = issueLinkTypeIterator.next();
			typeId = issueLinkType.getId();
		}
		IssueLinkManager issueLinkManager = ComponentAccessor.getIssueLinkManager();
		IssueLink issueLink = issueLinkManager.getIssueLink(link.getIngoingId(), link.getOutgoingId(), typeId);//TODO check
		issueLinkManager.removeIssueLink(issueLink , user);
	}

	@Override
	public List<SimpleDecisionRepresentation> searchUnlinkedDecisionComponents(long id, String projectKey) {
		List<SimpleDecisionRepresentation> decList = new ArrayList<SimpleDecisionRepresentation>();
		ProjectManager projectManager = ComponentAccessor.getProjectManager();
		IssueManager issueManager = ComponentAccessor.getIssueManager();
		Project project = projectManager.getProjectObjByKey(projectKey);
		if (project == null) {
			/* projekt mit diesem projectKey existiert nicht */
		} else {
			Collection<Long> issueIds;
			try {
				issueIds = issueManager.getIssueIdsForProject(project.getId());
			} catch (GenericEntityException e) {
				issueIds = new ArrayList<Long>();
			}
			List<Long> issueIdList = new ArrayList<Long>();
			for (Long issueId : issueIds) {
				issueIdList.add(issueId);
			}
			/* Filtere alle Issues raus, die weder Decision noch Decisionkomponente sind */
			List<Issue> issueList = new ArrayList<Issue>();
			for (int index = 0; index < issueIdList.size(); ++index) {
				Issue issue = issueManager.getIssueObject(issueIdList.get(index));
				String issueType = issue.getIssueType().getName();
				if (issueType.equals("Decision") || issueType.equals("Question") || issueType.equals("Issue")
						|| issueType.equals("Goal") || issueType.equals("Solution") || issueType.equals("Alternative")
						|| issueType.equals("Claim") || issueType.equals("Context") || issueType.equals("Assumption")
						|| issueType.equals("Constraint") || issueType.equals("Implication")
						|| issueType.equals("Assessment") || issueType.equals("Argument")) {
					issueList.add(issue);
				}
			}
			Issue parentIssue = issueManager.getIssueObject(id);
			if (parentIssue == null) {

			} else {
				/*
				 * Fuelle das Result-Array mit Issues welche noch nicht mit dem parentIssue
				 * verlinkt sind
				 */
				for (int index = 0; index < issueList.size(); ++index) {
					Issue issue = issueList.get(index);
					/*
					 * Das Issue, von dem die Anfrage ausgeht, soll nicht in der Liste der
					 * moeglichen Issues auftauchen
					 */
					if (!issue.getId().equals(id)) {
						/* bereits verlinkte Issues sollen nicht nochmal in der Liste auftauchen */
						List<IssueLink> allOutwardIssueLink = ComponentAccessor.getIssueLinkManager()
								.getOutwardLinks(parentIssue.getId());
						List<Issue> outwardIssuesList = new ArrayList<Issue>();
						for (int counter = 0; counter < allOutwardIssueLink.size(); ++counter) {
							IssueLink issueLink = allOutwardIssueLink.get(counter);
							outwardIssuesList.add(issueLink.getDestinationObject());
						}
						boolean linked = false;
						for (int counter = 0; counter < outwardIssuesList.size(); ++counter) {
							Issue linkIssue = outwardIssuesList.get(counter);
							if (linkIssue.equals(issue)) {
								linked = true;
							}
						}
						if (!linked) {
							decList.add(new SimpleDecisionRepresentation(issueList.get(index)));
						}
					}
				}
				/* Kreiere JSON-String und sende ihn zurueck */
			}
		}
		return decList;
	}
	
	private String getIssueTypeId(String type) {
		ConstantsManager constantsManager = ComponentAccessor.getConstantsManager();
		Collection<IssueType> listOfIssueTypes = constantsManager.getAllIssueTypeObjects();
		for (IssueType iType : listOfIssueTypes) {
			if (iType.getName().equalsIgnoreCase(type)) {
				return iType.getId();
			}
		}
		return "";
	}
}