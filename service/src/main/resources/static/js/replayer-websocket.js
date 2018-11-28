/**
 * Created by alice.martin
 * Developer: alice.martin
 * Date: 1/19/17
 * Time: 10:57 AM
 * Description: WebSocket connection code
 */

var stompClient = null;

var stompFailureCallback = function (error) {
    console.error('STOMP: ' + error);
    setTimeout(connect, 10000);
    console.error('STOMP: Reconecting in 10 seconds');
    footer.style.background="red";

    footer.innerHTML="Server disconnected... reconnecting in 10 seconds...";
};

function connect() {
    var socket = new SockJS('/websocket');
    socket.onerror
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        footer.style.background="#215649";
        footer.innerHTML="Server connected...";

        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/response', function (result) {

            displayResponse(JSON.parse(result.body))
        }, function (error) {

            displayResponse(error);
        });
    }, stompFailureCallback);




}



function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#log").html("");
}


function displayResponse(obj) {


    var html = "<li>";

    Object.keys(obj).forEach(function (key) {

        if (key === "stacktrace") {
            console.log(obj[key]);
            html += "<i class='error'>Check console for error details</i><br>";
        } else {
            var lineItem = obj[key].toString();
            html += "<b style='color:#0c7658'>" + key + ":" + "</b>" + lineItem + "<br>";
        }


    });
    html += "</li>";
    html += "<br/>";

    var ulId = "#set" + setCount;

    $(ulId).append(html);
}



