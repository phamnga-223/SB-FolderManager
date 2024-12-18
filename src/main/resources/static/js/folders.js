var toggler = document.getElementsByClassName("caret");
var i;

for (i = 0; i < toggler.length; i++) {
  toggler[i].addEventListener("click", function() {
    var nestedElement = this.parentElement.querySelector(".nested")
	if(nestedElement != null) {
		nestedElement.classList.toggle("active");
	}
    this.classList.toggle("caret-down");
  });
}

function getDetails(el) {
	if ($(el).prop("tagName") == "LABEL") {
		var id = $(el).prev().val();
		el = $('#myUL li #' + id + ' span');
	}
		
	$("#myUL li").removeClass('active');
			
			if (!$(el).parent().hasClass('active')) {
				$(el).parent().addClass('active');
			} else {
				$(el).parent().removeClass('active');
			}
			
			var id = $(el).parent().attr('id');
			$.ajax({
				type: "GET",
				url: "http://localhost:8080/details",
				data: {
					id: id 
				},
				success: function (response) {
					/*
					$('#details').removeClass('d-none');
					
					// Fill data
					$('#fullPath').text(response[0]);
					var files = response[1];
					var row = $('tr.row-details:eq(0)');
					$('#table-details tbody').find('tr:gt(0)').remove();
					for(var i = 0; i < files.length; i++) {
						var file = files[i];
						var newRow = row.clone();
						newRow.children('td.name').children('label').text(file.name);
						newRow.children('td.created').text(file.created);
						newRow.children('td.updated').text(file.updated);
						newRow.removeClass('d-none');
						$('#table-details tbody').append(newRow);
					}
					*/
					
					$('#details').empty();
					$('#details').append(response);
					
					$('#myUL #' + id + ' ul').remove();
					$('#myUL #' + id).append($('#details ul'));
					$('#myUL #' + id + ' ul').show();
					
					//Form buttons
					$('form#formButton input[name=id]').val(id);
				},
				error: function (response) {
					console.log(response);
				}
			});
		}

function getDetailParent() {
	var idParent = $('input[name=idParent]').val();
	if (idParent != 0) {
		getDetails($('#myUL li#' + idParent + ' span:first'));
	} else {
		$('#details tbody').empty();		
	}
}
		
$(document).ready(function () {
	$("#myUL span").click(function() {
		getDetails(this);
	});
});
