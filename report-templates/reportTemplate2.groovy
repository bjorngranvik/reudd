// List two types with indication of a relationship between two items
// NAME_OF_TYPE1, NAME_OF_TYPE1, NAME_OR_RELATIONSHIP
def type1Name = "NAME_OF_TYPE1"
def type2Name = "NAME_OF_TYPE2"
def relName = "NAME_OR_RELATIONSHIP"
def type1Nodes = typeNodeFactory.getTypeNode(type1Name).getAllDataNodes()
def type2Nodes = typeNodeFactory.getTypeNode(type2Name).getAllDataNodes()
def topRow = [""]
type2Nodes.each {
    topRow += it
}
def rows = [topRow]
for (thisNode in type1Nodes) {
    def row = [thisNode]
    for (otherNode in type2Nodes) {
        if (thisNode.hasDynamicRelationshipWith(otherNode,relName)) {
            row += "!"
        } else {
            row += ""
        }
    }
	rows.add(row)
}
rows