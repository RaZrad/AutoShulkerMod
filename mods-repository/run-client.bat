@echo off
chcp 65001 >nul
set "DIR=%~dp0"
set "DIR=%DIR:~0,-1%"
cd /d "%DIR%"
echo Folder: %DIR%
if not exist "%DIR%\gradlew.bat" (
    echo ERROR: gradlew.bat not found in %DIR%
    pause
    exit /b 1
)
echo Starting Minecraft...
"%DIR%\gradlew.bat" runClient
pause
