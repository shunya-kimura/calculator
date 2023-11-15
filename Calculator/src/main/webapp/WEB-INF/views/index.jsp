<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Calculator</title>
<link rel="stylesheet" href="./css/style.css">
</head>
<body>
<form action="calculator" method="post">
  <input type="number" step="any" name="num1">
  
  <label>+</label>
  <input type="radio" name="operator" value="+">
  
  <label>-</label>
  <input type="radio" name="operator" value="-">
  
  <label>×</label>
  <input type="radio" name="operator" value="×">
  
  <label>÷</label>
  <input type="radio" name="operator" value="÷">
  
  <input type="number" step="any" name="num2">
  <input type="submit" value="=">
  
  <form action="clear" method="post">
    <input type="submit" value="Clear">
   </form>
  
  <p><%= request.getAttribute("result") %></p>
</form>

</body>
</html>