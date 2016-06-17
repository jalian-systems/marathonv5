@rem ***************************************************************************
@rem Copyright 2016 Jalian Systems Pvt. Ltd.
@rem
@rem Licensed under the Apache License, Version 2.0 (the "License");
@rem you may not use this file except in compliance with the License.
@rem You may obtain a copy of the License at
@rem
@rem     http://www.apache.org/licenses/LICENSE-2.0
@rem
@rem Unless required by applicable law or agreed to in writing, software
@rem distributed under the License is distributed on an "AS IS" BASIS,
@rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@rem See the License for the specific language governing permissions and
@rem limitations under the License.
@rem ***************************************************************************

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
