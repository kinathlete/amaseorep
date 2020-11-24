<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>

<script src="https://code.jquery.com/jquery-3.5.1.min.js"
	integrity="sha256-9/aliU8dGd2tb6OSsuzixeV4y/faTqgFtohetphbbj0="
	crossorigin="anonymous"></script>

<style>
table, th, td {
  border: 1px solid black;
}
</style>	
	
<meta charset="ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
<script type="text/javascript">
var allProfiles;
var num = 0; 
var totalProfiles;

function fnc() {
	$.get("GetAllProfiles", function(responseText) {
		allProfiles = jQuery.parseJSON(responseText);
		totalProfiles= allProfiles.length;
		setTimeout(function(){recur_loop();}, 1000);	
		console.log(totalProfiles);
	});
}

function refreshAccessTokens() {	
	if(num == 0 || num < totalProfiles){
		$.get("refreshaccesstokens");
		var pauseTime = 55 * 60 * 1000			
		setTimeout(function(){refreshAccessTokens();}, pauseTime);	
	}
}	

refreshAccessTokens();

var recur_loop = function() {
	
	
	if (num < totalProfiles) {
		jQuery.ajax({
			url : "test?profileId="+ allProfiles[num].profileId,
			async: false,
			success : function(responseText) {
				
				var numberofKeywords = responseText;
					$("#tableOutput").append(
							"<tr><td>" + allProfiles[num].sellerName + "</td><td> "
									+ allProfiles[num].profileId + "</td><td>"
									+ numberofKeywords + "</td></tr>")
									
				console.log(num + "/" + totalProfiles)
				num++;
				if(num < totalProfiles){				
				setTimeout(function(){recur_loop();},500);
				}
				if(num == totalProfiles){
					$("#output").append(
					"<p><h1>FERTIG!</h1></p>");
				}
			}
		});
	} 
};

fnc();
console.log("hello borna");
</script>
		<table id="tableOutput">
		<tr>
			<th>Seller Name</th>
			<th>Profile Id</th>
			<th>#Keywords / Targets</th>
		</tr>
	
	</table>
<div id="output"></div>

</body>
</html>