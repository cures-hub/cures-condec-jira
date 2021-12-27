# Recommendation of Solution Options from External Knowledge Sources (Decision Guidance)

The ConDec Jira plug-in offers a feature that recommends solution options for decision problems from external knowledge sources.
External knowledge sources can be DBPedia or other Jira projects.

The Figure below shows the decision guidance view with three recommendations generated from DBPedia 
for the decision problem *Which framework should we use as a webcrawler?*
The developers can accept or discard the recommendations.
If they accept a recommendation, the respective solution option and arguments are added to the knowledge graph.

![Decision guidance view with three recommendations generated from DBPedia](../screenshots/decision_guidance_webcrawler.png)

*Decision guidance view with three recommendations generated from DBPedia*

The colored menu item indicates whether action is needed.
TODO JIP and ambient feedback

The score represents the predicted relevance of a recommendation, i.e., 
how likely the developers accept the recommendation.
The score is used to rank the recommendations.
The score consists of a value and an explanation. 
Besides, the score can be composed of various sub-scores for the criteria that were used to calculate the score.

![Explanation of the score for a recommendation generated from DBPedia](../screenshots/decision_guidance_recommendation_score.png)

*Explanation of the score for a recommendation generated from DBPedia*


## Activation and Configuration
The decision guidance feature offers various configuration possibilities.

TODO config screenshot

## Evaluation

TODO evaluation

## Design Details
The following class diagram gives an overview of relevant backend classes for this feature.

TODO class diagram screenshot