<div id="typeAdmin">
	<div id="typeNodes" class="typeNodes">
		<g:each var="item" in="${typeNodes}">
			<div class="typeNodeItem">
				<img class="imagelink" id="${item.name}MinusLink" title="Selected"
					src="${resource(dir:'images',file:'check-small.png')}" alt="toggle" 
					onClick="ReUDD.removeFromTagnames('${item.name}')" 
					<g:if test="${(node && !node.types.contains(item)) || !node}">style="display:none"</g:if> />
				<img class="imagelink" id="${item.name}PlusLink" title="Unselected"
					src="${resource(dir:'images',file:'check-grey-small.png')}" alt="toggle" 
					onClick="ReUDD.addToTagnames('${item.name}')" 
					<g:if test="${node && node.types.contains(item)}">style="display:none"</g:if> />
				${item.name}
			</div>
		</g:each>
	</div>
	<div>
		<div id="customTypeAdd" style="float:left">
			<img class="imagelink" id="customTypeAddLink" title="Add New Type"
				src="${resource(dir:'images',file:'plus.png')}" alt="Add Type"
				onClick="ReUDD.addNewTag()" onKeyPress="ReUDD.customTypeAddKeyPress(this,event)" />
			<input type="text" value="" id="customTypeInput"/>
		</div>
		<div class="imagechecklink" style="float:right">
			<img class="imagelink" title="Done"
				src="${resource(dir:'images',file:'check.png')}" alt="Done" onClick="ReUDD.hideTypeAdmin()" />
		</div>
		<script>
			$('#customTypeInput').keydown(function (event) {
				var code = event.keyCode;
				if (code == 13) { // 13:enter
					$('#customTypeAddLink').click();
					return false;
				} else if (code == 9) { // 9:tab
					$('#customTypeAddLink').click();
					ReUDD.hideTypeAdmin();
				} else if (code == 27) { // 27:escape
					ReUDD.hideTypeAdmin();
				} else if (code == 32) { //32:space
					return false;
				}
			});
		</script>
		<div class="clear"></div>
	</div>
</div>