@REM
@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.  See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership.  The ASF licenses this file
@REM to you under the Apache License, Version 2.0 (the
@REM "License"); you may not use this file except in compliance
@REM with the License.  You may obtain a copy of the License at
@REM
@REM   http://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on an
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied.  See the License for the
@REM specific language governing permissions and limitations
@REM under the License.
@REM
@if "%DEBUG%" == "" @echo off

if "%OS%"=="Windows_NT" setlocal enableextensions
set ERRORLEVEL=0

:begin

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.\

:check_JAVACMD
if not "%JAVACMD%" == "" goto check_SHELL_HOME

:check_JAVA_HOME
if not "%JAVA_HOME%" == "" goto have_JAVA_HOME
set JAVACMD=java
goto check_SHELL_HOME

:have_JAVA_HOME
set JAVACMD=%JAVA_HOME%\bin\java
goto check_SHELL_HOME

:check_SHELL_HOME
if "%SHELL_HOME%" == "" set SHELL_HOME=%DIRNAME%..

:init
@REM Get command-line arguments, handling Windowz variants
if not "%OS%" == "Windows_NT" goto win9xME_args
if "%eval[2+2]" == "4" goto 4NT_args

@REM Regular WinNT shell
set ARGS=%*
goto execute

:win9xME_args
@REM Slurp the command line arguments.  This loop allows for an unlimited number
set ARGS=

:win9xME_args_slurp
if "x%1" == "x" goto execute
set ARGS=%ARGS% %1
shift
goto win9xME_args_slurp

:4NT_args
@REM Get arguments from the 4NT Shell from JP Software
set ARGS=%$

:execute

set BOOTJAR=%SHELL_HOME%\lib\boot\bootstrap.jar

@REM Start the JVM
"%JAVACMD%" %JAVA_OPTS% -jar "%BOOTJAR%" %ARGS%

:end

if "%OS%"=="Windows_NT" endlocal
if "%SHELL_BATCH_PAUSE%" == "on" pause

