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
			url : "ShowPerformanceStatus?profileId="+ allProfiles[num].profileId,
			async: false,
			success : function(responseText) {
				
				var KPIs = jQuery.parseJSON(responseText);
				var targetAcos = allProfiles[num].targetAcos * 100.0;
				var acosLastMonth = KPIs.acos1MonthAgo;
				var revenueLastMonth = KPIs.revenue1MonthAgo;
				
				var caution3Days = "";
				var caution7Days = "";

				if(KPIs.acosLast3Days > targetAcos + 5){
					caution3Days = "acos";
				}else if(revenueLastMonth > 0 && KPIs.acosLast3Days > acosLastMonth + 5){
					caution3Days = "acos";
				}else if(KPIs.revenueLast3Days < 33){
					caution3Days = "revenue";				
				}else if(revenueLastMonth > 0 && KPIs.revenueLast3Days < revenueLastMonth * 0.7){
					caution3Days = "revenue";
				}else{
					
				}
				
				if(KPIs.acosLast7Days > targetAcos + 5){
					caution7Days = "acos";
				}else if(revenueLastMonth > 0 && KPIs.acosLast7Days > acosLastMonth + 5){
					caution7Days = "acos";
				}else if(KPIs.revenueLast7Days < 33){
					caution7Days = "revenue";				
				}else if(revenueLastMonth > 0 && KPIs.revenueLast7Days < revenueLastMonth * 0.7){
					caution7Days = "revenue";
				}else{
					
				}
				
					$("#tableOutput").append(
							"<tr><td>" + allProfiles[num].sellerName + "</td><td> "
									+ allProfiles[num].marketplace + "</td><td>"
									+ allProfiles[num].kam + "</td><td>"
									+ targetAcos + "</td><td>"
									+ KPIs.revenueLast3Days + "</td><td>"
									+ KPIs.acosLast3Days + "</td><td>"
									+ caution3Days + "</td><td>"
									+ KPIs.revenueLast7Days + "</td><td>"
									+ KPIs.acosLast7Days + "</td><td>"
									+ caution7Days + "</td><td>"
									+ KPIs.revenueThisMonth + "</td><td>"
									+ KPIs.acosThisMonth + "</td><td>"
									+ KPIs.revenue1MonthAgo + "</td><td style=\"font-weight:bold\">"
									+ KPIs.acos1MonthAgo + "</td><td>"
									+ KPIs.revenue2MonthsAgo + "</td><td>"
									+ KPIs.acos2MonthsAgo + "</td><td>"
									+ KPIs.revenue3MonthsAgo + "</td><td>"
									+ KPIs.acos3MonthsAgo + "</td><td>"
									+ allProfiles[num].comments + "</td></tr>")
									
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
			<th>Marketplace</th>
			<th>KAM</th>
			<th>Target Acos</th>
			<th>Revenue last 3 days</th>
			<th>Acos last 3 days</th>
			<th>Caution?</th>
			<th>Revenue last 7 days</th>
			<th>Acos last 7 days</th>
			<th>Caution?</th>
			<th>Revenue this month</th>
			<th>Acos this month</th>
			<th>Revenue last month</th>
			<th>Acos last month</th>
			<th>Revenue 2 months ago</th>
			<th>Acos 2 months ago</th>
			<th>Revenue 3 months ago</th>
			<th>Acos 3 months ago</th>
			<th>Comments</th>
		</tr>
	
	</table>
<div id="output"></div>

</body>
</html>