
/**
 * Functions for the search result page.
 */

var ajaxURL = "/Songle/processor";

$(function() {
	
	$("#genre_navigation li").click(function() {
		$("#genre_navigation li").removeClass("active");
		$(this).addClass("active");
	});
	
	
	$("#submitButton").click(function(){
		
		//alert("submit clicked");
		search();
		
		
	});
	
});

function search(){
	//alert("search function called");
	
	$.ajax({
		url: ajaxURL,
		type: 'POST',
		dataType: 'text',
		data: {
			Action: 'search',
			Query: 'song foo',
			Genre: 'pop'
		},
		success: function(response){	
			
			alert("response: " + response);
			
			/*
			var result = JSON.parse(response);
			var htmlResponse = "";
			
			for(var i = 0; i < 5; i++)
			{
				var r = Math.floor(Math.random()*result.length);
				htmlResponse += '<a href="?id='+ result[r]._id +'"><img src="' + result[r].movie_image_link + '" alt="Featured Movie"></a>';
			}
			$("#featuredMovies").html(htmlResponse);
			*/
		}
	});
	
}