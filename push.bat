@echo off
setlocal enabledelayedexpansion

REM === Git User Config ===
git config --global user.name "Andre Stubbe"
git config --global user.email "andrestubbe@proton.me"

REM === Commit Message ===
set MSG=Auto Update

REM === Basis-Pfad ===
set BASE=C:\Users\andre\Documents\2026-05-17-Work-FastJava

REM === Liste für ignorierte Repos ===
set IGNORED=

echo Starte FORCE Updates (Repos ohne Remote werden ignoriert)...
echo.

for /d %%R in ("%BASE%\Fast*") do (
    echo ================================
    echo Repository: %%R
    echo ================================
    cd "%%R"

    REM === Prüfen ob Remote existiert ===
    git remote get-url origin >nul 2>&1
    if errorlevel 1 (
        echo Kein Remote → Repo wird ignoriert.
        set IGNORED=!IGNORED!%%~nR;
        cd "%BASE%"
        echo.
        continue
    )

    REM === Commit immer ===
    git add -A
    git commit -m "%MSG%" >nul 2>&1

    REM === FORCE PUSH ===
    echo Force Push...
    git push origin HEAD --force

    cd "%BASE%"
    echo.
)

echo ============================================
echo =   ZUSAMMENFASSUNG: Ignorierte Repos       =
echo ============================================

if "%IGNORED%"=="" (
    echo Alle Repos hatten einen Remote.
) else (
    for %%I in (%IGNORED%) do (
        echo - %%I
    )
)

echo.
echo Fertig! Nur Repos mit Remote wurden gepusht.
