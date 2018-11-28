var changeReplayHost = true;
var formHolder;
var pushBody;
var setCount = 0;
var revisedUrl;
var firstReplay = true;
var fileNumElement;
var fileSizeElement;
var fileNamesElement;
var uploadFiles = [];
var replayServerUrl;
var userEnteredServer;
var footer;

$(document).ready(function () {

    point();

    updateTlmFileQueue();

    formHolder.prev = formHolder.style.display;

    pushBody.prev = pushBody.style.display;

    connect();



    $('#submitButton').click(function (e) {

        submitTlmFiles(e);
    });

});



function submitTlmFiles(e) {
    ++setCount;

    revisedUrl = updateUrl();

    if (!revisedUrl){

        return;
    }

    if (firstReplay) {
        pushBody.innerHTML += "<h2>" + "Results by date and time" + "</h2>";
        firstReplay = false;
    }

    pushBody.innerHTML += "<h3>" + new Date() + "</h3><ul id='set" + setCount + "'></ul>";

    uploadFiles = [];

    updateTlmFileQueue();

        $('#submitButton').disabled = true;
        $('#fileInput').disabled = true;


    fileNamesElement.innerHTML = "";

    revisedUrl = updateUrl();

    for (var i = 0; i < uploadFiles.length; i++) {

        submitSingleTlmFile(uploadFiles[i], i, uploadFiles.length, revisedUrl);

    }
    pushBody.innerHTML += "</ul>";


}


function updateTlmFileQueue() {

    fileNumElement.innerHTML = "";
    fileSizeElement.innerHTML = "";
    fileNamesElement.innerHTML = "";




    var totalBytes = 0,
        files = document.getElementById("fileInput").files,
        numberOfFiles = files.length;
    var fileList = "</br></br>";
    for (var i = 0; i < numberOfFiles; i++) {
        var file = files[i];
        var bytes = file.size;
        uploadFiles.push(file);
        totalBytes += bytes;
        fileList += file.name + " " + bytes + " bytes" + "</br>"
    }
    var sOutput = totalBytes + " bytes";
    // optional code for multiples approximation
    for (var aMultiples = ["KiB", "MiB", "GiB", "TiB", "PiB", "EiB", "ZiB", "YiB"], nMultiple = 0,
             nApprox = totalBytes / 1024; nApprox > 1; nApprox /= 1024, nMultiple++) {
        sOutput = nApprox.toFixed(3) + " " + aMultiples[nMultiple] + " (" + totalBytes + " bytes)";
    }
    // end of optional code
    fileNumElement.innerHTML = numberOfFiles;
    fileSizeElement.innerHTML = sOutput;
    fileNamesElement.innerHTML = fileList;
}


function submitSingleTlmFile(file, current, size, url) {


    console.log("submitting file to "+url);
    var un = $('#un').val();
    var pw = $('#pwd').val();

    var creds = un + ":" + pw;

    if (!un || !pw) {
        alert("Host, Port, Username and Password are all required");
        return
    }

    var xhr = new XMLHttpRequest(),
        fd = new FormData();


    if (!un || !pw) {
        alert("both username and password are required for the server you are trying to hit are required")
    }

    fd.append("file", file);
    if (url) {
        fd.append("url", url);
    }

    fd.append("creds", creds);
    xhr.open('POST', '/replayer/replay', true);
    xhr.onreadystatechange = function (e) {

        if (this.readyState == 4) {
            var status = e.currentTarget.statusText;
            document.getElementById("fileNames").innerHTML += "<li id='file'+i>" + file.name + ": " + status + "</li>";
        }

    };
    xhr.send(fd);


}

function callGet(url) {

    showHide('otherContent', 'formHolder');

    var xhr = new XMLHttpRequest();

    xhr.open('GET', url, true);

    xhr.onreadystatechange = function (e) {

        if (this.readyState == 4) {
           otherContent.innerHTML=e.currentTarget.response.toString();
        }

    };
    xhr.send();


}

function showHide(on, off){

    var on = $('#'+on);
    var off = $('#'+off);

    on.show();
    off.hide();
    if (off === 'otherContent'){
        off.innerHTML="";
    }


}


function updateUrl() {

    var newhost = $('#hostInput').val();
    var newPort = $('#portInput').val();
    if (!newhost || !newPort){
        alert("Host, Port, Username and Password are all required");
        return null;
    }
    var http = $('#protocol').val();
    replayServerUrl = http + "://" + newhost + ":" + newPort;
    if (replayServerUrl.endsWith("/")) {

        var n = str.lastIndexOf("/");
        replayServerUrl = replayServerUrl.substr(0, n);

    }
    userEnteredServer.innerHTML = "</br>" + replayServerUrl;

    return replayServerUrl;


}

function point() {
    formHolder = document.getElementById('formHolder');
    pushBody = document.getElementById('pushResults');
    fileNumElement = document.getElementById("fileNum");
    fileSizeElement = document.getElementById("fileSize");
    fileNamesElement = document.getElementById("fileNames");
    userEnteredServer = document.getElementById("configuredUrl");
    footer = document.getElementById("footer")
}







