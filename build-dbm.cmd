@echo off

call detect.cmd

rem Compilation
javac -d bin -classpath .\lib\hsqldb.jar -sourcepath src src\org\jabst\jabs\*.java
jar cvfm dbm.jar MANIFEST.DBM.MF -C bin .
move dbm.jar deployment