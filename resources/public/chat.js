function addMessage() {
    // builds a message JS object
    var message = {
        author: $("#author").val(),
        text: $("#text").val()
    };

    // POSTS this object to /add-message
    // the object needs to be stringified first.
    // the anonymous function will run when the POST
    // request completes.
    $.post(
        "/add-message",
        JSON.stringify(message),
        function(data) {
            $("#text").val("");
        }
    );
}

function getMessages() {
    // using jquery to issue a GET request to /get-messages
    $.get(
        "/get-messages",

        // this anonymous function will run with the request completes
        // "data" will be the returned data
        function(data) {

            // clearing out the area of the
            // page that is the messagaes
            $("#messages").empty();

            // parsing the resultant data into json
            var messages = JSON.parse(data);


            // looping through the returned messages
            // creating <div> elements
            // appending them to the messages area of the
            // page
            for (var i in messages) {
                var author = messages[i].author;
                var text = messages[i].text;
                var elem = $("<div>");
                elem.text(author + ": " + text);
                $("#messages").append(elem);
            }
        }
    );
}

// setInterval runs "getMessages" every "1000" ms
// or, ever 1 s.
setInterval(getMessages, 1000);