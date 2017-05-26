@echo off

call detect.cmd

rem Compilation
javac -g -d bin -classpath .\lib\hsqldb.jar -sourcepath src\main\java src\main\java\org\jabst\jabs\*.java
jar cfm jabs.jar MANIFEST.MF -C bin .
move jabs.jar deployment
copy lib\hsqldb.jar deployment\hsqldb.jar
