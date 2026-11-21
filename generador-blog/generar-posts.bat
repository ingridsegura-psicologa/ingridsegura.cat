@echo off
rem Executa directament GenerarPosts.java (sense .jar). Cal Java 11 o superior.
rem Us: generar-posts.bat          -> genera nomes els articles nous
rem     generar-posts.bat --force  -> regenera tots els articles
chcp 65001 >nul
cd /d "%~dp0"

where java >nul 2>nul
if errorlevel 1 (
  echo ERROR: no s'ha trobat Java. Instal.la Java 11 o superior.
  echo.
  pause
  exit /b 1
)

java -Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 "%~dp0GenerarPosts.java" %*
if errorlevel 1 (
  echo.
  echo Hi ha hagut un error. Comprova que tens Java 11 o superior: java -version
)
echo.
pause
