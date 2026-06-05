@echo off
chcp 65001 >nul
setlocal
cd /d "%~dp0"

echo ⚡ FastGrid Demo (v0.1.0)

echo.
echo 🚀 Launching: Visual Demo...

cd examples\Demo
call mvn -q compile exec:java -Dexec.mainClass="fastgrid.demo.Demo"
if %errorlevel% neq 0 (
    echo ❌ [ERROR] Demo failed to launch. 
    echo    Make sure you have all FastJava dependencies installed.
    pause
) else (
    echo ✅ Demo exited successfully.
)
cd ..\..
