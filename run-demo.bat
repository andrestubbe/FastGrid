@echo off
chcp 65001 >nul
cd /d "%~dp0"

echo ⚡ Building Project...
call mvn clean package -DskipTests -q
if %ERRORLEVEL% NEQ 0 ( echo ❌ Benchmark failed. & pause & exit /b %ERRORLEVEL% )

echo 🚀 Launching: Visual Demo...
cd examples\Demo
call mvn -q compile exec:java -Dexec.mainClass="fastgrid.demo.Demo"
if %ERRORLEVEL% NEQ 0 ( echo ❌ Benchmark failed. & pause & exit /b %ERRORLEVEL% )

cd ..\..
