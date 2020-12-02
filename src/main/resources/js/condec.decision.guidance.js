(function (global) {

	let ConDecDecisionGuidance = function ConDecDecisionGuidance() {};

	ConDecDecisionGuidance.prototype.initView = function () {
        console.log("ConDecDecisionGuidance initView");
        conDecObservable.subscribe(this);
    };

	ConDecDecisionGuidance.prototype.updateView = function () {
		conDecDecisionTable.updateView();
	};

	/*
	 * external usage: condec.decision.table
	 */
	ConDecDecisionGuidance.prototype.addOnClickListenerForRecommendations = function () {
		$("#recommendation-button").click(function(event) {
			event.preventDefault();
			const currentIssue = conDecDecisionTable.getCurrentIssue();
			$(this).prop("disabled",true);
			$("#recommendation-container tbody tr").remove() //TODO the rows are kept in the cache, but they should be removed completly
			const keyword = $("#recommendation-keyword");
			const spinner = $("#loading-spinner-recommendation");

			spinner.show();
			$("#recommendation-error").hide();
			console.log(currentIssue);
			conDecAPI.getRecommendation(conDecAPI.getProjectKey(), keyword.val(), currentIssue.id, currentIssue.documentationLocation ,  function(results, error) {

				if (error === null) {
					buildRecommendationTable(results);
				}
				$("#recommendation-button").prop("disabled",false);
				spinner.hide();

			});
		});

	};

	function buildRecommendationTable(results) {
		const table = $("#recommendation-container tbody");

		let counter = 0;
		var sortedByScore = results.slice(0);
		sortedByScore.sort(function(a,b) {
			return b.score - a.score;
		});

		sortedByScore.forEach((recommendation) => {
				const alternative = recommendation.recommendation;
				let url = "";
				let tableRow = "";

				counter += 1;
				tableRow += "<tr>";
				tableRow += "<td><a class='alternative-summary' href='" + recommendation.url + "'>" + recommendation.recommendation + "</a></td>";
				tableRow += "<td><div style='display:flex;gap:3px;align-items:center;'>" + recommendation.knowledgeSourceName + "<span class='aui-icon aui-icon-small "  + recommendation.icon + "'>Knowledge Source Type</span></div></td>";
				tableRow += "<td>"+ recommendation.score +"%</td>";
				tableRow += "<td><button id='row_" + counter + "' class='aui-button-primary aui-button accept-solution-button'>" +  "Accept" + "</button></td>";
				tableRow += "<td>";
				recommendation.arguments.forEach((argument) => {
					if(argument) {
						tableRow += "<img src='" + argument.image + "'/>";
						tableRow += argument.summary;
					}
				});
				tableRow += "</td>";
				tableRow += "</tr>";
				table.append(tableRow);

				$(" #row_" + counter).click(function() {
					const currentIssue = conDecDecisionTable.getCurrentIssue();
					conDecDialog.showCreateDialog(currentIssue.id, currentIssue.documentationLocation, "Alternative",  recommendation.recommendation, "");
					/*recommendation.arguments.forEach((argument) => {

						if(argument.type == "Con") {
						  conDecAPI.createDecisionKnowledgeElement(argument.summary, "", "Con", currentIssue.documentationLocation,
										currentIssue.id, currentIssue.documentationLocation, function (id) {
											conDecObservable.notify();
										});
						}
						if(argument.type == "Pro") {
						  conDecAPI.createDecisionKnowledgeElement(argument.summary, "", "Pro", currentIssue.documentationLocation,
                                         currentIssue.id, currentIssue.documentationLocation, function (id) {
													conDecObservable.notify();
												});
						}
					})*/
				});
		});
		conDecAPI.showFlag("success", "Results: " +  counter);
		//Since the data is added later, the table must be set to sortable afterwards
		AJS.tablessortable.setTableSortable(AJS.$("#recommendation-container"));
	}


	
	// export ConDecDecisionGuidance
	global.conDecDecisionGuidance = new ConDecDecisionGuidance();

})(window);