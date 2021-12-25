# Decision Guidance/Recommendation of Solution Options from External Knowledge Sources

The ConDec Jira plug-in offers a feature that recommends solution options for decision problems from external knowledge sources.
External knowledge sources can be DBPedia or other Jira projects.

The Figure below shows the decision guidance view with three recommendations generated from DBPedia 
for the decision problem **Which framework should we use as a webcrawler?**
The developers can accept or discard the recommendations.
If they accept a recommendation, the respective solution option and arguments are added to the knowledge graph.
The score represents the predicted relevance of a recommendation, i.e., 
how likely the developers accept the recommendation.
The score is used to rank the recommendations.
The score consists of a value and an explanation. 
Besides, the score can be composed of various sub-scores for the criteria that were used to calculate the score.
The colored menu item indicates whether action is needed.

![Decision guidance view with three recommendations generated from DBPedia](../screenshots/decision_guidance_webcrawler.png)