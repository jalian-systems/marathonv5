@ECHO OFF

SETLOCAL
SET DIST=%~dp0
java -jar "%DIST%SwingSet3.jar" %1 %2 %3 %4

ENDLOCAL
