<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>My Picasa Images</title>
</head>
<body>
	<a href="/imageupload">Upload Image</a>
	<br/><br/>
	<%
		List<List<String>> images = (List<List<String>>)request.getAttribute("images");
	%>
	<%if(images != null){ %>
	
		<%for(List<String> image : images) { %>
			<%for(String imageUrl : image) { %>
				<img alt="" src="<%=imageUrl%>" />
			<%} %>
			<br/>
		<%} %>
	
	<%} %>
</body>
</html>