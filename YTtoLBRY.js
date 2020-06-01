console.log("YouTube To LBRY finder!");
const fs = require('fs');
var XMLHttpRequest = require("xmlhttprequest").XMLHttpRequest;
var request = new XMLHttpRequest();
var ytChannelsString = "";
var lbryChannelsString = "";
let lbryArray = [];
let tempJson = {"0":0};
function getFile() {
let filePath='';
const path1 = './subscription_manager';
const path2 = './subscription_manager.xml';
	if (fs.existsSync(path1)){
		filePath = path1;
	} else {
		filePath = path2;
	}
            console.log(filePath);
    content = fs.readFileSync(filePath, 'utf8');
    content = content.replace('<opml version="1.1">','');
    content = content.replace('<body>','');
    content = content.replace('/><outline','');
    content = content.replace('</outline></body></opml>',' ');
    splitChannels = content.split("=");
    toCheck = [];
    for (var i = 0; i <= splitChannels.length-1; i++) {
        tempChannel = splitChannels[i];
        if (tempChannel.indexOf("outline text")>=29) {
            toCheck[toCheck.length] = tempChannel.slice(0, tempChannel.indexOf("outline text")-5);
        }
    }
}
function lbryAPIrequest() {
var lbryCounter = 0;
var tempStringArray = [];
var tempSplit;
    for (var x = 0; x <= toCheck.length-1; x++) {
        ytChannelsString = ytChannelsString + "," + toCheck[x];
        }
        ytChannelsString = ytChannelsString.slice(1, ytChannelsString.length);
    request.open("GET", 'https://api.lbry.com/yt/resolve?channel_ids={'+ytChannelsString+'}')
    request.send();
    request.onload = () => {
        //console.log(request);
        if (request.status == 200) {
            tempJson = JSON.parse(request.responseText);
            lbryChannelsString = JSON.stringify(tempJson.data.channels).split(",");
	    for (var l = 0; l <= lbryChannelsString.length-1; l++) {
	    if (lbryChannelsString[l].includes("null") != true) {
			lbryArray[lbryCounter] = lbryChannelsString[l];
			lbryCounter++;
		}
	    }

	    for (var w = 0; w <= lbryArray.length-1; w++) {
		tempSplit = lbryArray[w].split(":");
		if (tempSplit[0].includes("@") == true) {
			tempStringArray.push(tempSplit[0]);
		} else { 
			tempStringArray.push(tempSplit[1]);
		}
	    }
	    for (var k = 0; k <= tempStringArray.length-1; k++) {
		tempStringArray[k] = tempStringArray[k].replace('"','');
		tempStringArray[k] = "lbry://"+tempStringArray[k];
		fs.appendFile('LBRY.txt', tempStringArray[k].slice(0, tempStringArray[k].length-1) +"\n", function (err) {
		if (err) throw err;
		//console.log('Saved!');
		}); 
		}
            console.log(tempStringArray);
            console.log(lbryCounter+" Channels found!");
        } else {
            console.log('error ${request.status} ${request.statusText}')
        }
    }
}
getFile();
lbryAPIrequest();
    //console.log(lbryChannelsString);// doesnt work
