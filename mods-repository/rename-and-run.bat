@echo off
chcp 65001 >nul
set "SCRIPT_DIR=%~dp0"
set "SCRIPT_DIR=%SCRIPT_DIR:~0,-1%"

:: Get parent of mods-repository (v2) and then parent of v2 = folder to rename
for %%A in ("%SCRIPT_DIR%\..\..") do set "PARENT_PATH=%%~fA"
for %%A in ("%SCRIPT_DIR%\..\..") do set "DESKTOP_DIR=%%~dpA"

set "NEW_PROJECT=%DESKTOP_DIR%pasting\v2\mods-repository"

if exist "%NEW_PROJECT%\gradlew.bat" (
    echo Already using pasting folder. Starting...
    cd /d "%NEW_PROJECT%"
    call gradlew.bat runClient
    pause
    exit /b 0
)

echo Renaming parent folder to pasting...
powershell -NoProfile -ExecutionPolicy Bypass -Command "Rename-Item -LiteralPath '%PARENT_PATH%' -NewName 'pasting' -ErrorAction Stop"
if errorlevel 1 (
    echo Rename failed. Manually rename the folder "пастинг" to "pasting" on Desktop, then run run-client.bat
    pause
    exit /b 1
)

echo Starting Minecraft from new path...
cd /d "%NEW_PROJECT%"
if not exist gradlew.bat (
    echo ERROR: gradlew.bat not found
    pause
    exit /b 1
)
call gradlew.bat runClient
pause
