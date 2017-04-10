setlocal

:: Batch script that runs gradle from toolchain and puts the
:: build outputs under build root

:: fyi: we don't really want to know where /this/ script is located, but rather the concrete
:: script that is delegating to this one, which is within a specific language toolkit dir.

:: if there is trailing backslash, remove it
if %TCROOT:~-1%==\ set TCROOT=%TCROOT:~0,-1%

set JAVA_HOME=%TCROOT%\win64\jdk-1.7.0_79
@echo.JAVA_HOME: %JAVA_HOME%

set GRADLE_HOME=%TCROOT%\noarch\gradle-2.0
@echo.GRADLE_HOME: %GRADLE_HOME%

:: find vapi full path
for %%a in ("%TOOLKIT_DIR:~0,-1%") do set VAPI_DIR=%%~dpa
@echo.VAPI_DIR: %VAPI_DIR%

:: find toolkit dir name
for %%A in ("%TOOLKIT_DIR:~0,-1%") do set TOOLKIT_NAME=%%~nxA
@echo.TOOLKIT_NAME: %TOOLKIT_NAME%

set BUILDROOT=%VAPI_DIR%\build\gradle
@echo.BUILDROOT: %BUILDROOT%

set BUILDTHIS=%BUILDROOT%\%TOOLKIT_NAME%
@echo.BUILDTHIS: %BUILDTHIS%

if not defined BUILDFILE set BUILDFILE=%TOOLKIT_DIR%build.gradle
@echo.BUILDFILE: %BUILDFILE%

set OBJDIR=beta
if [%GOBUILD_LOCALCACHE_DIR%] == [] set GOBUILD_LOCALCACHE_DIR=%BUILDROOT%\package\COMPONENTS\
set GOBUILD_AUTO_COMPONENTS=True
set GOBUILD_AUTO_COMPONENTS_HOSTTYPE=generic
set HOSTTYPE=windows-vs2012


call %GRADLE_HOME%\bin\gradle.bat ^
    -Dbase.dir=%TOOLKIT_DIR% ^
    -Dbuild.root=%BUILDTHIS% ^
    -Dbuild.publish=%BUILDTHIS%\publish ^
    -Dvapi.core.version.properties="%VAPI_DIR%support\version\vapi-core-version.properties" ^
    --gradle-user-home %BUILDROOT%\gradle-home ^
    --project-cache-dir %BUILDROOT%\gradle-cache ^
    --build-file %BUILDFILE% ^
    %*
