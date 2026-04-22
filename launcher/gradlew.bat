@rem
@rem Gradle startup script for Windows.
@rem

@if "%DEBUG%" == "" @echo off
setlocal

set APP_HOME=%~dp0
set CLASSPATH=%APP_HOME%gradle\wrapper\gradle-wrapper.jar

if not exist "%CLASSPATH%" (
  echo Missing %CLASSPATH%.
  echo This repository includes wrapper scripts and properties, but not the wrapper JAR.
  echo On another machine with Gradle installed, run: gradle wrapper
  echo Or open this folder in Android Studio which will generate the wrapper files.
  exit /b 1
)

if "%JAVA_HOME%" == "" (
  set JAVA_EXE=java
) else (
  set JAVA_EXE=%JAVA_HOME%\bin\java.exe
)

"%JAVA_EXE%" "-Xmx64m" "-Xms64m" %JAVA_OPTS% %GRADLE_OPTS% -classpath "%CLASSPATH%" org.gradle.wrapper.GradleWrapperMain %*

endlocal

