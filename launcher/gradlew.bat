@rem
@rem Gradle startup script for Windows.
@rem

@if "%DEBUG%" == "" @echo off
setlocal

set APP_HOME=%~dp0
set CLASSPATH=%APP_HOME%gradle\wrapper\gradle-wrapper-main.jar;%APP_HOME%gradle\wrapper\gradle-wrapper-shared.jar

if not exist "%APP_HOME%gradle\wrapper\gradle-wrapper-main.jar" (
  echo Missing %APP_HOME%gradle\wrapper\gradle-wrapper-main.jar
  exit /b 1
)
if not exist "%APP_HOME%gradle\wrapper\gradle-wrapper-shared.jar" (
  echo Missing %APP_HOME%gradle\wrapper\gradle-wrapper-shared.jar
  exit /b 1
)

if "%JAVA_HOME%" == "" (
  set JAVA_EXE=java
) else (
  set JAVA_EXE=%JAVA_HOME%\bin\java.exe
)

"%JAVA_EXE%" "-Xmx64m" "-Xms64m" %JAVA_OPTS% %GRADLE_OPTS% -classpath "%CLASSPATH%" org.gradle.wrapper.GradleWrapperMain %*

endlocal

