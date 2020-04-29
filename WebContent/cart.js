let cart = $("#cart");

/**
 * Handle the items in item list
 * @param resultDataString jsonObject, needs to be parsed to html
 */
function handleCartArray(resultDataString) {
    const resultArray = resultDataString.split(",");
    console.log("cart: " + resultArray);
    let item_list = $("#item_list");
    // change it to html list
    let res = "<ul>";
    for (let i = 0; i < resultArray.length; i++) {
        // each item will be in a bullet point
        res += "<li>" + resultArray[i] + "</li>";
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


}


// Bind the submit action of the form to a event handler function
// cart.submit(handleCartInfo);

$.ajax("api/items", {
    method: "GET",
    url: "api/items",
    success: handleCartArray
});