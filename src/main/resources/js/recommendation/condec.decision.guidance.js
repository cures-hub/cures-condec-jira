/**
 * This module implements the solution option recommendation for decision problems (=decision guidance).
 * The recommended solution options are taken from external knowledge sources, such as
 * other Jira projects or DBPedia.
 */
(function(global) {

	let ConDecDecisionGuidance = function() {

	};

	ConDecDecisionGuidance.prototype.initView = function() {

		// get all the decision problems for the dropdown and fill the dropdown
		conDecAPI.getDecisionProblems({}, fillDecisionProblemDropDown);

		// add button listener
		this.addOnClickListenerForRecommendations();

		// Register/subscribe this view as an observer
		conDecObservable.subscribe(this);
	};

	ConDecDecisionGuidance.prototype.updateView = function() {
	};

	ConDecDecisionGuidance.prototype.addOnClickListenerForRecommendations = function() {
		var tableBody = document.getElementById("recommendation-container-table-body");
		$("#recommendation-button").click(function(event) {
			event.preventDefault();
			let dropDownElement = document.getElementById("decision-guidance-dropdown-items");
			let selectedElement = {
				id: Number(dropDownElement.value.split(":")[0]),
				documentationLocation: dropDownElement.value.split(":")[1],
				projectKey: conDecAPI.getProjectKey()
			};
			tableBody.innerHTML = "";
			const spinner = $("#loading-spinner-recommendation");
			const keywords = document.getElementById("recommendation-keywords").value;
			spinner.show();
			Promise.resolve(conDecDecisionGuidanceAPI.getRecommendations(selectedElement, keywords))
				.then((recommendations) => {
					if (recommendations.length > 0) {
						buildRecommendationTable(recommendations, selectedElement);
					} else {
						tableBody.innerHTML = "<i>No recommendations found!</i>";
					}
					conDecNudgingAPI.decideAmbientFeedbackForTab(recommendations.length, "menu-item-decision-guidance");
					spinner.hide();
				})
				.catch(err => {
					console.log(err)
					spinner.hide();
					tableBody.innerHTML = "<strong>An error occurred!</strong>";
				});
		});
	};

	function buildRecommendationTable(recommendations, parentElement) {
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

			$("#row_" + counter).click(function() {
				onAcceptClicked(recommendation, parentElement);
			});
		});
		conDecAPI.showFlag("success", "#Recommendations: " + counter);
	}

	function onAcceptClicked(recommendation, parentElement) {
		conDecAPI.getDecisionKnowledgeElement(parentElement.id, parentElement.documentationLocation, (currentIssue) => {
			conDecDialog.showCreateDialog(currentIssue.id, currentIssue.documentationLocation, "Alternative", recommendation.summary, "", function(id, documentationLocation) {
				recommendation.arguments.forEach(argument => {
					conDecAPI.createDecisionKnowledgeElement(argument.summary, "", argument.type, argument.documentationLocation, id, documentationLocation, function() {
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
			dropDown.innerHTML += "<option value='" + issue.id + ":" + issue.documentationLocation + "'>" + issue.summary + "</option>";
		}
	}
	global.conDecDecisionGuidance = new ConDecDecisionGuidance();
})(window);