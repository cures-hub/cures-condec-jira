package de.uhd.ifi.se.decision.documentation.jira.persistence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.uhd.ifi.se.decision.documentation.jira.decisionknowledge.Type;
import org.ofbiz.core.entity.GenericEntityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import com.atlassian.jira.util.ErrorCollection;

import de.uhd.ifi.se.decision.documentation.jira.decisionknowledge.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.documentation.jira.decisionknowledge.Link;
import de.uhd.ifi.se.decision.documentation.jira.util.KeyValuePairList;
import de.uhd.ifi.se.decision.documentation.jira.util.Pair;

/**
 * @description Implements the PersistenceStrategy abstract class. Uses JIRA issues
 *              to store decision knowledge
 */
public class IssueStrategy extends PersistenceStrategy {
	private static final Logger LOGGER = LoggerFactory.getLogger(IssueStrategy.class);

	@Override
	public DecisionKnowledgeElement insertDecisionKnowledgeElement(DecisionKnowledgeElement decisionElement,
			ApplicationUser user) {
		IssueInputParameters issueInputParameters = ComponentAccessor.getIssueService().newIssueInputParameters();

		issueInputParameters.setSummary(decisionElement.getName());
		issueInputParameters.setDescription(decisionElement.getDescription());
		issueInputParameters.setAssigneeId(user.getName());
		issueInputParameters.setReporterId(user.getName());

		Project project = ComponentAccessor.getProjectManager().getProjectByCurrentKey(decisionElement.getProjectKey());
		issueInputParameters.setProjectId(project.getId());
		String issueTypeId = getIssueTypeId(decisionElement.getType());
		issueInputParameters.setIssueTypeId(issueTypeId);
		IssueService issueService = ComponentAccessor.getIssueService();

		IssueService.CreateValidationResult result = issueService.validateCreate(user, issueInputParameters);
		if (result.getErrorCollection().hasAnyErrors()) {
			for (Map.Entry<String, String> entry : result.getErrorCollection().getErrors().entrySet()) {
				LOGGER.error(entry.getKey() + ": " + entry.getValue());
			}
			return null;
		} else {
			IssueResult issueResult = issueService.create(user, result);
			Issue issue = issueResult.getIssue();
			decisionElement.setId(issue.getId());
			decisionElement.setKey(issue.getKey());
			return decisionElement;
		}
	}

	@Override
	public boolean updateDecisionKnowledgeElement(DecisionKnowledgeElement decisionElement,
			ApplicationUser user) {
		IssueService issueService = ComponentAccessor.getIssueService();

		IssueResult issueResult = issueService.getIssue(user, decisionElement.getId());
		MutableIssue issueToBeUpdated = issueResult.getIssue();
		IssueInputParameters issueInputParameters = issueService.newIssueInputParameters();
		issueInputParameters.setSummary(decisionElement.getName());
		issueInputParameters.setDescription(decisionElement.getDescription());
		IssueService.UpdateValidationResult result = issueService.validateUpdate(user, issueToBeUpdated.getId(),
				issueInputParameters);
		if (result.getErrorCollection().hasAnyErrors()) {
			for (Map.Entry<String, String> entry : result.getErrorCollection().getErrors().entrySet()) {
				LOGGER.error(entry.getKey() + ": " + entry.getValue());
			}
			return false;
		} else {
			issueResult = issueService.update(user, result);
			return true;
		}
	}

	@Override
	public boolean deleteDecisionKnowledgeElement(DecisionKnowledgeElement decisionElement, ApplicationUser user) {
		IssueService issueService = ComponentAccessor.getIssueService();
		IssueService.IssueResult issue = issueService.getIssue(user, decisionElement.getId());
		if (issue.isValid()) {
			IssueService.DeleteValidationResult result = issueService.validateDelete(user, issue.getIssue().getId());
			if (result.getErrorCollection().hasAnyErrors()) {
				for (Map.Entry<String, String> entry : result.getErrorCollection().getErrors().entrySet()) {
					LOGGER.error(entry.getKey() + ": " + entry.getValue());
				}
				return false;
			} else {
				ErrorCollection errorCollection = issueService.delete(user, result);
				if (!errorCollection.hasAnyErrors()) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public long insertLink(Link link, ApplicationUser user) {
		IssueLinkManager issueLinkManager = ComponentAccessor.getIssueLinkManager();
		IssueLinkTypeManager issueLinkTypeManager = ComponentAccessor.getComponent(IssueLinkTypeManager.class);
		Collection<IssueLinkType> issueLinkTypeCollection = issueLinkTypeManager
				.getIssueLinkTypesByName(link.getLinkType());
		Iterator<IssueLinkType> issueLinkTypeIterator = issueLinkTypeCollection.iterator();
		long typeId = 0;
		while (issueLinkTypeIterator.hasNext()) {
			IssueLinkType issueLinkType = issueLinkTypeIterator.next();
			typeId = issueLinkType.getId();
		}
		long sequence = 0;
		List<IssueLink> inwardIssueLinkList = issueLinkManager.getInwardLinks(link.getIngoingId());
		List<IssueLink> outwardIssueLinkList = issueLinkManager.getOutwardLinks(link.getIngoingId());
		for (IssueLink issueLink : inwardIssueLinkList) {
			if (sequence <= issueLink.getSequence()) {
				sequence = issueLink.getSequence() + 1;
			}
		}
		for (IssueLink issueLink : outwardIssueLinkList) {
			if (sequence <= issueLink.getSequence()) {
				sequence = issueLink.getSequence() + 1;
			}
		}
		try {
			issueLinkManager.createIssueLink(link.getOutgoingId(), link.getIngoingId(), typeId, sequence, user);
		} catch (CreateException e) {
			LOGGER.error("CreateException");
			return (long) 0;
		} catch (NullPointerException e) {
			LOGGER.error("NullPointerException");
			return (long) 0;
		} finally {
			outwardIssueLinkList = issueLinkManager.getOutwardLinks(link.getIngoingId());
			issueLinkManager.resetSequences(outwardIssueLinkList);
			inwardIssueLinkList = issueLinkManager.getInwardLinks(link.getIngoingId());
			issueLinkManager.resetSequences(inwardIssueLinkList);
		}
		IssueLink issueLink = issueLinkManager.getIssueLink(link.getOutgoingId(), link.getIngoingId(), typeId);
		if (issueLink == null) {
			LOGGER.error("issueLink == null");
			return (long) 0;
		}
		return issueLink.getId();
	}

	@Override
	public void deleteLink(Link link, ApplicationUser user) {

	}

	@Override
	public List<Link> getInwardLinks(DecisionKnowledgeElement element) {
		return null;
	}

	@Override
	public List<Link> getOutwardLinks(DecisionKnowledgeElement element) {
		return null;
	}

	@Override
	public List<DecisionKnowledgeElement> getUnlinkedDecisionComponents(long id, String projectKey) {
		List<DecisionKnowledgeElement> unlinkedDecisionComponents = new ArrayList<DecisionKnowledgeElement>();
		ProjectManager projectManager = ComponentAccessor.getProjectManager();
		IssueManager issueManager = ComponentAccessor.getIssueManager();
		Project project = projectManager.getProjectObjByKey(projectKey);
		if (project != null) {
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
			// Make sure that only decision knowledge is included in list
			List<Issue> issueList = new ArrayList<Issue>();
			for (int index = 0; index < issueIdList.size(); ++index) {
				Issue issue = issueManager.getIssueObject(issueIdList.get(index));
				String issueType = issue.getIssueType().getName();
				// TODO Enable to configure decision knowledge types
				if (issueType.equalsIgnoreCase("Decision") || issueType.equalsIgnoreCase("Question") || issueType.equalsIgnoreCase("Issue")
						|| issueType.equalsIgnoreCase("Goal") || issueType.equalsIgnoreCase("Solution") || issueType.equalsIgnoreCase("Alternative")
						|| issueType.equalsIgnoreCase("Claim") || issueType.equalsIgnoreCase("Context") || issueType.equalsIgnoreCase("Assumption")
						|| issueType.equalsIgnoreCase("Constraint") || issueType.equalsIgnoreCase("Implication")
						|| issueType.equalsIgnoreCase("Assessment") || issueType.equalsIgnoreCase("Argument")) {
					issueList.add(issue);
				}
			}
			Issue parentIssue = issueManager.getIssueObject(id);
			if (parentIssue != null) {
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
							if (linkIssue != null) {
								if (linkIssue.equals(issue)) {
									linked = true;
								}
							}
						}
						if (!linked) {
							unlinkedDecisionComponents.add(new DecisionKnowledgeElement(issueList.get(index)));
						}
					}
				}
			}
		}
		return unlinkedDecisionComponents;
	}


	public List<Issue> getOutwardKnowledgeElements(DecisionKnowledgeElement decisionKnowledgeElement) {
		List<IssueLink> allOutwardIssueLink = ComponentAccessor.getIssueLinkManager()
				.getOutwardLinks(decisionKnowledgeElement.getId());
		List<Issue> outwardIssues = new ArrayList<Issue>();
		for (int i = 0; i < allOutwardIssueLink.size(); ++i) {
			IssueLink issueLink = allOutwardIssueLink.get(i);
			outwardIssues.add(issueLink.getDestinationObject());
		}
		return outwardIssues;
	}

	public List<Issue> getInwardKnowledgeElements(DecisionKnowledgeElement decisionKnowledgeElement) {
		List<IssueLink> allInwardIssueLink = ComponentAccessor.getIssueLinkManager()
				.getInwardLinks(decisionKnowledgeElement.getId());
		List<Issue> inwardIssues = new ArrayList<Issue>();
		for (int i = 0; i < allInwardIssueLink.size(); ++i) {
			IssueLink issueLink = allInwardIssueLink.get(i);
			inwardIssues.add(issueLink.getSourceObject());
		}
		return inwardIssues;
	}

	@Override
	public List<DecisionKnowledgeElement> getChildren(DecisionKnowledgeElement decisionKnowledgeElement) {
		List<Issue> outwardIssues = this.getOutwardKnowledgeElements(decisionKnowledgeElement);
		List<Issue> inwardIssues = this.getInwardKnowledgeElements(decisionKnowledgeElement);
		List<DecisionKnowledgeElement> children = new ArrayList<DecisionKnowledgeElement>();
		for (int i = 0; i < inwardIssues.size(); ++i) {
			if (inwardIssues.get(i).getIssueType().getName().equalsIgnoreCase("Argument")) {
				if (decisionKnowledgeElement != null & inwardIssues.get(i) != null) {
					children=computeChildren(decisionKnowledgeElement,inwardIssues.get(i),children);
				}
			}
		}
		for (int i = 0; i < outwardIssues.size(); ++i) {
			if (decisionKnowledgeElement != null & outwardIssues.get(i) != null) {
				children=computeChildren(decisionKnowledgeElement,outwardIssues.get(i),children);
			}
		}
		return children;
	}

	@Override
	public List<DecisionKnowledgeElement> getParents(DecisionKnowledgeElement decisionKnowledgeElement) {
		return null;
	}

	@Override
	public DecisionKnowledgeElement getDecisionKnowledgeElement(String key) {
		IssueManager issueManager = ComponentAccessor.getIssueManager();
		Issue issue = issueManager.getIssueByCurrentKey(key);
		return new DecisionKnowledgeElement(issue);
	}

	// TODO Implement this method and add it to the PersistenceStrategy interface
	public DecisionKnowledgeElement getDecisionKnowledgeElement(long id) {
		return null;
	}

	@Override
	public List<DecisionKnowledgeElement> getDecisionKnowledgeElements(String projectKey) {
		IssueManager issueManager = ComponentAccessor.getIssueManager();
		Project project = ComponentAccessor.getProjectManager().getProjectByCurrentKey(projectKey);
		Collection<Long> issueIds;
		if (projectKey == null) {
			return null;
		}
		try {
			issueIds = issueManager.getIssueIdsForProject(project.getId());
		} catch (GenericEntityException e) {
			issueIds = new ArrayList<Long>();
		}
		List<DecisionKnowledgeElement> decisionKnowledgeElements = new ArrayList<DecisionKnowledgeElement>();
		for (Long issueId : issueIds) {
			Issue issue = issueManager.getIssueObject(issueId);
			decisionKnowledgeElements.add(new DecisionKnowledgeElement(issue));
		}
		return decisionKnowledgeElements;
	}

	private List<DecisionKnowledgeElement> computeChildren(DecisionKnowledgeElement decisionKnowledgeElement, Issue issue,List<DecisionKnowledgeElement> children){
		Pair<String, String> newKVP = new Pair<String, String>(decisionKnowledgeElement.getKey(),
				issue.getKey());
		Pair<String, String> newKVPReverse = new Pair<String, String>(issue.getKey(),
				decisionKnowledgeElement.getKey());
		boolean boolvar = false;
		for (int counter = 0; counter < KeyValuePairList.keyValuePairList.size(); ++counter) {
			Pair<String, String> globalInst = KeyValuePairList.keyValuePairList.get(counter);
			if (newKVP.equals(globalInst)) {
				boolvar = true;
			}
		}
		if (!boolvar) {
			KeyValuePairList.keyValuePairList.add(newKVP);
			KeyValuePairList.keyValuePairList.add(newKVPReverse);
			children.add(new DecisionKnowledgeElement(issue));
			return children;
		}
		return  children;
	}

	private String getIssueTypeId(Type type) {
		ConstantsManager constantsManager = ComponentAccessor.getConstantsManager();
		Collection<IssueType> listOfIssueTypes = constantsManager.getAllIssueTypeObjects();
		for (IssueType issueType : listOfIssueTypes) {
			if (issueType.getName().equalsIgnoreCase(type.toString())) {
				return issueType.getId();
			}
		}
		return "";
	}
}