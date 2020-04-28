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

    let alphabet = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ'.split('');
    let htmlAlpha = "";

    htmlAlpha += "<a href='search.html?title=*&year=&director=&star=&genre='> * </a>"

    for (let i = 0; i < alphabet.length; i++)
    {
        htmlAlpha += "<a href='search.html?title=" + alphabet[i] +"&year=&director=&star=&genre=' > " + alphabet[i] + " </a>";
    }

    starSearchInfoElement.append(htmlAlpha);

    let genreInfoElement = jQuery("#genres");

    let htmlGenres = "";
    for (let i = 0; i < resultData.length; i++)
    {
        htmlGenres += "<a href='search.html?title=&year=&director=&star=&genre=" + resultData[i]["genre_id"] + "' > " +
            resultData[i]["genre_name"] + " </a>";
    }

    genreInfoElement.append(htmlGenres);
}


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