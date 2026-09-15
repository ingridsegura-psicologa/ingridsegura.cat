@echo off
cd /d "%~dp0"
java -jar "%~dp0generar-posts.jar" %*
echo.
pause
