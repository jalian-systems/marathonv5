@ECHO OFF

SETLOCAL

REM
REM Works on NT/2000/XP - Sets DIST to directory of the script
REM

SET DIST=%~dp0

IF NOT DEFINED DIST goto :nodist
SET MARATHONHOME=%DIST%
:nodist
IF NOT DEFINED MARATHONHOME goto :nodist

SET GEM_PATH=%MARATHONHOME%/support/ruby-selenium-webdriver-${version}.jar

java -Dfile.encoding=utf8 -jar "%MARATHONHOME%/support/$rubyJar" %*
goto :end

:nodist
echo Could not find Marathon install directory...
echo Please set MARATHON_HOME to the directory where Marathon is installed.

:end

ENDLOCAL
