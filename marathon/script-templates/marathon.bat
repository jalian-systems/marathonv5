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

for %%i in (%1 %2 %3 %4 %5 %6 %7 %8 %9) do if %%i==-batch goto :batch
for %%i in (%1 %2 %3 %4 %5 %6 %7 %8 %9) do if %%i==-b goto :batch
for %%i in (%1 %2 %3 %4 %5 %6 %7 %8 %9) do if %%i==-h goto :batch
for %%i in (%1 %2 %3 %4 %5 %6 %7 %8 %9) do if %%i==-help goto :batch
for %%i in (%1 %2 %3 %4 %5 %6 %7 %8 %9) do if %%i==-i goto :batch
for %%i in (%1 %2 %3 %4 %5 %6 %7 %8 %9) do if %%i==-ignore goto :batch
start javaw -jar "%MARATHONHOME%/$marathonJar" %1 %2 %3 %4 %5 %6 %7 %8 %9
goto end
:batch
java -jar "%MARATHONHOME%/$marathonJar" %1 %2 %3 %4 %5 %6 %7 %8 %9
goto :end

:nodist
echo Could not find Marathon install directory...
echo Please set MARATHON_HOME to the directory where Marathon is installed.

:end

ENDLOCAL
