@echo off
chcp 65001 >nul
setlocal
cd /d "%~dp0"

echo ⚡ FastGrid JMH Benchmark (v0.1.0)
echo 🚀 Building Core Library...
call mvn -q clean install -DskipTests

echo 🚀 Building Benchmark Module...
cd examples\Benchmark
call mvn -q clean package

echo 🚀 Launching Benchmark...
java --sun-misc-unsafe-memory-access=allow -jar target\benchmarks.jar -jvmArgsAppend "--sun-misc-unsafe-memory-access=allow"

pause
