/**
 * This class exposes methods for general usage with APIs.
 */
(function (global) {

    const GeneralAPI = function () {
    };

    function setJsonHeaders(xhr) {
        xhr.setRequestHeader("Content-type", "application/json; charset=utf-8");
        xhr.setRequestHeader("Accept", "application/json");
        xhr.responseType = "json";
        return xhr;
    }

    function defaultResponseHandler(xhr, callback) {
        return function () {
            var status = xhr.status;
            if (status === 200) {
                callback(null, xhr.response);
            } else {
                conDecAPI.showFlag("error", xhr.response.error, status);
                callback(status);
            }
        }
    }

    GeneralAPI.prototype.getJSON = function (url, callback) {
        var xhr = new XMLHttpRequest();
        xhr.open("GET", url, true);
        xhr = setJsonHeaders(xhr);
        xhr.onload = defaultResponseHandler(xhr, callback);
        xhr.send();
    };

    GeneralAPI.prototype.getResponseAsReturnValue = function (url) {
        var xhr = new XMLHttpRequest();
        xhr.open("GET", url, false);
        xhr.setRequestHeader("Content-type", "application/json; charset=utf-8");
        xhr.send();
        return JSON.parse(xhr.response);
    };

    GeneralAPI.prototype.getJSONReturnPromise = function (url) {
        return new Promise(function (resolve, reject) {
            generalApi.getJSON(url, function (err, result) {
            	if (result === null) {
            		return;
            	}
                if (err === null) {
                    resolve(result);
                } else {
                    reject(err);
                }
            });
        });
    };

    GeneralAPI.prototype.postJSONReturnPromise = function (url, data) {
        return new Promise(function (resolve, reject) {
            generalApi.postJSON(url, data, function (err, result) {
                if (err === null) {
                    resolve(result);
                } else {
                    reject(err);
                }
            })
        })
    };

    GeneralAPI.prototype.getText = function (url, callback) {
        var xhr = new XMLHttpRequest();
        xhr.open("GET", url, true);
        xhr.setRequestHeader("Content-type", "plain/text");
        xhr.onload = defaultResponseHandler(xhr, callback);
        xhr.send();
    };

    GeneralAPI.prototype.postJSON = function (url, data, callback) {
        var xhr = new XMLHttpRequest();
        xhr.open("POST", url, true);
        xhr = setJsonHeaders(xhr);
        xhr.onload = defaultResponseHandler(xhr, callback);
        xhr.send(JSON.stringify(data));
    };

    GeneralAPI.prototype.putJSON = function (url, data, callback) {
        var xhr = new XMLHttpRequest();
        xhr.open("PUT", url, true);
        xhr = setJsonHeaders(xhr);
        xhr.onload = defaultResponseHandler(xhr, callback);
        xhr.send(JSON.stringify(data));
    };

    GeneralAPI.prototype.deleteJSON = function (url, data, callback) {
        var xhr = new XMLHttpRequest();
        xhr.open("DELETE", url, true);
        xhr = setJsonHeaders(xhr);
        xhr.onload = defaultResponseHandler(xhr, callback);
        xhr.send(JSON.stringify(data));
    };

    GeneralAPI.prototype.deleteJSONReturnPromise = function (url, data) {
        return new Promise(function (resolve, reject) {
            generalApi.deleteJSON(url, data, function (err, result) {
                if (err === null) {
                    resolve(result);
                } else {
                    reject(err);
                }
            })
        })
    };

// export GeneralAPI
    global.generalApi = new GeneralAPI();
})(window);