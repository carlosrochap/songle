<%--
  Created by IntelliJ IDEA.
  User: Carlos
  Date: 4/14/2017
  Time: 11:44 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>$Title$ TEST</title>
</head>
<body> TEST
$END$ $message$

${message}
<br />
Results: ${results.size()} <br />
<c:forEach var="result" items="${results}">
    ${result.artist}: <strong>${result.title}</strong> ${result.year} => ${result.score}
    <br />
</c:forEach>

</body>
</html>
