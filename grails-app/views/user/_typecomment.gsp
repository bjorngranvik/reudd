<style>
	#typecomment {
		background-color:#FFFFFF;
		border:1px solid;
		position:absolute;
		top:148px;
		left:420px;
		padding:10px;
		padding-bottom:0px;
		display:none;
	}
	#commentInput {
		width:250px;
		height:130px;
	}
	#spinner {
		display:none;
	}
	#error {
		display:none;
	}
</style>
<script>
	function toggleTypeComment() {
		$('#typecomment').toggle('fast');
	}
	
	function submitComment() {
		$('#spinner').fadeIn('slow');
		var comment = $('#commentInput')[0]
		var text = comment.value;
		var types = $('#tagnames')[0].value
		$.post('/ReUDD/user/addTypeCommentSubmit', {text:text,types:types}, function(data) {
			if (data=='OK') {
				$('#spinner').fadeOut('slow', function() {
					comment.value="";
					toggleTypeComment();
				});
			} else {
				$('#spinner').fadeOut('fast', function() {
					$('#error').fadeIn('fast', function() {
						$('#error').fadeOut(1500);
					});
				});
			}
		});
	}
</script>
<div id="typecomment">
	<textarea name="comment" id="commentInput"></textarea>
	<div style="text-align:right;padding:5px 0px 3px;">
		<img class="imagelink" title="Loading" id="spinner"
			src="${resource(dir:'images',file:'spinner.gif')}" alt="Spinner" />
			
		<img class="imagelink" title="Error Submitting Comment" id="error"
			src="${resource(dir:'images',file:'error.png')}" alt="Error" />
			
		<img class="imagelink" title="Submit Comment" onClick="submitComment()"
			src="${resource(dir:'images',file:'check.png')}" alt="Done" />

		<img class="imagelink" title="Close" onClick="toggleTypeComment()"
			src="${resource(dir:'images',file:'close.png')}" alt="Close" />
	</div>
</div>