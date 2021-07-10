/**
 * This module implements the solution option recommendation for decision problems (=decision guidance).
 * The recommended solution options are taken from external knowledge sources, such as
 * other Jira projects or DBPedia.
 */
(function (global) {

	let ConDecDecisionGuidance = function () {

	};

	ConDecDecisionGuidance.prototype.initView = function () {
		// get all the elements for the dropdown and fill the dropdown
		
		var jiraIssueKey = conDecAPI.getIssueKey();
		console.log(jiraIssueKey);
		const filterSettings = {
			knowledgeTypes: ["Issue", "Problem", "Goal"],
			selectedElement: jiraIssueKey !== undefined ? jiraIssueKey : "",
		};
		conDecAPI.getKnowledgeElements(filterSettings, (results) => {
			const filteredResults = results.filter((element) => element.id !== JIRA.Issue.getIssueId())
			fillDecisionProblemDropDown(filteredResults);
		});
		// add button listener
		this.addOnClickListenerForRecommendations();
		
		// Register/subscribe this view as an observer
		conDecObservable.subscribe(this);
	};

	ConDecDecisionGuidance.prototype.updateView = function () {
	};

	ConDecDecisionGuidance.prototype.addOnClickListenerForRecommendations = function () {
		var tableBody = document.getElementById("recommendation-container-table-body");
		$("#recommendation-button").click(function (event) {
			event.preventDefault();
			let dropDownElement = document.getElementById("decision-guidance-dropdown-items");
			tableBody.innerHTML = "";
			const spinner = $("#loading-spinner-recommendation");
			spinner.show();
			var filterSettings = {
				"projectKey" : conDecAPI.getProjectKey(),
				"selectedElement" : dropDownElement.value
			}
			conDecDecisionGuidanceAPI.getRecommendations(filterSettings)
				.then((recommendationMap) => {				
					if (Object.keys(recommendationMap).length > 0 && Object.values(recommendationMap)[0].length > 0) {
						var decisionProblemId = Object.keys(recommendationMap)[0];								
						var recommendations = Object.values(recommendationMap)[0].sort((a, b) => b.score.value - a.score.value);
						// dropDownElement.value is the id of the decision knowledge element that is selected :)
						buildRecommendationTable(recommendations, decisionProblemId);
						conDecNudgingAPI.decideAmbientFeedbackForTab(recommendations.length, "menu-item-decision-guidance");
					} else {
						tableBody.innerHTML = "<i>No recommendations found!</i>";
					}
					spinner.hide();

				})
				.catch(err => {
					console.log(err)
					spinner.hide();
					tableBody.innerHTML = "<strong>An error occurred!</strong>";
				});
		});
	};

	function buildRecommendationTable(recommendations, currentIssueId) {
		var tableBody = document.getElementById("recommendation-container-table-body");		
		let counter = 0;
		recommendations.forEach(recommendation => {
			counter++;
			let tableRow = "<tr>";
			tableRow += "<td><a class='alternative-summary' href='" + recommendation.url + "'>" + recommendation.summary + "</a></td>";
			tableRow += "<td><div style='display:flex;gap:3px;align-items:center;'>" + recommendation.knowledgeSource.name + "<span class='aui-icon aui-icon-small " + recommendation.knowledgeSource.icon + "'>Knowledge Source Type</span></div></td>";
			tableRow += "<td>" + conDecRecommendation.buildScore(recommendation.score, "score_" + counter) + "</td>";
			tableRow += "<td><button title='Adds the recommendation to the knowledge graph' id='row_" + counter + "' class='aui-button-primary aui-button accept-solution-button'>" + "Accept" + "</button></td>";
			tableRow += "<td><ul>";
			recommendation.arguments.forEach(argument => {
				if (argument) {
					tableRow += "<li><img src='" + argument.image + "'/>";
					tableRow += argument.summary + "</li>";
				}
			});
			tableRow += "</ul></td>";
			tableRow += "</tr>";
			tableBody.insertAdjacentHTML('beforeend', tableRow);

			$("#row_" + counter).click(function () {
				onAcceptClicked(recommendation, currentIssueId);
			});
		});
		conDecAPI.showFlag("success", "#Recommendations: " + counter);
	}

	function onAcceptClicked(recommendation, currentIssueId) {
		conDecAPI.getDecisionKnowledgeElement(currentIssueId, "s", (currentIssue) => {
			conDecDialog.showCreateDialog(currentIssue.id, currentIssue.documentationLocation, "Alternative", recommendation.summary, "", function (id, documentationLocation) {
				recommendation.arguments.forEach(argument => {
					conDecAPI.createDecisionKnowledgeElement(argument.summary, "", argument.type, argument.documentationLocation, id, documentationLocation, function () {
						conDecAPI.showFlag("success", "Recommendation was added successfully!");
					});
				});
			});
		});
	}

	/**
	 * @param issues
	 *            all decision problems found.
	 */
	function fillDecisionProblemDropDown(issues) {
		let dropDown = document.getElementById("decision-guidance-dropdown-items");
		dropDown.innerHTML = "";

		if (!issues.length) {
			dropDown.innerHTML += "<option disabled>Could not find any issue. Please create a new issue!</option>";
			return;
		}

		for (let issue of issues) {
			dropDown.innerHTML += "<option value='" + issue.key + "'>" + issue.summary + "</option>";
		}
	}

	global.conDecDecisionGuidance = new ConDecDecisionGuidance();
})(window);