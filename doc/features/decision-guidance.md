# Recommendation of Solution Options from External Knowledge Sources (Decision Guidance)

The ConDec Jira plug-in offers a feature that recommends solution options for decision problems from **external knowledge sources**.
External knowledge sources can be **DBPedia** or **other Jira projects**.

The Figure below shows the **decision guidance view** with three recommendations generated from DBPedia 
for the decision problem *Which framework should we use as a webcrawler?*
The developers can **accept or discard** the recommendations.
If they accept a recommendation, the respective solution option and arguments are added to the knowledge graph.

![Decision guidance view with three recommendations generated from DBPedia](../screenshots/decision_guidance_webcrawler.png)

*Decision guidance view with three recommendations generated from DBPedia*

## Nudging

The colored menu item indicates whether action is needed.
TODO JIP and ambient feedback

## Recommendation Score

The **recommendation score** represents the predicted relevance of a recommendation, i.e., how likely the developers accept the recommendation.
The recommendation score is used to rank the recommendations.
The recommendation score consists of a value and an explanation. 
Besides, the recommendation score can be composed of various sub-scores for the criteria that were used to calculate the score.

![Explanation of the score for a recommendation generated from DBPedia](../screenshots/decision_guidance_recommendation_score.png)

*Explanation of the score for a recommendation generated from DBPedia*

## Evaluation

Software engineering researchers can assess the quality of the recommendations in the evaluation view.
The solution options documented for a selected decision problem are assumed to be the **ground truth/gold standard**.
The following evaluation metrics are calculated:

- **Number of True Positives:**
- **Precision@k:** Measures the precision (positive predictive value) within the top-k results, 
i.e. the fraction of relevant recommendations (that match the solution options in the ground truth) among the retrieved recommendations.
- **Recall:**
- **FScore:**
- **Average Precision:** Measures the average precision (AP) within the top-k results. 
Takes the total number of ground truth positives into account, i.e. the number of the solution options already documented.
- **Reciprokal Rank:**

![Decision guidance evaluation view](../screenshots/decision_guidance_evaluation.png)

*Decision guidance evaluation view*

## Activation and Configuration
The decision guidance feature offers various configuration possibilities.

TODO config screenshot

## Design Details
The following class diagram gives an overview of relevant backend classes for this feature.

TODO class diagram screenshot

You find the explanation for the class diagramm in the Javadoc in the code:

https://github.com/cures-hub/cures-condec-jira/tree/master/src/main/java/de/uhd/ifi/se/decision/management/jira/recommendation/decisionguidance

The UI code for decision guidance can be found here:

https://github.com/cures-hub/cures-condec-jira/tree/master/src/main/resources/templates/settings/decisionguidance

https://github.com/cures-hub/cures-condec-jira/tree/master/src/main/resources/templates/tabs/recommendation

https://github.com/cures-hub/cures-condec-jira/tree/master/src/main/resources/js/recommendation