var ws;

function connect() {
    var username = document.getElementById("username").value;

    var host = document.location.host;
    var pathname = document.location.pathname;

    ws = new WebSocket("ws://127.0.0.1:8080");

    ws.onmessage = function(event) {
    var log = document.getElementById("log");
        console.log(event.data);
        var message = JSON.parse(event.data);
        message.user_data = message.user_data != undefined ? message.user_data : {name: "System"}
        log.innerHTML += "[" + message.cmd + "] " + message.user_data.name + ":" + message.text + "\n";
    };
}

function send() {
    var content = document.getElementById("msg").value;
    var json = JSON.stringify({
        "content":content
    });

    ws.send(content);
}