# Nudging Developers To Perform Rationale Management

The ConDec Jira plug-in tries to **motivate developers** to document decision knowledge and to use the documentation using nudging mechanisms.

## Just-in-time prompts
ConDec shows a **just-in-time prompt** to the developers when they change the state of a Jira issue, e.g., when they start or finish a requirement.
The just-in-time prompt covers recommendations regarding the following **smart features for rationale management**:
- [Definition of done checking to support high quality of the knowledge documentation](https://github.com/cures-hub/cures-condec-jira/tree/master/doc/features/quality-checking.md)
- [Automatic text classification to identify decision knowledge in natural language text](https://github.com/cures-hub/cures-condec-jira/tree/master/doc/features/automatic-text-classification.md)
- [Recommendation of solution options from external knowledge sources](https://github.com/cures-hub/cures-condec-jira/tree/master/doc/features/decision-guidance.md)
- [Link suggestion and duplicate recognition](https://github.com/cures-hub/cures-condec-jira/tree/master/doc/features/link-suggestion.md)

![Just-in-time prompt](https://github.com/cures-hub/cures-condec-jira/raw/master/doc/screenshots/nudging_prompt.png)

*Just-in-time prompt*

The rationale manager can activate or deactivate the events for that just-in-time prompts are shown. 
The events are activated per default for **opt-out nudging**.

![Configuration view for the events that trigger a just-in-time prompt](https://github.com/cures-hub/cures-condec-jira/raw/master/doc/screenshots/config_nudging_events.png)

*Configuration view for the events that trigger a just-in-time prompt*

## Ambient feedback and friction nudges
ConDec offers **ambient feedback nudging mechanisms** to indicate that their are recommendations regarding rationale management.

ConDec colors menu items according to the number of recommendations that have not been accepted or discarded by the developers:
If there are many recommendations, menu items are colored in red (i.e. action is needed by the developers, this can also be seen as a way to **create friction**).
If there are a few recommendations, menu items are colored in orange.
If there are no recommendations, e.g. if the [definition of done]((https://github.com/cures-hub/cures-condec-jira/tree/master/doc/features/quality-checking.md) is fulfilled 
or all recommendations were accepted or discarded by the developers, menu items are colored in green.

![Coloring of menu items to indicate whether action is needed](https://github.com/cures-hub/cures-condec-jira/raw/master/doc/screenshots/nudging_ambient_feedback_menu.png)

*Coloring of menu items to indicate whether action is needed*

Besides, the knowledge elements that violate the definition of done are highlighted with a red text color within
the knowledge graph views to indicate quality problems. 
Tooltips explain which definition of done criteria are violated.
This should nudge the developers to improve the DoD.

![Overview of decision problems with quality hightlighting](https://github.com/cures-hub/cures-condec-jira/raw/master/doc/screenshots/knowledge_overview_quality_highlighting.png)

*Overview of decision problems with quality hightlighting*