#!/bin/sh
javac -g -d bin -classpath ./lib/hsqldb.jar -sourcepath src src/org/jabst/jabs/*.java
jar cvfm jabs.jar MANIFEST.MF -C bin .
mv jabs.jar deployment
cp lib/hsqldb.jar deployment/hsqldb.jar
