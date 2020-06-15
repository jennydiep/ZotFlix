let payment_form = $("#payment_form");
let totalPrice = 0;
let movies = "";

function handlePaymentResult(resultData) {
    // let resultDataJson = JSON.parse(resultDataString);
    console.log("handle payment response");
    console.log(resultData);
    console.log(resultData[0]["status"]);

    if (resultData[0]["status"] === "failed")
    {
        alert(`Purchase failed`);
    }
    else
    {
        alert(`Purchase successful\n\n`
            + movies
            + "Total: $" + totalPrice);
    }
    // make ajax call to insert sale
}

function handleCartArray(ResultData)
{
    for (let i = 0; i < ResultData.length; i++)
    {
        totalPrice += ResultData[i]["price"] * ResultData[i]["quantity"];
        movies += ResultData[i]["movieTitle"] + "    "
            + ResultData[i]["quantity"] + "    $"
            + ResultData[i]["price"] + "\n";
    }
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

jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/items", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: handleCartInfo // Setting callback function to handle data returned successfully by the StarsServlet
});

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitPaymentForm(formSubmitEvent) {
    console.log("submit payment form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    formSubmitEvent.preventDefault();

    $.ajax(
        "api/payment", {
            method: "POST",
            // Serialize the login form to the data sent by POST request
            data: payment_form.serialize(),
            success: handlePaymentResult
        }
    );
}

// Bind the submit action of the form to a handler function
payment_form.submit(submitPaymentForm);

