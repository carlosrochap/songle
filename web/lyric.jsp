<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<title>Songle - A lyric search engine.</title>
		<link rel="stylesheet" type="text/css" href="assets/css/style.css" />
		<link rel="stylesheet" type="text/css" href="assets/css/font-awesome-4.7.0/css/font-awesome.min.css" />
		<script src="https://code.jquery.com/jquery-3.1.1.min.js" integrity="sha256-hVVnYaiADRTO2PzUGmuLJr8BLUSjGIZsDYGmIJLv2b8=" crossorigin="anonymous"></script>
		<script>
			$(function() {
				
				//alert('asdf');
			});
		</script>
	</head>
	
	<body>
		<div id="search_bar_result">
			<a href="index.html">
				<div class="logo_result">
					<span class="blueFont">S</span><span class="redFont">o</span><span class="yelloFont">n</span><span class="blueFont">g</span><span class="greenFont">l</span><span class="redFont">e</span>
				</div>
			</a>
			<form class="resultSearchForm" action="result.jsp" method="post">
				<input class="searchBar" type="text" name="searchParam">
				<input class="submitButton" type="submit" value="Search">
			</form>
		</div>
		
		<div class="content_wrapper">
		
			<div class="lyric_wrapper">
				<!-- <h1 style="display: inline-block"> </h1>-->
				
				<h2><i style="font-size:20px;" class="fa fa-music" aria-hidden="true"></i> you-are-my-rock - beyonce-knowles</h2><br />
				
				<p><span>Year: </span> 2009 <span style="margin-left: 150px">Genre: </span> Pop</p><br /> 
							
				<pre class="lyrics">
If I wrote a book about where we stand
Then the title of my book would be ""Life with Superman""
That's how you make me feel I count you as a privilege
This love is so ideal
I'm honored to be in it
I know you feel the same I see it everyday
In all the things you do
In all the things you say
[Chorus:]
You are my rock
Baby you're the truth
You are my rock
I love to rock with you
You are my rock
You're everything I need
You are my rock
So baby rock with me ......
				</pre>
				<h1><a href="https://www.youtube.com/results?search_query=purple+rain" target="_blank"><i class="fa fa-youtube-play" aria-hidden="true"></i></a></h1>
			</div>
		</div>
		
		<div id="footer">
			<div class="logo_footer">
				<span class="blueFont">S</span><span class="redFont">o</span><span class="yelloFont">n</span><span class="blueFont">g</span><span class="greenFont">l</span><span class="redFont">e</span>
			</div>
			<p class="logo_subtitle" style="color:#fff">A lyrics search engine.</p>
		</div>
		
		
		
		
		
	</body>
</html>