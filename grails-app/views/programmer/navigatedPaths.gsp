<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <g:javascript library="d3"/>
    <r:require modules="d3"/>
    <r:layoutResources/>
    <g:javascript src="graph-renderer.js"/>
    <g:javascript src="jquery-1.3.2.min.js"/>
    <meta name="layout" content="main"/>
</head>

<body>
<div class="nodePaths">
    <h1>Navigated Paths</h1>

    <div class="center">
        <script language="JavaScript">
            renderGraph(${data});
        </script>
    </div>
</div>
</body>
</html>
