<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>

<script src="https://code.jquery.com/jquery-3.5.1.min.js"
	integrity="sha256-9/aliU8dGd2tb6OSsuzixeV4y/faTqgFtohetphbbj0="
	crossorigin="anonymous"></script>

<meta charset="ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
	<script type="text/javascript">
var allProfiles;
var executionCompletedFor = 0;
var counter= 0;
var totalProfiles = 0;
var totalCompletedProfiles = 0;

var numberOfThreadsAtTheSameTime = 13;
var runningThreads=0;
var start = new Date();


function fnc() {
	$.get("GetAllProfiles", function(responseText) {
		allProfiles = jQuery.parseJSON(responseText);
		totalProfiles= allProfiles.length;
		setTimeout(function(){recur_loop();}, 10000);	
	});
}

function refreshAccessTokens() {	
	if(counter == 0 || counter < totalProfiles){
		$.get("refreshaccesstokens");
		var pauseTime = 55 * 60 * 1000			
		setTimeout(function(){refreshAccessTokens();}, pauseTime);	
	}
}	

refreshAccessTokens();

var recur_loop = function() {
	for(var i=runningThreads;i<numberOfThreadsAtTheSameTime && counter<totalProfiles;i++,runningThreads++,counter++){
		var profileId = allProfiles[counter].profileId

		jQuery.ajax({
			url : "QuickOptimize?profileId="+ profileId+"&account="+allProfiles[counter].amaseoAccount,
			success : function(responseText) {
				/* console.log("iunten " + num); */
				if(responseText == 'No Permission'){
					
					totalCompletedProfiles++;
					$("#tableOutput").append(
							"<tr><td>" + profileId + "</td><td>No Permission</td></tr>")
					callRecurLoopIfNeeded();
				}else if(responseText == null){
					totalCompletedProfiles++;
					$("#tableOutput").append(
							"<tr><td>" + profileId + "</td><td>ERROR</td></tr>")
					callRecurLoopIfNeeded();
				}else {
					console.log(responseText);
					getActionStatus(responseText);
				}
				
			},
			error: function(responseText){
				totalCompletedProfiles++;
				callRecurLoopIfNeeded();
			}
		});
	} 
	
};
var getActionStatus= function(actionId){
	jQuery.ajax({
		url : "getactionstatus?actionId="+actionId,
		success : function(data) {
			
			/* console.log("Profile ID: "+data.profileId);
			console.log("Status: "+data.status); */
			if(data==undefined || data.status=='PENDING'){
				setTimeout(function(){getActionStatus(actionId);}, 10000);
			}else{
				totalCompletedProfiles++;
				console.log("Completed profiles: " + totalCompletedProfiles + "/" + totalProfiles);
				executionCompletedFor++;
				$("#tableOutput").append(
						"<tr><td>" + data.profileId + "</td><td> "
								+ data.status + "</td></tr>")
				callRecurLoopIfNeeded();
			}
		},
		error: function(responseText){
			console.log("Error:"+responseText)
		}
	});
	
}
var callRecurLoopIfNeeded = function(){
	if(counter<totalProfiles){
		//Reset execution completed count and call it for other 10 profiles
		runningThreads--;
		recur_loop();
	}
	if(totalCompletedProfiles==totalProfiles){
		var end = new Date();
		var sec = Math.abs(end - start) / 1000;
		var min = sec / 60;
		$("#output").append(
		"<p><h1>FERTIG! - Processing time: "+min+" min.</h1></p>");
		

	}
}
fnc();
console.log("hello borna");
</script>
	<table id="tableOutput">
		<tr>
			<th>Profile Id</th>
			<th>Status</th>
		</tr>

	</table>
	<div id="output"></div>

</body>
</html>