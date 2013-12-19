/*
 * Copyright (c) 2009-2013 Björn Granvik & Jonas Andersson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
var renderGraph = function (data) {
  var width = 780,
    height = 600;
  var color = d3.scale.category20();

  var lineId = function (d) {
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

  force.linkDistance(function () {
    width / force.nodes.length
  });

  var svg = d3.select(".center").append("svg")
    .attr("width", width)
    .attr("height", height);

  var defs = svg.append("defs");

  defs.append("marker")
    .attr("id", "arrow")
    .attr("markerUnits", "strokeWidth")
    .attr("orient", "auto")
    .attr("markerWidth", 15)
    .attr("markerHeight", 10)
    .attr("viewBox", "0 0 10 10")
    .attr("refX", 10)
    .attr("refY", 5)
    .append("polyline")
    .attr("points", "0,0 10,5 0,10 1,5")
    .attr("fill", "black");

  var link = defs.selectAll(".link")
    .data(data.links)
    .enter()
    .append("path")
    .attr("stroke", "black")
    .attr("stroke-width", 1)
    .attr("fill", "none")
    .attr("marker-end", "url(#arrow)")
    .attr("id", function (d) {
      return lineId(d);
    });

  svg.selectAll(".link")
    .data(data.links)
    .enter()
    .append("use")
    .attr("xlink:href", function (d) {
      return "#" + lineId(d)
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
      return "#" + lineId(d);
    })
    .attr("startOffset", "50%")
    .text(function (d) {
      return d.name;
    });

  var nodeRadius = 40;
  var node = svg.selectAll(".node")
    .data(data.nodes)
    .enter().append("g")
    .attr("class", "node")
    .call(force.drag);

  node.append("circle")
    .attr("r", nodeRadius)
    .style("fill", "#FFFFFF");

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
    link.attr("d", function (d) {
      var startPointCoordinates = findNodeBoundary(d.target, d.source, nodeRadius);
      var endPointCoordinates = findNodeBoundary(d.source, d.target, nodeRadius);
      return "M" + startPointCoordinates[0] + " " + startPointCoordinates[1] + " L" + endPointCoordinates[0] + " " + endPointCoordinates[1];
    });

    node.attr("transform", function (d) {
      return "translate(" + d.x + "," + d.y + ")";
    });
  });

  force.drag().on("dragstart", dragstart);


}

var findNodeBoundary = function (startNode, endNode, radius) {
  var startX, startY, endX, endY;
  startX = startNode.x;
  startY = startNode.y;
  endX = endNode.x;
  endY = endNode.y;
  var Dx, Dy, angle, dx, dy, px, py;
  Dx = endX - startX;
  Dy = endY - startY;
  angle = Math.atan2(Dy, Dx);
  dx = radius * Math.cos(angle);
  dy = radius * Math.sin(angle);
  px = endX - dx;
  py = endY - dy;
  return [px, py];
}

function dragstart(d) {
  d.fixed = true;
  d3.select(this).classed("fixed", true);
}
