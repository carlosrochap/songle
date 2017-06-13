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
    <title>$Title$</title>
    <script src="/resources/js/jquery-3.2.1.min.js"></script>

    <script type="text/javascript">

        $(function () {
            function doSearch () {
                $.ajax({
                    dataType: "json",
                    method: "POST",
                    url: "/search",
                    data: { query: $("#query").val()} //

                }).done(function( data ) {
//                    console.log(results);
                    var results = $("#results");
                    results.html("");
                    $("#results-found").html("Results Found: "+data['results_count']);
                    for(var x in data['results']){

                        var row = data['results'][x];//,
//                            html =
//                            item = $("<p>").text(html.prop('outerHTML'));

                        $("<a>").attr("href", "/lyrics?song_id="+row.songId).html(row.songId+": "+row.title + " - " +row.artist+ " "+ row.year+" - "+row.genre+" => "+row.score)
                            .wrap("<p>").parent().appendTo(results);
//                        results.append(item);
                    }

//                    if(data['suggestion'] !== ""){typeof foo !== 'undefined'
                    if (typeof data.suggestion !== 'undefined'){
                        console.log('inside');
                        $("#fixed-query").html(data['suggestion']);
                        $("#query-suggestion").css("display", "block");
                    } else {
                        $("#query-suggestion").css("display", "none");
                    }

                });
            }
            $("#doSearch").on( "click", function() {
                doSearch();
            });

            $("#fixed-query").on("click", function () {
                $("#query").val($("#fixed-query").text());
                $("#query-suggestion").css("display", "none");
                doSearch();
            });

        });

    </script>
  </head>
  <body>
  ${message}
  <div>
      <input type="text" name="query" id="query" size="40" value="Flood pressure climbs at a dramatic rate" />
      <input type="button" id="doSearch" value="Search"/>
  </div>
  <div>
      <ul>
    <c:forEach items="${clusters}" var="cluster">
      <li><a href="/clusters/?cluster_id=${cluster.key}"> ${cluster.value.get(1)} (${cluster.value.get(0)})</a></li>
    </c:forEach>
      </ul>
  </div>
  <div id="results-found"></div>
  <div id="query-suggestion" style="display: none">Did  you mean: <strong><a id='fixed-query' href='#'></a></strong></div>
  <div id="results">

  </div>
  </body>
</html>
