package com.atlassian.DecisionDocumentation.util;
import java.util.Collection;

import javax.inject.Named;

import org.springframework.beans.factory.InitializingBean;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.ConstantsManager;
import com.atlassian.jira.config.IssueTypeManager;
//import com.atlassian.jira.issue.fields.config.manager.IssueTypeSchemeManager;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.issue.link.IssueLinkType;
import com.atlassian.jira.issue.link.IssueLinkTypeManager;

/**
 * @author Ewald Rode
 * @description handles plugin initialization
 */
@Named("PluginListener")
public class PluginListener implements InitializingBean {

	@Override
	public void afterPropertiesSet() throws Exception {
		ConstantsManager constantsManager = ComponentAccessor.getConstantsManager();
		Collection<IssueType> listOfIssueTypes = constantsManager.getAllIssueTypeObjects();
		IssueTypeManager itm = ComponentAccessor.getComponent(IssueTypeManager.class);
		boolean decisionBool = false;
		boolean alternativeBool = false;
		boolean argumentBool = false;
		boolean assessmentBool = false;
		boolean assumptionBool = false;
		boolean claimBool = false;
		boolean constraintBool = false;
		boolean contextBool = false;
		boolean goalBool = false;
		boolean implicationBool = false;
		boolean issueBool = false;
		boolean problemBool = false;
		boolean solutionBool = false;
		for(IssueType iType : listOfIssueTypes){
			String iTypeName = iType.getName();
			
			if(iTypeName.equals("Decision")){
				decisionBool = true;
			}else if (iTypeName.equals("Alternative")){
				alternativeBool = true;
			}else if (iTypeName.equals("Argument")){
				argumentBool = true;
			}else if (iTypeName.equals("Assessment")){
				assessmentBool = true;
			}else if (iTypeName.equals("Assumption")){
				assumptionBool = true;
			}else if (iTypeName.equals("Claim")){
				claimBool = true;
			}else if (iTypeName.equals("Constraint")){
				constraintBool = true;
			}else if (iTypeName.equals("Context")){
				contextBool = true;
			}else if (iTypeName.equals("Goal")){
				goalBool = true;
			}else if (iTypeName.equals("Implication")){
				implicationBool = true;
			}else if (iTypeName.equals("Issue")){
				issueBool = true;
			}else if (iTypeName.equals("Problem")){
				problemBool = true;
			}else if (iTypeName.equals("Solution")){
				solutionBool = true;
			}
			
		}
		if(!decisionBool){
			itm.createIssueType("Decision", "Entscheidung", (long)10300 );
		}if(!alternativeBool){
			itm.createIssueType("Alternative", "Alternative", (long)10300 );
		}if(!argumentBool){
			itm.createIssueType("Argument", "Argument", (long)10300 );
		}if(!assessmentBool){
			itm.createIssueType("Assessment", "Einschaetzung", (long)10300 );
		}if(!assumptionBool){
			itm.createIssueType("Assumption", "Annahme", (long)10300 );
		}if(!claimBool){
			itm.createIssueType("Claim", "Behauptung", (long)10300 );
		}if(!constraintBool){
			itm.createIssueType("Constraint", "Einschraenkung", (long)10300 );
		}if(!contextBool){
			itm.createIssueType("Context", "Kontext", (long)10300 );
		}if(!goalBool){
			itm.createIssueType("Goal", "Ziel", (long)10300 );
		}if(!implicationBool){
			itm.createIssueType("Implication", "Implikation", (long)10300 );
		}if(!issueBool){
			itm.createIssueType("Issue", "Problem", (long)10300 );
		}if(!problemBool){
			itm.createIssueType("Problem", "Problem", (long)10300 );
		}if(!solutionBool){
			itm.createIssueType("Solution", "Loesung", (long)10300 );
		}
		IssueLinkTypeManager iltM = ComponentAccessor.getComponent(IssueLinkTypeManager.class);
		Collection<IssueLinkType> iltC = iltM.getIssueLinkTypes(true);
		boolean containExists =false;
		boolean attackExists = false;
		boolean supportExists = false;
		boolean commentExists = false;
		for (IssueLinkType ilType : iltC){
			if (ilType.getName().equals("contain")){
				containExists = true;
			}
			if (ilType.getName().equals("attack")){
				attackExists = true;
			}
			if (ilType.getName().equals("support")){
				supportExists = true;
			}
			if (ilType.getName().equals("comment")){
				commentExists = true;
			}
		}
		if(!containExists){
			iltM.createIssueLinkType("contain", "contains","is contained by", "contain_style");
		}
		if(!attackExists){
			iltM.createIssueLinkType("attack", "attacks", "is attacked by", "contain_style");
		}
		if(!supportExists){
			iltM.createIssueLinkType("support", "supports","is supported by", "contain_style");
		}
		if(!commentExists){
			iltM.createIssueLinkType("comment", "comments on","is commented on by", "contain_style");
		}
	}
}
