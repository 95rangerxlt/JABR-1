@echo off

call detect.cmd

rem Compilation
javac -d bin -classpath .\lib\hsqldb.jar -sourcepath src src\org\jabst\jabs\*.java
jar cfm jabs.jar MANIFEST.MF -C bin .
move jabs.jar deployment
copy lib\hsqldb.jar deployment\hsqldb.jar
pause