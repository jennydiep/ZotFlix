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

    console.log("handleResult: populating movie info from resultData");

    // populate the movie info h3
    // find the empty h3 body by id "movie_info"
    let movieInfoElement = jQuery("#movie_info");
    let movieList = jQuery("#movieListBtn");

    let index = resultData.length -1;

    console.log(resultData[index]["movielist"]);
    movieList.append( "<button onclick=location.href='/cs122b-spring20/" + resultData[index]["movielist"]  + "'>Movie List</button>");

    // append two html <p> created to the h3 body, which will refresh the page
    movieInfoElement.append("<h1>Movie Title: " + resultData[0][0]["title"] + "</h1>" +
        "<h1>Year: " + resultData[0][0]["year"] + "</h1>");

    let starsArray = resultData[0];

    movieInfoElement.append("<h3>Rating:</h3>");
    if (resultData.length === 3)
    {
        movieInfoElement.append("<h3>" + resultData[2]["rating"] + "</h3>");
    }
    else
    {
        movieInfoElement.append("<h3>N/A</h3>")
    }
    // movieInfoElement.append("<h3>" + starsArray[0]["rating"] + "</h3>")


    // console.log("handleResult: populating movie table from resultData");
    console.log("handleResult: displaying all stars");

    let HTMLstars = "";
    movieInfoElement.append("<br><br>");
    movieInfoElement.append( "<h2>Stars: </h2>");

    for (let i = 0; i < starsArray.length; i++)
    {
        HTMLstars +=
            "<p>" +
            '<a href="single-star.html?id=' + starsArray[i]["star_id"] + '">' +
            starsArray[i]["star_name"] +
            '</a>' +
            "</p>";
    }
    movieInfoElement.append(HTMLstars);
    movieInfoElement.append("</br>");

    let HTMLgenres = "";
    movieInfoElement.append("<h2>Genres:</h2>");

    let genresArray = resultData[1];
    for (let i = 0; i < genresArray.length; i++)
    {
        // HTMLgenres += "<p>" + genresArray[i]["genre_name"] + "</p>";
        HTMLgenres += "<p>" +
            '<a href="search.html?genre=' + genresArray[i]["genre_id"] + '">' +
            genresArray[i]["genre_name"] +
            '</a>' +
            "</p>";
    }
    movieInfoElement.append(HTMLgenres);

    console.log("handling adding to cart");
    let addCartElement = jQuery("#addCart");
    let html = "<button onclick='handleAddCart(\"" + movieId + "\")'>Add to Cart</button>"
    addCartElement.append(html);
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Get id from URL
let movieId = getParameterByName('id');
let movieListBtn = jQuery("#movieListBtn");

// movieListBtn.append("<>")

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/single-movie?id=" + movieId, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});

function handleAddCart(movieId) {
    console.log("handling add to cart");
    alert("Movie added to cart");

    jQuery.ajax({
        dataType: "json", // Setting return data type
        method: "GET", // Setting request method
        url: "api/items" + "?item=" + movieId, // Setting request url, which is mapped by StarsServlet in Stars.java
    });
}