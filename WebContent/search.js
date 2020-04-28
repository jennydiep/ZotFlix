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

// let page = 0; // page starts at first
// let numRecords = 10; // default records is 25

function incrementPage()
{
    // page++;
    // console.log("page number: " + page);

    // if offset is already at first/last page stay on same page
    // if (offset < 0)


    offset = (parseInt(offset) + parseInt(records)).toString();
    console.log("incrementPage() offset: " + offset);

    let temp = window.location.search;
    let tempArray = temp.split("&");
    let result = "";

    for (let i = 0; i < tempArray.length; i++)
    {
        if (tempArray[i].split("=")[0] == "offset")
        {
            tempArray[i] = "offset=" + offset; // replacing previous offset with new offset
        }
    }
    result = tempArray.join("&");
    if (!result.includes("offset")) // doesn't have offset then just add it
    {
        result += "&offset=" + offset;
    }
    let url = window.location.pathname + result;
    url = decodeURIComponent(url);
    console.log(url);
    return url;
}

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

    if (offset <= 0) // to hide prev button on first page
    {
        offset = 0;
        document.getElementById("buttonPrev").style.visibility = "hidden";
    }
    else
    {
        document.getElementById("buttonPrev").style.visibility = "visible";
    }

    if (resultData.length < records) // to hide next button on last page
    {
        document.getElementById("buttonNext").style.visibility = "hidden";
    }
    else
    {
        document.getElementById("buttonNext").style.visibility = "visible";
    }

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
    let num_records = resultData.length;

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
        let genresHTML = formatLinks(genresId, genres, "search.html?title=&year=&director=&star=&genre=");
        rowHTML += genresHTML;


        let starsHTML = formatLinks(starsId, stars, "single-star.html?id=");
        rowHTML += starsHTML;


        rowHTML += "<th>" + resultData[i]["rating"] + "</th>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        starSearchTableBodyElement.append(rowHTML);
    }
    //
    // let buttonsElement = jQuery("#buttons");

    // let buttonsHTML = "";
    // buttonsHTML += "<button onclick=\"location.href=/cs122b-spring20/\" type=\"button\">\n" +
    //     "    Prev\n" +
    //     "</button><br>\n" +
    //     "<button onclick=\"location.herf=/cs122b-spring20/search.html?title=&year=&director=&star=&genre=6&offset=20/\" type=\"button\">\n" +
    //     "    Next\n" +
    //     "</button><br>\n";
    //
    // console.log(buttonsHTML);
    //
    // buttonsElement.append(buttonsHTML);

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
let genre = getParameterByName("genre");
let records = getParameterByName("records");
let offset = getParameterByName("offset");

if (offset == null)
{
    offset = "0"; // default starts of first page
}
if (records == null)
{
    records = "10"; // default has 10 records
}
if (genre == null)
{
    genre = "";
}


console.log("title: " + title);
console.log("year: " + year);
console.log("director: " + director);
console.log("star: " + star);
console.log("genre: " + genre);
// console.log("page: " + page);
// console.log("numRecords: " + numRecords);
console.log("records: " + records);
console.log("offset: " + offset);

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/search?title=" + title + "&year=" +  year + "&director=" + director +"" +
        "&star=" + star + "&offset="+ offset + "&records=" + records + "&genre=" + genre, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});