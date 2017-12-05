@echo off
cls

echo    /$$   /$$                     /$$ /$$
echo   ^| $$$ ^| $$                    ^| $$^|__/
echo   ^| $$$$^| $$  /$$$$$$   /$$$$$$ ^| $$ /$$ /$$   /$$
echo   ^| $$ $$ $$ /$$__  $$ /$$__  $$^| $$^| $$^|  $$ /$$/
echo   ^| $$  $$$$^| $$$$$$$$^| $$$$$$$$^| $$^| $$ \  $$$$/
echo   ^| $$\  $$$^| $$_____/^| $$_____/^| $$^| $$  ^>$$  $$
echo   ^| $$ \  $$^|  $$$$$$$^|  $$$$$$$^| $$^| $$ /$$/\  $$
echo   ^|__/  \__/ \_______/ \_______/^|__/^|__/^|__/  \__/
echo         Updater 1.1 - 20.09.2016

:check
if not exist "neelix.marker" (
	echo.
	echo === FEHLER ===
	echo Ungueltiges Verzeichnis! Aus Sicherheitsgruenden sollte das Verzeichnis markiert sein.
	pause
	exit
) else (
	goto begin
)
exit

:begin
echo Neelix wird beendet...
timeout /nobreak 2
set NEELIX_HOME=%cd%
goto install

:install
set SCRIPT="%RANDOM%-%RANDOM%-%RANDOM%-%RANDOM%.bat"
(
	echo @echo off
	echo echo.
	echo echo Aktuelle Version wird heruntergeladen...
	echo xcopy /f \\daten.uni-bamberg.de\team.it-support\52_Neelix\Neelix-latest.zip %NEELIX_HOME%
	echo if %%errorlevel%% neq 0 goto abort
	echo echo Entpacken...
	echo "C:\Program Files\7-Zip\7z.exe" x -y  %NEELIX_HOME%\Neelix-latest.zip
	echo if %%errorlevel%% neq 0 goto noZip
	echo del /Q %NEELIX_HOME%\Neelix-latest.zip
	echo goto finished

	echo :noZip
	echo echo.
	echo echo === ACHTUNG! ===
	echo echo Kein Programm zum entpacken gefunden. Archiv ^(%NEELIX_HOME%\Neelix-latest.zip^) muss manuell entpackt werden.
	echo %SYSTEMROOT%\explorer.exe %NEELIX_HOME%
	echo pause
	echo goto exit

	echo :abort
	echo echo.
	echo echo Aborted.
	echo pause
	echo goto exit

	echo :finished
	echo echo.
	echo echo === Finished ===
	echo echo Neelix wurde aktualisiert.
	echo set /P c=Starten [j/N]?
	echo if /I "%%c%%" EQU "J" start %NEELIX_HOME%\Neelix.exe
	echo goto exit
	
	echo :exit
	echo start cmd /c "del %SCRIPT%"
	echo exit
) >> %SCRIPT%
start cmd /k %SCRIPT%
exit
