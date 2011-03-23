@echo off

start emulator -avd klient
start emulator -avd tjener
sleep 10
set /p srcemu= Serial til server-emulator (tallet xxxx:tjener)
echo %srcemu%

rem adb -s emulator-%srcemu% forward tcp:11700 tcp:11700