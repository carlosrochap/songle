<%--
  Created by IntelliJ IDEA.
  User: Carlos
  Date: 4/14/2017
  Time: 11:44 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
  <head>
    <title>Lyrics Detail</title>


  </head>
  <body>
  <h3>Similar Lyrics:</h3>
  <div>
      <c:forEach items="${similar}" var="item" begin="0" end="5">
          ${item.songId} <c:out value="${item.title}"/> - ${item.artist} => ${item.score}<p>
      </c:forEach>
  </div>

  <h3>Song Lyrics:</h3>
  <div>
      ${lyricsText}
  </div>

  </pre>

  </body>
</html>
