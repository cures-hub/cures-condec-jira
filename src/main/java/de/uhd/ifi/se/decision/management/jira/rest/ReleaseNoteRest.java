package de.uhd.ifi.se.decision.management.jira.rest;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.user.ApplicationUser;
import com.google.common.collect.ImmutableMap;
import de.uhd.ifi.se.decision.management.jira.config.AuthenticationManager;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterExtractor;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.ReleaseNotesPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.releasenotes.*;
import de.uhd.ifi.se.decision.management.jira.releasenotes.impl.ReleaseNoteImpl;
import de.uhd.ifi.se.decision.management.jira.releasenotes.impl.ReleaseNoteIssueProposalImpl;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
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
		FilterExtractor extractor = new FilterExtractor(projectKey, user, query);
		List<DecisionKnowledgeElement> elementsMatchingQuery = new ArrayList<DecisionKnowledgeElement>();
		elementsMatchingQuery = extractor.getAllElementsMatchingQuery();
		if(elementsMatchingQuery.size()==0){
			return Response.status(Response.Status.BAD_REQUEST).entity(ImmutableMap.of("error",
					"No resolved issues were found in this date range!"))
					.build();
		}
		ArrayList<ReleaseNoteIssueProposal> proposals = setPriorityValues(elementsMatchingQuery, user);
		ArrayList<ReleaseNoteIssueProposal> comparedProposals = compareProposals(proposals,releaseNoteConfiguration.getTaskCriteriaPrioritisation());

		HashMap<String, ArrayList<ReleaseNoteIssueProposal>> mappedProposals = mapProposals(comparedProposals, releaseNoteConfiguration);
		if(mappedProposals==null){
			return Response.status(Response.Status.BAD_REQUEST).entity(ImmutableMap.of("error",
					"No issues with the mapped types are resolved in this date range!"))
					.build();
		}
		HashMap<String,Object> result=new HashMap<String,Object>();
		result.put("proposals",mappedProposals);
		result.put("additionalConfiguration",releaseNoteConfiguration.getAdditionalConfiguration());
		result.put("title",releaseNoteConfiguration.getTitle());
		result.put("startDate",releaseNoteConfiguration.getStartDate());
		result.put("endDate",releaseNoteConfiguration.getEndDate());
		return Response.ok(result).build();
	}

	/**
	 * Gather priority metrics for the Release Note Issue Proposal
	 *
	 * @param elementsMatchingQuery is the list of DecisionKnowledge Elements
	 * @param user                  Application User
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

		for (int i = 0; i < elementsMatchingQuery.size(); i++) {
			DecisionKnowledgeElement dkElement = elementsMatchingQuery.get(i);
			//add key to used keys
			usedKeys.add(dkElement.getKey());
			//create Release note issue proposal with the element and the count of associated decision knowledge
			// check if DK or Comment
			ReleaseNoteIssueProposal proposal = new ReleaseNoteIssueProposalImpl(dkElement, 0);
			String dkKey = dkElement.getKey();

			//check if it is a dk Issue or just a DK comment
			//comments are not rated, just counted
			if (dkKey.contains(":")) {
				String[] parts = dkKey.split(":");
				Integer currentCount = dkLinkedCount.get(parts[0]);
				if (currentCount != null) {
					currentCount += 1;
					dkLinkedCount.put(parts[0], currentCount);
				} else {
					dkLinkedCount.put(parts[0], 1);
				}
			} else {
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
		}

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
	private ArrayList<ReleaseNoteIssueProposal> compareProposals(ArrayList<ReleaseNoteIssueProposal> proposals, EnumMap<TaskCriteriaPrioritisation,Double> userWeighting) {

		List<TaskCriteriaPrioritisation> criteriaEnumList = TaskCriteriaPrioritisation.getOriginalList();


		//find median
		EnumMap<TaskCriteriaPrioritisation,Integer> medianOfProposals= getMedianOfProposals(proposals);

		//for each criteria create a list of integers, so we can then compute min, max values and the scales
		EnumMap<TaskCriteriaPrioritisation, ArrayList<Integer>> countValues = this.getFlatListOfValues(proposals);

		//we later check in which interval the proposal would be and apply the corresponding lower and higher value

		//add min and max to lists
		//the first value of the ArrayList is for the first interval and the second is for the second interval
		EnumMap<TaskCriteriaPrioritisation,ArrayList<Integer>> minValues = new EnumMap<>(TaskCriteriaPrioritisation.class);
		EnumMap<TaskCriteriaPrioritisation, ArrayList<Integer>> maxValues = new EnumMap<>(TaskCriteriaPrioritisation.class);
		this.getMinAndMaxValues(minValues, maxValues, countValues,medianOfProposals);

		//calculate user weighting:
		//add all and then divide by count of items
		var ref= new Object(){
			Double total=0.0;
		};
		userWeighting.values().forEach(weight->{
			if(weight!=null){
				ref.total +=weight;
			}
		});
		Double part=ref.total/userWeighting.values().size();
		//then assign each the corresponding part
		EnumMap<TaskCriteriaPrioritisation,Double> weightedValues=new EnumMap<>(TaskCriteriaPrioritisation.class);
		userWeighting.forEach((key, value) -> {
				if(value!=null) {
					weightedValues.put(key, value * part);
				}else{
					weightedValues.put(key, 0.0);
				}
		});


		proposals.forEach(dkElement -> {
			EnumMap<TaskCriteriaPrioritisation, Integer> existingCriteriaValues = dkElement.getTaskCriteriaPrioritisation();
			//use ref object due to atomic problem etc.
			var totalRef = new Object() {
				Double total = 0.0;
			};
			criteriaEnumList.forEach(criteria -> {
				double scaling=0;

				//check if criteria is in first or second interval
				int index=0;
				int minVal=1;
				int maxVal=5;
				if(existingCriteriaValues.get(criteria)>medianOfProposals.get(criteria)&& minValues.get(criteria).size()>1 && maxValues.get(criteria).size()>1){
					index=1;
					minVal=6;
					maxVal=10;
				}
				//extra treatment for priorities, as there are no outliers and numbers are reversed
				//we just do the scaling on all using min and max values
				if (criteria == TaskCriteriaPrioritisation.PRIORITY) {
					int indexMinPrio=0;
					int indexMaxPrio=0;
					if(maxValues.get(criteria).size()>1){
						indexMaxPrio=1;
					}
					scaling = scaleFromSmallToLarge(existingCriteriaValues.get(criteria), minValues.get(criteria).get(indexMinPrio), maxValues.get(criteria).get(indexMaxPrio),1,10);
					scaling = 11-scaling;
				}else{
					scaling = scaleFromSmallToLarge(existingCriteriaValues.get(criteria), minValues.get(criteria).get(index), maxValues.get(criteria).get(index),minVal,maxVal);
				}
				//multiply scaling with associated weighting input from user
				double userWeight=weightedValues.get(criteria);
				scaling *=  userWeight;
				totalRef.total += scaling;
			});
			//set rating
			dkElement.setRating(Math.round(totalRef.total));
		});
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
	private static double scaleFromSmallToLarge(final double valueIn, final double baseMin, final double baseMax,double limitMin,double limitMax) {
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

	private void getMinAndMaxValues(EnumMap<TaskCriteriaPrioritisation,ArrayList<Integer>> minValues, EnumMap<TaskCriteriaPrioritisation,ArrayList<Integer>> maxValues, EnumMap<TaskCriteriaPrioritisation, ArrayList<Integer>> countValues,EnumMap<TaskCriteriaPrioritisation,Integer> medianOfProposals) {
		List<TaskCriteriaPrioritisation> criteriaEnumList = TaskCriteriaPrioritisation.getOriginalList();
		criteriaEnumList.forEach(criteria -> {
			ArrayList<Integer> values = countValues.get(criteria);
			ArrayList<ArrayList<Integer>> valuesInInterval= new ArrayList<ArrayList<Integer>>();
			ArrayList<Integer> firstInterval= new ArrayList<Integer>();
			ArrayList<Integer> secondInterval= new ArrayList<Integer>();
			if (values != null && values.size() > 0) {
				//first interval
				values.forEach(value->{
					if(value<=medianOfProposals.get(criteria)){
						firstInterval.add(value);
					}else{
						//second interval
						secondInterval.add(value);
					}
				});
				valuesInInterval.add(firstInterval);
				valuesInInterval.add(secondInterval);
				ArrayList<Integer> mins=new ArrayList<Integer>();
				ArrayList<Integer> maxs=new ArrayList<Integer>();
				if(valuesInInterval.get(0).size()>0){
					mins.add(Collections.min(valuesInInterval.get(0)));
					maxs.add(Collections.max(valuesInInterval.get(0)));
				}
				if(valuesInInterval.get(1).size()>0){
					mins.add(Collections.min(valuesInInterval.get(1)));
					maxs.add(Collections.max(valuesInInterval.get(1)));
				}

				minValues.put(criteria,mins);
				maxValues.put(criteria,maxs);
			}
		});
	}


	private EnumMap<TaskCriteriaPrioritisation, Integer> getMedianOfProposals(ArrayList<ReleaseNoteIssueProposal> proposals) {
		List<TaskCriteriaPrioritisation> criteriaEnumList = TaskCriteriaPrioritisation.getOriginalList();
		EnumMap<TaskCriteriaPrioritisation, Integer> medians = new EnumMap<TaskCriteriaPrioritisation, Integer>(TaskCriteriaPrioritisation.class);
		criteriaEnumList.forEach(criteria -> {
			ArrayList<Integer> flatList = new ArrayList<Integer>();
			proposals.forEach(proposal -> {
				flatList.add(proposal.getTaskCriteriaPrioritisation().get(criteria));
			});
			//sort list
			Collections.sort(flatList);
			//find median
			double medianIndex = 0.0;
			if (flatList.size() > 0) {
				medianIndex = ((double) flatList.size()) / 2;
			}
			//use floor value
			medianIndex = Math.floor(medianIndex);
			//the median is the value at index medianIndex
			medians.put(criteria, (flatList.get((int) medianIndex)));
		});
		return medians;
	}

	private HashMap<String, ArrayList<ReleaseNoteIssueProposal>> mapProposals(ArrayList<ReleaseNoteIssueProposal> proposals, ReleaseNoteConfiguration config) {
		IssueManager issueManager = ComponentAccessor.getIssueManager();

		HashMap<String, ArrayList<ReleaseNoteIssueProposal>> resultMap = new HashMap<String, ArrayList<ReleaseNoteIssueProposal>>();
		ArrayList<ReleaseNoteIssueProposal> bugs = new ArrayList<ReleaseNoteIssueProposal>();
		ArrayList<ReleaseNoteIssueProposal> features = new ArrayList<ReleaseNoteIssueProposal>();
		ArrayList<ReleaseNoteIssueProposal> improvements = new ArrayList<ReleaseNoteIssueProposal>();
		var ref= new Object(){
			Boolean hasResult=false;
		};
		proposals.forEach(proposal -> {
			Issue issue = issueManager.getIssueByCurrentKey(proposal.getDecisionKnowledgeElement().getKey());
			IssueType issueType = issue.getIssueType();
			Integer issueTypeId = Integer.valueOf(issueType.getId());
			//new features
			if (config.getFeatureMapping() != null && config.getFeatureMapping().contains(issueTypeId)) {
				features.add(proposal);
				ref.hasResult=true;
			}
			//bugs
			//check if include bugs is false
			if (config.getBugFixMapping() != null && config.getBugFixMapping().contains(issueTypeId) && config.getAdditionalConfiguration().get(AdditionalConfigurationOptions.INCLUDE_BUG_FIXES)) {
				bugs.add(proposal);
				ref.hasResult=true;
			}
			//improvements
			if (config.getImprovementMapping() != null && config.getImprovementMapping().contains(issueTypeId)) {
				improvements.add(proposal);
				ref.hasResult=true;
			}

		});
		if(!ref.hasResult){
			return null;
		}
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


		resultMap.put(ReleaseNoteCategory.BUG_FIXES.toString(), bugs);
		resultMap.put(ReleaseNoteCategory.NEW_FEATURES.toString(), features);
		resultMap.put(ReleaseNoteCategory.IMPROVEMENTS.toString(), improvements);
		return resultMap;
	}

	@Path("/postProposedKeys")
	@POST
	@Produces({MediaType.APPLICATION_JSON})
	public Response postProposedKeys(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey, HashMap<String,HashMap<String,ArrayList<String>>> postObject) {
		ApplicationUser user = AuthenticationManager.getUser(request);
		HashMap<String,ArrayList<String>> keysForContent = postObject.get("selectedKeys");
		String title= postObject.get("title").get("id").get(0);
		ArrayList<String> additionalConfiguration = postObject.get("additionalConfiguration").get("id");
		List<DecisionKnowledgeElement> list = getIssuesFromIssueKeys(user, projectKey, keysForContent);

		//generate text string
		String markDownString = generateMarkdownString(list, keysForContent,title,additionalConfiguration);
		//return text string
		HashMap<String, String> result = new HashMap<String, String>();
		result.put("markdown", markDownString);
		return Response.ok(result).build();
	}

	private List<DecisionKnowledgeElement> getIssuesFromIssueKeys(ApplicationUser user, String projectKey, HashMap<String, ArrayList<String>> keysForContent) {
		String issueQuery = buildQueryFromIssueKeys(keysForContent);
		//make one jql request and later seperate by bugs, features and improvements
		String query = "?jql=project=" + projectKey + "&& key in(" + issueQuery + ")";
		FilterExtractor extractor = new FilterExtractor(projectKey, user, query);
		List<DecisionKnowledgeElement> elementsQueryLinked = new ArrayList<DecisionKnowledgeElement>();
		elementsQueryLinked = extractor.getAllElementsMatchingQuery();
		return elementsQueryLinked;
	}


	private String buildQueryFromIssueKeys(HashMap<String, ArrayList<String>> keysForContent) {
		String result = "";
		StringBuilder jql = new StringBuilder();

		List<String> categories = ReleaseNoteCategory.toList();
		//create flat
		List<String> uniqueList = new ArrayList<>();
		categories.forEach(category -> {
			ArrayList<String> issueKeys = keysForContent.get(category);
			if (issueKeys != null && !issueKeys.isEmpty()) {
				issueKeys.forEach(key -> {
					if (!uniqueList.contains(key)) {
						uniqueList.add(key);
					}
				});
			}
		});
		if (!uniqueList.isEmpty()) {
			uniqueList.forEach(key -> {
				jql.append(key);
				jql.append(",");
			});
		}
		if (!jql.toString().isEmpty()) {
			//remove last comma
			result = jql.toString().substring(0, jql.length() - 1);
		}
		return result;
	}

	private String generateMarkdownString(List<DecisionKnowledgeElement> issues, HashMap<String, ArrayList<String>> keysForContent,String title,ArrayList<String> additionalConfiguration) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("# ").append(title).append(" \n");
		EnumMap<ReleaseNoteCategory, Boolean> containsTitle = ReleaseNoteCategory.toBooleanMap();
		ReleaseNoteCategory.toList().forEach(cat -> {
			issues.forEach(issue -> {
				if (keysForContent.get(cat).contains(issue.getKey())) {
					//add title once
					if (!containsTitle.get(ReleaseNoteCategory.getTargetGroup(cat))) {
						stringBuilder.append("## ")
								.append(ReleaseNoteCategory.getTargetGroupReadable(ReleaseNoteCategory.getTargetGroup(cat)))
								.append(" \n");
						containsTitle.put(ReleaseNoteCategory.getTargetGroup(cat), true);
					}
					//add issue title and url
					markdownAddIssue(stringBuilder, issue);
					//add decision knowledge of the issue
					if(additionalConfiguration!=null && additionalConfiguration.contains(AdditionalConfigurationOptions.INCLUDE_DECISION_KNOWLEDGE.toUpperString())) {
						List<DecisionKnowledgeElement> comments = new ArrayList<DecisionKnowledgeElement>();
						issues.forEach(sameIssue -> {
							//check if dk knowledge is in issues which contains the issuekey and is one of types issue or decision
							String sameIssueKey = sameIssue.getKey();
							String issueKey = issue.getKey();
							Boolean b1 = sameIssueKey.contains(issueKey);
							Boolean b2 = sameIssueKey.contains(":");
							Boolean b3 = sameIssueKey.equals(issueKey);
							Boolean isIssue = sameIssue.getType().equals(KnowledgeType.ISSUE);
							Boolean isDecision = sameIssue.getType().equals(KnowledgeType.DECISION);
							if ((b1 && b2 && !b3) && (isIssue || isDecision)) {
								comments.add(sameIssue);
							}
						});
						markdownAddComments(stringBuilder, comments);
					}
				}
			});
			//append new line
			stringBuilder.append("\n");
		});

		addAdditionalConfigurationToMarkDownString(stringBuilder,additionalConfiguration);

		return stringBuilder.toString();
	}

	private void addAdditionalConfigurationToMarkDownString(StringBuilder stringBuilder, ArrayList<String> additionalConfiguration) {
		if(additionalConfiguration!=null) {
			additionalConfiguration.forEach(type->{
				stringBuilder.append(AdditionalConfigurationOptions.getMarkdownOptionsString(type));
			});

		}
	}

	private void markdownAddComments(StringBuilder stringBuilder,List<DecisionKnowledgeElement> dkElements){
		String pathToIcons="https://raw.githubusercontent.com/cures-hub/cures-condec-jira/master/src/main/resources/images/";
		dkElements.forEach(element->{
			String iconUrl="decision.png";
			if(element.getType().equals(KnowledgeType.ISSUE)){
			 iconUrl="issue.png";
			}
			stringBuilder.append("\t- ")
					.append("![")
					.append(element.getTypeAsString())
					.append("](")
					.append(pathToIcons)
					.append(iconUrl)
					.append(")")
					.append(element.getTypeAsString())
					.append(": ")
					.append(element.getSummary())
					.append("\n");
		});
	}

	private void markdownAddIssue(StringBuilder stringBuilder, DecisionKnowledgeElement issue) {
		stringBuilder.append("- ")
				.append(issue.getSummary())
				.append(" ([")
				.append(issue.getKey())
				.append("](")
				.append(issue.getUrl())
				.append(")) \n");
	}

	@Path("/createReleaseNote")
	@POST
	@Produces({MediaType.APPLICATION_JSON})
	public Response createReleaseNote(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey, HashMap<String,String> postObject) {
		ApplicationUser user = AuthenticationManager.getUser(request);
		String title = postObject.get("title");
		String startDate = postObject.get("startDate");
		String endDate = postObject.get("endDate");
		String releaseNoteContent=postObject.get("content");

		ReleaseNoteImpl releaseNote = new ReleaseNoteImpl(title, releaseNoteContent, projectKey,startDate,endDate);
		long id = ReleaseNotesPersistenceManager.createReleaseNotes(releaseNote, user);

		return Response.ok(id).build();
	}
	@Path("/updateReleaseNote")
	@POST
	@Produces({MediaType.APPLICATION_JSON})
	public Response updateReleaseNote(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey, ReleaseNote releaseNote) {
		ApplicationUser user = AuthenticationManager.getUser(request);

		boolean updated = ReleaseNotesPersistenceManager.updateReleaseNotes(releaseNote, user);

		return Response.ok(updated).build();
	}

	@Path("/getReleaseNote")
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	public Response getReleaseNote(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey, @QueryParam("id") long id) {
		ReleaseNote releaseNote= ReleaseNotesPersistenceManager.getReleaseNotes(id);
		return Response.ok(releaseNote).build();
	}

	@Path("/getAllReleaseNotes")
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	public Response getAllReleaseNotes(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey,@QueryParam("query") String query) {
		List<ReleaseNote> releaseNotes= ReleaseNotesPersistenceManager.getAllReleaseNotes(projectKey,query);
		return Response.ok(releaseNotes).build();
	}
	@Path("/deleteReleaseNote")
	@DELETE
	@Produces({MediaType.APPLICATION_JSON})
	public Response deleteReleaseNote(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey, @QueryParam("id") long id) {
		ApplicationUser user = AuthenticationManager.getUser(request);
		boolean deleted= ReleaseNotesPersistenceManager.deleteReleaseNotes(id,user);
		return Response.ok(deleted).build();
	}

}
