/**
 * Implements the communication with the ConDec Java REST API for creation and filtering of 
 * views on the knowledge graph.
 *
 * Requires: conDecAPI
 *
 * Is required by: conDecEvolutionPage
 * conDecTreant conDecTreeViewer conDecKnowledgePage conDecVis, conDecAdjacencyMatrix, conDecCriteriaMatrix
 *
 * Is referenced in HTML by none
 */
(function(global) {

	var ConDecViewAPI = function() {
		this.restPrefix = AJS.contextPath() + "/rest/condec/latest/view";
	};

	/**
	 * external references: condec.export
	 */
	ConDecViewAPI.prototype.getMarkdown = function(filterSettings) {
		filterSettings["projectKey"] = conDecAPI.projectKey;
		return generalApi.postJSONReturnPromise(this.restPrefix + "/markdown", filterSettings);
	};

	/**
	 * external references: condec.tree.viewer
	 */
	ConDecViewAPI.prototype.getTreeViewer = function(filterSettings, callback) {
		filterSettings["projectKey"] = conDecAPI.projectKey;
		generalApi.postJSON(this.restPrefix + "/indented-outline", filterSettings, function(error, core) {
			if (error === null) {
				callback(core);
			}
		});
	};

	/**
	 * external references: condec.treant
	 */
	ConDecViewAPI.prototype.getTreant = function(filterSettings, callback) {
		filterSettings["projectKey"] = conDecAPI.projectKey;
		generalApi.postJSON(this.restPrefix + "/treant", filterSettings, function(error, treant) {
			if (error === null) {
				callback(treant);
			}
		});
	};

	/**
	 * external references: condec.vis
	 */
	ConDecViewAPI.prototype.getVis = function(filterSettings, callback) {
		filterSettings["projectKey"] = conDecAPI.projectKey;
		generalApi.postJSON(this.restPrefix + "/node-link-diagram", filterSettings, function(error, vis) {
			if (error === null) {
				callback(vis);
			}
		});
	};

	/**
	 * If the search term is a Jira query in JQL, this function provides the
	 * filter settings matching the JQL. Otherwise it provides the default
	 * filter settings (e.g. link distance 3, all knowledge types, all link
	 * types, ...).
	 *
	 * external reference: condec.quality.check.api
	 */
	ConDecViewAPI.prototype.getFilterSettings = function(projectKey, searchTerm, callback) {
		generalApi.getJSON(this.restPrefix + "/filter-settings/" + projectKey
			+ "?searchTerm=" + searchTerm, function(error, filterSettings) {
				if (error === null) {
					callback(filterSettings);
				}
			});
	};

	/**
	 * @param isPlacedAtCreationDate elements will be placed at their creation date.
	 * @param isPlacedAtUpdatingDate elements will be placed at the date of their last update.
	 *
	 * If both isPlacedAtCreationDate and isPlacedAtUpdatingDate are true, a bar connecting
	 * both dates is shown in chronology view.
	 *
	 * external references: condec.evolution.page
	 */
	ConDecViewAPI.prototype.getEvolutionData = function(filterSettings, isPlacedAtCreationDate, isPlacedAtUpdatingDate, callback) {
		filterSettings["projectKey"] = conDecAPI.projectKey;
		generalApi.postJSON(this.restPrefix + "/chronology?isPlacedAtCreationDate=" + isPlacedAtCreationDate
			+ "&isPlacedAtUpdatingDate=" + isPlacedAtUpdatingDate, filterSettings, function(
				error, evolutionData) {
			if (error === null) {
				callback(evolutionData);
			}
		});
	};

	/**
	 * external references: condec.adjacency.matrix
	 */
	ConDecViewAPI.prototype.getAdjacencyMatrix = function(filterSettings, callback) {
		filterSettings["projectKey"] = conDecAPI.projectKey;
		generalApi.postJSON(this.restPrefix + "/adjacency-matrix", filterSettings, function(error, matrix) {
			if (error === null) {
				callback(matrix);
			}
		});
	};

	/**
	 * external references: condec.criteria.matrix
	 */
	ConDecViewAPI.prototype.getDecisionTable = function(filterSettings, callback) {
		filterSettings["projectKey"] = conDecAPI.projectKey;
		generalApi.postJSON(this.restPrefix + "/criteria-matrix", filterSettings,
			function(error, issues) {
				if (error === null) {
					callback(issues);
				}
			});
	};

	/**
	 * external references: condec.criteria.matrix
	 */
	ConDecViewAPI.prototype.getDecisionTableCriteria = function(callback) {
		generalApi.getJSON(this.restPrefix + "/decision-making-criteria/" + conDecAPI.projectKey,
			function(error, query) {
				if (error === null) {
					callback(query);
				}
			});
	};

	global.conDecViewAPI = new ConDecViewAPI();
})(window);