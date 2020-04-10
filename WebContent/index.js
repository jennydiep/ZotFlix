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
    console.log("handleStarResult: populating star table from resultData");

    // Populate the star table
    // Find the empty table body by id "star_table_body"
    let movieTableBodyElement = jQuery("#movie_table_body");

    // Iterate through resultData, no more than 10 entries
    for (let i = 0; i < Math.min(20, resultData.length); i++) {

        let stars = resultData[i]["movie_stars"];
        let starsArray = stars.split(',');

        let starsId = resultData[i]["movie_stars_id"];
        let starsIdArray = starsId.split(',');

        // Concatenate the html tags with resultData jsonObject
        let rowHTML = "";
        rowHTML += "<tr>";
        // rowHTML +=
        //     "<th>" +
        //     // Add a link to single-star.html with id passed with GET url parameter
        //     // '<a href="single-star.html?id=' + resultData[i]['star_id'] + '">'
        //     // + resultData[i]["star_name"] +     // display star_name for the link text
        //     // '</a>' +
        //     + resultData[i]["movie_title"] +
        //     "</th>";

        // rowHTML += "<th>" + resultData[i]["movie_title"] + "</th>";
        rowHTML +=
        "<th>" +
        '<a href="single-movie.html?id=' + resultData[i]["movie_id"] + '">'
        + resultData[i]["movie_title"] +
        '</a>' +
        "</th>";
        rowHTML += "<th>" + resultData[i]["movie_year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_director"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_genres"] + "</th>";

        rowHTML += "<th>";
        for (let j = 0; j < Math.min(2, starsArray.length); j++)
        {
            rowHTML +=
                '<a href="single-star.html?id=' + starsIdArray[j] + '">'
                + starsArray[j] +  ", "  + // display star_name for the link text
                '</a>'
        }
        rowHTML +=
            '<a href="single-star.html?id=' + starsIdArray[2] + '">'
            + starsArray[2]  + // display star_name for the link text
            '</a>'
        rowHTML += "</th>";


        rowHTML += "<th>" + resultData[i]["movie_rating"] + "</th>";
        // rowHTML += "<th>" + resultData[i]["movie_director"] + "</th>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);
    }
}


/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */

// Makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/movies", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleStarResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});