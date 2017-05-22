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
javac -d bin -classpath .\lib\hsqldb.jar -sourcepath src src\org\jabst\jabs\*.java
jar cvfm dbm.jar MANIFEST.DBM.MF -C bin .
move dbm.jar deployment
pause