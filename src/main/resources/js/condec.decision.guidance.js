(function (global) {




		let ConDecDecisionGuidance = function ConDecDecisionGuidance() {
		};

		ConDecDecisionGuidance.prototype.doSomething = function doSomething() {
			alert("Decision Guidance loaded");
		}

		//JQuery On Click Listener
		$(document).ready(function() {
			$("#recommendation-button").click(function(event) {
				event.preventDefault();
				const table = $("#recommendation-container");
				const keyword = $("#recommendation-keyword");

				conDecAPI.getRecommendation(conDecAPI.getProjectKey(), keyword.val(), function(results) {
					results.forEach((recommendation) => {
						recommendation.recommendations.forEach(alternative => {
							table.append("<tr><td>"+ alternative.summary + "</td><td>" + recommendation.knowledgeSourceName +"</td></tr>")
						});
					});
				});
			});
		});



	
	// export ConDecDecisionTable
	global.conDecDecisionGuidance = new ConDecDecisionGuidance();

})(window);