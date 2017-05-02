@echo off

rem JDK bin directory auto-detection
java -version > jversion 2>&1
for /f tokens^=2^delims^=^" %%a in (jversion) do (
    set jpath=%%a
)
del jversion
for /f %%a in ('echo %jpath%') do (
    set jpath=%%a
)
set jpath=%programfiles%\Java\jdk%jpath%\bin
set path=%path%;%jpath%

rem Compilation
javac -d bin/test -classpath .\lib\hsqldb.jar;.\lib\hamcrest-core-1.3.jar;.\lib\junit-4.12.jar -sourcepath test;src test\org\jabst\jabs\*.java
rem jar cvfm jabs.jar MANIFEST.MF -C bin .
rem move jabs.jar deployment