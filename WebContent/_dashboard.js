/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs two steps:
 *      1. Use jQuery to talk to backend API to get the json data.
 *      2. Populate the data to correct html elements.
 */


/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */
function handleMetaDataResult(resultData) {
    let metaDataElement = jQuery("#metadata");

    let html = "";



    for (let i = 0; i < resultData.length; i++)
    {
        html += "<h3>" + resultData[i][0]["tableName"] + "</h3>";
        for (let j = 0; j < resultData[i].length; j++)
        {
            html += "   <h5>" + resultData[i][j]["colName"] + " " + resultData[i][j]["colType"]
                +  " " + resultData[i][j]["colSize"] + "</h5>";
        }
        html += "<br><br>"
    }

    console.log(html);

    metaDataElement.append(html);

}

/**
 * Handle the data returned by IndexServlet
 * @param resultDataString jsonObject, consists of session info
 */
function handleSessionData(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);

    console.log("handle session response");
    console.log(resultDataJson);
    console.log(resultDataJson["sessionID"]);

    console.log(resultDataJson["currentUser"]);
    console.log(resultDataJson["admin"]);

    if (resultDataJson["admin"])
    {
        $("#dash").text("Tables")
    }

}


/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */

// Makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/metadata", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleMetaDataResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});

//when user logs in get session
$.ajax("api/session", {
    method: "GET",
    success: handleSessionData
});


