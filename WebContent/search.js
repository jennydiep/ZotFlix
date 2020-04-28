/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs three steps:
 *      1. Get parameter from request URL so it know which id to look for
 *      2. Use jQuery to talk to backend API to get the json data.
 *      3. Populate the data to correct html elements.
 */


/**
 * Retrieve parameter from request URL, matching by parameter name
 * @param target String
 * @returns {*}
 */
function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;

    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");

    // Ues regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */

function handleResult(resultData) {

    console.log("handleResult: populating star search info from resultData");

    // populate the star info h3
    // find the empty h3 body by id "star_info"
    let starSearchInfoElement = jQuery("#search_info");
    // append two html <p> created to the h3 body, which will refresh the page
    starSearchInfoElement.append("<h1>Search: " + resultData[0]["title"] + "</h1>");

    console.log("handleResult: populating star search table from resultData");

    // Populate the star table
    // Find the empty table body by id "movie_table_body"
    let starSearchTableBodyElement = jQuery("#search_table_body");
    let num_records = 11;

    // Concatenate the html tags with resultData jsonObject to create table rows
    for (let i = 1; i < Math.min(num_records, resultData.length); i++) {
        let stars = resultData[i]["stars"].split(',');
        let starsId = resultData[i]["stars_id"].split(',');

        let genres = resultData[i]["genres"].split(",");
        let genresId = resultData[i]["genres_id"].split(",");



        // Concatenate the html tags with resultData jsonObject
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML +=
            "<th>" +
            '<a href="single-movie.html?id=' + resultData[i]["id"] + '">'
            + resultData[i]["title"] +
            '</a>' +
            "</th>";
        rowHTML += "<th>" + resultData[i]["year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["director"] + "</th>";
        rowHTML += "<th>" + resultData[i]["genres"] + "</th>";


        let starsHTML = formatLinks(starsId, stars, "single-star.html?id=");
        rowHTML += starsHTML;
        rowHTML += "<th>" + resultData[i]["rating"] + "</th>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        starSearchTableBodyElement.append(rowHTML);
    }



}

function formatLinks(ids, names, link)
// takes the arrays and formats them into html with links
{
    let result = "";
    result += "<th>";
    for (let i = 0; i < Math.min(2, ids.length); i++)
    {
        result +=
            `<a href="${link}${ids[i]}">${names[i]}, </a>`;
    }

    if (ids.length > 2)
    {
        result +=
            `<a href="${link}${ids[2]}">${names[2]// display star_name for the link text without comma
            }</a>`;
        result += "</th>";
    }

    return result;
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Get id from URL
let title = getParameterByName('title');
let year = getParameterByName('year');
let director = getParameterByName('director');
let star = getParameterByName('star');
console.log("title: " + title);
console.log("year: " + year);
console.log("director: " + director);
console.log("star: " + star);

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/search?title=" + title + "&year=" +  year + "&director=" + director +"&star=" + star, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});