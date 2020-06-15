let cart = $("#cart");

/**
 * Handle the items in item list
 * @param resultDataString jsonObject, needs to be parsed to html
 */
function handleCartArray(resultDataString) {

    let item_list = $("#item_list");
    // change it to html list
    let res = "<ul>";
    for (let i = 0; i < resultDataString.length; i++) {
        // each item will be in a bullet point
        console.log("cart: " + resultDataString[i]["movieTitle"]);
        let movieId = '"' + resultDataString[i]["movieId"] + '"';
        res += "<li>" + resultDataString[i]["movieTitle"] + "  quantity: " + resultDataString[i]["quantity"] + "    $" + resultDataString[i]["price"] +
            "    <button onclick='handleAddCart(" + movieId + ")'>+</button>  " +
            "  <button onclick='handleDecreaseItem(" + movieId + ")'>-</button>" +
            "  <button onclick='handleDeleteItem(" + movieId + ")'>Delete</button>" + "</li>";
    }
    res += "</ul>";

    // clear the old array and show the new array in the frontend
    item_list.html("");
    item_list.append(res);
}

/**
 * Submit form content with POST method
 * @param cartEvent
 */
function handleCartInfo(cartEvent) {
    console.log("submit cart form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    // cartEvent.preventDefault();
    $.ajax("api/items", {
        method: "GET",
        url: "api/items",
        success: handleCartArray
    });

}


// Bind the submit action of the form to a event handler function
// cart.submit(handleCartInfo);

jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/items", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: handleCartInfo // Setting callback function to handle data returned successfully by the StarsServlet
});

function handleAddCart(movieId)
{
    console.log("handling increasing quantity");

    jQuery.ajax({
        dataType: "json", // Setting return data type
        method: "GET", // Setting request method
        url: "api/items" + "?item=" + movieId, // Setting request url, which is mapped by StarsServlet in Stars.java
    });
}

function handleDeleteItem(movieId)
{
    console.log("handling deleting item");
    jQuery.ajax({
        dataType: "json", // Setting return data type
        method: "GET", // Setting request method
        url: "api/deleteitem" + "?item=" + movieId, // Setting request url, which is mapped by StarsServlet in Stars.java
    });
}

function handleDecreaseItem(movieId)
{
    console.log("handling decreasing item");
    jQuery.ajax({
        dataType: "json", // Setting return data type
        method: "GET", // Setting request method
        url: "api/decreaseitem" + "?item=" + movieId, // Setting request url, which is mapped by StarsServlet in Stars.java
    });
}