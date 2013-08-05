<html>
	<head>
		<title><g:layoutTitle default="ReUDD" /></title>
		<link rel="shortcut icon" href="${resource(dir:'images',file:'favicon.ico')}" type="image/x-icon" />
        <link rel="stylesheet" href="${resource(dir:'css',file:'main.css')}" />
		<g:javascript library="application" />
		<g:javascript src="reudd.js" />
		<g:layoutHead />
	</head>
	<body onload="${pageProperty(name:'body.onload')}">
		<div id="wrapper">
			<div id="header">
				<g:render template="/shared/topmenu" />
			</div>
			<div id="breadcrums">
				<g:render template="/user/breadcrumbs" />
			</div>
			<div id="main">
				<div id="main-left">
					<g:render template="/shared/leftmenu" />
				</div>
				<div id="main-content">
					<g:layoutBody />
				</div>
				<div class="clear"></div>
			</div>
		</div>
	</body>
</html>