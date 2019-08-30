package de.uhd.ifi.se.decision.management.jira.rest;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.user.ApplicationUser;
import de.uhd.ifi.se.decision.management.jira.config.AuthenticationManager;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterExtractor;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.releasenotes.ReleaseNoteConfiguration;
import de.uhd.ifi.se.decision.management.jira.releasenotes.ReleaseNoteIssueProposal;
import de.uhd.ifi.se.decision.management.jira.releasenotes.TaskCriteriaPrioritisation;
import de.uhd.ifi.se.decision.management.jira.releasenotes.impl.ReleaseNoteIssueProposalImpl;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;


/**
 * REST resource for Release Note
 */
@Path("/release-note")
public class ReleaseNoteRest {

	@Path("/getProposedIssues")
	@POST
	@Produces({MediaType.APPLICATION_JSON})
	public Response getProposedIssues(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey, ReleaseNoteConfiguration releaseNoteConfiguration) {

		ApplicationUser user = AuthenticationManager.getUser(request);
		List<DecisionKnowledgeElement> queryResult = new ArrayList<DecisionKnowledgeElement>();
		String query = "?jql=project=" + projectKey + " && resolved >= " + releaseNoteConfiguration.getStartDate() + " && resolved <= " + releaseNoteConfiguration.getEndDate();
		//String query = "?jql=resolved >= 2019-08-01 && resolved <= 2020-08-16";
		FilterExtractor extractor = new FilterExtractor(projectKey, user, query);
		List<DecisionKnowledgeElement> elementsMatchingQuery = new ArrayList<DecisionKnowledgeElement>();
		elementsMatchingQuery = extractor.getAllElementsMatchingQuery();
		ArrayList<ReleaseNoteIssueProposal> proposals = setPriorityValues(elementsMatchingQuery, user);
		ArrayList<ReleaseNoteIssueProposal> comparedProposals = compareProposals(proposals);
		HashMap<String, ArrayList<ReleaseNoteIssueProposal>> mappedProposals = mapProposals(comparedProposals, releaseNoteConfiguration);
		return Response.ok(mappedProposals).build();
	}

	/**
	 * Gather priority metrics for the Release Note Issue Proposal
	 *
	 * @param elementsMatchingQuery is the list of DecisionKnowledge Elements
	 * @param user Application User
	 * @return Array with Release Note issue Proposals
	 */
	private ArrayList<ReleaseNoteIssueProposal> setPriorityValues(List<DecisionKnowledgeElement> elementsMatchingQuery, ApplicationUser user) {
		ArrayList<ReleaseNoteIssueProposal> releaseNoteIssueProposals = new ArrayList<ReleaseNoteIssueProposal>();
		//set up components we need to gather metrics
		IssueManager issueManager = ComponentAccessor.getIssueManager();

		//create plain array with no duplicates
		List<String> usedKeys = new ArrayList<String>();
		HashMap<String, Integer> reporterIssueCount = new HashMap<String, Integer>();
		HashMap<String, Integer> resolverIssueCount = new HashMap<String, Integer>();
		//for each DecisionKnowledgeElement create one ReleaseNoteIssueProposal element with the data
		HashMap<String, Integer> dkLinkedCount = new HashMap<String, Integer>();

		for(int i=0;i<elementsMatchingQuery.size();i++){
			DecisionKnowledgeElement dkElement=elementsMatchingQuery.get(i);
					//add key to used keys
					usedKeys.add(dkElement.getKey());
					//create Release note issue proposal with the element and the count of associated decision knowledge
					// check if DK or Comment
					ReleaseNoteIssueProposal proposal = new ReleaseNoteIssueProposalImpl(dkElement,0);
					String dkKey= dkElement.getKey();

					//check if it is a dk Issue or just a DK comment
					//comments are not rated, just counted
					if(dkKey.contains(":")){
						String[]parts=dkKey.split(":");
						Integer currentCount=dkLinkedCount.get(parts[0]);
						if(currentCount!=null){
							currentCount +=1;
						}else{
							dkLinkedCount.put(parts[0],1);
						}
					}else {
					Issue issue = issueManager.getIssueByCurrentKey(dkElement.getKey());

					//set priority
					proposal.getAndSetPriority(issue);

					//set count of comments
					proposal.getAndSetCountOfComments(issue);

					//set size summary
					proposal.getAndSetSizeOfSummary();

					//set size description
					proposal.getAndSetSizeOfDescription();

					//set days to complete
					proposal.getAndSetDaysToCompletion(issue);

					//set experience reporter
					proposal.getAndSetExperienceReporter(issue, reporterIssueCount, user);

					//set experience resolver
					proposal.getAndSetExperienceResolver(issue, resolverIssueCount, user);

					//add to results
					releaseNoteIssueProposals.add(proposal);
				}
			};
		//now check DK element links
		for (Map.Entry<String, Integer> entry : dkLinkedCount.entrySet()) {
			String key = entry.getKey();
			Integer value = entry.getValue();
			releaseNoteIssueProposals.forEach(proposal -> {
				if (proposal.getDecisionKnowledgeElement().getKey().equals(key)) {
					proposal.getTaskCriteriaPrioritisation().put(TaskCriteriaPrioritisation.COUNT_DECISION_KNOWLEDGE, value);
				}
			});
		}
		return releaseNoteIssueProposals;
	}

	/**
	 * compare all ReleaseNoteIssueProposal elements and set the rating for each category considering UserInput
	 * compare each element with the others of the same category
	 * scaling algorithm:
	 * https://stackoverflow.com/questions/5294955/how-to-scale-down-a-range-of-numbers-with-a-known-min-and-max-value
	 * alternative algorithm could be gaussian standard distribution
	 * other alternative could be median-interval-separation
	 *
	 * @param proposals
	 * @return
	 */
	private ArrayList<ReleaseNoteIssueProposal> compareProposals(ArrayList<ReleaseNoteIssueProposal> proposals) {

		//for each criteria create a list of integers, so we can then compute min, max values and the scales
		EnumMap<TaskCriteriaPrioritisation, ArrayList<Integer>> countValues = this.getFlatListOfValues(proposals);


		//add min and max to lists
		EnumMap<TaskCriteriaPrioritisation, Integer> minValues = new EnumMap<>(TaskCriteriaPrioritisation.class);
		EnumMap<TaskCriteriaPrioritisation, Integer> maxValues = new EnumMap<>(TaskCriteriaPrioritisation.class);
		this.getMinAndMaxValues(minValues, maxValues, countValues);


		// for each proposal we want to compute the scaling of all criterias
		getAndSetScalingForAllCriteria(proposals, minValues, maxValues);

		return proposals;
	}


	/**
	 * Scale a number from 1 to 10
	 *
	 * @param valueIn
	 * @param baseMin
	 * @param baseMax
	 * @return
	 */
	private static double scaleFromOneToTen(final double valueIn, final double baseMin, final double baseMax) {
		double limitMin = 1;
		double limitMax = 10;
		//check if baseMax===base min
		if (baseMax - baseMin == 0) {
			return limitMax;
		}
		return ((limitMax - limitMin) * (valueIn - baseMin) / (baseMax - baseMin)) + limitMin;
	}


	private EnumMap<TaskCriteriaPrioritisation, ArrayList<Integer>> getFlatListOfValues(ArrayList<ReleaseNoteIssueProposal> proposals) {

		EnumMap<TaskCriteriaPrioritisation, ArrayList<Integer>> countValues = new EnumMap<TaskCriteriaPrioritisation, ArrayList<Integer>>(TaskCriteriaPrioritisation.class);
		List<TaskCriteriaPrioritisation> criteriaEnumList = TaskCriteriaPrioritisation.getOriginalList();

		proposals.forEach(dkElement -> {
			EnumMap<TaskCriteriaPrioritisation, Integer> existingCriteriaValues = dkElement.getTaskCriteriaPrioritisation();
			//add values to
			criteriaEnumList.forEach(criteria -> {

				Integer currentValue = existingCriteriaValues.get(criteria);

				ArrayList<Integer> existingValues = countValues.get(criteria);

				if (existingValues == null) {
					// init new list
					ArrayList<Integer> newList = new ArrayList<>();
					// add value to new list
					newList.add(currentValue);
					countValues.put(criteria, newList);
				} else {
					existingValues.add(currentValue);
				}
			});
		});
		return countValues;
	}

	private void getMinAndMaxValues(EnumMap<TaskCriteriaPrioritisation, Integer> minValues, EnumMap<TaskCriteriaPrioritisation, Integer> maxValues, EnumMap<TaskCriteriaPrioritisation, ArrayList<Integer>> countValues) {
		List<TaskCriteriaPrioritisation> criteriaEnumList = TaskCriteriaPrioritisation.getOriginalList();
		criteriaEnumList.forEach(criteria -> {
			ArrayList<Integer> values = countValues.get(criteria);
			if (values != null && values.size() > 0) {
				maxValues.put(criteria, Collections.max(values));
				minValues.put(criteria, Collections.min(values));
			}
		});
	}

	private void getAndSetScalingForAllCriteria(ArrayList<ReleaseNoteIssueProposal> proposals, EnumMap<TaskCriteriaPrioritisation, Integer> minValues, EnumMap<TaskCriteriaPrioritisation, Integer> maxValues) {
		List<TaskCriteriaPrioritisation> criteriaEnumList = TaskCriteriaPrioritisation.getOriginalList();

		proposals.forEach(dkElement -> {
			EnumMap<TaskCriteriaPrioritisation, Integer> existingCriteriaValues = dkElement.getTaskCriteriaPrioritisation();
			//use ref object due to atomic problem etc.
			var totalRef = new Object() {
				Double total = 0.0;
			};
			criteriaEnumList.forEach(criteria -> {
				double scaling = scaleFromOneToTen(existingCriteriaValues.get(criteria), minValues.get(criteria), maxValues.get(criteria));

				//extra treatment for Priority as 1 is good higher is bad
				if (criteria == TaskCriteriaPrioritisation.PRIORITY) {
					scaling -= 11;
				}
				//@todo multiply scaling with associated weighting input from user
				totalRef.total += scaling;
			});
			//set rating
			dkElement.setRating(totalRef.total);
		});
	}

	private HashMap<String, ArrayList<ReleaseNoteIssueProposal>> mapProposals(ArrayList<ReleaseNoteIssueProposal> proposals, ReleaseNoteConfiguration config) {
		IssueManager issueManager = ComponentAccessor.getIssueManager();

		HashMap<String, ArrayList<ReleaseNoteIssueProposal>> resultMap = new HashMap<String, ArrayList<ReleaseNoteIssueProposal>>();
		ArrayList<ReleaseNoteIssueProposal> bugs = new ArrayList<ReleaseNoteIssueProposal>();
		ArrayList<ReleaseNoteIssueProposal> features = new ArrayList<ReleaseNoteIssueProposal>();
		ArrayList<ReleaseNoteIssueProposal> improvements = new ArrayList<ReleaseNoteIssueProposal>();
		proposals.forEach(proposal -> {
			Issue issue = issueManager.getIssueByCurrentKey(proposal.getDecisionKnowledgeElement().getKey());
			IssueType issueType= issue.getIssueType();
			Integer issueTypeId = Integer.valueOf(issueType.getId());
			//new features
			if (config.getFeatureMapping() != null && config.getFeatureMapping().contains(issueTypeId)) {
				features.add(proposal);
			}
			//bugs
			if (config.getBugFixMapping() != null && config.getBugFixMapping().contains(issueTypeId)) {
				bugs.add(proposal);
			}
			//improvements
			if (config.getImprovementMapping() != null && config.getImprovementMapping().contains(issueTypeId)) {
				improvements.add(proposal);
			}


		});
		Comparator<ReleaseNoteIssueProposal> compareByRating = new Comparator<ReleaseNoteIssueProposal>() {
			@Override
			public int compare(ReleaseNoteIssueProposal o1, ReleaseNoteIssueProposal o2) {
				Double rating1 = o1.getRating();
				Double rating2 = o2.getRating();
				return rating2.toString().compareTo(rating1.toString());
			}
		};
		bugs.sort(compareByRating);
		features.sort(compareByRating);
		improvements.sort(compareByRating);


		resultMap.put("bug_fixes", bugs);
		resultMap.put("new_features", features);
		resultMap.put("improvements", improvements);
		return resultMap;
	}

}
