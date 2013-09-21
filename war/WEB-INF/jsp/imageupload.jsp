<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Upload Image</title>
</head>
<body>
<form id="imageuploadform" name="imageuploadform" action="/imageupload" method="post" enctype="multipart/form-data"  >
		<input type="file" class="input"  id="image" name="image" ><br><br>
		<input type="submit"  title="Submit" value="Submit" class="button" id="butsubmit" name="addbut">
	</form>
</body>
</html>