/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs two steps:
 *      1. Use jQuery to talk to backend API to get the json data.
 *      2. Populate the data to correct html elements.
 */

let star_form = $("#star_form");
let movie_form = $("#movie_form");

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
    metaDataElement.append(html);

    star_form.show();

    // document.getElementById("starName").style.visibility="visible";
    // document.getElementById("starYear").style.visibility="visible";
    // document.getElementById("starButton").style.visibility="visible";

}

/**
 * Handle the data returned by IndexServlet
 * @param resultDataString jsonObject, consists of session info
 */
function handleSessionData(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);

    console.log("handle session response");
    // console.log(resultDataJson);
    // console.log(resultDataJson["sessionID"]);
    //
    // console.log(resultDataJson["currentUser"]);
    // console.log(resultDataJson["admin"]);

    if (resultDataJson["admin"])
    {
        console.log("admin success");
        $("#dash").text("Tables");
    }
    else
    {
        console.log("access denied");
        window.location.replace("index.html");

    }

}

function submitAddStarForm(formSubmitEvent) {
    console.log("submit add star form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    formSubmitEvent.preventDefault();

    console.log(star_form.serialize());

    $.ajax(
        "api/dashboard", {
            method: "GET",
            // Serialize the login form to the data sent by POST request
            data: star_form.serialize(),
            success: handleAddStarResult
        }
    );
}

function submitAddMovieForm(formSubmitEvent) {
    console.log("submit add movie form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    formSubmitEvent.preventDefault();

    console.log(movie_form.serialize());

    $.ajax(
        "api/addMovie", {
            method: "GET",
            // Serialize the login form to the data sent by POST request
            data: movie_form.serialize(),
            success: handleAddMovieResult
        }
    );
}

function handleAddMovieResult(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);

    console.log("handle add movie response");
    if (resultDataJson["status"] === "success") {
        // window.location.replace("index.html");
        console.log("Added movie");
        window.alert("Added movie\n" +
            "movieId = " + resultDataJson["starId"] +
            ", starId = " + resultDataJson["starId"] +
            ", genreId = " + resultDataJson["genreId"]);
    }
    else
    {
        console.log("Error, movie was not added");
        window.alert("Error, movie was not added");
    }

}

function handleAddStarResult(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);

    console.log("handle add star response");
    if (resultDataJson["status"] === "success") {
        // window.location.replace("index.html");
        console.log("Added star");
        window.alert("Added Star, id = " + resultDataJson["starId"]);
    }
    else
    {
        console.log("Error, star was not added");
        window.alert("Error, star was not added");
    }

}


/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */

//when user logs in get session
$.ajax("api/session", {
    method: "GET",
    success: handleSessionData
});


// Makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/metadata", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleMetaDataResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});


// Bind the submit action of the form to a handler function
star_form.submit(submitAddStarForm);
movie_form.submit(submitAddMovieForm);
