<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>Data Model</title>
    <style type="text/css">
    .node {
        stroke: #black;
        stroke-width: 2px;
        font-family: serif;
        font-size: 12px;
        color: balck;
    }

    .link {
        stroke-opacity: 1;
        font-family: serif;
        font-size: 10px;
        fill: #000000
        color: black;
    }

    </style>
    <g:javascript library="d3"/>
    <r:require modules="d3"/>
    <r:layoutResources/>
    <g:javascript library="prototype"/>
    <meta name="layout" content="main"/>
</head>

<body>
<div id="dataModel">
    <h1>Data Model</h1>

    <div class="center">
        <script>
            var data = ${data};

            var width = 780,
                    height = 600;

            var color = d3.scale.category20();

            var calculateLineId = function (d) {
                return "s" + d.source.index + "t" + d.target.index;
            }

            var force = d3.layout.force()
                    .gravity(.03)
                    .distance(200)
                    .charge(-400)
                    .size([width, height])
                    .nodes(data.nodes)
                    .links(data.links)
                    .start();

            var svg = d3.select(".center").append("svg")
                    .attr("width", width)
                    .attr("height", height);


            force.linkDistance(function () {
                width / force.nodes.length
            });

            var defs = svg.append("defs");

            var link = defs.selectAll(".link")
                    .data(data.links)
                    .enter()
                    .append("path")
                    .attr("stroke", "grey")
                    .attr("stroke-width", 2)
                    .attr("fill", "none")
                    .attr("id", function (d) {
                        return calculateLineId(d);
                    });

            svg.selectAll(".link")
                    .data(data.links)
                    .enter()
                    .append("use")
                    .attr("xlink:href", function (d) {
                        return "#" + calculateLineId(d)
                    });


            var linkText = svg.selectAll(".link")
                    .data(data.links)
                    .enter()
                    .append("text")
                    .attr("class", "link")
                    .attr("text-anchor", "middle");

            linkText
                    .append("svg:textPath")
                    .attr("xlink:href", function (d) {
                        return "#" + calculateLineId(d);
                    })
                    .attr("startOffset", "50%")
                    .text(function (d) {
                        return d.name;
                    });

            var node = svg.selectAll(".node")
                    .data(data.nodes)
                    .enter().append("g")
                    .attr("class", "node")
                    .call(force.drag);

            node.append("circle")
                    .attr("r", 60)
                    .style("fill", function (d) {
                        return color(d.colourGroup);
                    });

            node.append("text")
                    .attr("dy", ".35em")
                    .attr("text-anchor", "middle")
                    .text(function (d) {
                        return d.name;
                    });

            node.append("title")
                    .text(function (d) {
                        return d.name;
                    });

            force.on("tick", function () {
                link.attr("d", function(d) {
                    return "M" + d.source.x + " " + d.source.y + " L" + d.target.x + " " + d.target.y;
                  });

                node.attr("transform", function (d) {
                    return "translate(" + d.x + "," + d.y + ")";
                });
            });

            force.drag().on("dragstart", dragstart);

            function dragstart(d) {
                d.fixed = true;
                d3.select(this).classed("fixed", true);
            }
        </script>
    </div>
</div>
</body>
</html>
