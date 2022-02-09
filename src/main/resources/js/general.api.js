/**
 * This class exposes methods for general usage with APIs.
 */
(function(global) {

	const GeneralAPI = function() {
	};

	GeneralAPI.prototype.getJSON = function(url, callback) {
		createRequest("GET", url, "", callback);
	};

	GeneralAPI.prototype.getResponseAsReturnValue = function(url) {
		var xhr = new XMLHttpRequest();
		xhr.open("GET", url, false);
		xhr.setRequestHeader("Content-type", "application/json; charset=utf-8");
		xhr.send();
		return JSON.parse(xhr.response);
	};

	GeneralAPI.prototype.postJSONReturnText = function(url, data, callback) {
		xhr = createRequest("POST", url, data, callback);
		xhr.responseType = "text";
	};

	GeneralAPI.prototype.postJSON = function(url, data, callback) {
		console.log("Creating requrest: "+url+", "+data+", "+callback);
		createRequest("POST", url, data, callback);
	};

	GeneralAPI.prototype.putJSON = function(url, data, callback) {
		createRequest("PUT", url, data, callback);
	};

	GeneralAPI.prototype.deleteJSON = function(url, data, callback) {
		createRequest("DELETE", url, data, callback);
	};

	GeneralAPI.prototype.getJSONReturnPromise = function(url) {
		return new Promise(function(resolve, reject) {
			generalApi.getJSON(url, function(err, result) {
				if (err === null) {
					resolve(result);
				} else {
					reject(err);
				}
			});
		});
	};

	GeneralAPI.prototype.postJSONReturnPromise = function(url, data) {
		return new Promise(function(resolve, reject) {
			generalApi.postJSON(url, data, function(err, result) {
				if (err === null) {
					resolve(result);
				} else {
					reject(err);
				}
			})
		})
	};

	GeneralAPI.prototype.deleteJSONReturnPromise = function(url, data) {
		return new Promise(function(resolve, reject) {
			generalApi.deleteJSON(url, data, function(error, result) {
				if (error === null) {
					resolve(result);
				} else {
					reject(error);
				}
			})
		})
	};
	
	GeneralAPI.prototype.postJSONReturnTextPromise = function(url, data) {
		return new Promise(function(resolve, reject) {
			generalApi.postJSONReturnText(url, data, function(err, result) {
				if (err === null) {
					resolve(result);
				} else {
					reject(err);
				}
			})
		})
	};

	function createRequest(requestType, url, data, callback) {
		var xhr = new XMLHttpRequest();
		xhr.open(requestType, url, true);
		xhr = setJsonHeaders(xhr);
		xhr.onload = handleResponse(xhr, callback);
		xhr.send(JSON.stringify(data));
		return xhr;
	}

	function setJsonHeaders(xhr) {
		xhr.setRequestHeader("Content-type", "application/json; charset=utf-8");
		xhr.setRequestHeader("Accept", "application/json");
		xhr.responseType = "json";
		return xhr;
	}

	function handleResponse(xhr, callback) {
		return function() {
			var status = xhr.status;
			if (status === 200) {
				callback(null, xhr.response);
			} else {
				if (xhr !== null && xhr.response !== null) {
					conDecAPI.showFlag("error", xhr.response.error, status);
				}
				callback(status);
			}
		}
	}

	global.generalApi = new GeneralAPI();
})(window);