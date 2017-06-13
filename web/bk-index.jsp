<%--
  Created by IntelliJ IDEA.
  User: Carlos
  Date: 4/14/2017
  Time: 11:44 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
      <meta charset="UTF-8">
      <title>Songle - A lyric search engine.</title>
      <link rel="stylesheet" type="text/css" href="/resources/css/style.css" />
      <link rel="stylesheet" type="text/css" href="/resources/css/font-awesome-4.7.0/css/font-awesome.min.css" />
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
                    for(var x in data['results']){

                        var row = data['results'][x];//,
//                            html =
//                            item = $("<p>").text(html.prop('outerHTML'));

                        $("<a>").attr("href", "/lyrics?song_id="+row.songId).html(row.songId+": "+row.title + " - " +row.artist+ " "+ row.year+" - "+ row.genre)
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
  <div id="query-suggestion" style="display: none">Did  you mean: <strong><a id='fixed-query' href='#'></a></strong></div>
  <div id="results">

  </div>

  <div class="logo_index">
      <span class="blueFont">S</span><span class="redFont">o</span><span class="yelloFont">n</span><span class="blueFont">g</span><span class="greenFont">l</span><span class="redFont">e</span>
  </div>
  <p class="logo_subtitle">A lyrics search engine.</p>

  <form class="searchForm" action="result.jsp" method="post">
      <input class="searchBar" type="text" name="searchParam">
      <input class="submitButton" type="submit" value="Search">
  </form>
  </body>
</html>