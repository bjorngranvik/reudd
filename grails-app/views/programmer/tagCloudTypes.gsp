<html>
    <head>
        <meta name="layout" content="main"></meta>
		<g:javascript src="jquery.tagcloud.0.5.0/scripts/jquery-1.2.6.min.js" />
		<g:javascript src="jquery.tagcloud.0.5.0/scripts/jquery.tagcloud.min.js" />
		<g:javascript src="jquery.tagcloud.0.5.0/scripts/jquery.tinysort.min.js" />
		<script type="text/javascript">
			$(function(){
				$('#typelist').tagcloud({height:400,type:"sphere",sizemin:12,sizemax:100,colormin:"B0BBB0",colormax:"008000"});
			});
		</script>
    </head>
    <body>
		<div class="typeNodeTagCloud">
			<ul id="typelist">
				<g:each var="type" in="${typeNodes}">
					<li value="${type.countDataNodes()}" title="${type.calcPercentage(totalNbrOfDataNodes)} % of nodes">
						<g:link action="showType" id="${type.id}">
							${type.name}<g:if test="${!type.hasMetaModel()}"><img src="${resource(dir:'images',file:'alert-small.png')}" alt="&nbsp;[!]" /></g:if>
						</g:link>
					</li>
				</g:each>
			</ul>
		</div>
	</body>
</html>