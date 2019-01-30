@echo off
cls

echo.
echo    /$$   /$$                     /$$ /$$                              
echo   ^| $$$ ^| $$                    ^| $$^|__/               
echo   ^| $$$$^| $$  /$$$$$$   /$$$$$$ ^| $$ /$$ /$$   /$$     
echo   ^| $$ $$ $$ /$$__  $$ /$$__  $$^| $$^| $$^|  $$ /$$/    
echo   ^| $$  $$$$^| $$$$$$$$^| $$$$$$$$^| $$^| $$ \  $$$$/    
echo   ^| $$\  $$$^| $$_____/^| $$_____/^| $$^| $$  ^>$$  $$     
echo   ^| $$ \  $$^|  $$$$$$$^|  $$$$$$$^| $$^| $$ /$$/\  $$    
echo   ^|__/  \__/ \_______/ \_______/^|__/^|__/^|__/  \__/       
echo         Installer 1.2 - 21.09.2016

set NEELIX_HOME=C:%HOMEPATH%\Neelix

:begin
echo.
echo === Verzeichnis ===
echo Programm unter %NEELIX_HOME% installieren?
set /P a=Fortfahren [j/N]? 
if /I "%a%" EQU "J" goto install
goto abort

:install
echo.
echo === Installation ===
mkdir %NEELIX_HOME%
cd /d %NEELIX_HOME%
echo Aktuelle Version wird heruntergeladen...
xcopy /f \\daten.uni-bamberg.de\team.it-support\52_Neelix\Neelix-latest.zip
if %errorlevel% neq 0 goto abort
echo Entpacken...
"C:\Program Files\7-Zip\7z.exe" x -pNICHTEINFACHENTPACKEN -y %NEELIX_HOME%\Neelix-latest.zip
if %errorlevel% neq 0 goto noZip
echo Loesche temporaere Dateien...
del /Q %NEELIX_HOME%\Neelix-latest.zip
goto createShortcut

:createShortcut
echo Creating shortcut...
set SCRIPT="%TEMP%\%RANDOM%-%RANDOM%-%RANDOM%-%RANDOM%.vbs"
echo Set oWS = WScript.CreateObject("WScript.Shell") >> %SCRIPT%
echo %USERPROFILE%
echo sLinkFile = "\\daten.uni-bamberg.de\%USERNAME%\Work Folders\Desktop\Neelix.lnk" >> %SCRIPT%
echo Set oLink = oWS.CreateShortcut(sLinkFile) >> %SCRIPT%
echo oLink.TargetPath = "%NEELIX_HOME%\Neelix.exe" >> %SCRIPT%
echo oLink.Save >> %SCRIPT%
cscript /nologo %SCRIPT%
del %SCRIPT%
goto finished

:noZip
echo.
echo === ACHTUNG! ===
echo Kein Programm zum entpacken gefunden. Archiv (%NEELIX_HOME%\Neelix-latest.zip) muss manuell entpackt werden.
pause
%SystemRoot%/explorer.exe %NEELIX_HOME%
exit

:finished
echo.
echo === Finished ===
echo Neelix unter %NEELIX_HOME% installiert.
set /P c=Starten [j/N]? 
if /I "%c%" EQU "J" start %NEELIX_HOME%\Neelix.exe
exit

:abort
echo.
echo Aborted.
pause
exit