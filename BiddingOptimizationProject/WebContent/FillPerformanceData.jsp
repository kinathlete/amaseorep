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

function fnc() {
	$.get("GetAllProfiles", function(responseText) {
		allProfiles = jQuery.parseJSON(responseText);
		recur_loop(0, allProfiles[0].profileId);
	});
}

function move() {
	var id = setInterval(frame, 1000);
	function frame() {
		console.log("test")
	}
	
}
move();

var recur_loop = function(i, profileId) {
	var num = i || 0; // uses i if it's set, otherwise uses 0
	console.log("ioben " + num);
	console.log("PROF " + profileId);
	if (num < 10 /*allProfiles.length*/) {
		jQuery.ajax({
			url : "FillPerformanceData?profileId="+ profileId+"&account="+allProfiles[num].amaseoAccount,
			success : function(responseText) {
				console.log("iunten " + num);
				if (responseText !== null) {
					console.log(responseText);
					$("#tableOutput").append(
							"<tr><td>" + profileId + "</td><td> "
									+ responseText + "</td></tr>")
				}
				recur_loop(num + 1, allProfiles[num + 1].profileId);
			}
		});
	} else {
		$("#output").append(
				"<p><h1>FERTIG!</h1></p>");
	}
};

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