/**
 * This module implements the communication with the ConDec REST API for
 * automatic decision knowledge identification in Jira issue descriptions and comments.
 *
 * Is referenced in HTML by settings/classification/*.vm
 */
(function(global) {

	var ConDecTextClassificationAPI = function() {
		this.restPrefix = AJS.contextPath() + "/rest/condec/latest/classification";
	};

	ConDecTextClassificationAPI.prototype.setTextClassifierEnabled = function(isTextClassifierEnabled, projectKey) {
		generalApi.postJSON(this.restPrefix + "/setTextClassifierEnabled.json?projectKey="
			+ projectKey + "&isTextClassifierEnabled=" + isTextClassifierEnabled, null, function(error, response) {
				if (error === null) {
					conDecAPI.showFlag("success",
						"Activation of text classification for decision knowledge in Jira issue description and comments was set to "
						+ isTextClassifierEnabled + ".");
				}
			});
	};

	ConDecTextClassificationAPI.prototype.classifyText = function(text, projectKey, callback) {
		generalApi.postJSON(this.restPrefix + "/classifyText?projectKey=" + projectKey + "&text=" + text,
			null, function(error, response) {
				if (error === null) {
					callback(response.classificationResult);
				}
			});
	};

	ConDecTextClassificationAPI.prototype.classifyWholeProject = function(projectKey, animatedElement) {
		animatedElement.classList.add("aui-progress-indicator-value");
		generalApi.postJSON(this.restPrefix + "/classifyWholeProject.json?projectKey=" + projectKey,
			null,
			function(error, response) {
				animatedElement.classList.remove("aui-progress-indicator-value");
				if (error === null) {
					conDecAPI.showFlag("success", "The whole project has been classified.");
				}
			});
	};

	ConDecTextClassificationAPI.prototype.trainClassifier = function(projectKey, trainingFileName, binaryClassifierType, fineGrainedClassifierType, animatedElement) {
		animatedElement.classList.add("aui-progress-indicator-value");
		generalApi.postJSON(this.restPrefix + "/trainClassifier.json?projectKey=" + projectKey + "&trainingFileName="
			+ trainingFileName + "&binaryClassifierType=" + binaryClassifierType + "&fineGrainedClassifierType=" + fineGrainedClassifierType,
			null,
			function(error, response) {
				animatedElement.classList.remove("aui-progress-indicator-value");
				if (error === null) {
					conDecAPI.showFlag("success", "The classifier was successfully retrained.");
				}
			});
	};

	ConDecTextClassificationAPI.prototype.useTrainedClassifier = function(projectKey, trainedClassifier, isOnlineLearningActivated) {
		generalApi.postJSON(this.restPrefix + "/useTrainedClassifier.json?projectKey=" + projectKey +
			"&trainedClassifier=" + trainedClassifier + "&isOnlineLearningActivated=" + isOnlineLearningActivated,
			null,
			function(error, response) {
				if (error === null) {
					conDecAPI.showFlag("success", "The trained classifier was successfully set for the project.");
				}
			});
	};

	ConDecTextClassificationAPI.prototype.evaluateTextClassifier = function(projectKey, trainingFileName, numberOfFolds,
		binaryClassifierType, fineGrainedClassifierType, animatedElement, callback) {
		animatedElement.classList.add("aui-progress-indicator-value");
		generalApi.postJSON(this.restPrefix + "/evaluateTextClassifier.json?projectKey=" + projectKey + "&trainingFileName="
			+ trainingFileName + "&numberOfFolds=" + numberOfFolds
			+ "&binaryClassifierType=" + binaryClassifierType + "&fineGrainedClassifierType=" + fineGrainedClassifierType,
			null,
			function(error, response) {
				animatedElement.classList.remove("aui-progress-indicator-value");
				if (error === null) {
					conDecAPI.showFlag("success", "The evaluation results file was successfully created.");
					callback(response["content"]);
				}
			});
	};

	ConDecTextClassificationAPI.prototype.saveTrainingFile = function(projectKey, callback) {
		generalApi.postJSON(this.restPrefix + "/saveTrainingFile.json?projectKey=" + projectKey, null,
			function(error, response) {
				if (error === null) {
					conDecAPI.showFlag("success", "The training file was successfully created and saved in "
						+ response["trainingFile"] + ".");
					callback(response["content"]);
				}
			});
	};

	ConDecTextClassificationAPI.prototype.getNonValidatedElements = function(projectKey, issueKey) {
		var restUrl = `${this.restPrefix}/non-validated-elements/${projectKey}`;
		if (issueKey !== undefined) {
			restUrl += `/${issueKey}`;
		}
		return generalApi.getJSONReturnPromise(restUrl);
	};

	ConDecTextClassificationAPI.prototype.validateAllElements = function(projectKey, issueKey,
		callback) {
		this.getNonValidatedElements(projectKey, issueKey).then((nonValidatedSentences) => {
			for (let sentence of nonValidatedSentences) {
				this.setValidated(sentence.id, callback);
			}
		});
	};

	ConDecTextClassificationAPI.prototype.setValidated = function(id, callback) {
		const element = {
			"id": id,
			"documentationLocation": "s",
			"projectKey": conDecAPI.projectKey,
		};
		generalApi.postJSON(`${this.restPrefix}/validate`, element, function(error) {
			if (error === null) {
				conDecAPI.showFlag("success", "Classified text has been manually approved.");
				callback();
			}
		});
	};

	global.conDecTextClassificationAPI = new ConDecTextClassificationAPI();
})(window);