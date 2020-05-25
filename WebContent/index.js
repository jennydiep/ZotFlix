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
function handleStarResult(resultData) {
    let starSearchInfoElement = jQuery("#titles");
    // let offset = 10; // default offset and record values
    // let records = 10;

    let alphabet = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ'.split('');
    let htmlAlpha = "";

    htmlAlpha += "<a href='search.html?title=*'> * </a>"

    for (let i = 0; i < alphabet.length; i++)
    {
        htmlAlpha += "<a href='search.html?title=" + alphabet[i] +"'> " + alphabet[i] + " </a>";
    }

    starSearchInfoElement.append(htmlAlpha);

    let genreInfoElement = jQuery("#genres");

    let htmlGenres = "";
    for (let i = 0; i < resultData.length; i++)
    {
        htmlGenres += "<a href='search.html?genre=" + resultData[i]["genre_id"] + "' > " +
            resultData[i]["genre_name"] + " </a>";
    }

    genreInfoElement.append(htmlGenres);
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

    // // show the session information
    // $("#sessionID").text("Session ID: " + resultDataJson["sessionID"]);
    // $("#lastAccessTime").text("Last access time: " + resultDataJson["lastAccessTime"]);

}

/* ************ code for auto complete ************ */

let queries = {};

/*
 * This function is called by the library when it needs to lookup a query.
 *
 * The parameter query is the query string.
 * The doneCallback is a callback function provided by the library, after you get the
 *   suggestion list from AJAX, you need to call this function to let the library know.
 */
function handleLookup(query, doneCallback) {
    console.log("autocomplete initiated");

    // TODO: if you want to check past query results first, you can do it here
    if (queries[query] == undefined) {
        console.log("sending AJAX request to backend Java Servlet");
        // sending the HTTP GET request to the Java Servlet endpoint hero-suggestion
        // with the query data
        jQuery.ajax({
            "method": "GET",
            // generate the request url from the query.
            // escape the query string to avoid errors caused by special characters
            "url": "api/fullsearch?search=" + escape(query),
            "success": function (data) {
                // pass the data, query, and doneCallback function into the success handler
                handleLookupAjaxSuccess(data, query, doneCallback)
            },
            "error": function (errorData) {
                console.log("lookup ajax error");
                console.log(errorData)
            }
        })
    }
    else
    {
        let data = queries[query];
        console.log("sending cached result from previous autocomplete")
        handleLookupAjaxSuccess(data, query, doneCallback);
    }
}

/*
 * This function is used to handle the ajax success callback function.
 * It is called by our own code upon the success of the AJAX request
 *
 * data is the JSON data string you get from your Java Servlet
 *
 */
function handleLookupAjaxSuccess(data, query, doneCallback) {
    console.log(data);

    // TODO: if you want to cache the result into a global variable you can do it
    if (queries[query] == undefined)
    {
        queries[query] = data;
        console.log("caching autocomplete results")
    }


    // call the callback function provided by the autocomplete library
    // add "{suggestions: jsonData}" to satisfy the library response format according to
    //   the "Response Format" section in documentation
    doneCallback( { suggestions: data } );
}


/*
 * This function is the select suggestion handler function.
 * When a suggestion is selected, this function is called by the library.
 *
 * You can redirect to the page you want using the suggestion data.
 */
function handleSelectSuggestion(suggestion) {
    console.log("you select " + suggestion["value"] + " with ID " + suggestion["data"]["id"])
    let url = "single-movie.html?id=" + suggestion["data"]["id"];
    window.location.replace(url);
}


/*
 * This statement binds the autocomplete library with the input box element and
 *   sets necessary parameters of the library.
 *
 * The library documentation can be find here:
 *   https://github.com/devbridge/jQuery-Autocomplete
 *   https://www.devbridge.com/sourcery/components/jquery-autocomplete/
 *
 */
// $('#autocomplete') is to find element by the ID "autocomplete"
$('#autocomplete').autocomplete({
    // documentation of the lookup function can be found under the "Custom lookup function" section
    lookup: function (query, doneCallback) {
        handleLookup(query, doneCallback)
    },
    onSelect: function(suggestion) {
        handleSelectSuggestion(suggestion)
    },
    // set delay time
    deferRequestBy: 300,
    // there are some other parameters that you might want to use to satisfy all the requirements
    minChars: 3,

});


/*
 * do normal full text search if no suggestion is selected
 */
function handleNormalSearch(query) {
    console.log("doing normal search with query: " + query);
    // TODO: you should do normal search here
    let url = "search.html?title=" + query;
    window.location.replace(url);

}

// bind pressing enter key to a handler function
$('#autocomplete').keypress(function(event) {
    // keyCode 13 is the enter key
    if (event.keyCode == 13) {
        // pass the value of the input box to the handler function
        handleNormalSearch($('#autocomplete').val())
    }
})
// search button
$('#searchButton').click(function (event) {
    handleNormalSearch($('#autocomplete').val())
})


/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */

// Makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/genres", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleStarResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});

//when user logs in get session
$.ajax("api/session", {
    method: "GET",
    success: handleSessionData
});


