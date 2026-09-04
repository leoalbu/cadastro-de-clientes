@echo off
REM Compila todo o codigo-fonte de src\ para a pasta bin\
setlocal enabledelayedexpansion
cd /d "%~dp0"
if not exist bin mkdir bin

set "FONTES="
for /r "%~dp0src" %%f in (*.java) do set "FONTES=!FONTES! "%%f""

javac -encoding UTF-8 -d bin !FONTES!
if errorlevel 1 (
    echo.
    echo *** Falha na compilacao ***
    exit /b 1
)
echo Compilacao concluida em bin\
endlocal
