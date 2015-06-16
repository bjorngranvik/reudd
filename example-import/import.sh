#!/bin/bash

#todo Fix reference to user account
#todo Fix target reference

# Import example file
groovy -cp target/production/reudd:/Users/bjorngranvik/.m2/repository/org/neo4j/neo4j-kernel/1.9.5/neo4j-kernel-1.9.5.jar:/Users/bjorngranvik/.m2/repository/javax/transaction/jta/1.1/jta-1.1.jar \
src/groovy/org/reudd/Reudd import example-import/example-import-data_semicolon.csv

# Schema
groovy -cp /reudd/target/production/reudd:/Users/bjorngranvik/.m2/repository/org/neo4j/neo4j-kernel/1.9.5/neo4j-kernel-1.9.5.jar:/Users/bjorngranvik/.m2/repository/javax/transaction/jta/1.1/jta-1.1.jar \
/reudd/src/groovy/org/reudd/Reudd schema example-import-data-model pdf

groovy -cp /reudd/target/production/reudd:/Users/bjorngranvik/.m2/repository/org/neo4j/neo4j-kernel/1.9.5/neo4j-kernel-1.9.5.jar:/Users/bjorngranvik/.m2/repository/javax/transaction/jta/1.1/jta-1.1.jar \
/reudd/src/groovy/org/reudd/Reudd schema example-import-data-model svg
