@echo off
echo Building the project...
mvn clean package

if errorlevel 1 (
    echo Build failed. Exiting.
    exit /b 1
)

echo Running the project...
REM Change CiteWinder.jar to the actual jar name if different.
java -jar target\CiteWinder.jar
pause