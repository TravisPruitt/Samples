echo git-refresh running at `date`
REM if [ -e options/nobuild ]; then
REM	echo Builds turned off. Delete the "options/nobuild" file to enable builds
REM	exit
REM fi
REM load key
call C:\Git\disney.xBandController\build\tools\windows\scripts\build\pageant.bat
cd c:\git\disney.xBandController
git checkout .
git reset
git clean -f
git pull
cd c:\git\disney.xBandController\src\windows
nant clean
nant buildprod

