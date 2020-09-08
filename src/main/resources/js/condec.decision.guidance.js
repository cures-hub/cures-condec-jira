(function (global) {

	let ConDecDecisionGuidance = function ConDecDecisionGuidance() {};


	/*
	 * external usage: condec.decision.table
	 */
	ConDecDecisionGuidance.prototype.addOnClickListenerForRecommendations = function () {
		$("#recommendation-button").click(function(event) {
			event.preventDefault();
			$(this).prop("disabled",true);
			const table = $("#recommendation-container tbody");
			$("#recommendation-container tbody tr").remove()
			const keyword = $("#recommendation-keyword");
			const spinner = $("#loading-spinner-recommendation");

			spinner.show();

			conDecAPI.getRecommendation(conDecAPI.getProjectKey(), keyword.val(), function(results, error) {
				let counter = 0;
				if (error === null) {
					results.forEach((recommendation) => {
						recommendation.recommendations.forEach(alternative => {
							let url = "";
							let tableRow = "";

							if (alternative.url) { url = alternative.url }
							counter += 1;
							tableRow += "<tr>";
							tableRow += "<td><a class='alternative-summary' href='" + url + "'>" + alternative.summary + "</a></td>";
							tableRow += "<td>" + recommendation.knowledgeSourceName + "</td>";
							tableRow += "<td>100%</td>";
							tableRow += "<td><button id='row_" + counter + "' class='aui-button-primary aui-button accept-solution-button'>Accept</button></td>";
							tableRow += "</tr>";
							table.append(tableRow);

							$(" #row_" + counter).click(function() {
								const currentIssue = conDecDecisionTable.getCurrentIssue();
                            	conDecDialog.showCreateDialog(currentIssue.id, currentIssue.documentationLocation, "Alternative",  alternative.summary, alternative.description);
                            });

						});
					});
					conDecAPI.showFlag("success", "Results: " +  counter);
				}
				$("#recommendation-button").prop("disabled",false);
				spinner.hide();
			});
		});

	};


	
	// export ConDecDecisionGuidance
	global.conDecDecisionGuidance = new ConDecDecisionGuidance();

})(window);