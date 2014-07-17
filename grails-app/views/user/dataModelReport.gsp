<html>
<head>
    <meta name="layout" content="main"/>
</head>

<body>
<div id="dataNodeList">
    <g:if test="${allNodes.size() > 0}">
        <h1>Data Model Report</h1>
        <table class="dataNodeTable reportTable">
            <tr class="title">
                <td>Type</td>
                <td>Relationship and Target Type</td>
            </tr>
            <g:each var="node" in="${allNodes}">
                <tr>
                    <td>
                        <g:if test="${node.name}">
                            <b>[${node.name}]</b>
                        </g:if>
                        <g:else>
                            <b>[Node-${node.id}]</b>
                        </g:else>
                    </td>
                    <td>
                            <g:if test="${node.getOutgoingRelationshipNames().size() > 0}">
                                <g:each var="rel" in="${node.getOutgoingRelationshipNames()}">
                                        <img src="${resource(dir: 'images', file: 'arrow-right-green-small.png')}"/>
                                        ${rel} <b>${node.getOutgoingRelationshipTargetTypeNames(rel)}</b></br>
                                </g:each>
                            </g:if>
                            <g:if test="${node.getIncomingRelationshipNames().size() > 0}">
                                <g:each var="rel" in="${node.getIncomingRelationshipNames()}">
                                        <img src="${resource(dir: 'images', file: 'arrow-left-blue-small.png')}"/>
                                        ${rel} <b>${node.getIncomingRelationshipTargetTypeNames(rel)}</b></br>
                                </g:each>
                            </g:if>
                    </td>
                </tr>
            </g:each>
        </table>
    </g:if>
    <g:else>
        There are currently no data nodes.
    </g:else>
</div>
</body>
</html>