@echo off
REM Bookshop Discount Management System Windows Executable
REM This script runs the JavaFX application

REM Get the directory of this script
set SCRIPT_DIR=%~dp0

REM Path to the JAR file (assume it's in the target directory relative to script)
set JAR_FILE=%SCRIPT_DIR%target\BookshopDiscountSystem-1.0-SNAPSHOT.jar

REM Check if JAR file exists
if not exist "%JAR_FILE%" (
    echo Error: JAR file not found at %JAR_FILE%
    echo Please make sure the project is built and the JAR file exists.
    pause
    exit /b 1
)

REM Check if java is available
java -version >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo Error: Java is not installed or not in PATH.
    echo Please install Java 11 or higher.
    pause
    exit /b 1
)

REM Run the Java application
java -jar "%JAR_FILE%"

REM Check if java command succeeded
if %ERRORLEVEL% NEQ 0 (
    echo Failed to run the application. Please ensure JavaFX and Java 11+ are installed.
    pause
    exit /b 1
)

echo Application closed successfully.
pause
