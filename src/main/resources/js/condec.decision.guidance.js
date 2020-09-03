(function (global) {

	let ConDecDecisionGuidance = function ConDecDecisionGuidance() {};

	// TODO Please try to put as much HTML code as possible into velocity template
	const acceptButton = "<button class='aui-button-primary aui-button'>Accept</button>";
	const rejectButton = "<button class='aui-button'>Reject</button>";

	/*
	 * external usage: condec.jira.issue.module
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
							if (alternative.url) { url = alternative.url }
							counter += 1;
							table.append("<tr><td><a href='" + url + "'>" + alternative.summary + "</a></td><td>" + recommendation.knowledgeSourceName +"</td><td>100%</<td><td>" + acceptButton + rejectButton + "</td></tr>")
						});
					});
					conDecAPI.showFlag("success", "Results: " +  counter);
				}
				$("#recommendation-button").prop("disabled",false);
				spinner.hide();
			});
		});
	};
	
	// export ConDecDecisionTable
	global.conDecDecisionGuidance = new ConDecDecisionGuidance();

})(window);