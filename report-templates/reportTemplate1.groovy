// List a type with attributes
// NAME_OF_TYPE, CSV_ATTRIBUTES
def attributes = "CSV_ATTRIBUTES".split(",")
def typeName = "NAME_OF_TYPE"
def topRow = attributes
def rows = [topRow]
def dataNodes = typeNodeFactory.getTypeNode(typeName).getAllDataNodes()
for (item in dataNodes) {
    def row = []
    for (attribute in attributes) {
        row += item.attributes[attribute]
    }
	rows.add(row)
}
rows