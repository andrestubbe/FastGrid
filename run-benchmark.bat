@echo off
chcp 65001 >nul
setlocal
cd /d "%~dp0"

echo âš¡ FastGrid JMH Benchmark (v0.1.0)
echo ðŸš€ Building Core Library...

echo ðŸš€ Building Benchmark Module...
cd examples\Benchmark

echo ðŸš€ Launching Benchmark...
java --sun-misc-unsafe-memory-access=allow -jar target\benchmarks.jar -jvmArgsAppend "--sun-misc-unsafe-memory-access=allow"

pause
