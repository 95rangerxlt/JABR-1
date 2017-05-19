@echo off
call detect.cmd
rem Compilation
javac -d bin/test -classpath .\lib\hsqldb.jar;.\lib\hamcrest-core-1.3.jar;.\lib\junit-4.12.jar -sourcepath test;src test\org\jabst\jabs\*.java
rem jar cvfm jabs.jar MANIFEST.MF -C bin .
rem move jabs.jar deployment