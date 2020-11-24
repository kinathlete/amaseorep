<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Optimization done</title>
</head>
<body>
	<h1>The old Biddings:</h1>
	<table>
		<tr>
			<th>ID</th>
			<th>Bidding</th>
			<th>Keyword</th>
			<th>ACOS</th>
			<th>Costs</th>
			<th>Revenue</th>
			<th>Clicks</th>
			<th>Orders</th>
			<th>Impressions</th>
		</tr>
		<c:forEach items="${oldArray}" var="bulkRow">
			<tr>
				<td>${bulkRow.id}</td>
				<td>${bulkRow.bidding}</td>
				<td>${bulkRow.keyword}</td>
				<td>${bulkRow.acos}</td>
				<td>${bulkRow.costs}</td>
				<td>${bulkRow.revenue}</td>
				<td>${bulkRow.clicks}</td>
				<td>${bulkRow.orders}</td>
				<td>${bulkRow.impressions}</td>
			</tr>
		</c:forEach>
	</table>

	<h1>The new Biddings</h1>
	<table>
		<tr>
			<th>ID</th>
			<th>Bidding</th>
		</tr>
		<c:forEach items="${newArray}" var="bulkRow">
			<tr>
				<td>${bulkRow.id}</td>
				<td>${bulkRow.bidding}</td>
			</tr>
		</c:forEach>
	</table>

</body>
</html>