@echo off
chcp 65001 >nul
cd /d "%~dp0"

echo ⚡ Building Project...
call mvn clean package -DskipTests -q
if %ERRORLEVEL% NEQ 0 ( echo ❌ Benchmark failed. & pause & exit /b %ERRORLEVEL% )

echo 🚀 Running Demo...
cd examples\Benchmark
java --sun-misc-unsafe-memory-access=allow -jar target\benchmarks.jar -jvmArgsAppend "--sun-misc-unsafe-memory-access=allow"
if %ERRORLEVEL% NEQ 0 ( echo ❌ Benchmark failed. & pause & exit /b %ERRORLEVEL% )

pause
