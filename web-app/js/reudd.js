var ReUDD = {
    
    addAttributeRow: function() {
        var attributesDiv = $('#attributes');
        var templateDiv = $('#attributeTemplate');
        var template = templateDiv.children('.attribute');
        attributesDiv.children('.imageaddlink').before(template.clone());
        $('#attributes .attribute input.key').focus();
    },
    
    addRelationshipRow: function() {
        var relationshipsDiv = $('#relationships');
        var templateDiv = $('#relationshipTemplate');
        var template = templateDiv.children('.relationshiprow');
        relationshipsDiv.children('.imageaddlink').before(template.clone());
        $('#relationships .relationshiprow input.key').focus();
        $('#relationships .relationshiprow input.key:last').autocomplete('/ReUDD/user/getUsedRelationshipNamesList');
        
    },
    
    deleteRow: function(image) {
        var attributesDiv = image.parentNode.parentNode.parentNode;
        var attributeDiv = image.parentNode.parentNode;
        attributesDiv.removeChild(attributeDiv);
    },
    
    addToTagnames: function(type) {
        $('#' + type + 'PlusLink').fadeOut('fast',function() {
            $('#' + type + 'MinusLink').fadeIn('fast');
        })
		var tagnames = $('#tagnames')[0];
		tagnames.value = tagnames.value.replace(type + ', ',"");
        tagnames.value += type + ', ';
        $('.requiredAttributesFor' + type + ' .attribute input').removeAttr("disabled");
        $('#attributes div:first').after($('.requiredAttributesFor' + type + ' .attribute').clone());
        $('#customTypeInput').focus();
	},

    removeFromTagnames: function(type) {
        $('#' + type + 'MinusLink').fadeOut('fast',function() {
            $('#' + type + 'PlusLink').fadeIn('fast');
        })
		var tagnames = $('#tagnames')[0];
		tagnames.value = tagnames.value.replace(type + ', ',"");
		$('#customTypeInput').focus();
	},
	
	showTypeAdmin: function() {
		$('#tagnames').blur();
		$('#typeAdmin').slideDown('fast', function() {
		    $('#customTypeInput').focus();
		});
	},
	
	hideTypeAdmin: function() {
		$('#typeAdmin').slideUp('fast');
	},
	
	addNewTag: function() {
	    var input = $('#customTypeInput')[0]
	    if (input.value) {
	        var typeName = input.value.replace(' ','');
    	    var newDiv = document.createElement('div');
    	    newDiv.setAttribute('class','typeNodeItem');
        	newDiv.innerHTML = '' +
                '\t<img style="display: none;" onclick="ReUDD.removeFromTagnames(\''+typeName+'\')" alt="toggle" src="/ReUDD/images/check-small.png" id="'+typeName+'MinusLink" class="imagelink"/>\n' +
            	'\t<img onclick="ReUDD.addToTagnames(\''+typeName+'\')" alt="toggle" src="/ReUDD/images/check-grey-small.png" id="'+typeName+'PlusLink" class="imagelink"/>\n' +
            	typeName;
    	    $('#typeNodes').append(newDiv);
    	    input.value = "";
    	    ReUDD.addToTagnames(typeName)
        }
	},
	
    editRelationshipsAttributes: function(url) {
        window.location = url;
    },
    
    fillInAttributes: function() {
		var tagNames = $('#tagnames')[0].value
		$.ajax({
			url: "/ReUDD/user/getUsedAttributesHtml?types=" + tagNames,
			cache: false,
			success: function(html){
				$("#attributes").children('.inputhead').after(html);
			}
		});
		$.ajax({
			url: "/ReUDD/user/getUsedRelationshipsHtml?types=" + tagNames,
			cache: false,
			success: function(html){
				$("#relationships").children('.inputhead').after(html);
			}
		});
		
	},
	
	toggleRelEditMenu: function(DOMimage) {
	    var image = $(DOMimage);
	    var menu = image.siblings('.relEditMenu');
	    $('.relEditMenu').not(menu).hide()
	    menu.toggle();
	},
	
	setRelDirectionOut: function(DOMitem) {
	    var relRow = $(DOMitem).parents('.relationshiprow');
	    var keyInput = relRow.children('.key').children('input');
	    keyInput.attr('name', 'outRelationshipNames');
        var valueSelect = relRow.children('.value').children('select');
        valueSelect.attr('name', 'outRelationshipTargets');
        var relImage = relRow.children('.imagemiddle').children('img');
        relImage.attr('src', '/ReUDD/images/arrow-right-green-small.png');
        $('.relEditMenu').hide()
	},
    
	setRelDirectionIn: function(DOMitem) {
	    var relRow = $(DOMitem).parents('.relationshiprow');
	    var keyInput = relRow.children('.key').children('input');
	    keyInput.attr('name', 'inRelationshipNames');
        var valueSelect = relRow.children('.value').children('select');
        valueSelect.attr('name', 'inRelationshipTargets');
        var relImage = relRow.children('.imagemiddle').children('img');
        relImage.attr('src', '/ReUDD/images/arrow-left-blue-small.png');
        $('.relEditMenu').hide()
	},
	
	attributeCtrlClick: function(event) {
	    var code = event.keyCode;
        var modifierPressed = event.ctrlKey;
		if (modifierPressed && (code == 107 || code == 187)) {
			$('#addAttributeRowLink').click()
		}
	},
    
	relationshipCtrlClick: function(event) {
		var code = event.keyCode;
		var modifierPressed = event.ctrlKey;
		if (modifierPressed && (code == 107 || code == 187)) {
			$('#addRelationshipRowLink').click()
		}
	},
    
}