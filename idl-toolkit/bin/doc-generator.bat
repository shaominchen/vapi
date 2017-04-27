@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  doc-generator startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

@rem Add default JVM options here. You can also use JAVA_OPTS and DOC_GENERATOR_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=-Xms512m -Xmx1024m -Dfile.encoding=UTF-8 -noverify -XX:+UseSerialGC -XX:PermSize=128m -XX:MaxPermSize=512m

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto init

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto init

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:init
@rem Get command-line arguments, handling Windowz variants

if not "%OS%" == "Windows_NT" goto win9xME_args
if "%@eval[2+2]" == "4" goto 4NT_args

:win9xME_args
@rem Slurp the command line arguments.
set CMD_LINE_ARGS=
set _SKIP=2

:win9xME_args_slurp
if "x%~1" == "x" goto execute

set CMD_LINE_ARGS=%*
goto execute

:4NT_args
@rem Get arguments from the 4NT Shell from JP Software
set CMD_LINE_ARGS=%$

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\doc-generator-2.6.0.4750157.jar;%APP_HOME%\lib\groovy-all-2.1.9.jar;%APP_HOME%\lib\idl-transformer-2.6.0.4750157.jar;%APP_HOME%\lib\jansi-1.7.jar;%APP_HOME%\lib\ant-1.8.4.jar;%APP_HOME%\lib\commons-cli-1.2.jar;%APP_HOME%\lib\idl-core-2.6.0.4750157.jar;%APP_HOME%\lib\vmodl-parser-2.6.0.4750157.jar;%APP_HOME%\lib\commons-lang3-3.1.jar;%APP_HOME%\lib\ant-launcher-1.8.4.jar;%APP_HOME%\lib\jsoup-1.7.2.jar;%APP_HOME%\lib\jline-0.9.94.jar;%APP_HOME%\lib\junit-3.8.1.jar

@rem Execute doc-generator
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %DOC_GENERATOR_OPTS%  -classpath "%CLASSPATH%" com.vmware.vapi.idl.generator.doc.DocGeneratorApp %CMD_LINE_ARGS%

:end
@rem End local scope for the variables with windows NT shell
if "%ERRORLEVEL%"=="0" goto mainEnd

:fail
rem Set variable DOC_GENERATOR_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
if  not "" == "%DOC_GENERATOR_EXIT_CONSOLE%" exit 1
exit /b 1

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
