function changeFolder(el) {
	var id = $(el).siblings('input[name=id]').val();
	var updateName = $(el).siblings('input[name=updateName]').val();
	
	$.ajax({
		type: "POST", 
		url: "http://localhost:8080/update",
		data: {
			id: id,
			name: updateName
		},
		success: function (response) {
			console.log(response);
			if (response.code == 200) {
				var file = response.data;
				var idParent = file.idParent;
				var el = $('#myUL li#' + idParent + ' span');
				getDetails(el);
			}
		},
		error: function (response) {
			console.log(response);
		}
	});
}

function base64toBlob(base64Data, contentType) {
    contentType = contentType || '';
    var sliceSize = 1024;
    var byteCharacters = atob(base64Data);
    var bytesLength = byteCharacters.length;
    var slicesCount = Math.ceil(bytesLength / sliceSize);
    var byteArrays = new Array(slicesCount);

    for (var sliceIndex = 0; sliceIndex < slicesCount; ++sliceIndex) {
        var begin = sliceIndex * sliceSize;
        var end = Math.min(begin + sliceSize, bytesLength);

        var bytes = new Array(end - begin);
        for (var offset = begin, i = 0; offset < end; ++i, ++offset) {
            bytes[i] = byteCharacters[offset].charCodeAt(0);
        }
        byteArrays[sliceIndex] = new Uint8Array(bytes);
    }
    return new Blob(byteArrays, { type: contentType });
}

$(document).ready(function() {
	$('.btnDelete').click(function () {
		var id = $('form#formButton input[name=id]').val();
		
		$.ajax({
			type: "DELETE",
			url: "http://localhost:8080/delete",
			data: {
				id: id
			},
			success: function (response) {
				console.log(response);
				
				if (response.code = 200) {
					var idParent = response.data;
					if (idParent == 0) {
						location.reload(true);
					} else {
						var el = $('#myUL li#' + idParent + ' span');
						getDetails(el);
					}
				}
			},
			error: function (response) {
				console.log(response);
			}
		});
	});
	
	$('.btnDownload').click(function () {
			var id = $('form#formButton input[name=id]').val();
			
			$.ajax({
				type: "GET",
				url: "http://localhost:8080/download",
				data: {
					id: id
				},
				success: function (response) {
					console.log(response);
					
					if (response.code = 200) {
						var name = response.data[0];
						var dataFile = response.data[1];
						//Download file
						var blob = base64toBlob(dataFile);
						var link = document.createElement('a');
						link.href = window.URL.createObjectURL(blob);
						link.download = name;						
						link.click();
					}
				},
				error: function (response) {
					console.log(response);
				}
			});
		});
		
	$('#fileUpload').change(function() {
		var formData = new FormData($('form#formButton')[0]);
		
		$.ajax({
			type: "POST",
			url: "http://localhost:8080/upload/file",
			data: formData,
			processData: false,
			contentType: false,
			success: function (response) {
				console.log(response);
				
				if (response.code = 200) {
					var idParent = response.data;
					var el = $('#myUL li#' + idParent + ' span');
					getDetails(el);
				}
			},
			error: function (response) {
				console.log(response);
			}
		});
	});
	
	$('#folderUpload').change(function() {
			var formData = new FormData($('form#formButton')[0]);
			
			$.ajax({
				type: "POST",
				url: "http://localhost:8080/upload/folder",
				data: formData,
				processData: false,
				contentType: false,
				success: function (response) {
					console.log(response);
					
					if (response.code = 200) {
						var idParent = response.data;
						var el = $('#myUL li#' + idParent + ' span');
						getDetails(el);
					}
				},
				error: function (response) {
					console.log(response);
				}
			});
		});
});
