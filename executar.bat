@echo off
REM Executa a aplicacao (compile antes com compilar.bat)
setlocal
cd /d "%~dp0"
if not exist bin (
    echo Pasta bin\ nao encontrada. Rode compilar.bat primeiro.
    exit /b 1
)
java -cp bin com.leandro.cadastro.Main
endlocal
