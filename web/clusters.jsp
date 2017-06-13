<%--
  Created by IntelliJ IDEA.
  User: Carlos
  Date: 4/14/2017
  Time: 11:44 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<html>
  <head>
    <title>Cluster Documents</title>


  </head>
  <body>
  <div>
      <ul>
    <c:forEach items="${lyrics}" var="item">
      <li>${item.title} - ${item.artist} - ${item.year}</li>
    </c:forEach>
      </ul>
  </div>

  </div>
  </body>
</html>
