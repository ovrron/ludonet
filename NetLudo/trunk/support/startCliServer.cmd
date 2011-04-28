@echo off

start emulator -avd klient
start emulator -avd tjener
sleep 10
set /p srcemu= Serial til server-emulator (tallet xxxx:tjener)
echo %srcemu%

echo Her er det du bør skrive for routing.  (Host ip er da 10.0.2.2)
echo (copy og kjør selv...)
rem echo adb -s emulator-5554 forward tcp:11700 tcp:11700
echo adb -s emulator-%srcemu% forward tcp:11700 tcp:11700