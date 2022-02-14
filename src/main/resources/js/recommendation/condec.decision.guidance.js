/**
 * This module implements the solution option recommendation for decision problems (=decision guidance).
 * The recommended solution options are taken from external knowledge sources, such as
 * other Jira projects or DBPedia.
 */
(function(global) {

	var selectedDecisionProblem;

	let ConDecDecisionGuidance = function() {

	};

	ConDecDecisionGuidance.prototype.initView = function() {
		// get all the decision problems for the dropdown and fill the dropdown
		let dropdown = document.getElementById("decision-guidance-dropdown");
		conDecAPI.getDecisionProblems({}, (decisionProblems) =>
			conDecFiltering.initKnowledgeElementDropdown(dropdown, decisionProblems, this.selectedDecisionProblem,
				"decision-guidance", (selectedElement) => {
					conDecDecisionGuidance.selectedDecisionProblem = selectedElement;
				}));

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
			tableBody.innerHTML = "";
			const spinner = $("#loading-spinner-recommendation");
			const keywords = document.getElementById("recommendation-keywords").value;
			spinner.show();
			conDecDecisionGuidance.selectedDecisionProblem.projectKey = conDecAPI.projectKey;
			Promise.resolve(conDecDecisionGuidanceAPI.getRecommendations(conDecDecisionGuidance.selectedDecisionProblem, keywords))
				.then((recommendations) => {
					if (recommendations.length > 0) {
						buildRecommendationTable(recommendations, conDecDecisionGuidance.selectedDecisionProblem);
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
<<<<<<< HEAD
			tableRow += "<td><div style='display:flex;gap:3px;align-items:center;'>" + recommendation.url+"</div></td>";
=======
			tableRow += "<td><div style='display:flex;gap:3px;align-items:center;'>" + recommendation.knowledgeSource.name + "<span class='aui-icon aui-icon-small " + recommendation.knowledgeSource.icon + "'>Knowledge Source Type</span></div></td>";
>>>>>>> parent of e1c4b918 (Update condec.decision.guidance.js; Show URL as source)
			tableRow += "<td>" + conDecRecommendation.buildScore(recommendation.score, "score_" + counter) + "</td>";
			tableRow += `<td><button title='${conDecDecisionGuidance.ACCEPT_DESCRIPTION}' id='row_${counter}' class='aui-button-primary aui-button accept-solution-button'>${conDecDecisionGuidance.ACCEPT_TITLE}</button>`;
			tableRow += `<button title='${conDecDecisionGuidance.DISCARD_DESCRIPTION}' id='discard_${counter}' class='aui-button-primary aui-button accept-solution-button'>${conDecDecisionGuidance.DISCARD_TITLE}</button></td>`;
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
			$("#discard_" + counter).click(function() {
				console.log("Discard clicked...");
				conDecDecisionGuidanceAPI.discardRecommendation(recommendation);
				console.log("Successfully called conDecDecisionGuidanceAPI.discardRecommendation(recommendation);");
			});
		});
		conDecAPI.showFlag("success", "#Recommendations: " + counter);
	}

	function onAcceptClicked(recommendation, parentElement) {
		conDecAPI.getKnowledgeElement(parentElement.id, parentElement.documentationLocation, (currentIssue) => {
			conDecDialog.showCreateDialog(currentIssue.id, currentIssue.documentationLocation, "Alternative", recommendation.summary, "", function(id, documentationLocation) {
				recommendation.arguments.forEach(argument => {
					conDecAPI.createDecisionKnowledgeElement(argument.summary, "", argument.type, argument.documentationLocation, id, documentationLocation, function() {
						conDecAPI.showFlag("success", "Recommendation was added successfully!");
					});
				});
			});
		});
	}

	global.conDecDecisionGuidance = new ConDecDecisionGuidance();
})(window);