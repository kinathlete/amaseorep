<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>


<!DOCTYPE html>
<html>
<head>
<script src="https://code.jquery.com/jquery-3.5.1.min.js"
	integrity="sha256-9/aliU8dGd2tb6OSsuzixeV4y/faTqgFtohetphbbj0="
	crossorigin="anonymous"></script>
<meta charset="ISO-8859-1">
<title>The bidding optimization</title>
</head>
<body>

	<form action="UpdateClients" method="POST">

		<input type="submit" value="Update Clients">

	</form>
	
	<form action="AddData" method="POST">

		<input type="submit" value="Bid Opt. / Test">

	</form>
	<form action="RefreshKeywordsTargets" method="POST">

		<input type="submit" value="Pause Keywords/targets & delete from Bid_Information (Threads)">

	</form>
	
	<form action="ShowPerformanceStatus" method="POST">

		<input type="submit" value="Show Performance Status">

	</form>
	
	<form action="FillKPIsClients" method="POST">

		<input type="submit" value="Fill the KPIs in Clients Table (many Threads)">

	</form>
	
	<form action="FillKPIsClientsWithThreads" method="GET">

		<input type="submit" value="Fill the KPIs in Clients Table (Threads)">

	</form>	
	
	<form action="FillPerformanceData" method="POST">

		<input type="submit" value="Fill Performance Data">

	</form>
	
	<form action="FillPerformanceAndBidData" method="POST">

		<input type="submit" value="Fill Performance and Bid Data (Threads)">

	</form>
	
	<form action="test" method="POST">

		<input type="submit" value="test">

	</form>
	
	<form action="SemrushTest" method="POST">

		<input type="submit" value="Semrush test">

	</form>
	
	<form action="QuickOptimize" method="POST">

		<input type="submit" value="Quick optimize">

	</form>

	<script type="text/javascript">
	
		var allProfiles;

		var recur_loop = function(i, profileId) {
			var num = i || 0; // uses i if it's set, otherwise uses 0
			console.log("ioben " + num);
			console.log("PROF " + profileId);
			if (num < 2) {
				jQuery.ajax({
					url : "Optimize?profileId=" + profileId+"&targetAcos="+allProfiles[num].targetAcos+"&account="+allProfiles[num].amaseoAccount,
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
						"<p><h1>FERTIG NACH VIEEEEL ARBEIT</h1></p>");
			}
		};

		function fnc() {
			$.get("GetAllProfiles", function(responseText) {
				allProfiles = jQuery.parseJSON(responseText);
				recur_loop(0, allProfiles[0].profileId);

			});
		}
	</script>

	<input type="button" onClick="fnc()" value="MACH ALLE">
		<table id="tableOutput">
		<tr>
			<th>Profile Id</th>
			<th>Status</th>
		</tr>
	
	</table>


<div id="output"></div>


</body>
</html>