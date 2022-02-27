/**
 * Implements the view for solution option recommendation for decision problems (=decision guidance).
 * The recommended solution options are taken from external knowledge sources, such as
 * other Jira projects or DBPedia.
 * 
 * Is referenced in HTML by
 * tabs/recommendation/decisionGuidance.vm
 */
(function(global) {

	let ConDecDecisionGuidance = function() { };

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
		let tableBody = document.getElementById("recommendation-container-table-body");
		let showDiscarded = document.getElementById("checkbox-show-discarded").checked;
		tableBody.innerHTML = "";
		let counter = 0;
		recommendations.forEach(recommendation => {
			if (!showDiscarded && recommendation.isDiscarded) {
				return;
			}
			counter++;
			let tableRow;
			if (recommendation.isDiscarded) {
				tableRow = "<tr class = \"discarded\">";
			} else {
				tableRow = "<tr>";
			}
			tableRow += "<td><a class='alternative-summary' href='" + recommendation.url + "'>" + recommendation.summary + "</a></td>";
			tableRow += "<td><div style='display:flex;gap:3px;align-items:center;'>" + recommendation.knowledgeSource.name + "<span class='aui-icon aui-icon-small " + recommendation.knowledgeSource.icon + "'>Knowledge Source Type</span></div></td>";
			tableRow += "<td>" + conDecRecommendation.buildScore(recommendation.score, "score_" + counter) + "</td>";
			if (recommendation.isDiscarded) {
				tableRow += `<td><button title='${conDecDecisionGuidance.UNDO_DESCRIPTION}' id='undo_discard_${counter}' class='aui-button-primary aui-button accept-solution-button'><span class="aui-icon aui-icon-small aui-iconfont-undo"></span>${conDecDecisionGuidance.UNDO_TITLE}</button></td>`;
			} else {
				tableRow += `<td><button title='${conDecDecisionGuidance.ACCEPT_DESCRIPTION}' id='row_${counter}' class='aui-button-primary aui-button accept-solution-button'>${conDecDecisionGuidance.ACCEPT_TITLE}</button>`;
				tableRow += `<button title='${conDecDecisionGuidance.DISCARD_DESCRIPTION}' id='discard_${counter}' class='aui-button-primary aui-button accept-solution-button'><span class="aui-icon aui-icon-small aui-iconfont-trash"></span>${conDecDecisionGuidance.DISCARD_TITLE}</button></td>`;
			}
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

			if (recommendation.isDiscarded) {
				$("#undo_discard_" + counter).click(function () {
					conDecDecisionGuidanceAPI.undoDiscardRecommendation(recommendation);
					buildRecommendationTable(recommendations, parentElement);
				});
			} else {
				$("#row_" + counter).click(function () {
					onAcceptClicked(recommendation, parentElement);
				});
				$("#discard_" + counter).click(function () {
					conDecDecisionGuidanceAPI.discardRecommendation(recommendation);
					buildRecommendationTable(recommendations, parentElement);
				});
			}

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